LogMonitor application keeps track of all paths in .config/env.txt file and fire an e-mail,
if any error\exception occurs in any *.log file for all paths in real-time. It could be used
to analyze applicaion behavior in production enviroment

Prerequisites:
1. Java installed
2. JAVA_HOME set up
3. LOG_MONITOR environment variable set up to root of LogMonitor framework
4. Make sure .config/env.txt contains paths, which is proper mounted to the host filesystem
(i.e. log files are accessible)

To run LogMonitor use : "nohup ./run.sh &"
To stop LogMonitor - kill log_monitor_pid

To configure e-mails, log paths to monitor, change corresponding files in .config/ 
folder. NOTE:(!!) - put no spaces after value in property files - i.e. "true " and "true" does matter!!

Features of the application:
1. updating logs files in run-time, so it is not necessary to stop\start LogMonitor after redeployment
of an applications(especially when log files are deleted)
2. refreshing property files (.config/ dir) in run-time, so, it is not necessary to stop\start 
LogMonitor in order to make changes in properties files effective - just change config-files and in 1
minutes your changes take effect  
