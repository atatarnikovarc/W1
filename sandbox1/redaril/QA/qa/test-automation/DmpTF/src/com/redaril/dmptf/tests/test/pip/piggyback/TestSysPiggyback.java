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

public class TestSysPiggyback extends BasePiggybackTest {
    private static boolean isSetupSystemPb;
    private static Map<String, SystemPiggyback> mapPb = new HashMap<String, SystemPiggyback>();
    private static Map<String, DataPixel> mapDataPixel = new HashMap<String, DataPixel>();
    private static boolean isAtBasePixelUrl;
    private static Set<AudienceGroup> setAG;
    private static Logger sysPbLOG;
    private final static int delaySec = 100;

    @Rule
    public TestName name = new TestName();

    //isBasePb = true if we execute test for base pixel
    //isBasePb = false if we execute test for specified pixel and pb's (etc testDMP3675)
    private void goPbHtml(boolean isBasePb) {
        if (!(isBasePb & isAtBasePixelUrl)) {
            boolean isAvail = false;
            int i = 0;
            while (!isAvail && i < 3) {
                String url = urlPixel + "?" + DateWrapper.getRandom();
                proxyWrapper.watchUrl(url);
                sysPbLOG.info("Go to url = " + url);
                webDriverWrapper.getPage(url);
                wait(5);
                i++;
                isAvail = proxyWrapper.isValidPage();
            }
        }
        isAtBasePixelUrl = isBasePb;
    }

    @Before
    public void setup() {
        if (!isSetupSystemPb) {
            System.setProperty(LogSystemProperty, "systemPiggyback.log");
            sysPbLOG = LoggerFactory.getLogger(TestClientPiggyback.class);
            //config = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES);
            configEnv = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
            ENV = configEnv.getProperty("env");
            configID = configEnv.getProperty("configID");
            webDriverWrapper = new WebDriverWrapper(ENV);
            proxyWrapper = new ProxyWrapper(ENV, null);
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
            //set pixelId into pb.html
            sysPbLOG.info("Change pixelId at pb.html");
            SSHWrapper sshWrapper = new SSHWrapper(pathWebserver, "autotest", "812redaril");
            sshWrapper.getFile(filePbHtml, filePbHtml, pathPbHtml, true);
            FileHelper.getInstance().findAndReplaceStringAtFile(filePbHtml, "_pixel", "_pixel=" + dataPixel.getTagId() + ";");
            sshWrapper.putFile(filePbHtml, filePbHtml, pathPbHtml);
            FileHelper.getInstance().deleteFile(filePbHtml);
            //end
            reloadpip();
            webDriverWrapper.getDriver(oneDriverInfo, proxyWrapper.getProxy(), null);
            isSetupSystemPb = true;
        }
    }

