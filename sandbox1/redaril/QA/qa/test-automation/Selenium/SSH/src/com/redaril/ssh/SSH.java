package com.redaril.ssh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class SSH {
	
	private String RD_HOST;
	private String SSH_USERNAME;
	private String SSH_PASSWORD;
	private String DB_HOST;
	private String DB_PORT;
	private String DB_SID;
	private boolean FORCE_REBOOT;
	private String ENV;
	
	public SSH ( String env ) {
		this.ENV = env;
		this.SSH_USERNAME = Config.getProperty("sshUsername");
		this.SSH_PASSWORD = Config.getProperty("sshPassword");
	}
	
	public SSH (String env, String sshUser, String sshPswd,
			String sshDbHost, String sshDbPort, String sshDbSid) {
		this.ENV = env;
		this.SSH_USERNAME = sshUser;
		this.SSH_PASSWORD = sshPswd;
		this.DB_HOST = sshDbHost;
		this.DB_PORT = sshDbPort;
		this.DB_SID = sshDbSid;
	}
	
	private String getDBLogin() {
		return ENV + "_meta";
	}
	private String getDBPassword() {
		return ENV + "_meta";
	}
	
	public void setEnv (String env) {
		this.ENV = env;
	}
	public void setSshUserName (String sshUserName) {
		this.SSH_USERNAME = sshUserName;
	}
	public void setSshPassword (String sshPassword) {
		this.SSH_PASSWORD = sshPassword;
	}
	public void setRemoteDeveloperdHost (String rdHost) {
		this.RD_HOST = rdHost;
	}
	public void setDbHost (String dbHost) {
		this.DB_HOST = dbHost;
	}
	public void setDbPort (String dbPort) {
		this.DB_PORT = dbPort;
	}
	public void setForceReboot (boolean fr) {
		this.FORCE_REBOOT = fr;
	}
	
	public void importNewQualifiers () throws Exception {
		//DB_HOST, "sqlplus " + DB_LOGIN + "/"+ DB_PSWD +"@10.50.150.90:1521/qacluster @/root/QA/BunUpdater/updateDB.sql"
		exec(DB_HOST,
				"sqlplus " + getDBLogin() + "/"+ getDBPassword() + 
				"@" + DB_HOST + ":" + DB_PORT + "/" + DB_SID + " " +
				"@/root/QA/BunUpdater/updateDB.sql", true);
	}
	
	public void exec ( String host, String cmd, boolean stdoutOutput ) throws Exception  {
//		File f = new File("1");
//		System.out.println("\n\r" + f.getAbsolutePath());
		Connection conn = getConnection(host);
		boolean isAuthenticated = getAuthentication (conn, SSH_USERNAME, SSH_PASSWORD);
		if ( !isAuthenticated ) {
			System.out.println("Authentication failed.");
			System.exit(2);
		} else {
			try {
				String line = "";
				Session sess = getSession (conn);
				InputStream stdout = new StreamGobbler(sess.getStdout());
				InputStream stderr = new StreamGobbler(sess.getStderr());
				BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
				BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));
				sess.execCommand(cmd);
				
				while (true) {
					line = stdoutReader.readLine();
					if (line == null)
						break;
					if (stdoutOutput) System.out.println("ssh stdout: " + line);
				}
				
				while (true) {
					line = stderrReader.readLine();
					if (line == null)
						break;
					System.out.println("ssh stderr: " + line);
				}
				
				System.out.println("ExitCode: " + sess.getExitStatus());
				sess.close();
				conn.close();
			} catch (IOException e) {
				System.out.println("Exception: " + e.getMessage());
				e.printStackTrace(System.err);
				System.exit(2);
			}
		}
	}
	
	public void logReceiverExec ( String host, String sshLogFileName,
			String logFileNameLocal, boolean stdoutOutput ) throws Exception  {
//		File f = new File("1");
//		System.out.println("\n\r" + f.getAbsolutePath());
		appendFile("logs\\" + logFileNameLocal,
				"\r\n\r\n" +
				"-- cut line --------------------------------------------------------------------" +
				"\r\n\r\n");
		String cmd = "tail -f /home/csc/logs/" + sshLogFileName;
		Connection conn = getConnection(host);
		boolean isAuthenticated = getAuthentication (conn, SSH_USERNAME, SSH_PASSWORD);
		if ( !isAuthenticated ) {
			System.out.println("Authentication failed.");
			System.exit(2);
		} else {
			try {
				String line = "";
				Session sess = getSession (conn);
				InputStream stdout = new StreamGobbler(sess.getStdout());
				InputStream stderr = new StreamGobbler(sess.getStderr());
				BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
				BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));
				sess.execCommand(cmd);
				
				while (true) {
					line = stdoutReader.readLine();
					if (line == null)
						break;
					if (stdoutOutput) System.out.println("ssh stdout: " + line);
					appendFile("logs\\" + logFileNameLocal, line);
				}
				
				while (true) {
					line = stderrReader.readLine();
					if (line == null)
						break;
					System.out.println("ssh stderr: " + line);
				}
				
				System.out.println("ExitCode: " + sess.getExitStatus());
				sess.close();
				conn.close();
			} catch (IOException e) {
				System.out.println("Exception: " + e.getMessage());
				e.printStackTrace(System.err);
				System.exit(2);
			}
		}
	}
	
	public void reboot ( String clusterName ) throws Exception {
		
		String partnersHost = "";
		if ( clusterName.equals("env-1") ) {
			partnersHost = "10.50.150.131";
		} else if ( clusterName.equals("env-2") ) {
			partnersHost = "10.50.150.155";
		} else {
			System.out.println("ERROR: Wrong param value \"" + clusterName + "\"\r\n" +
					"must be \"env-1\" or \"env-2\"");
			System.exit(2);
		}
		
		System.out.println("INFO: STOP " + clusterName);
		stop(clusterName);

		System.out.println("INFO: Wait while cluster " + clusterName + " STOP");
		int psCount = 0;
		while (true) {
			if (isStopped(clusterName)) {
				System.out.println("INFO: " + clusterName + " is stopped");
				break;
			} else {
				if ( (psCount > 10) && FORCE_REBOOT ) {
					kill(clusterName);
				}
				System.out.println("INFO: " + clusterName + " is going down. wait 10 sec");
				psCount++;
				Thread.sleep(10000);
			}
		}
		System.out.println("INFO: START " + clusterName);

		start(clusterName);
		System.out.println("INFO: Wait while cluster " + clusterName + " START");
		while (true) {
			if (isStarted(partnersHost)) {
				System.out.println("INFO: " + clusterName + " started");
				break;
			} else {
				System.out.println("INFO: " + clusterName + " is going up. wait 10 sec");
				Thread.sleep(10000);
			}
		}
	}
	
	public boolean getNewQualifiers ( String clusterName, String filename ) throws Exception {
		System.out.println("getNewQualifiers start");
		boolean result = false;
		Connection conn = getConnection(DB_HOST);
		boolean isAuthenticated = getAuthentication (conn, SSH_USERNAME, SSH_PASSWORD);
		if ( !isAuthenticated ) {
			System.out.println("Authentication failed.");
			System.exit(2);
		} else {
			
			//delete file with Qualifiers if it exists from last use
			File fileToDelete = new File(filename);
			if (fileToDelete.exists()) {
				fileToDelete.delete();
			}		
			
			Session sess = getSession (conn);
			InputStream stdout = new StreamGobbler(sess.getStdout());
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
			sess.execCommand("sqlplus " + getDBLogin() + "/"+ getDBPassword() +"@10.50.150.90:1521/" + DB_SID + " @/root/QA/tcl/bashBunUpdate/bunUpdate_getNewQualifiers.sql");
			String line = br.readLine();
			int emptyLineCount = 0;
			while (true) {
				line = br.readLine();
				if (line == null)
					emptyLineCount++;
				if (emptyLineCount >= 50)
					break;
				System.out.println("DEBUG: " + line);
				
				if (line.contains("http:")) {
					appendFile(filename, line + "\r\n");
				} else if (line.contains("BASE_URL") ||
						line.contains("----------") ||
						line.contains("SQL*Plus") ||
						line.contains("Oracle Database") ||
						line.contains("With the Partitioning") ||
						line.contains("Copyright") ||
						line.contains("Connected") ||
						line.isEmpty()) {
				} else if (line.contains("rows selected")) {
					result = true;
					//TODO something with this count
				} else {
					appendFile(filename, line);
				}

			}
			sess.close();
			conn.close();
		}
		return result;
	}
	
	private Connection getConnection (String host) {
		Connection conn = null;
		try {
			conn = new Connection(host);
			conn.connect();
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(2);
		}
		return conn;
	}
	
	private boolean getAuthentication ( Connection conn, String username, String password ) {
		boolean isAuthenticated = false;
		try {
			isAuthenticated = conn.authenticateWithPassword(username, password);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(2);
		}
		return isAuthenticated;
	}
	
	private Session getSession ( Connection conn ) {
		Session sess = null;
		try {
			sess = conn.openSession();
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(2);
		}
		return sess;
	}
	
	private void stop ( String clusterName ) {
		try {
			Connection conn = getConnection(RD_HOST);
			boolean isAuthenticated = getAuthentication (conn, SSH_USERNAME, SSH_PASSWORD);
			if ( !isAuthenticated ) {
				System.out.println("Authentication failed.");
				System.exit(2);
			} else {
				String line = "";
				Session sess = getSession (conn);
				InputStream stdout = new StreamGobbler(sess.getStdout());
				InputStream stderr = new StreamGobbler(sess.getStderr());
				BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
				BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));				System.out.println("INFO: " + clusterName + " stop");
				sess.execCommand("/home/csc/bin/remoteDeployer -c " + clusterName + " stop");
				
				while (true) {
					line = stdoutReader.readLine();
					if (line == null)
						break;
					System.out.println("ssh stdout: " + line);
				}
				
				while (true) {
					line = stderrReader.readLine();
					if (line == null)
						break;
					System.out.println("ssh stderr: " + line);
				}
				
				System.out.println("ExitCode: " + sess.getExitStatus());
				sess.close();
				conn.close();
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace(System.err);
			System.exit(2);
		}
	}
	
	private boolean isStopped ( String clusterName ) throws Exception {
		boolean result = true;
		Connection conn = getConnection(RD_HOST);
		boolean isAuthenticated = getAuthentication (conn, SSH_USERNAME, SSH_PASSWORD);
		if ( !isAuthenticated ) {
			System.out.println("Authentication failed.");
			System.exit(2);
		} else {
			try {
				String line = "";
				Session sess = getSession (conn);
				InputStream stdout = new StreamGobbler(sess.getStdout());
				InputStream stderr = new StreamGobbler(sess.getStderr());
				BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
				BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));
				sess.execCommand("/home/csc/bin/remoteDeployer -c " + clusterName + " ps");

				while (true) {
					line = stdoutReader.readLine();
					if (line == null)
						break;
					System.out.println("ssh stdout: " + line);
					if (line.contains("Output from"))
						result = false;
				}
				
				while (true) {
					line = stderrReader.readLine();
					if (line == null)
						break;
					System.out.println("ssh stderr: " + line);
				}
			
				sess.close();
				conn.close();
			} catch (Exception e) {
				System.out.println("Exception: " + e.getMessage());
				e.printStackTrace(System.err);
				System.exit(2);
			}
		}
		return result;
	}
	
	private void start ( String clusterName ) {
		try {
			Connection conn = getConnection(RD_HOST);
			boolean isAuthenticated = getAuthentication (conn, SSH_USERNAME, SSH_PASSWORD);
			if ( !isAuthenticated ) {
				System.out.println("Authentication failed.");
				System.exit(2);
			} else {
				String line = "";
				Session sess = getSession (conn);
				InputStream stdout = new StreamGobbler(sess.getStdout());
				InputStream stderr = new StreamGobbler(sess.getStderr());
				BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
				BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));
				sess.execCommand("/home/csc/bin/remoteDeployer -c " + clusterName + " start");
				
				while (true) {
					line = stdoutReader.readLine();
					if (line == null)
						break;
					System.out.println("ssh stdout: " + line);
				}
				
				while (true) {
					line = stderrReader.readLine();
					if (line == null)
						break;
					System.out.println("ssh stderr: " + line);
				}
				
				System.out.println("ExitCode: " + sess.getExitStatus());
				sess.close();
				conn.close();
			}
			
		Thread.sleep(20000);
		if ( !summary(clusterName) ) {
			start(clusterName);
		}
		
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace(System.err);
			System.exit(2);
		}
	}
	
	private String getIP ( String line ) {
		//Output from 10.50.150.10:
		int start = line.lastIndexOf("from");
		start+=5;
		int end = line.length();
		end--;
		char ip[] = new char[end - start]; 
		line.getChars(start, end, ip, 0);
		return String.valueOf(ip);
	}

	private String getPID ( String line ) {
		//ssh stdout: 11028 /usr/bin/java
		int start = 0;
		int end = line.lastIndexOf("/");
		end--;
		char pid[] = new char[end - start]; 
		line.getChars(start, end, pid, 0);
		return String.valueOf(pid);
	}
	
	private void killApp ( String ip, String pid ) throws Exception {
		try {
			System.out.println("\r\n\r\nkill pid=" + pid + " at ip=" + ip + "\r\n\r\n");
			exec(ip, "kill -9 " + pid, true);
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace(System.err);
			System.exit(2);
		}
	}

	private void kill ( String clusterName ) throws Exception {
		Connection conn = getConnection(RD_HOST);
		boolean isAuthenticated = getAuthentication (conn, SSH_USERNAME, SSH_PASSWORD);
		if ( !isAuthenticated ) {
			System.out.println("Authentication failed.");
			System.exit(2);
		} else {
			try {
				String line = "";
//				String IPForKill = "";
//				String PIDForKill = "";
				Session sess = getSession (conn);
				InputStream stdout = new StreamGobbler(sess.getStdout());
				InputStream stderr = new StreamGobbler(sess.getStderr());
				BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
				BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));
				sess.execCommand("/home/csc/bin/remoteDeployer -c " + clusterName + " kill");

				while (true) {
					line = stdoutReader.readLine();
					if (line == null)
						break;
					System.out.println("ssh stdout: " + line);
//					if (line.contains("Output from")) {
//						IPForKill = getIP(line);
//						line = stdoutReader.readLine();
//						PIDForKill = getPID(line);
//						killApp(IPForKill, PIDForKill);
//					}
				}
				
				while (true) {
					line = stderrReader.readLine();
					if (line == null)
						break;
					System.out.println("ssh stderr: " + line);
				}
			
				sess.close();
				conn.close();
			} catch (Exception e) {
				System.out.println("Exception: " + e.getMessage());
				e.printStackTrace(System.err);
				System.exit(2);
			}
		}		
	}
	
	private boolean summary ( String clusterName ) throws Exception {
		boolean result = true;
		Connection conn = getConnection(RD_HOST);
		boolean isAuthenticated = getAuthentication (conn, SSH_USERNAME, SSH_PASSWORD);
		if ( !isAuthenticated ) {
			System.out.println("Authentication failed.");
			System.exit(2);
		} else {
			try {
				String line = "";
				Session sess = getSession (conn);
				InputStream stdout = new StreamGobbler(sess.getStdout());
				InputStream stderr = new StreamGobbler(sess.getStderr());
				BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
				BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));
				sess.execCommand("/home/csc/bin/remoteDeployer -c " + clusterName + " summary");

				while (true) {
					line = stdoutReader.readLine();
					if (line == null)
						break;
					System.out.println("ssh stdout: " + line);
					if (line.contains("deployer: Cannot connect to server for status.")) {
						System.out.println("WARNING: Some applications did not start.\r\n" +
								"Will try to send START command again");
						result = false;
					}
				}
				
				while (true) {
					line = stderrReader.readLine();
					if (line == null)
						break;
					System.out.println("ssh stderr: " + line);
				}
			
				sess.close();
				conn.close();
			} catch (Exception e) {
				System.out.println("Exception: " + e.getMessage());
				e.printStackTrace(System.err);
				System.exit(2);
			}
		}
		return result;
	}
	
	private boolean isStarted ( String host ) throws Exception {
		boolean result = false;
		Connection conn = getConnection(host);
		boolean isAuthenticated = getAuthentication (conn, SSH_USERNAME, SSH_PASSWORD);
		if ( !isAuthenticated ) {
			System.out.println("Authentication failed.");
			System.exit(2);
		} else {
			try {
				String line = "";
				Session sess = getSession (conn);
				InputStream stdout = new StreamGobbler(sess.getStdout());
				InputStream stderr = new StreamGobbler(sess.getStderr());
				BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
				BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));
				sess.execCommand("tail -f /home/csc/logs/partners.log");

				while (true) {
					line = stdoutReader.readLine();
					if (line == null)
						break;
					System.out.println("ssh stdout: " + line);
					if (line.contains("### DMP meta data report ###")) {
						//TODO: check "cluster loaded" event
						result = true;
						break;
					}
				}
				
				while (true) {
					line = stderrReader.readLine();
					if (line == null)
						break;
					System.out.println("ssh stderr: " + line);
				}
				
				System.out.println("ExitCode: " + sess.getExitStatus());
				sess.close();
				conn.close();
			} catch (Exception e) {
				System.out.println("Exception: " + e.getMessage());
				e.printStackTrace(System.err);
				System.exit(2);
			}		
		}
		return result;		
	}

	private void appendFile ( String filename, String msg ) throws Exception {
    	try {
    	    File file = new File(filename);
    	    FileWriter writer = new FileWriter(file, true);
    	    writer.write(msg + "\r\n");
    	    writer.flush();
    	    writer.close();
    	} catch (Exception e) {
	    	System.out.println(e.getMessage());
    	}
    }
	
	public boolean isTested ( String clusterName ) throws Exception {
		String partnersHost = "";
		if ( clusterName.equals("env-1") ) {
			partnersHost = "10.50.150.131";
		} else if ( clusterName.equals("env-2") ) {
			partnersHost = "10.50.150.155";
		} else {
			System.out.println("ERROR: Wrong param value \"" + clusterName + "\"\r\n" +
					"must be \"env-1\" or \"env-2\"");
			System.exit(2);
		}
		
		boolean result = false;
		Connection conn = getConnection(partnersHost);
		boolean isAuthenticated = getAuthentication (conn, SSH_USERNAME, SSH_PASSWORD);
		if ( !isAuthenticated ) {
			System.out.println("Authentication failed.");
			System.exit(2);
		} else {
			try {
				String line = "";
				Session sess = getSession (conn);
				InputStream stdout = new StreamGobbler(sess.getStdout());
				InputStream stderr = new StreamGobbler(sess.getStderr());
				BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
				BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));
				sess.execCommand("tail -f /home/csc/logs/partners.log");

				while (true) {
					line = stdoutReader.readLine();
					if (line == null)
						break;
					System.out.println("ssh stdout: " + line);
					if (line.contains("Loaded")) {
						System.out.println("IS TESTED: Loaded Founded: " + line);
						result = true;
						break;
					}
				}
				
				while (true) {
					line = stderrReader.readLine();
					if (line == null)
						break;
					System.out.println("ssh stderr: " + line);
				}
				
				System.out.println("ExitCode: " + sess.getExitStatus());
				sess.close();
				conn.close();
			} catch (Exception e) {
				System.out.println("Exception: " + e.getMessage());
				e.printStackTrace(System.err);
				System.exit(2);
			}		
		}
		return result;		
	}	
	
}
