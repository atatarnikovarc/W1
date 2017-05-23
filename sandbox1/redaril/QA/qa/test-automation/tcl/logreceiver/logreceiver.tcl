load libtclsqlite3.so
package require tcltest 2.0
namespace import ::tcltest::*
package require sqlite3

proc init {} {
global outputParam
if { ![file exists ETLlog] } { file mkdir ETLlog }
#set outputParam 1; #stdout + log
set outputParam 0; #log
}

proc getConnection { host port } {
global logreceiverChan env
# get connect to main
set i 0

while {1} {
	println "LOGRECEIVER INFO: Try to connect to main $host:$port"
	if { [catch { set logreceiverChan [socket $host $port] } errorMsg] } {
		#printlnchan stderr $errorMsg
	} else {
		#fconfigure $logreceiverChan -translation {lf lf} -buffering none -blocking 0 -encoding utf-8
		fconfigure $logreceiverChan -translation {binary binary} -buffering line -blocking 0 -encoding utf-8
		println "LOGRECEIVER_$env(CHANNUM):READY"
		printlnchan $logreceiverChan "LOGRECEIVER_$env(CHANNUM):READY"
		set count 0
		while {1} {
			if { [catch { gets $logreceiverChan line } errorMsg] } {
				#printlnchan stderr $errorMsg
			} else {
				#println "FROM MAIN: $line"
				if { $line ne ""} {
					println $line
					if { [string trim $line] == "LOGRECEIVER_$env(CHANNUM):READY OK" } {
						println "LOGRECEIVER INFO: Connection DONE."
						return $logreceiverChan
					}
				}
				if { $count >= 20 } { break }
			}
			incr count
			after 500
		}
	}
	after 1000
	incr i
	if { $i == 10 } { 
		println "LOGRECEIVER ERROR: Connection timeout. EXIT."
		exit
	}
}
}

proc print { msg } {
global outputParam
if { $outputParam } { puts -nonewline $msg }
set log [open logreceiverlog.txt a]
	puts -nonewline $log "stdout: $msg"
close $log
}

proc println { msg } {
global outputParam
if { $outputParam } { puts $msg }
set log [open logreceiverlog.txt a]
	puts $log "stdout: $msg"
close $log
}

proc printchan { chan msg } {
puts -nonewline $chan $msg
set log [open logreceiverlog.txt a]
	puts -nonewline $log "$chan: $msg"
close $log
}

proc printlnchan { chan msg } {
puts $chan $msg
set log [open logreceiverlog.txt a]
	puts $log "$chan: $msg"
close $log
}

proc storeToLog { fileToRead msg } {
set fileName [string range $fileToRead [expr [string last "/" $fileToRead] + 1] end]
set logFileName "ETLlog/"
append logFileName $fileName
	if { [catch {
	set f [open $logFileName a]
		println "storeToETLLog: $fileName $msg"
		puts $f $msg
	close $f
	} errorMsg] } {
		println "stoteToLog: $errorMsg"
	}
}

proc checkCommandFromLogreceiverCommander { chan } {
global continueFlag sqldblog

		if { [catch { gets $chan line } errorMsg] } {
			#printlnchan stderr $errorMsg
		} else {
			set line [string trim $line]
			switch $line {
				"LOGRECEIVER:START" {
					#set continueFlag 1
					#println "LOGRECEIVER INFO: receive cmd START"
				}
				"LOGRECEIVER:STOP" {
					#set continueFlag 0
					#sqldblog eval "DELETE FROM tFileData;"
					#println "LOGRECEIVER INFO: receive cmd STOP"
				}
				"LOGRECEIVER:EXIT" {
					println "LOGRECEIVER INFO: receive cmd EXIT"
					sqldblog eval "DELETE FROM tFileData;"
					exit
				}
				default {
				}
			}
		}
		
}

proc logreceiverInit { fileList } {
	set i 0
	set pollIntervalInMs 10
	set fileToReadCount [llength $fileList]
	while { $i < $fileToReadCount } {
		set fileToRead [lindex $fileList $i]
			set waitForFile 1
			while { $waitForFile } {
				if { [catch {
					set lastKnownFileSize [file size $fileToRead]
					set currentPos $lastKnownFileSize   ; # Can be forced to 0 is a full log file parse operation is required @ startup (but this could take a long time)
					sqldblog eval "DELETE FROM tFileData WHERE fileToRead = '$fileToRead';"
					sqldblog eval "INSERT OR REPLACE INTO tFileData VALUES ( '$fileToRead', $currentPos, $lastKnownFileSize);"
					} errorMsg] } {
					println "Warning : Could not get the initial size of $fileToRead. Retry in $pollIntervalInMs ms..."
					after 100
				} else {
					set waitForFile 0
				}
			}
		incr i
	}
}

