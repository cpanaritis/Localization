/*
 * Navigation.java
 */
package ca.mcgill.ecse211.localizationlab;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * @author Christos Panaritis Kevin Chuong
 * Navigation class
 *
 */
public class Navigation {
	
  private static final int FORWARD_SPEED = 50;
  private static final int ROTATE_SPEED = 50;
  public EV3LargeRegulatedMotor leftMotor;
  public EV3LargeRegulatedMotor rightMotor;
  double amountTurned;
  private double radius;
  private double width;
  private Odometer odometer;
  public boolean navigating; 
  public boolean active = false;  // Checks if avoidance should run or not (bang bang)
  

  /**
   * constructor
 * @param leftMotor
 * @param rightMotor
 * @param leftRadius
 * @param rightRadius
 * @param width
 * @param odometer
 */
public Navigation (EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
	      double leftRadius, double rightRadius, double width, Odometer odometer) {
	  this.leftMotor = leftMotor;
	  this.rightMotor = rightMotor;
	  this.radius = rightRadius;
	  this.width = width;
	  this.odometer = odometer;
  }
  

void travelTo(double x, double y) {

	  navigating =true;
	  double deltaY = y - odometer.getY();
	  double deltaX = x - odometer.getX();
	  
		  double thetaD = Math.toDegrees(Math.atan2(deltaX,deltaY));
		  double thetaTurn = thetaD - odometer.getTheta();
		  if(thetaTurn < -180.0) {
			  turnTo(360.0 + thetaTurn);
			  
		  }
		  else if(thetaTurn > 180.0) {
			  turnTo(thetaTurn - 360.0); 
		  }
		  else {
			  turnTo(thetaTurn);
		  }
	  while(leftMotor.isMoving()&&rightMotor.isMoving()) {
	  }
	  leftMotor.setSpeed(FORWARD_SPEED);
      rightMotor.setSpeed(FORWARD_SPEED);
      double distance = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
      leftMotor.rotate(convertDistance(radius, distance), true);
      rightMotor.rotate(convertDistance(radius, distance), false);
     
  }
  
  /**
 * @param theta
 * finds what the robot should rotate at.
 */

//DOES NOT WORK IN THREAD --> FIGURE THAT OUT
void turnTo(double theta) {
	
	leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    leftMotor.rotate(convertAngle(radius, width, theta), true);
  	rightMotor.rotate(-convertAngle(radius, width, theta), true); 
  }
  
  /**
 * @return the navigating boolean 
 */
boolean isNavigating() {
	  return this.navigating;
  }
  
  /**
 * @return the boolean
 */
public boolean getStatus() {
	  return this.active;
  }
  /**
 * sets the boolean to true
 */
public void activate() {
	  this.active = true;
  }
 
/**
 * sets the boolean to false
 */
public void deactivate() {
	  this.active = false;
  }
  
  /**
 * @param radius
 * @param distance
 * @return
 */
private static int convertDistance(double radius, double distance) {
    return (int) ((180.0 * distance) / (Math.PI * radius));
  }

  /**
 * @param radius
 * @param width
 * @param angle
 * @return
 */
private static int convertAngle(double radius, double width, double angle) {
    return convertDistance(radius, Math.PI * width * angle / 360.0);
  }

}
