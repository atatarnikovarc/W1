package com.redaril.logmonitor.core;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.redaril.logmonitor.core.file.FileMonitorDTO;
import com.redaril.logmonitor.util.Helper;
import com.redaril.logmonitor.util.config.Config;

public class Environment implements Runnable, Observer {
	private static final Logger logger = Logger.getLogger(Environment.class.getName());
	private Vector<FileMonitorDTO> currentMonitors = new Vector<FileMonitorDTO>(
			10);

	public Environment() {
		start();
	}
	
	private void start() {
		Set<Entry<Object, Object>> envs = Config.getInstance().getEnv()
				.entrySet();
		Iterator<Entry<Object, Object>> iterator = envs.iterator();

		while (iterator.hasNext()) {
			Entry<Object, Object> entry = iterator.next();
			runMonitors(entry.getKey().toString(), entry.getValue().toString(),
					Helper.getLogFilenames(entry.getValue().toString()));
		}
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(Config.getInstance().getRefreshLogFilelistInterval());
				refreshFiles();
			} catch (InterruptedException e) {
				logger.error("Can't sleep for a while : " + e);
			}
		}
	}

	private void refreshFiles() {
		// get vector with filenames
		// compare two vectors
		// if new files - create new files vector
		// run monitors for new files vector

		// form actual files list
		Set<Entry<Object, Object>> envs = Config.getInstance().getEnv()
				.entrySet();
		Iterator<Entry<Object, Object>> iterator = envs.iterator();

		Vector<FileMonitorDTO> newMonitors = new Vector<FileMonitorDTO>(5);

		boolean isFileAccessible = false;
		
		String envPath = null;

		while (iterator.hasNext()) {
			Entry<Object, Object> entry = iterator.next();

			envPath = entry.getValue().toString();
			String[] fileNames = Helper.getLogFilenames(envPath);
			if (fileNames != null) {
				List<String> currentKeyFiles = Arrays.asList(fileNames);
				for (int i = 0; i < currentKeyFiles.size(); i++) {
					String fullFileName = entry.getValue().toString()
							+ File.separator + currentKeyFiles.get(i);
					newMonitors.add(new FileMonitorDTO(entry.getKey()
							.toString(), fullFileName));
				}
				isFileAccessible = true;
			} else {
				logger.warn("log files are inaccessible, evnPath: " + envPath);
				break;
			}
		}

		if (isFileAccessible) {
			for (int i = 0; i < currentMonitors.size(); i++) { // form new
																// monitors
																// differenc
				FileMonitorDTO currDTO = currentMonitors.get(i);

				for (int j = 0; j < newMonitors.size(); j++) {
					FileMonitorDTO newMonDTO = newMonitors.get(j);
					if ((currDTO.getEnvName().equals(newMonDTO.getEnvName()))
							&& (currDTO.getFileName().equals(newMonDTO
									.getFileName())))
						newMonitors.remove(j);
				}
			}

			if (newMonitors.size() != 0) { // new
											// log
											// files
											// happen
				logger.info("new log files count: " + newMonitors.size());
				for (int i = 0; i < newMonitors.size(); i++) {
					FileMonitorDTO dto = newMonitors.get(i);
					// System.out.println("re-run monitor: " +
					// dto.getFileName());
					runOneMonitor(dto.getEnvName(), dto.getFileName());
				}
				updateFileListIntoFile();
			}
		}
	}
	
	private void updateFileListIntoFile() {
		//open for write
		//remove all content
		//put line by line actual files
	}

	private void runMonitors(String env, String envPath, String[] files) {
		String fullFileName = null;
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				fullFileName = envPath + File.separator + files[i];
				runOneMonitor(env, fullFileName);
			}
		} else 
			logger.warn("log files are inaccessible, evnPath: " + envPath);
	}

	private void runOneMonitor(String env, String fullFileName) {
		Monitor mon = new Monitor(env, fullFileName);
		mon.addObserver(this);
		currentMonitors.add(new FileMonitorDTO(env, fullFileName));
		(new Thread(mon)).start();
	}

	@Override
	public void update(Observable o, Object arg) {
		// System.out.println("update envs: " + currentMonitors.size());
		FileMonitorDTO dto = (FileMonitorDTO) arg;
		for (int i = 0; i < currentMonitors.size(); i++) {
			FileMonitorDTO forDto = currentMonitors.get(i);
			if ((forDto.getEnvName().equals(dto.getEnvName()))
					&& (forDto.getFileName().equals(dto.getFileName())))
				currentMonitors.remove(i);
		}

		// System.out.println("update envs: " + currentMonitors.size());
	}
}