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
	private double [] collectedData = new double [4];
	private int i = 0;
	private double thetaX, thetaY;
	private double deltaTheta; 

	//Constant(s)
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
		navigation.rightMotor.setSpeed(50);
		navigation.leftMotor.setSpeed(50);
		//navigation.rightMotor.backward();
		//navigation.leftMotor.forward();
		navigation.turnTo(360);
		
		while(navigation.leftMotor.isMoving()&&navigation.rightMotor.isMoving()) {
			colorSample.fetchSample(lightData, 0); // Get data from color sensor
		
			scaledColor = lightData[0]*1000; 
			// Collect data during the ultrasonic localization is running
			if(scaledColor < 250) {
	    			//implement collecting data here
	    			Sound.beep();
	    			collectedData[i] = odometer.getTheta();
	    			i++;
			}
		}
		navigation.rightMotor.stop();
		navigation.leftMotor.stop();
		startLightLocalization();
	}
	
	/**
	 * localize and go to origin
	 */
	void startLightLocalization() {
			//Arc angle from the first time you encounter and axis till the end. 
			thetaX = collectedData[3]-collectedData[1];
			thetaY = collectedData[2]-collectedData[0];
			//Set the new/actual position of the robot.
			odometer.setY(-sensorToTrack*Math.cos(Math.toRadians(thetaY/2)));
			odometer.setX(-sensorToTrack*Math.cos(Math.toRadians(thetaX/2)));
			//Correct angle 
			deltaTheta = 90-(collectedData[3]-180)+thetaX/2;
			odometer.setTheta(deltaTheta);

			//Navigate to the origin. 
			navigation.travelTo(0,0);
			navigation.turnTo(-(odometer.getTheta()));
	}
}
