package com.redaril.dmptf.tests.testnotready.legacy.selenium.helpers;
//package com.redaril.dmptf.tests.test.legacy.selenium.helpers;
//
//import com.thoughtworks.selenium.Selenium;
//
//public class BaseHelper {
//
//	public static void open(Selenium browser, String addr) throws Exception {
//		try {
//			browser.open(addr);
//			if (browser.isConfirmationPresent()) {
//				System.out.println(browser.getConfirmation());
//			}
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			System.exit(1);
//		}
//	}
//
//	public static void deleteCookies(Selenium browser) throws Exception {
//		// System.out.println("getCookie in deleteCookies: " +
//		// browser.getCookie());
//		try {
//			browser.deleteAllVisibleCookies();
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		try {
//			browser.deleteCookie("o", "/");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		try {
//			browser.deleteCookie("u", "/");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		try {
//			browser.deleteCookie("lpp", "/");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//	}
//}
