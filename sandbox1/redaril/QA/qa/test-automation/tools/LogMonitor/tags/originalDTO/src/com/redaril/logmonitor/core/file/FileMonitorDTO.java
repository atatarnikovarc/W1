package com.redaril.logmonitor.core.file;

public final class FileMonitorDTO {
	private String envName;
	private String fileName;
	
	public FileMonitorDTO(String env, String file) {
		this.envName = env;
		this.fileName = file;
	}

	public String getEnvName() { return envName; }
	
	public String getFileName() { return fileName; }
}
