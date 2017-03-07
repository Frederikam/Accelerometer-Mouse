#include <RedBot.h>
#include <RedBotSoftwareSerial.h>

RedBotMotors motors;

RedBotAccel accelerometer;

void setup(void)
{
  Serial.begin(9600);
}

void loop(void)
{
  accelerometer.read();
  
  // short delay in between readings/
  delay(10);

  //Serial.print(255, BIN);

  Serial.print(accelerometer.x, DEC);
  Serial.print(" ");
  Serial.print(accelerometer.z, DEC);
  Serial.print("\n");

  /*
  Serial.print(accelerometer.angleYZ, DEC);
  Serial.print(" ");
  Serial.print(accelerometer.angleXZ, DEC);
  Serial.print("\n");
  */
}

