package virtualskin;

import processing.core.PApplet;
import processing.serial.*;

public class processing extends PApplet {

	String leftcom = "COM4";
	String rightcom = "COM5";
//	String leftcom = "COM11";
//	String rightcom = "COM10";
	Serial rightPort;
	Serial leftPort;

	Body body;
//	static double ascale0 = .000061;
//	static double ascale1 = .000122;
//	static double ascale2 = .000244;
//	static double ascale3 = .000488;
//	static double[] ascales = { .000061, .000122, .000244, .000488 };
//	static double gscale0 = 0.007633;
//	static double gscale1 = 0.015267;
//	static double gscale2 = 0.030534;
//	static double gscale3 = 0.061068;
//	static double[] gscales = { 0.007633, 0.015267, 0.030534, 0.061068 };
//	static double ascale = ascale3;
//	static double gscale = gscale3;
//	static int[] atrans = { 2, 4, 8, 16 };
//	static int[] gtrans = { 250, 500, 1000, 2000 };
//	static int atran = atrans[3];
//	static int gtran = gtrans[3];
//	static int[] imuscales = { atran, gtran };

	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", "virtualskin.processing" });
	}

	public void settings() {
//		size(100, 100, P3D);
		fullScreen(P3D, 1);
	}

	public void setup() {
		frameRate(60);
		background(0);
		ellipseMode(RADIUS);

		rightPort = new Serial(this, rightcom, 115200);
		rightPort.clear();
		rightPort.buffer(134);
		leftPort = new Serial(this, leftcom, 115200);
		leftPort.clear();
		leftPort.buffer(134);
		delay(1000);
		sphereDetail(10);
		body = new Body(this, leftPort, rightPort);
	}

	int order = 0;
	int mt1, mt2, mt3, mt4, mt5 = 0;
	long nt1, nt2, nt3, nt4, nt5 = 0;
	long totalnano=0;
	long avgnano = 0;
	int counter = 1;
	float rotx = 0;
	float dscale = 40;
	int flicker = 5;
	int[] randomBox = { 0, 0 };

	public void leftCollect() {
		body.left.serialEvent();
	}
	public void rightCollect() {
		body.right.serialEvent();
	}

	public void draw() {
		background(0);
		int m1=millis();
		long n1=System.nanoTime();
		thread("leftCollect");
		thread("rightCollect");

		body.printSendTimes();
		body.printTRPY();
		int m2=millis();
		long n2=System.nanoTime();
		int m3 = m2-m1;
		long n3 = n2 - n1;
		totalnano += n3;
		avgnano = totalnano/(counter+1);
		println("TOTAL MILLIS CYCLE TIME: ", m3);
		println("TOTAL NANO CYCLE TIME:   ", n3);
		println();
		println("COUNTER: ", counter);
		println("AVERAGE NANO CYCLE TIME: ", avgnano);

		strokeWeight(3);
		stroke(255);
		fill(255);
		textSize(40);
		text(frameRate, 30, 30);
		counter++;
//		fill(255, 0, 0);
////		randomBox[0] = (int) random(0, 10);
////		randomBox[1] = (int) random(0, 10);
//		for (int k = 0; k < 2; k++) {
//			for (int i = 0; i < 10; i++) {
//				pushMatrix();
//				translate((6 - (1 + (k * 4))) * width / 6, (1 + i) * height / 11, 0);
//				rotateX(PI / 2);
//				rotateX(pitch[k][i] * PI / 180);
//				rotateY(roll[k][i] * PI / 180);
//				rotateZ(yaw[k][i] * PI / 180);
//				translate((float) imu[k][i][0] * dscale, (float) imu[k][i][1] * dscale, (float) imu[k][i][2] * dscale);
////				strokeWeight((float) imu[1][i][0] / 2);
//				strokeWeight(1);
//				stroke(255, 0, 0);
//				line(-1000, 0, 0, 1000, 0, 0);
////				strokeWeight((float) imu[1][i][1] / 2);
//				stroke(0, 255, 0);
//				line(0, -1000, 0, 0, 1000, 0);
////				strokeWeight((float) imu[1][i][2] / 2);
//				stroke(0, 0, 255);
//				line(0, 0, -1000, 0, 0, 1000);
//				stroke(0);
//				fill(255);
//				strokeWeight(5);
//				box(30);
//				popMatrix();
//			}
//			pushMatrix();
//			translate((6 - (2 + (k * 2))) * width / 6, height / 2, 0);
//			rotateX(PI / 2);
//			rotateX(pitch[k][10] * PI / 180);
//			rotateY(roll[k][10] * PI / 180);
//			rotateZ(yaw[k][10] * PI / 180);
//			strokeWeight(1);
//			stroke(255, 0, 0);
//			line(-1000, 0, 0, 1000, 0, 0);
//			stroke(0, 255, 0);
//			line(0, -1000, 0, 0, 1000, 0);
//			stroke(0, 0, 255);
//			line(0, 0, -1000, 0, 0, 1000);
//			stroke(0);
//			fill(255);
//			strokeWeight(5);
//			box(50);
//			popMatrix();
//		}
//
//		pushMatrix();
//		translate(width / 2, height / 4, 0);
//		rotateX(PI / 2);
//		rotateX(spitch * PI / 180);
//		rotateY(sroll * PI / 180);
//		rotateZ(syaw * PI / 180);
//		strokeWeight(1);
//		stroke(255, 0, 0);
//		line(-1000, 0, 0, 1000, 0, 0);
//		stroke(0, 255, 0);
//		line(0, -1000, 0, 0, 1000, 0);
//		stroke(0, 0, 255);
//		line(0, 0, -1000, 0, 0, 1000);
//		stroke(0);
//		fill(255);
//		strokeWeight(5);
//		box(100);
//		popMatrix();
//
//		pushMatrix();
//		translate(width / 2, 3 * height / 4, 0);
//		rotateX(PI / 2);
//		rotateX(tpitch * PI / 180);
//		rotateY(troll * PI / 180);
//		rotateZ(tyaw * PI / 180);
//		strokeWeight(1);
//		stroke(255, 0, 0);
//		line(-1000, 0, 0, 1000, 0, 0);
//		stroke(0, 255, 0);
//		line(0, -1000, 0, 0, 1000, 0);
//		stroke(0, 0, 255);
//		line(0, 0, -1000, 0, 0, 1000);
//		stroke(0);
//		fill(255);
//		strokeWeight(5);
//		box(100);
//		popMatrix();
//
//		hcount[0]++;
//		hcount[1]++;
//		if (hcount[0] == histlength) {
//			hcount[0] = 0;
//		}
//		if (hcount[1] == histlength) {
//			hcount[1] = 0;
//		}
//	}
//	double gyro[][][] = new double[2][10][3];
//	double angles[][][] = new double[2][10][3];
//	float[][] roll = new float[2][11];// { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
//	float[][] pitch = new float[2][11];// { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
//	float[][] yaw = new float[2][11];// { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
//	float sroll = 0;
//	float spitch = 0;
//	float syaw = 0;
//	float troll = 0;
//	float tpitch = 0;
//	float tyaw = 0;
//	float[][] magavg = new float[2][9];
//	float[][][] imuavg = new float[2][10][6];
//	double dt = .001;
//	double t = .002;
//	double a = t / (t + dt);
//	double a1 = 1 - a;
//	float zoffset = 0f;
//
//	public void rightCalc() {
//		calcrpy(0);
//		calcPress(0);
//	}
//
//	public void leftCalc() {
//		calcrpy(1);
//		calcPress(1);
//	}
//
//	public void calcPress(int k) {
//		if (switchbinary[k] == 1) {
//			presst[k][0] += normpress[k] / 10;
//		} else if (switchbinary[k] == 2) {
//			presst[k][0] -= normpress[k] / 10;
//		}
//		if (switchbinary[k] == 4) {
//			presst[k][1] += normpress[k] / 10;
//		} else if (switchbinary[k] == 8) {
//			presst[k][1] -= normpress[k] / 10;
//		}
//	}
//
//	public void calcrpy(int k) {
//		for (int i = 0; i < 9; i++) {
//			for (int j = 0; j < nframes; j++) {
//				magavg[k][i] += nmag[k][i][j];
//			}
//			magavg[k][i] /= nframes;
//			if (i < 6) {
//				imuavg[k][9][i] = magavg[k][i];
//			}
//		}
//		for (int i = 0; i < 9; i++) {
//			for (int j = 0; j < 6; j++) {
//				for (int n = 0; n < nframes; n++) {
//					imuavg[k][i][j] += nimu[k][i][j][n];
//				}
//				imuavg[k][i][j] /= nframes;
//			}
//		}
//		roll[k][9] = atan2(magavg[k][1], magavg[k][2]) * 180 / PI;
//		pitch[k][9] = atan2(-magavg[k][0], sqrt((magavg[k][1] * magavg[k][1]) + (magavg[k][2] * magavg[k][2]))) * 180
//				/ PI;
//		yaw[k][9] = atan2(magavg[k][6], magavg[k][7]) * 180 / PI + zoffset;
//		// yaw[k][0]=atan2(sqrt((magavg[k][0]*magavg[k][0])+(magavg[k][1]*magavg[k][1])),
//		// magavg[k][2]);
//
//		for (int i = 0; i < 9; i++) {
//			roll[k][i] = -atan2(imuavg[k][i][0], imuavg[k][i][2]) * 180 / PI;
//			pitch[k][i] = atan2(-imuavg[k][i][1],
//					sqrt((imuavg[k][i][0] * imuavg[k][i][0]) + (imuavg[k][i][2] * imuavg[k][i][2]))) * 180 / PI;
//			yaw[k][i] = atan2(sqrt((imuavg[k][i][1] * imuavg[k][i][1]) + (imuavg[k][i][0] * imuavg[k][i][0])),
//					imuavg[k][i][2]);
//		}
//		roll[k][10] = 0;
//		pitch[k][10] = 0;
//		yaw[k][10] = 0;
//		for (int i = 0; i < 10; i++) {
//			roll[k][10] += roll[k][i];
//			pitch[k][10] += pitch[k][i];
//			yaw[k][10] += yaw[k][i];
//		}
////		roll[k][10] /= 10;
////		pitch[k][10] /= 10;
////		yaw[k][10] /= 10;
//	}
//
//	public void calcSumrpy() {
//		sroll = (roll[0][10] + roll[1][10]);
//		spitch = (pitch[0][10] + pitch[1][10]);
//		syaw = (yaw[0][10] + yaw[1][10]);
//	}
//
//	public void calcAvgrpy() {
//		troll = sroll / 2;
//		tpitch = spitch / 2;
//		tyaw = syaw / 2;
//	}
	}
}
