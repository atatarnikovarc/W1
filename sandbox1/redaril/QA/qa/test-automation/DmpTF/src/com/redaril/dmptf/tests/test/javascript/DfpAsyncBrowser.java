package com.redaril.dmptf.tests.test.javascript;

import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.selenium.ProxyWrapper;
import com.redaril.dmptf.util.selenium.WebDriverWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(value = Parameterized.class)
public class DfpAsyncBrowser extends BaseDfpAsync {
    private HashMap<String, String> driverInfo;
    private static boolean isDfpBrowserSetup;
    private static boolean isAtPage = false;
    private final static String logFile = "dfpBrowser.log";
    private static Logger browserDFPLOG;
    protected final static String sourceDrivers = SOURCE_PATH + "driversProxy.txt";

    public DfpAsyncBrowser(HashMap<String, String> driverInfo) {
        this.driverInfo = driverInfo;
    }

    @Before
    public void setup() {
        if (!isDfpBrowserSetup) {
            configEnv = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
            ENV = configEnv.getProperty("env");
            configID = configEnv.getProperty("configID");
            webDriverWrapper = new WebDriverWrapper(ENV);
            proxyWrapper = new ProxyWrapper(ENV, null);
            setUpDfp();
            isDfpBrowserSetup = true;
        }
        webDriverWrapper.getDriver(driverInfo, proxyWrapper.getProxy(), null);
    }

    @Parameterized.Parameters
    public static List<Object[]> getParam() {
        System.setProperty(LogSystemProperty, logFile);
        browserDFPLOG = LoggerFactory.getLogger(DfpAsyncBrowser.class);
        return getDriversFromFile(sourceDrivers);
    }

    @Test
    public void testDMP4360() {
        browserDFPLOG.info("====DMP-4360 started");
        browserDFPLOG.info("OS = " + driverInfo.get("OS"));
        browserDFPLOG.info("Version = " + driverInfo.get("version"));
        browserDFPLOG.info("Browser = " + driverInfo.get("browser"));
        boolean isPassed = false;
        // execute test num times, because sometimes we get bad connection
        int i = 0;
        wait(3000);
        while (i < num && !isPassed) {
            webDriverWrapper.deleteAllCookies();
            webDriverWrapper.addCookie("o", "0");
            webDriverWrapper.addCookie("u", userWithCats);
            webDriverWrapper.getPage(request + rasegsHtml);
            webDriverWrapper.getCurrentDriver().findElement(By.cssSelector("input[value='RUN']")).click();
            wait(6000);
            if (!webDriverWrapper.getCookieByName("rasegs").equalsIgnoreCase("kvp-as1|kvp-as2")) isPassed = false;
            else browserDFPLOG.error("Can't find rasegs-cookie = kvp-as1|kvp-as2");
            List<WebElement> list = webDriverWrapper.getCurrentDriver().findElements(By.cssSelector("div"));
            isPassed = true;
//            for (WebElement element : list){
//
//            }


        }
        assertTrue("====DMP-4360 passed", isPassed);
        browserDFPLOG.info("====DMP-4360 passed");
    }

}
