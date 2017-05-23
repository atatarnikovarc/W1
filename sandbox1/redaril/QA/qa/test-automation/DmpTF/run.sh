#!/bin/bash
export JAVA_HOME=/usr/java/latest/
export DMPTF=/var/lib/hudson/jobs/Tests/workspace/
CLASSPATH=$JAVA_HOME/lib/tools.jar:$DMPTF/lib/ant.jar
CLASSPATH=$CLASSPATH:$DMPTF/lib/ant-launcher.jar
CLASSPATH=$CLASSPATH:$DMPTF/lib/ant-junit.jar
CLASSPATH=$CLASSPATH:$DMPTF/lib/ant-trax.jar
CLASSPATH=$CLASSPATH:$DMPTF/lib/maven-ant-tasks-2.1.3.jar
CLASSPATH=$CLASSPATH:$DMPTF/config
CLASSPATH=$CLASSPATH:$DMPTF/lib-deps
ANT_OPTS="-Xms128m -Xmx512m -XX:MaxPermSize=256m"
export ANT_OPTS=$ANT_OPTS

if [ -x "$JAVA_HOME/bin/java" ] ; then
  "$JAVA_HOME/bin/java" -Xmx1500m -Xms1500m -classpath $CLASSPATH org.apache.tools.ant.Main  $1 $2 $3 $4 $5
else
  echo "JAVA_HOME/bin/java not found: please set JAVA_HOME"
fi

