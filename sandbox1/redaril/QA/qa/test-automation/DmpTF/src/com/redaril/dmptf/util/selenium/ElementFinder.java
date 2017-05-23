package com.redaril.dmptf.util.selenium;

import org.openqa.selenium.*;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 27.11.12
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
public class ElementFinder {
    private final static int limit = 3;
    //private final static int defaultwait = 500;
    private WebDriver driver;

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public WebElement waitElement(By by) {
        int i = 0;
        while (i < limit) {
            try {
                WebElement element = driver.findElement(by);
                if (element.isDisplayed()) {
                    return element;
                }
            } catch (WebDriverException ignored) {
            }
            i++;
        }
        return null;
    }

    public WebElement waitElement(By by, int wait) {
        int i = 0;
        while (i < limit) {
            try {
                WebElement element = driver.findElement(by);
                if (element.isDisplayed()) {
                    return element;
                }
            } catch (NoSuchElementException ignored) {

            } catch (InvalidElementStateException ignored) {

            } catch (StaleElementReferenceException ignored) {

            }
            i++;
            driver.manage().timeouts().implicitlyWait(wait, TimeUnit.SECONDS);
        }
        return null;
    }


}
