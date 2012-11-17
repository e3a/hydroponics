/* This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
 
/* ------------------------------------------------------------------------------
 * Hydroponics Controller Arduino Sketch
 * 
 * This Sketch is controlling your 
 * 
 * Author: e3a
 * Date: October 2012
 * Version: 1.0
 *
 * Changes:
 * 
 * ------------------------------------------------------------------------------
 */
#include <SPI.h>         
#include <Ethernet.h>
#include <EthernetUdp.h>
#include <Time.h>
#include <stdio.h>
#include <EEPROM.h>
#include <JsonReader.h>
#include "DHT.h"
#include "EmonLib.h"
#include "hydroponics.h"

#define DHTTYPE DHT22  
#define PIN_DHT  8     
#define PIN_EMON 3
#define PIN_MOISTURE  2     
#define HTTP_LINE_BUFFER 128
#define SWITCHES_COUNT 6
#define START_SWITCHES 100
#define SWITCHES_SIZE 50
#define SWITCHES_TIMERS 2
int SWITCHES_INDEX=2;


byte _ip[4];
EthernetServer server = NULL;
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };

EthernetUDP Udp;
JsonReader reader;

unsigned int localPort = 8888;
const int NTP_PACKET_SIZE = 48;
byte packetBuffer[NTP_PACKET_SIZE];

char lineBuffer[HTTP_LINE_BUFFER];
int switchNumber;
unsigned long currentTimestamp = 0;

byte defaultValues[] = {
  1,                // (00) is configured 
  0,                // (01) enable DHCP
  192, 168, 0, 249, // (02) IP local
  00, 80,           // (06) Port local
  81, 94, 123, 17,  // (08) Time Server (0.ch.pool.ntp.org)
  192, 168, 0, 11,  // (12) Hydroponics Server IP
  0x1f, 0x90,       // (16) Hydroponics Server Port
  2,                // (18) TIMEZONE
  230,              // (19) Voltage
  0, 4              // (20) Irms Factor  
};

/* Main Setup */
void setup() {
  Serial.begin(9600);
  Serial.println("<---");
  Serial.print("Free RAM:");
  Serial.println(freeRam());

  /* Setup the EEPROM for configuration */
  // EEPROM.write(0, 0); //FLUSH EEPROM
  if(EEPROM.read(0) == 0) {
    for(int i=0; i<sizeof(defaultValues); i++) {
      EEPROM.write(i, defaultValues[i]);
    }
  }

  //Setup Sensors
  initSensors();

  //Setup Server
  getIp();
  IPAddress ip(_ip);
  server = EthernetServer(port());
  Ethernet.begin(mac, ip);
  server.begin();

  //Setup NTP Time Sync
  Udp.begin(localPort);
  setSyncProvider(processSyncMessage);
  setSyncInterval(24 * 3600 * 1000);

  //Setup Scheduler
  for(int i=0; i<SWITCHES_COUNT; i++) {
    pinMode(i+SWITCHES_INDEX, OUTPUT); 
  }
  Serial.println("--->");
}

