#!/usr/bin/python3

import nanoatp
import telegram
import subprocess
import sys
import random
import arrow
import json
import asyncio
from datetime import datetime
from time import sleep
from secrets import *

bot = telegram.Bot(telegram_token)

async def update_cache():
    subprocess.Popen(["java", "-jar", "ylekov.jar", "update"]).wait()

def get_ylekov_classic():
    with open("ylekov_classic.json") as f:
        classic_posts = json.loads(f.read())
    post = random.choice(classic_posts)
    return "klassinen ylekov:\n" + post["text"]

async def get_post():
    if arrow.now('Europe/Helsinki').hour == 18: # ylekov classic
        return get_ylekov_classic()
        
    else: # tavallinen ylekov
        result = subprocess.Popen(["java", "-jar", "ylekov.jar", "generate"], stdout=subprocess.PIPE).communicate()
        tweet = result[0].decode("utf-8").strip()
        return tweet

async def post_bsky(post):
    if is_debug:
        print("[BLUESKY] " + post)
    else:
        bsky = nanoatp.BskyAgent('https://bsky.social')
        bsky.login(bsky_handle, bsky_password)
        
        rich_text = nanoatp.RichText(post)
        rich_text.detectFacets(bsky)
        
        record = { 'text': rich_text.text, 'facets': rich_text.facets, 'langs': ['fi'] }
        
        await bsky.post(record)
        print('[BLUESKY] ', post)

async def post_telegram(post):
    if is_debug:
        print("[TELEGRAM] " + post)
    else:
        await bot.send_message(telegram_channel, post, disable_web_page_preview=True)

def log(message):
    with open("ylekov_log", "a") as file:
        file.write("[" + str(datetime.now()) + "] " + message + "\n")

async def try_and_log(callback, description):
    try:
        return_value = await callback()
        log("Completed action [" + description + "]")
        return return_value
    except:
        log("Failed action [" + description + "]: " + str(sys.exc_info()[:2]))

async def runbot():
    while True:
        await try_and_log(lambda: update_cache(), "update cache")
        post = await try_and_log(lambda: get_post(), "generate post")
        
        await try_and_log(lambda: post_bsky(post), "post to Bluesky: " + post)
        await try_and_log(lambda: post_telegram(post), "post to Telegram: " + post)
        
        await asyncio.sleep(60*60)

mode = sys.argv[1]

if mode == "run":
    is_debug = False
    asyncio.run(runbot())
elif mode == "test":
    is_debug = True
    asyncio.run(runbot())
else:
    print("Invoke with 'run' or 'test'")

