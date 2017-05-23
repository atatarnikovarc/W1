@echo off

if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome

set CLASSPATH=%JAVA_HOME%/lib/tools.jar;%LOG_MONITORF%/lib/ant.jar
set CLASSPATH=%CLASSPATH%;%LOG_MONITORF%/lib/ant-launcher.jar
set CLASSPATH=%CLASSPATH%;%LOG_MONITORF%/lib/ant-junit.jar
set CLASSPATH=%CLASSPATH%;%LOG_MONITORF%/lib/ant-trax.jar
set CLASSPATH=%CLASSPATH%;%LOG_MONITORF%/lib/junit-3.8.1.jar
set CLASSPATH=%CLASSPATH%;%LOG_MONITORF%/config

"%JAVA_HOME%\bin\java.exe" -Xmx128m -Xms128m -classpath %CLASSPATH% org.apache.tools.ant.Main  %1 %2 %3 %4 %5
goto end

:noJavaHome
echo "JAVA_HOME\bin\java.exe not found: please set JAVA_HOME"

:end