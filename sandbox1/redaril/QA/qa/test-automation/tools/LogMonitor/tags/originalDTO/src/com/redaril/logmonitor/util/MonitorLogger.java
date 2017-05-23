package com.redaril.logmonitor.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.redaril.logmonitor.util.config.Config;

public class MonitorLogger {
	public static MonitorLogger instance = new MonitorLogger();
	private Logger logger;
	
	private MonitorLogger() {
		logger = Logger.getLogger("");
		Handler handler = null;
		try {
			handler = new FileHandler("audit.log", Config.getInstance().getMaxLogSize(), 
					Config.getInstance().getLogFilesCount());
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Formatter formatterTxt = new SimpleFormatter();
		handler.setFormatter(formatterTxt);
		logger.addHandler(handler);
	}
	
	public MonitorLogger getInstance() {
		return instance;
	}
	
	public Logger getLogger() {
		return logger;
	}
}
