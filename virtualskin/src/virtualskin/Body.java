package virtualskin;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.serial.Serial;

public class Body {
	PApplet proc;
	Side left;
	Side right;
	float roty = 0f;

	Body(PApplet p, Serial leftport, Serial rightport) {
		proc = p;
		left = new Side(proc, leftport, 1);
		right = new Side(proc, rightport, 0);
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
		for (int i = 0; i < 10; i++) {
			int dscale = 30;
			proc.pushMatrix();
			proc.translate(0, (1 + i) * proc.height / 11, 0);
			proc.rotateX(PConstants.PI / 2);
			proc.rotateX(side.pitch[i] * PConstants.PI / 180);
			proc.rotateY(side.roll[i] * PConstants.PI / 180);
			proc.rotateZ(side.yaw[i] * PConstants.PI / 180);
//			proc.translate((float) side.imu[i][0] * dscale, (float) side.imu[i][1] * dscale,
//					(float) side.imu[i][2] * dscale);
			proc.strokeWeight(1);
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
		proc.translate(1*proc.width/4,0,0);
		testimus(left);
		proc.popMatrix();
		proc.pushMatrix();
		proc.translate(3*proc.width/4,0,0);
		testimus(right);
		proc.popMatrix();
		
		proc.translate(proc.width / 2, proc.height / 2, 0);
		proc.rotateX(PConstants.PI/3);
		proc.translate(0,-500,-500);
		torso();
		head();
		arm(left, -1);
		arm(right, 1);
	}
	
	public void head() {
		proc.pushMatrix();
		proc.translate(0,0,240);
		proc.fill(255);
		proc.sphere(50);
		proc.translate(0,0,110);
		proc.fill(255,0,0);
		proc.box(120,150,150);
		proc.popMatrix();
	}
	
	public void arm(Side side, int direction) {
		int d=direction;
		proc.pushMatrix();
		proc.translate(d*160,0,225);
		proc.fill(255);
		proc.sphere(50);
		proc.rotateX(-PConstants.PI / 2);
		proc.rotateX(side.pitch[0] * PConstants.PI / 180);
		proc.rotateY(side.roll[0] * PConstants.PI / 180);
		proc.rotateZ(side.yaw[0] * PConstants.PI / 180);
		proc.translate(d*80,0,-150);
		proc.strokeWeight(1);
		proc.stroke(255, 0, 0);
		proc.line(-500, 0, 0, 500, 0, 0);
		proc.stroke(0, 255, 0);
		proc.line(0, -500, 0, 0, 500, 0);
		proc.stroke(0, 0, 255);
		proc.line(0, 0, -500, 0, 0, 500);
		proc.fill(255,0,0);
		proc.box(100,100,300);
		proc.translate(0,0,-170);
		proc.fill(255);
		proc.sphere(40);
		proc.rotateX(side.pitch[2] * PConstants.PI / 180);
		proc.rotateY(side.roll[2] * PConstants.PI / 180);
		proc.rotateZ(side.yaw[2] * PConstants.PI / 180);
		proc.translate(00,0,-140);
		proc.strokeWeight(1);
		proc.stroke(255, 0, 0);
		proc.line(-500, 0, 0, 500, 0, 0);
		proc.stroke(0, 255, 0);
		proc.line(0, -500, 0, 0, 500, 0);
		proc.stroke(0, 0, 255);
		proc.line(0, 0, -500, 0, 0, 500);
		proc.fill(255,0,0);
		proc.box(80,80,250);
		proc.translate(0,0,-140);
		proc.fill(255);
		proc.sphere(30);
		proc.rotateX(side.pitch[9] * PConstants.PI / 180);
		proc.rotateY(side.roll[9] * PConstants.PI / 180);
		proc.rotateZ(side.yaw[9] * PConstants.PI / 180);
		proc.translate(0,0,-60);
		proc.strokeWeight(1);
		proc.stroke(255, 0, 0);
		proc.line(-500, 0, 0, 500, 0, 0);
		proc.stroke(0, 255, 0);
		proc.line(0, -500, 0, 0, 500, 0);
		proc.stroke(0, 0, 255);
		proc.line(0, 0, -500, 0, 0, 500);
		proc.fill(255,0,0);
		proc.box(80,50,90);
		proc.popMatrix();
		
	}
	public void torso() {
		proc.pushMatrix();
		proc.strokeWeight(1);
		proc.stroke(255);
		proc.fill(0, 255, 0);
		proc.box(300, 150, 450);
		proc.popMatrix();
	}

}
