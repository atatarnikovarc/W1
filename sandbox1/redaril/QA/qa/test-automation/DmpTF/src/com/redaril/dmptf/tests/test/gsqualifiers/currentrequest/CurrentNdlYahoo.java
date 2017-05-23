package com.redaril.dmptf.tests.test.gsqualifiers.currentrequest;

import com.redaril.dmptf.tests.support.gsqualifiers.BasicTestGeneralSearchQualifier;
import com.redaril.dmptf.tests.support.gsqualifiers.GeneralSearchQualifier;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.selenium.WebDriverWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 23.11.12
 * Time: 16:06
 * To change this template use File | Settings | File Templates.
 */
public class CurrentNdlYahoo extends BasicTestGeneralSearchQualifier {
    private static String query;
    private HashMap<String, String> driverInfo = new HashMap<String, String>();
    private final static String SOURCE_PATH = "data" + File.separator + "selenium" + File.separator;
    private final static String sourceDrivers = SOURCE_PATH + "drivers.txt";

    @Before
    public void setup() {
        logFile = "currentNdlYahoo.log";
        String getDomainsSQL = pathToSql + "getDomainsYahoo.sql";
        baseSetup();
        getOneGeneralSearchQualifiers();
        GeneralSearchQualifier qualifier = qualifiers.get(0);
        query = getQueryByRegex(qualifier.getRegex(), qualifier.isStemmed());
        getDomainsByScript(getDomainsSQL);
        //get first driver from file
        List<String> ipList = FileHelper.getInstance().getDataFromFile(sourceDrivers);
        String[] params;
        String aipList = ipList.get(0);
        params = aipList.split(";");
        for (int j = 0; j < columnNames.size(); j++) {
            driverInfo.put(columnNames.get(j), params[j]);
        }
        //end
        webDriverWrapper = new WebDriverWrapper(ENV);
        webDriverWrapper.getDriver(driverInfo, null, null);
    }

    @After
    public void after() {
        WebDriverWrapper.tearDown();
        if (proxyWrapper != null) proxyWrapper.stopProxyServer();
        wait(3000);
    }

    protected void submitForm(String baseUrl, int attempt) {
        //  if (baseUrl.contains("us.search.yahoo.com")||baseUrl.contains("http://espanol.search.yahoo.com/")){
        if (baseUrl.equalsIgnoreCase("http://video.search.yahoo.com") || baseUrl.equalsIgnoreCase("http://images.search.yahoo.com")) {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[id='yschsp']"));
            if (input != null) input.sendKeys(query);
            WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[type='submit']"));
            if (button != null) button.click();
        } else if (baseUrl.contains("www.yahoo.com")) {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("a[class='tab-label-first med-small']"));
            if (input != null) input.click();
            input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='p']"));
            if (input != null) input.sendKeys(query);
            WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("button[type='submit']"));
            if (button != null) button.click();
            wait(5000);
        } else if (baseUrl.contains("video.search.yahoo.com") || baseUrl.contains("images.search.yahoo.com") || baseUrl.contains("image.search.yahoo.com") || baseUrl.contains("videos.search.yahoo.com") && !baseUrl.equalsIgnoreCase("http://video.search.yahoo.com") && !baseUrl.equalsIgnoreCase("http://images.search.yahoo.com")) {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[id='sp']"));
            if (input != null) input.sendKeys(query);
            WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[type='submit']"));
            if (button != null) button.click();
        } else {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[id='yschsp']"));
            if (input != null) input.sendKeys(query);
            WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[type='submit']"));
            if (button != null) button.click();
        }
        wait(3000 * attempt);
    }

    @Test
    public void test() {
        Logger LOG = LoggerFactory.getLogger(CurrentNdlYahoo.class);
        LOG.info("======CurrentNdlYahoo tests started.");
        checkDomains();
        assertTrue("======CurrentNdlYahoo tests failed. Count of passed = " + (domains.size() - failedDomains.size()) + ". Count of failed = " + failedDomains.size() + ". Count of invalid = " + invalidDomains.size(), (failedDomains.size() == 0) & (invalidDomains.size() == 0));
        LOG.info("======CurrentNdlYahoo tests passed. Checked " + domains.size() + " domains.");
    }
}