package util;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

//provides application with access to properties
public class Config {
	private static Config instance = new Config();
	
	private Properties props = new Properties();
	
	public static Config getInstance() {
		return instance;
	}
	
	private Config() {
		InputStream in = null;
		
		try {
			//app.properties should be on classpath
			in = ClassLoader.getSystemResourceAsStream("app.properties");
			props.load(in);
		} catch (IOException e) {
			ExceptionHandler.castToRuntime("Cannot load config: ", e);
		}
		finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					
				}
			}
		}
	}
	
	public String getTestScriptFolder() {
		return props.getProperty("testScriptFolder");
	}
	
	public String getIsResizeTestScript() {
		return props.getProperty("resizeTestScript");
	}
}
