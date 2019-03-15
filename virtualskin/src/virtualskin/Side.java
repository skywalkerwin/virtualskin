package virtualskin;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.serial.Serial;

public class Side {
	PApplet proc;
	Body mybody;
	int debug = 0;

	// current sensor values
	float[][] imu = new float[6][6];
	float[][] magno = new float[3][9];

	// sensor history
	int hcount = 0;
	static int histlength = 120;
	static int nframes = 1;
	float[][][] mhist = new float[3][9][histlength];
	float[][][] ihist = new float[5][6][histlength];
	float[][][] nimu = new float[12][6][nframes];
	float[][][] nmag = new float[3][9][nframes];
	float[] magadj = { 176, 176, 165 };

	// touch values
	int ttime = 0;
	int switchbinary = 0;
	int thumbPressure = 0;
	int[] switches = { 0, 0, 0, 0 };
	int minpress = 1000;
	int maxpress = 0;
	int rangepress = 0;
	double normpress = 0;
	double[] presst = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	double totalPress = 0;

	// serial communication variables
	Serial port;
	String vals = " ";
	boolean firstContact = false;
	byte[] inBuffer = new byte[134];
	int off = 0;
	int clockcount = 0;
	int updated = 0;
	float ascale = .000488f;
	float gscale = .061068f;
	static int negcheck = 32767;
	static float PI = PConstants.PI;

	// sensor fusion stuff
	float deltat = 0f;
	float GyroMeasError = PI * (80.0f / 180.0f); // gyroscope measurement error in rads/s (start at 40 deg/s)
	float GyroMeasDrift = PI * (0.0f / 180.0f); // gyroscope measurement drift in rad/s/s (start at 0.0 deg/s/s)
	float beta = PApplet.sqrt(3.0f / 4.0f) * GyroMeasError; // compute beta
	float zeta = PApplet.sqrt(3.0f / 4.0f) * GyroMeasDrift;
	float[][] q = new float[8][4];

	// roll pitch yaw
	float[] roll = new float[11];// { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
	float[] pitch = new float[11];// { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
	float[] yaw = new float[11];// { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };

//	float[][] magavg = new float[3][9];
//	float[][] imuavg = new float[10][6];
//	double gyro[][] = new double[10][3];
//	double angles[][] = new double[10][3];
//	double dt = .001;
//	double t = .002;
//	double a = t / (t + dt);
//	double a1 = 1 - a;
//	float zoffset = 0f;

