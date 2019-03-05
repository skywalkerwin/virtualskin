package virtualskin;

import processing.core.PApplet;
import processing.serial.Serial;

public class Body {
	PApplet proc;
	Side left;
	Side right;
	Body(PApplet p, Serial leftport, Serial rightport){
		proc = p;
		left = new Side(proc, leftport);
		right = new Side(proc, rightport);
	}
	void leftSerial() {
		left.serialEvent();
	}
	void rightSerial() {
		right.serialEvent();
	}
	public void serialEvents() {
//		proc.thread("body.leftSerial");
//		proc.thread("body.rightSerial");
		left.serialEvent();
		right.serialEvent();
	}
	public void printSendTimes() {
		proc.println(left.ttime);
		proc.println(right.ttime);
		proc.println();
	}
	public void printTRPY() {
		proc.println(left.roll[10],left.pitch[10],left.yaw[10]);
		proc.println(right.roll[10],right.pitch[10],right.yaw[10]);
		proc.println();
	}

}
