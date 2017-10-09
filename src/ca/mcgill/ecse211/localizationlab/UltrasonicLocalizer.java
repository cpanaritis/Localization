package ca.mcgill.ecse211.localizationlab;

import lejos.robotics.SampleProvider;



/**
 * @author Christos Panaritis and Kevin Chuong
 *
 */
public class UltrasonicLocalizer {
	
	public enum LocalizationState { FALLING_EDGE, RISING_EDGE };
	private SampleProvider usSensor;
	private Odometer odometer;
	private float[] usData;
	public static LocalizationState state;
	private Navigation navigation;
	
	//Constants 
	private final int THRESHOLD_WALL = 35;
	private final int NOISE_GAP = 1;
	private final int TURN_SPEED = 50;
	
	//Booleans
	private boolean isCompleted = false;
	public static boolean active = true;
	
	
	
	
	
	/**
	 * Constructor
	 * @param odometer
	 * @param state
	 * @param usSensor
	 * @param usData
	 * @param navigation
	 */
	public UltrasonicLocalizer (Odometer odometer, LocalizationState state, SampleProvider usSensor, float[] usData, Navigation navigation){
		this.odometer = odometer;
		this.state = state;
		this.usSensor = usSensor;
		this.usData = usData;
		this.navigation = navigation;
	}
	
	/**
	 * localizes the 0 degree
	 */
	public void localize(){
		
		 double firstAngle;
		 double lastAngle;
		 double deltaTheta;
		 double newTheta;
		
		
		if(state == LocalizationState.FALLING_EDGE){
			// Makes robot turn to its right until it sees wall (falling edge)
			turnToWall();	
			//Record first angle at stop
			firstAngle = odometer.getTheta();
			
			// Makes robot turn again until it sees a rising edge
			turnAwayFromWall();
			
			//Record second angle at stop
			lastAngle = odometer.getTheta();
			
			//Calculate the deltaTheta
			deltaTheta = calculateTheta(lastAngle, firstAngle);
			newTheta = odometer.getTheta() + deltaTheta;
			
			odometer.setPosition(new double[] {0.0, 0.0, newTheta}, new boolean[]{true,true,true});
			
			
			//Make the robot turn to the calculated 0
			navigation.turnTo(359 - newTheta);
			 
		}
		else {
			//Make robot turn until it does not see a wall (rising edge)
			turnAwayFromWall();
			//Record first angle at stop
			firstAngle = odometer.getTheta();
			
			//Makes the robot turn until it sees a wall (falling edge)
			turnToWall();
			//Record second angle stop 
			lastAngle = odometer.getTheta();
			
			//Calculate the deltaTheta
			deltaTheta = calculateTheta(firstAngle, lastAngle);
			newTheta = odometer.getTheta() + deltaTheta;
			
			odometer.setPosition(new double[] {0.0, 0.0, newTheta}, new boolean[]{true,true,true});
			//Make the robot turn to the calculated 0
			navigation.turnTo(359 - newTheta);
			
		}
		active = false;
	}
	
	/**
	 * Turns towards the wall. Falling edge.
	 */
	private void turnToWall(){
		while(!isCompleted){
			setSpeed(TURN_SPEED);
			LocalizationLab.leftMotor.forward();
			LocalizationLab.rightMotor.backward();
			
			//Checks if we reached a falling edge
			if(getDistanceValue() < THRESHOLD_WALL + NOISE_GAP){
				setSpeed(0);
				isCompleted = true;
			}
		}
		isCompleted = false;	
		
	}
	/**
	 * Turns away from wall. Rising edge.
	 */
	private void turnAwayFromWall(){
		while(!isCompleted){
			setSpeed(TURN_SPEED);
			LocalizationLab.leftMotor.forward();
			LocalizationLab.rightMotor.backward();
			
			if(getDistanceValue() > THRESHOLD_WALL + NOISE_GAP){
				setSpeed(0);
				isCompleted = true;
			}
			
		}
			isCompleted = false;
			
	} 
	
	/**
	 * @param firstAngle
	 * @param secondAngle
	 * @return
	 */
	private double calculateTheta(double firstAngle, double secondAngle){
		if(firstAngle < secondAngle){
			return 40 - (firstAngle + secondAngle) / 2;
		}
		else{
			return 220 - (firstAngle + secondAngle) / 2;
		}
	}
	/**
	 * @return
	 */
	public double getDistanceValue(){
		usSensor.fetchSample(usData, 0);
		  return usData[0]*100;
	}
	/**
	 * @param speed
	 */
	public void setSpeed(int speed){
		LocalizationLab.leftMotor.setSpeed(speed);
		LocalizationLab.rightMotor.setSpeed(speed);
	}
}