void loop() {

  if(currentTimestamp == 0 || (currentTimestamp + 50000) < millis()) {
    currentTimestamp = millis();
    readValues();
    if(valuesUpdated()) {
      int l = jsonCalibre(lineBuffer);
      pushUpdate(lineBuffer, l);
      Serial.print("Values Updated: T:");  
      Serial.print(getTemperature());  
      Serial.print(", H:");  
      Serial.print(getHumidity());  
      Serial.print(", I:");  
      Serial.print(getCurrent());  
      Serial.print(", M:");  
      Serial.println(getMoisture());  
      
    }
  }

  //check switches
  for(int i=0; i<SWITCHES_COUNT; i++) {
    boolean mode = LOW;
    if(EEPROM.read(START_SWITCHES+((i)*SWITCHES_SIZE)) == 2) {
      mode = HIGH;
    } else if(EEPROM.read(START_SWITCHES+((i)*SWITCHES_SIZE)) == 3) {
      for(int k=0; k<SWITCHES_TIMERS; k++) {
        int startHour = EEPROM.read(START_SWITCHES+((i)*SWITCHES_SIZE)+1+(6*k)+0);
        int startMinute = EEPROM.read(START_SWITCHES+((i)*SWITCHES_SIZE)+1+(6*k)+1);
        int startSecond = EEPROM.read(START_SWITCHES+((i)*SWITCHES_SIZE)+1+(6*k)+2);
        int endHour = EEPROM.read(START_SWITCHES+((i)*SWITCHES_SIZE)+1+(6*k)+3);
        int endMinute = EEPROM.read(START_SWITCHES+((i)*SWITCHES_SIZE)+1+(6*k)+4);
        int endSecond = EEPROM.read(START_SWITCHES+((i)*SWITCHES_SIZE)+1+(6*k)+5);

        long start = ((long)startHour*60*60) + ((long)startMinute*60) + (long)startSecond;
        long end = ((long)endHour*60*60) + ((long)endMinute*60) + (long)endSecond;
        long secondDay = ((long)hour()*60*60) + ((long)minute()*60) +(long)second();
        if(start <= secondDay && end >= secondDay) {
          mode = HIGH;
        }
      }
    }
    if(bitRead(PORTD,i + SWITCHES_INDEX) != mode) {
      if(mode) {
        digitalWrite(i + SWITCHES_INDEX, HIGH);
      } 
      else {
        digitalWrite(i + SWITCHES_INDEX, LOW);
      }
      int l = jsonSwitch(lineBuffer, i);
      pushUpdate(lineBuffer, l);
    }
  }


  EthernetClient client = server.available();
  int linePosition = 0;
  boolean parseBody = false;
  if (client) {
    // an http request ends with a blank line
    boolean currentLineIsBlank = true;
    // Serial.println("start http parse");
    HTTP_METHOD httpMethod;
    HTTP_REQUEST_URI httpUri = NONE;

    while (client.connected()) {
      if (client.available()) {
        char c = client.read();
        if(parseBody) {
          // Serial.print(c);
          lineBuffer[linePosition++] = c;          
        } 
        else {
          if (c == '\n' && currentLineIsBlank) {
            // send a standard http response header
            // Serial.println("end http parse");
            parseBody = true;
          }
          if (c == '\n') {
            if (strstr_P(lineBuffer, PSTR("GET /config")) != 0) {
              httpMethod = GET; 
              httpUri = CONFIG;
            } 
            else if (strstr_P(lineBuffer, PSTR("PUT /config")) != 0) {
              httpMethod = PUT; 
              httpUri = CONFIG;
            } 
            else if (strstr_P(lineBuffer, PSTR("GET /status")) != 0) {
              httpMethod = GET; 
              httpUri = STATUS;
            } else if (strstr_P(lineBuffer, PSTR("GET /calibre")) != 0) {
              httpMethod = GET; 
              httpUri = CALIBRE;
            } else if (strstr_P(lineBuffer, PSTR("GET /switch/")) != 0) {
              httpMethod = GET; 
              httpUri = SWITCH;
              switchNumber = lineBuffer[12]-'0';
            } else if (strstr_P(lineBuffer, PSTR("PUT /switch")) != 0) {
              httpMethod = PUT; 
              httpUri = SWITCH;
            } else if (strstr_P(lineBuffer, PSTR("GET /switch")) != 0) {
              httpMethod = GET; 
              httpUri = SWITCH_STATUS;
            }        

            currentLineIsBlank = true;
            linePosition = 0;
            memset(&lineBuffer, 0, HTTP_LINE_BUFFER);

          } 
          else if (c != '\r') {
            currentLineIsBlank = false;
            lineBuffer[linePosition++] = c;
          }
        }
      } 
      else { //all request bytes received
        parseResponse(client, httpMethod, httpUri, lineBuffer, linePosition);
        break;
      }
    }
    // Serial.println(" ... close");
    delay(1);
    client.stop();
  }
  delay(500);
}

