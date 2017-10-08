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
	private double [] collectedData = new double[5];
	private int i = 0;
	
	//Constant(s)
	private static final long CORRECTION_PERIOD = 10;
	
	
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

	
			if(UltrasonicLocalizer.state == LocalizationState.FALLING_EDGE) {
		
			}
			else {
		
			}
			for(int x=0; x < collectedData.length; x++) {
				System.out.println(collectedData[x]); 
			}
			System.exit(0);
	}
	
	
}
