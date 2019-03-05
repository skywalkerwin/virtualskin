package virtualskin;
import processing.core.PApplet;
import processing.serial.Serial;

public class Side {
	PApplet proc;
	double[][] imu = new double[10][6];
	double[][] handimu = new double[7][6];
	double[][] armimu = new double[7][6];
	//double[][] armimuMAG = new double[3][9];
	//double [][] imu9 = new double[3][9];
	double[] magno = new double[9];
	int histlength = 120;
	int nframes = 3;
	double[][][] nimu = new double[12][6][nframes];
	double[][] nmag = new double[9][nframes];
	int thumbPressure = 0;
	int[] switches = { 0, 0, 0, 0 };
	int minpress = 1000;
	int maxpress = 0;
	int rangepress = 0;
	double normpress = 0;
	double[] presst = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	double totalPress = 0;
	int switchbinary = 0;
	
	int[] magadj = { 176, 176, 165 };
	int ttime = 0;
	String vals = " ";
	boolean firstContact = false;
	byte[] inBuffer = new byte[134];
	int negcheck = 32767;
	int clockcount = 0;
	int off = 0;
	Serial port;
	double ascale;
	double gscale;
	
	
	Side(PApplet p, Serial sidePort, double ascl, double gscl){
		proc = p;
		port = sidePort;
		ascale = ascl;
		gscale = gscl;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 6; j++) {
				imu[i][j] = 0;
			}
		}
		for (int i = 0; i < 9; i++) {
			magno[i] = 0;
		}		
	}
	public void serialEvent() {
		if (firstContact == false) {
			vals = port.readStringUntil(10);
			proc.println(vals);
			if (vals != null) {
				port.clear(); // clear the serial port buffer
				firstContact = true; // you've had first contact from the microcontroller
				port.write("A"); // ask for more
			}
		} else {
			if (port.available() > 133) {
				inBuffer = port.readBytes(134);
				port.write("A");
				if (inBuffer != null) {
					// accel
					magno[0] = ((inBuffer[0] << 8 | inBuffer[1] & 0xff));
					magno[1] = ((inBuffer[2] << 8 | inBuffer[3] & 0xff));
					magno[2] = ((inBuffer[4] << 8 | inBuffer[5] & 0xff));
					// gyro
					magno[3] = ((inBuffer[6] << 8 | inBuffer[7] & 0xff));
					magno[4] = ((inBuffer[8] << 8 | inBuffer[9] & 0xff));
					magno[5] = ((inBuffer[10] << 8 | inBuffer[11] & 0xff));
					// mag
					magno[6] = ((inBuffer[12] << 8 | inBuffer[13] & 0xff));
					magno[7] = ((inBuffer[14] << 8 | inBuffer[15] & 0xff));
					magno[8] = ((inBuffer[16] << 8 | inBuffer[17] & 0xff));
					for (int i = 0; i < 9; i++) {
						if (magno[i] > negcheck) {
							magno[i] = -1 * magno[i];
						}
						if (i < 3) {
							magno[i] = magno[i] * ascale;
						}
						if (i > 2 && i < 6) {
							magno[i] = magno[i] * gscale;
						}
						if (i > 5) {
							// magno[i]=(magno[i]*(((magadj[(-6+i)]-128) *.5)/128)+1);
							magno[i] = magno[i];
						}
					}
					for (int i = 0; i < 9; i++) {
//						mhista[i][hcount] = magno[i];
						nmag[i][off] = magno[i];
					}
					for (int i = 0; i < 6; i++) {
						imu[9][i] = magno[i];
					}
					for (int i = 0; i < 6; i++) {
//						imuhista[5][i][hcount] = handimu[5][i];
//						imuhista[6][i][hcount] += handimu[5][i];
//						handimu[6][i] += handimu[5][i];
						nimu[9][i][off] = imu[9][i];
//						nimu[6][i][off] += handimu[5][i];
					}
					for (int readnum = 0; readnum < 9; readnum++) {
						imu[readnum][0] = ((inBuffer[18 + (readnum * 12)] << 8)
								| (inBuffer[19 + (readnum * 12)] & 0xff));
						imu[readnum][1] = ((inBuffer[20 + (readnum * 12)] << 8)
								| (inBuffer[21 + (readnum * 12)] & 0xff));
						imu[readnum][2] = ((inBuffer[22 + (readnum * 12)] << 8)
								| (inBuffer[23 + (readnum * 12)] & 0xff));
						imu[readnum][3] = ((inBuffer[24 + (readnum * 12)] << 8)
								| (inBuffer[25 + (readnum * 12)] & 0xff));
						imu[readnum][4] = ((inBuffer[26 + (readnum * 12)] << 8)
								| (inBuffer[27 + (readnum * 12)] & 0xff));
						imu[readnum][5] = ((inBuffer[28 + (readnum * 12)] << 8)
								| (inBuffer[29 + (readnum * 12)] & 0xff));
						for (int i = 0; i < 3; i++) {
							if (imu[readnum][i] > negcheck) {
								imu[readnum][i] = (-(imu[readnum][i] - negcheck) * ascale);
							} else {
								imu[readnum][i] = imu[readnum][i] * ascale;
							}
						}
						for (int i = 3; i < 6; i++) {
							if (imu[readnum][i] > negcheck) {
								imu[readnum][i] = (-(imu[readnum][i] - negcheck) * gscale);
							} else {
								imu[readnum][i] = imu[readnum][i] * gscale;
							}
						}
						// APPEND TO HISTORIES
						for (int i = 0; i < 6; i++) {
							// imuhista[readnum][i][hcount]=imu[readnum][i];
							// imuhista[6][i][hcount]+=imu[readnum][i];
//							imu[6][i] += imu[readnum][i];
							nimu[readnum][i][off] = imu[readnum][i];
//							nimu[6][i][off] += imu[readnum][i];
						}
					}
					// for (int i=0; i<6; i++) {
					// imuhista[6][i][hcount]=imuhista[6][i][hcount]/6;
					// handimu[6][i]/=6;
					// nimu[6][i][off]/=6;
					// }
				}
				thumbPressure = ((inBuffer[126] << 8) | (inBuffer[127] & 0xff));
				if (thumbPressure > maxpress) {
					maxpress = thumbPressure;
				}
				if (thumbPressure < minpress) {
					minpress = thumbPressure;
				}
				rangepress = maxpress - minpress;
				normpress = (1.0 * thumbPressure - minpress) / rangepress;
				switches[0] = 1 - inBuffer[128];
				switches[1] = 1 - inBuffer[129];
				switches[2] = 1 - inBuffer[130];
				switches[3] = 1 - inBuffer[131];
				switchbinary = (switches[3] << 3 | switches[2] << 2 | switches[1] << 1 | switches[0]);
				ttime = ((inBuffer[132] << 8) | (inBuffer[133] & 0xff));
				off = off + 1;
				if (off == nframes) {
					off = 0;
				}
//				redraw();
			}
		}
	}
	
}
