package virtualskin;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.serial.Serial;

public class Body {
	PApplet proc;
	Side left;
	Side right;
//	float Now = 0f;
//	float deltat = 0f;
//	float lastUpdate = 0f;
//	float sum = 0f;
//	int sumCount = 0;
	static float PI = PConstants.PI;

	float roty = 0f;

	Body(PApplet p, Serial leftport, Serial rightport) {
		proc = p;
		left = new Side(proc, this, leftport, 0);
		right = new Side(proc, this, rightport, 0);
	}

	public void verifyUpdate() {
		if (left.firstContact == true && right.firstContact == true) {
			while ((left.updated + right.updated) != 2) {
				PApplet.println("WAITING...");
			}
		}
//		Now = System.nanoTime();
//		deltat = ((Now - lastUpdate) / 1000000000.0f); // set integration time by time elapsed since last filter update
//		lastUpdate = Now;
//		sum += deltat; // sum for averaging filter update rate
//		sumCount++;
//		PApplet.println(deltat);
	}

	public void printSendTimes() {
		PApplet.println(left.ttime);
		PApplet.println(right.ttime);
		PApplet.println();
	}

	public void printTRPY() {
		PApplet.println(left.roll[10], left.pitch[10], left.yaw[10]);
		PApplet.println(right.roll[10], right.pitch[10], right.yaw[10]);
		PApplet.println();
	}

	public void testimus(Side side) {
		for (int i = 0; i < 8; i++) {
//			int dscale = 30;
			proc.pushMatrix();
			proc.translate(0, (1 + i) * proc.height / 9, 0);
			proc.rotateX(PI / 2);
			proc.rotateX(side.roll[i] * PI / 180);
			proc.rotateY(side.pitch[i] * PI / 180);
			proc.rotateZ(side.yaw[i] * PI / 180);
//			proc.translate((float) side.imu[i][0] * dscale, (float) side.imu[i][1] * dscale,
//					(float) side.imu[i][2] * dscale);
			if (i == 0) {
				proc.strokeWeight(4);
			} else if (i == 1) {
				proc.strokeWeight(3);
			} else if (i == 2) {
				proc.strokeWeight(2);
			} else {
				proc.strokeWeight(1);
			}

			proc.stroke(255, 0, 0);
			proc.line(-500, 0, 0, 500, 0, 0);
			proc.stroke(0, 255, 0);
			proc.line(0, -500, 0, 0, 500, 0);
			proc.stroke(0, 0, 255);
			proc.line(0, 0, -500, 0, 0, 500);
			proc.stroke(0);
			proc.fill(255);
			proc.strokeWeight(5);
			proc.box(30);
			proc.popMatrix();
		}
	}

	public void drawBody() {
		
		proc.pushMatrix();
		proc.translate(1 * proc.width / 5, 0, 0);
		testimus(left);
		proc.popMatrix();
		proc.pushMatrix();
		proc.translate(4 * proc.width / 5, 0, 0);
		testimus(right);
		proc.popMatrix();

		proc.translate(proc.width / 2, proc.height / 2, 0);
		proc.rotateX(PI / 2);
//		proc.rotateZ(roty);
//		roty+=.01;
//		proc.translate(0,800,200);
		proc.scale(.5f);
		torso();
		head();
		arm(left, -1);
		arm(right, 1);
		legs();
	}

	public void head() {
		proc.pushMatrix();
		proc.translate(0, 0, 220);
		proc.fill(255);
		proc.sphere(50);
		proc.translate(0, 0, 100);
		proc.fill(255, 0, 0);
		proc.box(120, 150, 150);
		proc.popMatrix();
	}

	public void arm(Side side, int direction) {
		int d = direction;
		proc.pushMatrix();
		proc.translate(d * 160, 0, 200);
		proc.fill(255);
//		proc.stroke(0,0,255);
		proc.noStroke();
		proc.sphere(50);
//		proc.rotateX(-PI / 2);
		proc.rotateX(side.pitch[0] * PI / 180);
		proc.rotateY(side.roll[0] * PI / 180);
		proc.rotateZ(side.yaw[0] * PI / 180);
		proc.translate(d * 80, 0, -150);
		proc.strokeWeight(4);
		proc.stroke(255, 0, 0);
		proc.line(-500, 0, 0, 500, 0, 0);
		proc.stroke(0, 255, 0);
		proc.line(0, -500, 0, 0, 500, 0);
		proc.stroke(0, 0, 255);
		proc.line(0, 0, -500, 0, 0, 500);
		proc.fill(0, 255, 0);
		proc.box(100, 100, 300);
		proc.translate(0, 0, -170);
		proc.fill(255);
//		proc.stroke(0,0,255);
		proc.noStroke();
		proc.sphere(40);
		proc.rotateX(side.pitch[1] * PI / 180);
		proc.rotateY(side.roll[1] * PI / 180);
		proc.rotateZ(side.yaw[1] * PI / 180);
		proc.translate(00, 0, -140);
		proc.strokeWeight(3);
		proc.stroke(255, 0, 0);
		proc.line(-500, 0, 0, 500, 0, 0);
		proc.stroke(0, 255, 0);
		proc.line(0, -500, 0, 0, 500, 0);
		proc.stroke(0, 0, 255);
		proc.line(0, 0, -500, 0, 0, 500);
		proc.fill(0, 255, 0);
		proc.box(80, 80, 250);
		proc.translate(0, 0, -140);
		proc.fill(255);
//		proc.stroke(0,0,255);
		proc.noStroke();
		proc.sphere(30);
		proc.rotateX(side.pitch[2] * PI / 180);
		proc.rotateY(side.roll[2] * PI / 180);
		proc.rotateZ(side.yaw[2] * PI / 180);
		proc.translate(0, 0, -60);
		proc.strokeWeight(2);
		proc.stroke(255, 0, 0);
		proc.line(-500, 0, 0, 500, 0, 0);
		proc.stroke(0, 255, 0);
		proc.line(0, -500, 0, 0, 500, 0);
		proc.stroke(0, 0, 255);
		proc.line(0, 0, -500, 0, 0, 500);
		proc.fill(0, 255, 0);
		proc.box(80, 50, 90);
		proc.popMatrix();

	}

	public void torso() {
		proc.pushMatrix();
		proc.strokeWeight(1);
		proc.stroke(255);
		proc.fill(255, 0, 0);
		proc.box(300, 150, 420);
		proc.translate(0,0, -210);
		proc.noStroke();
		proc.fill(255);
		proc.sphere(75);
		proc.popMatrix();
	}
	
	public void leg(int direction) {
		int d = direction;
		proc.pushMatrix();
		proc.stroke(0);
		proc.translate(d*80, 0, -380);
		proc.fill(255, 0, 0);
		proc.box(125, 125, 280);
		proc.translate(0,0,-150);
		proc.fill(255);
		proc.noStroke();
		proc.sphere(40);
		proc.stroke(0);
		proc.translate(0, 0, -140);
		proc.fill(255, 0, 0);
		proc.box(100, 100, 230);
		proc.translate(0,0,-120);
		proc.fill(255);
		proc.noStroke();
		proc.sphere(30);
		proc.stroke(0);
		proc.translate(0, -80, -50);
		proc.fill(255, 0, 0);
		proc.box(100, 200, 50);
		proc.popMatrix();
	}
	public void legs() {
		proc.pushMatrix();
		leg(-1);
		leg(1);
		proc.popMatrix();
	}
}

