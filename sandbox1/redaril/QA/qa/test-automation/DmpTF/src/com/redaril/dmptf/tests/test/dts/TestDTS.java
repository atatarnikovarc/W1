package com.redaril.dmptf.tests.test.dts;

import com.redaril.dmptf.tests.support.etl.EtlLogAnalyzer;
import com.redaril.dmptf.tests.support.etl.log.ETLLog;
import com.redaril.dmptf.tests.support.etl.log.UserdataCall;
import com.redaril.dmptf.tests.support.etl.model.Model;
import com.redaril.dmptf.tests.support.etl.model.Record;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.database.oracle.OracleWrapper;
import com.redaril.dmptf.util.date.DateWrapper;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.network.lib.httpunit.HttpUnitWrapper;
import com.redaril.dmptf.util.network.protocol.ssh.SSHWrapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 14.05.13
 * Time: 11:58
 * To change this template use File | Settings | File Templates.
 */
public class TestDTS {
    private static boolean isInit = false;
    private static String ENV;
    private static String configID;
    private static int port;
    private static String baseDomain;
    private final static String PATH_CONFIG = "config" + File.separator;
    private final static String FILE_PROPERTIES_ENV = "env.properties";
    private final static String FILE_PROPERTIES_APP = "app.properties";
    private final static String logFile = "dts.log";
    private final static String LogSystemProperty = "DmptfLogFile";
    private static Logger LOG;
    private static HttpUnitWrapper session;
    private final static String paramExe = "EXelate";
    private final static String exUid = "111222";
    private final static String urlExe = "/exelate?xuid=" + exUid + "&isRedirect=0";
    private final static String dataSourceIdExe = "1003";
    private final static int attempt = 3;
    private static Map<String, String> headers;
    private static String categoryId;
    private static String categoryExternalId;
    private static String exchangeId;
    private final static String getCategorySQL = "data" + File.separator + "dts" + File.separator + "getCategoryByDataSource.sql";
    private final static String getExchangeId = "data" + File.separator + "dts" + File.separator + "getExchangeIdByName.sql";
    private static String exelateName = "elate";

    //check etl
    private static final Class ModelClassName = UserdataCall.class;
    private static final String XMLModel = "data" + File.separator + "etl" + File.separator + "userdataCall.xml";
    protected static Model model;
    protected static EtlLogAnalyzer fileAnalyzer;
    protected static SSHWrapper sshWrapperETL;
    protected final static String dmpEtllocal = "userdataCall.log";  //name of local ETL file
    protected final static String dmpEtlFile = "userdataCall";
    protected final static String etlpath = "/var/log/etl/";

    @Rule
    public TestName name = new TestName();

