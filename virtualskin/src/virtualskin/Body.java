package virtualskin;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.serial.Serial;
import ddf.minim.*;
import ddf.minim.Minim;
import ddf.minim.AudioPlayer;
import ddf.minim.analysis.*;
import ddf.minim.ugens.*;

public class Body {
	PApplet proc;
	Side left;
	Side right;
	static float PI = PConstants.PI;
	int xyz = 0;
//	int xyz = 1;
	int curLine = 0;
	int maxLines = 100;
	float[][][] handpoint = new float[2][maxLines][4];
	float[][][] elbowpoint = new float[2][maxLines][4];
	Minim minim;
	AudioInput in;
//	AudioOutput out1;
//	AudioOutput out2;
//	AudioOutput out3;
	AudioOutput[] out = new AudioOutput[30];
	AudioPlayer song;
	FFT fftLin;
	FFT fftLinV;
	FFT fftLog;
//	Oscil wave1;
//	Oscil wave2;
//	Oscil wave3;
	Oscil[] wave = new Oscil[30];

	Body(PApplet p, Serial leftport, Serial rightport, Minim m, AudioPlayer s) {
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

		minim = m;
		song = s;
		for(int i=0;i<30;i++) {
			out[i] = minim.getLineOut();
			wave[i] = new Oscil(440, 0.5f, Waves.SINE);
			wave[i].patch(out[i]);
		}

		in = minim.getLineIn(Minim.STEREO, 2048);
		in.enableMonitoring();
		
//		song.loop();
		fftLin = new FFT(song.bufferSize(), 88200);
		fftLinV = new FFT(in.bufferSize(), 88200);
		fftLin.linAverages(30);
		fftLog = new FFT(song.bufferSize(), song.sampleRate());
		fftLog.logAverages(22, 12);
		proc.rectMode(proc.CORNERS);
		proc.strokeCap(proc.ROUND);
		proc.strokeJoin(proc.ROUND);
		proc.ellipseMode(proc.RADIUS);

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
		for (int i = 0; i < 8; i++) {
			proc.pushMatrix();
			proc.translate(0, (1 + i) * proc.height / 9, 0);
//			proc.text(side.yaw[i], 50, 50);
//			proc.text(side.pitch[i], 50, 100);
//			proc.text(-side.roll[i], 50, 150);
			proc.rotateX(-PI / 2);
			proc.strokeWeight(.5f);
//			xyzlines();
			proc.rotateZ(side.yaw[i] * PI / 180);
			proc.rotateX(side.pitch[i] * PI / 180);
			proc.rotateY(-side.roll[i] * PI / 180);

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
		left.plot9Lines(0, 1);
//		left.plot9Points(0, 0);
//		left.plot9Points(0, 1);
//		left.plot9Points(0, 2);
//		left.plot6Points(0, 0);
//		left.plot6Points(0, 1);
//		left.plot6Points(0, 2);

	}

	float xd = 0;
	float yd = 0;
	float zd = 0;

	float mxd = 0;
	float myd = 0;
	float mzd = 0;
	float hdis = 0;

	float xzd = 0;
	float yrot = 0;
	float zrot = 0;

	float yrot2 = 0;
	float zrot2 = 0;
	int sidecheck = 0;

	public void drawBody() {
		proc.pushMatrix();
		plots();
		proc.pushMatrix();
		proc.translate(1 * proc.width / 7, 0, 0);
		testimus(left);
		proc.popMatrix();

		proc.pushMatrix();
		proc.translate(6 * proc.width / 7, 0, 0);
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
		xd = (handpoint[1][curLine][0] - handpoint[0][curLine][0]);
		yd = (handpoint[1][curLine][1] - handpoint[0][curLine][1]);
		zd = (handpoint[1][curLine][2] - handpoint[0][curLine][2]);

		mxd = (handpoint[1][curLine][0] + handpoint[0][curLine][0]) / 2;
		myd = (handpoint[1][curLine][1] + handpoint[0][curLine][1]) / 2;
		mzd = (handpoint[1][curLine][2] + handpoint[0][curLine][2]) / 2;
		hdis = PApplet.dist(handpoint[0][curLine][0], handpoint[0][curLine][1], handpoint[0][curLine][2],
				handpoint[1][curLine][0], handpoint[1][curLine][1], handpoint[1][curLine][2]);
		hdishist[curLine] = hdis;
		xzd = PApplet.sqrt((xd * xd + zd * zd));
//		yrot = -PApplet.asin(zd / xzd);
		yrot = -PApplet.atan2(zd, xd);
		zrot = PApplet.asin(yd / hdis);
		yrots[curLine] = yrot;
		zrots[curLine] = zrot;
		updatefft();
		armlines();
		proc.text(left.switchbinary, 50, 50);
		proc.text(right.switchbinary, 50, 100);
		proc.text(left.normpress, 50, 150);
		proc.text(right.normpress, 50, 200);
		if (left.switchbinary > 0) {
			songwave(50);
		}
		if (right.switchbinary > 0) {
			voicewave(50);
		}
		float bufsize = in.bufferSize();
		proc.translate(0, 500, 0);
		for (int i = 0; i < bufsize - 1; i++) {
			proc.line((i * (hdis / bufsize)), in.mix.get(i) * 100, ((i + 1) * (hdis / bufsize)),
					in.mix.get(i + 1) * 100);
		}
		updatewaves();
//		soundwave(50);
		showfft();
//		ffthist();
		curLine++;
		if (curLine == maxLines) {
			curLine = 0;
		}
		proc.popMatrix();
	}

	float rotz = 0;
	float radius = 200;
	float bandnum = 200;
	float maxfsong = 0;
	float maxfvoice = 0;
	float[] fhist = new float[(int) (bandnum)];
	int offset = 0;
	int maxisong = 0;
	int maxivoice = 0;
	float[][] sfreqhist = new float[maxLines][(int) bandnum];
	float[][] vfreqhist = new float[maxLines][(int) bandnum];
	float[] yrots = new float[maxLines];
	float[] zrots = new float[maxLines];
	float[] hdishist = new float[maxLines];

	float beatsong = 0f;
	float beatvoice = 0f;

	float amp = 0f;
	float freq = 0f;

	public void updatewaves() {
//		amp = proc.map(left.normpress, 0, 1, 0, 1);
		for(int i=0;i<5;i++) {
			wave[i*3].setFrequency(proc.map(Math.abs(left.imu[i][3]), 0, 2000, 0, left.imu[i][0]*1000));
			wave[i*3+1].setFrequency(proc.map(Math.abs(left.imu[i][4]), 0, 2000, 0, left.imu[i][1]*1000));
			wave[i*3+2].setFrequency(proc.map(Math.abs(left.imu[i][5]), 0, 2000, 0, left.imu[i][2]*1000));
		}
		for(int i=0;i<5;i++) {
			wave[i*3+15].setFrequency(proc.map(Math.abs(right.imu[i][3]), 0, 2000, 0, right.imu[i][0]*1000));
			wave[i*3+16].setFrequency(proc.map(Math.abs(right.imu[i][4]), 0, 2000, 0, right.imu[i][1]*1000));
			wave[i*3+17].setFrequency(proc.map(Math.abs(right.imu[i][5]), 0, 2000, 0, right.imu[i][2]*1000));
		}
	}

	public void updatefft() {
		fftLin.forward(song.mix);
		fftLinV.forward(in.mix);
		for (int i = offset; i < bandnum; i++) {
			if (fftLin.getBand(i) > maxfsong) {
				maxfsong = fftLin.getBand(i);
				maxisong = i;
			}
		}
		beatsong = fftLin.getBand(maxisong);

		for (int i = offset; i < bandnum; i++) {
			if (fftLinV.getBand(i) > maxfvoice) {
				maxfvoice = fftLinV.getBand(i);
				maxivoice = i;
			}
		}
		beatsong = fftLin.getBand(maxivoice);
	}

	public void showfft() {
		proc.pushMatrix();
		proc.strokeWeight(2);
		proc.stroke(0, 255, 0);
		proc.translate(handpoint[0][curLine][0], handpoint[0][curLine][1], handpoint[0][curLine][2]);
		proc.rotateY(yrot);
		proc.rotateZ(zrot);
		for (int i = offset; i < bandnum; i++) {
			// song
			proc.stroke(255, 0, 0);
			proc.line(i * (hdis / bandnum), 0, 0, i * (hdis / bandnum), -fftLin.getBand(i), 0);
			// voice
			proc.stroke(255, 0, 0);
			proc.line(i * (hdis / bandnum), 0, 0, i * (hdis / bandnum), -fftLinV.getBand(i), 0);
		}
		proc.popMatrix();
	}

	public void fftsongring(int r) {
		int rad = r;
		for (int i = 0; i < bandnum; i++) {
//			proc.pushMatrix();
			proc.rotateY(i / (bandnum - offset) * 2 * PI);
//			proc.translate(0,-rad, 0);
			proc.stroke(255);
			proc.strokeWeight(1 + beatsong / 100);
			proc.line(-rad - beatsong / 4, 0, 0, -rad - fftLin.getBand(i) - beatsong / 4, 0, 0);
//			proc.popMatrix();
		}
	}

	public void fftvoicering(int r) {
		int rad = r;
		for (int i = 0; i < bandnum; i++) {
//			proc.pushMatrix();
			proc.rotateY(i / (bandnum - offset) * 2 * PI);
//			proc.translate(0,-rad, 0);
			proc.stroke(255, 255, 0);
			proc.strokeWeight(1 + beatvoice / 100);
			proc.line(-rad - beatvoice / 4, 0, 0, -rad - fftLinV.getBand(i) - beatvoice / 4, 0, 0);
//			proc.popMatrix();
		}
	}

	public void ffthist() {
		proc.strokeWeight(1);
		for (int i = 0; i < bandnum; i++) {
			sfreqhist[curLine][i] = fftLin.getBand(i);
//			vfreqhist[curLine][i] = fftLinV.getBand(i);
		}
		for (int i = maxLines - 1; i > curLine; i--) {
			proc.pushMatrix();
			proc.translate(handpoint[0][i][0], handpoint[0][i][1], handpoint[0][i][2]);
			proc.rotateY(yrots[i]);
			proc.rotateZ(zrots[i]);
			for (int j = 0; j < bandnum; j++) {
				// song
				proc.stroke(255, 0, 0);
				proc.line(j * (hdishist[i] / bandnum), 0, 0, j * (hdishist[i] / bandnum), -sfreqhist[i][j], 0);
//				// voice
//				proc.stroke(0, 255, 0);
//				proc.line(j * (hdishist[i] / bandnum), 0, 0, j * (hdishist[i] / bandnum), -vfreqhist[i][j], 0);
			}
			proc.popMatrix();
		}
		for (int i = curLine; i >= 0; i--) {
			proc.pushMatrix();
			proc.translate(handpoint[0][i][0], handpoint[0][i][1], handpoint[0][i][2]);
			proc.rotateY(yrots[i]);
			proc.rotateZ(zrots[i]);
			for (int j = 0; j < bandnum; j++) {
				// song
				proc.stroke(255, 0, 0);
				proc.line(j * (hdishist[i] / bandnum), 0, 0, j * (hdishist[i] / bandnum), -sfreqhist[i][j], 0);
//				// voice
//				proc.stroke(0, 255, 0);
//				proc.line(j * (hdishist[i] / bandnum), 0, 0, j * (hdishist[i] / bandnum), -vfreqhist[i][j], 0);
			}
			proc.popMatrix();
		}

	}

	public void songwave(float a) {
		float amp = a;
		proc.pushMatrix();
		proc.strokeWeight(.5f);
		proc.translate(handpoint[0][curLine][0], handpoint[0][curLine][1], handpoint[0][curLine][2]);
		proc.stroke(0, 0, 255);
		proc.rotateY(yrot);
		proc.rotateZ(zrot);
		float bufsize = song.bufferSize();
		for (int i = 0; i < bufsize - 1; i++) {
			proc.line((i * (hdis / bufsize)), song.mix.get(i) * 100, ((i + 1) * (hdis / bufsize)),
					song.mix.get(i + 1) * 100);

//			proc.line((i * (hdis / bufsize)), song.mix.get(i) * 100 + proc.cos(i*(PI*2/bufsize))*a-a, ((i + 1) * (hdis / bufsize)), song.mix.get(i + 1) * 100 + proc.cos((i+1)*(proc.PI*2/bufsize))*a-a);
//			
//			proc.line((i * (hdis / bufsize)), song.mix.get(i) * 100 - proc.cos(i*(PI*2/bufsize))*a+a, ((i + 1) * (hdis / bufsize)), song.mix.get(i + 1) * 100 - proc.cos((i+1)*(proc.PI*2/bufsize))*a+a);
		}
		proc.popMatrix();
	}

	public void voicewave(float a) {
		float amp = a;
		proc.pushMatrix();
		proc.strokeWeight(.5f);
		proc.translate(handpoint[0][curLine][0], handpoint[0][curLine][1], handpoint[0][curLine][2]);
		proc.stroke(255, 0, 0);
		proc.rotateY(yrot);
		proc.rotateZ(zrot);
		float bufsize = in.bufferSize();
		for (int i = 0; i < bufsize - 1; i++) {
			proc.line((i * (hdis / bufsize)), in.mix.get(i) * 100, ((i + 1) * (hdis / bufsize)),
					in.mix.get(i + 1) * 100);

//			proc.line((i * (hdis / bufsize)), song.mix.get(i) * 100 + proc.cos(i*(PI*2/bufsize))*a-a, ((i + 1) * (hdis / bufsize)), song.mix.get(i + 1) * 100 + proc.cos((i+1)*(proc.PI*2/bufsize))*a-a);
//			
//			proc.line((i * (hdis / bufsize)), song.mix.get(i) * 100 - proc.cos(i*(PI*2/bufsize))*a+a, ((i + 1) * (hdis / bufsize)), song.mix.get(i + 1) * 100 - proc.cos((i+1)*(proc.PI*2/bufsize))*a+a);
		}
		proc.popMatrix();
	}

	public void armlines() {
		proc.pushMatrix();
		proc.translate(mxd, myd, mzd);
		proc.noFill();
		proc.box(xd, yd, zd);
		proc.popMatrix();

		proc.pushMatrix();
		proc.stroke(0, 255, 0);
//		proc.stroke(0, 0, 255);

		for (int i = maxLines - 1; i > curLine; i--) {
			proc.line(handpoint[0][i][0], handpoint[0][i][1], handpoint[0][i][2], handpoint[1][i][0],
					handpoint[1][i][1], handpoint[1][i][2]);
		}
		for (int i = curLine; i >= 0; i--) {
			proc.line(handpoint[0][i][0], handpoint[0][i][1], handpoint[0][i][2], handpoint[1][i][0],
					handpoint[1][i][1], handpoint[1][i][2]);
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
//	int drawbool = 1;

	public void arm(Side side, int direction) {
		int d = direction;
		int ulen = 400;
		int llen = 300;
		proc.pushMatrix();
		proc.strokeWeight(1);
		proc.translate(d * 100, 0, -200);
		if (drawbool == 1) {
			proc.fill(255);
			proc.stroke(0);
			proc.sphere(30);
		}
		proc.rotateZ(side.yaw[0] * PI / 180);
		proc.rotateX(side.pitch[0] * PI / 180);
		proc.rotateY(-side.roll[0] * PI / 180);
		if (xyz == 1) {
			xyzlines();
		}
		proc.translate(d * 0, ulen / 2, 0);
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
		if (xyz == 1) {
			xyzlines();
		}
		proc.translate(0, llen / 2, 0);
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
		int len = 200;
		proc.rotateY(side.roll[1] * PI / 180);
		proc.rotateX(-side.pitch[1] * PI / 180);
		proc.rotateZ(-side.yaw[1] * PI / 180);

		proc.rotateZ(side.yaw[2] * PI / 180);
		proc.rotateX(side.pitch[2] * PI / 180);
		proc.rotateY(-side.roll[2] * PI / 180);
		if (xyz == 1) {
			xyzlines();
		}
		proc.translate(0, len / 2, 0);
		fftvoicering(150);
		fftsongring(40);
		int k = d;
		if (k == -1) {
			k = 0;
		}
		handpoint[k][curLine][0] = proc.modelX(0, 0, 0);
		handpoint[k][curLine][1] = proc.modelY(0, 0, 0);
		handpoint[k][curLine][2] = proc.modelZ(0, 0, 0);
		handpoint[k][curLine][3] = (float) side.normpress;

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
		int len = 100;
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