void parseResponse(EthernetClient client, HTTP_METHOD httpMethod, HTTP_REQUEST_URI httpUri, char* lineBuffer, int l) {
  int length = 0;
  if(httpUri == NONE) {
    client.println("HTTP/1.1 404 Not Found");
    client.println("Content-Type: text/json");
    client.println();
    client.println("{\"status\":404, \"message\":\"File Not Found!\"}");

  } else {
    if (httpMethod == GET && httpUri == CONFIG) {
      length = config(lineBuffer);
    } else if (httpMethod == PUT && httpUri == CONFIG) {
      parseConfig(lineBuffer, l);
    } 
    //    else if (httpMethod == GET && httpUri == STATUS) {
    //      length = jsonStatus(lineBuffer);
    //    } 
    else if (httpMethod == GET && httpUri == CALIBRE) {
      length = jsonCalibre(lineBuffer);
    } else if (httpMethod == GET && httpUri == SWITCH) {
      length = jsonSwitch(lineBuffer, switchNumber);
    } else if (httpMethod == PUT && httpUri == SWITCH) {
      parseSwitch(lineBuffer, l);
    } else if (httpMethod == GET && httpUri == SWITCH_STATUS) {
      length = jsonSwitches(lineBuffer);
    }
    client.println("HTTP/1.1 200 OK");
    client.println("Content-Type: application/json");
    client.print("Content-Length: ");
    client.println(String(length, DEC));
    client.println();
    client.println(lineBuffer);
  }
}

/* 
 *    Sensor Utilities
 * 
 */

DHT dht(PIN_DHT, DHTTYPE);
EnergyMonitor energyMonitor;
float t, h, lastTemp, lastHum;
double Irms, lastIrms;
int moisture, lastMoisture;

void initSensors() {
  //Setup DHT Sensor
  dht.begin();
  //Setup Energy Monitor
  energyMonitor.current(PIN_EMON, 4); // XXX this is the value to config
}
void readValues() {
  h = dht.readHumidity();
  t = dht.readTemperature();
  Irms = energyMonitor.calcIrms(iRms());
  moisture = analogRead(PIN_MOISTURE);
}
boolean valuesUpdated() {
//  if(t == lastTemp && h == lastHum & Irms == lastIrms) {
//    return false;    
//  } 
//  else {
//    lastTemp = t;
//    lastHum = h;
//    lastIrms = Irms;
//    return true;
//  }
  return true;
}
double getCurrent() {
  return Irms;
}
float getHumidity() {
  return h;
}
float getTemperature() {
  return t;
}
int getMoisture() {
  return moisture;
}


/* 
 * Push the Updated Values to the Server.
 * Will only push when the server IP is configured.
 */
void pushUpdate(char* body, int length) {
  byte _serverIp[4];
  serverIp(_serverIp);
  if(_serverIp[0] != 0) {
    EthernetClient client;
    if (client.connect(_serverIp, serverPort())) {
      client.println("POST /org.hydroponics.web-1.0.0/app/update HTTP/1.0");
      client.println("Accept: application/json");
      client.println("Content-Type: application/json");
      client.print("Content-Length: ");
      client.println(length);
      client.println();
      for(int i=0; i<length; i++) {
        client.print(body[i]);
      }
      client.println();
      client.flush();
    } else {
      Serial.println("Connection to Server failed.");
    }
    //Read response and dont care...
    if (client.available()) {
      char c = client.read();
    }
    client.stop();
  }
}


/* 
 *    String Utils
 * 
 */

