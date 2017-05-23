package com.redaril.dmptf.util.configuration;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;

public class LogConfigurer {
//    protected final static String LogSystemProperty = "DmptfLogFile";

    //    public static void initLog4j() {
//        //delete previous log file
//        String filename = System.getProperty(LogSystemProperty);
//        if (filename == null) {
//            System.out.println("ERROR: "+ LogSystemProperty +" is not defined. Log4J is unavailable");
//        } else {
//            File f = new File("./output/logs/"+filename);
//            f.delete();
//        }
//        //init log4j
//        File propertiesFile = new File(PATH_CONFIG, LOGBACK_XML);
//        PropertyConfigurator.configure(propertiesFile.toString());
//        Logger LOG = LoggerFactory.getLogger(LogConfigurer.class);
//        LOG.info("Log4j has been initialized from file " + propertiesFile);
//        LOG.info("Log file = " + filename);
//    }
    public static void initLogback() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        ContextInitializer ci = new ContextInitializer(lc);
        lc.reset();
        try {
            ci.autoConfig();
        } catch (JoranException e) {
            e.printStackTrace();
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
    }
}

