#include <SoftwareSerial.h>

//need to make 2 different softwareserials
//one for the transmitter
//one for the reciever

#define TRAN      7    
#define RECV      2

#define SYNC      10
#define DEVON     14
#define DEVOFF    18
#define ALERTON   22
#define ALERTOFF  26
#define STOP      30

#define NOTIFY    1
#define ALERT     2
#define OK        3

int ledStatus = OK;

String lcdBuffer = String("WELCOME");
boolean lcdChange = false;

SoftwareSerial transmitter(255,TRAN);
SoftwareSerial reciever(RECV,255);

void setup()
{
  ledSetup(); //Setup the RGB led
  lcdSetup(); //Setup the LCD on the serial pin
  wirSetup(); //Setup the transmitter and reciever
  
  clearLCD();
  lcdPrint("WHY?");
  
  backlightOn();
  
  //hausDemo();
}

String str = String("");

void loop()
{
  while(Serial.available())
  {
    if(Serial.available() > 0)
    {
      str += char(Serial.read());
    }
  }
  
  if(str.length() > 0)
  {
    checkMessage(str);
    str="";
  }  
  
  switch(ledStatus)
  {
    case OK:
    ledOk();
    break;
    case ALERT:
    ledAlert();
    break;
    case NOTIFY:
    ledNotify();
    break;
  }
  
  sendSlaveCommand(SYNC);
  
  if(reciever.available() > 0)
  {
    int package = 0;
    int mail = 0;
    for(int i=0;i < 45;i++)
    {
      int c = reciever.read();
      if(c != 0)
      {
        //Serial.println(c);
      }
      if(c == 200)
      {
        clearLCD();
        lcdPrint("MAIL ARRIVED");
        Serial.println();
        break;        
      }
      else if(c == 150)
      {
        clearLCD();
        lcdPrint("PACKAGE ARRIVED");
        Serial.println();     
        break;        
      }
      delay(18);
    }
    //Serial.println("------------");
  }
}

void wirSetup()
{
  pinMode(TRAN,OUTPUT);
  pinMode(RECV,INPUT);
  
  transmitter.begin(2400);
  reciever.begin(4800);
}

void checkMessage(String message)
{  
  if(message.indexOf("LCD") != -1)
  {
     if(message.substring(4).equals("CLEAR"))
     {
      clearLCD();
      return;
     }
    
    char line[message.substring(4,5).length()+1];
    message.substring(4,5).toCharArray(line,message.substring(4,5).length()+1);
    int lcdLine = line[0]-48;
    
    lcdBuffer = (message.substring(6));
    
    switch(lcdLine)
    {
      case 1:
      selectLineOne();
      break;
      case 2:
      selectLineTwo();
      break;
      case 3:
      selectLineThree();
      break;
      case 4:
      selectLineFour();
      break;
      default:
      selectLineOne();
      break;
    }
    
    lcdPrint(lcdBuffer);
  }
  else if(message.indexOf("LED") != -1)
  {
    
    String ledBuffer = (message.substring(4));
    lcdChange = true;
    
    if(ledBuffer.equals("OK"))
    {
      ledStatus = OK;
    }
    else if(ledBuffer.equals("ALERT"))
    {
      ledStatus = ALERT;
    }
    else if(ledBuffer.equals("NOTIFY"))
    {
      ledStatus = NOTIFY;
    }
    else
    {
      ledStatus = OK;
    }
  }
  else if(message.indexOf("SLAVE") != -1)
  {
    char ID[message.substring(6,9).length()+1];
    message.substring(6,9).toCharArray(ID,message.substring(6,9).length()+1);
    
    int slaveID = (100*(ID[0]-48)) + (10*(ID[1]-48)) + (ID[2]-48);
    String slaveCommand = message.substring(10);
    
    clearLCD();
    selectLineOne();
    lcdPrint("SLAVE MOD ID " + String(slaveID));
    
    if(slaveCommand.equals("DEVON"))
    {
      selectLineTwo();
      lcdPrint("DEVICE ON");
      
      sendSlaveCommand(slaveID);
      sendSlaveCommand(DEVON);
      sendSlaveCommand(STOP);
    }
    else if(slaveCommand.equals("DEVOFF"))
    {
      selectLineTwo();
      lcdPrint("DEVICE OFF");
      
      sendSlaveCommand(slaveID);
      sendSlaveCommand(DEVOFF);
      sendSlaveCommand(STOP);
    }
    else if(slaveCommand.equals("ALERTON"))
    {
      selectLineTwo();
      lcdPrint("ALERT ON");
      
      sendSlaveCommand(slaveID);
      sendSlaveCommand(ALERTON);
      sendSlaveCommand(STOP);
    }
    else if(slaveCommand.equals("ALERTOFF"))
    {
      selectLineTwo();
      lcdPrint("ALERT OFF");
      
      sendSlaveCommand(slaveID);
      sendSlaveCommand(ALERTOFF);
      sendSlaveCommand(STOP);
    }
    else if(slaveCommand.equals("DEMO"))
    {
      hausDemo(); 
    }
  }
}


void sendSlaveCommand(int message)
{
  if(message != SYNC)
  {
    for(int i=0;i<100;i++)
    {
      transmitter.write(message);
      delay(6);
    }
    for(int i=0;i<50;i++)
    {
      transmitter.write(SYNC);
      delay(4);
    }
    for(int i=0;i<100;i++)
    {
      transmitter.write(message);
      delay(4);
    }
  }
  else
  {
    for(int i=0;i<50;i++)
    {
      transmitter.write(SYNC);
      delay(0);
    }
  }
}

void hausDemo()
{
  clearLCD();
  sendSlaveCommand(DEVON);
  lcdPrint("DEVICE ON");
  
  int time = millis();
  while(millis() - time < 15000)
  {
    sendSlaveCommand(SYNC);
  }
  
  clearLCD();
  sendSlaveCommand(DEVOFF);
  lcdPrint("DEVICE OFF");
  time = millis();
  while(millis() - time < 15000)
  {
    sendSlaveCommand(SYNC);
  }
  
  clearLCD();
  sendSlaveCommand(ALERTON);
  lcdPrint("ALERT ON");
  time = millis();
  
  while(millis() - time < 15000)
  {
    sendSlaveCommand(SYNC);
  }
  clearLCD();
  
  lcdPrint("ALERT OFF");
  sendSlaveCommand(ALERTOFF);
}
