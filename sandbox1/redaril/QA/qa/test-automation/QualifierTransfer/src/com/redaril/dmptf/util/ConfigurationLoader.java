package com.redaril.dmptf.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.lang.String;

import org.apache.log4j.Logger;


public class ConfigurationLoader {

	private PropertyResourceBundle propertyResourceBundle = null;
	private final static Logger LOG = Logger.getLogger(ConfigurationLoader.class);

	public ConfigurationLoader(String configPath) {
		try {
            propertyResourceBundle = new PropertyResourceBundle(new FileReader(configPath));
        } catch (IOException e) {
            LOG.error("Exception: " + e.getMessage());
        }
	}
    
    public String getProperty(final String name) {
        return propertyResourceBundle.getString(name);
    }

}
