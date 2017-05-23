package com.redaril.dmptf.tests.testnotready.legacy.selenium.helpers;
//package com.redaril.dmptf.tests.test.legacy.selenium.helpers;
//
//import org.openqa.selenium.WebDriver;
//import com.thoughtworks.selenium.Selenium;
//
//public class OptOutIEHelper extends OptOutBaseHelper {
//
//	public static void optOut(Selenium browser, WebDriver driver, int stage)
//			throws Exception {
//		try {
//			// browser.focus("link=Opt Out");
//			// browser.mouseOver("link=Opt Out");
//			browser.click("link=Opt Out");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//
//		// wait until Alert will apear
//		while (true) {
//			try {
//				driver.switchTo().alert().getText();
//				break;
//			} catch (Exception e) {
//				// System.out.println(e.getMessage());
//			}
//			Thread.sleep(500);
//		}
//
//		if (!checkAlertMessage(stage, driver.switchTo().alert().getText())) {
//			System.exit(1);
//		}
//		driver.switchTo().alert().accept();
//	}
//
//
//}
