package com.redaril.dmptf.tests.test.qualifiers;

import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.database.oracle.OracleWrapper;
import com.redaril.dmptf.util.database.sqlite.RowSet;
import com.redaril.dmptf.util.date.DateWrapper;
import com.redaril.dmptf.util.network.appinterface.jmx.JMXWrapper;
import com.redaril.dmptf.util.network.appinterface.webservice.WSHelper;
import com.redaril.dmptf.util.network.lib.httpunit.HttpUnitWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

//flow:
//at the first we go to getParam and create parameters for our tests
//at the second we go to constructor ParamTest
//at the third we go to Test()
//then at loop 2 and 3 steps will be repeated until list will be empty

@RunWith(value = Parameterized.class)

public class TestRegular {

    private static Logger LOG;
    private static final String PATH_COMMON = File.separator + "qualifiers" + File.separator + "regular" + File.separator;
    private static final String PATH_RESOURCE = "data" + PATH_COMMON;
    private static final String PATH_OUTPUT = "output" + File.separator + "oraclewrapper" + File.separator;
    private static final String PATH_CONFIG = "config" + File.separator;
    private static final String FILE_SQLITE_DB = "regular_qualifiers.db";
    private static String PARTNERSINFO_URL;
    private static String PARTNERSINFO_URL_EX;
    private static HashMap<String, String> dataSourceMap = new HashMap<String, String>();
    private final static String logFile = "regular.log";
    protected final static String FILE_PROPERTIES_ENV = "env.properties";
    protected static ConfigurationLoader configEnv;
    protected static HttpUnitWrapper session;
    protected final static String LogSystemProperty = "DmptfLogFile";
    protected static String ENV;
    protected static String configID;
    private static OracleWrapper oracleBun;
    private final static String initDB = PATH_RESOURCE + "initDB.sql";
    private String id;//parameter of test
    private String url;//parameter of test
    private String interest;//parameter of test
    private static WSHelper wsHelper;
    protected final static String FILE_PROPERTIES_APP = "app.properties";
    private static String baseDomain;
    private final static String FILE_PROPERTIES_DATES = "date.properties";
    private static int count;
    private static int port;

    public TestRegular(String id, String url, String interest) {
        this.id = id;
        this.url = url;
        this.interest = interest;
    }

    private static void reloadPartners(String env) {
        JMXWrapper jmxWrapper = new JMXWrapper(env, configID, "pip");
        jmxWrapper.execCommand("doReload");
        jmxWrapper.waitForReloading();
    }

