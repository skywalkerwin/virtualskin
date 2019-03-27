package virtualskin;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.serial.Serial;

public class Body {
	PApplet proc;
	Side left;
	Side right;
	static float PI = PConstants.PI;
	int xyz = 1;
	int curLine = 0;
	int maxLines = 600;
	float[][][] linep = new float[2][maxLines][4];

	Body(PApplet p, Serial leftport, Serial rightport) {
		proc = p;
		left = new Side(proc, this, leftport, 0);
		right = new Side(proc, this, rightport, 0);
		for (int i = 0; i < maxLines; i++) {
			for (int j = 0; j < 4; j++) {
				linep[0][i][j] = 0;
				linep[1][i][j] = 0;
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

		proc.pushMatrix();
		proc.translate(1 * proc.width / 6, 0, 0);
		testimus(left);
		proc.popMatrix();
		
		proc.pushMatrix();
		proc.translate(5 * proc.width / 6, 0, 0);
		testimus(right);
		proc.popMatrix();

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

//		proc.pushMatrix();
//		proc.strokeWeight(1);
////		proc.beginShape(proc.TRIANGLE_FAN);
////		proc.beginShape();
////		proc.text(curLine, 100, 100);
//		proc.noFill();
//		for (int i = 255; i > curLine; i--) {
//			proc.stroke(i - curLine);
//			proc.strokeWeight(1);
//			proc.line(linep[0][i][0], linep[0][i][1], linep[0][i][2], linep[1][i][0], linep[1][i][1], linep[1][i][2]);
////			proc.stroke(0,255,0);
////			proc.strokeWeight(5);
////			proc.point((linep[0][i][0]+linep[1][i][0])/2, (linep[0][i][1]+linep[1][i][1])/2, (linep[0][i][2]+linep[1][i][2])/2);
////			proc.vertex(linep[0][i][0], linep[0][i][1], linep[0][i][2]);
////			proc.vertex(linep[1][i][0], linep[1][i][1], linep[1][i][2]);
//}
//		for (int i = curLine; i >= 0; i--) {
//			proc.stroke(255 - (curLine - i));
//			proc.strokeWeight(1);
//			proc.line(linep[0][i][0], linep[0][i][1], linep[0][i][2], linep[1][i][0], linep[1][i][1], linep[1][i][2]);
////			proc.stroke(0,255,0);
////			proc.strokeWeight(5);
////			proc.point((linep[0][i][0]+linep[1][i][0])/2, (linep[0][i][1]+linep[1][i][1])/2, (linep[0][i][2]+linep[1][i][2])/2);
////			proc.vertex(linep[0][i][0], linep[0][i][1], linep[0][i][2]);
////			proc.vertex(linep[1][i][0], linep[1][i][1], linep[1][i][2]);
//		}
////		proc.endShape();
//		proc.popMatrix();

//		proc.pushMatrix();
//		proc.strokeWeight(1);
////		proc.beginShape(proc.TRIANGLE_FAN);
////		proc.beginShape();
//		proc.noFill();
////		proc.stroke(255);
//		for (int i = maxLines - 1; i > curLine; i--) {
//			proc.stroke((linep[0][i][3]+linep[1][i][3])/2*255);
//			proc.strokeWeight(1);
//			proc.line(linep[0][i][0], linep[0][i][1], linep[0][i][2], linep[1][i][0], linep[1][i][1], linep[1][i][2]);
//
//		}
//		for (int i = curLine; i >= 0; i--) {
//			proc.stroke((linep[0][i][3]+linep[1][i][3])/2*255);			
//			proc.strokeWeight(1);
//			proc.line(linep[0][i][0], linep[0][i][1], linep[0][i][2], linep[1][i][0], linep[1][i][1], linep[1][i][2]);
//		}
////		proc.endShape();
//		proc.popMatrix();

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

	int drawbool = 1;

	public void arm(Side side, int direction) {
		int d = direction;
		int ulen = 300;
		int llen = 200;
		proc.pushMatrix();
		proc.translate(d * 200, 0, -200);
		if (drawbool == 1) {
			proc.fill(255);
			proc.stroke(0);
			proc.sphere(30);
		}
		proc.rotateZ(side.yaw[0] * PI / 180);
		proc.rotateX(side.pitch[0] * PI / 180);
		proc.rotateY(-side.roll[0] * PI / 180);
		proc.rotateX(PI / 2);
		proc.rotateZ(-PI / 2);
		proc.translate(d * 0, 0, -150);
		if (xyz == 1) {
			xyzlines();
		}
		if (drawbool == 1) {
			proc.strokeWeight(4);
			proc.stroke(0);
			proc.fill(0, 255, 0);
//			proc.fill(255);
			proc.box(50, 50, -300);
		}
		proc.translate(0, 0, -170);
		if (drawbool == 1) {
			proc.fill(255);
			proc.stroke(0);
			proc.sphere(20);
		}
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
		if (drawbool == 1) {
			proc.strokeWeight(3);
			proc.stroke(0);
			proc.fill(0, 255, 0);
//			proc.fill(255);
			proc.box(40, 40, -250);
		}
		proc.translate(0, 0, -140);
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
		int len = 100;
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
		proc.translate(0, 0, -len);
		int k = d;
		if (k == -1) {
			k = 0;
		}
		linep[k][curLine][0] = proc.modelX(0, 0, 0);
		linep[k][curLine][1] = proc.modelY(0, 0, 0);
		linep[k][curLine][2] = proc.modelZ(0, 0, 0);
		linep[k][curLine][3] = (float)side.normpress;
		if (xyz == 1) {
			xyzlines();
		}
		if (drawbool == 1) {
			proc.strokeWeight(2);
			proc.stroke(0);
			proc.fill(0, 255, 0);
//			proc.fill(255);
			proc.box(30, 30, -90);
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
