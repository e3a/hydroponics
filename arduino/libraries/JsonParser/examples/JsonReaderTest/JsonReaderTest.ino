#include <MemoryFree.h>
#include <JsonReader.h>

JsonReader reader;
int failures = 0;

int result;
boolean test;

void setup() {
  Serial.begin(9600);
  delay(3000);

  char buffer[ ] = "{\"abc\":\"1234\", \"def\":\"4567\", \"ghi\":\"78{}[],:9\", \"jkl\":\"123\"123\", \"lmn\":123}";

  char actName[10];
  char value[10];

  reader.reset();
  reader.load(buffer, 0, sizeof(buffer));
  JsonReader::Types type = reader.next(); 
  while(type != JsonReader::END) {
    if(type == JsonReader::NAME) {
      reader.characters(actName);
    } else if(type == JsonReader::VALUE) {
      reader.characters(value);
      if(result == 0) {
        if(strcmp(actName, "abc")  != 0) {
          test = false;          
        } else test = true;
        if(strcmp(value, "1234") != 0) {
          test = false;          
        } else test = true;

      }  else if(result == 1) {
        if(strcmp(actName, "def")  != 0) {
          test = false;          
        } else test = true;
        if(strcmp(value, "4567") != 0) {
          test = false;          
        } else test = true;

      } else if(result == 2) {
        if(strcmp(actName, "ghi") == 0) {
          test = false;          
        } else test = true;
        if(strcmp(value, "78{}[],:9") != 0) {
          test = false;          
        } else test = true;

      } else if(result == 3) {
        if(strcmp(actName, "jkl") == 0) {
          test = false;          
        } else test = true;
        if(strcmp(value, "123\"123") != 0) {
          test = false;          
        } else test = true;

      } else if(result == 4) {
        if(strcmp(actName, "lmn") == 0) {
          test = false;          
        } else test = true;
        if(strcmp(value, "123") != 0) {
          test = false;          
        } else test = true;

      } else if(result == 5) {
        if(strcmp(actName, "opq") == 0) {
          test = false;          
        } else test = true;
        if(strcmp(value, "456") != 0) {
          test = false;          
        } else test = true;
      }   
      
      if(test) {
        Serial.print(actName);
        Serial.print("=");
        Serial.println(value);
      } else {
        failures ++;
        Serial.print("! ");
        Serial.print(actName);
        Serial.print("=");
        Serial.println(value);
      }
      result ++;
    }
    type = reader.next();
  }
  Serial.println();
  Serial.print("Failures:");
  Serial.println(failures);
  Serial.println();
  Serial.print("freeMemory()=");
  Serial.println(freeMemory());
}

void loop() {
}