    private void createPiggybacks() {
        String pixelUrl;
        SystemPiggyback sysPb;
        //dmp3683
        sysPbLOG.info("Create piggyback(s) to dmp3683");
        pixelUrl = "dmp3683_1_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        mapPb.put("dmp3683_1", sysPb);
        pixelUrl = "dmp3683_2_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        sysPb.setWeight(950);
        sysPb = wsHelper.updateSystemPiggyback(sysPb);
        mapPb.put("dmp3683_2", sysPb);
        //end
        //dmp3686
        sysPbLOG.info("Create piggyback(s) to dmp3686");
        pixelUrl = "dmp3686_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        sysPb.setEndDate(DateWrapper.getDate(-1));
        sysPb.setStartDate(DateWrapper.getDate(-2));
        sysPb = wsHelper.updateSystemPiggyback(sysPb);
        mapPb.put("dmp3686", sysPb);
        //end
        //dmp3687
        sysPbLOG.info("Create piggyback(s) to dmp3687");
        pixelUrl = "dmp3687_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        sysPb.setStatus(PiggybackStatus.INACTIVE);
        sysPb = wsHelper.updateSystemPiggyback(sysPb);
        mapPb.put("dmp3687", sysPb);
        //end
        //dmp3684
        sysPbLOG.info("Create piggyback(s) to dmp3684");
        pixelUrl = "dmp3684_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        sysPb.setConditionExpression("req.partnerId > 1 && !req.isSecure");
        sysPb = wsHelper.updateSystemPiggyback(sysPb);
        mapPb.put("dmp3684", sysPb);
        //end
        //dmp3681
        sysPbLOG.info("Create piggyback(s) to dmp3681");
        pixelUrl = "dmp3681_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        sysPb.setConditionExpression("req.partnerId > 1");
        //sysPb.setPostfixExpression("then");
        sysPb = wsHelper.updateSystemPiggyback(sysPb);
        mapPb.put("dmp3681", sysPb);
        //end
        //dmp3685_1
        sysPbLOG.info("Create piggyback(s) to dmp3685_1");
        pixelUrl = "dmp3685_1_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        sysPb.setConditionExpression("req.partnerId > 1 && !req.isSecure");
        sysPb = wsHelper.updateSystemPiggyback(sysPb);
        mapPb.put("dmp3685_1", sysPb);
        //end
        //dmp3685_2
        sysPbLOG.info("Create piggyback(s) to dmp3685_2");
        pixelUrl = "dmp3685_2_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        sysPb.setConditionExpression("req.partnerId > 1");
        sysPb = wsHelper.updateSystemPiggyback(sysPb);
        mapPb.put("dmp3685_2", sysPb);
        //end
        //dmp3682
        sysPbLOG.info("Create piggyback(s) to dmp3682");
        pixelUrl = "dmp3682_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        sysPb.setConditionExpression("req.partnerId < 1");
        sysPb = wsHelper.updateSystemPiggyback(sysPb);
        mapPb.put("dmp3682", sysPb);
        //end
        //dmp3679
        sysPbLOG.info("Create piggyback(s) to dmp3679");
        pixelUrl = "dmp3679_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        sysPb.setConditionExpression("(req.QQQ) && req.partnerId > 1");
        sysPb = wsHelper.updateSystemPiggyback(sysPb);
        mapPb.put("dmp3679", sysPb);
        //end
        //dmp3680
        sysPbLOG.info("Create piggyback(s) to dmp3680");
        pixelUrl = "dmp3680_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        sysPb.setConditionExpression("req.partnerId > 1");
        sysPb.setPostfixExpression("req.paaaaaa + 'then-1'");
        sysPb = wsHelper.updateSystemPiggyback(sysPb);
        mapPb.put("dmp3680", sysPb);
        //end
        //dmp3675
        sysPbLOG.info("Create piggyback(s) to dmp3675");
        DataPixel dataPixel3675 = wsHelper.createDataPixel(dataConsumer.getDataOwner(), setAG);
        mapDataPixel.put("dataPixel3675", dataPixel3675);
        pixelUrl = "dmp3675_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        sysPb.setConditionExpression("req.partnerId == " + dataPixel3675.getTagId());
        sysPb.setDelayUnit(DelayUnit.SECONDS);
        sysPb.setDelay(delaySec);
        sysPb.setUnlimited(false);
        sysPb = wsHelper.updateSystemPiggyback(sysPb);
        mapPb.put("dmp3675", sysPb);
        //end
        //dmp3676
        sysPbLOG.info("Create piggyback(s) to dmp3676");
        DataPixel dataPixel3676 = wsHelper.createDataPixel(dataConsumer.getDataOwner(), setAG);
        mapDataPixel.put("dataPixel3676", dataPixel3676);
        pixelUrl = "dmp3676_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        sysPb.setConditionExpression("req.partnerId == " + dataPixel3676.getTagId());
        sysPb.setDelayUnit(DelayUnit.MINUTES);
        sysPb.setDelay(1);
        sysPb.setUnlimited(false);
        sysPb = wsHelper.updateSystemPiggyback(sysPb);
        mapPb.put("dmp3676", sysPb);
        //end
        //dmp3677
        sysPbLOG.info("Create piggyback(s) to dmp3677");
        DataPixel dataPixel3677 = wsHelper.createDataPixel(dataConsumer.getDataOwner(), setAG);
        mapDataPixel.put("dataPixel3677", dataPixel3677);
        pixelUrl = "dmp3677_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        sysPb.setConditionExpression("req.partnerId == " + dataPixel3677.getTagId());
        sysPb.setDelayUnit(DelayUnit.HOURS);
        sysPb.setDelay(1);
        sysPb.setUnlimited(false);
        sysPb = wsHelper.updateSystemPiggyback(sysPb);
        mapPb.put("dmp3677", sysPb);
        //end
        //dmp3678
        sysPbLOG.info("Create piggyback(s) to dmp3678");
        DataPixel dataPixel3678 = wsHelper.createDataPixel(dataConsumer.getDataOwner(), setAG);
        mapDataPixel.put("dataPixel3678", dataPixel3678);
        pixelUrl = "dmp3678_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        sysPb.setConditionExpression("req.partnerId == " + dataPixel3678.getTagId());
        sysPb.setDelayUnit(DelayUnit.DAYS);
        sysPb.setDelay(1);
        sysPb.setUnlimited(false);
        sysPb = wsHelper.updateSystemPiggyback(sysPb);
        mapPb.put("dmp3678", sysPb);
        //end
        //dmp4501
        sysPbLOG.info("Create piggyback(s) to dmp4501");
        DataPixel dataPixel4501 = wsHelper.createDataPixel(dataConsumer.getDataOwner(), setAG);
        pixelUrl = "dmp4501_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        HashSet<SystemPiggyback> set = new HashSet<SystemPiggyback>();
        set.add(sysPb);
        dataPixel4501 = wsHelper.updateDataPixel(dataPixel4501, IncludeMode.CUSTOM, set);
        mapDataPixel.put("dataPixel4501", dataPixel4501);
        mapPb.put("dmp4501", sysPb);
        //end
        //dmp4500
        sysPbLOG.info("Create piggyback(s) to dmp4500");
        HashSet<SystemPiggyback> set4500 = new HashSet<SystemPiggyback>();
        DataPixel dataPixel4500 = wsHelper.createDataPixel(dataConsumer.getDataOwner(), setAG);
        pixelUrl = "dmp4500_" + DateWrapper.getRandom();
        sysPb = wsHelper.createSystemPiggyback(pixelUrl);
        set4500.add(sysPb);
        mapPb.put("dmp4500_1", sysPb);
        pixelUrl = "dmp4500_" + DateWrapper.getRandom();
        SystemPiggyback sysPb2 = wsHelper.createSystemPiggyback(pixelUrl);
        sysPb2.setThirdParty(true);
        sysPb2 = wsHelper.updateSystemPiggyback(sysPb2);
        mapPb.put("dmp4500_2", sysPb2);
        set4500.add(sysPb2);
        dataPixel4500 = wsHelper.updateDataPixel(dataPixel4500, IncludeMode.NONE, set4500);
        mapDataPixel.put("dataPixel4500", dataPixel4500);
        //end

    }