	Side(PApplet p, Body s, Serial sidePort, int d) {
		proc = p;
		mybody = s;
		port = sidePort;
		debug = d;
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 6; j++) {
				imu[i][j] = 0;
				for (int k = 0; k < histlength; k++) {
					ihist[i][j][k] = 0;
				}
			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				magno[i][j] = 0;
				for (int k = 0; k < histlength; k++) {
					mhist[i][j][k] = 0;
				}
			}
		}
		for (int i = 0; i < 8; i++) {
			q[i][0] = 1.0f;
			q[i][1] = 0f;
			q[i][2] = 0f;
			q[i][3] = 0f;
		}
	}

	public void serialEvent() {
		updated = 0;
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
					// 3x imu9
					for (int i = 0; i < 3; i++) {
						// ACCEL
						magno[i][0] = ((inBuffer[0 + (i * 18)] << 8 | inBuffer[1 + (i * 18)] & 0xff));
						magno[i][1] = ((inBuffer[2 + (i * 18)] << 8 | inBuffer[3 + (i * 18)] & 0xff));
						magno[i][2] = ((inBuffer[4 + (i * 18)] << 8 | inBuffer[5 + (i * 18)] & 0xff));
						// GYRO
						magno[i][3] = ((inBuffer[6 + (i * 18)] << 8 | inBuffer[7 + (i * 18)] & 0xff));
						magno[i][4] = ((inBuffer[8 + (i * 18)] << 8 | inBuffer[9 + (i * 18)] & 0xff));
						magno[i][5] = ((inBuffer[10 + (i * 18)] << 8 | inBuffer[11 + (i * 18)] & 0xff));
						// MAGNO
						magno[i][6] = ((inBuffer[12 + (i * 18)] << 8 | inBuffer[13 + (i * 18)] & 0xff));
						magno[i][7] = ((inBuffer[14 + (i * 18)] << 8 | inBuffer[15 + (i * 18)] & 0xff));
						magno[i][8] = ((inBuffer[16 + (i * 18)] << 8 | inBuffer[17 + (i * 18)] & 0xff));
						for (int j = 0; j < 9; j++) {
							if (magno[i][j] > negcheck) {
								magno[i][j] = -(magno[i][j]);// - negcheck);
							}
							if (j < 3) {
								magno[i][j] = magno[i][j] * ascale;
							} else if (j > 2 && j < 6) {
								magno[i][j] = magno[i][j] * gscale;
							} else {
								magno[i][j] = magno[i][j];
							}
//								magno[i][j]=(float)(magno[i][j]*(((magadj[(-6+j)]-128) *.5)/128)+1);
						}
						// APPEND TO HISTORIES
						for (int j = 0; j < 9; j++) {
							nmag[i][j][off] = magno[i][j];
							mhist[i][j][hcount] = magno[i][j];
						}
					}
					// 5x imu6
					for (int i = 0; i < 5; i++) {
						// ACCEL
						imu[i][0] = ((inBuffer[54 + (i * 12)] << 8) | (inBuffer[55 + (i * 12)] & 0xff));
						imu[i][1] = ((inBuffer[56 + (i * 12)] << 8) | (inBuffer[57 + (i * 12)] & 0xff));
						imu[i][2] = ((inBuffer[58 + (i * 12)] << 8) | (inBuffer[59 + (i * 12)] & 0xff));
						// GYRO
						imu[i][3] = ((inBuffer[60 + (i * 12)] << 8) | (inBuffer[61 + (i * 12)] & 0xff));
						imu[i][4] = ((inBuffer[62 + (i * 12)] << 8) | (inBuffer[63 + (i * 12)] & 0xff));
						imu[i][5] = ((inBuffer[64 + (i * 12)] << 8) | (inBuffer[65 + (i * 12)] & 0xff));

						for (int j = 0; j < 6; j++) {
							if (imu[i][j] > negcheck) {
								imu[i][j] = -(imu[i][j] - negcheck);
							}
							if (j < 3) {
								imu[i][j] = imu[i][j] * ascale;
							} else {
								imu[i][j] = imu[i][j] * gscale;
							}
						}
						// APPEND TO HISTORIES
						for (int j = 0; j < 6; j++) {
							nimu[i][j][off] = imu[i][j];
							ihist[i][j][hcount] = imu[i][j];
						}
					}
				}
				// touch stuff
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
			deltat = ttime / 1000000.0f;
		}
		sensorfusion();
		updated = 1;
	}


