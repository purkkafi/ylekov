#!/usr/bin/python3

import tweepy
import telegram
import subprocess
import sys
from datetime import datetime
from time import sleep
from secrets import *

auth = tweepy.OAuthHandler(consumer_key, consumer_secret);
auth.set_access_token(access_token, access_secret)
api = tweepy.API(auth)

bot = telegram.Bot(telegram_token)

def update_cache():
	subprocess.Popen(["java", "-jar", "ylekov.jar", "update"]).wait()

def get_post():
	result = subprocess.Popen(["java", "-jar", "ylekov.jar", "generate"], stdout=subprocess.PIPE).communicate()
	tweet = result[0].decode("utf-8").strip()
	return tweet
	
def post_twitter(post):
	if is_debug:
		print("[TWITTER] " + post)
	else:
		api.update_status(post)

def post_telegram(post):
	if is_debug:
		print("[TELEGRAM] " + post)
	else:
		bot.send_message(telegram_channel, post, disable_web_page_preview=True)

def log(message):
	with open("ylekov_log", "a") as file:
		file.write("[" + str(datetime.now()) + "] " + message + "\n")

def try_and_log(callback, description):
	try:
		return_value = callback()
		log("Completed action [" + description + "]")
		return return_value
	except:
		log("Failed action [" + description + "]: " + str(sys.exc_info()[:2]))

def runbot():
	while True:
	
		try_and_log(lambda: update_cache(), "update cache")
		post = try_and_log(lambda: get_post(), "generate post")
		
		try_and_log(lambda: post_twitter(post), "post to Twitter: " + post)
		try_and_log(lambda: post_telegram(post), "post to Telegram: " + post)
		
		sleep(60*60)

mode = sys.argv[1]

if mode == "run":
	is_debug = False
	runbot()
elif mode == "test":
	is_debug = True
	runbot()
else:
	print("Invoke with 'run' or 'test'")
