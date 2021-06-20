#include <SoftwareSerial.h>

int bRx = 11; 
int bTx = 10; 
SoftwareSerial bluetooth(bTx,bRx);
int llPin = A0;
int lrPin = A1;
int ulPin = A2;
int urPin = A3;
int period=10000;
void setup() {
  Serial.begin(9600);
  delay(100);
  bluetooth.begin(9600);

}

void loop() {
  float llIn = analogRead(llPin);
  float lrIn = analogRead(lrPin);
  float ulIn = analogRead(ulPin);
  float urIn = analogRead(urPin);

  char llCh[10];
  char lrCh[10];
  char ulCh[10];
  char urCh[10];
  dtostrf(llIn, 4, 2, llCh);
  dtostrf(lrIn, 4, 2, lrCh);
  dtostrf(ulIn, 4, 2, ulCh);
  dtostrf(urIn, 4, 2, urCh);

  char str[128];
  sprintf(str, "{data:[%s, %s, %s, %s], type:1}", llCh, lrCh, ulCh, urCh);
  Serial.println(str);                                                        
  bluetooth.println(str);
  delay(period);
}
