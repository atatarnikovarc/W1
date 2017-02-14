
package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


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
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
			} catch (Exception e) {// ignore
				}
			}
		}
	}

	/*
	 * public String getValue(String name) { return props.getProperty(name); }
	 */

	public String getLogsPath() {
		String path = System.getProperty("logs_path");

		if (path == null) {
			path = props.getProperty("logs_path");
		}
		return path;
	}

	public String getTestPlansPath() {
		String path = System.getProperty("tp_path");

		if (path == null) {
			path = props.getProperty("tp_path");
		}
		return path;
	}

	public long geCeProcTTL() {
		return Long.parseLong(props.getProperty("ce_proc_ttl"));
	}
	
	public String getRequirementMappingsFileName() {
		return props.getProperty("req_mapp_file");
	}
	
	public String getAnalyzerMappingsFileName() {
		return props.getProperty("analyzer_mapp_file");
	}
	
	public String getResultsDir() {
		return props.getProperty("results_folder");
	}
	
	public String getDataDir() {
		return props.getProperty("data_folder");
	}
	
	public String getConfigDir() {
		return props.getProperty("config_dir");
	}
}