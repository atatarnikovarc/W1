#!/bin/bash

CLASSPATH=$JAVA_HOME/lib/tools.jar:$LOG_MONITOR/lib/ant.jar
CLASSPATH=$CLASSPATH:$LOG_MONITOR/lib/ant-launcher.jar
CLASSPATH=$CLASSPATH:$LOG_MONITOR/lib/ant-junit.jar
CLASSPATH=$CLASSPATH:$LOG_MONITOR/lib/ant-trax.jar
CLASSPATH=$CLASSPATH:$LOG_MONITOR/lib/junit-3.8.1.jar
CLASSPATH=$CLASSPATH:$LOG_MONITOR/config

if [ -x "$JAVA_HOME/bin/java" ] ; then
  "$JAVA_HOME/bin/java" -Xmx1280m -Xms128m -classpath $CLASSPATH org.apache.tools.ant.Main  $1 $2 $3 $4 $5
else
  echo "JAVA_HOME/bin/java not found: please set JAVA_HOME"
fi
