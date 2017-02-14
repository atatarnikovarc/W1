@echo off

if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome

set CLASSPATH=%JAVA_HOME%/lib/tools.jar;%RUNAPI%/lib/ant.jar
set CLASSPATH=%CLASSPATH%;%RUNAPI%/lib/ant-launcher.jar
set CLASSPATH=%CLASSPATH%;%RUNAPI%/lib/ant-junit.jar
set CLASSPATH=%CLASSPATH%;%RUNAPI%/lib/ant-trax.jar
set CLASSPATH=%CLASSPATH%;%RUNAPI%/lib/junit-3.8.1.jar
set CLASSPATH=%CLASSPATH%;%RUNAPI%/config

"%JAVA_HOME%\bin\java.exe" -Xmx128m -Xms128m -classpath %CLASSPATH% org.apache.tools.ant.Main  %1 %2 %3 %4 %5
goto end

:noJavaHome
echo "JAVA_HOME\bin\java.exe not found: please set JAVA_HOME"

:end