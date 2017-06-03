javac -sourcepath src -d bin src/fi/purkka/ylekov/Ylekov.java
jar cfe ylekov.jar fi.purkka.ylekov.Ylekov -C bin/ .
