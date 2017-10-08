package ca.mcgill.ecse211.localizationlab;

import ca.mcgill.ecse211.localizationlab.UltrasonicLocalizer.LocalizationState;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

/**
 * @author Christos Panaritis and Keving Chuong
 *
 */
public class LightLocalizer extends Thread {
	
	private Odometer odometer;
	private SampleProvider colorSample;
	private float [] lightData;
	private Navigation navigation;
	private float scaledColor;
	private double [] collectedData = {0,0,0,0,0,0,0};
	private int i = 0;
	private double thetaX, thetaY;
	private int startArray;
	private boolean endCounter;
	
	//Constant(s)
	private static final long CORRECTION_PERIOD = 10;
	private static final double sensorToTrack = 14.2;
	
	
	/**
	 * @param odometer
	 * @param colorSample
	 * @param lightData
	 * @param navigation
	 */
	public LightLocalizer (Odometer odometer, SampleProvider colorSample, float [] lightData, Navigation navigation) {
		this.odometer = odometer;
		this.colorSample = colorSample;
		this.lightData = lightData;
		this.navigation = navigation;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
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
	
	/**
	 * localize and go to origin
	 */
	void startLightLocalization() {
			getStart();
			goToOrigin();
	}
	
	/**
	 * @return the position in the array that are the y axis starting point.
	 */
	int getStart() {
		endCounter =true;
		if(UltrasonicLocalizer.state == LocalizationState.RISING_EDGE) {
			for(int i=0; i < collectedData.length; i++) {
				if(endCounter && (collectedData[i] == 0 || i == collectedData.length-1)) {
					startArray= i-4;
					endCounter= false;	
				}
			}
		}
		else {
			startArray = 0;
		}
		return startArray;
	}
	
	/**
	 * Goes to the origin 
	 */
	void goToOrigin() {
		//Arc angle from the first time you encounter and axis till the end. 
		thetaX = collectedData[startArray+3]-collectedData[startArray+1];
		thetaY = collectedData[startArray+2]-collectedData[startArray];
		//Set the new/actual position of the robot.
		odometer.setY(-sensorToTrack*Math.cos(Math.toRadians(thetaY/2)));
		odometer.setX(-sensorToTrack*Math.cos(Math.toRadians(thetaX/2)));

		//Navigate to the origin. 
		navigation.travelTo(0,0);
	}
}
