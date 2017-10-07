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
	
  private static final int FORWARD_SPEED = 200;
  private static final int ROTATE_SPEED = 100;
  private static double[][] waypoints = new double[][] {
	  										{1, 0, 2, 2, 1 },  // Row 0 is x coordinates
	  										{1, 2, 2, 1, 0 } };// Row 1 is y coordinates
  public EV3LargeRegulatedMotor leftMotor;
  public EV3LargeRegulatedMotor rightMotor;
  private double radius;
  private double width;
  private Odometer odometer;
  public boolean navigating; 
  private int whichPoint = 0;
  private double lastTheta;
  private int distanceFromBlock;
  public boolean active = false;  // Checks if avoidance should run or not (bang bang)
  public static final int bandCenter = 8; // Offset from the wall (cm)
  private static final int bandWidth = 2; // Width of dead band (cm)

  private int filterControl;
  private static final int FILTER_OUT = 20;

  

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
	  
		  double thetaD = Math.atan2(deltaX,deltaY);
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
	  
	  leftMotor.setSpeed(FORWARD_SPEED);
      rightMotor.setSpeed(FORWARD_SPEED);
      double distance = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
      leftMotor.rotate(convertDistance(radius, distance), true);
      rightMotor.rotate(convertDistance(radius, distance), true);
     
  }
  
  /**
 * @param theta
 * finds what the robot should rotate at.
 */
void turnTo(double theta) {
	  navigating = true;
	  leftMotor.setSpeed(ROTATE_SPEED);
      rightMotor.setSpeed(ROTATE_SPEED);
      leftMotor.rotate(convertAngle(radius, width, theta), true);
    	  rightMotor.rotate(-convertAngle(radius, width,  theta), false);  
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
