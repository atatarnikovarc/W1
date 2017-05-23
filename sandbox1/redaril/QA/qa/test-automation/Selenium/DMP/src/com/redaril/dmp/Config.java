package com.redaril.dmp;

import java.io.FileReader;
import java.io.IOException;
import java.lang.String;
import java.util.PropertyResourceBundle;

public class Config {
    private PropertyResourceBundle propertyResourceBundle = null;

    public Config(String fileName) {
        try {
            propertyResourceBundle = new PropertyResourceBundle(new FileReader(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(final String name) {
        return propertyResourceBundle.getString(name);
    }
}