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

	public void printSendTimes() {
		PApplet.println(left.ttime);
		PApplet.println(right.ttime);
		PApplet.println();
	}
	public void printTRPY() {
		PApplet.println(left.roll[10],left.pitch[10],left.yaw[10]);
		PApplet.println(right.roll[10],right.pitch[10],right.yaw[10]);
		PApplet.println();
	}

}