void parseSwitch(char* buffer, int length) {
  int switchNumber;
  reader.reset();
  reader.load(buffer, 0, length);

  char actName[10], value[10];

  JsonReader::Types type = reader.next(); 
  while(type != JsonReader::END) {
    if(type == JsonReader::NAME) {
      reader.characters(actName);

    } 
    else if(type == JsonReader::VALUE) {
      reader.characters(value);
      if(strcmp(actName, "number")  == 0)  {
        switchNumber = atoi(value);
      } 
      else if(strcmp(actName, "mode")  == 0)  {
        EEPROM.write(START_SWITCHES+((switchNumber-1)*SWITCHES_SIZE), atoi(value));
      } 
    } 
    else if(type == JsonReader::START_ARRAY) {
      int arrayItems = 0;
      do {
        type = reader.next();
        reader.characters(value);
        EEPROM.write(START_SWITCHES+((switchNumber-1)*SWITCHES_SIZE)+1+arrayItems, atoi(value));
        arrayItems ++;
      } 
      while(type != JsonReader::END_ARRAY);
    }
    type = reader.next();
  }
}
void parseConfig(char* buffer, int length) {
  reader.reset();
  reader.load(buffer, 0, length);

  char actName[7], value[17];

  JsonReader::Types type = reader.next(); 
  while(type != JsonReader::END) {
    if(type == JsonReader::NAME) {
      reader.characters(actName);

    } 
    else if(type == JsonReader::VALUE) {
      reader.characters(value);
      if(strcmp(actName, "ip")  == 0)  {
        byte ip[4];
        parseIp(value, sizeof(value), ip);
        EEPROM.write(POSITION_LOCAL_IP, ip[0]);
        EEPROM.write(POSITION_LOCAL_IP+1, ip[1]);
        EEPROM.write(POSITION_LOCAL_IP+2, ip[2]);
        EEPROM.write(POSITION_LOCAL_IP+3, ip[3]);

      } 
      else if(strcmp(actName, "port")  == 0)  {
        int intValue = atoi(value);
        EEPROM.write(POSITION_LOCAL_PORT, (byte(intValue >> 8)));
        EEPROM.write(POSITION_LOCAL_PORT+1, (byte(intValue & 0x00FF)));

      } 
      else if(strcmp(actName, "ntp")  == 0)  {
        byte ip[4];
        parseIp(value, sizeof(value), ip);
        EEPROM.write(POSITION_NTP_SERVER, ip[0]);
        EEPROM.write(POSITION_NTP_SERVER+1, ip[1]);
        EEPROM.write(POSITION_NTP_SERVER+2, ip[2]);
        EEPROM.write(POSITION_NTP_SERVER+3, ip[3]);

      } 
      else if(strcmp(actName, "server")  == 0)  {
        byte ip[4];
        parseIp(value, sizeof(value), ip);
        EEPROM.write(POSITION_SERVER_IP, ip[0]);
        EEPROM.write(POSITION_SERVER_IP+1, ip[1]);
        EEPROM.write(POSITION_SERVER_IP+2, ip[2]);
        EEPROM.write(POSITION_SERVER_IP+3, ip[3]);

      } 
      else if(strcmp(actName, "sport")  == 0)  {
        int intValue = atoi(value);
        EEPROM.write(POSITION_SERVER_PORT, (byte(intValue >> 8)));
        EEPROM.write(POSITION_SERVER_PORT+1, (byte(intValue & 0x00FF)));

      } 
      else if(strcmp(actName, "zone")  == 0)  {
        EEPROM.write(POSITION_TIMEZONE, atoi(value));
      } 
      else if(strcmp(actName, "u")  == 0)  {
        EEPROM.write(POSITION_VOLTAGE, atoi(value));
      } 
      else if(strcmp(actName, "iRms")  == 0)  {
        int intValue = atoi(value);
        EEPROM.write(POSITION_IRMS, (byte(intValue >> 8)));
        EEPROM.write(POSITION_IRMS+1, (byte(intValue & 0x00FF)));
      }
    }
    type = reader.next();
  }
}
void parseIp(char* in, int length, byte* ip) {
  char octet[3];
  int octetPosition = 0, number = 0;
  for(int i=0; i<length; i++) {
    if(in[i] == '.' || in[i] == '\0') {
      ip[octetPosition++] = atoi(octet);
      number = 0;
      octet[0] = 0;
      octet[1] = 0;
      octet[2] = 0;
      if(in[i] == '\0') {
        break;
      }
    } 
    else {
      octet[number++] = in[i];
    }
  }
}