    @Before
    public void setUp() {
        if (!isInit) {
            ConfigurationLoader envConfigurationLoader = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
            ENV = envConfigurationLoader.getProperty("env");
            configID = envConfigurationLoader.getProperty("configID");
            ConfigurationLoader configApp = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_APP);
            port = Integer.valueOf(configApp.getProperty("httpPort"));
            baseDomain = configApp.getProperty("baseDomain");
            System.setProperty(LogSystemProperty, logFile);
            LogConfigurer.initLogback();
            LOG = LoggerFactory.getLogger(TestDTS.class);
            getTestDataByDataSourceName(exelateName);
            session = new HttpUnitWrapper();
            headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json");
            //check etl
            ConfigurationLoader config = new ConfigurationLoader(PATH_CONFIG + ENV + ".properties");
            String ipETL = config.getProperty("host.etl");
            fileAnalyzer = new EtlLogAnalyzer();
            model = new Model();
            sshWrapperETL = new SSHWrapper(ipETL, "autotest", "812redaril");
            isInit = true;
        }
    }

    private void createUser(String configId) {
        session.deleteAllCookies();
        String urlToCreateUser = "http://" + ENV + "." + configId + ".p." + baseDomain + ":" + port + "/partners/universal/in?pid=9&ndl=hcasinc.com*";
        LOG.info("Create user. URL = " + urlToCreateUser);
        session.goToUrl(urlToCreateUser);
        LOG.info("Created user = " + session.getCookieValueByName("u"));
    }

    private void createMapping(String configId, String urlMapping) {
        String mappingUrl = "http://" + ENV + "." + configId + ".p." + baseDomain + ":" + port + "/partners" + urlMapping;
        LOG.info("Create mapping. URL = " + mappingUrl);
        session.goToUrl(mappingUrl);
    }

    private void getTestDataByDataSourceName(String sourceName) {
        OracleWrapper db = new OracleWrapper(ENV, "dmp");
        //get category
        List<String> params = new ArrayList<String>();
        params.add(sourceName);
        String script = FileHelper.getInstance().getDataWithParams(getCategorySQL, params);
        LOG.info(script);
        ResultSet rset = db.executeSelect(script);
        try {
            int i = 0;
            while (i < 1 && rset.next()) {
                categoryId = rset.getString(1);
                categoryExternalId = rset.getString(2);
                i++;
            }
            rset.close();
            db.closeStatement();
        } catch (SQLException e) {
            LOG.error("Exception: " + e.getMessage());
        }

        //get exchangeId
        params.clear();
        params = new ArrayList<String>();
        params.add(sourceName);
        script = FileHelper.getInstance().getDataWithParams(getExchangeId, params);
        LOG.info(script);
        rset = db.executeSelect(script);
        try {
            int i = 0;
            while (i < 1 && rset.next()) {
                exchangeId = rset.getString(1);
                i++;
            }
            rset.close();
            db.closeStatement();
        } catch (SQLException e) {
            LOG.error("Exception: " + e.getMessage());
        }
    }

    @Test
    public void DMP3598() {
        LOG.info(name.getMethodName() + " STARTED");
        int i = 0;
        boolean isPassed = false;
        while (i < attempt && !isPassed) {
            i++;
            createUser("west");
            createMapping("west", urlExe);
            String request = "http://" + ENV + ".west.dts." + baseDomain + "/dts/data?exchange=" + exchangeId + "&exUid=" + exUid + "&raUid=" + session.getCookieValueByName("u") + "&cats=" + categoryExternalId + "&timestamp=" + DateWrapper.getPreviousDateyyyyMMddHmmss(0);
            LOG.info("Request = " + request);
            session.sendGetRequest(request, null);
            sshWrapperETL.getFile(dmpEtlFile, dmpEtllocal, etlpath, false);
            String cstUrl = "http://" + ENV + "." + configID + ".cst." + baseDomain + ":" + port + "/cacheservertester/cserver?uid=me";
            String userModel = session.getResponsePage(cstUrl);
            isPassed = userModel.contains(categoryId) && checkUserDataCall(session.getCookieValueByName("u"), dataSourceIdExe, categoryId);
            if (!isPassed) LOG.info("Attempt " + (i + 1));
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void DMP3597() {
        LOG.info(name.getMethodName() + " STARTED");
        int i = 0;
        boolean isPassed = false;
        while (i < attempt && !isPassed) {
            i++;
            createUser("west");
            createMapping("west", urlExe);
            String request = "http://" + ENV + ".west.dts." + baseDomain + "/dts/data?exchange=" + exchangeId;
            LOG.info("Request = " + request);
            String body = "{\"pixels\": [\n" +
                    "  {\n" +
                    "    \"exUid\": \"" + exUid + "\",\n" +
                    "    \"raUid\": " + session.getCookieValueByName("u") + ",\n" +
                    "    \"cats\": \"" + categoryExternalId + "\",\n" +
                    "    \"timestamp\": \"" + DateWrapper.getPreviousDateyyyyMMddHmmss(0) + "\"\n" +
                    "  }\n" +
                    "]}";
            String contentType = "application/json";
            session.sendPostRequest(request, body, contentType, null, headers);
            sshWrapperETL.getFile(dmpEtlFile, dmpEtllocal, etlpath, false);
            String cstUrl = "http://" + ENV + "." + configID + ".cst." + baseDomain + ":" + port + "/cacheservertester/cserver?uid=me";
            String userModel = session.getResponsePage(cstUrl);
            isPassed = userModel.contains(categoryId) && checkUserDataCall(session.getCookieValueByName("u"), dataSourceIdExe, categoryId);
            if (!isPassed) LOG.info("Attempt " + (i + 1));
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void DMP3596() {
        LOG.info(name.getMethodName() + " STARTED");
        int i = 0;
        boolean isPassed = false;
        while (i < attempt && !isPassed) {
            i++;
            createUser("west");
            createMapping("west", urlExe);
            String request = "http://" + ENV + ".west.dts." + baseDomain + "/dts/exelate";
            LOG.info("Request = " + request);
            String body = "{\n" +
                    "    \"pixelCount\": 1,\n" +
                    "     \"pixels\": [\n" +
                    "          {\n" +
                    "             \"xuid\": \"" + exUid + "\",\n" +
                    "             \"buid\": " + session.getCookieValueByName("u") + ",\n" +
                    "             \"segments\": [" + categoryExternalId + "],\n" +
                    "             \"timestamp\": \"" + DateWrapper.getPreviousDateEEEMMMddHHzyyyy(0) + "\"\n" +
                    "          }\n" +
                    "     ]\n" +
                    "}";
            String contentType = "application/json";
            session.sendPostRequest(request, body, contentType, null, headers);
            sshWrapperETL.getFile(dmpEtlFile, dmpEtllocal, etlpath, false);
            String cstUrl = "http://" + ENV + "." + configID + ".cst." + baseDomain + ":" + port + "/cacheservertester/cserver?uid=me";
            String userModel = session.getResponsePage(cstUrl);
            isPassed = userModel.contains(categoryId) && checkUserDataCall(session.getCookieValueByName("u"), dataSourceIdExe, categoryId);
            if (!isPassed) LOG.info("Attempt " + (i + 1));
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        LOG.info(name.getMethodName() + " PASSED");
    }

    protected boolean checkUserDataCall(String uid, String dataSourceId, String categoryId) {
        // get pattern
        ETLLog pattern = model.getModel(XMLModel, ModelClassName);
        Record record = pattern.getRecord();
        //set Values into pattern
        record.getFieldByName("created_date_time").setValue(null);
        record.getFieldByName("cookie_user_id").setValue(uid);
        record.getFieldByName("data_source_id").setValue(dataSourceId);
        record.getFieldByName("category_id").setValue(categoryId);
        pattern.setRecord(record);
        LOG.info("Try to find record with cookie_user_id = " + uid + " , dataSourceId = " + dataSourceId + " , categoryId = " + categoryId);
        //check log
        boolean isCheck = fileAnalyzer.checkLog(dmpEtllocal, pattern);
        sshWrapperETL.tearDown(dmpEtllocal);
        return isCheck;
    }
}

