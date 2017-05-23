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
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 23.11.12
 * Time: 16:06
 * To change this template use File | Settings | File Templates.
 */
public class CurrentNdlOthers extends BasicTestGeneralSearchQualifier {
    private static String query;
    private final static String SOURCE_PATH = "data" + File.separator + "selenium" + File.separator;
    private final static String sourceDrivers = SOURCE_PATH + "drivers.txt";

    @Before
    public void setup() {
        logFile = "currentNdlOthers.log";
        String getDomainsSQL = pathToSql + "getDomainsOthers.sql";
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
        if (baseUrl.contains("yandex")) {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='text']"));
            if (input != null) {
                input.sendKeys(query);
                input.submit();
            }
        }
        if (baseUrl.contains("aol.com")) {
            webDriverWrapper.getPage(baseUrl);
            wait(30000);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[id='aol-header-query']"));
            if (input != null) input.sendKeys(query);
            WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[id='aol-header-search-button']"));
            if (button != null) button.click();
            wait(5000);
        }
        if (baseUrl.contains("aol.ca")) {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='q']"));
            if (input != null) input.sendKeys(query);
            WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[type='submit']"));
            if (button != null) button.click();
            wait(5000);
        }
        if (baseUrl.contains("bing")) {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='q']"));
            if (input != null) {
                input.sendKeys(query);
                input.submit();
            }
        }
        if (baseUrl.contains("ezilon")) {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='q']"));
            if (input != null) {
                input.sendKeys(query);
                input.submit();
            }
        }
        if (baseUrl.contains("centrum")) {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='q']"));
            if (input != null) {
                input.sendKeys(query);
                input.submit();
            }
        }
        if (baseUrl.contains("ask")) {
            if (baseUrl.contains("m.customsearch.ask.com") || baseUrl.contains("safesearch") || baseUrl.contains("iwon") || baseUrl.equalsIgnoreCase("http://www.search.ask.com")) {
                webDriverWrapper.getPage(baseUrl);
                WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='q']"));
                if (input != null) input.sendKeys(query);
                WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("button[type='submit']"));
                if (button != null) button.click();
            } else if (baseUrl.contains("www.ask.com")) {
                wait(2000);
                webDriverWrapper.getPage(baseUrl);
                WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='q']"));
                if (input != null) input.sendKeys(query);
                WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("button[type='submit']"));
                if (button != null) button.click();
            } else {
                webDriverWrapper.getPage(baseUrl);
                WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='q']"));
                if (input != null) input.sendKeys(query);
                WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[type='submit']"));
                if (button == null) webDriverWrapper.elementFinder.waitElement(By.cssSelector("button[type='submit']"));
                if (button != null) button.click();
            }
        }
        if (baseUrl.contains("seznam")) {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='q']"));
            if (input != null) input.sendKeys(query);
            WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[type='submit']"));
            if (button != null) button.click();
        }
        if (baseUrl.contains("search-results")) {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='q']"));
            if (input != null) input.sendKeys(query);
            WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[type='submit']"));
            if (button != null) button.click();
        }
        if (baseUrl.contains("5earch")) {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='s']"));
            if (input != null) {
                input.sendKeys(query);
                input.submit();
            }
            wait(3000 * attempt);
        }
        if (baseUrl.contains("msn") || baseUrl.contains("nbc")) {
            if (baseUrl.contains("search.ca.msn.com")) {
                webDriverWrapper.getPage(baseUrl);
                WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='q']"));
                if (input != null) input.sendKeys(query);
                WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[type='image']"));
                if (button != null) button.click();
            } else if (baseUrl.contains("today.msnbc.msn.com")) {
                webDriverWrapper.getPage(baseUrl);
                WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='q']"));
                if (input != null) input.sendKeys(query);
                WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[type='submit']"));
                if (button != null) button.click();
            } else if (baseUrl.contains("now.msn.com")) {
                webDriverWrapper.getPage(baseUrl);
                WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='q']"));
                if (input != null) input.sendKeys(query);
                WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[type='image']"));
                if (button != null) button.click();
                Set<String> set = webDriverWrapper.getCurrentDriver().getWindowHandles();
                String newWindow = (String) set.toArray()[set.toArray().length - 1];
                if (!newWindow.contains("bing") && set.size() > 2)
                    newWindow = (String) set.toArray()[set.toArray().length - 2];
                webDriverWrapper.getCurrentDriver().switchTo().window(newWindow);
                wait(10000);
            } else {
                webDriverWrapper.getPage(baseUrl);
                WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='q']"));
                if (input != null) input.sendKeys(query);
                WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='search']"));
                if (button != null) button.click();
                wait(10000);
            }
        }
        if (baseUrl.contains("avg.com") || baseUrl.contains("foxstart") || baseUrl.contains("incredibar.com")) {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='q']"));
            if (input != null) input.sendKeys(query);
            WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[type='submit']"));
            if (button != null) button.click();
        }
        if (baseUrl.contains("sweetim.com")) {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='q']"));
            if (input != null) input.sendKeys(query);
            WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[id='Submit']"));
            if (button != null) button.click();
        }
        if (baseUrl.contains("toshiba.com")) {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='q']"));
            if (input != null) input.sendKeys(query);
            WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("button[value='submit']"));
            if (button != null) button.click();
        }
        if (baseUrl.contains("szukaj")) {
            webDriverWrapper.getPage(baseUrl);
            WebElement input = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[name='q']"));
            if (input != null) input.sendKeys(query);
            WebElement button = webDriverWrapper.elementFinder.waitElement(By.cssSelector("input[type='submit']"));
            if (button != null) button.click();
        }
        wait(1000);
    }

    @Test
    public void test() {
        Logger LOG = LoggerFactory.getLogger(CurrentNdlOthers.class);
        LOG.info("======CurrentNdlOthers tests started.");
        checkDomains();
        assertTrue("======CurrentNdlOthers tests failed. Count of passed = " + (domains.size() - failedDomains.size()) + ". Count of failed = " + failedDomains.size() + ". Count of invalid = " + invalidDomains.size(), (failedDomains.size() == 0) & (invalidDomains.size() == 0));
        LOG.info("======CurrentNdlOthers tests passed. Checked " + domains.size() + " domains.");
    }
}