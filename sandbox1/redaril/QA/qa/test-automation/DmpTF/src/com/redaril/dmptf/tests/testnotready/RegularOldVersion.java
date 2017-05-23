//package com.redaril.dmptf.tests.testnotready;
//
//
//import com.redaril.dmptf.util.configuration.ConfigurationLoader;
//import com.redaril.dmptf.util.database.oracle.OracleWrapper;
//import com.redaril.dmptf.util.database.sqlite.RowSet;
//import com.redaril.dmptf.util.database.sqlite.SqliteWrapper;
//import com.redaril.dmptf.util.network.lib.httpunit.HttpUnitWrapper;
//
//import java.io.*;
//import java.net.URLEncoder;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Calendar;
//import java.util.Date;
//
//import org.slf4j.Logger;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;
//
//public class RegularOldVersion{
//
//    private static Logger LOG;
//    private static final String PATH_COMMON = File.separator + "qualifiers" + File.separator + "validation" + File.separator + "regular" + File.separator;
//    private static final String PATH_RESOURCE = "data" + PATH_COMMON;
//    private static final String PATH_OUTPUT = "output" + PATH_COMMON;
//    private static final String PATH_RESULT = "result" + PATH_COMMON;
//    private static final String FILE_SQLITE_DB = "regular_qualifiers.db";
//    private static final String DATE = getDATE();
//    private static String PARTNERSINFO_URL;
//    private static String PARTNERSINFO_URL_EX;
//    private static String QUALIFIER_URL_PREFIX;
//    private static final String FILE_RESULT_REGULAR = "regular_qualifiers_validation_result.txt";
//    private List<String> failedQualifiers;
//    private int passedQualifiers;
//    private static HashMap<String, String> dataSourceMap = new HashMap<String, String>();
//    private static OracleWrapper oracle;
//    private final static String logFile = "regular.log";
//    protected final static String testClassProperties = "qualifiers.properties";
//    protected static ConfigurationLoader testClassConfigurationLoader;
//    protected static HttpUnitWrapper session;
//    protected static SqliteWrapper sqlite;
//    protected static Boolean isInit = false;
//
//    @Before
//    public void setUp() {
//        if (!isInit) {
//            System.setProperty(LogSystemProperty, logFile);
//            testClassConfigurationLoader = new ConfigurationLoader(CONFIG_PATH + testClassProperties);
//            super.setUp();
//            PARTNERSINFO_URL = "http://" + ENV + ".west.p.raasnet.com:8080/partners/info";
//            PARTNERSINFO_URL_EX = "http://" + ENV + ".west.p.raasnet.com:8080/partners/info?ex=1";
//            QUALIFIER_URL_PREFIX = "http://" + ENV + ".west.p.raasnet.com:8080/partners/universal/in?pid=";
//            LOG = LoggerFactory.getLogger(RegularOldVersion.class);
//            oracle = new OracleWrapper("csc");
//            session = new HttpUnitWrapper();
//            sqlite = new SqliteWrapper();
//            isInit = true;
//        }
//    }
//
//    private static String getDATE() {
//        try {
//            return testClassConfigurationLoader.getProperty("DATE");
//        } catch (Exception e) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.WEEK_OF_YEAR, -2);
//            Date date = calendar.getTime();
//            DateFormat format = new SimpleDateFormat("dd.MM.yy");
//            return format.format(date);
//
//        }
//    }
//
//    @Test
//    public void testRegularQualifiers() {
//
//        File outputFolder = new File(PATH_OUTPUT);
//        outputFolder.mkdirs();
//        File resultFolder = new File(PATH_RESULT);
//        resultFolder.mkdirs();
//
//        LOG.info("- PROPERTIES -------------------------------");
//        LOG.info("ENV: " + ENV);
//        LOG.info("envConfigID: " + envConfigID);
//        LOG.info("DATE: " + DATE);
//
//        LOG.info("- INIT SQLITE DB ---------------------------");
//        sqlite.setOutputPath(PATH_OUTPUT);
//       // sqlite.setResourcePath(PATH_RESOURCE);
//        sqlite.setSqliteDBFile(FILE_SQLITE_DB);
//        sqlite.executeSqlScriptFile("initDB.sql");
//
//        LOG.info("- INIT ORACLE DB ---------------------------");
//        LOG.info("- GET NEW QUALIFIERS FROM DATE " + DATE + " ------");
//        if (!oracle.getNewRegularQualifiers(DATE)) {
//            LOG.error("Some error was happened in getNewQualifiers date= " + DATE);
//            fail();
//        }
//
//        passedQualifiers = 0;
//        failedQualifiers = new ArrayList<String>();
//        LOG.info("- CHECK NEW QUALIFIERS ---------------------");
//        checkQualifiers();
//        if (failedQualifiers.size() > 0) {
//            LOG.error("Some error was happened... Errors Count = " + failedQualifiers.size());
//        }
//
//        // make report
//        String str = "- SUMMARY new regular qualifiers -----------\r\n" + "" +
//                "Qualifiers checked from : " + DATE + " to current date\r\n" +
//                "Qualifiers passed: " + passedQualifiers + "\r\n" +
//                "Qualifiers failed: " + failedQualifiers.size() + "\r\n";
//        appendStringToFile(PATH_RESULT + FILE_RESULT_REGULAR, str);
//        LOG.info(str);
//        if (failedQualifiers.size() > 0) {
//            str = "ID List of failed qualifiers:";
//            appendStringToFile(PATH_RESULT + FILE_RESULT_REGULAR, str);
//            LOG.info(str);
//            for (String failedQualifier : failedQualifiers) {
//                str = "		" + failedQualifier;
//                appendStringToFile(PATH_RESULT + FILE_RESULT_REGULAR, str);
//                LOG.info(str);
//            }
//        }
//        str = "--------------------------------------------";
//        appendStringToFile(PATH_RESULT + FILE_RESULT_REGULAR, str);
//        LOG.info(str);
//
//        LOG.info("FINISH.");
//        assertTrue(failedQualifiers.size() == 0);
//    }
//
//    private void checkQualifiers() {
//
//        String qualifier = "";
//        String userModel;
//        String URL;
//
//        int totalRowsCount = sqlite.getRowCount("toCheck");
//        if (totalRowsCount == 0) {
//            LOG.info("All qualifiers were verified earlier. " +
//                    "If you want to verify them again set properties: ignoreChecked=false");
//        } else {
//
//            int verified = 0;
//            int queue = totalRowsCount;
//
//            int rowID = 0;
//            int prevRowID = 0;
//
//            String ocook;
//            String ucook;
//
//            while (queue > 0) {
//                RowSet rowSet = sqlite.executeQuery("select * from toCheck where rowID > " + prevRowID + " limit 1;");
//                while (rowSet.next()) {
//                    if (prevRowID != Integer.parseInt(rowSet.getRow("rowID"))) {
//                        LOG.debug("Check qualifier: " + rowSet.getRow("url"));
//                        session.goToUrl(PARTNERSINFO_URL);
//                        try {
//                            session.deleteAllCookies();
//                        } catch (Exception e) {
//                            LOG.error("Exception: " + e.getMessage());
//                        }
//
//                        /* COMPILE QUALIFIER */
//                        LOG.debug("/* COMPILE QUALIFIER */");
//                        try {
//                            qualifier = URLEncoder.encode(rowSet.getRow("url"), "UTF-8");
//                        } catch (UnsupportedEncodingException e) {
//                            LOG.error("Exception: " + e.getMessage());
//                        }
//
//                        if (dataSourceMap.get(rowSet.getRow("dataSource")) == null) {
//                            LOG.debug("Get pixel ID from dmp by data_source= " + rowSet.getRow("dataSource"));
//                            String pixelID = oracle.getPixelIDbyDataSource(rowSet.getRow("dataSource"));
//                            LOG.debug("PixelID= " + pixelID);
//                            dataSourceMap.put(rowSet.getRow("dataSource"), pixelID);
//                        }
//
//                        URL = QUALIFIER_URL_PREFIX +
//                                dataSourceMap.get(rowSet.getRow("dataSource")) + "&ndr=" + qualifier;
//
//                        /* EXECUTE REQUEST */
//                        LOG.debug("/* EXECUTE REQUEST */");
//                        LOG.debug("URL: " + URL);
//
//                        //GoToUrl don`t need try/catch here because try/catch is in HttpUnitWrapper
//                        session.goToUrl(URL);
//
//                        //TODO: if cluster reboot skip testing
//
//                        /* CHECK USER MODEL */
//                        LOG.debug("/* CHECK USER MODEL */");
//
//                        //GetResponse don`t need try/catch here because try/catch is in HttpUnitWrapper
//                        userModel = session.getResponsePage(PARTNERSINFO_URL_EX);
//                        ocook = session.getCookieValueByName(ocookie);
//                        ucook = session.getCookieValueByName(ucookie);
//
//                        LOG.debug("cookies: o=" + ocook + "; u=" + ucook + ";\r\n" +
//                                "userModel:\r\n" + userModel);
//
//                        if (userModel.contains(rowSet.getRow("interest").trim())) {
//                            LOG.info("interestCategory: \"" + rowSet.getRow("interest") + "\"" +
//                                    " for qualifier ID = " + rowSet.getRow("id") + " is PASSED");
//                            passedQualifiers++;
//
////                            sqlite.executeUpdate("insert into checked " +
////                                    "select * from toCheck " +
////                                    "where id = \"" + rowSet.getRow("id") + "\";");
//                            sqlite.executeUpdate("insert into checked values (null, " +
//                                    "\"" + rowSet.getRow("id") + "\"," +
//                                    "\"" + rowSet.getRow("url") + "\"," +
//                                    "\"" + rowSet.getRow("interest") + "\"," +
//                                    "\"" + rowSet.getRow("interestID") + "\"," +
//                                    "\"" + rowSet.getRow("dataSource") + "\");");
//
//                            sqlite.executeUpdate("delete from toCheck " +
//                                    "where id = \"" + rowSet.getRow("id") + "\";");
//
//                        } else {
//                            // alarm... can`t found category
//                            LOG.error("interestCategory: \"" + rowSet.getRow("interest") + "\"" +
//                                    " for qualifier ID = " + rowSet.getRow("id") + " is FAILED");
//                            failedQualifiers.add(rowSet.getRow("id"));
//                        }
//
//                    }
//                    prevRowID = Integer.parseInt(rowSet.getRow("rowID"));
//                    rowID++;
//                    verified++;
//                }
//
//                queue = totalRowsCount - verified;
//
//                LOG.info("SUMMARY: Verified: " + verified + "; " +
//                        "in the queue: " + queue + "; " +
//                        "passed: " + passedQualifiers + "; " +
//                        "failed: " + failedQualifiers.size() + ";");
//                LOG.info("");
//                LOG.info("");
//
//            }
//        }
//    }
//
//    private void appendStringToFile(String filename, String msg) {
//        try {
//            File file = new File(filename);
//            FileWriter writer = new FileWriter(file, true);
//            writer.write(msg + "\r\n");
//            writer.flush();
//            writer.close();
//        } catch (Exception e) {
//            LOG.error(e.getMessage());
//        }
//    }
//
//}
