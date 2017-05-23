package com.redaril.dmptf.tests.testnotready.legacy.selenium;
//package com.redaril.dmptf.tests.test.legacy.selenium;
//
//import junit.framework.TestCase;
//
//import org.junit.After;
//import org.junit.Before;
//
//import com.redaril.dmptf.tests.test.legacy.selenium.helpers.OptOutIEHelper;
//import com.redaril.dmptf.util.Config;
//import com.thoughtworks.selenium.Selenium;
//import org.openqa.selenium.*;
//import org.openqa.selenium.ie.InternetExplorerDriver;
//
//public class OptOutIE extends TestCase {
//	private Selenium browser;
//	private WebDriver driver;
//
//	@Before
//	public void setUp() throws Exception {
//		// 10.50.150.143
//		// BrowserConfigurationOptions webSec = new
//		// BrowserConfigurationOptions();
//		driver = new InternetExplorerDriver();
//		browser = new WebDriverBackedSelenium(driver, Config.getInstance()
//				.getBaseAddr());
//
//		// browser2 = new DefaultSelenium("localhost",
//		// 4444, "*iexplore", baseAddr);
//		// browser.start(webSec.setCommandLineFlags("--disable-web-security"));
//		// browser2.start();
//		// browser.setTimeout("60000");
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		// browser.close();
//		browser.stop();
//	}
//
//	public void test() throws Exception {
//		// browser.setSpeed("750");
//		// browser.windowMaximize();
//		// browser.windowFocus();
//
//		// //////////////////////////////////////////////////////////////////////
//		// PREPARE
//		System.out
//				.println("INFO: prerare to test. Delete .raasnet.com cookies;");
//		OptOutIEHelper
//				.open(browser, Config.getInstance().getCheckCookiesAddr());
//		OptOutIEHelper.deleteCookies(browser);
//
//		// //////////////////////////////////////////////////////////////////////
//		// START
//		System.out.println("INFO: open RedAril main page;");
//		OptOutIEHelper.open(browser, Config.getInstance().getRaMainAddr());
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: open RedAril Opt-Out page;");
//		// browser.click("xpath=/html/body//div[@id='footer']//a[@href= '/opt-out']");
//		// waitForPageLoadingElement("xpath=/html/body//div[@id='content']//a[@href='/opt-out?p=confirm']");
//		browser.click("link=Opt Out");
//		OptOutIEHelper.waitForPageLoadingElement(browser, "link=Opt Out");
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: click Opt-Out button;");
//		// stage == 0 when we have no out-out cook
//		// stage == 1 when we have out-out cook
//		int stage = 0;
//		OptOutIEHelper.optOut(browser, driver, stage);
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: check cookies;");
//		OptOutIEHelper
//				.open(browser, Config.getInstance().getCheckCookiesAddr());
//		OptOutIEHelper.checkCookie(browser, "o", "9");
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: open RedAril main page;");
//		OptOutIEHelper.open(browser, Config.getInstance().getRaMainAddr());
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: open RedAril Opt-Out page;");
//		// browser.click("xpath=/html/body//div[@id='footer']//a[@href= '/opt-out']");
//		// waitForPageLoadingElement("xpath=/html/body//div[@id='content']//a[@href='/opt-out?p=confirm']");
//		browser.click("link=Opt Out");
//		OptOutIEHelper.waitForPageLoadingElement(browser, "link=Opt Out");
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: click Opt-Out button;");
//		stage = 1;
//		OptOutIEHelper.optOut(browser, driver, stage);
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: check cookies;");
//		OptOutIEHelper
//				.open(browser, Config.getInstance().getCheckCookiesAddr());
//		OptOutIEHelper.checkCookie(browser, "o", "9");
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: dekete cookie;");
//		// setCookie("o", "0");
//		// Thread.sleep(1000);
//		// checkCookie("o", "0");
//		OptOutIEHelper
//				.open(browser, Config.getInstance().getCheckCookiesAddr());
//		OptOutIEHelper.deleteCookies(browser);
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: open RedAril Opt-Out page;");
//		OptOutIEHelper.open(browser, Config.getInstance().getRaMainAddr());
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: check cookies;");
//		OptOutIEHelper
//				.open(browser, Config.getInstance().getCheckCookiesAddr());
//		OptOutIEHelper.checkCookie(browser, "o", "0");
//		OptOutIEHelper.checkCookie(browser, "u", "0");
//		OptOutIEHelper.checkCookie(browser, "lpp", "0");
//		Thread.sleep(1000);
//	}
//
//}
