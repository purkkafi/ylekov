javac -sourcepath src -d bin -cp jars/* src/fi/purkka/ylekov/Ylekov.java
jar cfm ylekov.jar MANIFEST.mf -C bin/ .
