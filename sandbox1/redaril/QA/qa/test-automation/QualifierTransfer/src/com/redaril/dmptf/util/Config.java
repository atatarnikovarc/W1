package com.redaril.dmptf.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
	private static Config instance = new Config();
		
	public static Config getInstance() {
		return instance;
	}
	
	private Properties appProps = new Properties();
	private Properties runtimeProps = new Properties();
	
	private Config() {
		loadALLProperties();
	}


	private void loadALLProperties() {
		loadProperties("config/app.properties", appProps);
		loadProperties("config/runtime.properties", runtimeProps);
	}


	private void loadProperties(String filename, Properties props) {
		InputStream in = null;

		try {
			// app.properties should be on classpath
			in = ClassLoader.getSystemResourceAsStream(filename);
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
	
	//app.properties
	
	//BDP
	public String getBbpIpPath() {
		return runtimeProps.getProperty("bdp.ip.path");
	}
	
	public String getBbpIpTxtPath() {
		return runtimeProps.getProperty("bdp.ip.txt.path");
	}
	
	//ETL
	public String getVarLogEtlPath() {
		return runtimeProps.getProperty("var.log.etl.path");
	}
	
	//runtime.properties
	public String getSeleniumHost() {
		return runtimeProps.getProperty("selenium.host");
	}
	
	public String getSeleniumPort() {
		return runtimeProps.getProperty("selenium.port");
	}
	
	//legacy properties - app.properties
	public String getHtmlFolder() {
		return appProps.getProperty("html.folder");
	}
	
	public String getRequestAddrPrefixHttp() {
		return appProps.getProperty("request.addr.prefix.http");
	}
	
	public String getRequestAddrPrefixHttps() {
		return appProps.getProperty("request.addr.prefix.https");
	}
	
	public String getCheckCookiesAddr() {
		return appProps.getProperty("check.cookies.addr");
	}
	
	public String getBaseAddr() {
		return appProps.getProperty("base.addr");
	}
	
	public String getLogFilename() {
		return appProps.getProperty("log.filename");
	}
	
	public String getLastPixelIdFilename() {
		return appProps.getProperty("last.pixel.id.filename");
	}
	
	public String getCampaignPixelPrefix() {
		return appProps.getProperty("campaign.pixle.prefix");
	}
	
	public String getDataPixelPrefix() {
		return appProps.getProperty("data.pixel.prefix");
	}
	
	public String getAddr() {
		return appProps.getProperty("addr");
	}
	
	public String getAlertMsgWithoutOptOut() {
		return appProps.getProperty("alert.msg.without.optout");
	}
	
	public String getAlertMsgWithOptOut() {
		return appProps.getProperty("alert.msg.with.optout");
	}
	
	public String getRaMainAddr() {
		return appProps.getProperty("ra.mainaddr");
	}
}
