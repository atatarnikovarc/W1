package selenium;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Created by atatarnikov on 4/1/15.
 */
public class EveloTask {
    private WebDriver driver;
    private static String startPage = "http://online.transportmonitoring.ru";

    public EveloTask() {
        super();
    }

    @Before
    public void init() {
        //try catch for : timeout, webdriverexception, etc. - fail test if happen
        driver = new FirefoxDriver();
        driver.navigate().to(startPage);

        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return driver.findElements(By.id("loginpassword-inputEl")).size() != 0;
            }
        };
        Wait<WebDriver> wait = new WebDriverWait(driver, 10);
        try {
            wait.until(expectation);
        } catch (Throwable error) {
            Assert.fail("Timeout waiting for Page Load Request to complete");
        }
    }


    @Test
    public void doEvoTest1() throws Exception {
        try {
            WebElement loginField = driver.findElement(By.id("loginuname-inputEl"));
            WebElement passwdField = driver.findElement(By.id("loginpassword-inputEl"));
            loginField.sendKeys("demo");
            passwdField.sendKeys("demo");

            WebElement loginButton = driver.findElement(By.id("button-1012-btnEl"));
            loginButton.click();

            //temp approach, us need to think of some anchor to wait for...
            Thread.sleep(10000);

            ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    WebElement grid = driver.findElement(By.id("WatchTabGrid-body"));
                    return  grid != null && grid.isDisplayed();
                }
            };

            Wait<WebDriver> wait = new WebDriverWait(driver, 10);
            try {
                wait.until(expectation);
            } catch (Throwable error) {
                Assert.fail("Grid table is not visible in time");
            }

            Assert.assertTrue("No online cars happen",  driver.findElement(By.id("WatchTabGrid-body")).findElements(By.cssSelector("tbody [class^=\"x-grid-row green-ds\"]")).size() != 0);

            WebElement exitButton = driver.findElement(By.id("logoutButton"));
            exitButton.click();

            //Wait approach should be used here
            Thread.sleep(3000);

            WebElement loginForm = driver.findElement(By.id("login-panel"));


            Assert.assertTrue("login form is not visible", loginForm.isDisplayed());
        } catch (WebDriverException e) {  //nosuchelm, elementnotvisible, etc exceptions
            e.printStackTrace();
            Assert.fail("relevant error message");
        }
    }

    @After
    public void done() {
        driver.quit();
    }

    public static void main(String[] args) {
        System.out.println("hi");
    }
}
