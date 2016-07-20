
rm *.class
rm plugin.jar
javac -cp ..\lib\javatracer.jar;. *.java
jar -cvf plugin.jar *.class META-INF
