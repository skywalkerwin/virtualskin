package virtualskin;

import processing.core.PApplet;
import processing.serial.*;

public class processing extends PApplet {

	// String leftcom="COM4";
	// String rightcom="COM5";
	String leftcom = "COM11";
	String rightcom = "COM10";

	Serial rightPort;
	Serial leftPort;
	int vcheck = 0;
	int pcheck = 0;
	static double ascale0 = .000061;
	static double ascale1 = .000122;
	static double ascale2 = .000244;
	static double ascale3 = .000488;
	static double[] ascales = { .000061, .000122, .000244, .000488 };
	static double gscale0 = 0.007633;
	static double gscale1 = 0.015267;
	static double gscale2 = 0.030534;
	static double gscale3 = 0.061068;
	static double[] gscales = { 0.007633, 0.015267, 0.030534, 0.061068 };
	static double ascale = ascale3;
	static double gscale = gscale3;
	static int[] atrans = { 2, 4, 8, 16 };
	static int[] gtrans = { 250, 500, 1000, 2000 };
	static int atran = atrans[3];
	static int gtran = gtrans[3];
	static int[] imuscales = { atran, gtran };

	double[][][] imu = new double[2][10][6];
	double[][][] handimu = new double[2][7][6];
	double[][][] armimu = new double[2][7][6];
	double[][] magno = new double[2][9];

	int histlength = 120;
	int nframes = 3;
//	double[][][] mhistp = new double[2][9][histlength];
//	double[][][][] imuhistp = new double[2][12][6][histlength];
//	double[][][] mhistv = new double[2][9][histlength];
//	double[][][][] imuhistv = new double[2][12][6][histlength];
//	double[][][] mhista = new double[2][9][histlength];
//	double[][][][] imuhista = new double[2][12][6][histlength];
//	double[][][] mhistj = new double[2][9][histlength];
//	double[][][][] imuhistj = new double[2][12][6][histlength];
	double[][][][] nimu = new double[2][12][6][nframes];
	double[][][] nmag = new double[2][9][nframes];
	double[][] avgtotalimu = new double[2][12];
	int[] hcount = { 0, 0 };

	int[] thumbPressure = { 0, 0 };
	int[][] switches = { { 0, 0, 0, 0 }, { 0, 0, 0, 0 } };
	int[] minpress = { 1000, 1000 };
	int[] maxpress = { 0, 0 };
	int[] rangepress = { 0, 0 };
	double[] normpress = { 0, 0 };
	double[][] presst = { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
	double totalPress = 0;
	int[] switchbinary = { 0, 0 };
	double rad = 100;
	float distances[][][] = new float [2][10][3];

	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", "virtualskin.processing" });
	}

	public void settings() {
//		size(100, 100, P3D);
		fullScreen(P3D);
	}

	public void setup() {
		frameRate(60);
		background(0);
		ellipseMode(RADIUS);
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 6; j++) {
				imu[0][i][j]=0;
				imu[1][i][j]=0;
			}
		}
		for (int i = 0; i < 9; i++) {
			magno[0][i] = 0;
			magno[1][i] = 0;
		}
		
		for(int i=0;i<10;i++) {
			for(int j =0;j<3;j++) {
				distances[0][i][j]=0;
				distances[1][i][j]=0;
			}
		}
