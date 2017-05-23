package com.redaril.dmptf.tests.test.qualifiers;

import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.database.oracle.OracleWrapper;
import com.redaril.dmptf.util.date.DateWrapper;
import com.redaril.dmptf.util.network.appinterface.jmx.JMXWrapper;
import com.redaril.dmptf.util.network.appinterface.webservice.WSHelper;
import com.redaril.dmptf.util.network.lib.httpunit.HttpUnitWrapper;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 25.04.13
 * Time: 11:57
 * To change this template use File | Settings | File Templates.
 */
public class RegularQualifiersAllTime {

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
    private static WSHelper wsHelper;
    protected final static String FILE_PROPERTIES_APP = "app.properties";
    private static String baseDomain;
    private final static String FILE_PROPERTIES_DATES = "date.properties";
    private static int port;

    private List<RegQualifierForTest> qualifiers = new ArrayList<RegQualifierForTest>();


    private static void reloadPartners(String env) {
        JMXWrapper jmxWrapper = new JMXWrapper(env, configID, "pip");
        jmxWrapper.execCommand("doReload");
        jmxWrapper.waitForReloading();
    }

    @Before
    public void setup() {
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
        String script = "SELECT nt_qualifier.base_url, INTName, nt_qualifier.qualifier_id, nt_qualifier.interest_id, INTSource FROM nt_qualifier JOIN (select interest_id as IntId, data_source as INTSource,name as INTName from nc_interest where nc_interest.category_identifier not like '%DEACTIVATED%') ON  nt_qualifier.interest_id = IntId WHERE nt_qualifier.status_code_id = 95 AND nt_qualifier.qualifier_type_id = 55 AND nt_qualifier.probability_lower_bound=0 AND nt_qualifier.probability_upper_bound=100";
        ResultSet rowSet = oracleBun.executeSelect(script);
        try {
            while (rowSet.next()) {
                RegQualifierForTest newQual = new RegQualifierForTest();
                newQual.setUrl(rowSet.getString(1));
                newQual.setInterestName(rowSet.getString(2));
                newQual.setId(rowSet.getString(3));
                newQual.setInterestId(rowSet.getString(4));
                newQual.setInterestSource(rowSet.getString(5));
                qualifiers.add(newQual);
            }
        } catch (SQLException e) {
            LOG.error("Can't get qualifiers from DB");
        }
        try {
            rowSet.close();
        } catch (SQLException e) {
        }
        oracleBun.closeStatement();
        boolean isNeedRealodPIP = false;
        for (RegQualifierForTest qualifier : qualifiers) {
            String url = "";
            try {
                url = URLEncoder.encode(qualifier.getUrl(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOG.error("Can't encode URL of qualifier. Exception: " + e.getMessage());
            }
            if (dataSourceMap.get(qualifier.getInterestSource()) == null) {
                LOG.info("Get pixel ID from dmp by data_source= " + qualifier.getInterestSource());
                String pixelID = oracleDmp.getPixelIDbyDataSource(qualifier.getInterestSource());
                if (pixelID.equals("")) {
                    if (wsHelper == null) wsHelper = new WSHelper(ENV);
                    pixelID = wsHelper.createDataPixel(qualifier.getInterestSource());
                    isNeedRealodPIP = true;
                    LOG.info("Create new DataPixel. ID = " + pixelID);

                }
                LOG.info("PixelID= " + pixelID);
                dataSourceMap.put(qualifier.getInterestSource(), pixelID);
            }
            qualifier.setUrl(QUALIFIER_URL_PREFIX + dataSourceMap.get(qualifier.getInterestSource()) + "&ndr=" + url);
        }
        if (isNeedRealodPIP) reloadPartners(ENV);
        LOG.info("- GET NEW QUALIFIERS FINISHED --------------");
        LOG.info("TEST's COUNT = " + qualifiers.size());
    }

    @Test
    public void testRegularQualifiers() {
        int countFail = 0;
        int counter = 1;
        for (RegQualifierForTest qualifier : qualifiers) {
            String url = qualifier.getUrl();
            String interest = qualifier.getInterestName();
            String id = qualifier.getId();
            String QUALIFIER_URL_PREFIX = "http://" + ENV + ".west.p." + baseDomain + ":" + port + "/partners/universal/in?pid=";
            String userModel = null;
            String ocook = null;
            String ucook = null;
            boolean result = false;
            int attempt = 0;
            while (!result && attempt < 3) {
                session.deleteAllCookies();
                if (url.contains(QUALIFIER_URL_PREFIX + "&ndr"))
                    fail("Can't get and create pixel for this Qualifier(ID = " + id + " )");
                session.goToUrl(url);
                userModel = session.getResponsePage(PARTNERSINFO_URL_EX);
                ocook = session.getCookieValueByName("o");
                ucook = session.getCookieValueByName("u");
                result = userModel.contains(interest.trim());
                if (!result && attempt < 3) wait(1);
            }
            if (result) {
                LOG.info("\r\n" + counter + ". Start to check qualifier: " + id + "\r\n" + "interestCategory: " + interest + ";\r\n" + "URL: " + url + ";\r\n" + "cookie o=" + ocook + ";\r\n" + "cookie u=" + ucook + ";\r\n" + "userModel:" + userModel + "\r\n" + "qualifier ID = " + id + " is PASSED" + "\r\n" + "-----------------------------------------\r\n ");
            } else {
                LOG.error("\r\n" + counter + ". Start to check qualifier: " + id + "\r\n" + "interestCategory: " + interest + ";\r\n" + "URL: " + url + ";\r\n" + "cookie o=" + ocook + ";\r\n" + "cookie u=" + ucook + ";\r\n" + "userModel:" + userModel + "\r\n" + "qualifier ID = " + id + " is FAILED" + "\r\n" + "-----------------------------------------\r\n ");
                countFail++;
            }
            counter++;

        }
        assertTrue("Qualifiers " + qualifiers.size() + " . Failed " + countFail, countFail > 0);
        LOG.info("Qualifiers " + qualifiers.size() + " . Failed " + countFail);
    }

    private void wait(int sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {
        }
    }
//    @Test
//    public void test(){
//        int failed = 0;
//        DefaultHttpClient httpClient = createHttpClient(16);
//        List<GoogleMappingThread> threads = new ArrayList<GoogleMappingThread>();
//        ExecutorService service = Executors.newFixedThreadPool(16);
//        //String QUALIFIER_URL_PREFIX = "http://" + ENV + ".west.p." + baseDomain + ":" + port + "/partners/universal/in?pid=";
//        for(int i=0; i<qualifiers.size()+1;i++){
//        Future status = service.submit(new QualifierThread(i,qualifiers.get(i),httpClient,ENV, configID,baseDomain,port));
//            try {
//                if (status.get().equals("false")){
//                    failed++;
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            } catch (ExecutionException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        for (int i=0;i<17;i++){
//            GoogleMappingThread thread = new GoogleMappingThread(1,"","","",null);
//            service.execute(thread);
//            threads.add(thread);
//        }
//    }
//    }

//    private DefaultHttpClient createHttpClient(int count) {
//        PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
//        connectionManager.setDefaultMaxPerRoute(count);
//        connectionManager.setMaxTotal(count);
//        return new DefaultHttpClient(connectionManager);
//    }
}