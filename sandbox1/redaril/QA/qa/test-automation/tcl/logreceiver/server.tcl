load libtclsqlite3.so
package require tcltest 2.0
namespace import ::tcltest::*
package require sqlite3
global _SOCKET

proc init {} {
global outputParam
#set outputParam 1; #stdout + log
set outputParam 0; #log
}

proc print { msg } {
global outputParam
if { $outputParam } { puts -nonewline $msg }
set log [open logreceiverServerlog.txt a]
	puts -nonewline $log "stdout: $msg"
close $log
}

proc println { msg } {
global outputParam
if { $outputParam } { puts $msg }
set log [open logreceiverServerlog.txt a]
	puts $log "stdout: $msg"
close $log
}

proc printchan { chan msg } {
puts -nonewline $chan $msg
set log [open logreceiverServerlog.txt a]
	puts -nonewline $log "$chan: $msg"
close $log
}

proc printlnchan { chan msg } {
puts $chan $msg
set log [open logreceiverServerlog.txt a]
	puts $log "$chan: $msg"
close $log
}

proc get_client { channel clientaddr clientport } {
global sqldb
fconfigure $channel -translation {binary binary} -buffering line -blocking 0
println "\r\nNew connection to port 7070 from: $clientaddr : $clientport @ $channel"
printchan $channel "Wellcome to LOGRECEIVERSERVER\r\n"
flush $channel
sqldb eval "INSERT OR REPLACE INTO tChan VALUES ( 'client', '0', '$clientaddr', '$clientport', '$channel');"
fileevent $channel readable [ list logreceiverChanListener $channel $clientaddr $clientport ]
}

proc get_clientCmd { channel clientaddr clientport } {
global sqldb
fconfigure $channel -translation {binary binary} -buffering line -blocking 0
println "\r\nNew connection to comand port 7071 from: $clientaddr : $clientport @ $channel"
printchan $channel "Wellcome to LOGRECEIVERSERVER COMMANDER\r\n"
flush $channel
sqldb eval "INSERT OR REPLACE INTO tChan VALUES ( 'client', '-1', '$clientaddr', '$clientport', '$channel');"
fileevent $channel readable [ list logreceiverChanCommander $channel $clientaddr $clientport ]
}

proc logreceiverChanListener { chan host port } {
global _CHANNEL clientList sqldb logreceiverChan
	if { [catch {gets $chan line} fid] } {
		printlnchan stderr "Could not gets line from $chan\n$fid"
	}
	
	if { [eof $chan] } {
		println "ERROR: LOGRECEIVER is not alive."
		sqldb eval "DELETE FROM tChan WHERE name = 'LOGRECEIVER';"
		close $chan
		return
	}

	if { $line ne "" } {
		set line [string trim $line]
		println $line

		if { [string first ":READY" $line] != -1 } {
			set name [string range $line 0 [expr [string first "_" $line] - 1]]
			set chanNum [string range $line [expr [string first "_" $line] + 1] [expr [string first ":" $line] - 1]]
			set _CHANNEL($name) $chan
			sqldb eval "UPDATE tChan SET name = '$name' WHERE chan = '$chan';"
			sqldb eval "UPDATE tChan SET chanNum = '$chanNum' WHERE chan = '$chan';"
			println "$name arrived at chanNum: $chanNum."
			printlnchan $chan "$line OK"
			println "$line OK"
		} else {
				if { $line ne "" } {
					println "< logreceiver: $line"
					set clientList [sqldb eval "SELECT chan FROM tChan WHERE name = 'client';"]
					set i 0
					println "--- send ---"
					while { $i < [llength $clientList] } {
						set clientChan [lindex $clientList $i]
							if { [catch { printchan $clientChan $line\r\n } errorMsg] } {
								printlnchan stderr $errorMsg
								sqldb eval "DELETE FROM tChan WHERE chan = '$clientChan';"
								println "$clientChan has left logreceiver"
								incr i
							} else {
								println "> $clientChan: $line"
								incr i
							}
					}
					println "--- done ---\r\n"
				}
		}
	}
}

