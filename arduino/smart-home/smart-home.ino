#include <RTClib.h>
#define NUM_READINGS 20
//SD
#include <SD.h>
#include <SPI.h>
#include "bb_LED.h"
#define CURRENT_SENSOR  A0
#define BATTERY_VOLTAGE A1
#define SOLAR_VOLTAGE   A2

//int readInterval = 60000;

unsigned long readInterval = 60000;


RTC_DS3231 rtc; // Real Time Clock object


int CS_PIN = 4;

const int numReadings = 10;
float currentReadings[numReadings];      // the readings from the analog input
float battReadings[numReadings];      // the readings from the analog input
float solarReadings[numReadings];      // the readings from the analog input

float currentTotal = 0.0;
float battTotal = 0.0;
float solarTotal = 0.0;


int index = 0;                  // the index of the current reading

float currentValue = 0;
float solarValue = 0;
float battValue = 0;

unsigned long timestamp;

int leds[] = {3, 5, 6, 9, 10};
int num_leds = 5;


char dateLogFile[23];

char datalog[] = "datalog.txt";

String inText;


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


void setup()
{
  Serial.begin(9600);

  // initialise real time clock
  rtc.begin();

  updateFileName();

  // fname = temp;

  //initialise SD card
  initializeSD();

  for (int i = 0; i < numReadings; i++) {
    currentReadings[i] = 0;
    battReadings[i] = 0;
    solarReadings[i] = 0;
  }

  for(int i =0; i < num_leds; i++){
    pinMode(leds[i], OUTPUT);
  }

  timestamp = millis() - readInterval + 1000;
}

void updateFileName() {
  String filename = "16_" + String(rtc.now().month())
                    + "_" + String(rtc.now().day())
                    + ".txt";

  int len = filename.length() + 1;
  //  char temp[len];
  filename.toCharArray(dateLogFile, len);

}

void readCurrentSensor() {
  currentTotal = currentTotal - currentReadings[index];
  currentReadings[index] = analogRead(CURRENT_SENSOR); //Raw data reading
  //Data processing:510-raw data from analogRead when the input is 0;
  // 5-5v; the first 0.04-0.04V/A(sensitivity); the second 0.04-offset val;
  currentReadings[index] = (currentReadings[index] - 512) * 5 / 1024 / 0.04 - 0.04;

  currentTotal = currentTotal + currentReadings[index];
  currentValue = currentTotal / numReadings; //Smoothing algorithm (http://www.arduino.cc/en/Tutorial/Smoothing)

  if (abs(currentValue) < 0.3) {
    currentValue = 0.0;
  }

}

void readSolarVoltage() {
  solarTotal = solarTotal - solarReadings[index];
  solarReadings[index] = analogRead(SOLAR_VOLTAGE); //Raw data reading

  solarReadings[index] = (solarReadings[index] / 1024) * 19.7;

  solarTotal = solarTotal + solarReadings[index];
  solarValue = solarTotal / numReadings; //Smoothing algorithm (http://www.arduino.cc/en/Tutorial/Smoothing)

}


void readBatteryVoltage() {

  battTotal = battTotal - battReadings[index];
  battReadings[index] = analogRead(BATTERY_VOLTAGE); //Raw data reading

  battReadings[index] = (battReadings[index] / 1024) * 19.7;

  battTotal = battTotal + battReadings[index];
  battValue = battTotal / numReadings; //Smoothing algorithm (http://www.arduino.cc/en/Tutorial/Smoothing)

}

void writeToSD(char *fname, String data) {


  //  Serial.println(fname);
  File dataFile = SD.open(fname, FILE_WRITE);
  //File dataFile = SD.open("datalog.txt", FILE_WRITE);

  // if the file is available, write to it:
  if (dataFile) {
    dataFile.print(data);
    dataFile.close();
    // print to the serial port too:
    //Serial.println(data);
  }
  else {
    Serial.println("failed to write sd");
    Serial.println(data);
  }
}

void readFromSD() {
  File dataFile = SD.open(datalog, FILE_READ);
  Serial.print("#D");
  if (dataFile) {
    while (dataFile.available()) {
      Serial.write(dataFile.read());//read until nothing is left
    }
    dataFile.close();
  }
  Serial.println("~");
}

void clearLog() {
  if (SD.exists(datalog)) {
    SD.remove(datalog);
    Serial.println("#COK~");
  }
}

void updateLED(int ledNum, int brightness) {
  if (ledNum > 0 && ledNum <= num_leds) {

    analogWrite(leds[ledNum -1], brightness);
     
    //Echo back 
    Serial.print("#L");
    Serial.print(ledNum);
    Serial.print("+");
    Serial.print(brightness);
    Serial.println("~");


  }else{
    Serial.print("#L");
    Serial.print(ledNum);
    Serial.println("+ERROR~");
    
  }

}

void checkSerial() {

  char ard_command = 0;
  int pin_num = 0;
  int pin_value = 0;
  char dv = '+';

  char get_char = ' ';  //read serial
  // wait for incoming data
  if (Serial.available() < 1) return; // if serial empty, return to loop().

  // parse incoming command start flag
  get_char = Serial.read();
  if (get_char != '#') return; // if no command start flag, return to loop().

  // parse incoming command type
  ard_command = Serial.read(); // read the command

  switch (ard_command) {
    case 'L':
      pin_num = Serial.parseInt();
      dv = Serial.read();
      pin_value = Serial.parseInt();
      //      Serial.print("LED #");
      //      Serial.print(pin_num);
      //      Serial.print(" brightness:");
      //      Serial.println(pin_value);
      updateLED(pin_num, pin_value);

      break;
    case 'R':
      Serial.print("#R");
      Serial.print(battValue);
      Serial.print("+");
      Serial.print(currentValue);
      Serial.println("~");

      break;
    case 'D':
      readFromSD();
      break;
    case 'C':
      clearLog();
      break;
    default:
      Serial.print("#Bad Command: ");
      Serial.println(ard_command);
      break;

  }

}


void loop()
{
  while (millis() - timestamp < readInterval) {
    readCurrentSensor();
    readSolarVoltage();
    readBatteryVoltage();

    //update index
    index = index + 1;

    if (index >= numReadings)
      index = 0;

    checkSerial();

    //Sleep a little while between readings
    delay(30);
  }

  String ts =  String(rtc.now().unixtime());
  String data1 = String(currentValue);
  String data2 = String(battValue);
  String data3 = String(solarValue);

  inText = ts + "+" + data1 + "+" + data2 + "|";
  writeToSD(datalog, inText);

  updateFileName();
  inText = ts + "\t" + data1 + "\t" + data2 + "\t" + data3 + "\n";
  writeToSD(dateLogFile, inText);

  timestamp = millis();



}
