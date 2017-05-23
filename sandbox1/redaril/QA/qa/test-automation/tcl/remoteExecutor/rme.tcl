package require tcltest 2.0
namespace import ::tcltest::*

proc get_client { channel clientaddr clientport } {
global sqldb
fconfigure $channel -translation {binary binary} -buffering line -blocking 0
flush $channel
fileevent $channel readable [ list remoteExecutor $channel $clientaddr $clientport ]
}

proc getArg { argList num } {
	if {[catch {
			puts -nonewline "[lindex $argList $num] "
			flush stdout
		} errMsg]} {
		puts "Error was happend in getArg: $errMsg"
	} else {
		return [lindex $argList $num]
	}
}

proc remoteExecutor { clientChan clientAddr clientPort } {
set protocolConnectLine "rme 1.0"
set protocolAuthLine "auth_username rmeClient"

set startTime [clock seconds]
while {1} {
	if { [catch {gets $clientChan line} fid] } {
		close $clientChan
		exit
	}
	if { [string trim $line] == $protocolConnectLine } {
		break
	} else {
		puts $clientChan $line
		flush $clientChan
	}
	if { [expr [clock seconds] - $startTime] > 10000 } { 
		close $clientChan
		exit
	}
	after 100
}
puts $clientChan "$protocolConnectLine OK"
flush $clientChan

set startTime [clock seconds]
while {1} {
	if { [catch {gets $clientChan line} fid] } {
		close $clientChan
		exit
	}
	if { [string trim $line] == $protocolAuthLine } {
		break
	} else {
		puts $clientChan $line
		flush $clientChan
	}
	if { [expr [clock seconds] - $startTime] > 10000 } {
		close $clientChan
		exit
	}
	after 100
}
puts $clientChan "$protocolAuthLine OK"
flush $clientChan

while {1} {
	if { ![eof $clientChan] } {
		if { [catch {gets $clientChan line} fid] } {
			close $clientChan
			break
		}

		if { $line ne "" } {
				set argList [split $line]
					switch [llength $argList] {
						1 {
							catch { exec [getArg $argList 0] } msg
							puts $msg
							puts $clientChan $msg
							flush $clientChan
						}
						2 {
							catch { exec [getArg $argList 0] [getArg $argList 1] } msg
							puts $msg
							puts $clientChan $msg
							flush $clientChan
						}
						3 {
							catch { exec [getArg $argList 0] [getArg $argList 1] [getArg $argList 2] } msg
							puts $msg
							puts $clientChan $msg
							flush $clientChan
						}
						4 {
							catch { exec [getArg $argList 0] [getArg $argList 1] [getArg $argList 2] [getArg $argList 3] } msg
							puts $msg
							puts $clientChan $msg
							flush $clientChan
						}
						5 {
							catch { exec [getArg $argList 0] [getArg $argList 1] [getArg $argList 2] [getArg $argList 3] [getArg $argList 4] } msg
							puts $msg
							puts $clientChan $msg
							flush $clientChan
						}
						6 {
							catch { exec [getArg $argList 0] [getArg $argList 1] [getArg $argList 2] [getArg $argList 3] [getArg $argList 4] [getArg $argList 5] } msg
							puts $msg
							puts $clientChan $msg
							flush $clientChan
						}
						7 {
							catch { exec [getArg $argList 0] [getArg $argList 1] [getArg $argList 2] [getArg $argList 3] [getArg $argList 4] [getArg $argList 5] [getArg $argList 6] } msg
							puts $msg
							puts $clientChan $msg
							flush $clientChan
						}
						8 {
							catch { exec [getArg $argList 0] [getArg $argList 1] [getArg $argList 2] [getArg $argList 3] [getArg $argList 4] [getArg $argList 5] [getArg $argList 6] [getArg $argList 7] } msg
							puts $msg
							puts $clientChan $msg
							flush $clientChan
						}
						default {
							puts "very long comand"
						}
					}
				puts ""
				puts $clientChan "rme done"
				flush $clientChan
				break
		}
		
		after 100
	} else { break }
}
catch { close $clientChan } msg
}

proc main {} {
global _CHANNEL forever sqldb fileToRead

set _CHANNEL(main) [socket -server get_client 7050]
fconfigure $_CHANNEL(main) -translation {binary binary} -buffering none -blocking 0
flush stdout

vwait forever
}

main