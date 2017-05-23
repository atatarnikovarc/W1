package com.redaril.dmptf.tests.test.pip.cookiemapping;

import com.redaril.dmptf.tests.support.etl.log.ETLLog;
import com.redaril.dmptf.tests.support.etl.log.ExchangeMappingCall;
import com.redaril.dmptf.tests.support.etl.model.Record;
import com.redaril.dmptf.tests.support.pip.base.BaseCookieMapping;
import com.redaril.dmptf.util.database.oracle.OracleWrapper;
import com.redaril.dmptf.util.file.FileHelper;
import org.junit.AfterClass;
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
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestCookieMapping extends BaseCookieMapping {
    private static Logger LOG;
    private final static String etlFile = "exchangeMappingCall";  //name of ETL file
    private final static String etlpath = "/var/log/etl/";
    private final static String etllocal = etlFile + ".log";  //name of local ETL file
    private static final String XMLModel = "data" + File.separator + "etl" + File.separator + "exchangeMappingCall.xml";
    private static final Class ModelClassName = ExchangeMappingCall.class;

    @Rule
    public TestName name = new TestName();

    @Before
    public void setUp() {
        super.setUp();
        LOG = LoggerFactory.getLogger(TestCookieMapping.class);
    }

    @AfterClass
    public static void tearDown() {
        SSH.tearDown(etllocal);
    }


    //at cookiemapping tests we check ETL exchangeMappingCall.log
    //This log consists of 10 values
    //Important for us:
    // 1 position: created_date_time 	java.util.Date 	  	the creation time of the log object
    // 2 position: cookie_user_id 	java.lang.Long 	  	User Id from the request cookies
    // 3 position: exchange_id 	java.lang.Byte 	  	Exchange id
    // 4 position: exchange_user_id 	java.lang.String 	External User ID
    //log example: 2012-07-30 07:55:49	173641232810011	1	XXX	**	1	192.168.0.165	NULL	2	NULL

    @Test
    public void GoogleMapping() {
        LOG.info(name.getMethodName() + " STARTED");
        String extUID = "XXX";
        String exchID = "1"; //Google -1
        String URL = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/pixel?t=gcm&id=" + extUID + "&cver=1";
        Boolean isMap = checkSimpleMapping(URL, exchID + "(AdX) -> " + extUID);
        assertTest(name.getMethodName(), isMap, exchID, extUID);
        LOG.info(name.getMethodName() + " PASSED");

    }

    @Test
    public void OpenXMapping() {
        LOG.info(name.getMethodName() + " STARTED");
        String extUID = "XXX";
        String exchID = "9"; //OpenX -9
        String URL = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/oxmap?external_user_id=" + extUID;
        Boolean isMap = checkSimpleMapping(URL, exchID + "(OpenX) -> " + extUID);
        assertTest(name.getMethodName(), isMap, exchID, extUID);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void ExelateMapping() {
        LOG.info(name.getMethodName() + " STARTED");
        String extUID = "aa";
        String exchID = "11"; //Exelate -11
        String URL = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/exelate?xuid=" + extUID + "&isRedirect=0";
        Boolean isMap = checkSimpleMapping(URL, exchID + "(EXelate) -> " + extUID);
        assertTest(name.getMethodName(), isMap, exchID, extUID);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void AdifyMapping() {
        LOG.info(name.getMethodName() + " STARTED");
        String extUID = "NULL";//now adify doesn't set External User ID, so we have NULL at log
        String exchID = "12"; //Adify -12
        String URL = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/adify";
        Boolean isMap = checkSimpleMapping(URL, exchID + "(Adify)");
        //get log
        SSH.getFile(etlFile, etllocal, etlpath, false);
        // get pattern
        ETLLog pattern = model.getModel(XMLModel, ModelClassName);
        Record record = pattern.getRecord();
        //set Values into pattern
        //unfortunately, you should to check name of field at exchangeMappingCall.xml
        //name must be equals name at exchangeMappingCall.xml!
        record.getFieldByName("cookie_user_id").setValue(mycookie);
        record.getFieldByName("exchange_id").setValue(exchID);
        record.getFieldByName("exchange_user_id").setValue(extUID);
        pattern.setRecord(record);
        LOG.info("Try to find record with cookie_user_id = " + mycookie + " , exchange_id = " + exchID);
        //check log
        Boolean isLogChecked = fileAnalyzer.checkLog(etllocal, pattern);
        assertTrue(name.getMethodName() + " FAILED", isMap && isLogChecked);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void DatonicsMapping() {
        LOG.info(name.getMethodName() + " STARTED");
        String extUID = "BBBB";
        String exchID = "15"; //Datonics -15
        String redirectURL = "http://ya.ru";
        String URL = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/universal/sync?exchange=15&euid=" + extUID + "&mapping=nostore&redir=" + redirectURL;
        Boolean isMap = checkSimpleMappingWithRedirect(URL, "15(Datonics) -> " + extUID, redirectURL);
        assertTest(name.getMethodName(), isMap, exchID, extUID);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void LiveRampMapping() {
        LOG.info(name.getMethodName() + " STARTED");
        String extUID = "BBBB";
        String exchID = "16"; //LiveRamp -16
        String redirectURL = "http://ya.ru";
        String URL = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/universal/sync?exchange=16&euid=" + extUID + "&mapping=nostore&redir=" + redirectURL;
        Boolean isMap = checkSimpleMappingWithRedirect(URL, exchID + "(LiveRamp) -> " + extUID, redirectURL);
        assertTest(name.getMethodName(), isMap, exchID, extUID);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void noWriteUmIntoCS() {
        LOG.info(name.getMethodName() + " STARTED");
        String url = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/pixel?t=gcm&id=XXX&cver=1";
        String getCategorySQL = "data" + File.separator + "dts" + File.separator + "getCategoryByDataSource.sql";
        LOG.info("Go to URL = " + url);
        session.deleteAllCookies();
        session.goToUrl(url);
        mycookie = session.getCookieValueByName(ucookie);
        LOG.info("Get cookie from browser ucookie = " + mycookie);
        LOG.info("Get Mapping from CST = " + CST);
        String userModel = session.getResponsePage(CST);
        Boolean isMap = userModel.contains("No cache server mapping in userdata for uid: " + mycookie);
        createRAUser();
        LOG.info("Verificate mapping");
        if (isMap) {
            LOG.info("Mapping is correct.");
        } else {
            LOG.error("Mapping is incorrect.");
        }
        assertTrue(name.getMethodName() + " FAILED", isMap);
        LOG.info(name.getMethodName() + " PASSED");

    }

//    @Test
//    public void deleteUmFromCSOn3DPM() {
//        LOG.info(name.getMethodName()+ " STARTED");
//        String dataSourceId = "1009";
//        OracleWrapper oracleWrapper = new OracleWrapper(ENV,"dmp");
//        WSHelper wsHelper = new WSHelper(ENV);
//        String pixelId = oracleWrapper.getPixelIDbyDataSource(dataSourceId);
//        Category category = wsHelper.getActiveCategory(Long.parseLong(dataSourceId));
//        DataSource dataSource = new DataSource();
//        dataSource.setId(Long.parseLong(dataSourceId));
//        RegexQualifier regexQualifier = wsHelper.createRegexQualifier(dataSource, category);
//        JMXWrapper jmxWrapper = new JMXWrapper(ENV,configID,"pip");
//        jmxWrapper.execCommand("doReload");
//        jmxWrapper.waitForReloading();
//        String ndl = regexQualifier.getRegex();
//        ndl = ndl.substring(0, ndl.length() - 1);
//        ndl = ndl.replace("/", "%2F");
//        ndl = "http%3A%2F%2F" + ndl;
//        String url = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/universal/in?pid=" + pixelId + "&ndl=" + ndl;
//        session.goToUrl(url);
//        String checkUrl = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/pixel?t=gcm&id=XXX&cver=1";
//        session.goToUrl(checkUrl);
//        mycookie = session.getCookieValueByName(ucookie);
//        LOG.info("Get cookie from browser ucookie = " + mycookie);
//        LOG.info("Get Mapping from CST = " + CST);
//        String userModel = session.getResponsePage(CST);
//        Boolean isMap = userModel.contains("No cache server mapping in userdata for uid: " + mycookie);
//        createRAUser();
////        LOG.info("Verificate mapping");
////        if (isMap) {
////            LOG.info("Mapping is correct.");
////        } else {
////            LOG.error("Mapping is incorrect.");
////        }
////        assertTrue(name.getMethodName()+ " FAILED",isMap);
//        LOG.info(name.getMethodName()+ " PASSED");
//
//    }

    private void assertTest(String testname, boolean isMap, String exchID, String extUID) {
        assertTrue(testname + " failed. Mapping failed", isMap);
        assertTrue(testname + " failed. ETL check failed", checkEtlLog(exchID, extUID));
    }

    private boolean checkEtlLog(String exchID, String extUID) {
        SSH.getFile(etlFile, etllocal, etlpath, false);
        // get pattern
        ETLLog pattern = model.getModel(XMLModel, ModelClassName);
        Record record = pattern.getRecord();
        //set Values into pattern
        //unfortunately, you should to check name of field at exchangeMappingCall.xml
        //name must be equals name at exchangeMappingCall.xml!
        record.getFieldByName("cookie_user_id").setValue(mycookie);
        record.getFieldByName("exchange_id").setValue(exchID);
        record.getFieldByName("exchange_user_id").setValue(extUID);
        pattern.setRecord(record);
        LOG.info("Try to find record with cookie_user_id = " + mycookie + " , exchange_id = " + exchID + " , exchange_user_id = " + extUID);
        //check log
        return fileAnalyzer.checkLog(etllocal, pattern);
    }

    private String getCategoryByDataSourceName(String sourceName) {
        OracleWrapper db = new OracleWrapper(ENV, "dmp");
        List<String> params = new ArrayList<String>();
        params.add(sourceName);
        String getCategorySQL = "data" + File.separator + "dts" + File.separator + "getCategoryByDataSource.sql";
        String script = FileHelper.getInstance().getDataWithParams(getCategorySQL, params);
        LOG.info(script);
        ResultSet rset = db.executeSelect(script);
        try {
            while (rset.next()) {
                return rset.getString(1);
            }
            rset.close();
            db.closeStatement();
        } catch (SQLException e) {
            LOG.error("Exception: " + e.getMessage());
        }
        return null;
    }
}
