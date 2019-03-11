package virtualskin;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.serial.Serial;

public class Side {
	PApplet proc;
	Body mybody;
	int debug = 0;
	double[][] imu = new double[6][6];
//	double[][] handimu = new double[7][6];
//	double[][] armimu = new double[7][6];
	// double[][] armimuMAG = new double[3][9];
	// double [][] imu9 = new double[3][9];
	double[][] magno = new double[3][9];
	static int histlength = 120;
	static int nframes = 1;
	double[][][] nimu = new double[12][6][nframes];
	double[][][] nmag = new double[3][9][nframes];
	int thumbPressure = 0;
	int[] switches = { 0, 0, 0, 0 };
	int minpress = 1000;
	int maxpress = 0;
	int rangepress = 0;
	double normpress = 0;
	double[] presst = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	double totalPress = 0;
	int switchbinary = 0;

	int[] magadj = { 176, 176, 165 };
	int ttime = 0;
	String vals = " ";
	boolean firstContact = false;
	byte[] inBuffer = new byte[134];
	static int negcheck = 32767;
	int clockcount = 0;
	int off = 0;
	Serial port;
	double ascale = .000488;
	double gscale = .061068;

	double gyro[][] = new double[10][3];
	double angles[][] = new double[10][3];
	float[] roll = new float[11];// { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
	float[] pitch = new float[11];// { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
	float[] yaw = new float[11];// { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
	float sroll = 0;
	float spitch = 0;
	float syaw = 0;
	float troll = 0;
	float tpitch = 0;
	float tyaw = 0;
	float[][] magavg = new float[3][9];
	float[][] imuavg = new float[10][6];
	double dt = .001;
	double t = .002;
	double a = t / (t + dt);
	double a1 = 1 - a;
	float zoffset = 0f;
	
	int updated =0;

