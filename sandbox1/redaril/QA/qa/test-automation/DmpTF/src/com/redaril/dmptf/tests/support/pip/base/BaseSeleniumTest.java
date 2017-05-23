package com.redaril.dmptf.tests.support.pip.base;

import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.selenium.ProxyWrapper;
import com.redaril.dmptf.util.selenium.WebDriverWrapper;
import org.junit.AfterClass;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public abstract class BaseSeleniumTest {
    protected final static String PATH_CONFIG = "config" + File.separator;
    protected final static String LogSystemProperty = "DmptfLogFile";
    protected final static String FILE_PROPERTIES_ENV = "env.properties";
    protected final static String FILE_PROPERTIES_APP = "app.properties";
    protected static String ENV;
    protected static String configID;
    protected static ConfigurationLoader config;
    protected static ConfigurationLoader configEnv;
    protected final static String SOURCE_PATH = "data" + File.separator + "selenium" + File.separator;
    protected final static List<String> columnNames = Arrays.asList("OS", "browser", "version", "ip", "requestStart");
    protected static ProxyWrapper proxyWrapper;
    protected static WebDriverWrapper webDriverWrapper;

    @AfterClass
    public static void tearDown() {
        if (webDriverWrapper != null) WebDriverWrapper.tearDown();
        if (proxyWrapper != null) proxyWrapper.stopProxyServer();
    }

    protected static void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
