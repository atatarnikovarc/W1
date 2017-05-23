package com.redaril.JIRA2764;

import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;

public class Config {
    public static void main(String[] args) {
        final String s = getProperty("property.name");
        System.out.println(s);
    }

    private static PropertyResourceBundle propertyResourceBundle = null;

    static {
        try {
            propertyResourceBundle = new PropertyResourceBundle(new FileReader("JIRA2764.properties"));
        } catch (IOException e) {
        	System.out.println(e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static String getProperty(final String name) {
        return propertyResourceBundle.getString(name);
    }
}
