package com.redaril.logreceiver;

public class Logreceiver {

	private String ENV;
	private String CLUSTER;
	private Thread t;
	private String DATE;
	private String OUTPUT_PATH;
	
	public void setEnv (String env) {
		this.ENV = env;
	}
	public String getEnv() {
		return ENV;
	}
	public void setCluster (String cluster) {
		this.CLUSTER = cluster;
	}
	public String getCluster() {
		return CLUSTER;
	}
	public void setDate (String date) {
		this.DATE = date;
	}
	public String getDate() {
		return ENV;
	}
	public void setOutPutPath (String outputPath) {
		this.OUTPUT_PATH = outputPath;
	}
	public String getOutPutPath() {
		return OUTPUT_PATH;
	}
	public Logreceiver(String env) {
		this.ENV = env;
		this.CLUSTER = "";
		this.OUTPUT_PATH = "";
	}
	public Logreceiver(String env, String param2) {
		if ( param2.equalsIgnoreCase("west") || param2.equalsIgnoreCase("east") ) {
			this.ENV = env;
			this.CLUSTER = param2;
			this.OUTPUT_PATH = "";
		} else {
			this.ENV = env;
			this.CLUSTER = "";
			this.OUTPUT_PATH = param2;
		}
	}

	public Logreceiver(String env, String cluster, String outputPath) {
		this.ENV = env;
		this.CLUSTER = cluster;
		this.OUTPUT_PATH = outputPath;
	}
	
	public void start(String threadName) {
		LogreceiverCore l = new LogreceiverCore();
		l.setThreadName(threadName);
		l.setENV(ENV);
		l.setCluster(CLUSTER);
		l.setDate(DATE);
		l.setOutPutPath(OUTPUT_PATH);
		t = new Thread(l, threadName);
		t.start();
	}
	
	@SuppressWarnings("deprecation")
	public void stop () {
		t.stop();
	}
}
