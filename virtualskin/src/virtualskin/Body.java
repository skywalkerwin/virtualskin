package virtualskin;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.serial.Serial;

public class Body {
	PApplet proc;
	Side left;
	Side right;
	static float PI = PConstants.PI;
	int xyz = 0;
	int curLine = 0;
	int maxLines = 255;
	float[][][] handpoint = new float[2][maxLines][4];
	float[][][] elbowpoint = new float[2][maxLines][4];

	Body(PApplet p, Serial leftport, Serial rightport) {
		proc = p;
		left = new Side(proc, this, leftport, 0);
		right = new Side(proc, this, rightport, 0);
		for (int i = 0; i < maxLines; i++) {
			for (int j = 0; j < 4; j++) {
				handpoint[0][i][j] = 0;
				handpoint[1][i][j] = 0;
				elbowpoint[0][i][j] = 0;
				elbowpoint[1][i][j] = 0;
			}
		}
	}

	public void verifyUpdate() {
		if (left.firstContact == true && right.firstContact == true) {
			while ((left.updated + right.updated) != 2) {
				PApplet.println("WAITING...");
			}
		}
	}

	public void printSendTimes() {
		PApplet.println(left.ttime);
		PApplet.println(right.ttime);
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

		// DRAW BODY STUFF
		proc.pushMatrix();
		proc.translate(proc.width / 2, proc.height / 2, 0);
		proc.rotateX(-PI / 2);
//		proc.scale(.5f);
//		torso();
//		head();
		arm(left, -1);
		arm(right, 1);
//		legs();
		proc.popMatrix();

		proc.pushMatrix();
		proc.strokeWeight(1);
		proc.stroke(255);
		for (int i = maxLines - 1; i > curLine; i--) {
//			proc.stroke(i - curLine);
//			proc.strokeWeight(1);
//			proc.stroke(0, (i - curLine), 0);
			proc.stroke(0,255,0);
			proc.line(handpoint[0][i][0], handpoint[0][i][1], handpoint[0][i][2], handpoint[1][i][0],
					handpoint[1][i][1], handpoint[1][i][2]);
//			proc.stroke((i - curLine), 0, 0);
			proc.stroke(255,0,0);
			proc.line(elbowpoint[0][i][0], elbowpoint[0][i][1], elbowpoint[0][i][2], elbowpoint[1][i][0],
					elbowpoint[1][i][1], elbowpoint[1][i][2]);
//			proc.stroke(255);
//			proc.point(	(((handpoint[0][i][0]+handpoint[1][i][0])/2)+((elbowpoint[0][i][0]+elbowpoint[1][i][0])/2))/2, 
//					(((handpoint[0][i][1]+handpoint[1][i][1])/2)+((elbowpoint[0][i][1]+elbowpoint[1][i][1])/2))/2,
//					(((handpoint[0][i][2]+handpoint[1][i][2])/2)+((elbowpoint[0][i][2]+elbowpoint[1][i][2])/2))/2); 
		}
		for (int i = curLine; i >= 0; i--) {
//			proc.stroke(255 - (curLine - i));
//			proc.strokeWeight(1);
//			proc.stroke(0, 255 - (curLine - i), 0);
			proc.stroke(0,255,0);
			proc.line(handpoint[0][i][0], handpoint[0][i][1], handpoint[0][i][2], handpoint[1][i][0],
					handpoint[1][i][1], handpoint[1][i][2]);
//			proc.stroke(255 - (curLine - i), 0, 0);
			proc.stroke(255,0,0);
			proc.line(elbowpoint[0][i][0], elbowpoint[0][i][1], elbowpoint[0][i][2], elbowpoint[1][i][0],
					elbowpoint[1][i][1], elbowpoint[1][i][2]);
//			proc.stroke(255);
//			proc.point(	(((handpoint[0][i][0]+handpoint[1][i][0])/2)+((elbowpoint[0][i][0]+elbowpoint[1][i][0])/2))/2, 
//					(((handpoint[0][i][1]+handpoint[1][i][1])/2)+((elbowpoint[0][i][1]+elbowpoint[1][i][1])/2))/2,
//					(((handpoint[0][i][2]+handpoint[1][i][2])/2)+((elbowpoint[0][i][2]+elbowpoint[1][i][2])/2))/2); 
		}
		proc.popMatrix();

		proc.pushMatrix();
		proc.strokeWeight(1);
		proc.noFill();
		proc.stroke(0, 0, 255);
		for (int i = maxLines - 1; i > curLine; i--) {
//			proc.stroke(0, 0, (i - curLine));
			proc.bezier(handpoint[0][i][0], handpoint[0][i][1], handpoint[0][i][2], elbowpoint[0][i][0],
					elbowpoint[0][i][1], elbowpoint[0][i][2], elbowpoint[1][i][0], elbowpoint[1][i][1],
					elbowpoint[1][i][2], handpoint[1][i][0], handpoint[1][i][1], handpoint[1][i][2]);
		}
		for (int i = curLine; i >= 0; i--) {
//			proc.stroke(0, 0, 255 - (curLine - i));
			proc.bezier(handpoint[0][i][0], handpoint[0][i][1], handpoint[0][i][2], elbowpoint[0][i][0],
					elbowpoint[0][i][1], elbowpoint[0][i][2], elbowpoint[1][i][0], elbowpoint[1][i][1],
					elbowpoint[1][i][2], handpoint[1][i][0], handpoint[1][i][1], handpoint[1][i][2]);
		}
//		proc.stroke(255);
//		for (int i = maxLines - 1; i > curLine; i--) {
//			proc.stroke((i - curLine));
//			proc.bezier(elbowpoint[0][i][0], elbowpoint[0][i][1], elbowpoint[0][i][2], handpoint[0][i][0],
//					handpoint[0][i][1], handpoint[0][i][2], handpoint[1][i][0], handpoint[1][i][1], handpoint[1][i][2],
//					elbowpoint[1][i][0], elbowpoint[1][i][1], elbowpoint[1][i][2]);
//		}
//		for (int i = curLine; i >= 0; i--) {
//			proc.stroke(255 - (curLine - i));
//			proc.bezier(elbowpoint[0][i][0], elbowpoint[0][i][1], elbowpoint[0][i][2], handpoint[0][i][0],
//					handpoint[0][i][1], handpoint[0][i][2], handpoint[1][i][0], handpoint[1][i][1], handpoint[1][i][2],
//					elbowpoint[1][i][0], elbowpoint[1][i][1], elbowpoint[1][i][2]);
//		}
		proc.popMatrix();

		curLine++;
		if (curLine == maxLines) {
			curLine = 0;
		}
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

	int drawbool = 0;

	public void arm(Side side, int direction) {
		int d = direction;
		int ulen = 250;
		int llen = 200;
		proc.pushMatrix();
		proc.strokeWeight(1);
		proc.translate(d * 200, 0, -200);
		if (drawbool == 1) {
			proc.fill(255);
			proc.stroke(0);
			proc.sphere(30);
		}
		proc.rotateZ(side.yaw[0] * PI / 180);
		proc.rotateX(side.pitch[0] * PI / 180);
		proc.rotateY(-side.roll[0] * PI / 180);
//		proc.rotateX(PI / 2);
//		proc.rotateZ(-PI / 2);
		proc.translate(d * 0, ulen / 2, 0);
		if (xyz == 1) {
			xyzlines();
		}
		if (drawbool == 1) {
			proc.strokeWeight(4);
			proc.stroke(0);
			proc.fill(0, 255, 0);
//			proc.fill(255);
			proc.box(50, ulen, 50);
		}
		proc.translate(0, ulen / 2, 0);
		int k = d;
		if (k == -1) {
			k = 0;
		}
		elbowpoint[k][curLine][0] = proc.modelX(0, 0, 0);
		elbowpoint[k][curLine][1] = proc.modelY(0, 0, 0);
		elbowpoint[k][curLine][2] = proc.modelZ(0, 0, 0);
		if (drawbool == 1) {
			proc.fill(255);
			proc.stroke(0);
			proc.sphere(20);
		}
		proc.rotateY(side.roll[0] * PI / 180);
		proc.rotateX(-side.pitch[0] * PI / 180);
		proc.rotateZ(-side.yaw[0] * PI / 180);

		proc.rotateZ(side.yaw[1] * PI / 180);
		proc.rotateX(side.pitch[1] * PI / 180);
		proc.rotateY(-side.roll[1] * PI / 180);
		proc.translate(0, llen / 2, 0);
		if (xyz == 1) {
			xyzlines();
		}
		if (drawbool == 1) {
			proc.strokeWeight(3);
			proc.stroke(0);
			proc.fill(0, 255, 0);
//			proc.fill(255);
			proc.box(40, llen, 40);
		}
		proc.translate(0, llen / 2, 0);
		if (drawbool == 1) {
			proc.fill(255);
			proc.stroke(0);
			proc.sphere(20);
		}
		hands(side, d);
		proc.popMatrix();

	}

	public void hands(Side side, int direction) {
		int d = direction;
		int len = 300;
		proc.rotateY(side.roll[1] * PI / 180);
		proc.rotateX(-side.pitch[1] * PI / 180);
		proc.rotateZ(-side.yaw[1] * PI / 180);

		proc.rotateZ(side.yaw[2] * PI / 180);
		proc.rotateX(side.pitch[2] * PI / 180);
		proc.rotateY(-side.roll[2] * PI / 180);

		proc.translate(0, len / 2, 0);
		int k = d;
		if (k == -1) {
			k = 0;
		}
		handpoint[k][curLine][0] = proc.modelX(0, 0, 0);
		handpoint[k][curLine][1] = proc.modelY(0, 0, 0);
		handpoint[k][curLine][2] = proc.modelZ(0, 0, 0);
		handpoint[k][curLine][3] = (float) side.normpress;
		if (xyz == 1) {
			xyzlines();
		}
		if (drawbool == 1) {
			proc.strokeWeight(2);
			proc.stroke(0);
			proc.fill(0, 255, 0);
//			proc.fill(255);
			proc.box(30, len, 30);
		}
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
