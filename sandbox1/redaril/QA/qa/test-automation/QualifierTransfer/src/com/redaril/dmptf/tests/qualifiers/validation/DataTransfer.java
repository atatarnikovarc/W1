package com.redaril.dmptf.tests.qualifiers.validation;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.redaril.dmptf.util.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.jmx.JMX;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataTransfer extends TestCase {
    private final static String PATH_CONFIG = "config" + File.separator;
    private final static String FILE_PROPERTIES = "transfer.properties";
    private static ConfigurationLoader config;
    private static Logger log;
    private static String ENV;
    private static String CLUSTER;
    protected final static String LogSystemProperty = "DmptfLogFile";
    private final static String logFile = "transfer.log";

    @Test
    public void test() throws Exception {
        System.setProperty(LogSystemProperty, logFile);
        LogConfigurer.initLog4j();
        log = Logger.getLogger(DataTransfer.class);
        config = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES);
        ENV = config.getProperty("env");
        CLUSTER = config.getProperty("cluster");
        log.info("Started at " + getDateWithTime());
        log.info("- IMPORT NEW QUALIFIERS FROM BUN DB --------");
        log.info("Synchronize prod -> env3");
        cleanAppLog();
        importNewQualifiers();
        List<String> errors = getErrors();
        if (errors.size() > 0) {
            log.error("Synchronization was finished unsuccessfully due to errors: ");
            for (String error : errors) {
                log.error(error);
            }
        }
        assertTrue("", errors.size() == 0);
        log.info("Synchronization successfully executed");
        JMX jmx = new JMX(ENV);
        boolean isReboot;
        isReboot = jmx.reboot("taxonomyLoader");
        if (isReboot) log.info("Reboot taxonomyLoader successfully executed");
        else log.info("Reboot taxonomyLoader FAILED");

//        isReboot = jmx.reboot("QualifierUpdaterJob");
//        if (isReboot) log.info("Reboot QualifierUpdaterJob successfully executed");
//        else log.info("Reboot QualifierUpdaterJob FAILED");
        isReboot = reloadQualUpdJob();
        if (isReboot) log.info("Reboot QualifierUpdaterJob successfully executed");
        else log.info("Reboot QualifierUpdaterJob FAILED");

        isReboot = jmx.reboot("partners", CLUSTER);
        if (isReboot) log.info("Reboot partners successfully executed");
        else log.info("Reboot partners FAILED");

        isReboot = jmx.reboot("CST");
        if (isReboot) log.info("Reboot CST successfully executed");
        else log.info("Reboot CST FAILED");
        log.info("QualifierTransfer completed");
    }

    private boolean reloadQualUpdJob() {
        log.info("Start to reboot QualifierUpdaterJob");
        WebConversation wc = new WebConversation();
        try {
            String url = "http://" + ENV + "." + CLUSTER + ".p.raasnet.com:8080/dmpmodel/uq?status=93&status=95";
            log.info("Go to "+ url);
            WebResponse response = wc.getResponse(url);
            String text = response.getText();
            log.info("============RESPONCE============");
            log.info(text);
            log.info("===============END==============");
            return text.contains("Qualifiers of type GeneralSearchQualifier disabled") &&
                    text.contains("Qualifiers of type RegexQualifier disabled") &&
                    text.contains("Qualifiers type #55 loaded") &&
                    text.contains("Qualifiers type #61 loaded");

        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        } catch (SAXException e) {
            log.error(e.getLocalizedMessage());
        }
        return true;
    }

    private void cleanAppLog() {
        Connection dbConn = dataBaseConnect(getDBLogin("meta"), getDBPassword("meta"));
        if (dbConn == null) {
            log.error("Data base connection FAILED");
            System.exit(1);
        }
        log.debug("Data base connection OK");
        try {
            Statement statement = dbConn.createStatement();
            statement.executeUpdate("delete from APPLICATION_LOG");
            log.info("Clean APPLICATION_LOG");
            statement.close();
            dbConn.close();
        } catch (SQLException e) {
            log.error("Exception:" + e.getMessage());
        }
    }

    private List<String> getErrors() {
        List<String> errors = new ArrayList<String>();
        Connection dbConn = dataBaseConnect(getDBLogin("meta"), getDBPassword("meta"));
        if (dbConn == null) {
            errors.add("Data base connection FAILED");
            return errors;
        }
        ResultSet set;
        log.info("Get errors after synchronization");
        try {
            Statement statement = dbConn.createStatement();
            set = statement.executeQuery("select err_msg from APPLICATION_LOG");
            while (set.next()) {
                if (set.getString(1) != null && set.getString(1).contains("ORA")) {
                    errors.add(set.getString(1));
                }
            }
            statement.close();
            dbConn.close();
        } catch (SQLException e) {
            errors.add(e.getLocalizedMessage());
        }
        return errors;
    }

    private String getDBLogin(String dbName) {
        return ENV + "_" + dbName;
    }

    private String getDBPassword(String dbName) {
        return ENV + "_" + dbName;
    }

    private Connection dataBaseConnect(String login, String password) {
        try {
            Connection connection;

            String url = "jdbc:oracle:thin:@" +
                    config.getProperty("dbHost") + ":" +
                    config.getProperty("dbPort") + ":" +
                    config.getProperty("dbSid");

            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, login, password);
            log.debug("connecting: " + url);
            if (connection == null)
                return null;
            else
                return connection;
        } catch (ClassNotFoundException e) {
            log.error("ClassNotFoundException: " + e.getLocalizedMessage());
        } catch (SQLException e) {
            log.error("SQLException: " + e.getMessage());
        }
        return null;
    }

    private void importNewQualifiers() throws Exception {
        Connection dbConn = dataBaseConnect(getDBLogin("bun"), getDBPassword("bun"));
        if (dbConn == null) {
            log.error("Data base connection FAILED");
            System.exit(1);
        }
        log.debug("Data base connection OK");
        log.info("Start synchronization");
        try {
            CallableStatement cstmt = dbConn.prepareCall("{call BUNDB_SYNC_PRC()}");
            try {
                cstmt.execute();
                cstmt.close();
                dbConn.close();
            } catch (Exception e) {
                log.error("Exception: " + e.getMessage());
            }
        } catch (Exception e) {
            log.error("Exception:" + e.getMessage());
        }
    }

    public static String getDateWithTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 11);
        java.util.Date date = calendar.getTime();
        DateFormat format = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        return format.format(date);
    }
}
