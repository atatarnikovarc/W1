#!/bin/sh
export YA_ROOT=/home/tav/Work/eclipseWS/yand/yand-p
$JAVA_HOME/bin/java -cp $JAVA_HOME/lib/tools.jar:$YA_ROOT/config:$YA_ROOT/lib/ant.jar:$YA_ROOT/lib/ant-launcher.jar:$YA_ROOT/lib/junit.jar org.apache.tools.ant.Main $1 $2 $3 $4 $5
$JAVA_HOME/bin/java -cp $JAVA_HOME/lib/tools.jar:$YA_ROOT/config:$YA_ROOT/dist/framework.jar:$YA_ROOT/lib/mysql-connector-java-5.1.18-bin.jar app.RunLuggage