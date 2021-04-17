/*
  Library for the MMA8452Q
  By: Jim Lindblom and Andrea DeVore
  SparkFun Electronics

  Do you like this library? Help support SparkFun. Buy a board!
  https://www.sparkfun.com/products/14587

  This sketch uses the SparkFun_MMA8452Q library to initialize
  the accelerometer and stream raw x, y, z, acceleration
  values from it.

  Hardware hookup:
  Arduino --------------- MMA8452Q Breakout
    3.3V  ---------------     3.3V
    GND   ---------------     GND
  SDA (A4) --\/330 Ohm\/--    SDA
  SCL (A5) --\/330 Ohm\/--    SCL

  The MMA8452Q is a 3.3V max sensor, so you'll need to do some
  level-shifting between the Arduino and the breakout. Series
  resistors on the SDA and SCL lines should do the trick.

  License: This code is public domain, but if you see me
  (or any other SparkFun employee) at the local, and you've
  found our code helpful, please buy us a round (Beerware
  license).

  Distributed as is; no warrenty given.
*/
/*#include "KickFilters.h"*/
using namespace std;
#include <Wire.h>                 // Must include Wire library for I2C
#include "SparkFun_MMA8452Q.h"    // Click here to get the library: http://librarymanager/All#SparkFun_MMA8452Q
#include "max86150.h"
#include <arduino.h>                 // Must include Wire library for I2C


//// sample rate for MAX86150
//const uint8_t PPG_CONFIG_1 = 0x0E ;
//const uint8_t PPG_SAMPLE_RATE_MASK = 0b11000011 ;
//const uint8_t PPG_SAMPLE_RATE_200 = 0b00010100 ;
//const uint8_t ECG_CONFIG_1 = 0x3C;
//const uint8_t ECG_SAMPLE_RATE_MASK = 0b11111000 ;
//const uint8_t ECG_SAMPLE_RATE_200 = 0b00000011;


#define MMA8452_ADDRESS 0x1D  // 0x1D if SA0 is high, 0x1C if low

//Define a few of the registers that we will be accessing on the MMA8452
#define OUT_X_MSB 0x01
#define XYZ_DATA_CFG  0x0E
#define WHO_AM_I   0x0D
#define CTRL_REG1  0x2A
signed int x = 0;
signed int y = 0;

MMA8452Q accel;                  // create instance of the MMA8452 class
MAX86150 max86150Sensor;         // instance of MAX86150 sensor



void setup() {
  Serial.begin(9600);
  Wire.begin();
  
  // Initialize PPG/ECG sensor
    if (max86150Sensor.begin(Wire, I2C_SPEED_FAST) == false)
    {
        Serial.println("MAX86150 was not found. Please check wiring/power. ");
        while (1);
    }
    // sampling rate for ECG and PPG is 200Hz
    max86150Sensor.setup(); //Configure sensor. Use 6.4mA for LED drive
    
  // Initialize accelerometer
  if (accel.begin() == false) {
    Serial.println("Not Connected. Please check connections and read the hookup guide.");
    while (1);
  }

 //done
}

// Initialize the MMA8452 registers 
// See the many application notes for more info on setting all of these registers:
// http://www.freescale.com/webapp/sps/site/prod_summary.jsp?code=MMA8452Q
void initMMA8452()
{
  byte c = readRegister(WHO_AM_I);  // Read WHO_AM_I register
  if (c == 0x2A) // WHO_AM_I should always be 0x2A
  {  
    Serial.println("MMA8452Q is online...");
  }
  else
  {
    Serial.print("Could not connect to MMA8452Q: 0x");
    Serial.println(c, HEX);
    while(1) ; // Loop forever if communication doesn't happen
  }

 
}



void loop() {
  if (accel.available()) {      // Wait for new data from accelerometer
    MMA8452Standby();  // Must be in standby to change registers

  accel.setDataRate(ODR_200); // set sampling rate to 200
  writeRegister(0x0F, 0x30);// Turns on HPF bypassing and LP filtering 
  writeRegister(0x2B,0X02);// Turns on High-res mode
  writeRegister(0x0E, 0X11);// Turns on HPF_out to the output registers and sets full scale range to 4g
  MMA8452Active();  // Set to active to start reading

  Serial.print(max86150Sensor.getECG()); Serial.print("\t"); Serial.print(accel.getZ()); Serial.print("\t"); Serial.println(max86150Sensor.getIR());
  

    }
  }

//void setPpgSampleRate(uint8_t sampleRate)
//{
//  max86150Sensor.bitMask(PPG_CONFIG_1, PPG_SAMPLE_RATE_MASK, sampleRate);
//}
//
//void setEcgSampleRate(uint8_t sampleRate)
//{
//  max86150Sensor.bitMask(ECG_CONFIG_1, ECG_SAMPLE_RATE_MASK, sampleRate);
//}

// Sets the MMA8452 to standby mode. It must be in standby to change most register settings
void MMA8452Standby()
{
  byte c = readRegister(CTRL_REG1);
  writeRegister(CTRL_REG1, c & ~(0x01)); //Clear the active bit to go into standby
}

// Sets the MMA8452 to active mode. Needs to be in this mode to output data
void MMA8452Active()
{
  byte c = readRegister(CTRL_REG1);
  writeRegister(CTRL_REG1, c | 0x01); //Set the active bit to begin detection
}

// Read bytesToRead sequentially, starting at addressToRead into the dest byte array
void readRegisters(byte addressToRead, int bytesToRead, byte * dest)
{
  Wire.beginTransmission(MMA8452_ADDRESS);
  Wire.write(addressToRead);
  Wire.endTransmission(false); //endTransmission but keep the connection active

  Wire.requestFrom(MMA8452_ADDRESS, bytesToRead); //Ask for bytes, once done, bus is released by default

  while(Wire.available() < bytesToRead); //Hang out until we get the # of bytes we expect

  for(int x = 0 ; x < bytesToRead ; x++)
    dest[x] = Wire.read();    
}

// Read a single byte from addressToRead and return it as a byte
byte readRegister(byte addressToRead)
{
  Wire.beginTransmission(MMA8452_ADDRESS);
  Wire.write(addressToRead);
  Wire.endTransmission(false); //endTransmission but keep the connection active

  Wire.requestFrom(MMA8452_ADDRESS, 1); //Ask for 1 byte, once done, bus is released by default

  while(!Wire.available()) ; //Wait for the data to come back
  return Wire.read(); //Return this one byte
}

// Writes a single byte (dataToWrite) into addressToWrite
void writeRegister(byte addressToWrite, byte dataToWrite)
{
  Wire.beginTransmission(MMA8452_ADDRESS);
  Wire.write(addressToWrite);
  Wire.write(dataToWrite);
  Wire.endTransmission(); //Stop transmitting
}
