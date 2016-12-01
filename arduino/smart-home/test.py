import serial
import time
import os
import redis

#strPort = '/dev/ttyACM0'
strPort = '/dev/ttySpruino'

ser = serial.Serial(strPort, 9600)

#redis_cloud = redis.Redis(host='rohbot', password="redisb0x1337")
#redis_pi = redis.Redis(host='raspi')


while 1:
	line = ser.readline()
	#vals = line.strip().split('\t')
	#count = vals[0]
	print line

	ser.write(line)
	#os.system('xdotool click 1')
	#redis_cloud.publish('fb-out', "arduino press " + str(count))
	#redis_pi.publish('socketio', 'blink')
	#redis_pi.publish('socketio', 'button1')
	
	#os.system('xdotool key XF86AudioNext')
	