int config(char* buffer) {
  PROGMEM char sLocalIp[] = "{\"ip\":\"";
  PROGMEM char sLocalPort[] = "\", \"port\":";
  PROGMEM char sNtp[] = ", \"ntp\":\"";
  PROGMEM char sServerIp[] = "\", \"server\":\"";
  PROGMEM char sServerPort[] = "\", \"sport\":";
  PROGMEM char sTimezone[] = ", \"zone\":";
  PROGMEM char sVoltage[] = ", \"u\":";
  PROGMEM char sIrms[] = ", \"u\":";
  PROGMEM char sEnd[] = "}";

  int position = copyBytes(buffer, sLocalIp, 0, sizeof(sLocalIp));
  position = addNumber(buffer, EEPROM.read(POSITION_LOCAL_IP), position);
  buffer[position++] = '.';
  position = addNumber(buffer, EEPROM.read(POSITION_LOCAL_IP+1), position);
  buffer[position++] = '.';
  position = addNumber(buffer, EEPROM.read(POSITION_LOCAL_IP+2), position);
  buffer[position++] = '.';
  position = addNumber(buffer, EEPROM.read(POSITION_LOCAL_IP+3), position);
  position = copyBytes(buffer, sLocalPort, position, sizeof(sLocalPort));
  position = addNumber(buffer, port(), position);
  position = copyBytes(buffer, sNtp, position, sizeof(sNtp));
  position = addNumber(buffer, EEPROM.read(POSITION_NTP_SERVER), position);
  buffer[position++] = '.';
  position = addNumber(buffer, EEPROM.read(POSITION_NTP_SERVER+1), position);
  buffer[position++] = '.';
  position = addNumber(buffer, EEPROM.read(POSITION_NTP_SERVER+2), position);
  buffer[position++] = '.';
  position = addNumber(buffer, EEPROM.read(POSITION_NTP_SERVER+3), position);
  position = copyBytes(buffer, sServerIp, position, sizeof(sServerIp));
  position = addNumber(buffer, EEPROM.read(POSITION_SERVER_IP), position);
  buffer[position++] = '.';
  position = addNumber(buffer, EEPROM.read(POSITION_SERVER_IP+1), position);
  buffer[position++] = '.';
  position = addNumber(buffer, EEPROM.read(POSITION_SERVER_IP+2), position);
  buffer[position++] = '.';
  position = addNumber(buffer, EEPROM.read(POSITION_SERVER_IP+3), position);
  position = copyBytes(buffer, sServerPort, position, sizeof(sServerPort));
  position = addNumber(buffer, serverPort(), position);
  position = copyBytes(buffer, sTimezone, position, sizeof(sTimezone));
  position = addNumber(buffer, timezone(), position);
  position = copyBytes(buffer, sVoltage, position, sizeof(sVoltage));
  position = addNumber(buffer, voltage(), position);
  position = copyBytes(buffer, sIrms, position, sizeof(sIrms));
  position = addNumber(buffer, iRms(), position);
  position = copyBytes(buffer, sEnd, position, sizeof(sEnd));
  return position;
}

