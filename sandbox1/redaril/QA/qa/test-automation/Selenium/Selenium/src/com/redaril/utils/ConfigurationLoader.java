package com.redaril.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.lang.String;

public class ConfigurationLoader {

	private PropertyResourceBundle propertyResourceBundle = null;

	public ConfigurationLoader(String configPath) {
		
		try {
            propertyResourceBundle = new PropertyResourceBundle(new FileReader(configPath));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
	}
    
    public String getProperty(final String name) {
        return propertyResourceBundle.getString(name);
    }

}
