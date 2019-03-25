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
	int xyz = 0;

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
		for (int i = 0; i < 3; i++) {
			proc.pushMatrix();
			proc.translate(0, (1 + i) * proc.height / 4, 0);
			proc.text(side.yaw[i], 50, 50);
			proc.text(side.pitch[i], 50, 100);
			proc.text(-side.roll[i], 50, 150);
			proc.rotateX(-PI / 2);
			proc.strokeWeight(.5f);
//			xyzlines();
			proc.rotateZ(side.yaw[i] * PI / 180);
			proc.rotateX(side.pitch[i] * PI / 180);
			proc.rotateY(-side.roll[i] * PI / 180);
//			proc.text(side.yaw[i], 50, 50);
//			proc.text(side.pitch[i], 50, 100);
//			proc.text(-side.roll[i], 50, 150);

			if (i == 0) {
				proc.strokeWeight(4);
			} else if (i == 1) {
				proc.strokeWeight(3);
			} else if (i == 2) {
				proc.strokeWeight(2);
			} else {
				proc.strokeWeight(1);
			}
			if (xyz == 1) {
				xyzlines();
			}
			proc.stroke(0);
			proc.fill(255);
			proc.strokeWeight(5);
			proc.box(30);
			proc.popMatrix();
		}
	}

	public void plots() {
//		proc.scale(.2f);
		left.plot9Lines(0, 2);
//		left.plot9Points(0, 0);
//		left.plot9Points(0, 1);
//		left.plot9Points(0, 2);
//		left.plot6Points(0, 0);
//		left.plot6Points(0, 1);
//		left.plot6Points(0, 2);

	}

	public void drawBody() {
		proc.pushMatrix();
//		plots();
		
//		proc.pushMatrix();
//		proc.translate(1 * proc.width / 6, 0, 0);
//		testimus(left);
//		proc.popMatrix();
//		
//		proc.pushMatrix();
//		proc.translate(5 * proc.width / 6, 0, 0);
//		testimus(right);
//		proc.popMatrix();
		
		proc.pushMatrix();
		proc.translate(proc.width / 2, proc.height / 2, 0);
		proc.rotateX(-PI / 2);
		proc.scale(.8f);
//		torso();
//		head();
		arm(left, -1);
		arm(right, 1);
//		legs();
		proc.popMatrix();
		
		proc.pushMatrix();
		proc.stroke(255);
		proc.strokeWeight(5);
		proc.line(left.mx, left.my,left.mz, right.mx, right.my, right.mz);
		proc.popMatrix();
		
		proc.popMatrix();
	}

	public void head() {
		proc.pushMatrix();
		proc.translate(0, 0, -230);
		proc.fill(255);
		proc.sphere(50);
		proc.translate(0, 0, -100);
		proc.fill(255, 0, 0);
		proc.strokeWeight(3);
		proc.stroke(0);
		proc.box(120, 150, -150);
		proc.translate(0, 0, -500);
		xyzlines();
		proc.popMatrix();
	}

	public void arm(Side side, int direction) {
		int d = direction;
		proc.pushMatrix();
		proc.translate(d * 200, 0, -200);
		proc.fill(255);
		proc.stroke(0);
		proc.sphere(50);
		proc.rotateZ(side.yaw[0] * PI / 180);
		proc.rotateX(side.pitch[0] * PI / 180);
		proc.rotateY(-side.roll[0] * PI / 180);
		proc.rotateX(PI / 2);
		proc.rotateZ(-PI / 2);
		proc.translate(d * 0, 0, -150);
		if (xyz == 1) {
			xyzlines();
		}
		proc.strokeWeight(4);
		proc.stroke(0);
		proc.fill(0, 255, 0);
		proc.fill(255);
		proc.box(100, 100, -300);

		proc.translate(0, 0, -170);
		proc.fill(255);
		proc.stroke(0);
		proc.sphere(40);
		proc.rotateZ(PI / 2);
		proc.rotateX(-PI / 2);
		proc.rotateY(side.roll[0] * PI / 180);
		proc.rotateX(-side.pitch[0] * PI / 180);
		proc.rotateZ(-side.yaw[0] * PI / 180);

		proc.rotateZ(side.yaw[1] * PI / 180);
		proc.rotateX(side.pitch[1] * PI / 180);
		proc.rotateY(-side.roll[1] * PI / 180);
		proc.rotateX(PI / 2);
		proc.rotateZ(-PI / 2);
		proc.translate(00, 0, -140);
		if (xyz == 1) {
			xyzlines();
		}
		proc.strokeWeight(3);
		proc.stroke(0);
		proc.fill(0, 255, 0);
		proc.fill(255);
		proc.box(80, 80, -250);
		proc.translate(0, 0, -140);
		proc.fill(255);
		proc.stroke(0);
		proc.sphere(30);
		hands(side, d);
		proc.popMatrix();

	}

	public void hands(Side side, int direction) {
		int d = direction;
		proc.rotateZ(PI / 2);
		proc.rotateX(-PI / 2);
		proc.rotateY(side.roll[1] * PI / 180);
		proc.rotateX(-side.pitch[1] * PI / 180);
		proc.rotateZ(-side.yaw[1] * PI / 180);

		proc.rotateZ(side.yaw[2] * PI / 180);
		proc.rotateX(side.pitch[2] * PI / 180);
		proc.rotateY(-side.roll[2] * PI / 180);

		proc.rotateX(PI / 2);
		proc.rotateZ(-PI / 2);
		proc.translate(0, 0, -60);

		side.mx = proc.modelX(0, 0, 0);
		side.my = proc.modelY(0, 0, 0);
		side.mz = proc.modelZ(0, 0, 0);
		if (xyz == 1) {
			xyzlines();
		}
		proc.strokeWeight(2);

		proc.stroke(0);
		proc.fill(0, 255, 0);
		proc.fill(255);
		proc.box(70, 70, -90);
	}

	public void torso() {
		proc.pushMatrix();
		proc.strokeWeight(3);
		proc.stroke(255);
		proc.fill(255, 0, 0);
		proc.stroke(0);
		proc.box(300, 150, -450);
		proc.translate(0, 0, 230);
		proc.fill(255);
//		proc.noStroke();
		proc.stroke(0);
		;
		proc.sphere(75);
		proc.popMatrix();
	}

	public void leg(int direction) {
		int d = direction;
		proc.pushMatrix();
		proc.strokeWeight(3);
		proc.stroke(0);
		proc.translate(d * 80, 0, 430);
		proc.fill(255, 0, 0);
		proc.box(125, 125, -350);
		proc.translate(0, 0, 180);
		proc.fill(255);
		proc.stroke(0);
		proc.sphere(40);
		proc.stroke(0);
		proc.translate(0, 0, 160);
		proc.fill(255, 0, 0);
		proc.box(100, 100, -270);
		proc.translate(0, 0, 140);
		proc.fill(255);
		proc.stroke(0);
		proc.sphere(30);
		proc.stroke(0);
		proc.translate(0, -50, 50);
		proc.fill(255, 0, 0);
		proc.box(100, 200, -50);
		proc.popMatrix();
	}

	public void legs() {
		proc.pushMatrix();
		leg(-1);
		leg(1);
		proc.popMatrix();
	}

	public void xyzlines() {
		proc.pushMatrix();
		int len = 300;
		proc.strokeWeight(4);
		proc.stroke(255, 0, 0);
		proc.line(0, 0, 0, len, 0, 0);
		proc.stroke(0, 255, 0);
		proc.line(0, 0, 0, 0, len, 0);
		proc.stroke(0, 0, 255);
		proc.line(0, 0, 0, 0, 0, len);
		proc.popMatrix();
	}
}
