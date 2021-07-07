using namespace std;
#include <Wire.h>                 // Must include Wire library for I2C
#include "max86150.h"
#include <arduino.h>                 // Must include Wire library for I2C


MAX86150 max86150Sensor;         // instance of MAX86150 sensor



void setup() {
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

  //done
}

unsigned long  sampleCount = 0;
unsigned long initTime = millis();
void loop() {
  //   if (accel.available()){
  //     Serial.println(accel.getZ());
  //     max86150Sensor.getIR();
  //     max86150Sensor.getECG();
  //     sampleCount++;
  //     if (sampleCount == 1000){
  //       Serial.print(sampleCount); Serial.print("\t"); Serial.println(millis() - initTime);
  //     }

  Serial.print(max86150Sensor.getECG()); Serial.print("\t"); Serial.println(max86150Sensor.getIR());
  //   }
}