//int jsonStatus(char* buffer) {
//  PROGMEM char sTime[] = "{\"time\":\"";
//  PROGMEM char sUptime[] = "\", \"uptime\":";
//  PROGMEM char sRam[] = ", \"ram\":";
//  PROGMEM char sProcessorTemp[] = ", \"processorTemp\":";
//  PROGMEM char sTimestatus[] = ", \"timeStatus\":";
//  PROGMEM char sEnd[] = "}";
//
//  int position = copyBytes(buffer, sTime, 0, sizeof(sTime));
//  position = addDigits(buffer, day(), position);
//  buffer[position++] = ':';
//  position = addDigits(buffer, month(), position);
//  buffer[position++] = ':';
//  position = addNumber(buffer, year(), position);
//  buffer[position++] = ' ';
//  position = addDigits(buffer, hour(), position);
//  buffer[position++] = ':';
//  position = addDigits(buffer, minute(), position);
//  buffer[position++] = ':';
//  position = addDigits(buffer, second(), position);  
//  position = copyBytes(buffer, sUptime, position, sizeof(sUptime));
//  position = addNumber(buffer, (now() - startupTime), position);
//  position = copyBytes(buffer, sRam, position, sizeof(sRam));
//  position = addNumber(buffer, freeRam(), position);
//  position = copyBytes(buffer, sProcessorTemp, position, sizeof(sProcessorTemp));
//  position = addNumber(buffer, processorTemp(), position);
//  position = copyBytes(buffer, sTimestatus, position, sizeof(sTimestatus));
//  position = addNumber(buffer, timeStatus(), position);
//  position = copyBytes(buffer, sEnd, position, sizeof(sEnd));
//  return position;
//}
int jsonCalibre(char* buffer) {
  PROGMEM char sTime[] = "{\"time\":\"";
  PROGMEM char sTemp[] = "\", \"temperature\":";
  PROGMEM char sHum[] = ", \"humidity\":";
  PROGMEM char sCur[] = ", \"current\":";
  PROGMEM char sMoist[] = ", \"moisture\":";
  PROGMEM char sEnd[] = "}";

  int position = copyBytes(buffer, sTime, 0, sizeof(sTime));
  position = addDigits(buffer, day(), position);
  buffer[position++] = ':';
  position = addDigits(buffer, month(), position);
  buffer[position++] = ':';
  position = addNumber(buffer, year(), position);
  buffer[position++] = ' ';
  position = addDigits(buffer, hour(), position);
  buffer[position++] = ':';
  position = addDigits(buffer, minute(), position);
  buffer[position++] = ':';
  position = addDigits(buffer, second(), position);  
  position = copyBytes(buffer, sTemp, position, sizeof(sTemp));
  position = addFloat(buffer, getTemperature(), position);
  position = copyBytes(buffer, sHum, position, sizeof(sHum));
  position = addFloat(buffer, getHumidity(), position);
  position = copyBytes(buffer, sCur, position, sizeof(sCur));
  position = addFloat(buffer, getCurrent(), position);
  position = copyBytes(buffer, sMoist, position, sizeof(sMoist));
  position = addFloat(buffer, getMoisture(), position);
  position = copyBytes(buffer, sEnd, position, sizeof(sEnd));

  return position; 
}
int jsonSwitches(char* buffer) {
  PROGMEM char sNumber[] = "{\"number\":";
  PROGMEM char sMode[] = ", \"mode\":";
  PROGMEM char sStatus[] = ", \"status\":";
  PROGMEM char sEnd[] = "}";

  int position = 0;
  buffer[position++] = '[';
  for(int i=0; i<SWITCHES_COUNT; i++) {
    if(i > 0) {
      buffer[position++] = ',';
    }
    position = copyBytes(buffer, sNumber, position, sizeof(sNumber));
    position = addNumber(buffer, i+1, position);
    position = copyBytes(buffer, sMode, position, sizeof(sMode));
    position = addNumber(buffer, EEPROM.read(START_SWITCHES+((i)*SWITCHES_SIZE)), position);
    position = copyBytes(buffer, sStatus, position, sizeof(sStatus));
    position = addNumber(buffer, bitRead(PORTD,i+SWITCHES_INDEX), position);
    position = copyBytes(buffer, sEnd, position, sizeof(sEnd));
  }
  buffer[position++] = ']';
  return position; 
}

