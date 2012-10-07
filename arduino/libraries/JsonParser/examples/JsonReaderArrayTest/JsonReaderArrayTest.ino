#include <MemoryFree.h>
#include <JsonReader.h>

JsonReader reader;
int failures = 0;

int result;
boolean test;

void setup() {
  Serial.begin(9600);
  delay(3000);

  char buffer[ ] = "{\"number\":1, \"mode\":0, \"status\":1, \"timer\":[01,02,03,04,05,06]}";

  char actName[10];
  char value[10];

  reader.reset();
  reader.load(buffer, 0, sizeof(buffer));
  JsonReader::Types type = reader.next(); 
  
  while(type != JsonReader::END) {
    if(type == JsonReader::NAME) {
      reader.characters(actName);
    } else if(type == JsonReader::START_ARRAY) {
        int arrayItems = 0;
        Serial.print("[");
        do {
          type = reader.next();
          reader.characters(value);

          if(arrayItems > 0) {
            Serial.print(", ");
          }
          Serial.print(value);
          arrayItems ++;
        } while(type != JsonReader::END_ARRAY);
        
        Serial.println("]");
        if(arrayItems != 6) {
          Serial.println("ARRAY not corrent");
          failures ++;
        }
        
    } else if(type == JsonReader::VALUE) {
      Serial.println("VALUE");
      reader.characters(value);
      if(result == 0) {
        if(strcmp(actName, "number")  != 0) {
          test = false;          
        } else test = true;
        if(strcmp(value, "1") != 0) {
          test = false;          
        } else test = true;

      }  else if(result == 1) {
        if(strcmp(actName, "mode")  != 0) {
          test = false;          
        } else test = true;
        if(strcmp(value, "0") != 0) {
          test = false;          
        } else test = true;

      } else if(result == 2) {
        if(strcmp(actName, "status") == 0) {
          test = false;          
        } else test = true;
        if(strcmp(value, "1") != 0) {
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


