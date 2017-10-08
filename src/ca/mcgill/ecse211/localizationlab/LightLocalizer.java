package ca.mcgill.ecse211.localizationlab;

import ca.mcgill.ecse211.localizationlab.UltrasonicLocalizer.LocalizationState;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class LightLocalizer extends Thread {
	
	private Odometer odometer;
	private SampleProvider colorSample;
	private float [] lightData;
	private Navigation navigation;
	private float scaledColor;
	private double [] collectedData = {0,0,0,0,0,0,0};
	private int i = 0;
	private double thetaX, thetaY;
	private double tmpMax;
	private int startArray;
	
	//Constant(s)
	private static final long CORRECTION_PERIOD = 10;
	private static final double sensorToTrack = 14.2;
	
	
	public LightLocalizer (Odometer odometer, SampleProvider colorSample, float [] lightData, Navigation navigation) {
		this.odometer = odometer;
		this.colorSample = colorSample;
		this.lightData = lightData;
		this.navigation = navigation;
	}
	
	public void run() {
		long correctionStart, correctionEnd;
		while(true) {
			correctionStart = System.currentTimeMillis();
			colorSample.fetchSample(lightData, 0); // Get data from color sensor
			
			scaledColor = lightData[0]*1000; 
		    // Collect data during the ultrasonic localization is running
		    if(scaledColor < 250) {
		    		Sound.beep();
		    		//implement collecting data here
		    		collectedData[i] = odometer.getTheta();
		    		i++;
		    }
			
			// this ensure the lightLocalizer occurs only once every period
		    correctionEnd = System.currentTimeMillis();
		    if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
		      try {
		    	  	Thread.sleep(CORRECTION_PERIOD - (correctionEnd - correctionStart));
		      } catch (InterruptedException e) {
		          // there is nothing to be done here because it is not
		          // expected that the odometry correction will be
		          // interrupted by another thread
		      }
		    }	
		}
	}
	
	void startLightLocalization() {
		tmpMax = Math.abs(collectedData[0] - collectedData[1]);
		if(UltrasonicLocalizer.state == LocalizationState.RISING_EDGE) {	
			startArray = 0;
			for(int j=2; j<collectedData.length; j++) {
				if(tmpMax< Math.abs(collectedData[j-1]-collectedData[j]) && collectedData[j] != 0) {
					tmpMax = Math.abs(collectedData[j-1]-collectedData[j]);
					startArray = j;
				}
			}
			
			thetaX = collectedData[startArray+1]-collectedData[startArray+3];
			thetaY = collectedData[startArray]-collectedData[startArray+2];
			
			odometer.setY(-sensorToTrack*Math.cos(Math.toRadians(thetaY/2)));
			odometer.setX(-sensorToTrack*Math.cos(Math.toRadians(thetaX/2)));
			System.out.println(odometer.getX());
			System.out.println(odometer.getY());

			navigation.travelTo(0,0);
			
		}
		else {
			
			odometer.setY(-sensorToTrack*Math.cos(Math.toRadians(thetaY/2)));
			odometer.setX(-sensorToTrack*Math.cos(Math.toRadians(thetaX/2)));
			
			navigation.travelTo(0,0);
		}
	}
}
