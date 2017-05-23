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
public class CurrentNdlGoogle extends BasicTestGeneralSearchQualifier {
    private static String query;
    private HashMap<String, String> driverInfo = new HashMap<String, String>();
    private final static String SOURCE_PATH = "data" + File.separator + "selenium" + File.separator;
    private final static String sourceDrivers = SOURCE_PATH + "drivers.txt";

    @Before
    public void setup() {
        logFile = "currentNdlGoogle.log";
        String getDomainsSQL = pathToSql + "getDomainsGoogle.sql";
        baseSetup();
        getOneGeneralSearchQualifiers();
        GeneralSearchQualifier qualifier = qualifiers.get(0);
        query = getQueryByRegex(qualifier.getRegex(), qualifier.isStemmed());
        getDomainsByScript(getDomainsSQL);
        List<String> ipList = FileHelper.getInstance().getDataFromFile(sourceDrivers);
        String[] params;
        String aipList = ipList.get(0);
        params = aipList.split(";");
        for (int j = 0; j < columnNames.size(); j++) {
            driverInfo.put(columnNames.get(j), params[j]);
        }
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
        if (baseUrl.contains("google.com.tw") || baseUrl.contains("google.isearchinfo.com") || baseUrl.contains("google.com.hk")) {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[id='lst-ib']"));
            if (input != null) input.sendKeys(query);
            input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("div[id='hplogo']"));
            if (input != null) input.click();
            WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='btnK']"));
            if (button != null) button.click();
        } else if (baseUrl.contains("google.im") || baseUrl.contains("google.com.cu")) {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[id='lst-ib']"));
            if (input != null) input.sendKeys(query);
            WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("button[class='lsb']"));
            if (button != null) button.click();
        } else if (baseUrl.contains("google.com.sa")) {
            webDriverWrapper.getPage("http://google.com.sa/#hl=ru&gs_nf=3&cp=4&gs_id=e&xhr=t&q=1+speaker+1&pf=p&tbo=d&output=search&sclient=psy-ab&oq=cars&gs_l=&pbx=1&bav=on.2,or.r_gc.r_pw.r_qf.&fp=ccfab4e7ac6c8eb&bpcl=38625945&biw=1366&bih=677");
        } else {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[id='gbqfq']"));
            if (input != null) input.sendKeys(query);
            WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("button[id='gbqfb']"));
            if (button != null) button.click();
        }
        wait(3000 * attempt);
    }

    @Test
    public void test() {
        Logger LOG = LoggerFactory.getLogger(CurrentNdlGoogle.class);
        LOG.info("======CurrentNdlGoogle tests started.");
        checkDomains();
        assertTrue("======CurrentNdlGoogle tests failed. Count of passed = " + (domains.size() - failedDomains.size()) + ". Count of failed = " + failedDomains.size() + ". Count of invalid = " + invalidDomains.size(), (failedDomains.size() == 0) & (invalidDomains.size() == 0));
        LOG.info("======CurrentNdlGoogle tests passed. Checked " + domains.size() + " domains.");
    }
}