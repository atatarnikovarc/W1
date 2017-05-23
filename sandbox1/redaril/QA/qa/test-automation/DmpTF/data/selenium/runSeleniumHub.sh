#!/usr/bin/env bash
EXPECTED_ARGS=1
E_BADARGS=65
DO_showUsage() {
echo "Usage: $(basename $0) {start|stop}"
exit $E_BADARGS
}
if [ $# -ne $EXPECTED_ARGS ]; then
DO_showUsage
fi
################################################################################
WEBDRIVER_SERVER_JAR=selenium-server-standalone-2.25.0.jar
WEBDRIVER_HUB_PARAMS="-ensureCleanSession -trustAllSSL -timeout 60 -browserTimeout 60 -role hub -browserSessionReuse -port 4444"
WEBDRIVER_HUB_PIDFILE="webdriver_hub.pid"
if [ ! -f $WEBDRIVER_SERVER_JAR ]; then
echo "You must place the Selenium-WebDriver standalone JAR file at ${WEBDRIVER_SERVER_JAR} before proceeding."
exit 1
fi
case "$1" in
start)
echo "Starting Selenium-WebDriver Grid2 hub..."
if [ -f $WEBDRIVER_HUB_PIDFILE ]; then
echo "${FAIL_MSG} Selenium-WebDriver Grid2 hub already running with PID $(cat $WEBDRIVER_HUB_PIDFILE). Run 'runSeleniumHub stop'."
exit 1
else
START_HUB_CMD="java -jar ${WEBDRIVER_SERVER_JAR} ${WEBDRIVER_HUB_PARAMS}"
$START_HUB_CMD &
PID=$!
echo $PID > "${WEBDRIVER_HUB_PIDFILE}"
#echo "${SUCCESS_MSG} Selenium-WebDriver Grid2 hub started successfully."
fi
;;
stop)
echo "Stopping Selenium-WebDriver Grid2 hub..."
if [ -f $WEBDRIVER_HUB_PIDFILE ]; then
PID=$(cat $WEBDRIVER_HUB_PIDFILE)
kill $PID
rm $WEBDRIVER_HUB_PIDFILE
sleep 1
if [[ $(ps -A | egrep "^${PID}") ]]; then
echo "${FAIL_MSG} Tried to kill the hub with PID ${PID}, but was unsuccessful. You need to kill it with something stronger, like 'kill -9'"
exit 1
else
#echo "${SUCCESS_MSG} Selenium-WebDriver Grid2 hub stopped successfully."
exit 0
fi
else
echo "${SUCCESS_MSG} Selenium-WebDriver Grid2 hub has already been stopped."
exit 0
fi
;;