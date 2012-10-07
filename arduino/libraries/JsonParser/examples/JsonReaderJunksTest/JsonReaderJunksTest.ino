#include <JsonReader.h>

JsonReader reader;

String actName;
int result;
boolean test;

int failures = 0;

void setup() {
  Serial.begin(9600);
  delay(3000);

  int resultTypes[] = {
    JsonReader::START_MAP, 
    JsonReader::NAME, JsonReader::VALUE, 
    JsonReader::NAME, JsonReader::VALUE, 
    JsonReader::NAME, JsonReader::VALUE, 
    JsonReader::NAME, JsonReader::VALUE, 
    JsonReader::NAME, JsonReader::VALUE, 
    /* JsonReader::END_MAP */
  };
  int actResults[12];
  int statusPosition = 0;

  char bufferA[ ] = "{\"abc\":\"1234\", \"def\":\"4567\", \"ghi\":";
  char bufferB[ ] = "\"78{}[],:9\", \"jkl\":\"123\"123\", \"lmn\":123}";

  char actName[10];
  char value[10];
  
  int l = 0;

  reader.reset();
  reader.load(bufferA, 0, sizeof(bufferA));
  JsonReader::Types type = reader.next(); 
  while(type != JsonReader::END) {
    actResults[statusPosition++] = type;
    if(type == JsonReader::START_MAP) {
      Serial.println("{");
    } 
    else if(type == JsonReader::END_MAP) {
      Serial.println("}");
    } 
    else if(type == JsonReader::NAME) {
      reader.characters(actName);
    } 
    else if(type == JsonReader::VALUE) {
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
      } 
      else {
        failures ++;
        Serial.print("! ");
        Serial.print(actName);
        Serial.print("=");
        Serial.println(value);
      }
      result ++;
    }
    type = reader.next();
    if(type == JsonReader::END && l == 0) {
      reader.load(bufferB, 0, sizeof(bufferB));
      l ++;
      type = reader.next();
    }
  }

  for(int i=0; i<11; i++) { //XXX have to be 12
    if(actResults[i] != resultTypes[i]) {
      Serial.print("status does not match, position:");
      Serial.print(i);
      Serial.print("; ");
      Serial.print(resultTypes[i]);
      Serial.print(" != ");
      Serial.println(actResults[i]);
      failures ++;
    }
  }

  Serial.println();
  Serial.print("Failures:");
  Serial.println(failures);
}

void loop() {
}



