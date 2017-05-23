package com.redaril.dmptf.tests.test.qualifiers;

import com.redaril.dmptf.tests.support.pip.base.BaseSeleniumTest;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.database.oracle.OracleWrapper;
import com.redaril.dmptf.util.date.DateWrapper;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.network.appinterface.jmx.JMXWrapper;
import com.redaril.dmptf.util.network.appinterface.webservice.WSHelper;
import com.redaril.dmptf.util.network.lib.httpunit.HttpUnitWrapper;
import com.redaril.dmptf.util.selenium.WebDriverWrapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 21.01.13
 * Time: 12:30
 * To change this template use File | Settings | File Templates.
 */
public class TestQualifierActuality extends BaseSeleniumTest {

    private static Logger LOG;
    private static final String PATH_COMMON = File.separator + "qualifiers" + File.separator + "regular" + File.separator;
    private static final String PATH_CONFIG = "config" + File.separator;
    private final static String logFile = "regularActual.log";
    protected final static String FILE_PROPERTIES_DATES = "date.properties";
    protected final static String FILE_PROPERTIES_ENV = "env.properties";
    protected static ConfigurationLoader config;
    protected static ConfigurationLoader configEnv;
    protected static HttpUnitWrapper session;
    protected final static String LogSystemProperty = "DmptfLogFile";
    protected static String ENV;
    protected static String configID;
    private static OracleWrapper oracleBun;
    private static OracleWrapper oracledmp;
    private static HashMap<String, String> qualifiers = new HashMap<String, String>();
    private final static String FILE_QUALIFIERS_SQL = "data" + File.separator + "qualifiers" + File.separator +
            "regular" + File.separator + "getNewRegularQualifiers.sql";
    private final static String FILE_QUALIFIER_WITH_CAT = "data" + File.separator + "qualifiers" + File.separator +
            "regular" + File.separator + "getQualifierWithCatById.sql";
    //for selenium
    private HashMap<String, String> driverInfo = new HashMap<String, String>();
    private final static String SOURCE_PATH = "data" + File.separator + "selenium" + File.separator;
    private final static String sourceDrivers = SOURCE_PATH + "drivers.txt";
    private static String PARTNERSINFO_URL_EX;
    private static String QUALIFIER_URL_PREFIX;
    private static HashMap<String, String> dataSourceMap = new HashMap<String, String>();
    private static WSHelper wsHelper;

