package com.redaril.dmptf.tests.test.pip.piggyback;


import com.redaril.dmp.model.meta.*;
import com.redaril.dmptf.tests.support.pip.base.BasePiggybackTest;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.date.DateWrapper;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.network.appinterface.jmx.JMXWrapper;
import com.redaril.dmptf.util.network.protocol.ssh.SSHWrapper;
import com.redaril.dmptf.util.selenium.ProxyWrapper;
import com.redaril.dmptf.util.selenium.WebDriverWrapper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class TestClientPiggyback extends BasePiggybackTest {
    private static boolean isSetupSystemPb;
    private static Map<String, SystemPiggyback> mapSystemPb = new HashMap<String, SystemPiggyback>();
    private static Map<String, DmpPiggyback> mapClientPb = new HashMap<String, DmpPiggyback>();
    private static Map<String, DataPixel> mapDataPixel = new HashMap<String, DataPixel>();
    private static Map<String, RegexQualifier> mapRegexQualifier = new HashMap<String, RegexQualifier>();
    private static Map<String, Category> mapCategory = new HashMap<String, Category>();
    private static boolean isAtBasePixelUrl;
    private static Set<AudienceGroup> setAG;
    private static boolean isSetup;
    private static AdvConversionPixel conversionPixel;
    private static TrackingPixel trackingPixel;
    private static Logger clientPBLOG;
    private static String baseDomain;
    private final static int delaySec = 60;

    @Rule
    public TestName name = new TestName();

    private void watchAndGoUrl(String url) {
        proxyWrapper.watchUrl(url);
        clientPBLOG.info("Go to url = " + url);
        webDriverWrapper.getPage(url);
        wait(5);
    }

    //isBasePb = true if we execute test for base pixel
    //isBasePb = false if we execute test for specified pixel and pb's (e.g. testDMP615)
    private void goPbHtml(boolean isBasePb) {
        if (!(isBasePb & isAtBasePixelUrl)) {
            boolean isAvail = false;
            int i = 0;
            while (!isAvail && i < 3) {
                String url = urlPixel + "?" + DateWrapper.getRandom();
                watchAndGoUrl(url);
                i++;
                isAvail = proxyWrapper.isValidPage();
                if (!isAvail) {
                    clientPBLOG.error("Page is unavailable. Reload page.");
                }
            }
        }
        isAtBasePixelUrl = isBasePb;
    }

    private void goPbHtmlForce() {
        boolean isAvail = false;
        int i = 0;
        while (!isAvail && i < 3) {
            String url = urlPixel + "?" + DateWrapper.getRandom();
            watchAndGoUrl(url);
            i++;
            isAvail = proxyWrapper.isValidPage();
            if (!isAvail) {
                clientPBLOG.error("Page is unavailable. Reload page.");
            }
        }
    }

    @Before
    public void setup() {
        if (!isSetupSystemPb) {
            System.setProperty(LogSystemProperty, "clientPiggyback.log");
            clientPBLOG = LoggerFactory.getLogger(TestClientPiggyback.class);
            configEnv = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
            ENV = configEnv.getProperty("env");
            configID = configEnv.getProperty("configID");
            ConfigurationLoader configApp = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_APP);
            baseDomain = configApp.getProperty("baseDomain");

            //create data for tests
            super.setUpPiggybacks();
            getFirstDriverFromFile();
            setAG = new HashSet<AudienceGroup>();
            setAG.add(wsHelper.createAudienceGroup(dataConsumer.getDataOwner()));
            DataPixel dataPixel = wsHelper.createDataPixel(dataConsumer.getDataOwner(), setAG);
            mapDataPixel.put("default", dataPixel);
            createPiggybacks();
            //end of creating
            jmxWrapper = new JMXWrapper(ENV, configID, "pip");
            jmxWrapperCST = new JMXWrapper(ENV, configID, "cst");
            //set pixelId into pb.html
            clientPBLOG.info("Change pixelId at pb.html");
            SSHWrapper sshWrapper = new SSHWrapper(pathWebserver, "autotest", "812redaril");
            sshWrapper.getFile(filePbHtml, filePbHtml, pathPbHtml, true);
            FileHelper.getInstance().findAndReplaceStringAtFile(filePbHtml, "_pixel", "_pixel=" + dataPixel.getTagId() + ";");
            sshWrapper.putFile(filePbHtml, filePbHtml, pathPbHtml);
            FileHelper.getInstance().deleteFile(filePbHtml);
            //end
            reloadpip();
            reloadcst();
            webDriverWrapper = new WebDriverWrapper(ENV);
            proxyWrapper = new ProxyWrapper(ENV, null);
            webDriverWrapper.getDriver(oneDriverInfo, proxyWrapper.getProxy(), null);
            isSetupSystemPb = true;
        }
    }

    private void createPiggybacks() {
        String pixelUrl;
        DmpPiggyback dmpPb;
        SystemPiggyback sysPb;
        Set<Pixel> setPixels = new HashSet<Pixel>();
        setPixels.add(mapDataPixel.get("default"));
        //dmp3669
        clientPBLOG.info("Create piggyback(s) to dmp3669");
        pixelUrl = "dmp3669_1_" + DateWrapper.getRandom();
        dmpPb = wsHelper.createClientPiggyback(dataConsumer.getDataOwner().getId(), setPixels, pixelUrl);
        mapClientPb.put("dmp3669_1", dmpPb);
        pixelUrl = "dmp3669_2_" + DateWrapper.getRandom();
        dmpPb = wsHelper.createClientPiggyback(dataConsumer.getDataOwner().getId(), setPixels, pixelUrl);
        dmpPb.setWeight(500);
        dmpPb = wsHelper.updateDmpPiggyback(dmpPb);
        mapClientPb.put("dmp3669_2", dmpPb);
        pixelUrl = "dmp3669_3_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        mapSystemPb.put("dmp3669_3", sysPb);
        //end

        //dmp3670
        clientPBLOG.info("Create piggyback(s) to dmp3670");
        pixelUrl = "dmp3670_" + DateWrapper.getRandom();
        dmpPb = wsHelper.createClientPiggyback(dataConsumer.getDataOwner().getId(), setPixels, pixelUrl);
        dmpPb.setEndDate(DateWrapper.getDate(-1));
        dmpPb.setStartDate(DateWrapper.getDate(-2));
        dmpPb = wsHelper.updateDmpPiggyback(dmpPb);
        mapClientPb.put("dmp3670", dmpPb);
        //end

        //dmp3671
        clientPBLOG.info("Create piggyback(s) to dmp3671");
        pixelUrl = "dmp3671_" + DateWrapper.getRandom();
        dmpPb = wsHelper.createClientPiggyback(dataConsumer.getDataOwner().getId(), setPixels, pixelUrl);
        dmpPb.setStatus(PiggybackStatus.INACTIVE);
        dmpPb = wsHelper.updateDmpPiggyback(dmpPb);
        mapClientPb.put("dmp3671", dmpPb);
        //end

        //dmp3667
        clientPBLOG.info("Create piggyback(s) to dmp3667");
        pixelUrl = "dmp3667_" + DateWrapper.getRandom();
        dmpPb = wsHelper.createClientPiggyback(dataConsumer.getDataOwner().getId(), setPixels, pixelUrl);
        dmpPb.setConditionExpression("!ri.isSecure");
        dmpPb = wsHelper.updateDmpPiggyback(dmpPb);
        mapClientPb.put("dmp3667", dmpPb);
        //end

        //dmp3666
        clientPBLOG.info("Create piggyback(s) to dmp3666");
        pixelUrl = "dmp3666_" + DateWrapper.getRandom();
        dmpPb = wsHelper.createClientPiggyback(dataConsumer.getDataOwner().getId(), setPixels, pixelUrl);
        dmpPb.setConditionExpression("ri.getRequestParameter('pid') < 0");
        dmpPb = wsHelper.updateDmpPiggyback(dmpPb);
        mapClientPb.put("dmp3666", dmpPb);
        //end

        //dmp3660
        clientPBLOG.info("Create piggyback(s) to dmp3660");
        pixelUrl = "dmp3660_" + DateWrapper.getRandom();
        dmpPb = wsHelper.createClientPiggyback(dataConsumer.getDataOwner().getId(), setPixels, pixelUrl);
        dmpPb.setConditionExpression("ri.getRequestParameter('pid') > 0");
        dmpPb.setPostfixExpression("ri.getRequestParameter('pid')");
        dmpPb = wsHelper.updateDmpPiggyback(dmpPb);
        mapClientPb.put("dmp3660", dmpPb);
        //end

        //dmp3635
        clientPBLOG.info("Create piggyback(s) to dmp3635");
        DataPixel dataPixel3635 = wsHelper.createDataPixel(dataConsumer.getDataOwner(), setAG);
        mapDataPixel.put("dataPixel3635", dataPixel3635);
        setPixels.clear();
        setPixels.add(dataPixel3635);
        pixelUrl = "dmp3635_" + DateWrapper.getRandom();
        dmpPb = wsHelper.createClientPiggyback(dataConsumer.getDataOwner().getId(), setPixels, pixelUrl);
        dmpPb.setConditionExpression("ri.getRequestParameter('pid') == " + dataPixel3635.getTagId());
        dmpPb.setDelayUnit(DelayUnit.SECONDS);
        dmpPb.setDelay(delaySec);
        dmpPb.setUnlimited(false);
        dmpPb = wsHelper.updateDmpPiggyback(dmpPb);
        mapClientPb.put("dmp3635", dmpPb);
        //end

        //dmp3636
        clientPBLOG.info("Create piggyback(s) to dmp3636");
        DataPixel dataPixel3636 = wsHelper.createDataPixel(dataConsumer.getDataOwner(), setAG);
        mapDataPixel.put("dataPixel3636", dataPixel3636);
        pixelUrl = "dmp3636_" + DateWrapper.getRandom();
        setPixels.clear();
        setPixels.add(dataPixel3636);
        dmpPb = wsHelper.createClientPiggyback(dataConsumer.getDataOwner().getId(), setPixels, pixelUrl);
        dmpPb.setConditionExpression("ri.getRequestParameter('pid') == " + dataPixel3636.getTagId());
        dmpPb.setDelayUnit(DelayUnit.MINUTES);
        dmpPb.setDelay(1);
        dmpPb.setUnlimited(false);
        dmpPb = wsHelper.updateDmpPiggyback(dmpPb);
        mapClientPb.put("dmp3636", dmpPb);
        //end

        //dmp3637
        clientPBLOG.info("Create piggyback(s) to dmp3637");
        DataPixel dataPixel3637 = wsHelper.createDataPixel(dataConsumer.getDataOwner(), setAG);
        mapDataPixel.put("dataPixel3637", dataPixel3637);
        pixelUrl = "dmp3637_" + DateWrapper.getRandom();
        setPixels.clear();
        setPixels.add(dataPixel3637);
        dmpPb = wsHelper.createClientPiggyback(dataConsumer.getDataOwner().getId(), setPixels, pixelUrl);
        dmpPb.setConditionExpression("ri.getRequestParameter('pid') == " + dataPixel3637.getTagId());
        dmpPb.setDelayUnit(DelayUnit.HOURS);
        dmpPb.setDelay(1);
        dmpPb.setUnlimited(false);
        dmpPb = wsHelper.updateDmpPiggyback(dmpPb);
        mapClientPb.put("dmp3637", dmpPb);
        //end

        //dmp3638
        clientPBLOG.info("Create piggyback(s) to dmp3638");
        DataPixel dataPixel3638 = wsHelper.createDataPixel(dataConsumer.getDataOwner(), setAG);
        mapDataPixel.put("dataPixel3638", dataPixel3638);
        pixelUrl = "dmp3638_" + DateWrapper.getRandom();
        setPixels.clear();
        setPixels.add(dataPixel3638);
        dmpPb = wsHelper.createClientPiggyback(dataConsumer.getDataOwner().getId(), setPixels, pixelUrl);
        dmpPb.setConditionExpression("ri.getRequestParameter('pid') == " + dataPixel3638.getTagId());
        dmpPb.setDelayUnit(DelayUnit.DAYS);
        dmpPb.setDelay(1);
        dmpPb.setUnlimited(false);
        dmpPb = wsHelper.updateDmpPiggyback(dmpPb);
        mapClientPb.put("dmp3638", dmpPb);
        //end

        //dmp3661
        clientPBLOG.info("Create piggyback(s) to dmp3661");
        DataPixel dataPixel3661 = wsHelper.createDataPixel(dataConsumer.getDataOwner(), setAG);
        mapDataPixel.put("dataPixel3661", dataPixel3661);
        pixelUrl = "dmp3661_" + DateWrapper.getRandom();
        setPixels.clear();
        setPixels.add(dataPixel3661);
        dmpPb = wsHelper.createClientPiggyback(dataConsumer.getDataOwner().getId(), setPixels, pixelUrl);
        clientPBLOG.info("Create category and RegexQualifier.");
        PricingMethod pricingMethod = wsHelper.getPricingMethodByName("CPU");
        SourceType sourceType = wsHelper.getSourceTypeByName("1st party qualifiable");
        DataSource dataSource = wsHelper.createDataSource(dataConsumer.getDataOwner(), pricingMethod, sourceType);
        DataType dataType = wsHelper.getDataTypeByName("Unknown");
        Category category = wsHelper.createCategory(null, dataType, dataSource);
        mapCategory.put("dmp3661", category);
        RegexQualifier regexQualifier = wsHelper.createRegexQualifier(dataSource, category);
        // regexQualifier.getRegex();
        mapRegexQualifier.put("dmp3661", regexQualifier);
        dmpPb.setConditionExpression("ri.getPageCategory('" + category.getName() + "') != null");
        dmpPb = wsHelper.updateDmpPiggyback(dmpPb);
        mapClientPb.put("dmp3661", dmpPb);
        //end

        //dmp3663
        clientPBLOG.info("Create piggyback(s) to dmp3663");
        DataPixel dataPixel3663 = wsHelper.createDataPixel(dataConsumer.getDataOwner(), setAG);
        mapDataPixel.put("dataPixel3663", dataPixel3663);
        pixelUrl = "dmp3663_" + DateWrapper.getRandom();
        setPixels.clear();
        setPixels.add(dataPixel3663);
        dmpPb = wsHelper.createClientPiggyback(dataConsumer.getDataOwner().getId(), setPixels, pixelUrl);
        clientPBLOG.info("Create category and RegexQualifier.");
        pricingMethod = wsHelper.getPricingMethodByName("CPU");
        sourceType = wsHelper.getSourceTypeByName("1st party qualifiable");
        dataSource = wsHelper.createDataSource(dataConsumer.getDataOwner(), pricingMethod, sourceType);
        dataType = wsHelper.getDataTypeByName("Unknown");
        category = wsHelper.createCategory(null, dataType, dataSource);
        mapCategory.put("dmp3663", category);
        long asId = wsHelper.createAudienceSegment(dataConsumer.getId());
        AudienceSegment as = wsHelper.getAudienceSegmentById(asId);
        DataSale dataSale = wsHelper.updateDataSale(dataConsumer, pricingMethod, category);
        long sectionId = wsHelper.createSection(asId, category);
        wsHelper.publishAudienceSegment(asId);
        regexQualifier = wsHelper.createRegexQualifier(dataSource, category);
        mapRegexQualifier.put("dmp3663", regexQualifier);
        dmpPb.setConditionExpression("ri.getAudienceSegment('" + as.getName() + "') != null");
        long advId = wsHelper.createAdvertiser(platformClient);
        long advCampId = wsHelper.createAdvertiserCampaign(platformClient, advId);
        AdvertiserCampaign advCamp = new AdvertiserCampaign();
        advCamp.setId(advCampId);
        ImpressionPixel iPixel = wsHelper.createDMPImpressionPixel(dataConsumer, setAG, advCampId);
        long dataCampId = wsHelper.createDataCampaign(dataConsumer.getId(), as, iPixel, dataConsumer.getDataOwner(), sectionId, setAG);
        dmpPb = wsHelper.updateDmpPiggyback(dmpPb);
        mapClientPb.put("dmp3663", dmpPb);
        //end

        //dmp3664
        clientPBLOG.info("Create piggyback(s) to dmp3664");
        pixelUrl = "dmp3664_" + DateWrapper.getRandom();
        dmpPb = wsHelper.createClientPiggyback(dataConsumer.getDataOwner().getId(), setPixels, pixelUrl);
        dmpPb.setConditionExpression("(ri.geTTTTTT()) && (ri.getRequestParameter('pid')  > 1)");
        dmpPb = wsHelper.updateDmpPiggyback(dmpPb);
        mapClientPb.put("dmp3664", dmpPb);
        //end

        //dmp3665
        clientPBLOG.info("Create piggyback(s) to dmp3665");
        pixelUrl = "dmp3665_" + DateWrapper.getRandom();
        dmpPb = wsHelper.createClientPiggyback(dataConsumer.getDataOwner().getId(), setPixels, pixelUrl);
        dmpPb.setConditionExpression("ri.getRequestParameter('pid')  > 1");
        dmpPb.setPostfixExpression("ri.getRequestParameter('pid') + 'suffix' + ri.getTTTT()");
        dmpPb = wsHelper.updateDmpPiggyback(dmpPb);
        mapClientPb.put("dmp3665", dmpPb);
        //end

        //dmp3673 and dmp 3674
        clientPBLOG.info("Create piggyback(s) to dmp3673 and 3674");
        PlatformClient adv3673 = wsHelper.createAdvertiser();
        long advId3673 = wsHelper.createAdvertiser(adv3673);
        DataConsumer dataConsumer3673 = wsHelper.createDataConsumer(adv3673);
        Set<AudienceGroup> setAG3673 = new HashSet<AudienceGroup>();
        setAG3673.add(wsHelper.createAudienceGroup(dataConsumer3673.getDataOwner()));
        long adCampId3673 = wsHelper.createAdvertiserCampaign(adv3673, advId3673);
        AdvImpressionPixel impressionPixel = wsHelper.createImpressionPixel(dataConsumer3673.getDataOwner(), setAG3673);
        Set<AdvImpressionPixel> impressionPixelSet = new HashSet<>();
        impressionPixelSet.add(impressionPixel);
        conversionPixel = wsHelper.createConversionPixel(dataConsumer3673.getDataOwner(), setAG3673);
        String productKey = "prod";
        String productCategoryKey = "prodcat";
        trackingPixel = wsHelper.createTrackingPixel(dataConsumer3673.getDataOwner(), setAG3673, productKey, productCategoryKey);
        pixelUrl = "dmp3673" + DateWrapper.getRandom();
        setPixels.clear();
        setPixels.add(conversionPixel);
        setPixels.add(trackingPixel);
        dmpPb = wsHelper.createClientPiggyback(dataConsumer3673.getDataOwner().getId(), setPixels, pixelUrl);
        mapClientPb.put("dmp3673", dmpPb);
        //end
    }

    @Test
    public void testDMP3669() {
        //DMP-3669:Weight
        clientPBLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        DmpPiggyback dmpPb1000 = mapClientPb.get("dmp3669_1");
        DmpPiggyback dmpPb999 = mapClientPb.get("dmp3669_2");
        SystemPiggyback sysPb1000 = mapSystemPb.get("dmp3669_3");
        int i = 0;
        // execute test num times, because sometimes we get bad connection
        goPbHtml(true);
        while (i < num && !isPassed) {
            int order1 = proxyWrapper.findRequestUrl(dmpPb1000.getUrl());
            int order2 = proxyWrapper.findRequestUrl(dmpPb999.getUrl());
            int order3 = proxyWrapper.findRequestUrl(sysPb1000.getUrl());
            isPassed = order1 < order2 & order2 < order3;
            i++;
            if (!isPassed) goPbHtml(false);
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        clientPBLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3670() {
        //DMP-3670:Dates range
        clientPBLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        DmpPiggyback dmpPb = mapClientPb.get("dmp3670");
        int i = 0;
        // execute test num times, because sometimes we get bad connection
        while (i < num && !isPassed) {
            goPbHtml(true);
            int isFind = proxyWrapper.findRequestUrl(dmpPb.getUrl());
            i++;
            isPassed = isFind == 0;
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        clientPBLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3671() {
        //DMP-3671:Non Active
        clientPBLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        DmpPiggyback dmpPb = mapClientPb.get("dmp3671");
        int i = 0;
        // execute test num times, because sometimes we get bad connection
        while (i < num && !isPassed) {
            goPbHtml(true);
            int isFind = proxyWrapper.findRequestUrl(dmpPb.getUrl());
            i++;
            isPassed = isFind == 0;
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        clientPBLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3667() {
        //DMP-3667:Http
        clientPBLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        DmpPiggyback dmpPb = mapClientPb.get("dmp3667");
        int i = 0;
        // execute test num times, because sometimes we get bad connection
        while (i < num && !isPassed) {
            goPbHtml(false);
            int isFind = proxyWrapper.findRequestUrl(dmpPb.getUrl());
            i++;
            isPassed = isFind > 0;
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        clientPBLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3666() {
        //DMP-3666:IF = FALSE
        clientPBLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        DmpPiggyback dmpPb = mapClientPb.get("dmp3666");
        int i = 0;
        // execute test num times, because sometimes we get bad connection
        while (i < num && !isPassed) {
            goPbHtml(true);
            int isFind = proxyWrapper.findRequestUrl(dmpPb.getUrl());
            i++;
            isPassed = isFind == 0;
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        clientPBLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3660() {
        //DMP-3660:IF = TRUE
        clientPBLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        DmpPiggyback dmpPb = mapClientPb.get("dmp3660");
        DataPixel dataPixel = mapDataPixel.get("default");
        int i = 0;
        // execute test num times, because sometimes we get bad connection
        while (i < num && !isPassed) {
            goPbHtmlForce();
            sshWrapperETL.getFile(dmpEtlFile, dmpEtllocal, etlpath, false);
            sshWrapperETL.getFile(requestDataPartnerProdEtlFile, requestDataPartnerProdEtlFileLocal, etlpath, false);
            int isFind = proxyWrapper.findRequestUrl(dmpPb.getUrl());
            if (isFind > 0) {
                clientPBLOG.info("Check DmpUserModel");
                String ucookie = proxyWrapper.findCookieByNameAtRequests("u");
                boolean isDmpUserModelChecked = checkDmpUserModelETL(ucookie, dataPixel.getId().toString(), null);
                if (!isDmpUserModelChecked){
                    sshWrapperETL.getFile(dmpEtlFile, dmpEtllocal, etlpath, false);
                    isDmpUserModelChecked = checkDmpUserModelETL(ucookie, dataPixel.getId().toString(), null);
                }
                clientPBLOG.info("isDmpUserModelChecked = " + isDmpUserModelChecked);
                boolean isRequestDataPartnerProdChecked = checkAsRequestDataPartnerProd(ucookie, dataPixel.getTagId().toString(), dmpPb.getId().toString());
                if (!isRequestDataPartnerProdChecked){
                    sshWrapperETL.getFile(requestDataPartnerProdEtlFile, requestDataPartnerProdEtlFileLocal, etlpath, false);
                    isRequestDataPartnerProdChecked = checkAsRequestDataPartnerProd(ucookie, dataPixel.getTagId().toString(), dmpPb.getId().toString());
                }
                clientPBLOG.info("isRequestDataPartnerProdChecked = " + isRequestDataPartnerProdChecked);
                isPassed = isDmpUserModelChecked && isRequestDataPartnerProdChecked;
            }
            i++;
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        clientPBLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3635() {
        //DMP-3635:Second
        clientPBLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        clientPBLOG.info("Change pixelId at pb.html");
        SSHWrapper sshWrapper = new SSHWrapper(pathWebserver, "autotest", "812redaril");
        sshWrapper.getFile(filePbHtml, filePbHtml, pathPbHtml, true);
        FileHelper.getInstance().copyFile(filePbHtml, filePbHtml + "2");
        DataPixel dataPixel3635 = mapDataPixel.get("dataPixel3635");
        FileHelper.getInstance().findAndReplaceStringAtFile(filePbHtml, "_pixel", "_pixel=" + dataPixel3635.getTagId() + ";");
        sshWrapper.putFile(filePbHtml, filePbHtml, pathPbHtml);
        DmpPiggyback dmpPb = mapClientPb.get("dmp3635");
        int i = 0;
        // execute test num times, because sometimes we get bad connection
        while (i < num && !isPassed) {
            goPbHtml(false);
            int isFind = proxyWrapper.findRequestUrl(dmpPb.getUrl());
            goPbHtml(false);
            int isFind2 = proxyWrapper.findRequestUrl(dmpPb.getUrl());
            wait(delaySec);
            goPbHtml(false);
            int isFind3 = proxyWrapper.findRequestUrl(dmpPb.getUrl());
            i++;
            isPassed = isFind > 0 & isFind2 == 0 & isFind3 > 0;
            if (!isPassed) wait(delaySec);
        }
        sshWrapper.putFile(filePbHtml + "2", filePbHtml, pathPbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml + "2");
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        clientPBLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3636() {
        //DMP-3636:Minute
        clientPBLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        clientPBLOG.info("Change pixelId at pb.html");
        SSHWrapper sshWrapper = new SSHWrapper(pathWebserver, "autotest", "812redaril");
        DataPixel dataPixel3636 = mapDataPixel.get("dataPixel3636");
        sshWrapper.getFile(filePbHtml, filePbHtml, pathPbHtml, true);
        FileHelper.getInstance().copyFile(filePbHtml, filePbHtml + "2");
        FileHelper.getInstance().findAndReplaceStringAtFile(filePbHtml, "_pixel", "_pixel=" + dataPixel3636.getTagId() + ";");
        sshWrapper.putFile(filePbHtml, filePbHtml, pathPbHtml);
        DmpPiggyback dmpPb = mapClientPb.get("dmp3636");
        int i = 0;
        // execute test num times, because sometimes we get bad connection
        while (i < num && !isPassed) {
            goPbHtml(false);
            int isFind = proxyWrapper.findRequestUrl(dmpPb.getUrl());
            goPbHtml(false);
            int isFind2 = proxyWrapper.findRequestUrl(dmpPb.getUrl());
            wait(60);
            goPbHtml(false);
            int isFind3 = proxyWrapper.findRequestUrl(dmpPb.getUrl());
            i++;
            isPassed = isFind > 0 & isFind2 == 0 & isFind3 > 0;
        }
        sshWrapper.putFile(filePbHtml + "2", filePbHtml, pathPbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml + "2");
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        clientPBLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3637() {
        //DMP-3637:Hour
        clientPBLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        clientPBLOG.info("Change pixelId at pb.html");
        SSHWrapper sshWrapper = new SSHWrapper(pathWebserver, "autotest", "812redaril");
        sshWrapper.getFile(filePbHtml, filePbHtml, pathPbHtml, true);
        FileHelper.getInstance().copyFile(filePbHtml, filePbHtml + "2");
        DataPixel dataPixel3637 = mapDataPixel.get("dataPixel3637");
        FileHelper.getInstance().findAndReplaceStringAtFile(filePbHtml, "_pixel", "_pixel=" + dataPixel3637.getTagId() + ";");
        sshWrapper.putFile(filePbHtml, filePbHtml, pathPbHtml);
        DmpPiggyback dmpPb = mapClientPb.get("dmp3637");
        int i = 0;
        // execute test num times, because sometimes we get bad connection
        while (i < num && !isPassed) {
            goPbHtml(false);
            int isFind = proxyWrapper.findRequestUrl(dmpPb.getUrl());
            goPbHtml(false);
            int isFind2 = proxyWrapper.findRequestUrl(dmpPb.getUrl());
            i++;
            isPassed = isFind > 0 & isFind2 == 0;
        }
        sshWrapper.putFile(filePbHtml + "2", filePbHtml, pathPbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml + "2");
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        clientPBLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3638() {
        //DMP-3638:Day
        clientPBLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        clientPBLOG.info("Change pixelId at pb.html");
        SSHWrapper sshWrapper = new SSHWrapper(pathWebserver, "autotest", "812redaril");
        sshWrapper.getFile(filePbHtml, filePbHtml, pathPbHtml, true);
        FileHelper.getInstance().copyFile(filePbHtml, filePbHtml + "2");
        DataPixel dataPixel3638 = mapDataPixel.get("dataPixel3638");
        FileHelper.getInstance().findAndReplaceStringAtFile(filePbHtml, "_pixel", "_pixel=" + dataPixel3638.getTagId() + ";");
        sshWrapper.putFile(filePbHtml, filePbHtml, pathPbHtml);
        DmpPiggyback dmpPb = mapClientPb.get("dmp3638");
        wait(1);
        // execute test num times, because sometimes we get bad connection
        goPbHtml(false);
        int isFind = proxyWrapper.findRequestUrl(dmpPb.getUrl());
        goPbHtml(false);
        int isFind2 = proxyWrapper.findRequestUrl(dmpPb.getUrl());
        isPassed = isFind > 0 & isFind2 == 0 || isFind == 0 & isFind2 > 0;
        sshWrapper.putFile(filePbHtml + "2", filePbHtml, pathPbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml + "2");
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        clientPBLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3661() {
        //DMP-3661:Page Category
        clientPBLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        clientPBLOG.info("Change pixelId at pb.html");
        DataPixel dataPixel3661 = mapDataPixel.get("dataPixel3661");
        DmpPiggyback dmpPb = mapClientPb.get("dmp3661");
        RegexQualifier regexQualifier = mapRegexQualifier.get("dmp3661");
        String ndl = regexQualifier.getRegex();
        ndl = ndl.substring(0, ndl.length() - 1);
        ndl = ndl.replace("/", "%2F");
        ndl = "http%3A%2F%2F" + ndl;
        String url = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/universal/in?pid=" + dataPixel3661.getTagId() + "&ndl=" + ndl;
        int i = 0;
        // execute test num times, because sometimes we get bad connection
        while (i < num && !isPassed) {
            watchAndGoUrl(url);
            clientPBLOG.info("Try to find piggyback with url = " + dmpPb.getUrl());
            boolean isFind = webDriverWrapper.getPageSource().contains(dmpPb.getUrl());
            i++;
            isPassed = isFind;
        }
        assertTrue(name.getMethodName() + " FAILED. Can't find piggyback.", isPassed);
        String ucookie = proxyWrapper.findCookieByNameAtRequests("u");
        clientPBLOG.info("U Cookie = " + ucookie);
        webDriverWrapper.getPage("http://" + ENV + "." + configID + ".cst." + baseDomain + ":" + port + "/cacheservertester/cserver?uid=" + ucookie);
        String source = webDriverWrapper.getPageSource();
        boolean isFindUM = source.contains(mapCategory.get("dmp3661").getName());
        assertTrue(name.getMethodName() + " FAILED" + ". Can't find category at UserModel.", isFindUM);
        clientPBLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3663() {
        //DMP-3663:Audience Segment
        clientPBLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        clientPBLOG.info("Change pixelId at pb.html");
        DataPixel dataPixel3663 = mapDataPixel.get("dataPixel3663");
        DmpPiggyback dmpPb = mapClientPb.get("dmp3663");
        RegexQualifier regexQualifier = mapRegexQualifier.get("dmp3663");
        String ndl = regexQualifier.getRegex();
        ndl = ndl.substring(0, ndl.length() - 1);
        ndl = ndl.replace("/", "%2F");
        ndl = "http%3A%2F%2F" + ndl;
        String url = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/universal/in?pid=" + dataPixel3663.getTagId() + "&ndl=" + ndl;
        int i = 0;
        // execute test num times, because sometimes we get bad connection
        while (i < num && !isPassed) {
            webDriverWrapper.getPage(url);
            webDriverWrapper.getPage(url);
            webDriverWrapper.getPage(url);
            watchAndGoUrl(url);
            clientPBLOG.info("Try to find piggyback with url = " + dmpPb.getUrl());
            boolean isFind = webDriverWrapper.getPageSource().contains(dmpPb.getUrl());
            i++;
            isPassed = isFind;
        }
        String ucookie = proxyWrapper.findCookieByNameAtRequests("u");
        clientPBLOG.info("U Cookie = " + ucookie);
        assertTrue(name.getMethodName() + " FAILED" + ". Can't find piggyback.", isPassed);
        webDriverWrapper.getPage("http://" + ENV + "." + configID + ".cst." + baseDomain + ":" + port + "/cacheservertester/cserver?uid=" + ucookie);
        String source = webDriverWrapper.getPageSource();
        boolean isFindUM = source.contains(mapCategory.get("dmp3663").getName());
        assertTrue(name.getMethodName() + " FAILED" + ". Can't find category at UserModel.", isFindUM);
        clientPBLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3664() {
        //DMP-3664:IF - incorrect
        clientPBLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        DmpPiggyback dmpPb = mapClientPb.get("dmp3664");
        int i = 0;
        // execute test num times, because sometimes we get bad connection
        while (i < num && !isPassed) {
            goPbHtml(true);
            int isFind = proxyWrapper.findRequestUrl(dmpPb.getUrl());
            i++;
            isPassed = isFind == 0;
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        clientPBLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3665() {
        //DMP-3665:IF = FALSE
        clientPBLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        DmpPiggyback dmpPb = mapClientPb.get("dmp3665");
        int i = 0;
        // execute test num times, because sometimes we get bad connection
        while (i < num && !isPassed) {
            goPbHtml(true);
            int isFind = proxyWrapper.findRequestUrl(dmpPb.getUrl());
            i++;
            isPassed = isFind == 0;
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        clientPBLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3673() {
        //DMP-3673:Conversion
        clientPBLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        DmpPiggyback dmpPb = mapClientPb.get("dmp3673");
        int i = 0;
        String request = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/universal/in?pid=X";
        String requestURL = request.replace("X", conversionPixel.getTagId().toString());
        // execute test num times, because sometimes we get bad connection
        while (i < num && !isPassed) {
            webDriverWrapper.getPage(requestURL);
            isPassed = webDriverWrapper.getPageSource().contains(dmpPb.getUrl());
            i++;
            //isPassed = isFind > 0;
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        clientPBLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3674() {
        //DMP-3674:Tracking
        clientPBLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        DmpPiggyback dmpPb = mapClientPb.get("dmp3673");
        String request = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/universal/in?pid=X";
        String requestURL = request.replace("X", trackingPixel.getTagId().toString());
        // execute test num times, because sometimes we get bad connection
        int i = 0;
        while (i < num && !isPassed) {
            clientPBLOG.info("Try to find Pb's URL = " + dmpPb.getUrl());
            webDriverWrapper.getPage(requestURL);
            wait(2);
            isPassed = webDriverWrapper.getPageSource().contains(dmpPb.getUrl());
            i++;
            //isPassed = isFind > 0;
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        clientPBLOG.info(name.getMethodName() + " PASSED");
    }

    @AfterClass
    public static void tearDownClientPbs() {
        for (Map.Entry<String, SystemPiggyback> entry : mapSystemPb.entrySet()) {
            wsHelper.deleteSystemPiggyback(entry.getValue());
        }
        for (Map.Entry<String, DmpPiggyback> entry : mapClientPb.entrySet()) {
//            if (!entry.getKey().contains("dmp3673"))
            wsHelper.deleteDmpPiggyback(entry.getValue());
//            else
//                wsHelper.deleteDmpPiggyback(entry.getValue(), dataConsumer3673.getDataOwner().getId());
        }
        for (Map.Entry<String, DataPixel> entry : mapDataPixel.entrySet()) {
            wsHelper.deleteDataPixel(entry.getValue());
        }
//        for (AudienceGroup audienceGroup : setAG) {
//            wsHelper.deleteAudienceGroup(audienceGroup);
//        }
    }
}
