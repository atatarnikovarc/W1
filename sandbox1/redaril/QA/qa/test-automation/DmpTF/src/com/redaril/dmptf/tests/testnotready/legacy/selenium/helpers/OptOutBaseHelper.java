package com.redaril.dmptf.tests.testnotready.legacy.selenium.helpers;
//package com.redaril.dmptf.tests.test.legacy.selenium.helpers;
//
//import com.redaril.dmptf.util.Config;
//import com.thoughtworks.selenium.Selenium;
//
//public class OptOutBaseHelper extends BaseHelper {
//	public static void checkCookie(Selenium browser, String cookName,
//			String cookValue) throws Exception {
//		try {
//			// System.out.println("checkCookie: getCookie: " +
//			// browser.getCookie());
//			String currentCookValue = browser.getCookieByName(cookName);
//			System.out.println("checkCookie: " + cookName + "= "
//					+ currentCookValue + ";");
//			if (cookName == "o") {
//				if (cookValue.compareTo(currentCookValue) != 0) {
//					System.out.println("ERROR: wrong \"o\" cook value");
//					System.exit(1);
//				}
//			}
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			System.exit(1);
//		}
//	}
//
//
//
//	public static void deleteCookies(Selenium browser) throws Exception {
//		browser.deleteAllVisibleCookies();
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
//
//	/*
//	 * private void setCookie (String name, String value) throws Exception {
//	 * browser.createCookie(name + "=" + value,
//	 * "Expires=Thu, 01-Jan-2020 00:00:01 GMT; Path=/; Domain=.raasnet.com;"); }
//	 */
//	public static boolean checkAlertMessage(int state, String msg)
//			throws Exception {
//		String alertMsgWithoutOptOut = Config.getInstance()
//				.getAlertMsgWithoutOptOut();
//		String alertMsgWithOptOut = Config.getInstance()
//				.getAlertMsgWithOptOut();
//		if (state == 0) {
//			if (msg.compareTo(alertMsgWithoutOptOut) == 0) {
//				System.out.println("INFO: alertMessage is correct;");
//				System.out.println("DEBUG: alertMessage is: " + msg + ";");
//				return true;
//			} else {
//				System.out.println("ERROR: alertMessage is incorrect;" + "\r\n"
//						+ msg + "\r\n" + alertMsgWithoutOptOut);
//				return false;
//			}
//		} else if (state == 1) {
//			if (msg.compareTo(alertMsgWithOptOut) == 0) {
//				System.out.println("INFO: alertMessage is correct;");
//				System.out.println("DEBUG: alertMessage is: " + msg + ";");
//				return true;
//			} else {
//				System.out.println("ERROR: alertMessage is incorrect;" + "\r\n"
//						+ msg + "\r\n" + alertMsgWithOptOut);
//				return false;
//			}
//		} else {
//			System.out.println("ERROR: Wrong state value;");
//			return false;
//		}
//	}
//
//	public static void optOut(Selenium browser, int stage) throws Exception {
//		try {
//			browser.focus("link=Opt Out");
//			browser.mouseOver("link=Opt Out");
//			browser.click("link=Opt Out");
//			long start = System.currentTimeMillis();
//			while (System.currentTimeMillis() < start + 60000) {
//				try {
//					if (browser
//							.isTextPresent("We have successfully executed your opt-out request") == true) {
//						// System.out.println("We have successfully");
//						if (!checkAlertMessage(stage, browser.getAlert()
//								.toString())) {
//							System.exit(1);
//						}
//						break;
//					}
//					if (browser
//							.isTextPresent("You have successfully opted-out earlier. Please return to the opt-out page if you delete your cookies") == true) {
//						// System.out.println("You have successfully");
//						if (!checkAlertMessage(stage, browser.getAlert()
//								.toString())) {
//							System.exit(1);
//						}
//						break;
//					}
//					if (browser.isTextPresent("When you opt out") == true) {
//						// System.out.println("When you opt out");
//						if (!checkAlertMessage(stage, browser.getAlert()
//								.toString())) {
//							System.exit(1);
//						}
//						break;
//					}
//				} catch (Exception e) {
//				}
//				Thread.sleep(500);
//			}
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//	}
//
//	public static void waitForPageLoadingElement(Selenium browser,
//			String locator) {
//		long start = System.currentTimeMillis();
//		while (System.currentTimeMillis() < start + 60000) {
//			try {
//				if (browser.isElementPresent(locator))
//					return;
//			} catch (Exception e) {
//			}
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				throw new AssertionError(e);
//			}
//		}
//		throw new AssertionError("timeout");
//	}
//}
