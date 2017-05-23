#!/bin/bash

rm -f logreceiverServerlog.txt
rm -f logreceiverlog.txt
rm -f logreceiver.db
rm -f logreceiverChan.db

tclsh server.tcl &
sleep 1s

i=1
while read line ; do
	echo "Start logreceiver with logFile: /var/log/etl/"$line"ZZ1891"
		export LOGFILE="/var/log/etl/"$line"ZZ1891"
		export CHANNUM=$i
		tclsh logreceiver.tcl &
		sleep 1s
	((i++))
done < logFileList

