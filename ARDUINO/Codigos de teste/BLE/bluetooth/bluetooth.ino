#include <SoftwareSerial.h>

SoftwareSerial ble(10, 11); // RX, TX

void setup() {
  // Open serial port
  Serial.begin(9600);
  // begin bluetooth serial port communication
  ble.begin(9600);
}

// Now for the loop

void loop() {
  Serial.println("Sending Bluetooth Message...");
  ble.write("Testing...123");
  delay(500);
  
}
