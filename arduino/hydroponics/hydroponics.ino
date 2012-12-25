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

/* ------------------------------------------------------------------------------
   1 Start NTP update
   2 End NTP update
   3 Client Connected 
   4 Client disconnected
   5 Before update values
   6 After client udpate
   ------------------------------------------------------------------------------ */
 
#include <SPI.h>         
#include <Ethernet.h>
#include <EthernetUdp.h>
#include <Wire.h>  
#include <Time.h>
#include <DS1307RTC.h>
#include "DHT.h"
#include "EmonLib.h"
#include <EEPROM.h>

#define DHTTYPE DHT22  
#define PIN_DHT  8     
#define PIN_EMON 3
#define EMON_FACT 4
#define EMON_SAMPLES 1480
#define PIN_MOISTURE 2     
#define SWITCHES_SIZE 50
#define START_SWITCHES 100
#define SWITCHES_TIMERS 2

const int SWITCHES_INDEX = 2;
const int SWITCHES_COUNT = 6;
const unsigned long broadcastIntervall = 60000;
const unsigned long ntpIntervall = 86400000;
const unsigned long currentIntervall = 30000;

const int NTP_PACKET_SIZE= 48;
const unsigned int localPort = 8888;
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
byte packetBuffer[ NTP_PACKET_SIZE]; //buffer to hold incoming and outgoing packets 
byte broadcastServer[] = {255, 255, 255, 255};  
byte timeserver[] = {94,126,19,139};  
int timezone = 1;
boolean debug = true;

EthernetServer server(9997);
EthernetUDP Udp;

IPAddress address(broadcastServer);
DHT dht(PIN_DHT, DHTTYPE);
EnergyMonitor energyMonitor;

void setup() {
  // Open serial communications and wait for port to open:
  Serial.begin(9600);

  // initialize the RTC Clock
  setSyncProvider(RTC.get);

  // initialize the sensors
  //Setup DHT Sensor
  dht.begin();
  //Setup Energy Monitor
  energyMonitor.current(PIN_EMON, EMON_FACT);
  
  // start Ethernet and UDP
  if (Ethernet.begin(mac) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
    // no point in carrying on, so do nothing forevermore:
    for(;;)
      ;
  }
  Udp.begin(localPort);
  server.begin();
  
  //Setup Scheduler
  for(int i=0; i<SWITCHES_COUNT; i++) {
    pinMode(i+SWITCHES_INDEX, OUTPUT); 
  }
}

unsigned long lastBroadcast = 0;
unsigned long lastNtpSync= 0;
unsigned long lastCurrentUpdate= 0;

byte lastTemp, lastHum;
int lastIrms, lastMoisture;

