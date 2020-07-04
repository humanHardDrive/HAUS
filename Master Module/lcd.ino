void lcdSetup()
{
  Serial.begin(2400);
}

void selectLineOne(){  //puts the cursor at line 0 char 0.
   Serial.write(0xFE);   //command flag
   Serial.write(128);    //position
}

void selectLineTwo(){  //puts the cursor at line 2 char 0.
   Serial.write(0xFE);   //command flag
   Serial.write(192);    //position
}

void selectLineThree(){  //puts the cursor at line 3 char 0.
   Serial.write(0xFE);   //command flag
   Serial.write(148);    //position
}

void selectLineFour(){  //puts the cursor at line 4 char 0.
   Serial.write(0xFE);   //command flag
   Serial.write(212);    //position
}

void goTo(int position) { //position = line 1: 0-19, line 2: 20-39, etc, 79+ defaults back to 0
if (position<20){ Serial.write(0xFE);   //command flag
              Serial.write((position+128));    //position
}else if (position<40){Serial.write(0xFE);   //command flag
              Serial.write((position+128+64-20));    //position 
}else if (position<60){Serial.write(0xFE);   //command flag
              Serial.write((position+128+20-40));    //position
}else if (position<80){Serial.write(0xFE);   //command flag
              Serial.write((position+128+84-60));    //position              
} else { goTo(0); }
}

void clearLCD(){
   Serial.write(0xFE);   //command flag
   Serial.write(0x01);   //clear command.
}

void backlightOn(){  //turns on the backlight
    Serial.write(0x7C);   //command flag for backlight stuff
    Serial.write(157);    //light level.
}

void backlightOff(){  //turns off the backlight
    Serial.write(0x7C);   //command flag for backlight stuff
    Serial.write(128);     //light level for off.
}

void backlight50(){  //sets the backlight at 50% brightness
    Serial.write(0x7C);   //command flag for backlight stuff
    Serial.write(143);     //light level for off.
}

void serCommand(){   //a general function to call the command flag for issuing all other commands   
  Serial.write(0xFE);
}

void displayOn()
{
  Serial.write(254);
  Serial.write(12);
}

void displayOff()
{
  Serial.write(254);
  Serial.write(8);
}

void lcdPrint(String str)
{
  Serial.print(str);
}
