package com.redaril.dmptf.util.network.lib.htmlunit;


import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.file.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;

public class HtmlunitWrapper {

    //PATH and FILES
    protected final static String CONFIG_DIR = "config" + File.separator;
    private final static String ENV_FILE_PROPERTIES = "env.properties";

    //WRAPPER
    private static WebClient webClient;

    //LOGGER
    private static Logger LOG;
    protected final static String LogSystemProperty = "DmptfLogFile";

    //CONFIGURATION
    protected static ConfigurationLoader envConfigurationLoader;
    protected static String ENV;
    protected static String envConfigID;

//    static {
//        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
//    }

    public HtmlunitWrapper(String logfile) {
        //log4j setup
        FileHelper.getInstance().deleteFile("output" + File.separator + "logs" + File.separator + logfile);
        System.setProperty(LogSystemProperty, logfile);
        envConfigurationLoader = new ConfigurationLoader(CONFIG_DIR + ENV_FILE_PROPERTIES);
        LOG = LoggerFactory.getLogger(HtmlunitWrapper.class);
        LOG.info("Initialize HtmlunitWrapper");

        //setting up environment
        ENV = envConfigurationLoader.getProperty("env");
        envConfigID = envConfigurationLoader.getProperty("configID");

        //web client initiation
        webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_8);
        webClient.setAppletEnabled(true);
        webClient.setCssEnabled(true);
        webClient.setJavaScriptEnabled(true);
        webClient.setRedirectEnabled(true);
        webClient.setThrowExceptionOnScriptError(false);
        webClient.setThrowExceptionOnFailingStatusCode(false);
        class IncLis implements IncorrectnessListener {
            @Override
            public void notify(String arg0, Object arg1) {
            }
        }
        IncLis listener = new IncLis();
        webClient.setIncorrectnessListener(listener);
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiesEnabled(true);
        webClient.setCookieManager(cookieManager);
        webClient.setActiveXNative(false);
//        ErrorHandler cssErrorHandler = new DefaultCssErrorHandler();
//        webClient.setCssErrorHandler(cssErrorHandler);
//        webClient.getCssErrorHandler().
    }

    public void deleteAllCookies() {
        webClient.getCookieManager().clearCookies();
        LOG.info("DELETE cookies");
    }

    public String getCookieValueByName(String name) {
        Cookie cookie = webClient.getCookieManager().getCookie(name);
        if (cookie != null) {
            LOG.info("Get " + name + " cookie. Value = " + cookie.getValue());
            return cookie.getValue();
        } else {
            LOG.error("Get " + name + " cookie. Cookie was not found");
            fail("Get " + name + " cookie. Cookie was not found");
            return null;
        }
    }

    public String getCurrentUrl() {
        String url = webClient.getCurrentWindow().getEnclosedPage().getUrl().toString();
        LOG.info("Get current url = " + url);
        return url;
    }

    public void goToUrl(String url) {
        try {
            LOG.info("Go to URL = " + url);
            Page page = webClient.getPage(url);
        } catch (IOException e) {
            LOG.error("Can't go to URL = " + url + "   " + e.getLocalizedMessage());
            fail("Can't go to URL = " + url);

        }
    }

    public void goToUrlAndClick(String url, String button) {
        LOG.info("Go to URL = " + url);
        HtmlPage page = null;
        try {
            page = webClient.getPage(url);
        } catch (IOException e) {
            LOG.error("We got IOException. Exception = " + e.getLocalizedMessage());
            fail("Can't get page from url = " + url);
        }
        HtmlSubmitInput input;
        Boolean isFind = false;
        Integer i = 0;
        while (!isFind && i < 100) {
            try {
                input = page.getFirstByXPath(button);
                if (input.isDisplayed()) {
                    input.click();
                    isFind = true;
                } else {
                    Thread.sleep(1000);
                }
                i++;
            } catch (IOException e) {
                LOG.info("We got IOException. Exception = " + e.getLocalizedMessage());
            } catch (InterruptedException e) {
                LOG.info("We got InterruptedException. Exception = " + e.getLocalizedMessage());
            }
        }
        if (!isFind) {
            LOG.error("Can't find an element = " + button);
            fail("Can't find an element = " + button);
        }
    }
}
