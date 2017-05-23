Prerequisites: 
-java 1.6 and upper installed
-JAVA_HOME environment variable is set up
-DMPTF environment varibale is set up (path to framework root)

--Selenium tests specific:
-Google Chrome should be in PATH
-Firefox should be in PATH
-IE should be in PATH
-Selenium RC server is up and running


To run tests: run.sh or run.bat
Syntax: ./run.sh -Dclass.name=com/redaril/dmptf/tests/cookiemapping/TestCookieMapping.java

i.e. in order to run tests, one should point package name or class name

Tests Specific Prerequisites:
1. BDP ip.path is mounted to "bdp.ip.path" from ./config/runtime.properties
2. ETL logs path is mounted to "var.log.etl.path" from ./config/runtime.properties   