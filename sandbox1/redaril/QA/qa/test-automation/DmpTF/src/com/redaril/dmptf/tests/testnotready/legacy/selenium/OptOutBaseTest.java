package com.redaril.dmptf.tests.testnotready.legacy.selenium;
//package com.redaril.dmptf.tests.test.legacy.selenium;
//
//import com.redaril.dmptf.tests.test.legacy.selenium.helpers.OptOutBaseHelper;
//import com.redaril.dmptf.util.Config;
//import com.thoughtworks.selenium.Selenium;
//
//import junit.framework.TestCase;
//
//public class OptOutBaseTest extends TestCase {
//	protected Selenium browser;
//
//	public void test() throws Exception {
//		browser.setSpeed("750");
//		browser.windowMaximize();
//		browser.windowFocus();
//
//		// //////////////////////////////////////////////////////////////////////
//		// PREPARE
//		System.out
//				.println("INFO: prerare to test. Delete .raasnet.com cookies;");
//		OptOutBaseHelper.open(browser, Config.getInstance()
//				.getCheckCookiesAddr());
//		OptOutBaseHelper.deleteCookies(browser);
//
//		// //////////////////////////////////////////////////////////////////////
//		// START
//		System.out.println("INFO: open RedAril main page;");
//		OptOutBaseHelper.open(browser, Config.getInstance().getRaMainAddr());
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: open RedAril Opt-Out page;");
//		browser.click("xpath=/html/body//div[@id='footer']//a[@href= '/opt-out']");
//		OptOutBaseHelper
//				.waitForPageLoadingElement(browser,
//						"xpath=/html/body//div[@id='content']//a[@href='/opt-out?p=confirm']");
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: click Opt-Out button;");
//		// stage == 0 when we have no out-out cook
//		// stage == 1 when we have out-out cook
//		int stage = 0;
//		OptOutBaseHelper.optOut(browser, stage);
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: check cookies;");
//		OptOutBaseHelper.open(browser, Config.getInstance()
//				.getCheckCookiesAddr());
//		OptOutBaseHelper.checkCookie(browser, "o", "9");
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: open RedAril main page;");
//		OptOutBaseHelper.open(browser, Config.getInstance().getRaMainAddr());
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: open RedAril Opt-Out page;");
//		browser.click("xpath=/html/body//div[@id='footer']//a[@href= '/opt-out']");
//		OptOutBaseHelper
//				.waitForPageLoadingElement(browser,
//						"xpath=/html/body//div[@id='content']//a[@href='/opt-out?p=confirm']");
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: click Opt-Out button;");
//		stage = 1;
//		OptOutBaseHelper.optOut(browser, stage);
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: check cookies;");
//		OptOutBaseHelper.open(browser, Config.getInstance()
//				.getCheckCookiesAddr());
//		OptOutBaseHelper.checkCookie(browser, "o", "9");
//
//		// //////////////////////////////////////////////////////////////////////
//		// System.out.println("INFO: set cookie o=0;");
//		OptOutBaseHelper.deleteCookies(browser);
//		// setCookie("o", "0");
//		// checkCookie("o", "0");
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: open RedAril Opt-Out page;");
//		OptOutBaseHelper.open(browser, Config.getInstance().getRaMainAddr());
//
//		// //////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: check cookies;");
//		OptOutBaseHelper.open(browser, Config.getInstance()
//				.getCheckCookiesAddr());
//		OptOutBaseHelper.checkCookie(browser, "o", "0");
//		OptOutBaseHelper.checkCookie(browser, "u", "0");
//		OptOutBaseHelper.checkCookie(browser, "lpp", "0");
//	}
//
//}
