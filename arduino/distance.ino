double duration,distance;
int trigPin = 7;
int echoPin =A0;
int period=10000;

int LEDG = 6;
int LEDR = 5;
int LEDY = 4;
int PIR = 8;


#include <SoftwareSerial.h> // 0,1번핀 제외하고 Serial 통신을 하기 위해 선언

// Serial 통신핀으로 D11번핀을 Tx로, D10번핀을 Rx로 선언
SoftwareSerial bluetooth(11, 10);

void setup() {
  pinMode(LEDG, OUTPUT);
  pinMode(LEDR, OUTPUT);
  pinMode(LEDY, OUTPUT);
  pinMode(PIR, INPUT);
  pinMode(trigPin,OUTPUT);
  pinMode(echoPin,INPUT);
  Serial.begin(9600);
  delay(100);
  bluetooth.begin(9600); // 통신 속도 9600bps로 블루투스 시리얼 통신 시작
}

int check_pir() {
  int human = digitalRead(PIR);
  if(human == HIGH) {
    digitalWrite(LEDY, HIGH);
    Serial.println("PIR: ON");
    //s.println("PIR: ON");
  } else {
    digitalWrite(LEDY, LOW);
    Serial.println("PIR: OFF");
    //s.println("PIR:OFF");
  }
  return human;
}

double check_distance(){
  double distance, duration;
  /* Ultrasonic Sensor */
  digitalWrite(trigPin,LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin,HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin,LOW);
  duration = pulseIn(echoPin,HIGH);
  distance = duration / 29 / 2;
  Serial.print("Distance2: ");
  Serial.println(distance);
  //bluetooth.print("Distance2: ");
  //bluetooth.println(distance);
  return distance;
}

void check_status(int human, double distance) {
  //Serial.print("human: ");
  //Serial.println(human);
  if (distance >= 3000) {
    Serial.print("ddd: ");
    Serial.println(distance);
    return;
  }
  
  if (distance <= 70){
    //Serial.println("1");
    digitalWrite(LEDG, HIGH);
    digitalWrite(LEDR, LOW);
    //s.println("Studying");
  } else {
    //Serial.println("0");
    digitalWrite(LEDR, HIGH);
    digitalWrite(LEDG, LOW);
    //s.println("Not Studying");
  }
}

void loop() {
  //int human = check_pir();
  double distance = check_distance();
  char distance_str[10];
  dtostrf(distance,4, 2, distance_str); 
  char str[128];
  sprintf(str, "{data:[%s], type:0}", distance_str);
  Serial.println(str);
  bluetooth.println(str);
  //check_status(human, distance);
  delay(period);
}
