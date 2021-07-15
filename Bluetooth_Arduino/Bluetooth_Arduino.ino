// Arduino code to send messages from smartphone to Arduino
#include <SoftwareSerial.h>

SoftwareSerial ble_device(3,4);


void setup() {  
  ble_device.begin(9600);
  delay(100);

  // Name the bluetooth device and reset
  ble_cmd("AT+NAMEHarp"); // printout device name
  ble_cmd("AT+RESET"); // reset module
  delay(500);
}

void loop() {
  // Test by sending a sample of biometric data
  ble_device.write("303 3210");
  delay(500);
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
