package com.redaril.logmonitor.core.monitor;

import java.io.File;
import java.util.*;

import com.redaril.logmonitor.core.environment.Environment;
import com.redaril.logmonitor.core.file.FileHelper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.redaril.logmonitor.util.config.Config;

public class Monitor extends Observable implements Runnable {
	private static final Logger logger = Logger.getLogger(Monitor.class);
	private volatile boolean flag = true;
    private File file;
	private Thread thread = null;
    private String envName;
    private String fileName;
    private MonitorHelper monitorHelper;

    public String getEnvName() { return envName; }

    public String getFileName() { return fileName; }

    File getFile() { return this.file; }

	public Monitor(String env, String fileName,String path) {
		this.file = new File(path);
        this.fileName = fileName;
        this.envName = env;
        this.monitorHelper = new MonitorHelper(this);
	}

	public void run() {
		long previousLength = 0;
        logger.log(Level.INFO,"Check file = " + file.getName());
		while (this.flag) {
			try {
				if (file.canRead()) {
					long currentLength = this.file.length();
					if (currentLength > previousLength) {// read difference in
															// length,
															// previousLenght
															// File.length()
						String change = monitorHelper.getChange(previousLength, currentLength);
						monitorHelper.checkChange(change);
						previousLength = currentLength;
					} else if (currentLength < previousLength) {// read from the
																// beginning,
																// previous
																// length to 0
						previousLength = 0;
						String change = monitorHelper.getChange(0, currentLength);
						monitorHelper.checkChange(change);
					}
				} else {// file inaccessible, stop the thread
                    stop();
					break;
				}
				Thread.sleep(Config.getInstance().getMonitorInterval());
			} catch (InterruptedException e) {
				logger.warn("Can't sleep for a while ", e);
            } catch (RuntimeException e) {
                logger.error(e.getMessage(), e);
                throw e;
            }
		}
	}
	
	public void start() {
		if (this.thread == null) {
			this.thread = new Thread(this);
			this.thread.start();
		}
	}

    public void stop(){
        logger.warn("Interrupt monitor for " + getEnvName() + " " + getFileName());
        FileHelper.deleteRecordFromAvMonitors(getEnvName() + " " + getFileName());
        setChanged();
        notifyObservers(this);
        clearChanged();
    }

	public void done() {
		this.flag = false;
	}
}
