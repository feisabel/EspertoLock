#include <SoftwareSerial.h>
#include <Stepper.h>

//------ lock and door ------
#define OPEN HIGH
#define CLOSED LOW
#define LOCKED 1
#define UNLOCKED 0

void operate_lock();
byte lock_state, door_state, owner_inside;

//------ motor -------
//preto +5, branco gnd
const int stepsPerRevolution = 580;
const int in1 = 5, in2 = 6, in3 = 7, in4 = 8;
const int fullTurn = 4*stepsPerRevolution;
Stepper myStepper(stepsPerRevolution, in1, in3, in2, in4);

//------ magnet control switch ------
#define MCS_PIN 4

//------ bluetooth -------
#define BT_TX 2
#define BT_RX 3

SoftwareSerial BT(BT_RX, BT_TX);
String owner = "E891:20:372D83";

String sendATCommand(char *cmd)
{
  BT.write(cmd);
  while(!BT.available()); //Hold while answer is not available

  String answer="";
  while(BT.available()) answer += (char)BT.read();

  return answer;
}

bool isOwnerNear(String owner)
{
  String ans = sendATCommand("AT+INQ\r\n");

  //loop through ANS looking for \n. At each stop, we
  //have a line with device info. Check whether this
  //device is OWNER and check whether distance is in
  //acceptable range
  int last = 0;
  for(int i = 0; i < ans.length(); ++i)
    if( ans[i] == '\n' ) 
    { 
      String dev = ans.substring(last, i-1);
      if( dev.equals("OK") ) continue;
      
      String mac = dev.substring(5, 19);
      String rssi = dev.substring(27, 31);

      if( !mac.equals(owner) ) continue;

      //Serial.println("Dev [" + mac + "] RSSI [" + rssi + "]");
      return rssi[2] == 'C';
      
      last = i+1;
    }
    
  //Ok, technically we should get the last chunk,
  //but I'm too lazy to think of how to do this
  //in an elegant way
  
  return false;
}

void wakeBluetooth()
{
  sendATCommand("AT+ORGL\r\n"); //Reseta para configuração padrão
  sendATCommand("AT+RMAAD\r\n"); //Remove dispositivos pareados
  sendATCommand("AT+ROLE=1\r\n");//Seta modo MASTER
  //sendATCommand("AT+RESET\r\n"); //Reseta
  sendATCommand("AT+CMODE=1\r\n"); //Permite conexão a qualquer endereço
  sendATCommand("AT+INQM=1,20,8\r\n"); //Procure um dispositivo ou pare após 2s
  sendATCommand("AT+INIT\r\n"); //Inicializa dispositivo  
}

void setup() 
{
  Serial.begin(9600);
  BT.begin(38400);
  myStepper.setSpeed(58);

  wakeBluetooth();
  
  pinMode(MCS_PIN, INPUT);
  
  lock_state = UNLOCKED;
  owner_inside = true;
}

void loop() 
{
  delay(100);
    
  //check state of the door
  door_state = digitalRead(MCS_PIN);
  
  Serial.print("Door is "); Serial.print( door_state == CLOSED ? "CLOSED" : "OPEN" );
  Serial.print(" and "); Serial.println( lock_state == LOCKED ? "LOCKED" : "UNLOCKED" );

  //if door is open, do nothing
  if( door_state == OPEN ) return;

  //automatically lock if door is closed
  if(lock_state == UNLOCKED)
  {
    Serial.println("Door is closed and unlocked. Closing automatically.");
    operate_lock();

    return;
  }

  //look for owner's cellphone
  if( isOwnerNear(owner) && lock_state == LOCKED)
  {
    Serial.println("Owner is near and door is locked! Unlocking.");
    operate_lock();

    return;
  }
}

void operate_lock()
{
  if(lock_state == LOCKED) {
    myStepper.step(-fullTurn);
    lock_state = UNLOCKED;
  }
  else if(lock_state == UNLOCKED) {
    myStepper.step(fullTurn);
    lock_state = LOCKED;
  } 
}

