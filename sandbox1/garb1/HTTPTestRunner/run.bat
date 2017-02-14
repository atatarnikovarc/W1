@echo off

if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome


set CLASSPATH=%JAVA_HOME%/lib/tools.jar
set CLASSPATH=%CLASSPATH%;./lib/junit.jar
set CLASSPATH=%CLASSPATH%;./lib/ant.jar
set CLASSPATH=%CLASSPATH%;./lib/ant-launcher.jar
set CLASSPATH=%CLASSPATH%;./lib/ant-junit.jar
set CLASSPATH=%CLASSPATH%;./lib/ant-trax.jar
set CLASSPATH=%CLASSPATH%;./config

"%JAVA_HOME%\bin\java.exe" -classpath %CLASSPATH% org.apache.tools.ant.Main  %1 %2 %3 %4 %5
goto end

:noJavaHome
echo "JAVA_HOME\bin\java.exe not found: please set JAVA_HOME"

:end