package com.redaril.dmptf.tests.test.optout;

import com.redaril.dmptf.tests.support.pip.base.BaseSeleniumTest;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.date.DateWrapper;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.selenium.WebDriverWrapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;

//import org.apache.log4j.Level;
//import org.slf4j.Logger;

public class TestOptout extends BaseSeleniumTest {
    private static Logger LOG;
    private final static String PATH_CONFIG = "config" + File.separator;
    private final static String LogSystemProperty = "DmptfLogFile";
    private static Boolean isInit = false;
    private static String optUrl1;
    private static String optUrl2;
    private static String urlToCreateUser;
    private static String button1;
    private static String button2;
    protected final static String FILE_PROPERTIES_APP = "app.properties";
    private final static String FILE_PROPERTIES_ENV = "env.properties";
    private final static String FILE_PROPERTIES = "optout.properties";
    private final static String logFile = "optout.properties";
    protected static HashMap<String, String> oneDriverInfo;
    protected final static String sourceDrivers = SOURCE_PATH + "driversProxy.txt";

    @Rule
    public TestName name = new TestName();

    protected static void getFirstDriverFromFile() {
        //get data from file into List<String>
        // Object[] array = new Object[1];
        List<String> ipList = FileHelper.getInstance().getDataFromFile(sourceDrivers);
        //end get data
        //parse first line into hashmap, all hashmaps put into array[1] and into List() (structure List<Object[]>)
        String[] params;
        HashMap<String, String> data = new HashMap<>();
        params = ipList.get(0).split(";");
        for (int j = 0; j < columnNames.size(); j++) {
            data.put(columnNames.get(j), params[j]);
        }
        oneDriverInfo = data;
    }