void loop() {
//  Serial.print(day());
//  Serial.print(":");
//  Serial.print(month());
//  Serial.print(":");
//  Serial.print(year());
//  Serial.print(" ");
//  Serial.print(hour());
//  Serial.print(":");
//  Serial.print(minute());
//  Serial.print(":");
//  Serial.println(second());
  
  //check switches
  for(int i=0; i<SWITCHES_COUNT; i++) {
    boolean mode = LOW;
    if(EEPROM.read(START_SWITCHES+((i)*SWITCHES_SIZE)) == 1) { // switch mode on
      mode = HIGH;
    } else if(EEPROM.read(START_SWITCHES+((i)*SWITCHES_SIZE)) == 2) { // switch mode auto
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
      //Broadcast the change
      packetBuffer[0] ='S';
      packetBuffer[1] = i;
      packetBuffer[2] = EEPROM.read(START_SWITCHES+((i)*SWITCHES_SIZE));
      packetBuffer[3] = bitRead(PORTD,i+SWITCHES_INDEX);
    
      Udp.beginPacket(address, 9999);
      Udp.write(packetBuffer,4);
      Udp.endPacket(); 
      
      lastCurrentUpdate = 0;
    }
  }  
  
  //Send Client Broadcast
  if(lastBroadcast == 0 || lastBroadcast + broadcastIntervall < millis()) {
    memset(packetBuffer, 0, NTP_PACKET_SIZE); 
  
    unsigned long _now = now();
    packetBuffer[0] ='C';
    packetBuffer[1] = (byte)((_now >> 24) & 0xff);
    packetBuffer[2] = (byte)((_now >> 16) & 0xff);
    packetBuffer[3] = (byte)((_now >> 8) & 0xff);
    packetBuffer[4] = (byte)(_now & 0xff);
  
    Udp.beginPacket(address, 9999);
    Udp.write(packetBuffer,5);
    Udp.endPacket(); 

    lastBroadcast = millis();
  }

  //Set RTC Clock
  if(lastNtpSync == 0 || lastNtpSync + ntpIntervall < millis()) {
    Serial.println("Update RTC");
    if(debug) { logger(1);}
    time_t t = processSyncMessage();
    if(t > 0) {
      RTC.set(t);   // set the RTC and the system time to the received value
      setTime(t);          
      lastNtpSync = millis();
    }
    if(debug) { logger(2);}
  }
  
  //Read Values
  if(lastCurrentUpdate == 0 || lastCurrentUpdate + currentIntervall < millis()) {
    if(debug) { logger(5);}
    byte h = (dht.readHumidity()+0.5);
    byte t = (dht.readTemperature()+0.5);
    int Irms = ((int)(energyMonitor.calcIrms(EMON_SAMPLES)*10+0.5))*230/10;
    
    int moisture = 0;
    for(int j=0; j<100; j++) {
      moisture += analogRead(PIN_MOISTURE);
    }
    moisture = moisture / 100;
    
    if(t != lastTemp || h != lastHum || Irms != lastIrms || moisture != lastMoisture) {
      lastTemp = t;
      lastHum = h;
      lastIrms = Irms;
      lastMoisture = moisture;
      Serial.print("V ");
      Serial.print(t);
      Serial.print("C, ");
      Serial.print(h);
      Serial.print("%, ");
      Serial.print(Irms);
      Serial.print("W, ");
      Serial.println(moisture);
  
      memset(packetBuffer, 0, NTP_PACKET_SIZE); 
    
      packetBuffer[0] ='V';
      packetBuffer[1] = t;
      packetBuffer[2] = h;
      packetBuffer[3] = (byte(Irms >> 8));
      packetBuffer[4]  = (byte(Irms & 0x00FF)); 
      packetBuffer[5] = (byte(moisture >> 8));
      packetBuffer[6]  = (byte(moisture & 0x00FF)); 
    
      Udp.beginPacket(address, 9999);
      Udp.write(packetBuffer,7);
      Udp.endPacket();
    }
    lastCurrentUpdate = millis();
    if(debug) { logger(6);}
  }
  
  // listen for incoming clients
  EthernetClient client = server.available();
  if (client) {
    if(debug) { logger(3);}
    int readCounter = 0;
    boolean query = false;
    int timeout = 0;
    while (client.connected()) {
      if (client.available()) {
        char c = client.read();
        if(readCounter == 0 && c == '?') {
          query = true;
        } else if(readCounter == 0 && c >= 0 && c < SWITCHES_COUNT) {

          // NUMBER of the switch
          // MODE of the switch
          // size of the schedules (bytes)
          // SCHEDULE the schedule

          EEPROM.write(START_SWITCHES + (SWITCHES_SIZE * c), client.read());
          int size = client.read();
          for(int i=0; i<size; i++) {
            EEPROM.write(START_SWITCHES + (SWITCHES_SIZE * c) + i + 1, client.read());
          }
          
        } else if(readCounter == 1 && c == 'S') {

          // LENGTH of the message
          // for each switch
          // number of the switch
          // mode of the switch
          // status of the switch

          client.write(3 * SWITCHES_COUNT);
          for(int i=0; i<SWITCHES_COUNT; i++) {
            client.write(i);
            client.write(EEPROM.read(START_SWITCHES+((i)*SWITCHES_SIZE)));
            client.write(bitRead(PORTD,i+SWITCHES_INDEX));
          }
        } else if(readCounter == 1 && c == 'C') {
          Serial.println("get config");

          // LENGTH of the message
          // TIMEZONE
          // NTP_SERVER (4 bytes)
          
          client.write(1 + 4);
          client.write(timezone);
          client.write(timeserver[0]);
          client.write(timeserver[1]);
          client.write(timeserver[2]);
          client.write(timeserver[3]);
          
          
        } else if(readCounter == 1 && c >= 0 && c < SWITCHES_COUNT) {
           
          // LENGTH of the message
          // COUNT of schedules
          // MODE of the SWTICH
          // STATUS of the SWITCH
          // START-END (hh:mm:ss) 
          
          client.write(3 + SWITCHES_TIMERS*6);          
          client.write(EEPROM.read(START_SWITCHES+(c*SWITCHES_SIZE)));
          client.write(bitRead(PORTD,c+SWITCHES_INDEX));
          client.write(SWITCHES_TIMERS);
          for(int i=0; i<(SWITCHES_TIMERS*6); i++) {
            client.write(EEPROM.read(START_SWITCHES+(c*SWITCHES_SIZE)+1+i));
          }
        }
        readCounter ++;
      }
    }
    client.flush();
    // give the Client time to receive the data
    delay(1);
    // close the connection:
    client.stop();
    if(debug) { logger(4);}
  }
}

void logger(byte code) {
      packetBuffer[0] ='L';
      packetBuffer[1] = code;
    
      Udp.beginPacket(address, 9999);
      Udp.write(packetBuffer,2);
      Udp.endPacket();
}

time_t processSyncMessage() {
  boolean success = false;
  
  while(!success) {
    IPAddress timeserverIp = IPAddress(timeserver);

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
      epoch += (timezone * 60 * 60);
      success = true;
      return epoch;
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
