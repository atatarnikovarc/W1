package com.redaril.dmptf.tests.test.pip.piggyback;

import com.redaril.dmp.model.meta.SystemPiggyback;
import com.redaril.dmptf.tests.support.pip.base.BasePiggybackTest;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.date.DateWrapper;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.network.appinterface.jmx.JMXWrapper;
import com.redaril.dmptf.util.network.protocol.ssh.SSHWrapper;
import com.redaril.dmptf.util.selenium.ProxyWrapper;
import com.redaril.dmptf.util.selenium.WebDriverWrapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 01.04.13
 * Time: 15:07
 * To change this template use File | Settings | File Templates.
 */
public class TestExistedPiggyback extends BasePiggybackTest {
    private static boolean isSetupSystemPb;
    private static List<SystemPiggyback> systemPiggybackList = new ArrayList<SystemPiggyback>();
    private static Logger sysPbLOG;
    private final static long dataSourceRAInterestId = 1005;

    @Rule
    public TestName name = new TestName();

    private static String getNewDataPixel(long dataSourceId) {
        return wsHelper.createDataPixel(Long.toString(dataSourceId));
    }

    private void watchAndGoUrl(String url) {
        proxyWrapper.watchUrl(url);
        sysPbLOG.info("Go to url = " + url);
        webDriverWrapper.getPage(url);
        wait(5);
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
                sysPbLOG.error("Page is unavailable. Reload page.");
            }
        }
    }

    @Before
    public void setup() {
        if (!isSetupSystemPb) {
            System.setProperty(LogSystemProperty, "existedPiggyback.log");
            sysPbLOG = LoggerFactory.getLogger(TestExistedPiggyback.class);
            configEnv = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
            ENV = configEnv.getProperty("env");
            configID = configEnv.getProperty("configID");
            webDriverWrapper = new WebDriverWrapper(ENV);
            proxyWrapper = new ProxyWrapper(ENV, null);
            //create data for tests
            super.setUpPiggybacks();
            getFirstDriverFromFile();
            String pixelId = getNewDataPixel(dataSourceRAInterestId);
            getPiggybacks();
            //end of creating
            jmxWrapper = new JMXWrapper(ENV, configID, "pip");
            //set pixelId into pb.html
            sysPbLOG.info("Change pixelId at pb.html");
            SSHWrapper sshWrapper = new SSHWrapper(pathWebserver, "autotest", "812redaril");
            sshWrapper.getFile(filePbHtml, filePbHtml, pathPbHtml, true);
            FileHelper.getInstance().findAndReplaceStringAtFile(filePbHtml, "_pixel", "_pixel=" + pixelId + ";");
            sshWrapper.putFile(filePbHtml, filePbHtml, pathPbHtml);
            FileHelper.getInstance().deleteFile(filePbHtml);
            //end
            reloadpip();
            webDriverWrapper.getDriver(oneDriverInfo, proxyWrapper.getProxy(), null);
            isSetupSystemPb = true;
        }
    }

    private void getPiggybacks() {
        List<Long> existedSystemPb = new ArrayList<Long>();
        existedSystemPb.add(3658574L);
        existedSystemPb.add(3658589L);
        existedSystemPb.add(3658679L);
        existedSystemPb.add(3830266L);
        existedSystemPb.add(3658477L);
        existedSystemPb.add(3658487L);
        existedSystemPb.add(3658534L);
        existedSystemPb.add(3658636L);
        existedSystemPb.add(3658574L);
        existedSystemPb.add(3680035L);
        for (Long id : existedSystemPb) {
            systemPiggybackList.add(wsHelper.getSystemPiggyback(id));
        }
    }

    @Test
    public void test() {
        sysPbLOG.info(name.getMethodName() + " STARTED");
        boolean isPassed = false;
        int i = 0;
        int isFind;
        while (i < num && !isPassed) {
            goPbHtmlForce();
            int count = 0;
            for (SystemPiggyback systemPiggyback : systemPiggybackList) {
                String url = systemPiggyback.getUrl();
                if (url.contains("{")) url = url.substring(0, url.indexOf("{"));
                isFind = proxyWrapper.findRequestUrl(url);
                if (isFind > 0) count++;
                else {
                    sysPbLOG.error("Can't find Piggyback. Id = " + systemPiggyback.getId() + " . Url = " + url);
                }
            }
            if (count == systemPiggybackList.size()) isPassed = true;
            i++;
            wait(30);

        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        sysPbLOG.info(name.getMethodName() + " PASSED");
    }
}
