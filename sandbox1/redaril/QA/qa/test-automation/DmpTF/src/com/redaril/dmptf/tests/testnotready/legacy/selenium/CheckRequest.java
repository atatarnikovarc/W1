package com.redaril.dmptf.tests.testnotready.legacy.selenium;
//package com.redaril.dmptf.tests.test.legacy.selenium;
//
//import com.redaril.dmptf.tests.test.legacy.selenium.helpers.CheckRequestHelper;
//import com.redaril.dmptf.util.Config;
//
//import org.junit.After;
//import org.junit.Before;
//import com.thoughtworks.selenium.DefaultSelenium;
//import com.thoughtworks.selenium.Selenium;
//import junit.framework.TestCase;
//
//public class CheckRequest extends TestCase {
//	private Selenium browser;
//
//	@Before
//	public void setUp() throws Exception {
//		// 10.50.150.143
//		browser = new DefaultSelenium("localhost", 4444, "*firefox", Config
//				.getInstance().getBaseAddr());
//		browser.start();
//		browser.setTimeout("60000");
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		browser.close();
//		browser.stop();
//	}
//
//	public void test() throws Exception {
//		browser.setSpeed("750");
//		browser.windowMaximize();
//		browser.windowFocus();
//		try {
//			browser.open(Config.getInstance().getBaseAddr());
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//			browser.stop();
//			System.exit(1);
//		}
//
//		CheckRequestHelper.checkRequests(browser);
//	}
//
//}
