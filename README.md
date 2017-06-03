A simple, dependency-free utility that generates Finnish headlines procedurally using data from [YLE Uutiset](http://yle.fi/uutiset). 

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