//	int c1 = proc.color(255,0,0);
//	int c2 = proc.color(0,255,0);
//	int c3 = proc.color(0,0,255);
//	int[] c = {c1, c2, c3};
	void plotmagno(int i) {
//		float yscale = (proc.height/2)/negcheck;
		float yscale=.000001f;
		float xscale = proc.width/(histlength+5);
		int c1 = proc.color(255,0,0);
		int c2 = proc.color(0,255,0);
		int c3 = proc.color(0,0,255);
		int[] c = {c1, c2, c3};
		proc.pushMatrix();
		proc.stroke(c[i]);
		proc.translate(50, 0, 0);
		proc.pushMatrix();
		proc.translate(0, proc.height / 2, 0);
		
		int hptr = hcount;
		for (int j = 6; j < 9; j++) {
			for (int k = hptr; k > 0; k--) {
				proc.line((hptr-k)*xscale, mhist[i][j][k]*yscale, (hptr-k+1)*xscale, mhist[i][j][k-1]);
			}
			proc.line((hptr)*xscale, mhist[i][j][0]*yscale, (hptr+1)*xscale, mhist[i][j][histlength-1]*yscale);
			for (int k = histlength-1; k > hptr+1; k--) {
				proc.line((hptr+(histlength-k))*xscale, mhist[i][j][k]*yscale, (hptr+(histlength-k)+1)*xscale, mhist[i][j][k-1]);
			}
		}

		proc.popMatrix();
		proc.popMatrix();
		hcount++;
		if (hcount == histlength) {
			hcount = 0;
		}
	}

	void Madgwick6(int i, float ax, float ay, float az, float gx, float gy, float gz) {
		float q1 = q[i][0], q2 = q[i][1], q3 = q[i][2], q4 = q[i][3]; // short name local variable for readability
		float norm; // vector norm
		float f1, f2, f3; // objetive funcyion elements
		float J_11or24, J_12or23, J_13or22, J_14or21, J_32, J_33; // objective function Jacobian elements
		float qDot1, qDot2, qDot3, qDot4;
		float hatDot1, hatDot2, hatDot3, hatDot4;
		float gerrx, gerry, gerrz, gbiasx, gbiasy, gbiasz; // gyro bias error

		// Auxiliary variables to avoid repeated arithmetic
		float _halfq1 = 0.5f * q1;
		float _halfq2 = 0.5f * q2;
		float _halfq3 = 0.5f * q3;
		float _halfq4 = 0.5f * q4;
		float _2q1 = 2.0f * q1;
		float _2q2 = 2.0f * q2;
		float _2q3 = 2.0f * q3;
		float _2q4 = 2.0f * q4;
		float _2q1q3 = 2.0f * q1 * q3;
		float _2q3q4 = 2.0f * q3 * q4;

		// Normalise accelerometer measurement
		norm = PApplet.sqrt(ax * ax + ay * ay + az * az);
		if (norm == 0.0f)
			return; // handle NaN
		norm = 1.0f / norm;
		ax *= norm;
		ay *= norm;
		az *= norm;

		// Compute the objective function and Jacobian
		f1 = _2q2 * q4 - _2q1 * q3 - ax;
		f2 = _2q1 * q2 + _2q3 * q4 - ay;
		f3 = 1.0f - _2q2 * q2 - _2q3 * q3 - az;
		J_11or24 = _2q3;
		J_12or23 = _2q4;
		J_13or22 = _2q1;
		J_14or21 = _2q2;
		J_32 = 2.0f * J_14or21;
		J_33 = 2.0f * J_11or24;

		// Compute the gradient (matrix multiplication)
		hatDot1 = J_14or21 * f2 - J_11or24 * f1;
		hatDot2 = J_12or23 * f1 + J_13or22 * f2 - J_32 * f3;
		hatDot3 = J_12or23 * f2 - J_33 * f3 - J_13or22 * f1;
		hatDot4 = J_14or21 * f1 + J_11or24 * f2;

		// Normalize the gradient
		norm = PApplet.sqrt(hatDot1 * hatDot1 + hatDot2 * hatDot2 + hatDot3 * hatDot3 + hatDot4 * hatDot4);
		hatDot1 /= norm;
		hatDot2 /= norm;
		hatDot3 /= norm;
		hatDot4 /= norm;

		// Compute estimated gyroscope biases
		gerrx = _2q1 * hatDot2 - _2q2 * hatDot1 - _2q3 * hatDot4 + _2q4 * hatDot3;
		gerry = _2q1 * hatDot3 + _2q2 * hatDot4 - _2q3 * hatDot1 - _2q4 * hatDot2;
		gerrz = _2q1 * hatDot4 - _2q2 * hatDot3 + _2q3 * hatDot2 - _2q4 * hatDot1;

		// Compute and remove gyroscope biases
//        gbiasx += gerrx * deltat * zeta;
//        gbiasy += gerry * deltat * zeta;
//        gbiasz += gerrz * deltat * zeta;
//        gx -= gbiasx;
//        gy -= gbiasy;
//        gz -= gbiasz;

		// Compute the quaternion derivative
		qDot1 = -_halfq2 * gx - _halfq3 * gy - _halfq4 * gz;
		qDot2 = _halfq1 * gx + _halfq3 * gz - _halfq4 * gy;
		qDot3 = _halfq1 * gy - _halfq2 * gz + _halfq4 * gx;
		qDot4 = _halfq1 * gz + _halfq2 * gy - _halfq3 * gx;

		// Compute then integrate estimated quaternion derivative
		q1 += (qDot1 - (beta * hatDot1)) * deltat;
		q2 += (qDot2 - (beta * hatDot2)) * deltat;
		q3 += (qDot3 - (beta * hatDot3)) * deltat;
		q4 += (qDot4 - (beta * hatDot4)) * deltat;

		// Normalize the quaternion
		norm = PApplet.sqrt(q1 * q1 + q2 * q2 + q3 * q3 + q4 * q4); // normalise quaternion
		norm = 1.0f / norm;
		q[i][0] = q1 * norm;
		q[i][1] = q2 * norm;
		q[i][2] = q3 * norm;
		q[i][3] = q4 * norm;
	}

	public void Madgwick9(int i, float ax, float ay, float az, float gx, float gy, float gz, float mx, float my,
			float mz) {
		// https://github.com/kriswiner/MPU9250/blob/master/quaternionFilters.ino
		float q1 = q[i][0], q2 = q[i][1], q3 = q[i][2], q4 = q[i][3]; // short name local variable for readability
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
//		float beta = 0f;
		// Normalise accelerometer measurement
		norm = PApplet.sqrt(ax * ax + ay * ay + az * az);
		if (norm == 0.0f)
			return; // handle NaN
		norm = 1.0f / norm;
		ax *= norm;
		ay *= norm;
		az *= norm;

		// Normalise magnetometer measurement
		norm = PApplet.sqrt(mx * mx + my * my + mz * mz);
		if (norm == 0.0f)
			return; // handle NaN
		norm = 1.0f / norm;
		mx *= norm;
		my *= norm;
		mz *= norm;

		// Reference direction of Earth's magnetic field
		_2q1mx = 2.0f * q1 * mx;
		_2q1my = 2.0f * q1 * my;
		_2q1mz = 2.0f * q1 * mz;
		_2q2mx = 2.0f * q2 * mx;
		hx = mx * q1q1 - _2q1my * q4 + _2q1mz * q3 + mx * q2q2 + _2q2 * my * q3 + _2q2 * mz * q4 - mx * q3q3
				- mx * q4q4;
		hy = _2q1mx * q4 + my * q1q1 - _2q1mz * q2 + _2q2mx * q3 - my * q2q2 + my * q3q3 + _2q3 * mz * q4 - my * q4q4;
		_2bx = PApplet.sqrt(hx * hx + hy * hy);
		_2bz = -_2q1mx * q3 + _2q1my * q2 + mz * q1q1 + _2q2mx * q4 - mz * q2q2 + _2q3 * my * q4 - mz * q3q3
				+ mz * q4q4;
		_4bx = 2.0f * _2bx;
		_4bz = 2.0f * _2bz;

		// Gradient decent algorithm corrective step
		s1 = -_2q3 * (2.0f * q2q4 - _2q1q3 - ax) + _2q2 * (2.0f * q1q2 + _2q3q4 - ay)
				- _2bz * q3 * (_2bx * (0.5f - q3q3 - q4q4) + _2bz * (q2q4 - q1q3) - mx)
				+ (-_2bx * q4 + _2bz * q2) * (_2bx * (q2q3 - q1q4) + _2bz * (q1q2 + q3q4) - my)
				+ _2bx * q3 * (_2bx * (q1q3 + q2q4) + _2bz * (0.5f - q2q2 - q3q3) - mz);
		s2 = _2q4 * (2.0f * q2q4 - _2q1q3 - ax) + _2q1 * (2.0f * q1q2 + _2q3q4 - ay)
				- 4.0f * q2 * (1.0f - 2.0f * q2q2 - 2.0f * q3q3 - az)
				+ _2bz * q4 * (_2bx * (0.5f - q3q3 - q4q4) + _2bz * (q2q4 - q1q3) - mx)
				+ (_2bx * q3 + _2bz * q1) * (_2bx * (q2q3 - q1q4) + _2bz * (q1q2 + q3q4) - my)
				+ (_2bx * q4 - _4bz * q2) * (_2bx * (q1q3 + q2q4) + _2bz * (0.5f - q2q2 - q3q3) - mz);
		s3 = -_2q1 * (2.0f * q2q4 - _2q1q3 - ax) + _2q4 * (2.0f * q1q2 + _2q3q4 - ay)
				- 4.0f * q3 * (1.0f - 2.0f * q2q2 - 2.0f * q3q3 - az)
				+ (-_4bx * q3 - _2bz * q1) * (_2bx * (0.5f - q3q3 - q4q4) + _2bz * (q2q4 - q1q3) - mx)
				+ (_2bx * q2 + _2bz * q4) * (_2bx * (q2q3 - q1q4) + _2bz * (q1q2 + q3q4) - my)
				+ (_2bx * q1 - _4bz * q3) * (_2bx * (q1q3 + q2q4) + _2bz * (0.5f - q2q2 - q3q3) - mz);
		s4 = _2q2 * (2.0f * q2q4 - _2q1q3 - ax) + _2q3 * (2.0f * q1q2 + _2q3q4 - ay)
				+ (-_4bx * q4 + _2bz * q2) * (_2bx * (0.5f - q3q3 - q4q4) + _2bz * (q2q4 - q1q3) - mx)
				+ (-_2bx * q1 + _2bz * q3) * (_2bx * (q2q3 - q1q4) + _2bz * (q1q2 + q3q4) - my)
				+ _2bx * q2 * (_2bx * (q1q3 + q2q4) + _2bz * (0.5f - q2q2 - q3q3) - mz);
		norm = PApplet.sqrt(s1 * s1 + s2 * s2 + s3 * s3 + s4 * s4); // normalise step magnitude
		norm = 1.0f / norm;
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
		norm = PApplet.sqrt(q1 * q1 + q2 * q2 + q3 * q3 + q4 * q4); // normalise quaternion
		norm = 1.0f / norm;
		q[i][0] = q1 * norm;
		q[i][1] = q2 * norm;
		q[i][2] = q3 * norm;
		q[i][3] = q4 * norm;
//		PApplet.println(q[i][0], q[i][1], q[i][2], q[i][3] );
	}

	public void sensorfusion() {
		for (int i = 0; i < 3; i++) {
			Madgwick9(i, magno[i][0], magno[i][1], magno[i][2], magno[i][3] * PI / 180.f, magno[i][4] * PI / 180.f,
					magno[i][5] * PI / 180.f, magno[i][7], magno[i][6], magno[i][8]);
			yaw[i] = PApplet.atan2(2.0f * (q[i][1] * q[i][2] + q[i][0] * q[i][3]),
					q[i][0] * q[i][0] + q[i][1] * q[i][1] - q[i][2] * q[i][2] - q[i][3] * q[i][3]);
			pitch[i] = -PApplet.asin(2.0f * (q[i][1] * q[i][3] - q[i][0] * q[i][2]));
			roll[i] = PApplet.atan2(2.0f * (q[i][0] * q[i][1] + q[i][2] * q[i][3]),
					q[i][0] * q[i][0] - q[i][1] * q[i][1] - q[i][2] * q[i][2] + q[i][3] * q[i][3]);
			pitch[i] *= 180.0f / PI;
			yaw[i] *= 180.0f / PI;
			yaw[i] -= 8; // Declination at Danville, California is 13 degrees 48 minutes and 47 seconds
			roll[i] *= 180.0f / PI;
		}

		for (int i = 3; i < 8; i++) {
			Madgwick6(i, imu[i - 3][0], imu[i - 3][1], imu[i - 3][2], imu[i - 3][3] * PI / 180.f,
					imu[i - 3][4] * PI / 180.f, imu[i - 3][5] * PI / 180.f);
			yaw[i] = PApplet.atan2(2.0f * (q[i][1] * q[i][2] + q[i][0] * q[i][3]),
					q[i][0] * q[i][0] + q[i][1] * q[i][1] - q[i][2] * q[i][2] - q[i][3] * q[i][3]);
			pitch[i] = -PApplet.asin(2.0f * (q[i][1] * q[i][3] - q[i][0] * q[i][2]));
			roll[i] = PApplet.atan2(2.0f * (q[i][0] * q[i][1] + q[i][2] * q[i][3]),
					q[i][0] * q[i][0] - q[i][1] * q[i][1] - q[i][2] * q[i][2] + q[i][3] * q[i][3]);
			pitch[i] *= 180.0f / PI;
			yaw[i] *= 180.0f / PI;
//			yaw[i] -= 30; // Declination at Danville, California is 13 degrees 48 minutes and 47 seconds
			roll[i] *= 180.0f / PI;
//			proc.println(yaw[i], pitch[i], roll[i]);
		}
	}
}

