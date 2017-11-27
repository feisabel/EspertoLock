//------------------------------------------
// This code is an adaptation of the WiFiWebServer
// example available from the ESP8266 library, with
// some minor modifications.
//------------------------------------------

#include <ESP8266WiFi.h>

const char ssid[] = "GOUVEIA";
const char pass[] = "ginamoura3006";

const byte DOOR_SIGNAL_PIN = 2;

#define UNLOCK HIGH
#define LOCK LOW

//Port 80 is the default HTTP port.
//This means we can access it using
//a url like http://ip
WiFiServer server(80);

void setup() 
{
  //TODO: check whether we can use baud 9600,
  //like the rest of the code
  Serial.begin(115200);

  //Prepare output PIN: HIGH means
  //we want the door to unlock, LOW
  //means we want the door to lock.
  //We want it to be locked, initially
  pinMode(DOOR_SIGNAL_PIN, OUTPUT);
  digitalWrite(DOOR_SIGNAL_PIN, LOCK);

  //TODO: Wait for user to input network ID and password

  //Connect to local wireless network
  WiFi.begin(ssid, pass);

  Serial.print("Waiting for connection");
  while(WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.print("\nConnected at ");
  Serial.print(WiFi.localIP());

  //Launch server
  server.begin();
}

void loop() 
{
  //wait for client connection. 
  //We support only ONE connection at a time!
  WiFiClient client;
  while( !(client = server.available()) );
  Serial.println("A client!");

  //read first line of the request
  String req = client.readStringUntil('\r');
  Serial.print("Client requested: ");
  Serial.println(req);

  //define whether it is requesting
  //to open or close the door. Undefined
  //requests will be equivalent to request
  //locking.
  byte lock = LOCK;

  if(req.indexOf("/lock") != -1) lock = LOCK;
  else if(req.indexOf("/unlock") != -1) lock = UNLOCK;
 
  //write to Arduino board we want to lock/unlock door
  digitalWrite(DOOR_SIGNAL_PIN, lock);

  //Send message to client, then disconnect
  client.flush();
  client.print("OK");
  client.stop();
}
