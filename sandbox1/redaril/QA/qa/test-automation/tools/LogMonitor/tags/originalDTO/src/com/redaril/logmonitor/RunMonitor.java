package com.redaril.logmonitor;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.redaril.logmonitor.core.Environment;

public class RunMonitor {
	private static final Logger logger = Logger.getLogger(RunMonitor.class.getName());
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		(new Thread(new Environment())).start();
		
		while (true) {
			try {
				Thread.sleep(120000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.log(Level.SEVERE, "can't sleep for a while ");
				e.printStackTrace();
			}
		}
	}

}
