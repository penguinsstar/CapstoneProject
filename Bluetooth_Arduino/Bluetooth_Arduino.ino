// Arduino code to send messages from smartphone to Arduino
using namespace std;
#include <Wire.h>                 // Must include Wire library for I2C
#include "max86150.h"
#include <arduino.h>                 // Must include Wire library for I2C
#include <SoftwareSerial.h>

SoftwareSerial ble_device(3,4);
MAX86150 max86150Sensor;         // instance of MAX86150 sensor


void setup() {  
  ble_device.begin(19200);
  delay(100);

  // Name the bluetooth device and reset
  ble_cmd("AT+NAMEHarp"); // printout device name
  ble_cmd("AT+RESET"); // reset module
  delay(500);

  Serial.begin(19200);
  Wire.begin();
  // Initialize PPG/ECG sensor
  if (max86150Sensor.begin(Wire, I2C_SPEED_FAST) == false)
  {
    Serial.println("MAX86150 was not found. Please check wiring/power. ");
    while (1);
  }
  // sampling rate for ECG and PPG is 100Hz
  max86150Sensor.setup(); //Configure sensor. Use 6.4mA for LED drive
}

void loop() {
  //send biometric data
  ble_device.print(String(max86150Sensor.getECG()) + " " + String(max86150Sensor.getIR()));
  delay(10);
}

void ble_cmd(const char * command){
  Serial.print("Command send :");
  Serial.println(command);
  ble_device.println(command);
  //wait some time
  delay(100);
  
  char reply[100];
  int i = 0;
  while (ble_device.available()) {
    reply[i] = ble_device.read();
    i += 1;
  }
  //end the string
  reply[i] = '\0';
  Serial.print(reply);
  Serial.println("Reply end");
}
