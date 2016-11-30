

#define START_CMD_CHAR '*'
#define END_CMD_CHAR '#'
#define DIV_CMD_CHAR '|'
#define CMD_DIGITALWRITE 10
#define CMD_ANALOGWRITE 11
#define CMD_TEXT 12
#define CMD_READ_ARDUDROID 13
#define MAX_COMMAND 20  // max command number code. used for error checking.
#define MIN_COMMAND 10  // minimum command number code. used for error checking. 
#define IN_STRING_LENGHT 40
#define MAX_ANALOGWRITE 255
#define PIN_HIGH 3
#define PIN_LOW 2


//RTC
#define NUM_READINGS 20
//SD
#include <SD.h>
//  #include <SPI.h>

//RTC_DS3231 rtc; // Real Time Clock object


int CS_PIN = 4;
int interval = 2000;
int Anlg1 = A0;
int Anlg2 = A1;
int Anlg3 = A2;

String inText;

char filename[] = "data.log";

void initializeSD()
{
  Serial.println("Initializing SD card...");
  pinMode(CS_PIN, OUTPUT);

  if (SD.begin())
  {
    Serial.println("SD card is ready to use.");
  } else
  {
    Serial.println("SD card initialization failed");
    return;
  }
}


// the setup routine runs once when you press reset:
void setup() {
  // initialize serial communication at 9600 bits per second:
  Serial.begin(9600);

  // initialise real time clock
  //  rtc.begin();

  //initialise SD card
  initializeSD();


}


void readFromSD() {
  File dataFile = SD.open(filename, FILE_READ);

  if (dataFile) {
    while (dataFile.available()) {
      Serial.write(dataFile.read());//read until nothing is left
    }
    dataFile.close();
  }
}

void writeToSD(String data) {


  File dataFile = SD.open(filename, FILE_WRITE);

  // if the file is available, write to it:
  if (dataFile) {
    dataFile.println(data);
    dataFile.close();
    // print to the serial port too:
    Serial.println(data);
  }

}

void logData() {
  String data1 = String(analogRead(Anlg1));
  String data2 = String(analogRead(Anlg2));
  String data3 = String(analogRead(Anlg3));

  String text = String(millis() / 1000) + "\t" + data1 + "\t" + data2 + "\t" + data3;
  writeToSD(text);

}

void clearLog() {
  if (SD.exists(filename)) {
  SD.remove(filename);
    Serial.println("Log Cleared!");
  }
}

void loop() {

  char get_char = ' ';  //read serial

  // wait for incoming data
  if (Serial.available() < 1) return; // if serial empty, return to loop().

  get_char = Serial.read();
  //  if (get_char != START_CMD_CHAR) return; // if no command start flag, return to loop().

  switch (get_char) {
    case 'a':
      Serial.println("helloworld");

      break;
    case 'b':
      readFromSD();
      break;

    case 'l':
      logData();
      break;

    case 'c':
      clearLog();
      break;


  }


}


