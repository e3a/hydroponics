/*
 * Config.h - library for the hydroponics controller
 */
#ifndef JsonReader_h
#define JsonReader_h

#include "Arduino.h"

class JsonReader
{
  public:
    JsonReader();
    enum Types { NONE, START, START_MAP, END_MAP, STRING, START_STRING, START_VALUE, NAME, VALUE, END, START_ARRAY, END_ARRAY, VALUE_ARRAY };
    Types next();
    void reset();
    void characters(char* b);
    void load(char* buffer, int s, int l);
private:
   bool collectCharacters;
   char* buffer;
   int start, length, position, textStart, textEnd, dot;
   Types myType;
};

#endif
