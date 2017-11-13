#include <MFRC522Hack.h>
#include <MFRC522Debug.h>
#include <require_cpp11.h>
#include <MFRC522.h>
#include <MFRC522Extended.h>
#include <deprecated.h>
#include <SPI.h>

#include <Stepper.h>

//------ motor -------
const int stepsPerRevolution = 580;
const int in1 = 2, in2 = 3, in3 = 4, in4 = 5;
const int fullTurn = 4*stepsPerRevolution;
Stepper myStepper(stepsPerRevolution, in1, in3, in2, in4);

#define OPEN 0
#define CLOSE 1
void operate_door(byte mode);

byte door_state = CLOSE;

//------ RFID --------
#define SS_PIN 10
#define RST_PIN 9
MFRC522 myRFID(SS_PIN, RST_PIN);

//------ database -------
String authorized[] = {"2cda2c5", "a6261e7e", "ebe668d2"};

void setup() 
{
  Serial.begin(9600);
  SPI.begin();
  myStepper.setSpeed(58);
  myRFID.PCD_Init();
}

void loop() 
{
  //TODO: wait for magnetic trigger signal 
  //AND door_state = open to lock
  if(door_state == OPEN)
  {
    Serial.print("Closing lock...");
    operate_door(CLOSE);
    Serial.println("done");
  }
  else
  {
    //wait cards
    if( !myRFID.PICC_IsNewCardPresent() )
      return;
  
    //read card if any was found
    if( !myRFID.PICC_ReadCardSerial() )
      return;
  
    //load tag UID
    String UID = "";
    for (byte i = 0; i < myRFID.uid.size; i++)
       UID.concat( String(myRFID.uid.uidByte[i], HEX) );
  
    Serial.print("UID: "); Serial.println(UID);

    //Check against registered tags
    for (byte i = 0; i < sizeof(authorized); ++i)
      if(UID.compareTo(authorized[i]) == 0)
      {
        Serial.print("Opening lock...");
        operate_door(OPEN);
        Serial.println("done");
        break;
      } 
  }

  //wait so things won't rush
  delay(500);
}

void operate_door(byte mode)
{
  if(mode == OPEN) myStepper.step(fullTurn);
  else if(mode == CLOSE) myStepper.step(-fullTurn);
  
  door_state = mode;
}

