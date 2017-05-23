package com.redaril.dmptf.tests.testnotready.DMPUI.Smoke.Analytics;

import java.net.URL;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;

public class JavaActiveAudienceSegmentsReport {
  private RemoteWebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  private static Long getCurrentDateLong() {
        Date date = new Date();
        return date.getTime();
  }

  @Before
 public void setUp() throws Exception {
    baseUrl = "http://env1.dmpui/";
    driver = new FirefoxDriver();
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
  }




  @Test
  public void testJavaActiveAudienceSegmentsReport() throws Exception {
    /*driver.get(baseUrl + "login.html");
    driver.findElement(By.id("name")).clear();
    driver.findElement(By.id("name")).sendKeys("psemyonov_pub");
    driver.findElement(By.id("password")).clear();
    driver.findElement(By.id("password")).sendKeys("qwerty");
    driver.findElement(By.id("form-submit-button")).click();*/


    for (int second = 0;; second++) {
    	if (second >= 60) fail("timeout");
    	try { if (driver.findElement(By.linkText("REPORTS")).isDisplayed()) break; } catch (Exception e) {}
    	Thread.sleep(1000);
    }

    driver.findElement(By.linkText("REPORTS")).click();
      for (int second = 0;; second++) {
          if (second >= 60) fail("timeout");
          try { if (driver.findElement(By.linkText("ACTIVE AUDIENCE SEGMENTS")).isDisplayed()) break; } catch (Exception e) {}
          Thread.sleep(1000);
      }

      driver.findElement(By.linkText("ACTIVE AUDIENCE SEGMENTS")).click();
      for (int second = 0; ; second++) {
          if (second >= 60) fail("timeout");
          try {
              if (driver.findElement(By.name("app-id-201-constraint101-inputEl")).isDisplayed()) break;
          } catch (Exception e) {
          }
          Thread.sleep(1000);
      }

//    driver.switchTo().frame("reportsproperties:type:201").findElement(By.name("app-id-201-constraint101-inputEl")).click();
//    driver.findElement(By.cssSelector("iframe[id='app-id-201-constraint102-inputEl']")).click();
    driver.findElement(By.id("app-id-201-dimensions602-inputEl")).click();
    driver.findElement(By.id("app-id-201-dimensions105-inputEl")).click();
    driver.findElement(By.id("app-id-201-metrics103-inputEl")).click();
    driver.findElement(By.id("app-id-201-metrics104-inputEl")).click();
    driver.findElement(By.name("app-id-201-constraint101-inputEl")).click();
    driver.findElement(By.id("app-id-201-constraint101-combo-list-item-all")).click();
    driver.findElement(By.name("app-id-201-constraint101-inputEl")).click();
    driver.findElement(By.id("app-id-201-constraint201-inputEl")).click();
    driver.findElement(By.id("app-id-201-constraint201-combo-list-item-all")).click();
    driver.findElement(By.id("app-id-201-constraint201-inputEl")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [getEval |  | ]]
    driver.findElement(By.name("name")).clear();
    driver.findElement(By.name("name")).sendKeys("Report" + getCurrentDateLong());
    driver.findElement(By.id("app-id-201-email-inputEl")).clear();
    driver.findElement(By.id("app-id-201-email-inputEl")).sendKeys("psemyonov@coreaudience.com");
    driver.findElement(By.xpath("(//a[contains(text(),'Save')])[3]")).click();


      for (int second = 0; ; second++) {
          if (second >= 60) fail("timeout");
          try {
              if (driver.findElement(By.linkText("Delete Report")).isDisplayed()) break;
          } catch (Exception e) {
          }
          Thread.sleep(1000);
      }

    driver.findElement(By.linkText("Delete Report")).click();
    driver.findElement(By.xpath("//*[text()='OK']")).click();
//    driver.switchTo().defaultContent();
//    for (int second = 0;; second++) {
//    	if (second >= 60) fail("timeout");
//    	try { if (driver.findElement(By.linkText("Logout")).isDisplayed()) break; } catch (Exception e) {}
//    	Thread.sleep(1000);
//    }

    driver.findElement(By.linkText("LOGOUT")).click();
  }

/*  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alert.getText();
    } finally {
      acceptNextAlert = true;
    }
  }*/
}
