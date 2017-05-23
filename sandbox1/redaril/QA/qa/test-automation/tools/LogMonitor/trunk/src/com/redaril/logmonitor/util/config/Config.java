package com.redaril.logmonitor.util.config;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;

public class Config {
    private static final Logger logger = Logger.getLogger(Config.class);
	private Properties appProps = new Properties();
	private Properties envProps = new Properties();
	private Properties emailProps = new Properties();
	private Properties exclusionFilenamesProps = new Properties();
    private Properties envRecipientProps = new Properties();
    private Vector<String> exclusionKeywords = new Vector<String>(10);

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
						logger.warn("sleep thread has been interrupted", e);
					}
				}
			}
		}).start();
	}

	private void loadAllProperties() {
		loadProperties("app.properties", appProps);
		loadProperties("email.properties", emailProps);
		loadProperties("env.properties", envProps);
		loadProperties("exclusionfilenames.properties", exclusionFilenamesProps);
        loadProperties("envrecepient.properties", envRecipientProps);
        loadPropertiesAsStrings("exclusionkeywords.properties", exclusionKeywords);
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

    private void loadPropertiesAsStrings(String name, Collection props) {
        synchronized (props) {
            String config_path = "config" + File.separator;
            File file = new File(config_path + name);
            Scanner scanner = null;
            try {

                scanner = new Scanner(file);
                logger.debug("before scanner cycle" + new File(".").getCanonicalPath());
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    props.add(line);
                    logger.debug("exclusion keyword: " + "--" + line + "--");
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {

            } finally {
                if (scanner != null)
                    scanner.close();
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

    public String getMaxEventPace() {
        return appProps.getProperty("max.event.pace");
    }

	// env
	public Properties getEnv() {
		return envProps;
	}

	// e-mail
	public Properties getEmail() {
		return emailProps;
	}

	// exclusionkeywords
	public Vector<String> getExclusionKeywords() {
		return exclusionKeywords;
	}
	
	// exclusionkeywords
	public Properties getExclustionFilenames() {
		return exclusionFilenamesProps;
	}

    // envrecepient
    public Properties getEnvRecipient() {
        return envRecipientProps;
    }
}