    @Test
    public void testDMP3681() {
        //DMP-3681:IF = TRUE
        sysPbLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        SystemPiggyback sysPb = mapPb.get("dmp3681");
        DataPixel dataPixel = mapDataPixel.get("default");
        int i = 0;
        while (i < num && !isPassed) {
            goPbHtml(true);
            sshWrapperETL.getFile(dmpEtlFile, dmpEtllocal, etlpath, false);
            sshWrapperETL.getFile(requestDataPartnerProdEtlFile, requestDataPartnerProdEtlFileLocal, etlpath, false);
            int isFind = proxyWrapper.findRequestUrl(sysPb.getUrl());
            if (isFind > 0) {
                sysPbLOG.info("Check DmpUserModel");
                String ucookie = proxyWrapper.findCookieByNameAtRequests("u");
                boolean isDmpUserModelChecked = checkDmpUserModelETL(ucookie, dataPixel.getId().toString(), null);
                if (!isDmpUserModelChecked){
                    sshWrapperETL.getFile(dmpEtlFile, dmpEtllocal, etlpath, false);
                    isDmpUserModelChecked = checkDmpUserModelETL(ucookie, dataPixel.getId().toString(), null);
                }
                sysPbLOG.info("isDmpUserModelChecked = " + isDmpUserModelChecked);
                boolean isRequestDataPartnerProdChecked = checkAsRequestDataPartnerProd(ucookie, dataPixel.getTagId().toString(), sysPb.getId().toString());
                if (!isRequestDataPartnerProdChecked){
                    sshWrapperETL.getFile(requestDataPartnerProdEtlFile, requestDataPartnerProdEtlFileLocal, etlpath, false);
                    isRequestDataPartnerProdChecked = checkAsRequestDataPartnerProd(ucookie, dataPixel.getTagId().toString(), sysPb.getId().toString());
                }
                sysPbLOG.info("isRequestDataPartnerProdChecked = " + isRequestDataPartnerProdChecked);
                isPassed = isDmpUserModelChecked && isRequestDataPartnerProdChecked;
            }
            i++;
            if (!isPassed) wait(5);
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        sysPbLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3683() {
        //DMP-3683:Weight
        sysPbLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        SystemPiggyback sysPb1000 = mapPb.get("dmp3683_1");
        SystemPiggyback sysPb999 = mapPb.get("dmp3683_2");
        int i = 0;
        while (i < num && !isPassed) {
            goPbHtml(true);
            isPassed = proxyWrapper.getOrderOfRequests(sysPb1000.getUrl(), sysPb999.getUrl());
            if (!isPassed) wait(5);
            i++;
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        sysPbLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3686() {
        //DMP-3686:Dates range
        sysPbLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        SystemPiggyback sysPb = mapPb.get("dmp3686");
        int i = 0;
        while (i < num && !isPassed) {
            goPbHtml(true);
            int isFind = proxyWrapper.findRequestUrl(sysPb.getUrl());
            isPassed = isFind == 0;
            if (!isPassed) wait(5);
            i++;
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        sysPbLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3687() {
        //DMP-3687:Non Active
        sysPbLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        SystemPiggyback sysPb = mapPb.get("dmp3687");
        int i = 0;
        while (i < num && !isPassed) {
            goPbHtml(true);
            int isFind = proxyWrapper.findRequestUrl(sysPb.getUrl());
            isPassed = isFind == 0;
            if (!isPassed) wait(5);
            i++;
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        sysPbLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3684() {
        //DMP-3684:Http
        sysPbLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        SystemPiggyback sysPb = mapPb.get("dmp3684");
        int i = 0;
        while (i < num && !isPassed) {
            goPbHtml(true);
            int isFind = proxyWrapper.findRequestUrl(sysPb.getUrl());
            isPassed = isFind > 0;
            if (!isPassed) wait(5);
            i++;
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        sysPbLOG.info(name.getMethodName() + " PASSED");
    }

    // @Test
    // not ready support of https
    public void testDMP3685_1() {
        //DMP-3685:Https
        //check IF = req.partnerId>1 && !req.isSecure
        //use https
        SystemPiggyback sysPb = mapPb.get("dmp3685_1");
        String url = urlPixelHTTPS + "?" + DateWrapper.getRandom();
        proxyWrapper.watchUrl(url);
        sysPbLOG.info("Go to url = " + url);
        webDriverWrapper.getPage(url);
        wait(10);
        int isFind = proxyWrapper.findRequestUrl(sysPb.getUrl());
        assertTrue("Fail dmp3685_1", isFind == 0);
        sysPbLOG.info("testDMP3685_1 PASSED");
    }

    // @Test
    // not ready support of https
    public void testDMP3685_2() {
        //DMP-3685:Https
        //check IF = req.partnerId>1
        //use https
        SystemPiggyback sysPb = mapPb.get("dmp3685_2");
        String url = urlPixelHTTPS + "?" + DateWrapper.getRandom();
        proxyWrapper.watchUrl(url);
        sysPbLOG.info("Go to url = " + url);
        webDriverWrapper.getPage(url);
        wait(10);
        int isFind = proxyWrapper.findRequestUrl(sysPb.getUrl());
        assertTrue("Fail dmp3685_2", isFind > 0);
        sysPbLOG.info("testDMP3685_2 PASSED");
    }

    @Test
    public void testDMP3682() {
        //DMP-3682:IF = FALSE
        sysPbLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        SystemPiggyback sysPb = mapPb.get("dmp3682");
        int i = 0;
        while (i < num && !isPassed) {
            goPbHtml(true);
            int isFind = proxyWrapper.findRequestUrl(sysPb.getUrl());
            isPassed = isFind == 0;
            i++;
            if (!isPassed) wait(5);
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        sysPbLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3679() {
        //DMP-3679:IF - incorrect
        sysPbLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        SystemPiggyback sysPb = mapPb.get("dmp3679");
        int i = 0;
        while (i < num && !isPassed) {
            goPbHtml(true);
            int isFind = proxyWrapper.findRequestUrl(sysPb.getUrl());
            isPassed = isFind == 0;
            if (!isPassed) wait(5);
            i++;
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        sysPbLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3680() {
        //DMP-3680:Then - incorrect
        sysPbLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        SystemPiggyback sysPb = mapPb.get("dmp3680");
        int i = 0;
        while (i < num && !isPassed) {
            goPbHtml(true);
            int isFind = proxyWrapper.findRequestUrl(sysPb.getUrl());
            isPassed = isFind == 0;
            if (!isPassed) wait(5);
            i++;
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        sysPbLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3675() {
        //DMP-3675:Second
        sysPbLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        sysPbLOG.info("Change pixelId at pb.html");
        SSHWrapper sshWrapper = new SSHWrapper(pathWebserver, "autotest", "812redaril");
        sshWrapper.getFile(filePbHtml, filePbHtml, pathPbHtml, true);
        FileHelper.getInstance().copyFile(filePbHtml, filePbHtml + "2");
        DataPixel dataPixel3675 = mapDataPixel.get("dataPixel3675");
        FileHelper.getInstance().findAndReplaceStringAtFile(filePbHtml, "_pixel", "_pixel=" + dataPixel3675.getTagId() + ";");
        sshWrapper.putFile(filePbHtml, filePbHtml, pathPbHtml);
        SystemPiggyback sysPb = mapPb.get("dmp3675");
        int i = 0;
        while (i < num && !isPassed) {
            goPbHtml(false);
            int isFind = proxyWrapper.findRequestUrl(sysPb.getUrl());
            goPbHtml(false);
            int isFind2 = proxyWrapper.findRequestUrl(sysPb.getUrl());
            wait(delaySec);
            goPbHtml(false);
            int isFind3 = proxyWrapper.findRequestUrl(sysPb.getUrl());
            isPassed = isFind > 0 & isFind2 == 0 & isFind3 > 0;
            if (!isPassed) wait(delaySec);
            i++;
        }
        sshWrapper.putFile(filePbHtml + "2", filePbHtml, pathPbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml + "2");
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        sysPbLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3676() {
        //DMP-3676:Minute
        sysPbLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        sysPbLOG.info("Change pixelId at pb.html");
        SSHWrapper sshWrapper = new SSHWrapper(pathWebserver, "autotest", "812redaril");
        sshWrapper.getFile(filePbHtml, filePbHtml, pathPbHtml, true);
        FileHelper.getInstance().copyFile(filePbHtml, filePbHtml + "2");
        DataPixel dataPixel3676 = mapDataPixel.get("dataPixel3676");
        FileHelper.getInstance().findAndReplaceStringAtFile(filePbHtml, "_pixel", "_pixel=" + dataPixel3676.getTagId() + ";");
        sshWrapper.putFile(filePbHtml, filePbHtml, pathPbHtml);
        SystemPiggyback sysPb = mapPb.get("dmp3676");
        int i = 0;
        while (i < num && !isPassed) {
            goPbHtml(false);
            int isFind = proxyWrapper.findRequestUrl(sysPb.getUrl());
            goPbHtml(false);
            int isFind2 = proxyWrapper.findRequestUrl(sysPb.getUrl());
            wait(60);
            goPbHtml(false);
            int isFind3 = proxyWrapper.findRequestUrl(sysPb.getUrl());
            isPassed = isFind > 0 & isFind2 == 0 & isFind3 > 0;
            if (!isPassed) wait(5);
            i++;
        }
        sshWrapper.putFile(filePbHtml + "2", filePbHtml, pathPbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml + "2");
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        sysPbLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3677() {
        //DMP-3677:Hour
        sysPbLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        sysPbLOG.info("Change pixelId at pb.html");
        SSHWrapper sshWrapper = new SSHWrapper(pathWebserver, "autotest", "812redaril");
        sshWrapper.getFile(filePbHtml, filePbHtml, pathPbHtml, true);
        FileHelper.getInstance().copyFile(filePbHtml, filePbHtml + "2");
        DataPixel dataPixel3677 = mapDataPixel.get("dataPixel3677");
        FileHelper.getInstance().findAndReplaceStringAtFile(filePbHtml, "_pixel", "_pixel=" + dataPixel3677.getTagId() + ";");
        sshWrapper.putFile(filePbHtml, filePbHtml, pathPbHtml);
        SystemPiggyback sysPb = mapPb.get("dmp3677");
        int i = 0;
        while (i < num && !isPassed) {
            goPbHtml(false);
            int isFind = proxyWrapper.findRequestUrl(sysPb.getUrl());
            goPbHtml(false);
            int isFind2 = proxyWrapper.findRequestUrl(sysPb.getUrl());
            isPassed = isFind > 0 & isFind2 == 0;
            if (!isPassed) wait(5);
            i++;
        }
        sshWrapper.putFile(filePbHtml + "2", filePbHtml, pathPbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml + "2");
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        sysPbLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP3678() {
        //DMP-3678:Day
        sysPbLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        sysPbLOG.info("Change pixelId at pb.html");
        SSHWrapper sshWrapper = new SSHWrapper(pathWebserver, "autotest", "812redaril");
        sshWrapper.getFile(filePbHtml, filePbHtml, pathPbHtml, true);
        FileHelper.getInstance().copyFile(filePbHtml, filePbHtml + "2");
        DataPixel dataPixel3678 = mapDataPixel.get("dataPixel3678");
        FileHelper.getInstance().findAndReplaceStringAtFile(filePbHtml, "_pixel", "_pixel=" + dataPixel3678.getTagId() + ";");
        sshWrapper.putFile(filePbHtml, filePbHtml, pathPbHtml);
        SystemPiggyback sysPb = mapPb.get("dmp3678");
        int i = 0;
        while (i < num && !isPassed) {
            goPbHtml(false);
            int isFind = proxyWrapper.findRequestUrl(sysPb.getUrl());
            goPbHtml(false);
            int isFind2 = proxyWrapper.findRequestUrl(sysPb.getUrl());
            isPassed = isFind > 0 & isFind2 == 0;
            if (!isPassed) wait(5);
            i++;
        }
        sshWrapper.putFile(filePbHtml + "2", filePbHtml, pathPbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml + "2");
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        sysPbLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP4501() {
        //DMP-4501:sysPb(custom)
        sysPbLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        sysPbLOG.info("Change pixelId at pb.html");
        SSHWrapper sshWrapper = new SSHWrapper(pathWebserver, "autotest", "812redaril");
        sshWrapper.getFile(filePbHtml, filePbHtml, pathPbHtml, true);
        FileHelper.getInstance().copyFile(filePbHtml, filePbHtml + "2");
        DataPixel dataPixel = mapDataPixel.get("dataPixel4501");
        FileHelper.getInstance().findAndReplaceStringAtFile(filePbHtml, "_pixel", "_pixel=" + dataPixel.getTagId() + ";");
        sshWrapper.putFile(filePbHtml, filePbHtml, pathPbHtml);
        SystemPiggyback sysPb = mapPb.get("dmp4501");
        int i = 0;
        while (i < num && !isPassed) {
            goPbHtml(true);
            sshWrapperETL.getFile(dmpEtlFile, dmpEtllocal, etlpath, false);
            sshWrapperETL.getFile(requestDataPartnerProdEtlFile, requestDataPartnerProdEtlFileLocal, etlpath, false);
            int isFind = proxyWrapper.findRequestUrl(sysPb.getUrl());
            if (isFind > 0) {
                sysPbLOG.info("Check DmpUserModel");
                String ucookie = proxyWrapper.findCookieByNameAtRequests("u");
                boolean isDmpUserModelChecked = checkDmpUserModelETL(ucookie, dataPixel.getId().toString(), null);
                if (!isDmpUserModelChecked){
                    sshWrapperETL.getFile(dmpEtlFile, dmpEtllocal, etlpath, false);
                    isDmpUserModelChecked = checkDmpUserModelETL(ucookie, dataPixel.getId().toString(), null);
                }
                sysPbLOG.info("isDmpUserModelChecked = " + isDmpUserModelChecked);
                boolean isRequestDataPartnerProdChecked = checkAsRequestDataPartnerProd(ucookie, dataPixel.getTagId().toString(), sysPb.getId().toString());
                if (!isRequestDataPartnerProdChecked){
                    sshWrapperETL.getFile(requestDataPartnerProdEtlFile, requestDataPartnerProdEtlFileLocal, etlpath, false);
                    isRequestDataPartnerProdChecked = checkAsRequestDataPartnerProd(ucookie, dataPixel.getTagId().toString(), sysPb.getId().toString());
                }
                sysPbLOG.info("isRequestDataPartnerProdChecked = " + isRequestDataPartnerProdChecked);
                isPassed = isDmpUserModelChecked && isRequestDataPartnerProdChecked;
            }
            i++;
            if (!isPassed) wait(5);
        }
        sshWrapper.putFile(filePbHtml + "2", filePbHtml, pathPbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml + "2");
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        sysPbLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testDMP4500() {
        //DMP-4500:sysPb(NONE)
        sysPbLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        sysPbLOG.info("Change pixelId at pb.html");
        SSHWrapper sshWrapper = new SSHWrapper(pathWebserver, "autotest", "812redaril");
        sshWrapper.getFile(filePbHtml, filePbHtml, pathPbHtml, true);
        FileHelper.getInstance().copyFile(filePbHtml, filePbHtml + "2");
        DataPixel dataPixel4500 = mapDataPixel.get("dataPixel4500");
        FileHelper.getInstance().findAndReplaceStringAtFile(filePbHtml, "_pixel", "_pixel=" + dataPixel4500.getTagId() + ";");
        sshWrapper.putFile(filePbHtml, filePbHtml, pathPbHtml);
        SystemPiggyback sysPb = mapPb.get("dmp4500_1");
        SystemPiggyback sysPb2 = mapPb.get("dmp4500_2");//thirdParty Syspb
        int i = 0;
        while (i < num && !isPassed) {
            goPbHtml(false);
            int isFind = proxyWrapper.findRequestUrl(sysPb.getUrl());
            int isFind2 = proxyWrapper.findRequestUrl(sysPb2.getUrl());
            isPassed = isFind > 0 && isFind2 == 0;
            if (!isPassed) wait(5);
            i++;
        }
        sshWrapper.putFile(filePbHtml + "2", filePbHtml, pathPbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml);
        FileHelper.getInstance().deleteFile(filePbHtml + "2");
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        sysPbLOG.info(name.getMethodName() + " PASSED");
    }

    @AfterClass
    public static void tearDownSystemPbs() {
        for (Map.Entry<String, DataPixel> entry : mapDataPixel.entrySet()) {
            wsHelper.deleteDataPixel(entry.getValue());
        }
        for (Map.Entry<String, SystemPiggyback> entry : mapPb.entrySet()) {
            wsHelper.deleteSystemPiggyback(entry.getValue());
        }
        for (AudienceGroup audienceGroup : setAG) {
            wsHelper.deleteAudienceGroup(audienceGroup);
        }

    }
}
