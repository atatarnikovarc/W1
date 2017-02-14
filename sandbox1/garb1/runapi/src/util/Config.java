package util;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import util.ExceptionHandler;

public class Config {
	private static Config instance = new Config();

	private Properties props = new Properties();

	public static Config getInstance() {
		return instance;
	}

	private Config() {
		InputStream in = null;

		try {
			// app.properties should be on classpath
			in = ClassLoader.getSystemResourceAsStream("app.properties");
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
	
	public String getAppUrl() {
		return props.getProperty("app_url");
	}

	public String getCorpDbConnectionString() {
		return props.getProperty("corporate_db_connection_string");
	}

	public String getCorpDbLogin() {
		return props.getProperty("corporate_db_login");
	}

	public String getCorpDbPwd() {
		return props.getProperty("corporate_db_pwd");
	}
}