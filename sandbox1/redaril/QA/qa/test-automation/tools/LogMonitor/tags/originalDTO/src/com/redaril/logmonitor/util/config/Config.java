package com.redaril.logmonitor.util.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
	private Properties appProps = new Properties();
	private Properties envProps = new Properties();
	private Properties emailProps = new Properties();
	private Properties exclusionKeywordsProps = new Properties();
	private Properties exclusionFilenamesProps = new Properties();

	private static Config instance = new Config();

	public static Config getInstance() {
		return instance;
	}

	private Config() {
		new Thread(new Thread() {
			public void run() {
				while (true) {
					loadAllProperties();
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void loadAllProperties() {
		loadProperties("app.properties", appProps);
		loadProperties("e-mail.txt", emailProps);
		loadProperties("env.txt", envProps);
		loadProperties("exclusionkeywords.txt", exclusionKeywordsProps);
		loadProperties("exclusionfilenames.txt", exclusionFilenamesProps);
	}

	private void loadProperties(String name, Properties props) {
		synchronized (props) {
			InputStream in = null;
			props.clear();
			try {
				// app.properties should be on classpath
				in = ClassLoader.getSystemResourceAsStream(name);
				props.load(in);
			} catch (IOException e) {
				ExceptionHandler.castToRuntime("Cannot load config: ", e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (Exception e) {// ignore
					}
				}
			}
		}
	}

	// app.properties
	public String getExtensions() {
		return appProps.getProperty("extensions");
	}

	public String getKeywords() {
		return appProps.getProperty("keywords");
	}
	
	public int getMaxChangeSize() {
		return Integer.parseInt(appProps.getProperty("max.change.size"));
	}
	
	public int getMaxLogSize() {
		return Integer.parseInt(appProps.getProperty("max.log.size"));
	}
	
	public int getLogFilesCount() {
		return Integer.parseInt(appProps.getProperty("log.files.count"));
	}

	public int getMonitorInterval() {
		return Integer.parseInt(appProps.getProperty("monitor.interval"));
	}
	
	public int getRefreshLogFilelistInterval() {
		return Integer.parseInt(appProps.getProperty("refresh.log.file.list.interval"));
	}

	public String getMailServerHost() {
		return appProps.getProperty("mail.server");
	}

	public String getMailServerPort() {
		return appProps.getProperty("mail.port");
	}

	public String getMailUser() {
		return appProps.getProperty("mail.user");
	}

	public String getMailPassword() {
		return appProps.getProperty("mail.password");
	}

	public String getMailFrom() {
		return appProps.getProperty("mail.from");
	}

	public String getAllowEmailSending() {
		return appProps.getProperty("allow.email.sending");
	}

	// env.txt
	public Properties getEnv() {
		return envProps;
	}

	// e-mail.txt
	public Properties getEmail() {
		return emailProps;
	}

	// exclusionkeywords.txt
	public Properties getExclustionKeywords() {
		return exclusionKeywordsProps;
	}
	
	// exclusionkeywords.txt
	public Properties getExclustionFilenames() {
		return exclusionFilenamesProps;
	}
}
