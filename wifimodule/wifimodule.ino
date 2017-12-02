//------------------------------------------
// This code is an adaptation of the WiFiWebServer
// example available from the ESP8266 library, with
// some minor modifications.
//------------------------------------------

#include <ESP8266WiFi.h>

const char ssid[] = "PI_Guest_C604_2.4G_altos";
const char pass[] = "1029384756";

const byte DOOR_SIGNAL_PIN = 2;

#define UNLOCK HIGH
#define LOCK LOW

//Port 80 is the default HTTP port.
//This means we can access it using
//a url like http://ip
WiFiServer server(80);

const int keys_capacity = 10;
String keys[keys_capacity];
int keys_size = 0;

void addKey(String key);
void removeKey(String key);
bool isKeyAuthorized(String key);

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
  digitalWrite(DOOR_SIGNAL_PIN, LOW);

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
  char mode = req.charAt(5);
  String key = req.substring(6, 10);
  Serial.print("Mode: ");
  Serial.println(mode);
  Serial.print("Key: ");
  Serial.println(key);

  //define whether it is requesting
  //to open or close the door. Undefined
  //requests will be equivalent to request
  //locking.

  if(mode == 'u') {
    Serial.println("unlock requested");
    if(isKeyAuthorized(key)) {
      Serial.println("key authorized");
      digitalWrite(DOOR_SIGNAL_PIN, HIGH);
      delay(50);
      digitalWrite(DOOR_SIGNAL_PIN, LOW);
    }
  }
  else if(mode == 's') {
    Serial.println("save requested");
    addKey(key);
  }
  else if(mode == 'r') {
    Serial.println("remove requested");
    removeKey(key);
  }

  //Send message to client, then disconnect
  client.flush();
  client.print("OK");
  client.stop();
}

void addKey(String key) {
  if(keys_size < keys_capacity) {
    keys[keys_size] = key;
    keys_size++;
  }
}
  
void removeKey(String key) {
  for(int i = 0; i < keys_size; i++) {
    if(keys[i].equals(key)) {
      keys[i] = keys[keys_size - 1];
      keys_size--;
    }
  }
}

bool isKeyAuthorized(String key) {
  for(int i = 0; i < keys_size; i++) {
    if(keys[i].equals(key)) {
      return true;
    }
  }
  return false;
}

