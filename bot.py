#!/usr/bin/python3

import tweepy
import subprocess
import sys
from time import sleep
from secrets import *

auth = tweepy.OAuthHandler(consumer_key, consumer_secret);
auth.set_access_token(access_token, access_secret)

api = tweepy.API(auth)

def post():
	subprocess.run(["java", "-jar", "ylekov.jar", "update"])
	result = subprocess.run(["java", "-jar", "ylekov.jar", "generate"], stdout=subprocess.PIPE)
	tweet = result.stdout.decode("utf-8").strip()
	
	api.update_status(tweet)

while True:
	try:
		post()
		sleep(60*60)
	except:
		print("Error: ", sys.exc_info()[0])
		sleep(60)
