package com.redaril.dmptf.tests.testnotready.qualifiers;

import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.network.lib.httpunit.HttpUnitWrapper;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class Basic {
    private final static String PATH_COMMON = File.separator + "qualifiers" + File.separator + "validation" + File.separator + "basic" + File.separator;
    private final static String PATH_RESOURCE = "data" + PATH_COMMON;
    private final static String PATH_OUTPUT = "output" + PATH_COMMON;
    private final static String PATH_RESULT = "result" + PATH_COMMON;
    private final static String FILE_PROPERTIES = "qualifiers.properties";
    private final static String FILE_SQLITE_DB = "basic_qualifiers.db";
    private final static String FILE_RESULT_REGULAR = "basic_qualifiers_validation_result.txt";
    private static ConfigurationLoader config;
    private static String PARTNERSINFO_URL;
    private static String PARTNERSINFO_URL_EX;
    private String QUALIFIER_URL_PREFIX;
    private List<String> failedInterest;
    private int passedInterest;
    private static Connection sqlite;
    protected static HttpUnitWrapper session;
    private static Logger LOG;
    private final static String logFile = "Basic.log";
    protected static Boolean isInit = false;
    protected final static String LogSystemProperty = "DmptfLogFile";
    private final static String FILE_PROPERTIES_ENV = "env.properties";
    protected static ConfigurationLoader configEnv;
    protected final static String PATH_CONFIG = "testClassConfigurationLoader" + File.separator;
    protected static String ENV;
    protected static String CLUSTER;
    protected final static String ucookie = "u";
    protected final static String ocookie = "o";

    @Before
    public void setUp() {
        if (!isInit) {
            System.setProperty(LogSystemProperty, logFile);
            configEnv = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
            ENV = configEnv.getProperty("env");
            CLUSTER = configEnv.getProperty("configID");
            LogConfigurer.initLogback();
            config = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES);
            PARTNERSINFO_URL = "http://" + ENV + ".west.p.raasnet.com:8080/partners/info";
            PARTNERSINFO_URL_EX = PARTNERSINFO_URL + "?ex=1";
            LOG = LoggerFactory.getLogger(Basic.class);
            session = new HttpUnitWrapper();
            isInit = true;
        }
    }

    private void setPID(String PID) {
        QUALIFIER_URL_PREFIX = "http://" + ENV + ".west.p.raasnet.com:8080/partners/universal/in?pid=" + PID + "&ndl=";
    }

    @Test
    public void test() {
        File outputFolder = new File(PATH_OUTPUT);
        outputFolder.mkdirs();
        File resultFolder = new File(PATH_RESULT);
        resultFolder.mkdirs();
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            fail("Can't find JDBC class");
        }
        try {
            sqlite = DriverManager.getConnection("jdbc:sqlite:" + PATH_RESOURCE + FILE_SQLITE_DB);
        } catch (SQLException e) {
            fail("Can't connect to DB. Connection string = " + "jdbc:sqlite:" + PATH_RESOURCE + FILE_SQLITE_DB);
        }
        LOG.debug("DB path: " + PATH_RESOURCE + FILE_SQLITE_DB);
        passedInterest = 0;
        failedInterest = new ArrayList<String>();
        String str;
        LOG.info("- CHECK BASIC QUALIFIERS -------------------");
        checkQualifiers();
        if (failedInterest.size() > 0) {
            LOG.error("Some error was happened... Errors Count = " + failedInterest.size());
        }
        // make report
        str = "- SUMMARY basic qualifiers -----------------\r\n" + "" +
                "Qualifiers count: " + passedInterest + "\r\n" +
                "Interest category failed: " + failedInterest.size() + "\r\n";
        appendStringToFile(PATH_RESULT + FILE_RESULT_REGULAR, str);
        LOG.info(str);
        if (failedInterest.size() > 0) {
            str = "Fail List:";
            appendStringToFile(PATH_RESULT + FILE_RESULT_REGULAR, str);
            LOG.info(str);
            for (String aFailedInterest : failedInterest) {
                str = "		" + aFailedInterest;
                appendStringToFile(PATH_RESULT + FILE_RESULT_REGULAR, str);
                LOG.info(str);
            }
        }
        str = "--------------------------------------------";
        appendStringToFile(PATH_RESULT + FILE_RESULT_REGULAR, str);
        LOG.info(str);
        LOG.info("FINISH.");
        assertTrue(failedInterest.size() == 0);
    }

    private void checkQualifiers() {
        String qualifier = "";
        String userModel;
        Statement stat;
        Statement stat2;
        int totalRowsCount = getCount("url");
        if (totalRowsCount == 0) {
            LOG.error("basic_qualifiers.db is empty");
            fail();
        } else {
            int verified = 0;
            int queue;
            while (verified < totalRowsCount) {
                try {
                    stat = sqlite.createStatement();
                    ResultSet rs = stat.executeQuery("select * from url;");
                    while (rs.next()) {
                        LOG.debug("Check qualifier: " + rs.getString("url"));
                        /* DELETE REDARIL COOKIES */
                        session.goToUrl(PARTNERSINFO_URL);
                        session.deleteAllCookies();
                        /* COMPILE QUALIFIER */
                        LOG.debug("/* COMPILE QUALIFIER */");
                        try {
                            qualifier = URLEncoder.encode(rs.getString("url"), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            fail("Can't encode URL of qualifier. URL = " + rs.getString("url"));
                        }
                        // get pid for request
                        // if qualifier owner INC.COM use pixel with same owner
                        if (rs.getString("url").contains("inc.com")) {
                            setPID(config.getProperty("inc.com.pixelID"));
                        } else {
                            setPID(config.getProperty("redaril.pixelID"));
                        }
                        /* EXECUTE REQUEST */
                        LOG.debug("/* EXECUTE REQUEST */");
                        LOG.debug("URL: " + QUALIFIER_URL_PREFIX + qualifier);
                        session.goToUrl(QUALIFIER_URL_PREFIX + qualifier);
                        /* CHECK USER MODEL */
                        LOG.debug("/* CHECK USER MODEL */");
                        userModel = session.getResponsePage(PARTNERSINFO_URL_EX);
                        String ocook = session.getCookieValueByName(ocookie);
                        String ucook = session.getCookieValueByName(ucookie);
                        LOG.debug("cookies: o=" + ocook + "; u=" + ucook + ";\r\n" +
                                "userModel:\r\n" + userModel);
                        stat2 = sqlite.createStatement();
                        ResultSet interest = stat2.executeQuery("select * from interest " +
                                "where urlID = " + rs.getString("rowID") + ";");
                        while (interest.next()) {

                            if (userModel.contains(interest.getString("interest"))) {
                                LOG.info("interestCategory: \"" +
                                        interest.getString("interest") + "\" is PASSED");
                                passedInterest++;
                            } else {
                                LOG.error("interestCategory: \"" +
                                        interest.getString("interest") + "\" is FAILED");
                                failedInterest.add("qalifier: " + rs.getString("url") +
                                        "; interest: " + interest.getString("interest") + ";");
                            }
                        }
                        interest.close();
                        verified++;
                        queue = totalRowsCount - verified;
                        LOG.info("Verified qaulifiers: " + verified + "; " +
                                "in the queue: " + queue + "; " +
                                "interest passed: " + passedInterest + "; " +
                                "interest failed: " + failedInterest.size() + ";");
                    }
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void appendStringToFile(String filename, String msg) {
        try {
            File file = new File(filename);
            FileWriter writer = new FileWriter(file, true);
            writer.write(msg + "\r\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            LOG.error("Can't write results into file = " + filename);
        }
    }

    private static int getCount(String table) {
        int res = 0;
        Statement stat = null;
        try {
            stat = sqlite.createStatement();
            ResultSet rs = stat.executeQuery("select count(*) from " + table + ";");
            while (rs.next()) {
                res = Integer.parseInt(rs.getString(1));
            }
            rs.close();
        } catch (SQLException e) {
            fail("Can't get count of qualifiers from DB");
        }

        return res;
    }
}