proc logreceiver { fileList } {
global continueFlag sqldblog

println "LOGRECEIVER INFO: Connection."
set chan [getConnection 127.0.0.1 7070]
set chanCmd [getConnection 127.0.0.1 7071]

println "LOGRECEIVER INFO: Prepare sqlite db"
sqlite3 sqldblog logreceiver.db
sqldblog eval {DROP TABLE IF EXISTS tFileData}
sqldblog eval {CREATE TABLE tFileData(fileToRead TEXT, currentPos INT, lastKnownFileSize INT)}
println "LOGRECEIVER INFO: Prepare sqlite db DONE."

set fileToReadCount [llength $fileList]
set pollIntervalInMs 10
set continueFlag 1

while { 1 } {
	after 50
	
	# Check messages from 7071 port (logreceiver comander)
	if { ![eof $chan] } {
		checkCommandFromLogreceiverCommander $chanCmd $fileList
	} else {
		println "LOGRECEIVER ERROR: connection was lost. Retry connection."
		set chan [getConnection 127.0.0.1 7070]
	}

# INIT
# Get and store the current size of the file to read :
	logreceiverInit $fileList

# DO NOT DELETE code in if{0}{...}
# may be in a future will in use
if {0} {
	if { $continueFlag == 1 } {
		set i 0
		while { $i < $fileToReadCount } {
			set fileToRead [lindex $fileList $i]
				set waitForFile 1
				while { $waitForFile } {
					if { [catch {
						set lastKnownFileSize [file size $fileToRead]
						set currentPos $lastKnownFileSize   ; # Can be forced to 0 is a full log file parse operation is required @ startup (but this could take a long time)
						sqldblog eval "INSERT OR REPLACE INTO tFileData VALUES ( '$fileToRead', $currentPos, $lastKnownFileSize);"
						} errorMsg] } {
						println "Warning : Could not get the initial size of $fileToRead. Retry in $pollIntervalInMs ms..."
						after 100
					} else {
						set waitForFile 0
					}
				}
			incr i
		}
	}
}


# WORK
# Loop until a condition is set :
	while { $continueFlag } {
	
	if { ![eof $chan] } {
		checkCommandFromLogreceiverCommander $chanCmd $fileList
	} else {
		println "LOGRECEIVER ERROR: connection was lost. Retry connection."
		set chan [getConnection 127.0.0.1 7070]
	}
	
		#println "WORK WHILE continueFlag"
		set j 0
		while { $j < $fileToReadCount } {
			set fileToRead [lindex $fileList $j]
			# Wait between each poll :
			# after $pollIntervalInMs
		 
			# Get the new file size :
			if { [catch {
				set newFileSize [file size $fileToRead]
			} errorMsg ] } {
				println "Warning : Could not get the size of $fileToRead !"
				continue
			}
		 
			# Has file been modified ? :
			set lastKnownFileSize [sqldblog eval "SELECT lastKnownFileSize FROM tFileData WHERE fileToRead = '$fileToRead';"]
			if { $newFileSize != $lastKnownFileSize } {
		 
				# Store file size for next change detection :
				set lastKnownFileSize $newFileSize
				sqldblog eval "UPDATE tFileData SET lastKnownFileSize = $lastKnownFileSize WHERE fileToRead = '$fileToRead';"

				# Try to open file :
				if { [catch {
					set fileHandler [open $fileToRead r]
				} errorMsg ] } {
					println "Warning : Could not open $fileToRead for reading after change detection ($errorMsg) !"
					continue 
				}

				set currentPos [sqldblog eval "SELECT currentPos FROM tFileData WHERE fileToRead = '$fileToRead';"]
				# Deal with the 'truncated' file case :
				if { $newFileSize < $currentPos } {
					println "Warning : File truncated, moving to the end of file."
					set currentPos $newFileSize
					sqldblog eval "UPDATE tFileData SET currentPos = $currentPos WHERE fileToRead = '$fileToRead';"
				}

				# Move to the last "known" position :
				if { [catch {
					seek $fileHandler $currentPos start
				} errorMsg] } {
					println "Warning : Could not seek file $fileToRead to byte $currentPos ($errorMsg) !"
					continue
				}
		 
				# Read until the end of file :
				set errCount 0
				set done 0
				while { !$done } {
					if { [catch {
						while { [gets $fileHandler line] >= 0 } {
							printlnchan $chan "$fileToRead->$line"
							println "[clock format [clock seconds] -format {%Y-%m-%d %H:%M:%S}]: $line"
							#println -$fileToRead-($currentPos)->$line
							incr j
							set done 1
						}
					} errorMsg] } {
						#println "Warning : Could not read data from $fileToRead ($errorMsg) !"
						println $errorMsg
						if { $errCount >= 20 } {
							close $chan
							println "LOGRECEIVER ERROR: connection was lost. Retry connection."
							set chan [getConnection 127.0.0.1 7070]
						}
						incr errCount
					}
				}
		 #println "!!! AFTER BREAK IN WHILE"
				# Store current position and close file :
				if { [catch {
					set currentPos [tell $fileHandler]
					sqldblog eval "UPDATE tFileData SET currentPos = $currentPos WHERE fileToRead = '$fileToRead';"
					close $fileHandler
				} errorMsg] } {
					println "Warning : Could not properly close $fileToRead ($errorMsg)"
				}
			}
			incr j
			#println "j= $j"
		}
	}
}
vwait forever
}

