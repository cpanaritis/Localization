package ca.mcgill.ecse211.localizationlab;

import ca.mcgill.ecse211.localizationlab.UltrasonicLocalizer.LocalizationState;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class LightLocalizer extends Thread {
	
	private Odometer odometer;
	private SampleProvider colorSample;
	private float [] lightData;
	private Navigation navigation;
	private float [] scaledColor = new float[2];
	private double [] collectedData = new double[6];
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
			
			//if(scaledColor[0] == 0) {
			//	scaledColor[0] = lightData[0]*1000;
			//}
			//else {
				scaledColor[1] = lightData[0]*1000; 
		    // Collect data during the ultrasonic localization is running
		    if(scaledColor[1] < 250) {
		    		Sound.beep();
		    		//implement collecting data here
		    		collectedData[i] = odometer.getTheta();
		    		i++;
		    }
		    		//scaledColor[0] = scaledColor[1];
			//}
		    
		    // After ultrasonic localization has ran perform the light sensor localization
		    while(!(UltrasonicLocalizer.active)){
		    		//implement travel to here
		    		for(int x=0; x < collectedData.length; x++) {
		    			System.out.println(collectedData[x]); 
		    		}
		    		System.exit(0);
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
	
	
	
	
}
