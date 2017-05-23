@echo off

if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome

set CLASSPATH="%JAVA_HOME%"\lib\tools.jar;%DMPTF%\lib\ant.jar
set CLASSPATH=%CLASSPATH%;%DMPTF%\lib\ant-launcher.jar
set CLASSPATH=%CLASSPATH%;%DMPTF%\lib\ant-junit.jar
set CLASSPATH=%CLASSPATH%;%DMPTF%\lib\ant-trax.jar
set CLASSPATH=%CLASSPATH%;%DMPTF%\lib\maven-ant-tasks-2.1.3.jar
set CLASSPATH=%CLASSPATH%;%DMPTF%\config
set CLASSPATH=%CLASSPATH%;%DMPTF%\lib-deps
"%JAVA_HOME%\bin\java.exe" -Xmx128m -Xms128m -classpath %CLASSPATH% org.apache.tools.ant.Main  %1 %2 %3 %4 %5
REM "%JAVA_HOME%\bin\java.exe" -Xmx128m -Xms128m -Denv=1 -classpath %CLASSPATH% org.apache.tools.ant.Main  %1 %2 %3 %4 %5
goto end

:noJavaHome
echo "JAVA_HOME\bin\java.exe not found: please set JAVA_HOME"

:end