    @Before
    public void setUp() {
        if (!isInit) {
            ConfigurationLoader config = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES);
            System.setProperty(LogSystemProperty, logFile);
            LogConfigurer.initLogback();
            LOG = LoggerFactory.getLogger(TestOptout.class);
            optUrl1 = config.getProperty("optUrl1");
            optUrl2 = config.getProperty("optUrl2");
            button1 = config.getProperty("button1");
            button2 = config.getProperty("button2");
            ConfigurationLoader configApp = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_APP);
            String port = configApp.getProperty("httpPort");
            String baseDomain = configApp.getProperty("baseDomain");
            ConfigurationLoader envConfigurationLoader = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
            String ENV = envConfigurationLoader.getProperty("env");
            String configID = envConfigurationLoader.getProperty("configID");
            //urlToCreateUser = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/pixel?id=CAESEOS8DIt22VoL5E4ei5mPYhA&cver=1&t=gcm";
            urlToCreateUser = "http://www.inc.com/";
            webDriverWrapper = new WebDriverWrapper(ENV);
            getFirstDriverFromFile();
            FileHelper.getInstance().copyFile("data/hosts/" + ENV + "hosts.txt", "hosts");
            FileHelper.getInstance().findAndReplaceStringAtFile("hosts", "p0.raasnet.com", "#10.33.4.47 p0.raasnet.com");
            FileHelper.getInstance().findAndReplaceStringAtFile("hosts", "p.raasnet.com", "#10.33.4.47 p.raasnet.com");
            FileHelper.getInstance().findAndReplaceStringAtFile("hosts", "a.raasnet.com", "#10.33.4.47 a.raasnet.com");
            webDriverWrapper.getDriver(oneDriverInfo, null, "hosts");
            isInit = true;
        }
    }


    private void testWithoutAnyCookies(String optUrl, String button) {
        LOG.info("----testWithoutAnyCookies----");
        int i = 0;
        String oCookie = "";
        while (!oCookie.equalsIgnoreCase("9") && i < 3) {
            webDriverWrapper.deleteAllCookies();
            if (!button.equalsIgnoreCase("null")) {
                webDriverWrapper.getPage(optUrl);
                webDriverWrapper.clickButton(button);
                wait(3000);
                try {
                    webDriverWrapper.getCurrentDriver().switchTo().alert().accept();
                } catch (Exception e) {
                }
                ;
            } else {
                webDriverWrapper.getPage(optUrl);
            }
            oCookie = webDriverWrapper.getCookieByName("o");
            i++;
        }
        assertTrue("Test failed. O-Cookie is not 9. Value = " + oCookie, oCookie.equalsIgnoreCase("9"));
    }

    private void testWithRACookies(String optUrl, String button) {
        LOG.info("----testWithRACookies----");
        int i = 0;
        String oCookie = "";
        while (!oCookie.equalsIgnoreCase("9") && i < 3) {
            webDriverWrapper.deleteAllCookies();
            webDriverWrapper.getPage(urlToCreateUser);
            if (!button.equalsIgnoreCase("null")) {
                webDriverWrapper.getPage(optUrl);
                webDriverWrapper.clickButton(button);
                wait(3000);
                try {
                    webDriverWrapper.getCurrentDriver().switchTo().alert().accept();
                } catch (Exception e) {
                }
                ;
            } else {
                webDriverWrapper.getPage(optUrl);
            }
            oCookie = webDriverWrapper.getCookieByName("o");
            i++;
        }
        assertTrue("Test failed. O-Cookie is not 9. Value = " + oCookie, oCookie.equalsIgnoreCase("9"));
    }

    private void testWithOptOutCookies(String optUrl, String button) {
        LOG.info("----testWithOptOutCookies----");
        int i = 0;
        String oCookie = "";
        while (!oCookie.equalsIgnoreCase("9") && i < 3) {
            webDriverWrapper.deleteAllCookies();
            if (!button.equalsIgnoreCase("null")) {
                webDriverWrapper.getPage(optUrl);
                webDriverWrapper.clickButton(button);
                wait(3000);
                try {
                    webDriverWrapper.getCurrentDriver().switchTo().alert().accept();
                } catch (Exception e) {
                }
                ;
            } else {
                webDriverWrapper.getPage(optUrl);

            }
            oCookie = webDriverWrapper.getCookieByName("o");
            i++;
        }
        assertTrue("Test failed. O-Cookie is not 9. Value = " + oCookie, oCookie.equalsIgnoreCase("9"));
    }

    //if i=0 we get today date
    // if i>0 we get date in future
    private String getUrlNai(String optUrl, Integer i) {
        Long time = DateWrapper.getLongTime() + i;
        return optUrl.replaceAll("<time>", time.toString());
    }

    @Test
    public void testCoreAudienceWithoutAnyCookies() {
        LOG.info(name.getMethodName() + " STARTED");
        LOG.info("Check url = " + optUrl1);
        testWithoutAnyCookies(optUrl1, button1);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testCoreAudienceWithRACookies() {
        LOG.info(name.getMethodName() + " STARTED");
        LOG.info("Check url = " + optUrl1);
        testWithRACookies(optUrl1, button1);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testCoreAudienceWithOptOutCookies() {
        LOG.info(name.getMethodName() + " STARTED");
        LOG.info("Check url = " + optUrl1);
        testWithOptOutCookies(optUrl1, button1);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testNAIWithRACookies() {
        LOG.info(name.getMethodName() + " STARTED");
        String url = getUrlNai(optUrl2, 0);
        LOG.info("Check url = " + url);
        testWithRACookies(url, button2);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testNAIWithoutAnyCookies() {
        LOG.info(name.getMethodName() + " STARTED");
        String url = getUrlNai(optUrl2, 0);
        LOG.info("testCoreAudienceWithoutAnyCookies. Check url = " + url);
        testWithoutAnyCookies(url, button2);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testNAIWithOptOutCookies() {
        LOG.info(name.getMethodName() + " STARTED");
        String url = getUrlNai(optUrl2, 0);
        LOG.info("Check url = " + url);
        testWithOptOutCookies(url, button2);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testNAIWithoutAnyCookiesWithTokenInFuture() {
        LOG.info(name.getMethodName() + " STARTED");
        String url = getUrlNai(optUrl2, 1000000);
        LOG.info("Check url = " + url);
        testWithoutAnyCookies(url, button2);
        LOG.info(name.getMethodName() + " PASSED");
    }
}
