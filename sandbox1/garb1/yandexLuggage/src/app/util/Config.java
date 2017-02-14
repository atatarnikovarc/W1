package app.util;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import app.util.ExceptionHandler;

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
	
	public String getDbHost() {
		return props.getProperty("db.host");
	}
	
	public String getDbPort() {
		return props.getProperty("db.port");
	}

	public String getDbUser() {
		return props.getProperty("db.user");
	}
	
	public String getDbPassword() {
		return props.getProperty("db.password");
	}
	
	public String getDbName() {
		return props.getProperty("db.name");
	}
	
	public String getLuggageFile() {
		return props.getProperty("luggage.file");
	}
	
	public String getSqlsFile() {
		return props.getProperty("test-data-sql");
	}
}