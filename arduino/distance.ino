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
  Serial.print("Distance: ");
  Serial.println(distance);
  return distance;
}

void loop() {
  double distance = check_distance();
  char distance_str[10];
  dtostrf(distance,4, 2, distance_str); 
  char str[128];
  int n = sprintf(str, "{data:[%s], type:0}", distance_str);
  //str[n] = '\0';
  Serial.println(str);
  bluetooth.println(str);
  delay(period);
}
