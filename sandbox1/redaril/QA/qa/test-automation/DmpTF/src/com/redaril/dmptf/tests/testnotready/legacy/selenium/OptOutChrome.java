package com.redaril.dmptf.tests.testnotready.legacy.selenium;
//package com.redaril.dmptf.tests.test.legacy.selenium;
//
//import org.junit.After;
//import org.junit.Before;
//
//import com.redaril.dmptf.util.Config;
//import com.thoughtworks.selenium.DefaultSelenium;
//import com.thoughtworks.selenium.BrowserConfigurationOptions;
//
//public class OptOutChrome extends OptOutBaseTest {
//
//	@Before
//	public void setUp() throws Exception {
//		// 10.50.150.143
//		BrowserConfigurationOptions webSec = new BrowserConfigurationOptions();
//		browser = new DefaultSelenium("localhost", 4444, "*googlechrome /usr/lib/chromium-browser/chromium-browser",
//				Config.getInstance().getBaseAddr());
//		browser.start(webSec.setCommandLineFlags("--disable-web-security"));
//		browser.setTimeout("60000");
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		browser.close();
//		browser.stop();
//	}
//
//
//}