	Side(PApplet p, Body s, Serial sidePort, int d) {
		proc = p;
		mybody = s;
		port = sidePort;
		debug = d;
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 6; j++) {
				imu[i][j] = 0;
			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				magno[i][j] = 0;
			}
		}
	}

	public void serialEvent() {
		updated=0;
		if (firstContact == false) {
			vals = port.readStringUntil(10);
			PApplet.println(vals);
			if (vals != null) {
				port.clear(); // clear the serial port buffer
				firstContact = true; // you've had first contact from the microcontroller
				port.write("A"); // ask for more
			}
		} else {
			if (port.available() > 121) {
				inBuffer = port.readBytes(122);
				port.write("A");
				if (inBuffer != null) {
					for (int i = 0; i < 3; i++) {
						// accel
						magno[i][0] = ((inBuffer[0 + (i * 18)] << 8 | inBuffer[1 + (i * 18)] & 0xff));
						magno[i][1] = ((inBuffer[2 + (i * 18)] << 8 | inBuffer[3 + (i * 18)] & 0xff));
						magno[i][2] = ((inBuffer[4 + (i * 18)] << 8 | inBuffer[5 + (i * 18)] & 0xff));
						// gyro
						magno[i][3] = ((inBuffer[6 + (i * 18)] << 8 | inBuffer[7 + (i * 18)] & 0xff));
						magno[i][4] = ((inBuffer[8 + (i * 18)] << 8 | inBuffer[9 + (i * 18)] & 0xff));
						magno[i][5] = ((inBuffer[10 + (i * 18)] << 8 | inBuffer[11 + (i * 18)] & 0xff));
						// mag
						magno[i][6] = ((inBuffer[12 + (i * 18)] << 8 | inBuffer[13 + (i * 18)] & 0xff));
						magno[i][7] = ((inBuffer[14 + (i * 18)] << 8 | inBuffer[15 + (i * 18)] & 0xff));
						magno[i][8] = ((inBuffer[16 + (i * 18)] << 8 | inBuffer[17 + (i * 18)] & 0xff));
						for (int j = 0; j < 9; j++) {
							if (magno[i][j] > negcheck) {
								magno[i][j] = -1 * magno[i][j];
							}
							if (j < 3) {
								magno[i][j] = magno[i][j] * ascale;
							}
							if (j > 2 && j < 6) {
								magno[i][j] = magno[i][j] * gscale;
							}
							if (j > 5) {
								magno[i][j] = magno[i][j];
							}
						}
						for (int j = 0; j < 9; j++) {
							nmag[i][j][off] = magno[i][j];
						}
//						for (int i = 0; i < 6; i++) {
//							imu[9][i] = magno[i][j];
//						}
//						for (int i = 0; i < 6; i++) {
//							nimu[9][i][off] = imu[9][i];
//						}
					}
					for (int readnum = 0; readnum < 5; readnum++) {
						imu[readnum][0] = ((inBuffer[54 + (readnum * 12)] << 8)
								| (inBuffer[55 + (readnum * 12)] & 0xff));
						imu[readnum][1] = ((inBuffer[56 + (readnum * 12)] << 8)
								| (inBuffer[57 + (readnum * 12)] & 0xff));
						imu[readnum][2] = ((inBuffer[58 + (readnum * 12)] << 8)
								| (inBuffer[59 + (readnum * 12)] & 0xff));
						imu[readnum][3] = ((inBuffer[60 + (readnum * 12)] << 8)
								| (inBuffer[61 + (readnum * 12)] & 0xff));
						imu[readnum][4] = ((inBuffer[62 + (readnum * 12)] << 8)
								| (inBuffer[63 + (readnum * 12)] & 0xff));
						imu[readnum][5] = ((inBuffer[64 + (readnum * 12)] << 8)
								| (inBuffer[65 + (readnum * 12)] & 0xff));
						for (int i = 0; i < 3; i++) {
							if (imu[readnum][i] > negcheck) {
								imu[readnum][i] = (-(imu[readnum][i] - negcheck) * ascale);
							} else {
								imu[readnum][i] = imu[readnum][i] * ascale;
							}
						}
						for (int i = 3; i < 6; i++) {
							if (imu[readnum][i] > negcheck) {
								imu[readnum][i] = (-(imu[readnum][i] - negcheck) * gscale);
							} else {
								imu[readnum][i] = imu[readnum][i] * gscale;
							}
						}
						// APPEND TO HISTORIES
						for (int i = 0; i < 6; i++) {
							nimu[readnum][i][off] = imu[readnum][i];
						}
					}
				}
				thumbPressure = ((inBuffer[114] << 8) | (inBuffer[115] & 0xff));
				if (thumbPressure > maxpress) {
					maxpress = thumbPressure;
				}
				if (thumbPressure < minpress) {
					minpress = thumbPressure;
				}
				rangepress = maxpress - minpress;
				normpress = (1.0 * thumbPressure - minpress) / rangepress;
				switches[0] = 1 - inBuffer[116];
				switches[1] = 1 - inBuffer[117];
				switches[2] = 1 - inBuffer[118];
				switches[3] = 1 - inBuffer[119];
				switchbinary = (switches[3] << 3 | switches[2] << 2 | switches[1] << 1 | switches[0]);
				ttime = ((inBuffer[120] << 8) | (inBuffer[121] & 0xff));
				off = off + 1;
				if (off == nframes) {
					off = 0;
				}
			}
		}
//		if (debug == 1) {
//			for (int i = 0; i < 6; i++) {
//				imu[2][i] = imu[3][i];
//				for (int j = 0; j < nframes; j++) {
//					nimu[2][i][j] = nimu[3][i][j];
//				}
//			}
//		}
		calcrpy();
		updated=1;
	}
	float[] q = {1.0f, 0.0f, 0.0f, 0.0f};
	
	public void MadgwickQuaternionUpdate(float ax, float ay, float az, float gx, float gy, float gz, float mx, float my, float mz) {
		//https://github.com/kriswiner/MPU9250/blob/master/quaternionFilters.ino
		float deltat = mybody.deltat;
		float q1 = q[0], q2 = q[1], q3 = q[2], q4 = q[3];   // short name local variable for readability
        float norm;
        float hx, hy, _2bx, _2bz;
        float s1, s2, s3, s4;
        float qDot1, qDot2, qDot3, qDot4;

        // Auxiliary variables to avoid repeated arithmetic
        float _2q1mx;
        float _2q1my;
        float _2q1mz;
        float _2q2mx;
        float _4bx;
        float _4bz;
        float _2q1 = 2.0f * q1;
        float _2q2 = 2.0f * q2;
        float _2q3 = 2.0f * q3;
        float _2q4 = 2.0f * q4;
        float _2q1q3 = 2.0f * q1 * q3;
        float _2q3q4 = 2.0f * q3 * q4;
        float q1q1 = q1 * q1;
        float q1q2 = q1 * q2;
        float q1q3 = q1 * q3;
        float q1q4 = q1 * q4;
        float q2q2 = q2 * q2;
        float q2q3 = q2 * q3;
        float q2q4 = q2 * q4;
        float q3q3 = q3 * q3;
        float q3q4 = q3 * q4;
        float q4q4 = q4 * q4;
        float beta=0f;
        // Normalise accelerometer measurement
        norm = PApplet.sqrt(ax * ax + ay * ay + az * az);
        if (norm == 0.0f) return; // handle NaN
        norm = 1.0f/norm;
        ax *= norm;
        ay *= norm;
        az *= norm;

        // Normalise magnetometer measurement
        norm = PApplet.sqrt(mx * mx + my * my + mz * mz);
        if (norm == 0.0f) return; // handle NaN
        norm = 1.0f/norm;
        mx *= norm;
        my *= norm;
        mz *= norm;

        // Reference direction of Earth's magnetic field
        _2q1mx = 2.0f * q1 * mx;
        _2q1my = 2.0f * q1 * my;
        _2q1mz = 2.0f * q1 * mz;
        _2q2mx = 2.0f * q2 * mx;
        hx = mx * q1q1 - _2q1my * q4 + _2q1mz * q3 + mx * q2q2 + _2q2 * my * q3 + _2q2 * mz * q4 - mx * q3q3 - mx * q4q4;
        hy = _2q1mx * q4 + my * q1q1 - _2q1mz * q2 + _2q2mx * q3 - my * q2q2 + my * q3q3 + _2q3 * mz * q4 - my * q4q4;
        _2bx = PApplet.sqrt(hx * hx + hy * hy);
        _2bz = -_2q1mx * q3 + _2q1my * q2 + mz * q1q1 + _2q2mx * q4 - mz * q2q2 + _2q3 * my * q4 - mz * q3q3 + mz * q4q4;
        _4bx = 2.0f * _2bx;
        _4bz = 2.0f * _2bz;

        // Gradient decent algorithm corrective step
        s1 = -_2q3 * (2.0f * q2q4 - _2q1q3 - ax) + _2q2 * (2.0f * q1q2 + _2q3q4 - ay) - _2bz * q3 * (_2bx * (0.5f - q3q3 - q4q4) + _2bz * (q2q4 - q1q3) - mx) + (-_2bx * q4 + _2bz * q2) * (_2bx * (q2q3 - q1q4) + _2bz * (q1q2 + q3q4) - my) + _2bx * q3 * (_2bx * (q1q3 + q2q4) + _2bz * (0.5f - q2q2 - q3q3) - mz);
        s2 = _2q4 * (2.0f * q2q4 - _2q1q3 - ax) + _2q1 * (2.0f * q1q2 + _2q3q4 - ay) - 4.0f * q2 * (1.0f - 2.0f * q2q2 - 2.0f * q3q3 - az) + _2bz * q4 * (_2bx * (0.5f - q3q3 - q4q4) + _2bz * (q2q4 - q1q3) - mx) + (_2bx * q3 + _2bz * q1) * (_2bx * (q2q3 - q1q4) + _2bz * (q1q2 + q3q4) - my) + (_2bx * q4 - _4bz * q2) * (_2bx * (q1q3 + q2q4) + _2bz * (0.5f - q2q2 - q3q3) - mz);
        s3 = -_2q1 * (2.0f * q2q4 - _2q1q3 - ax) + _2q4 * (2.0f * q1q2 + _2q3q4 - ay) - 4.0f * q3 * (1.0f - 2.0f * q2q2 - 2.0f * q3q3 - az) + (-_4bx * q3 - _2bz * q1) * (_2bx * (0.5f - q3q3 - q4q4) + _2bz * (q2q4 - q1q3) - mx) + (_2bx * q2 + _2bz * q4) * (_2bx * (q2q3 - q1q4) + _2bz * (q1q2 + q3q4) - my) + (_2bx * q1 - _4bz * q3) * (_2bx * (q1q3 + q2q4) + _2bz * (0.5f - q2q2 - q3q3) - mz);
        s4 = _2q2 * (2.0f * q2q4 - _2q1q3 - ax) + _2q3 * (2.0f * q1q2 + _2q3q4 - ay) + (-_4bx * q4 + _2bz * q2) * (_2bx * (0.5f - q3q3 - q4q4) + _2bz * (q2q4 - q1q3) - mx) + (-_2bx * q1 + _2bz * q3) * (_2bx * (q2q3 - q1q4) + _2bz * (q1q2 + q3q4) - my) + _2bx * q2 * (_2bx * (q1q3 + q2q4) + _2bz * (0.5f - q2q2 - q3q3) - mz);
        norm = PApplet.sqrt(s1 * s1 + s2 * s2 + s3 * s3 + s4 * s4);    // normalise step magnitude
        norm = 1.0f/norm;
        s1 *= norm;
        s2 *= norm;
        s3 *= norm;
        s4 *= norm;

        // Compute rate of change of quaternion
        qDot1 = 0.5f * (-q2 * gx - q3 * gy - q4 * gz) - beta * s1;
        qDot2 = 0.5f * (q1 * gx + q3 * gz - q4 * gy) - beta * s2;
        qDot3 = 0.5f * (q1 * gy - q2 * gz + q4 * gx) - beta * s3;
        qDot4 = 0.5f * (q1 * gz + q2 * gy - q3 * gx) - beta * s4;

        // Integrate to yield quaternion
        q1 += qDot1 * deltat;
        q2 += qDot2 * deltat;
        q3 += qDot3 * deltat;
        q4 += qDot4 * deltat;
        norm = PApplet.sqrt(q1 * q1 + q2 * q2 + q3 * q3 + q4 * q4);    // normalise quaternion
        norm = 1.0f/norm;
        q[0] = q1 * norm;
        q[1] = q2 * norm;
        q[2] = q3 * norm;
        q[3] = q4 * norm;

	}
	
	public void calcrpy() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				magavg[i][j]=0;
				for (int k = 0; k < nframes; k++) {
					magavg[i][j] += nmag[i][j][k];
				}
				magavg[i][j] /= nframes;