proc logreceiver2 { fileToRead } {
set chan [getConnection 127.0.0.1 7070]
set chanCmd [getConnection 127.0.0.1 7071]

set pollIntervalInMsStage0 100
set pollIntervalInMsStage1 1000
set pollIntervalInMsStage2 5000
set balanceCountStage1 100
set balanceCountStage2 500
set tryCount 0

# Get and store the current size of the file to read :
set waitForFile 1
while { $waitForFile } {
	if { [catch {
		set lastKnownFileSize [file size $fileToRead]
		set tryCount 0
		} errorMsg] } {
		if { $tryCount > $balanceCountStage2 } {
			after $pollIntervalInMsStage2
			println "Warning : Could not get the initial size of $fileToRead. Retry in $pollIntervalInMsStage2 ms..."
		} elseif { $tryCount > $balanceCountStage1 } {
			after $pollIntervalInMsStage1
			println "Warning : Could not get the initial size of $fileToRead. Retry in $pollIntervalInMsStage1 ms..."
		} else {
			after $pollIntervalInMsStage0
			println "Warning : Could not get the initial size of $fileToRead. Retry in $pollIntervalInMsStage0 ms..."
		}
		incr tryCount
	} else {
		set waitForFile 0
	}
}
# Store the initial file pointer position (end of file) :
set currentPos $lastKnownFileSize   ; # Can be forced to 0 is a full log file parse operation is required @ startup (but this could take a long time)
# Loop until a condition is set :
set continueFlag 1  ; # Set to 0 by a depressed button ?
while { $continueFlag } {
	if { ![eof $chan] } {
		checkCommandFromLogreceiverCommander $chanCmd
	} else {
		println "LOGRECEIVER ERROR: connection was lost. Retry connection."
		set chan [getConnection 127.0.0.1 7070]
	}
	# Wait between each poll :
	after $pollIntervalInMsStage0
	# Get the new file size :
	if { [catch {
		set newFileSize [file size $fileToRead]
	} errorMsg ] } {
		println "Warning : Could not get the size of $fileToRead !"
		continue
	}
	# Has file been modified ? :
	if { $newFileSize != $lastKnownFileSize } {
		# Store file size for next change detection :
		set lastKnownFileSize $newFileSize
		# Try to open file :
		if { [catch {
			set fileHandler [open $fileToRead r]
		} errorMsg ] } {
			println "Warning : Could not open $fileToRead for reading after change detection ($errorMsg) !"
			continue
		}
		# Deal with the 'truncated' file case :
		if { $newFileSize < $currentPos } {
			println "Warning : File truncated, moving to the end of file."
			set currentPos $newFileSize
		}
		# Move to the last "known" position :
		if { [catch {
			seek $fileHandler $currentPos start
		} errorMsg] } {
			println "Warning : Could not seek file $fileToRead to byte $currentPos ($errorMsg) !"
			continue
		}
		# Read until the end of file :

####################################################################################################
#===================================================================================================
# WITHOUT RECONECTION
#===================================================================================================
		# turned off
		if {0} {
		if { [catch {
			while { [gets $fileHandler line] >= 0 } {
				#println -($currentPos)->$line
				println "$fileToRead: $line"
				printlnchan $chan "$fileToRead: $line"
				storeToLog $fileToRead $line
			}
		} errorMsg] } {
			println "Warning : Could not read data from $fileToRead ($errorMsg) !"
		}
		}
		

#===================================================================================================
# WITH RECONECTION
#===================================================================================================
		set errCount 0
		set done 0
		while { !$done } {
			if { [catch {
				while { [gets $fileHandler line] >= 0 } {
					println "$fileToRead: $line"
					printlnchan $chan "$fileToRead: $line"
					storeToLog $fileToRead $line
					set done 1
				}
			} errorMsg] } {
				println "Warning : Could not read data from $fileToRead ($errorMsg) !"
				if { $errCount >= 20 } {
					close $chan
					println "LOGRECEIVER ERROR: connection was lost. Retry connection."
					set chan [getConnection 127.0.0.1 7070]
				}
				incr errCount
			}
		}
####################################################################################################
		
		
		# Store current position and close file :
		if { [catch {
			set currentPos [tell $fileHandler]
			close $fileHandler
		} errorMsg] } {
			println "Warning : Could not properly close $fileToRead ($errorMsg)"
		}
	}
}
}

global env
set logFileName $env(LOGFILE)
init
logreceiver2 $logFileName