//		for (int i = 0; i < 6; i++) {
//			for (int j = 0; j < 6; j++) {
//				for (int k = 0; k < histlength; k++) {
//					imuhistp[0][i][j][k] = 0;
//					imuhistv[0][i][j][k] = 0;
//					imuhista[0][i][j][k] = 0;
//					imuhistj[0][i][j][k] = 0;
//					imuhistp[1][i][j][k] = 0;
//					imuhistv[1][i][j][k] = 0;
//					imuhista[1][i][j][k] = 0;
//					imuhistj[1][i][j][k] = 0;
//				}
//			}
//		}

		rightPort = new Serial(this, rightcom, 115200);
		rightPort.clear();
		rightPort.buffer(134);
		leftPort = new Serial(this, leftcom, 115200);
		leftPort.clear();
		leftPort.buffer(134);
		delay(1000);
		sphereDetail(10);
	}

	int order = 0;
	int mt1, mt2, mt3, mt4, mt5 = 0;
	long nt1, nt2, nt3, nt4, nt5 = 0;
	long avgnano = 0;
	int counter = 1;
	float rotx = 0;
	float dscale = 40;
	int flicker=5;
	public void draw() {
		background(0);

		thread("serialr");
		thread("seriall");
		
		thread("rightCalc");
		thread("leftCalc");
		strokeWeight(3);
		stroke(255);
		fill(255);
		textSize(40);
		text(frameRate, 30, 30);
		fill(255, 0, 0);
		
		for (int i = 0; i < 10; i++) {
			pushMatrix();
			translate(width / 4, (1 + i) * height / 11, 0);
			rotateX(PI/2);
			rotateX(pitch[1][i] * PI / 180);
			rotateY(roll[1][i] * PI / 180);
			rotateZ(yaw[1][i] * PI / 180);
			translate((float)imu[1][i][0]*dscale,(float)imu[1][i][1]*dscale,(float)imu[1][i][2]*dscale);
			strokeWeight(1);
			stroke(255,0,0);
			line(-20000,0,0,20000,0,0);
			stroke(0,255,0);
			line(0,-20000,0,0,20000,0);
			stroke(0,0,255);
			line(0,0,-20000,0,0,20000);
			stroke(0);
			fill(255);
			strokeWeight(5);
			box(50);
			popMatrix();
		}
		
		for (int i = 0; i < 10; i++) {
			pushMatrix();
			translate((3*width) / 4, (1 + i) * height / 11, 0);
			rotateX(PI/2);
			rotateX(pitch[0][i] * PI / 180);
			rotateY(roll[0][i] * PI / 180);
			rotateZ(yaw[0][i] * PI / 180);
			translate((float)imu[0][i][0]*dscale,(float)imu[0][i][1]*dscale,(float)imu[0][i][2]*dscale);
			strokeWeight(1);
			stroke(255,0,0);
			line(-20000,0,0,20000,0,0);
			stroke(0,255,0);
			line(0,-20000,0,0,20000,0);
			stroke(0,0,255);
			line(0,0,-20000,0,0,20000);
			stroke(0);
			fill(255);
			strokeWeight(5);
			box(50);
			popMatrix();
		}
		
		hcount[0]++;
		hcount[1]++;
		if (hcount[0] == histlength) {
			hcount[0] = 0;
		}
		if (hcount[1] == histlength) {
			hcount[1] = 0;
		}
	}

	int[] magadj = { 176, 176, 165 };
	int[] ttime = { 0, 0 };
	String[] vals = { " ", " " };
	boolean[] firstContact = { false, false };
	byte[][] inBuffer = new byte[2][134];
	int negcheck = 32767;
	int[] clockcount = { 0, 0 };
	int[] off = { 0, 0 };

	public void seriall() {
		serialEvent(leftPort, 1);
	}

	public void serialr() {
		serialEvent(rightPort, 0);
	}

	public void serialEvent(Serial port, int k) {
		if (firstContact[k] == false) {
			vals[k] = port.readStringUntil(10);
			println(vals[k]);
			if (vals[k] != null) {
				port.clear(); // clear the serial port buffer
				firstContact[k] = true; // you've had first contact from the microcontroller
				port.write("A"); // ask for more
			}
		} else {
			if (port.available() > 133) {
				inBuffer[k] = port.readBytes(134);
				port.write("A");
				if (inBuffer[k] != null) {
					// accel
					magno[k][0] = ((inBuffer[k][0] << 8 | inBuffer[k][1] & 0xff));
					magno[k][1] = ((inBuffer[k][2] << 8 | inBuffer[k][3] & 0xff));
					magno[k][2] = ((inBuffer[k][4] << 8 | inBuffer[k][5] & 0xff));
					// gyro
					magno[k][3] = ((inBuffer[k][6] << 8 | inBuffer[k][7] & 0xff));
					magno[k][4] = ((inBuffer[k][8] << 8 | inBuffer[k][9] & 0xff));
					magno[k][5] = ((inBuffer[k][10] << 8 | inBuffer[k][11] & 0xff));
					// mag
					magno[k][6] = ((inBuffer[k][12] << 8 | inBuffer[k][13] & 0xff));
					magno[k][7] = ((inBuffer[k][14] << 8 | inBuffer[k][15] & 0xff));
					magno[k][8] = ((inBuffer[k][16] << 8 | inBuffer[k][17] & 0xff));
					for (int i = 0; i < 9; i++) {
						if (magno[k][i] > negcheck) {
							magno[k][i] = -1 * magno[k][i];
						}
						if (i < 3) {
							magno[k][i] = magno[k][i] * ascale;
						}
						if (i > 2 && i < 6) {
							magno[k][i] = magno[k][i] * gscale;
						}
						if (i > 5) {
							// magno[k][i]=(magno[k][i]*(((magadj[(-6+i)]-128) *.5)/128)+1);
							magno[k][i] = magno[k][i];
						}
					}
					for (int i = 0; i < 9; i++) {
//						mhista[k][i][hcount[k]] = magno[k][i];
						nmag[k][i][off[k]] = magno[k][i];
					}
					for (int i = 0; i < 6; i++) {
						imu[k][0][i] = magno[k][i];
					}
					for (int i = 0; i < 6; i++) {
//						imuhista[k][5][i][hcount[k]] = handimu[k][5][i];
//						imuhista[k][6][i][hcount[k]] += handimu[k][5][i];
//						handimu[k][6][i] += handimu[k][5][i];
						nimu[k][0][i][off[k]] = handimu[k][5][i];
//						nimu[k][6][i][off[k]] += handimu[k][5][i];
					}
					for (int readnum = 1; readnum < 10; readnum++) {
						imu[k][readnum][0] = ((inBuffer[k][6 + (readnum * 12)] << 8) | (inBuffer[k][7 + (readnum * 12)] & 0xff));
						imu[k][readnum][1] = ((inBuffer[k][8 + (readnum * 12)] << 8) | (inBuffer[k][9 + (readnum * 12)] & 0xff));
						imu[k][readnum][2] = ((inBuffer[k][10 + (readnum * 12)] << 8)| (inBuffer[k][11 + (readnum * 12)] & 0xff));
						imu[k][readnum][3] = ((inBuffer[k][12 + (readnum * 12)] << 8)| (inBuffer[k][13 + (readnum * 12)] & 0xff));
						imu[k][readnum][4] = ((inBuffer[k][14 + (readnum * 12)] << 8)| (inBuffer[k][15 + (readnum * 12)] & 0xff));
						imu[k][readnum][5] = ((inBuffer[k][16 + (readnum * 12)] << 8)| (inBuffer[k][17 + (readnum * 12)] & 0xff));
						for (int i = 0; i < 3; i++) {
							if (imu[k][readnum][i] > negcheck) {
								imu[k][readnum][i] = (-(imu[k][readnum][i] - negcheck) * ascale);
							} else {
								imu[k][readnum][i] = imu[k][readnum][i] * ascale;
							}
						}
						for (int i = 3; i < 6; i++) {
							if (imu[k][readnum][i] > negcheck) {
								imu[k][readnum][i] = (-(imu[k][readnum][i] - negcheck) * gscale);
							} else {
								imu[k][readnum][i] = imu[k][readnum][i] * gscale;
							}
						}
						// APPEND TO HISTORIES
						for (int i = 0; i < 6; i++) {
							// imuhista[k][readnum][i][hcount[k]]=imu[k][readnum][i];
							// imuhista[k][6][i][hcount[k]]+=imu[k][readnum][i];
//							imu[k][6][i] += imu[k][readnum][i];
							nimu[k][readnum][i][off[k]] = imu[k][readnum][i];
//							nimu[k][6][i][off[k]] += imu[k][readnum][i];
						}
					}
					// for (int i=0; i<6; i++) {
					// imuhista[k][6][i][hcount[k]]=imuhista[k][6][i][hcount[k]]/6;
					// handimu[k][6][i]/=6;
					// nimu[k][6][i][off[k]]/=6;
					// }
				}
				thumbPressure[k] = ((inBuffer[k][126] << 8) | (inBuffer[k][127] & 0xff));
				if (thumbPressure[k] > maxpress[k]) {
					maxpress[k] = thumbPressure[k];
				}
				if (thumbPressure[k] < minpress[k]) {
					minpress[k] = thumbPressure[k];
				}
				rangepress[k] = maxpress[k] - minpress[k];
				normpress[k] = (1.0 * thumbPressure[k] - minpress[k]) / rangepress[k];
				switches[k][0] = 1 - inBuffer[k][128];
				switches[k][1] = 1 - inBuffer[k][129];
				switches[k][2] = 1 - inBuffer[k][130];
				switches[k][3] = 1 - inBuffer[k][131];
				switchbinary[k] = (switches[k][3] << 3 | switches[k][2] << 2 | switches[k][1] << 1 | switches[k][0]);
				ttime[k] = ((inBuffer[k][132] << 8) | (inBuffer[k][133] & 0xff));
				off[k] = off[k] + 1;
				if (off[k] == nframes) {
					off[k] = 0;
				}
//				redraw();
			}
		}
	}

	double gyro[][][] = new double[2][10][3];
	double angles[][][] = new double[2][10][3];
	float[][] roll = { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
	float[][] pitch = { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
	float[][] yaw = { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
	float[][] magavg = new float[2][9];
	float[][][] imuavg = new float[2][10][6];
	double dt = .001;
	double t = .002;
	double a = t / (t + dt);
	double a1 = 1 - a;
	float zoffset = 0f;

	public void rightCalc() {
		calcrpy(0);
		calcPress(0);
	}

	public void leftCalc() {
		calcrpy(1);
		calcPress(1);
	}

	public void calcPress(int k) {
		if (switchbinary[k] == 1) {
			presst[k][0] += normpress[k] / 10;
		} else if (switchbinary[k] == 2) {
			presst[k][0] -= normpress[k] / 10;
		}
		if (switchbinary[k] == 4) {
			presst[k][1] += normpress[k] / 10;
		} else if (switchbinary[k] == 8) {
			presst[k][1] -= normpress[k] / 10;
		}
	}

	public void calcrpy(int k) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < nframes; j++) {
				magavg[k][i] += nmag[k][i][j];
			}
			magavg[k][i] /= nframes;
			if(i<6) {
				imuavg[k][0][i]=magavg[k][i];
			}
		}
		for (int i = 1; i < 10; i++) {
			for (int j = 0; j < 6; j++) {
				for (int n = 0; n < nframes; n++) {
					imuavg[k][i][j] += nimu[k][i][j][n];
				}
				imuavg[k][i][j] /= nframes;
			}
		}
		roll[k][0] = atan2(magavg[k][1], magavg[k][2]) * 180 / PI;
		pitch[k][0] = atan2(-magavg[k][0], sqrt((magavg[k][1] * magavg[k][1]) + (magavg[k][2] * magavg[k][2]))) * 180 / PI;
		yaw[k][0] = atan2(magavg[k][6], magavg[k][7]) * 180 / PI + zoffset;
		// yaw[k][0]=atan2(sqrt((magavg[k][0]*magavg[k][0])+(magavg[k][1]*magavg[k][1])), magavg[k][2]);

		for (int i = 1; i < 10; i++) {
			roll[k][i] = -atan2(imuavg[k][i][0], imuavg[k][i][2]) * 180 / PI;
			pitch[k][i] = atan2(-imuavg[k][i][1], sqrt((imuavg[k][i][0] * imuavg[k][i][0]) + (imuavg[k][i][2] * imuavg[k][i][2]))) * 180 / PI;
			yaw[k][i] = atan2(sqrt((imuavg[k][i][1] * imuavg[k][i][1]) + (imuavg[k][i][0] * imuavg[k][i][0])), imuavg[k][i][2]);
		}
	}

}