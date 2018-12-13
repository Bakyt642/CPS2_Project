
#include <string.h>
#include <Wire.h>
#include <SPI.h>
#include <DHT.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include <RTCZero.h>
#include <WiFiNINA.h>
#include <MQTT.h>
#include <ArduinoJson.h>
#include <Arduino.h>
#include "wiring_private.h"






/* In which pins are my sensors plugged? */
#define LUM A0  // TEMT6000 Signal pin
#define TMP 4   // DHT22 Signal pin



/* We need a DHT object to address the sensor. */
DHT dht(TMP, DHT22) ; // pin: TMP, model: DHT22

/* Create the new UART instance*/
Uart mySerial (&sercom3, 0, 1, SERCOM_RX_PAD_1, UART_TX_PAD_0); 


/* We need objects to handle:
    1. WiFi connectivity
    2. MQTT messages
*/
WiFiClient wifi_client ;
MQTTClient mqtt_client ;
RTCZero rtc;

/* And associated variables to tell:
    1. which WiFi network to connect to
    2. what are the MQTT broket IP address and TCP port
*/
const char* wifi_ssid     = "gr5";
const char* wifi_password = "92b8276658310";

const char* mqtt_host = "m15.cloudmqtt.com" ;
const uint16_t mqtt_port = 14558;
const char* mqtt_user = "gxovygso";
const char* mqtt_password = "0P7X_9cvhzzV";

/* Change these values to set the current initial time */
const byte seconds = 00;
const byte minutes = 04;
const byte hours = 10;

/* Change these values to set the current initial date */
const byte day = 13;
const byte month = 12;
const byte year = 18;

/* Some variables to handle measurements. */
int tmp ;
int lum ;
int hmdt ;

boolean first_time ;

uint32_t t0, t ;


/* 'topic' is the string representing the topic on which messages
    will be published.
*/

String topic = "ITM/Workbench1/" ;

/*initializing variables related to timestamp*/
String timestamp;
String timeDate;

/*initializing variables related to RFIDcard*/
String identity ="";
byte i = 0;
byte val = 0;
byte code[6];
byte checksum = 0;
byte bytesread = 0;
byte tempbyte = 0;

/*initializing variables related to Button*/
const int buttonPin = 6;     // the number of the pushbutton pin
const int ledPin =  7;      // the number of the LED pin

int buttonPushCounter1 = 0;   // counter for the number of button presses for first WORKER1
int buttonPushCounter2 = 0;   // counter for the number of button presses for first WORKER2
int buttonState = 0;         // current state of the button
int lastButtonState = 0;     // previous state of the button


/* Time between two sensings and values sent. */
#define DELTA_T 8000


/* ################################################################### */
void setup() {
  mySerial.begin(9600); //Uart setup

  pinPeripheral(0, PIO_SERCOM); //Assign RX function to pin 0
  pinPeripheral(1, PIO_SERCOM); //Assign TX function to pin 1

  Serial.begin(9600) ;  // monitoring via Serial 
  delay(100) ;
  Serial.println("Initializing...\n") ;
  rtc.begin(); // initialize RTC
  pinMode(LED_BUILTIN, OUTPUT) ;
  digitalWrite(LED_BUILTIN, LOW) ;  // switch LED off

  dht.begin() ;

  WiFi.begin(wifi_ssid, wifi_password) ;
  mqtt_client.begin(mqtt_host, mqtt_port, wifi_client) ;
  mqtt_client.onMessage(callback) ;

  tmp = lum = hmdt = -1.0 ;

  first_time = true ;

  // Time begins now!
  t0 = t = millis() ;
  rtc.setTime(hours, minutes, seconds);
  rtc.setDate(day, month, year);

  // initialize the LED pin as an output:
  pinMode(ledPin, OUTPUT);
  // initialize the pushbutton pin as an input:
  pinMode(buttonPin, INPUT);
}