    @Parameterized.Parameters
    public static List<Object[]> getParam() {
        count = 0;
        System.setProperty(LogSystemProperty, logFile);
        LogConfigurer.initLogback();
        LOG = LoggerFactory.getLogger(TestRegular.class);
        configEnv = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
        ENV = configEnv.getProperty("env");
        configID = configEnv.getProperty("configID");
        ConfigurationLoader configApp = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_APP);
        port = Integer.valueOf(configApp.getProperty("httpPort"));
        baseDomain = configApp.getProperty("baseDomain");
        PARTNERSINFO_URL = "http://" + ENV + ".west.p." + baseDomain + ":" + port + "/partners/info";
        PARTNERSINFO_URL_EX = "http://" + ENV + ".west.p." + baseDomain + ":" + port + "/partners/info?ex=1";
        String bun = configEnv.getProperty("bun");
        oracleBun = new OracleWrapper(bun, "bun");
        OracleWrapper oracleDmp = new OracleWrapper(ENV, "dmp");
        session = new HttpUnitWrapper();
        String QUALIFIER_URL_PREFIX = "http://" + ENV + ".west.p." + baseDomain + ":" + port + "/partners/universal/in?pid=";
        ConfigurationLoader config = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_DATES);
        String begin = config.getProperty("begin");
        String end = config.getProperty("end");
        String checkModified = config.getProperty("checkModified");
        String modDate = "";
        if (begin.length() != 10) {
            LOG.info("Begin Date has wrong format(should be DD.MM.YYYY).");
            LOG.info("Begin Date uses default value = 01.01.2001");
            begin = "01.01.2001";
        }
        if (end.length() != 10) {
            LOG.info("End Date has wrong format(should be DD.MM.YYYY).");
            LOG.info("End Date uses default value = 31.12.2099");
            end = "31.12.2099";
        }
        if (checkModified.equalsIgnoreCase("true"))
            modDate = "or (nt_qualifier.modified_date_time >= TO_DATE('" + begin + "','dd.mm.yyyy') and nt_qualifier.modified_date_time < TO_DATE('" + end + "','dd.mm.yyyy'))";
        List<Object[]> list = new ArrayList<Object[]>();
        LOG.info("- PROPERTIES -------------------------------");
        LOG.info("| Start time: " + DateWrapper.getDateWithTime(0));
        LOG.info("| ENV: " + ENV);
        LOG.info("| ConfigID: " + configID);
        LOG.info("| bun: " + bun);
        LOG.info("| Begin Date: " + begin);
        LOG.info("| End Date: " + end);
        LOG.info("| Check modified date: " + Boolean.toString(modDate != ""));
        oracleBun.getSqlite().setOutputPath(PATH_OUTPUT);
        oracleBun.getSqlite().setSqliteDBFile(FILE_SQLITE_DB);
        oracleBun.getSqlite().executeSqlScriptFile(initDB);
        LOG.info(" GET NEW QUALIFIERS");
        if (!oracleBun.getNewRegularQualifiers(begin, end, modDate)) {
            LOG.error("Some errors was happened in getNewQualifiers");
        }
        String qualifier = "";
        String URL;
        int totalRowsCount = oracleBun.getSqlite().getRowCount("toCheck");
        if (totalRowsCount == 0) {
            LOG.info("All qualifiers were verified earlier. " +
                    "If you want to verify them again set properties: ignoreChecked=false");
        } else {
            int queue = totalRowsCount;
            int rowID = 0;
            int prevRowID = 0;
            while (queue > 0) {
                RowSet rowSet = oracleBun.getSqlite().executeQuery("select * from toCheck where rowID > " + prevRowID + " limit 1;");
                while (rowSet.next()) {
                    Object[] array = new Object[3];
                    if (prevRowID != Integer.parseInt(rowSet.getRow("id"))) {
                        array[0] = oracleBun.unswitchQuote(rowSet.getRow("id"));
                        ;
                        try {
                            qualifier = URLEncoder.encode(rowSet.getRow("url"), "UTF-8");

                        } catch (UnsupportedEncodingException e) {
                            LOG.error("Can't encode URL of qualifier. Exception: " + e.getMessage());
                            fail("Can't encode URL of qualifier.");
                        }
                        if (dataSourceMap.get(rowSet.getRow("dataSource")) == null) {
                            LOG.info("Get pixel ID from dmp by data_source= " + rowSet.getRow("dataSource"));
                            String pixelID = oracleDmp.getPixelIDbyDataSource(rowSet.getRow("dataSource"));
                            if (pixelID.equals("")) {
                                pixelID = wsHelper.createDataPixel(rowSet.getRow("dataSource"));
                                LOG.info("Create new DataPixel. ID = " + pixelID);
                                reloadPartners(ENV);
                            }
                            LOG.info("PixelID= " + pixelID);
                            dataSourceMap.put(rowSet.getRow("dataSource"), pixelID);
                        }
                        URL = QUALIFIER_URL_PREFIX + dataSourceMap.get(rowSet.getRow("dataSource")) + "&ndr=" + qualifier;
                        array[1] = oracleBun.unswitchQuote(URL);
                        array[2] = oracleBun.unswitchQuote(rowSet.getRow("interest"));
                    }
                    prevRowID = Integer.parseInt(rowSet.getRow("rowID"));
                    rowID++;
                    list.add(array);
                }
                queue--;
            }
        }
        LOG.info("- GET NEW QUALIFIERS FINISHED --------------");
        LOG.info("TEST's COUNT = " + list.size());
        return list;
    }

    @Test
    public void testRegularQualifier() {
        count++;
        String QUALIFIER_URL_PREFIX = "http://" + ENV + ".west.p." + baseDomain + ":" + port + "/partners/universal/in?pid=";
        String userModel;
        String ocook;
        String ucook;
        session.goToUrl(PARTNERSINFO_URL);
        session.deleteAllCookies();
        if (url.contains(QUALIFIER_URL_PREFIX + "&ndr"))
            fail("Can't get and create pixel for this Qualifier(ID = " + id + " )");
        session.goToUrl(url);
        userModel = session.getResponsePage(PARTNERSINFO_URL_EX);
        ocook = session.getCookieValueByName("o");
        ucook = session.getCookieValueByName("u");
        Boolean result = userModel.contains(interest.trim());
        if (result) {
            LOG.info("\r\n" + count + ". Start to check qualifier: " + id + "\r\n" + "interestCategory: " + interest + ";\r\n" + "URL: " + url + ";\r\n" + "cookie o=" + ocook + ";\r\n" + "cookie u=" + ucook + ";\r\n" + "userModel:" + userModel + "\r\n" + "qualifier ID = " + id + " is PASSED" + "\r\n" + "-----------------------------------------\r\n ");
            oracleBun.getSqlite().moveCheckedQualifier(id);
        } else {
            LOG.error("\r\n" + count + ". Start to check qualifier: " + id + "\r\n" + "interestCategory: " + interest + ";\r\n" + "URL: " + url + ";\r\n" + "cookie o=" + ocook + ";\r\n" + "cookie u=" + ucook + ";\r\n" + "userModel:" + userModel + "\r\n" + "qualifier ID = " + id + " is FAILED" + "\r\n" + "-----------------------------------------\r\n ");
        }
        assertTrue("qualifier ID = " + id + " is FAILED", result);
    }
}
