package com.redaril.dmptf.util.configuration;

import org.apache.log4j.PropertyConfigurator;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 30.01.13
 * Time: 13:10
 * To change this template use File | Settings | File Templates.
 */
public class LogConfigurer {
    private final static String FILE_PROPERTIES_LOG4J = "log4j.properties";
    private final static String PATH_CONFIG = "config" + File.separator;
    protected final static String LogSystemProperty = "DmptfLogFile";

    public static void initLog4j() {
        //delete previous log file
        String filename = System.getProperty(LogSystemProperty);
        if (filename == null) {
            System.out.println("ERROR: "+ LogSystemProperty +" is not defined. Log4J is unavailable");
        } else {
            File f = new File("./output/logs/"+filename);
            f.delete();
        }
        //init log4j
        File propertiesFile = new File(PATH_CONFIG, FILE_PROPERTIES_LOG4J);
        PropertyConfigurator.configure(propertiesFile.toString());
       // Logger LOG = Logger.getLogger(LogConfigurer.class);
       // LOG.info("Log4j has been initialized from file " + propertiesFile);
       // LOG.info("Log file = " + filename);
    }
}
