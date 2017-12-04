//------------------------------------------
// This code is an adaptation of the WiFiWebServer
// example available from the ESP8266 library, with
// some minor modifications.
//------------------------------------------

#include <ESP8266WiFi.h>
#include <Stepper.h>

#define OPEN 0
#define CLOSED 1

//------ wifi ------
const char ssid[] = "PI_Guest_C604_2.4G_altos";
const char pass[] = "1029384756";
//Port 80 is the default HTTP port.
//This means we can access it using
//a url like http://ip
WiFiServer server(80);

//------ door ------
byte door_state;

//------ magnet ------
//maget D2
const byte MAGNET = 4;

//------ lock ------
void operate_lock();
byte lock_state;

//------ motor -------
//in1 D1, in2 D4, in3 D5, in4 D6
const int stepsPerRevolution = 580;
const int in1 = 5, in2 = 2, in3 = 14, in4 = 12;
const int fullTurn = 4*stepsPerRevolution;
Stepper myStepper(stepsPerRevolution, in1, in3, in2, in4);

//------ authorized keys ------
const int keys_capacity = 10;
String keys[keys_capacity];
int keys_size = 0;

//------ subroutines ------
void addKey(String key);
void removeKey(String key);
bool isKeyAuthorized(String key);

void setup() 
{
  ESP.wdtDisable();
  Serial.begin(115200);
  
  pinMode(MAGNET, INPUT);
  
  myStepper.setSpeed(58);
  lock_state = CLOSED;
  door_state = CLOSED;

  //Connect to local wireless network
  WiFi.begin(ssid, pass);

  Serial.print("Waiting for connection");
  while(WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.print("\nConnected at ");
  Serial.println(WiFi.localIP());

  //Launch server
  server.begin();
}

void loop() 
{
  ESP.wdtDisable();

  if(digitalRead(MAGNET) == HIGH) {
    door_state = OPEN;
  }
  else {
    if(door_state == OPEN) {
      operate_lock();
      door_state = CLOSED;
    }
  }

  //wait for client connection. 
  //We support only ONE connection at a time!
  WiFiClient client;
  if(!(client = server.available())) {
    return;
  }
  else {
    Serial.println("A client!");
  
    //read first line of the request
    String req = client.readStringUntil('\r');
    char mode = req.charAt(5);
    String key = req.substring(6, 10);
    Serial.print("Mode: "); Serial.println(mode);
    Serial.print("Key: "); Serial.println(key);
  
    //define whether it is requesting
    //to open or close the door. Undefined
    //requests will be equivalent to request
    //locking.
    if(mode == 'u') {
      Serial.println("unlock requested");
      if(lock_state == CLOSED && door_state == CLOSED) {
        if(isKeyAuthorized(key)) {
          Serial.println("key authorized");
          operate_lock();
        }
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
}

void operate_lock() {
  ESP.wdtDisable();
  if(lock_state == CLOSED) {
    myStepper.step(-fullTurn);
    lock_state = OPEN;
  }
  else if(lock_state == OPEN) {
    myStepper.step(fullTurn);
    lock_state = CLOSED;
  } 
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

