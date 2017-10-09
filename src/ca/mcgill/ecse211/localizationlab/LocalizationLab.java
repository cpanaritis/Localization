// Lab2.java

package ca.mcgill.ecse211.localizationlab;

import ca.mcgill.ecse211.localizationlab.UltrasonicLocalizer.LocalizationState;
import ca.mcgill.ecse211.localizationlab.Navigation;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

/**
 * @author Christos Panaritis and Kevin Chuong
 *
 */
public class LocalizationLab {

  static final EV3LargeRegulatedMotor leftMotor =
      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
  
  static final EV3LargeRegulatedMotor rightMotor =
      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
  private static final Port usPort = LocalEV3.get().getPort("S4");
  private static final EV3ColorSensor lightSensor = new EV3ColorSensor(LocalEV3.get().getPort("S3"));
  public static final EV3LargeRegulatedMotor sensorMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

  public static final double WHEEL_RADIUS = 2.15;
  public static final double TRACK = 12.16;
  public static final double GRID_LENGTH = 30.48;

/**
 * @param args
 */
public static void main(String[] args) {
    
    // Setup Ultrasonic sensor to obtain information on distance.
    SensorModes usSensor = new EV3UltrasonicSensor(usPort);
    SampleProvider usDistance = usSensor.getMode("Distance");
    float usData[] = new float[usDistance.sampleSize()];	// Contains distance values
    //
    Odometer odometer = new Odometer(leftMotor, rightMotor);
    final TextLCD t = LocalEV3.get().getTextLCD();
    LCDDisplay lcdDisplay = new LCDDisplay(odometer, t, usSensor, usData);
    Navigation navigation = new Navigation(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK, odometer);
    // Setup Light sensor to obtain data
    SampleProvider colorSample = lightSensor.getMode("Red");
    float [] lightData = new float[colorSample.sampleSize()];
    LightLocalizer lightLocalizer = new LightLocalizer(odometer, colorSample, lightData, navigation);
    
    //Display
    int buttonChoice;
    do{
    	// clear the display
    	t.clear();

    	// ask the user to start localizing
    	 t.drawString("< Left | Right >", 0, 0);
     t.drawString("       |        ", 0, 1);
     t.drawString("Falling| Rising ", 0, 2);
     t.drawString(" Edge  | Edge   ", 0, 3);
     t.drawString("       |        ", 0, 4);
     
    	buttonChoice = Button.waitForAnyPress();
    	} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);

    if (buttonChoice == Button.ID_LEFT) {
    		UltrasonicLocalizer localizer = new UltrasonicLocalizer(odometer, LocalizationState.FALLING_EDGE, usSensor, usData, navigation);
    		odometer.start();
    		lcdDisplay.start();
    		localizer.localize();
    } 
    else {
    		UltrasonicLocalizer localizer = new UltrasonicLocalizer(odometer, LocalizationState.RISING_EDGE, usSensor, usData, navigation);
    		odometer.start();
    		lcdDisplay.start();
    		localizer.localize();
    }
    while(Button.waitForAnyPress() != Button.ID_ENTER);
    lightLocalizer.run();
    	
    	while(Button.waitForAnyPress() != Button.ID_ESCAPE);
    	System.exit(0);

    
  }
}
