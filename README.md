A simple, dependency-free utility that generates Finnish headlines procedurally using data from [YLE Uutiset](http://yle.fi/uutiset), [Iltasanomat](http://is.fi), [Iltalehti](http://iltalehti.fi), [HS](https://www.hs.fi/), [Hymy](https://hymy.fi), [MTV](https://www.mtv.fi) and [Seiska](https://www.seiska.fi/), . Also contains Python code for running Twitter and Telegram bots that post a headline hourly.

## Building

Build the .jar file:

    ./build.sh

## Usage

This program manages a file called `cached_headlines` in the folder in which it is run to make the process faster. Update or create it like this:

    java -jar ylekov.jar update

To print a headline to the standard output stream, do this:

    java -jar ylekov.jar generate

To print `n` headlines, run this:

    java -jar ylekov.jar generate n

To test the bot, run

    ./bot.py test

and it will print out a sample headline. To run the bot, run

    ./bot.py run

Note that the bot requires `tweepy` and `python-telegram-bot`. Store the required keys and tokens in a file called `secrets.py`.
