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
//	static double gscale = gscale3;`
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
		fullScreen(P3D, 2);
	}

	public void setup() {
		frameRate(60);
		background(0);
		ellipseMode(RADIUS);

		rightPort = new Serial(this, rightcom, 115200);
		rightPort.clear();
		rightPort.buffer(122);
		leftPort = new Serial(this, leftcom, 115200);
		leftPort.clear();
		leftPort.buffer(122);
		delay(1000);
		sphereDetail(10);
		body = new Body(this, leftPort, rightPort);
	}

	int order = 0;
	int mt1, mt2, mt3, mt4, mt5 = 0;
	long nt1, nt2, nt3, nt4, nt5 = 0;
	long totalnano = 0;
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
		ortho();
		background(50);
		int m1 = millis();
		long n1 = System.nanoTime();
		thread("leftCollect");
		thread("rightCollect");
		body.printSendTimes();
//		body.printTRPY();
		int m2 = millis();
		long n2 = System.nanoTime();
		body.verifyUpdate();
		int m3 = m2 - m1;
		long n3 = n2 - n1;
		totalnano += n3;
		avgnano = totalnano / (counter + 1);
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
		body.drawBody();

	}

	public void mousePressed() {
		exit();
	}
}
