package com.redaril.logreceiver;

import com.redaril.ssh.*;

class LogreceiverCore implements Runnable {

	private static final boolean DEBUG = false;
	
	private String IP;
	private String SSH_LOGFILENAME;
	private String LOCAL_LOGFILENAME;
	private String ENV;
	private String CLUSTER;
	private String THREADNAME;
	private String DATE;
	private String OUTPUT_PATH;
	
	@Override
	public void run() {
		
		if ( THREADNAME.equals("partners") ) {
			this.IP = getIP("partners", CLUSTER);
			this.SSH_LOGFILENAME = "partners.log";
			if (DATE == "" || DATE == null) {
				this.LOCAL_LOGFILENAME = OUTPUT_PATH + "partners.log";
			} else {
				this.LOCAL_LOGFILENAME = OUTPUT_PATH + "partners_" + DATE + ".log";
			}
		} else if ( THREADNAME.equals("taxonomyloader") ) {
			this.IP = getIP("taxonomyloader");
			this.SSH_LOGFILENAME = "taxonomyloader.log";
			if (DATE == "" || DATE == null) {
				this.LOCAL_LOGFILENAME = OUTPUT_PATH + "taxonomyloader.log";
			} else {
				this.LOCAL_LOGFILENAME = OUTPUT_PATH + "taxonomyloader_" + DATE + ".log";
			}
		} else if ( THREADNAME.equals("DmpModel") ) {
			this.IP = getIP("DmpModel");
			this.SSH_LOGFILENAME = "DmpModel.log";
			if (DATE == "" || DATE == null) {
				this.LOCAL_LOGFILENAME = OUTPUT_PATH + "DmpModel.log";
			} else {
				this.LOCAL_LOGFILENAME = OUTPUT_PATH + "DmpModel_" + DATE + ".log";
			}
		}
		
		System.out.println("LOGRECEIVER THREADNAME= " + THREADNAME + "; ENV= " + ENV +
				"; CLUSTER= " + CLUSTER + "; IP= " + IP +
				"; SSH_LOGFILENAME= " + SSH_LOGFILENAME +
				"; LOCAL_LOGFILENAME= " + LOCAL_LOGFILENAME + ";");
		
		if ( IP == "" ) {
			System.out.println("ERROR: Can`t get IP. Check params.");
			System.exit(1);
		}
		
		SSH ssh = new SSH(ENV);
		try {
			ssh.logReceiverExec(IP, SSH_LOGFILENAME, LOCAL_LOGFILENAME, false);
		} catch (Exception e) {
			System.out.println("ERROR: ssh.exec in LogreceiverCore.run(): " +
					"Exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	public void setThreadName (String threadName) {
		if (DEBUG) System.out.println("setThreadName: THREADNAME= " + threadName + ";");
		this.THREADNAME = threadName;
	}

	public void setENV (String env) {
		if ( THREADNAME == "" || THREADNAME == null ) {
			System.out.println("ERROR: Use \"setThreadName();\" first.");
			System.exit(1);
		}
		if (DEBUG) System.out.println(THREADNAME + ": setENV: ENV= " + env + ";");
		this.ENV = env;
	}
	public void setDate (String date) {
		this.DATE = date;
	}

	public void setOutPutPath (String outputPath) {
		this.OUTPUT_PATH = outputPath;
	}
	
	public void setCluster (String cluster) {
		if ( THREADNAME == "" || THREADNAME == null ) {
			System.out.println("ERROR: Use \"setThreadName();\" first.");
			System.exit(1);
		}
		if (DEBUG) System.out.println(THREADNAME + ": setCluster: CLUSTER= " + cluster + ";");
		this.CLUSTER = cluster;
	}

	private String getIP ( String app, String cluster ) {
		String ip = "";
		if (DEBUG) System.out.println(THREADNAME + ": getIP: " + app + "; ENV= " + ENV + "; "
				+ "CLUSTER= " + CLUSTER + ";");
		if ( app.equals("partners") ) {
			if ( cluster.equals("west") ) {
				if ( ENV.equals("env1") ) {
					ip = "10.50.150.152";
				} else if ( ENV.equals("env2") ) {
					ip = "10.50.150.10";
				}
			} else if ( cluster.equals("east") ) {
				if ( ENV.equals("env1") ) {
					ip = "10.50.150.131";
				} else if ( ENV.equals("env2") ) {
					ip = "10.50.150.155";
				}
			}			
		}
		return ip;
	}

	private String getIP ( String app ) {
		String ip = "";
		if (DEBUG) System.out.println(THREADNAME + ": getIP: " + app + "; ENV= " + ENV + ";");
		if ( app.equals("taxonomyloader") ) {
			if ( ENV.equals("env1") ) {
				ip = "10.50.150.152";
			} else if ( ENV.equals("env2") ) {
				ip = "10.50.150.10";
			}
		} else if ( app.equals("DmpModel") ) {
			if ( ENV.equals("env1") ) {
				ip = "10.50.150.12";
			} else if ( ENV.equals("env2") ) {
				ip = "10.50.150.14";
			}
		}
		return ip;
	}
	
}
