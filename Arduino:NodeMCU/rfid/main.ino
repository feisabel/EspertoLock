#include <MFRC522Hack.h>
#include <MFRC522Debug.h>
#include <require_cpp11.h>
#include <MFRC522.h>
#include <MFRC522Extended.h>
#include <deprecated.h>
#include <SPI.h>

#include <Stepper.h>

//------ lock ------
#define OPEN 0
#define CLOSED 1
void operate_lock();
byte lock_state;

//------ motor -------
const int stepsPerRevolution = 580;
const int in1 = 5, in2 = 6, in3 = 7, in4 = 8;
const int fullTurn = 4*stepsPerRevolution;
Stepper myStepper(stepsPerRevolution, in1, in3, in2, in4);

//------ RFID --------
#define SS_PIN 10
#define RST_PIN 9
MFRC522 myRFID(SS_PIN, RST_PIN);

//------ magnet control switch ------
#define MCS_PIN 2

//------ database -------
String authorized[] = {"2cda2c5", "a6261e7e", "ebe668d2"};
int n_authorized = 3;

void setup() 
{
  Serial.begin(9600);
  SPI.begin();
  myStepper.setSpeed(58);
  myRFID.PCD_Init();
  pinMode(MCS_PIN, INPUT);
  lock_state = OPEN;
}

void loop() 
{ 

  //wait while door is open
  while(digitalRead(MCS_PIN) == HIGH);

  //lock the door when it closes
  operate_lock();

  //wait card
  while(!myRFID.PICC_IsNewCardPresent());

  //read card if any was found
  while(!myRFID.PICC_ReadCardSerial());
  
  //load tag UID
  String UID = "";
  for (byte i = 0; i < myRFID.uid.size; i++) {
     UID.concat( String(myRFID.uid.uidByte[i], HEX) );
  }
  Serial.print("UID: "); Serial.println(UID);

  //Check against registered tags
  for (byte i = 0; i < n_authorized; ++i) {
    //if it' a registered tag, unlock door
    if(UID.compareTo(authorized[i]) == 0) {
      Serial.print("Opening lock...");
      operate_lock();
      Serial.println("done");
      break;
    }
  }

  //wait while door is closed
  while(digitalRead(MCS_PIN) == LOW);
  
  //wait so things won't rush
  delay(500);
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

