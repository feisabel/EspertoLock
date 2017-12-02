#include <Stepper.h>

//------ lock ------
#define OPEN 0
#define CLOSED 1
void operate_lock();
byte lock_state;

//------ motor -------
//preto +5, branco gnd
const int stepsPerRevolution = 580;
const int in1 = 5, in2 = 6, in3 = 7, in4 = 8;
const int fullTurn = 4*stepsPerRevolution;
Stepper myStepper(stepsPerRevolution, in1, in3, in2, in4);

//------ magnet control switch ------
#define MCS_PIN 2

//------ bluetooth -------
#define BT_TX 3
#define BT_RX 4

//------ WIFI ------
#define WIFI_IN 9 //connected to NodeMCU`s D4

void setup() 
{
  Serial.begin(9600);
  myStepper.setSpeed(58);
  pinMode(MCS_PIN, INPUT);
  lock_state = OPEN;
}

void loop() 
{ 
  //wait while door is open
  while(digitalRead(MCS_PIN) == HIGH);

  //lock the door when it closes
  operate_lock();

  //wait command to open door
  while(digitalRead(WIFI_IN) == LOW);

  //wait while door is closed
  while(digitalRead(MCS_PIN) == LOW);
}

void operate_lock()
{
  if(lock_state == CLOSED) {
    myStepper.step(-fullTurn);
    lock_state = OPEN;
  }
  else if(lock_state == OPEN) {
    myStepper.step(fullTurn);
    lock_state = CLOSED;
  } 
}
