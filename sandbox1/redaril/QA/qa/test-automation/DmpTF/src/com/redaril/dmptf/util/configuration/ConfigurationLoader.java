package com.redaril.dmptf.util.configuration;

import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;

import static org.junit.Assert.fail;

public class ConfigurationLoader {

    private PropertyResourceBundle propertyResourceBundle = null;

    public ConfigurationLoader(String configPath) {
        try {
            propertyResourceBundle = new PropertyResourceBundle(new FileReader(configPath));
        } catch (IOException e) {
            e.printStackTrace();
            fail("Can't read properties from " + configPath);
        }
    }

    public String getProperty(final String name) {
        return propertyResourceBundle.getString(name);
    }

}