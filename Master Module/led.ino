#define RED    3
#define GREEN  5
#define BLUE   6

void ledSetup()
{
  pinMode(RED,OUTPUT);
  pinMode(BLUE,OUTPUT);
  pinMode(GREEN,OUTPUT);
}

void ledAlert()
{
  digitalWrite(BLUE,LOW);
  digitalWrite(GREEN,LOW);
  
  for(int r = 0;r < 100;r++)
  {
    analogWrite(RED,r);
    transmitter.write(SYNC);
    delay(9);
  }
  
  for(int r = 100;r >=0;r--)
  {
    analogWrite(RED,r);
    transmitter.write(SYNC);
    delay(9);
  }
  sendSlaveCommand(SYNC);
}

void ledNotify()
{
  digitalWrite(RED,LOW);
  digitalWrite(GREEN,LOW);
  
  for(int b=0;b < 100;b++)
  {
    analogWrite(BLUE,b);
    transmitter.write(SYNC);
    delay(9);
  }
  
  for(int b=100;b >= 0;b--)
  {
    analogWrite(BLUE,b);
    transmitter.write(SYNC);
    delay(9);    
  }
  sendSlaveCommand(SYNC);
}

void ledOk()
{
  digitalWrite(RED,LOW);
  digitalWrite(BLUE,LOW);
  digitalWrite(GREEN,HIGH);
}