int jsonSwitch(char* buffer, int number) {
  PROGMEM char sNumber[] = "{\"number\":";
  PROGMEM char sMode[] = ", \"mode\":";
  PROGMEM char sStatus[] = ", \"status\":";
  PROGMEM char sTimer[] = ", \"timer\":[";
  PROGMEM char sEnd[] = "]}";

  int position = copyBytes(buffer, sNumber, 0, sizeof(sNumber));
  position = addNumber(buffer, number, position);
  position = copyBytes(buffer, sMode, position, sizeof(sMode));
  position = addNumber(buffer, EEPROM.read(START_SWITCHES+((number-1)*SWITCHES_SIZE)), position);
  position = copyBytes(buffer, sStatus, position, sizeof(sStatus));
  position = addNumber(buffer, bitRead(PORTD,number-1), position);
  position = copyBytes(buffer, sTimer, position, sizeof(sTimer));
  for(int i=0; i<(SWITCHES_TIMERS*6); i++) {
    if(i>0) {
      buffer[position++] = ',';
    }
      position = addNumber(buffer, EEPROM.read(START_SWITCHES+((number-1)*SWITCHES_SIZE)+1+i), position);
  }
  position = copyBytes(buffer, sEnd, position, sizeof(sEnd));
  return position; 
}

int copyBytes(char* target, char* source, int p, int l) {
  for(int i=0; i<l-1; i++) {
    target[p+i] = source[i];
  }
  return p+l-1;
}
int addDigits(char* target, byte number, int p) {
  char dChar[3];
  itoa(number, dChar, 10);
  if(number < 10) {
    target[p] = '0';
    target[p+1] = dChar[0];
  } 
  else {
    target[p] = dChar[0];
    target[p+1] = dChar[1];
  }
  return p + 2;
}
int addNumber(char* target, int number, int p) {
  char dChar[6];
  itoa(number, dChar, 10);
  int length = 0;
  for(int i=0; i<sizeof(dChar); i++) {
    if(dChar[i] == '\0') {
      break;
    } 
    else {
      target[p+i] = dChar[i];
      length++;
    }
  }
  return p + length;
}
int addFloat(char* target, float number, int p) {
  char dChar[10];
  dtostrf(number, 1, 2, dChar);
  int length = 0;
  for(int i=0; i<sizeof(dChar); i++) {
    if(dChar[i] == '\0') {
      break;
    } 
    else {
      target[p+i] = dChar[i];
      length ++;
    }
  }
  return p + length;
}


/*
 * Properties for configuration
 */

void getIp() {
  _ip[0] = EEPROM.read(POSITION_LOCAL_IP);
  _ip[1] = EEPROM.read(POSITION_LOCAL_IP+1);
  _ip[2] = EEPROM.read(POSITION_LOCAL_IP+2);
  _ip[3] = EEPROM.read(POSITION_LOCAL_IP+3);
}
int port() {
  return int(EEPROM.read(POSITION_LOCAL_PORT) << 8) + int(EEPROM.read(POSITION_LOCAL_PORT+1));
}
void serverIp(byte* _serverIp) {
  _serverIp[0] = EEPROM.read(POSITION_SERVER_IP);
  _serverIp[1] = EEPROM.read(POSITION_SERVER_IP+1);
  _serverIp[2] = EEPROM.read(POSITION_SERVER_IP+2);
  _serverIp[3] = EEPROM.read(POSITION_SERVER_IP+3);
}
int serverPort() {
  return int(EEPROM.read(POSITION_SERVER_PORT) << 8) + int(EEPROM.read(POSITION_SERVER_PORT+1));
}
void timeserver(byte* _timeserver) {
  _timeserver[0] = EEPROM.read(POSITION_NTP_SERVER);
  _timeserver[1] = EEPROM.read(POSITION_NTP_SERVER+1);
  _timeserver[2] = EEPROM.read(POSITION_NTP_SERVER+2);
  _timeserver[3] = EEPROM.read(POSITION_NTP_SERVER+3);
}
int timezone() {
  return EEPROM.read(POSITION_TIMEZONE);
}
int voltage() {
  return EEPROM.read(POSITION_VOLTAGE);
}
int iRms() {
  return int(EEPROM.read(POSITION_IRMS) << 8) + int(EEPROM.read(POSITION_IRMS+1));
} 

