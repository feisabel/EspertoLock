#include <SoftwareSerial.h>

#define TX_PIN 11
#define RX_PIN 10

SoftwareSerial bt(RX_PIN, TX_PIN);

void setup()
{
  Serial.begin(9600);
  bt.begin(38400);
}

void loop()
{
  if(bt.available())
    Serial.write( bt.read() );
}