/* ################################################################### */
void loop() {
  ButtonCount();

  if (mySerial.available() > 0) {
    if ((val = mySerial.read()) == 2) {                 // check for header
      bytesread = 0;
      while (bytesread < 12) {                        // read 10 digit code + 2 digit checksum
        if ( mySerial.available() > 0) {
          val = mySerial.read();
          if ((val == 0x0D) || (val == 0x0A) || (val == 0x03) || (val == 0x02)) { // if header or stop bytes before the 10 digit reading
            break;                                    // stop reading
          }

          // Do Ascii/Hex conversion:
          if ((val >= '0') && (val <= '9')) {
            val = val - '0';
          } else if ((val >= 'A') && (val <= 'F')) {
            val = 10 + val - 'A';
          }

          // Every two hex-digits, add byte to code:
          if (bytesread & 1 == 1) {
            // make some space for this hex-digit by
            // shifting the previous hex-digit with 4 bits to the left:
            code[bytesread >> 1] = (val | (tempbyte << 4));

            if (bytesread >> 1 != 5) {                // If we're at the checksum byte,
              checksum ^= code[bytesread >> 1];       // Calculate the checksum... (XOR)
            };
          } else {
            tempbyte = val;                           // Store the first hex digit first...
          };

          bytesread++;                                // ready to read next digit
        }
      }

      // Output to Serial:
      identity = String();
      if (bytesread == 12) {                          // if 12 digit read is complete
        Serial.print("5-byte code: ");
        for (i = 0; i < 5; i++) {
          if (code[i] < 16) {
            identity += String("0") ;
          }
          identity += String(code[i], HEX) ;
        }
        Serial.println(identity);
      }
      bytesread = 0;
    }
  }




  /* We try to connect to the broker and launch the client loop.
  */
  mqtt_client.loop() ;
  if ( ! mqtt_client.connected() ) {
    reconnect() ;
  }

  /* Values are read from sensors at fixed intervals of time.
  */
  // ===================================================
  t = millis() ;
  if ( first_time || (t - t0) >= DELTA_T ) {
    t0 = t ;
    first_time = false ;

    lum = getLum() ;
    tmp = getTmp() ;
    hmdt = getHmdt() ;
    fetchTime();
    fetchDate();
    sendValues() ;
  }


}


/* ------------------------------------------------------------------- */
double getLum()
{
  double acc = 0 ;
  uint8_t n_val ;

  for ( n_val = 0 ; n_val < 10 ; n_val++ ) {
    acc += analogRead(LUM) ;
    delay(5) ;
  }

  return acc / n_val ;
}


/* ------------------------------------------------------------------- */
double getTmp()
{
  return dht.readTemperature() ;
}


/* ------------------------------------------------------------------- */
double getHmdt()
{
  return dht.readHumidity() ;
}

/* ################################################################### */
/* This function handles the connection/reconnection to
   the MQTT broker.
   Each time a connection or reconnection is made, it
   publishes a message on the topic containing
   the number of milli seconds since uController start.
*/
void reconnect() {
  Serial.print("Connecting to ");
  Serial.print(wifi_ssid);
  Serial.print("\n") ;

  while (WiFi.status() != WL_CONNECTED) {
    digitalWrite(LED_BUILTIN, HIGH) ;
    delay(500);
    digitalWrite(LED_BUILTIN, LOW) ;
    Serial.print(".");
    delay(500);
  }

  Serial.print("\n");
  Serial.print("WiFi connected\n");
  Serial.print("IP address: \n");
  Serial.print(WiFi.localIP());
  Serial.print("\n") ;


  Serial.print("MQTT: trying to connect to host ") ;
  Serial.print(mqtt_host) ;
  Serial.print(" on port ") ;
  Serial.print(mqtt_port) ;
  Serial.println(" ...") ;

  while ( !mqtt_client.connect(mqtt_user, mqtt_user, mqtt_password) ) {
    digitalWrite(LED_BUILTIN, HIGH) ;
    delay(500);
    digitalWrite(LED_BUILTIN, LOW) ;
    Serial.print(".");
    delay(500);
  }
  Serial.println("MQTT: connected") ;
  Serial.print("MQTT: root topic is \"") ;
  Serial.print(topic) ;
  Serial.println("\"") ;

  mqtt_client.publish(String(topic).c_str(), String(millis())) ;
  //  mqtt_client.subscribe(String(topic + "/ACTION").c_str()) ;  /* If you want to subscribe to topics, you have to do it here. */

}

/* ################################################################### */
/* This function handles the sending of all the values
   collected by the sensors.
   Values are sent on a regular basis (every DELTA_T_SEND_VALUES
   milliseconds).
*/