    public void setUp() {
        System.setProperty(LogSystemProperty, logFile);
        LogConfigurer.initLogback();
        LOG = LoggerFactory.getLogger(TestQualifierActuality.class);
        configEnv = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
        ENV = configEnv.getProperty("env");
        configID = configEnv.getProperty("configID");
        ConfigurationLoader configApp = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_APP);
        int port = Integer.valueOf(configApp.getProperty("httpPort"));
        String baseDomain = configApp.getProperty("baseDomain");
        PARTNERSINFO_URL_EX = "http://" + ENV + ".west.p." + baseDomain + ":" + port + "/partners/info?ex=1";
        QUALIFIER_URL_PREFIX = "http://" + ENV + ".west.p." + baseDomain + ":" + port + "/partners/universal/in?pid=";
        String bun = configEnv.getProperty("bun");
        oracleBun = new OracleWrapper(bun, "bun");
        oracledmp = new OracleWrapper(ENV, "dmp");
        session = new HttpUnitWrapper();
        LOG.info("| PROPERTIES -------------------------------");
        LOG.info("| Start time: " + DateWrapper.getDateWithTime(0));
        LOG.info("| ENV: " + ENV);
        LOG.info("| envConfigID: " + configID);
        LOG.info("| bun: " + bun);
        //create WebDriver
        List<String> ipList = FileHelper.getInstance().getDataFromFile(sourceDrivers);
        String[] params;
        String aipList = ipList.get(0);
        params = aipList.split(";");
        for (int j = 0; j < columnNames.size(); j++) {
            driverInfo.put(columnNames.get(j), params[j]);
        }
        webDriverWrapper = new WebDriverWrapper(ENV);
    }

    private int isPageAvailable(String url) {
        HttpURLConnection httpUrlConn;
        try {
            httpUrlConn = (HttpURLConnection) new URL(url).openConnection();
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.setConnectTimeout(20000);
            httpUrlConn.setReadTimeout(20000);
            httpUrlConn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:19.0) Gecko/20100101 Firefox/19.0");
            httpUrlConn.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            httpUrlConn.addRequestProperty("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
            httpUrlConn.addRequestProperty("Accept-Encoding", "gzip, deflate");
            httpUrlConn.addRequestProperty("Connection", "keep-alive");
            httpUrlConn.connect();
            return (httpUrlConn.getResponseCode());
        } catch (Exception e) {
            return 0;
        }
    }

    private String isPageAvailableBySelenium(String url, String id) {
        if (!url.contains("http")) url = "http://" + url;
        webDriverWrapper.getPage(url);
        String newUrl = webDriverWrapper.getCurrentUrl();
        boolean isQual = checkQualification(id, newUrl);
        return newUrl + "\t" + isQual;
    }

    private static void reloadPartners(String env) {
        JMXWrapper jmxWrapper = new JMXWrapper(env, configID, "pip");
        jmxWrapper.execCommand("doReload");
        jmxWrapper.waitForReloading();
    }

    private boolean checkQualification(String id, String currentUrl) {
        try {
            List<String> params = new ArrayList<String>();
            params.add(id);
            String script = FileHelper.getInstance().getDataWithParams(FILE_QUALIFIER_WITH_CAT, params);
            ResultSet rset = oracleBun.executeSelect(script);
            rset.next();
            String interest = rset.getString(1);
            String datasource = rset.getString(4);
            if (dataSourceMap.get(datasource) == null) {
                String pixelID = oracledmp.getPixelIDbyDataSource(datasource);
                if (pixelID.equals("")) {
                    pixelID = wsHelper.createDataPixel(datasource);
                    reloadPartners(ENV);
                }
                dataSourceMap.put(datasource, pixelID);
            }
            String encodedUrl = "";
            try {
                encodedUrl = URLEncoder.encode(currentUrl, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOG.error("Can't encode url = " + currentUrl);
            }
            rset.close();
            oracleBun.closeStatement();
            String URL = QUALIFIER_URL_PREFIX + dataSourceMap.get(datasource) + "&ndr=" + encodedUrl;
            session.deleteAllCookies();
            if (URL.contains(QUALIFIER_URL_PREFIX + "&ndr"))
                LOG.error("Can't get and create pixel.");
            session.goToUrl(URL);
            String userModel = session.getResponsePage(PARTNERSINFO_URL_EX);
            return userModel.contains(interest.trim());

        } catch (SQLException e) {
            LOG.error("Can't get regular qualifier with category. Exception: " + e.getMessage());
            return false;
        }
    }

    private void createTXTReport() {
        List<String> source = FileHelper.getInstance().getDataFromFile("output" + File.separator + "logs" + File.separator + logFile);
        List<String> target = new ArrayList<String>();
        for (String str : source) {
            if (!str.contains("|")) {
                target.add(str.substring(str.indexOf("- ") + 2));
            }
        }
        FileHelper.getInstance().createFile("output" + File.separator + "logs" + File.separator + "report.log", target);
    }

    @Test
    public void testActuality() {
        setUp();
        int failCount = 0;
        int totalCount = 0;
        ConfigurationLoader config = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_DATES);
        String begin = config.getProperty("begin");
        String end = config.getProperty("end");
        if (begin.equals("")) begin = DateWrapper.getPreviousDateDDMMYYYY(-14);
        if (end.equals("")) end = DateWrapper.getPreviousDateDDMMYYYY(1);
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
        LOG.info("- PROPERTIES -------------------------------");
        LOG.info("| Start time: " + DateWrapper.getDateWithTime(0));
        LOG.info("| ENV: " + ENV);
        LOG.info("| ConfigID: " + configID);
        LOG.info("| Begin Date: " + begin);
        LOG.info("| End Date: " + end);
        LOG.info("| Check modified date: false");
        try {
            List<String> params = new ArrayList<String>();
            params.add(begin);
            params.add(end);
            params.add("");
            String script = FileHelper.getInstance().getDataWithParams(FILE_QUALIFIERS_SQL, params);
            LOG.info(script);
            ResultSet rset = oracleBun.executeSelect(script);
            int httpStatus;
            String id;
            String url;
            webDriverWrapper.getDriver(driverInfo, null, null);
            while (rset.next()) {
                totalCount++;
                id = rset.getString(3);
                url = rset.getString(1);
                httpStatus = isPageAvailable(url);
                if (httpStatus == 200) {
                    LOG.info(id + "\t" + "true" + "\t" + httpStatus + "\t" + url + "\t" + " ");
                } else if (httpStatus / 100 != 3) {
                    LOG.info(id + "\t" + "false" + "\t" + httpStatus + "\t" + url + "\t" + " ");
                    failCount++;
                } else {
                    String isAvail = isPageAvailableBySelenium(url, id);
                    LOG.info(id + "\t" + "redirect" + "\t" + httpStatus + "\t" + isAvail);
                }
            }
            rset.close();
            oracleBun.closeStatement();
        } catch (SQLException e) {
            LOG.error("Can't get regular qualifiers. Exception: " + e.getMessage());
        }
        LOG.info("Total count = " + totalCount + " . Passed count = " + (totalCount - failCount) + " Failed count = " + failCount);
        createTXTReport();
        assertTrue(failCount == 0);
    }
}
