package ca.mcgill.ecse211.localizationlab;

import lejos.robotics.SampleProvider;



public class USLocalization {
	
	public enum LocalizationState { FALLING_EDGE, RISING_EDGE };
	private SampleProvider usSensor;
	private Odometer odometer;
	private float[] usData;
	private LocalizationState state;
	private Navigation navigation;
	
	//Constants 
	private final int THRESHOLD_WALL = 40;
	private final int NOISE_GAP = 1;
	private final int TURN_SPEED = 150;
	
	//Booleans
	private boolean isCompleted = false;
	
	
	
	
	
	public USLocalization (Odometer odometer, LocalizationState state, SampleProvider usSensor, float[] usData, Navigation navigation){
		this.odometer = odometer;
		this.state = state;
		this.usSensor = usSensor;
		this.usData = usData;
		this.navigation = navigation;
	}
	
	public void localize(){
		
		 double firstAngle;
		 double lastAngle;
		 double deltaTheta;
		 double newTheta;
		
		
		if(state == LocalizationState.FALLING_EDGE){
			// Makes robot turn to its right until it sees wall
			turnToWall();	
			
			//Record first angle at stop
			firstAngle = odometer.getTheta();
			// Makes robot turn again until it sees a rising edge
			turnAwayFromWall();
			
			//Record second angle at stop
			lastAngle = odometer.getTheta();
			
			//Calculate the deltaTheta
			deltaTheta = calculateTheta(firstAngle, lastAngle);
			newTheta = odometer.getTheta() + deltaTheta;
			
			odometer.setPosition(new double[] {0.0, 0.0, newTheta}, new boolean[]{true,true,true});
			
			navigation.turnTo(newTheta);
			 
		}
	}
	
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
	private double calculateTheta(double firstAngle, double secondAngle){
		if(firstAngle < secondAngle){
			return 40 - (firstAngle + secondAngle) / 2;
		}
		else{
			return 220 - (firstAngle + secondAngle) / 2;
		}
	}
	public double getDistanceValue(){
		usSensor.fetchSample(usData, 0);
		  return usData[0]*100;
	}
	public void setSpeed(int speed){
		LocalizationLab.leftMotor.setSpeed(speed);
		LocalizationLab.rightMotor.setSpeed(speed);
	}
}
