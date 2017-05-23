set JAVA_HOME=C:\Program Files\Java\jdk1.5.0_11
set path=%JAVA_HOME%\bin;%path%

java -version
java -version

cd ..
cd ./selenium-remote-control-0.9.2-SNAPSHOT/selenium-server-0.9.2-SNAPSHOT/
java -jar selenium-server.jar
pause
