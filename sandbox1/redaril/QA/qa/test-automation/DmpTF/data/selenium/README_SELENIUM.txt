--RUN HUB
1) go to 10.50.150.13
2) login as csc
3) cd QA/selenium-server/
4) java -jar selenium-server-standalone-2.25.0.jar -ensureCleanSession -trustAllSSL -timeout 600 -browserTimeout 600 -role hub -browserSessionReuse
--END

--ADD ability to use new remote machine
1) go to machine
2) install jre6 (http://www.oracle.com/technetwork/java/javase/downloads/index.html)
3) add java_home to env properties
4) download JAR into folder c:\\selenium-server\ (http://selenium.googlecode.com/files/selenium-server-standalone-2.25.0.jar)
5) run jar at remote machine to connect with HUB
!-port must be unique.
Next parameters should be specific for every machine:
-browser browserName=firefox,version=14,platform=WINDOWS
Expected values:
Platfoom: WINDOWS, XP, VISTA, MAC, UNIX, LINUX
Browser name: chrome, firefox, internet explorer, opera, safari
Version : version of browser(type String)
--END

CHROME:
check http://code.google.com/p/selenium/wiki/ChromeDriver to find the path
you should download chromedriver.exe and set the path to it
you should switch off auto-updating of chrome, because you can't resolve version of it in your tests
clean all about GoogleUpdate.exe from registr(use cmd->regedit->find)
and remove auto-loading from jobs(msconfig)

SAFARI:
http://code.google.com/p/selenium/wiki/SafariDriver for safari(it was complex for me)
https://groups.google.com/forum/?fromgroups=#!topic/webdriver/HJ5L0Pu1dWs (for safari too)



All scripts you can find at data/selenium/

DEFAULT DRIVERS.TXT
OS;Browser;IP;Version;Request;isForProxy
XP;firefox;15.0.1;192.168.0.153;java -jar selenium-server-standalone-2.25.0.jar -role webdriver -hub http://10.50.150.13:4444/grid/register -browser browserName="firefox",version=15.0.1,platform=XP,maxInstances=5 -port;1
WINDOWS;internet explorer;6;192.168.0.153;java -jar selenium-server-standalone-2.25.0.jar -role webdriver -hub http://10.50.150.13:4444/grid/register -browser browserName="internet explorer",version=6,platform=WINDOWS,maxInstances=5 -port;1
WINDOWS;opera;11.60;192.168.0.226;java -jar selenium-server-standalone-2.25.0.jar -role webdriver -hub http://10.50.150.13:4444/grid/register -browser browserName="opera",version=11.60,platform=WINDOWS,maxInstances=5 -port;0
WINDOWS;internet explorer;9;192.168.0.226;java -jar selenium-server-standalone-2.25.0.jar -role webdriver -hub http://10.50.150.13:4444/grid/register -browser browserName="internet explorer",version=9,platform=WINDOWS,maxInstances=5 -port;1
XP;chrome;22;192.168.0.153;java -jar selenium-server-standalone-2.25.0.jar -role webdriver -hub http://10.50.150.13:4444/grid/register -browser browserName="chrome",version=22,platform=XP,maxInstances=5  -Dwebdriver.chrome.driver="c:\selenium-server\chromedriver.exe" -port;1

in progress
WINDOWS;safari;5.1.7;192.168.0.153;java -jar selenium-server-standalone-2.25.0.jar -role webdriver -hub http://10.50.150.13:4444/grid/register -browser browserName=safari,version=5.1.7,platform=WINDOWS,maxInstances=5 -port


Files
Drivers.txt file which consists of drivers to test UI without proxy
DriversProxy.txt file which consists of drivers to test UI with proxy


Browser specific settings:
-don't remember history, addresses