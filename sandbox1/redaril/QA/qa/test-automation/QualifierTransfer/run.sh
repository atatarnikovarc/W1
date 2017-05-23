#!/bin/bash
CLASSPATH=$JAVA_HOME/lib/tools.jar:$QTF/lib/ant.jar
CLASSPATH=$CLASSPATH:$QTF/lib/ant-launcher.jar
CLASSPATH=$CLASSPATH:$QTF/lib/ant-junit.jar
CLASSPATH=$CLASSPATH:$QTF/lib/ant-trax.jar
CLASSPATH=$CLASSPATH:$QTF/lib/maven-ant-tasks-2.1.3.jar
CLASSPATH=$CLASSPATH:$QTF/config
CLASSPATH=$CLASSPATH:$QTF/lib-deps
if [ -x "$JAVA_HOME/bin/java" ] ; then
  "$JAVA_HOME/bin/java" -Xmx128m -Xms128m -classpath $CLASSPATH org.apache.tools.ant.Main  $1 $2 $3 $4 $5
else
  echo "JAVA_HOME/bin/java not found: please set JAVA_HOME"
fi