void sendValues() {

  if ( mqtt_client.connected() ) {

    if (identity.equals("3d002187a8")) 
    {
      mqtt_client.publish(String(topic + "Worker1").c_str(), String("{\"RFID1 \":\"") + String(identity)+String("\",")+String("\"Time\":\"") + String(timestamp) + String("\",")+String("\"Date \":\"") + String(timeDate)  + String("\"}")) ;
      mqtt_client.publish(String(topic + "Worker1/"+"Enviroment/LUMI" ).c_str(), String("{\"Illumination \":\"") + String(lum).c_str() + String("\",")+String("\"Time\":\"") + String(timestamp) + String("\",")+String("\"Date \":\"") + String(timeDate) + String("\"}") );
      mqtt_client.publish(String(topic + "Worker1/"+"Enviroment/TEMP").c_str(), String("{\"Temperature *C \":\"") + String(tmp).c_str() + String("\",")+String("\"Time\":\"") + String(timestamp) + String("\",")+String("\"Date \":\"") + String(timeDate)  + String("\"}")) ;
      mqtt_client.publish(String(topic + "Worker1/"+"Enviroment/HUMI").c_str(), String("{\"Humidity \":\"") + String(hmdt).c_str() + String("\",")+String("\"Time\":\"") + String(timestamp) + String("\",")+String("\"Date \":\"") + String(timeDate) + String("\"}")) ;
      mqtt_client.publish(String(topic + "Worker1/Product").c_str(), String("{\"Readyproducts\":\"") + String(buttonPushCounter1) +String("\",")+String("\"Time\":\"") + String(timestamp) + String("\",")+String("\"Date \":\"") + String(timeDate)  + String("\"}")) ;


    }
    if (identity.equals("8400071725"))
    {
      mqtt_client.publish(String(topic + "Worker2").c_str(), String("{\"RFID2 \":\"") + String(identity)+String("\",")+String("\"Time\":\"") + String(timestamp) + String("\",")+String("\"Date \":\"") + String(timeDate)  + String("\"}")) ;
      mqtt_client.publish(String(topic + "Worker2/"+"Enviroment/LUMI" ).c_str(), String("{\"Illumination  \":\"") + String(lum).c_str() + String("\",")+String("\"Time\":\"") + String(timestamp) + String("\",")+String("\"Date \":\"") + String(timeDate) + String("\"}") );
      mqtt_client.publish(String(topic + "Worker2/"+"Enviroment/TEMP").c_str(), String("{\"Temperature *C \":\"") + String(tmp).c_str() + String("\",")+String("\"Time\":\"") + String(timestamp) + String("\",")+String("\"Date \":\"") + String(timeDate)  + String("\"}")) ;
      mqtt_client.publish(String(topic + "Worker2/"+"Enviroment/HUMI").c_str(), String("{\"Humidity \":\"") + String(hmdt).c_str() + String("\",")+String("\"Time\":\"") + String(timestamp) + String("\",")+String("\"Date \":\"") + String(timeDate) + String("\"}")) ;
      mqtt_client.publish(String(topic + "Worker2/Product").c_str(), String("{\"Readyproducts\":\"") + String(buttonPushCounter2) +String("\",")+String("\"Time\":\"") + String(timestamp) + String("\",")+String("\"Date \":\"") + String(timeDate)  + String("\"}")) ;
     }
   }
}



/* ################################################################### */
/* The main callback to listen to topics.
*/
void callback(String &intopic, String &payload)
{
 /* Here as an example code for call back if want to subscribe later,need to change as per requirement*/
//  Serial.println("incoming: " + topic + " - " + payload);
//
//  if (payload.equals("ON") ) {
//    digitalWrite(LED_BUILTIN, HIGH);   // turn the LED on (HIGH is the voltage level)
//    Serial.println("I recieved ON");
//    // wait for a second
//
//  }
//  else if (payload.equals("OFF") ) {
//    digitalWrite(LED_BUILTIN, LOW);    // turn the LED off by making the voltage LOW
//    Serial.println("I recieved OFF");
//  }
//
//  else
//  {
//    mqtt_client.publish(String("/LOG").c_str(), String("NOT EXPECTED VALUE")) ;
//  }
//
//
//  boolean rc = mqtt_client.subscribe("sensor/2/ACTION", 1);
//  Serial.println(rc);
}




 /* Function to get time */
void fetchTime()
{
  char buffer [25] = "";

  sprintf(buffer, "%02d:%02d:%02d", rtc.getHours(), rtc.getMinutes(), rtc.getSeconds());
  timestamp = buffer;
}

 /* Function to get date */
void fetchDate()
{
  char buffer [25] = "";

  sprintf(buffer, "%d:%d:%0d", rtc.getDay(), rtc.getMonth(), rtc.getYear());
  timeDate = buffer;
}


 /* Function to count products depending on which RFID was scaned  */
void ButtonCount()
{
    buttonState = digitalRead(buttonPin);
    if (digitalRead(buttonPin) != lastButtonState) {
    // if the state has changed, increment the counter
    if (digitalRead(buttonPin) == HIGH && identity.equals("3d002187a8")) {
      // if the current state is HIGH then the button went from off to on:
      buttonPushCounter1++;
      Serial.println("on");
      Serial.print("number of button pushes: ");
      Serial.println(buttonPushCounter1);
      digitalWrite(ledPin, HIGH);
    }
     if (digitalRead(buttonPin) == HIGH && identity.equals("8400071725")) {
      // if the current state is HIGH then the button went from off to on:
      buttonPushCounter2++;
      Serial.println("on");
      Serial.print("number of button pushes: ");
      Serial.println(buttonPushCounter2);
      digitalWrite(ledPin, HIGH);
    }
    
    else {
      // if the current state is LOW then the button went from on to off:
      Serial.println("off");
      digitalWrite(ledPin, LOW);
    }
    // Delay a little bit to avoid bouncing
    delay(50);
  }
    lastButtonState = buttonState;


}

void SERCOM3_Handler() {
  mySerial.IrqHandler();
}
