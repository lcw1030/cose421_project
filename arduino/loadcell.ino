#include <SoftwareSerial.h>
#include <HX711.h>

#define RX 11
#define TX 10
#define calibration_factor -7050.0
#define DOUT1 3
#define CLK1 2
#define DOUT2 5
#define CLK2 4
#define Period 1000

HX711 scale1(DOUT1,CLK1);
HX711 scale2(DOUT2,CLK2);
SoftwareSerial bluetooth(TX,RX);
int stuff_used(float weight);
void setup(){
  Serial.begin(9600);
  bluetooth.begin(9600);
  
  scale1.set_scale(calibration_factor);
  scale1.tare();
  scale2.set_scale(calibration_factor);
  scale2.tare();
  }
void loop(){
  float stuff1=scale1.get_units();
  float stuff2=scale2.get_units();
  
  int stu1_used=stuff_used(stuff1);
  int stu2_used=0;
  char stf1[10];
  char stf2[10];
  dtostrf(stu1_used,4,2,stf1);
  dtostrf(stu2_used,4,2,stf2);
  char str[128];
  int n=sprintf(str,"{data:[%s,%s],type:2}",stf1,stf2);
  str[n]='\0';
  bluetooth.println(str);
  delay(Period);
  }


int stuff_used(float weight){
  if(weight>=0.1){
    return 1;}
    else{
      return 0;
      }
  }
