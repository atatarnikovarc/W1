package com.redaril.logmonitor.core.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Vector;

import com.redaril.logmonitor.util.Helper;
import com.redaril.logmonitor.util.config.Config;

public class Filter implements FilenameFilter {
	private Vector<String> extensions = new Vector<String>(10);
	private Vector<String> exclusionFilenames = new Vector<String>(10);
		
	public Filter() {
		Helper.splitString(Config.getInstance().getExtensions(), ";", extensions);
		exclusionFilenames = Helper.getExclusionFilenames();
	}
	
	public boolean accept(File file, String name) {
		return (Helper.isCollectionContains(name, extensions) &&
				!Helper.isCollectionContains(name, exclusionFilenames));
	}
}
