package com.redaril.dmptf.tests.test.pip.pixels.advertiser;

import com.redaril.dmp.model.meta.*;
import com.redaril.dmptf.tests.support.etl.EtlLogAnalyzer;
import com.redaril.dmptf.tests.support.etl.log.AdvDmp;
import com.redaril.dmptf.tests.support.etl.log.DmpUserModel;
import com.redaril.dmptf.tests.support.etl.log.ETLLog;
import com.redaril.dmptf.tests.support.etl.model.Model;
import com.redaril.dmptf.tests.support.etl.model.Record;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.network.appinterface.jmx.JMXWrapper;
import com.redaril.dmptf.util.network.appinterface.webservice.WSHelper;
import com.redaril.dmptf.util.network.lib.httpunit.HttpUnitWrapper;
import com.redaril.dmptf.util.network.protocol.ssh.SSHWrapper;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class AdvertiserPixels {

    protected static Logger LOG;
    private final static String PATH_CONFIG = "config" + File.separator;
    private final static String LogSystemProperty = "DmptfLogFile";
    private static Boolean isInit = false;
    private static SSHWrapper sshWrapper;
    private final static String FILE_PROPERTIES_ENV = "env.properties";
    private static ConfigurationLoader configEnv;
    private static String ENV;
    private static String configID;
    private static WSHelper wsHelper;
    private static HttpUnitWrapper httpUnitWrapper;
    private static String universalInHTTP;
    private static String universalInHTTPS;
    private static String urlToCreateUser;

    //for pixels
    private static PlatformClient platformClient;
    private static DataConsumer dataConsumer;
    private static long advId;
    private static long adCampId;
    private static AdvImpressionPixel advImpressionPixel;
    private static AdvConversionPixel advConversionPixel;
    private static AdvClickPixel advClickPixel;
    private static TrackingPixel trackingPixel;
    private static Set<AudienceGroup> setAG;
    private final static String productKey = "prod";
    private final static String productCategoryKey = "prodcat";

    //for checking etl
    private final static String etlFile = "advDmp";  //name of advDMP file
    private final static String dmpEtlFile = "dmpUserModel";  //name of DMPuserModel file
    private final static String dmpEtllocal = dmpEtlFile + ".log";  //name of local ETL file
    private final static String etllocal = etlFile + ".log";  //name of local ETL file
    private final static String etlpath = "/var/log/etl/";
    private static final Class ModelClassName = AdvDmp.class;
    private static final Class DMPModelClassName = DmpUserModel.class;
    protected static Model model;
    private static final String XMLModel = "data" + File.separator + "etl" + File.separator + "advDmp.xml";
    private static final String XMLDMPModel = "data" + File.separator + "etl" + File.separator + "dmpUserModel.xml";
    protected static EtlLogAnalyzer fileAnalyzer;
    protected final static String FILE_PROPERTIES_APP = "app.properties";

    @Rule
    public TestName name = new TestName();

    @Before
    public void setUp() {
        if (!isInit) {
            configEnv = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
            String logFile = "advPixel.log";
            System.setProperty(LogSystemProperty, logFile);
            FileHelper.getInstance().deleteFile("output" + File.separator + "logs" + File.separator + logFile);
            FileHelper.getInstance().deleteFile("output" + File.separator + "logs" + File.separator + logFile);
            System.setProperty(LogSystemProperty, logFile);
            LogConfigurer.initLogback();
            ENV = configEnv.getProperty("env");
            configID = configEnv.getProperty("configID");
            ConfigurationLoader configApp = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_APP);
            String baseDomain = configApp.getProperty("baseDomain");
            String port = configApp.getProperty("httpPort");
            universalInHTTP = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/universal/in?pid=X";
            universalInHTTPS = "https://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/universal/in?pid=X";
            urlToCreateUser = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/pixel?id=CAESEOS8DIt22VoL5E4ei5mPYhA&cver=1&t=gcm";
            ConfigurationLoader config = new ConfigurationLoader(PATH_CONFIG + ENV + ".properties");
            String ipETL = config.getProperty("host.etl");
            isInit = true;
            sshWrapper = new SSHWrapper(ipETL, "autotest", "812redaril");
            wsHelper = new WSHelper(ENV);
            LOG = LoggerFactory.getLogger(AdvertiserPixels.class);
            httpUnitWrapper = new HttpUnitWrapper();
            fileAnalyzer = new EtlLogAnalyzer();
            model = new Model();
            //setup for advertiser' pixels
            platformClient = wsHelper.createAdvertiser();
            advId = wsHelper.createAdvertiser(platformClient);
            dataConsumer = wsHelper.createDataConsumer(platformClient);
            setAG = new HashSet<AudienceGroup>();
            setAG.add(wsHelper.createAudienceGroup(dataConsumer.getDataOwner()));
            adCampId = wsHelper.createAdvertiserCampaign(platformClient, advId);
            createAdvImpressionPixel();
            createAdvConversionPixel();
            createAdvClickPixel();
            createTrackingPixel();
            JMXWrapper jmxWrapper = new JMXWrapper(ENV, configID, "pip");
            jmxWrapper.execCommand("doReload");
            jmxWrapper.waitForReloading();

        }
    }

    private void createAdvConversionPixel() {
        Set<AdvImpressionPixel> impressionPixelSet = new HashSet<AdvImpressionPixel>();
        impressionPixelSet.add(advImpressionPixel);
        advConversionPixel = wsHelper.createConversionPixel(dataConsumer.getDataOwner(), setAG);
    }

    private void createAdvImpressionPixel() {
        advImpressionPixel = wsHelper.createImpressionPixel(dataConsumer.getDataOwner(), setAG);
    }

    private void createAdvClickPixel() {
        advClickPixel = wsHelper.createClickPixel(dataConsumer.getDataOwner(), setAG, MediaPartner.CORE_AUDIENCE);
    }

    private void createTrackingPixel() {
        trackingPixel = wsHelper.createTrackingPixel(dataConsumer.getDataOwner(), setAG, productKey, productCategoryKey);
    }

    private boolean checkAdvDmpLog(String uid, String pixel_id, @Nullable String advDmpJson, @Nullable String extraParamsJson) {
        sshWrapper.getFile(etlFile, etllocal, etlpath, false);
        // get pattern
        ETLLog pattern = model.getModel(XMLModel, ModelClassName);
        Record record = pattern.getRecord();
        //set Values into pattern
        record.getFieldByName("created_date_time").setValue(null);
        record.getFieldByName("cookie_user_id").setValue(uid);
        record.getFieldByName("pixel_id").setValue(pixel_id);
        record.getFieldByName("adv_dmp_json").setValue(advDmpJson);
        record.getFieldByName("extra_params_json").setValue(extraParamsJson);
        pattern.setRecord(record);
        LOG.info("Try to find record with cookie_user_id = " + uid + " , pixel_id = " + pixel_id + " , adv_dmp_json = " + advDmpJson + " , extra_params_json = " + extraParamsJson);
        //check log
        boolean isCheck = fileAnalyzer.checkLog(etllocal, pattern);
        sshWrapper.tearDown(etllocal);
        return isCheck;
    }

    private boolean checkDmpUserModelLog(String uid, String pixel_id, @Nullable String extraParamsJson) {
        sshWrapper.getFile(dmpEtlFile, dmpEtllocal, etlpath, false);
        // get pattern
        ETLLog pattern = model.getModel(XMLDMPModel, DMPModelClassName);
        Record record = pattern.getRecord();
        //set Values into pattern
        record.getFieldByName("created_date_time").setValue(null);
        record.getFieldByName("cookie_user_id").setValue(uid);
        record.getFieldByName("pixel_id").setValue(pixel_id);
        record.getFieldByName("extra_params_json").setValue(extraParamsJson);
        pattern.setRecord(record);
        LOG.info("Try to find record with cookie_user_id = " + uid + " , pixel_id = " + pixel_id + " , extra_params_json = " + extraParamsJson);
        //check log
        boolean isCheck = fileAnalyzer.checkLog(dmpEtllocal, pattern);
        sshWrapper.tearDown(dmpEtllocal);
        return isCheck;
    }

    private void testPixel(String request, String pixelId, @Nullable String advDmpjson, @Nullable String extraParamJson) {
        String requestURL = request.replace("X", pixelId);
        int attempt = 0;
        boolean isETLChecked = false;
        while (attempt < 3 && !isETLChecked) {
            httpUnitWrapper.deleteAllCookies();
            httpUnitWrapper.goToUrl(urlToCreateUser);
            String ucookie = httpUnitWrapper.getCookieValueByName("u");
            LOG.info("U Cookie = " + ucookie);
            httpUnitWrapper.goToUrl(requestURL);
            wait(3000);
            isETLChecked = checkAdvDmpLog(ucookie, pixelId, advDmpjson, extraParamJson);
        }
        assertTrue("ETL failed", isETLChecked);
    }

    @Test
    public void testAdvConversionPixelWithOptionalParams() {
        LOG.info(name.getMethodName() + " STARTED");
        String request = universalInHTTP + "&oid=1&accid=2&gclid=3&curr=4";
        String json = "\"advertiser_id\":" + platformClient.getId() + ",\"type\":\"conversion\"";
        String extraJson = "\"gclid\":\"3\",\"accid\":\"2\",\"oid\":\"1\",\"curr\":\"4\"";
        testPixel(request, advConversionPixel.getTagId().toString(), json, extraJson);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testAdvConversionPixelHTTP() {
        LOG.info(name.getMethodName() + " STARTED");
        String json = "\"advertiser_id\":" + platformClient.getId() + ",\"type\":\"conversion\"";
        testPixel(universalInHTTP, advConversionPixel.getTagId().toString(), json, null);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testAdvConversionPixelHTTPS() {
        LOG.info(name.getMethodName() + " STARTED");
        String json = "\"advertiser_id\":" + platformClient.getId() + ",\"type\":\"conversion\"";
        testPixel(universalInHTTPS, advConversionPixel.getTagId().toString(), json, null);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testClickPixelHTTP() {
        LOG.info(name.getMethodName() + " STARTED");
        String json = "\"advertiser_id\":" + platformClient.getId() + ",\"type\":\"click\"";
        testPixel(universalInHTTP, advClickPixel.getTagId().toString(), json, null);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testClickPixelHTTPS() {
        LOG.info(name.getMethodName() + " STARTED");
        String json = "\"advertiser_id\":" + platformClient.getId() + ",\"type\":\"click\"";
        testPixel(universalInHTTPS, advClickPixel.getTagId().toString(), json, null);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testClickPixelWithOptionalParams() {
//        cid 	Long 	Optional, the Campaign ID from the advertising platform
//        sid 	Long 	Optional, the Site ID from the advertising platform
//        plid 	Long 	Optional, the Placement ID from the advertising platform
//        adid 	Long 	Optional, the Ad ID from the advertising platform
//        creid 	Long 	Optional, the Creative ID from the advertising platform
//        asid 	Long 	Optional, the Ad Server ID from the advertising platform
        LOG.info(name.getMethodName() + " STARTED");
        String request = universalInHTTP + "&cid=1&sid=2&plid=3&adid=4&creid=5&asid=6&mid=7&ct=8&st=9&city=10&zp=11&bw=12&dma=13&redir=http%3A%2F%2Fgoogle.com";
        String json = "\"creative_id\":\"5\",\"site_id\":\"2\",\"ad_id\":\"4\",\"placement_id\":\"3\"," + "\"advertiser_id\":" + platformClient.getId() + ",\"type\":\"click\",\"campaign_id\":\"1\",\"ad_server_id\":\"6\"";
        String extraJson = "\"redir\":\"http://google.com\",\"dma\":\"13\",\"bw\":\"12\",\"st\":\"9\",\"mid\":\"7\",\"zp\":\"11\",\"ct\":\"8\",\"city\":\"10\"";
        testPixel(request, advClickPixel.getTagId().toString(), json, extraJson);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testAdvImpressionPixelHTTP() {
        LOG.info(name.getMethodName() + " STARTED");
        String json = "\"advertiser_id\":" + platformClient.getId() + ",\"type\":\"impression\"";
        testPixel(universalInHTTP, advImpressionPixel.getTagId().toString(), json, null);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testAdvImpressionPixelHTTPS() {
        LOG.info(name.getMethodName() + " STARTED");
        String json = "\"advertiser_id\":" + platformClient.getId() + ",\"type\":\"impression\"";
        testPixel(universalInHTTPS, advImpressionPixel.getTagId().toString(), json, null);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testAdvImpressionPixelWithOptionalParams() {
//        Parameter 	Type 	Description
//        pid 	Long 	Mandatory, the parameter uses for getting pixel information at Red Aril server
//        cid 	Long 	Optional, the Campaign ID from the advertising platform
//        sid 	Long 	Optional, the Site ID from the advertising platform
//        plid 	Long 	Optional, the Placement ID from the advertising platform
//        adid 	Long 	Optional, the Ad ID from the advertising platform
//        creid 	Long 	Optional, the Creative ID from the advertising platform
//        asid 	Long 	Optional, the Ad Server ID from the advertising platform
// http://p.raasnet.com/partners/universal/in?pid=84814&t=i&cid=%ebuy!&sid=%esid!&plid=%epid!&adid=%eaid!&creid=%ecid!
        LOG.info(name.getMethodName() + " STARTED");
        String requestURL = universalInHTTP + "&cid=1&sid=2&plid=3&adid=4&creid=5&asid=6";
        String json = "\"creative_id\":\"5\",\"ad_server_id\":\"6\",\"ad_id\":\"4\",\"placement_id\":\"3\"," + "\"advertiser_id\":" + platformClient.getId() + ",\"site_id\":\"2\"" + ",\"type\":\"impression\",\"campaign_id\":\"1\"";
        testPixel(requestURL, advImpressionPixel.getTagId().toString(), json, null);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testTrackingPixelHTTP() {
        LOG.info(name.getMethodName() + " STARTED");
        String json = "\"advertiser_id\":" + platformClient.getId() + ",\"type\":\"tracking\"";
        testPixel(universalInHTTP, trackingPixel.getTagId().toString(), json, null);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testTrackingPixelHTTPS() {
        LOG.info(name.getMethodName() + " STARTED");
        String json = "\"advertiser_id\":" + platformClient.getId() + ",\"type\":\"tracking\"";
        testPixel(universalInHTTPS, trackingPixel.getTagId().toString(), json, null);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testTrackingPixelWithCategories() {
        LOG.info(name.getMethodName() + " STARTED");
        String pixelId = trackingPixel.getTagId().toString();
        String requestURL = universalInHTTP.replace("X", pixelId);
        //create Categories
        PricingMethod pricingMethod = wsHelper.getPricingMethodByName("CPU");
        SourceType sourceType = wsHelper.getSourceTypeByName("1st party qualifiable");
        DataSource dataSource = wsHelper.createDataSource(dataConsumer.getDataOwner(), pricingMethod, sourceType);
        DataType dataType = wsHelper.getDataTypeByName("Unknown");
        Category categoryParent = wsHelper.createCategory(null, dataType, dataSource);
        Category categoryChild = wsHelper.createCategory(categoryParent, dataType, dataSource);
        Category categoryChild2 = wsHelper.createCategory(categoryParent, dataType, dataSource);
        requestURL = requestURL + "&" + productKey + "=" + categoryChild.getExternalId();
        requestURL = requestURL + "&" + productCategoryKey + "=" + categoryChild2.getExternalId();
        boolean isPassed = false;
        int i = 0;
        while (i < 3 && !isPassed) {
            httpUnitWrapper.deleteAllCookies();
            httpUnitWrapper.goToUrl(urlToCreateUser);
            String ucookie = httpUnitWrapper.getCookieValueByName("u");
            LOG.info("U Cookie = " + ucookie);
            httpUnitWrapper.goToUrl(requestURL);
            String extrajson = "\"" + productCategoryKey + "\":\"" + categoryChild2.getExternalId() + "\",\"" + productKey + "\":\"" + categoryChild.getExternalId() + "\"";
            wait(3000);
            boolean isAdvDmpChecked = checkAdvDmpLog(ucookie, pixelId, null, extrajson);
            boolean isDmpUserModelChecked = checkDmpUserModelLog(ucookie, pixelId, extrajson);
            isPassed = isAdvDmpChecked && isDmpUserModelChecked;
            i++;
        }
        assertTrue(name.getMethodName() + " FAILED. ETL failed", isPassed);
        LOG.info(name.getMethodName() + " PASSED");
    }

    private void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOG.info("Can't sleep the thread");
        }
    }
}