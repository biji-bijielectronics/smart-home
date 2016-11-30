SmartHome App Bluetooth Protocol
Introduction
This document defines the protocol used to communicate between the Arduino and the Android application with a bluetooth serial link.
Protocol
The protocol is request-reply from the Android App to the Arduino. As in the App sends commands to the Arduino and the arduino responds.


Each packet starts with # and ends with ~
The first character after the # denotes the function to perform
Commands
L - LEDs 
#L<LED Number ><Brightness>~


Responds with:
Echoes back response if successful
#L<LED Number>+<Brightness>~


LEDs number from 1 - 6
Brightness in range 0-255
Example
 #L1+255~ mean switch LED 1 to 255 brightness
R - Read latest data 
#R~
Responds with:
#R<Voltage>+<Current>~
Example
 #R~ 
Responds with:
#R6.60+0.4~ - means Voltages 6.60V, current 0.4A
L - Retrieve data stored in log files on SD CARD
#L~
Responds with:
#R<Timestamp>+<Voltage>+<Current>|<Timestamp>+<Voltage>+<Current>|.....~


It will send each data set separated by | for each row in the log file


C - Clear log files on SD CARD
#C~
Responds with:
#COK~