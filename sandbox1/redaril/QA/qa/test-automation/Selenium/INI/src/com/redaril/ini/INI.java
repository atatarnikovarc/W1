package com.redaril.ini;

/* INI file parser */

import java.io.*;
import java.util.*;

public class INI {

Properties iniProperty = new Properties();

public INI(File f) throws IOException { this( f.getPath() ); }
public INI(String fname) throws IOException { loadFile( fname ); }

	private void loadFile( String fname ) throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fname)));
		
		try {
			String section = "";
			
			String line;
			
			while( (line = br.readLine())!=null ) {
				if( line.startsWith(";") ) continue;
				
				if( line.startsWith("[") ) {
					section = line.substring(1,line.lastIndexOf("]")).trim();
					continue;
				}
				
				addProperty(section,line);
			}
		} finally { br.close(); }
	}
	
	private void addProperty(String section,String line) {
		int equalIndex = line.indexOf("=");
	
		if( equalIndex > 0 ) {
			String name = section+'.'+line.substring(0,equalIndex).trim();
			String value = line.substring(equalIndex+1).trim();
			
			iniProperty.put(name,value);
		}
	}

	public String getProperty(String section,String var) {
		return iniProperty.getProperty(section+'.'+var);
	}

	public String getProperty(String section,String var,String def) {
		return iniProperty.getProperty(section+'.'+var,def);
	}

	public int getProperty(String section,String var,int def) {
		String sval = getProperty(section,var,Integer.toString(def));
		
		return Integer.decode(sval).intValue();
	}

	public boolean getProperty(String section,String var,boolean def) {
		String sval = getProperty(section,var,def ? "True":"False");
		
		return sval.equalsIgnoreCase("Yes") || sval.equalsIgnoreCase("True");
	}
}