//OLD CALCULATION FUNCTIONS
//for (int i = 0; i < 3; i++) {
//for (int j = 0; j < 9; j++) {
//	magavg[i][j] = 0;
//	for (int k = 0; k < nframes; k++) {
//		magavg[i][j] += nmag[i][j][k];
//	}
//	magavg[i][j] /= nframes;
////	if (i < 6) {
////		imuavg[9][i] = magavg[i];
////	}
//}
//}
//for (int i = 0; i < 5; i++) {
//for (int j = 0; j < 6; j++) {
//	imuavg[i][j] = 0;
//	for (int n = 0; n < nframes; n++) {
//		imuavg[i][j] += nimu[i][j][n];
//	}
//	imuavg[i][j] /= nframes;
//}
//}
//for (int i = 0; i < 3; i++) {
//roll[i] = PApplet.atan2(magavg[i][1], magavg[i][2]) * 180 / PI;
//pitch[i] = PApplet.atan2(-magavg[i][0], PApplet.sqrt((magavg[i][1] * magavg[i][1]) + (magavg[i][2] * magavg[i][2]))) * 180 / PI;
//yaw[i] = -PApplet.atan2(magavg[i][6], magavg[i][7]) * 180 / PI + zoffset;
//}
//
//for (int i = 0; i < 5; i++) {
//roll[i + 3] = -PApplet.atan2(imuavg[i][0], imuavg[i][2]) * 180 / PI;
//pitch[i + 3] = PApplet.atan2(-imuavg[i][1],
//		PApplet.sqrt((imuavg[i][0] * imuavg[i][0]) + (imuavg[i][2] * imuavg[i][2]))) * 180 / PI;
//yaw[i + 3] = PApplet.atan2(PApplet.sqrt((imuavg[i][1] * imuavg[i][1]) + (imuavg[i][0] * imuavg[i][0])),
//		imuavg[i][2]);
//}
