@echo off

if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome

set CLASSPATH=c:\projects\PingIdentity\pfceTestFramework\launch\config
rem set CLASSPATH=%JAVA_HOME%/lib/tools.jar

"%JAVA_HOME%\bin\java.exe" -Xmx128m -Xms256m -classpath %CLASSPATH% 
goto end

:noJavaHome
echo "JAVA_HOME\bin\java.exe not found: please set JAVA_HOME"

:end