proc logreceiverChanCommander { chan host port } {
global sqldb
	if { [catch {gets $chan line} fid] } {
		printlnchan stderr "Could not gets line from $chan\n$fid"
	}
	
	if { [eof $chan] } {
		println "ERROR: LOGRECEIVER COMMANDER is not alive."
		sqldb eval "DELETE FROM tChan WHERE name = 'logreceiverCmd';"
		close $chan
		return
	}

if {0} {
set logreceiverChan [sqldb eval "SELECT chan FROM tChan WHERE name = 'LOGRECEIVER';"]
set i 0
while { $i < [llength $logreceiverChan] } {
	printchan [lindex $logreceiverChan $i] "LOGRECEIVER:EXIT\r\n"
	flush [lindex $logreceiverChan $i]
	incr i
}
exit
}
	
	
	
	if { $line ne "" } {
		set line [string trim $line]
		println $line	

		if { [string first ":READY" $line] != -1 } {
			set name [string range $line 0 [expr [string first "_" $line] - 1]]
			set chanNum [string range $line [expr [string first "_" $line] + 1] [expr [string first ":" $line] - 1]]
			set _CHANNEL($name) $chan
			sqldb eval "UPDATE tChan SET name = 'logreceiverCmd' WHERE chan = '$chan';"
			sqldb eval "UPDATE tChan SET chanNum = '$chanNum' WHERE chan = '$chan';"
			println "$name arrived at chanNum: $chanNum."
			printlnchan $chan "$line OK"
			println "$line OK"
		} else {
			if { $line ne "" } {
				set line [string trim $line]
				print "INFO: get command $line.\r\n"
						set logreceiverChan [sqldb eval "SELECT chan FROM tChan WHERE name = 'logreceiverCmd';"]
				switch $line {
					"logstart" {
						println "INFO: Send command LOGRECEIVER:START to logreceiver."
						set logreceiverChan [sqldb eval "SELECT chan FROM tChan WHERE name = 'logreceiverCmd';"]
						printchan $logreceiverChan "LOGRECEIVER:START\r\n"
						flush $logreceiverChan
					}
					"logstop" {
						println "INFO: Send command LOGRECEIVER:STOP to logreceiver."
						set logreceiverChan [sqldb eval "SELECT chan FROM tChan WHERE name = 'logreceiverCmd';"]
						printchan $logreceiverChan "LOGRECEIVER:STOP\r\n"
						flush $logreceiverChan
					}
					"logexit" {
						println "INFO: Send command LOGRECEIVER:EXIT to logreceiver."
						set logreceiverChan [sqldb eval "SELECT chan FROM tChan WHERE name = 'logreceiverCmd';"]
						printchan $logreceiverChan "LOGRECEIVER:EXIT\r\n"
						flush $logreceiverChan
					}
					"exit" {
						set logreceiverChan [sqldb eval "SELECT chan FROM tChan WHERE name = 'LOGRECEIVER';"]
						set i 0
						while { $i < [llength $logreceiverChan] } {
							printchan [lindex $logreceiverChan $i] "LOGRECEIVER:EXIT\r\n"
							flush [lindex $logreceiverChan $i]
							incr i
						}
						exit
					}
					default {
						print "WARNING: unknown command $line.\r\n"
					}
				}
			}
		}
	}
}

proc main {} {
global _CHANNEL forever sqldb fileToRead

println "open new db logreceiverChan.db"
sqlite3 sqldb logreceiverChan.db
sqldb eval "DROP TABLE IF EXISTS tChan"
sqldb eval "CREATE TABLE tChan(name TEXT, chanNum TEXT, ip TEXT, port TEXT, chan TEXT)"
println "done"
flush stdout

set _CHANNEL(main) [socket -server get_client 7070]
fconfigure $_CHANNEL(main) -translation {binary binary} -buffering none -blocking 0
println "started. _CHANNEL(main): $_CHANNEL(main)"
flush stdout

set _CHANNEL(mainCmd) [socket -server get_clientCmd 7071]
fconfigure $_CHANNEL(mainCmd) -translation {binary binary} -buffering none -blocking 0
println "started. _CHANNEL(mainCmd): $_CHANNEL(mainCmd)"
flush stdout

vwait forever
println "DONE\n"
}

init
main