/* 
 *    String Utils
 * 
 */
String digits(int number) {
  String result = "";
  if(number < 10) {
    result += "0";
  }
  result += String(number);
  return result;
}
String parseIP(byte* buffer) {
  String result = "";
  for(int i=0; i<4; i++) {
    if(i>0) {
      result += ".";
    }
    result += buffer[i];
  }
  return result;
}

/* 
 *    Misc Utils
 * 
 */

int freeRam () {
  extern int __heap_start, *__brkval; 
  int v; 
  return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval); 
}

/* 
 *    NTP Time Methods
 * 
 */

time_t processSyncMessage() {
  boolean success = false;
  
  while(!success) {
    Serial.println("Get Time from NTP Server...");
    byte ts[4];
    timeserver(ts);
    IPAddress timeserverIp = IPAddress(ts);

    sendNTPpacket(timeserverIp); // send an NTP packet to a time server
  
    // wait to see if a reply is available
    delay(2000);  
    
    int packetSize = Udp.parsePacket();
    if(packetSize) {
      // We've received a packet, read the data from it
      Udp.read(packetBuffer,NTP_PACKET_SIZE);  // read the packet into the buffer
  
      //the timestamp starts at byte 40 of the received packet and is four bytes,
      // or two words, long. First, esxtract the two words:
  
      unsigned long highWord = word(packetBuffer[40], packetBuffer[41]);
      unsigned long lowWord = word(packetBuffer[42], packetBuffer[43]);  
      // combine the four bytes (two words) into a long integer
      // this is NTP time (seconds since Jan 1 1900):
      unsigned long secsSince1900 = highWord << 16 | lowWord;  
      // now convert NTP time into everyday time:
      // Unix time starts on Jan 1 1970. In seconds, that's 2208988800:
      const unsigned long seventyYears = 2208988800UL;     
      // subtract seventy years:
      unsigned long epoch = secsSince1900 - seventyYears;  
      epoch += (timezone() * 60 * 60);
      success = true;
      return epoch;
    } else {
      Serial.println("Failed to Parse UDP Response!");
      delay(5000);
    }
  }
}
// send an NTP request to the time server at the given address 
unsigned long sendNTPpacket(IPAddress& address) {
  // set all bytes in the buffer to 0
  memset(packetBuffer, 0, NTP_PACKET_SIZE); 
  // Initialize values needed to form NTP request
  // (see URL above for details on the packets)
  packetBuffer[0] = 0b11100011;   // LI, Version, Mode
  packetBuffer[1] = 0;     // Stratum, or type of clock
  packetBuffer[2] = 6;     // Polling Interval
  packetBuffer[3] = 0xEC;  // Peer Clock Precision
  // 8 bytes of zero for Root Delay & Root Dispersion
  packetBuffer[12]  = 49; 
  packetBuffer[13]  = 0x4E;
  packetBuffer[14]  = 49;
  packetBuffer[15]  = 52;

  // all NTP fields have been given values, now
  // you can send a packet requesting a timestamp: 		   
  Udp.beginPacket(address, 123); //NTP requests are to port 123
  Udp.write(packetBuffer,NTP_PACKET_SIZE);
  Udp.endPacket(); 
}