//				if (i < 6) {
//					imuavg[9][i] = magavg[i];
//				}
			}
		}
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 6; j++) {
				imuavg[i][j]=0;
				for (int n = 0; n < nframes; n++) {
					imuavg[i][j] += nimu[i][j][n];
				}
				imuavg[i][j] /= nframes;
			}
		}
		
//		
//		for (int i = 0; i < 3; i++) {
//			roll[i] = PApplet.atan2(magavg[i][1], magavg[i][2]) * 180 / PConstants.PI;
//			pitch[i] = PApplet.atan2(-magavg[i][0], PApplet.sqrt((magavg[i][1] * magavg[i][1]) + (magavg[i][2] * magavg[i][2]))) * 180 / PConstants.PI;
//			yaw[i] = -PApplet.atan2(magavg[i][6], magavg[i][7]) * 180 / PConstants.PI + zoffset;
//		}

		for (int i = 0; i < 5; i++) {
			roll[i+3] = -PApplet.atan2(imuavg[i][0], imuavg[i][2]) * 180 / PConstants.PI;
			pitch[i+3] = PApplet.atan2(-imuavg[i][1], PApplet.sqrt((imuavg[i][0] * imuavg[i][0]) + (imuavg[i][2] * imuavg[i][2]))) * 180 / PConstants.PI;
			yaw[i+3] = PApplet.atan2(PApplet.sqrt((imuavg[i][1] * imuavg[i][1]) + (imuavg[i][0] * imuavg[i][0])), imuavg[i][2]);
		}

	}

}
