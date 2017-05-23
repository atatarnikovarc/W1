package com.redaril.logmonitor.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import com.redaril.logmonitor.core.file.Filter;
import com.redaril.logmonitor.util.config.Config;

public class Helper {
	public static String[] getLogFilenames(String path) {
		return new File(path).list(new Filter());
	}
	
	synchronized public static void splitString(String str, String delimiter, Vector<String> dest) {
		dest.addAll(Arrays.asList(str.split(delimiter, 0)));
	}
	
	synchronized public static boolean isCollectionContains(String str, Collection<String> collect) {
		Iterator<String> iterator = collect.iterator();
		
		while (iterator.hasNext()) {
			String curr = iterator.next().toLowerCase();
			if (str.toLowerCase().contains(curr)) {
				//System.out.println(str);
				return true;
			}
		}
		 
		return false;
	}
	
	synchronized public static Vector<String> getKeywords() {
		Vector<String> keywords = new Vector<String>(10);
		Helper.splitString(Config.getInstance().getKeywords(), ";", keywords);
		return keywords;
	}
	
	synchronized public static Vector<String> getExclusionKeywords() {
		Vector<String> exclusionKeywords = new Vector<String>(10);
		exclusionKeywords.addAll(Config.getInstance().getExclustionKeywords().stringPropertyNames());
		return exclusionKeywords;
	}
	
	synchronized public static Vector<String> getExclusionFilenames() {
		Vector<String> exclusionFilenames = new Vector<String>(10);
		exclusionFilenames.addAll(Config.getInstance().getExclustionFilenames().stringPropertyNames());
		return exclusionFilenames;
	}
}
