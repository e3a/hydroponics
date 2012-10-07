/*
 * Config.cpp - Library for the hydroponics controller.
 * 
 * TODO: The library does not return the end Map type at the end
 */

#include <stdio.h>
#include "Arduino.h"
#include "JsonReader.h"

JsonReader::JsonReader() {}

JsonReader::Types JsonReader::next() {
  position ++;
  for(; position<length; position++) {
    if(! collectCharacters && buffer[position] == '{') {
      return START_MAP;
    } else if(! collectCharacters && buffer[position] == '}') {
      if(myType == VALUE) {
	myType = NONE;
	return VALUE;
      }
      return END_MAP;
    } else if(! collectCharacters && buffer[position] == '[') {
      dot = position;
      myType = VALUE_ARRAY;
      return START_ARRAY;
    } else if(! collectCharacters && buffer[position] == ']') {
      return END_ARRAY;
    } else if(buffer[position] == '"') {
      if(textStart == -1) {
	textStart = position;
	collectCharacters = true;
      } else {
	textEnd = position;
	collectCharacters = false;
      }
    } else if(! collectCharacters && buffer[position] == ':') { //does it need the collectCharacters check?
      dot = position;
      myType = VALUE;
      return NAME;
    } else if(! collectCharacters && buffer[position] == ',') {
      if(myType == VALUE) {
	myType = NONE;
	return VALUE;
      } else {
	myType = VALUE_ARRAY;
	return VALUE_ARRAY;
      }
    }
  }
  return END;
}

void JsonReader::reset() {
  textStart = -1;
  position = -1;
}

void JsonReader::load(char* b, int s, int l) {
 position = s - 1; // we decrement because in the begining of the loop we invrement again (WAH)
 start = s;
 length = l;
 buffer = b;
}

void JsonReader::characters(char* b) {
  int s, e;
  if(textStart == -1) {
    s = dot;
    e = position;
  } else {
    s = textStart;
    e = textEnd;
  }
  
  for(int i=0; i<(e-s-1); i++) {
     b[i] = buffer[i+s+1];
  }
  b[e-s-1] = '\0';
  textStart = -1; textEnd = 0;
  if(myType == VALUE_ARRAY) {
    dot = position;
  }
}
