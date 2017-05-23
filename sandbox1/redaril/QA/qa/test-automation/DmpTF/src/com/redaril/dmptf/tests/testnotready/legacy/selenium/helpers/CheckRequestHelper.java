package com.redaril.dmptf.tests.testnotready.legacy.selenium.helpers;
//package com.redaril.dmptf.tests.test.legacy.selenium.helpers;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//
//import com.redaril.dmptf.util.Config;
//import com.thoughtworks.selenium.Selenium;
//
//public class CheckRequestHelper extends BaseHelper {
//	private static String htmlFolder = Config.getInstance().getHtmlFolder();
//
//	public static void checkRequests(Selenium browser) throws Exception {
//
//		File dir = new File(htmlFolder);
//		String[] list = dir.list();
//		if (list.length == 0)
//			return;
//		File file;
//		for (int i = 0; i < list.length; i++) {
//			file = new File(htmlFolder + list[i]);
//			// String addr = htmlFolder + "CAMPAIGN_100055_something.html";
//			// System.out.println("addr= " + addr);
//			// System.out.println("path= " + file.getPath());
//			// System.out.println("name= " + file.getName());
//
//			String requestAddrPrefix = "";
//			if (isHttps(file.getName())) {
//				// System.out.println("HTTPS");
//				requestAddrPrefix = Config.getInstance()
//						.getRequestAddrPrefixHttps();
//			} else {
//				// System.out.println("HTTP");
//				requestAddrPrefix = Config.getInstance()
//						.getRequestAddrPrefixHttp();
//			}
//
//			System.out.println("\r\ndelete cookies");
//			open(browser, Config.getInstance().getCheckCookiesAddr());
//			System.out.println("before delete, getCookie: "
//					+ browser.getCookie());
//			deleteCookies(browser);
//			System.out.println("after delete, getCookie: "
//					+ browser.getCookie());
//
//			System.out.println("open: " + requestAddrPrefix + file.getName());
//			try {
//				open(browser, requestAddrPrefix + file.getName());
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//			}
//
//			String title = browser.getTitle();
//			open(browser, Config.getInstance().getCheckCookiesAddr());
//			System.out.println(title);
//			// System.out.println(browser.getCookie());
//			checkOutOutCookie(browser, "0");
//			checkUCookie(browser);
//			checkLppCookie(browser, title);
//			// }
//		} // for
//	}
//
//	public static void checkOutOutCookie(Selenium browser, String cookValue)
//			throws Exception {
//		try {
//			String currentCookValue = browser.getCookieByName("o");
//			System.out.println("checkCookie: o= " + currentCookValue + ";");
//			if (cookValue.compareTo(currentCookValue) != 0) {
//				System.out.println("ERROR: wrong \"o\" cook value");
//				System.exit(1);
//			}
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			System.exit(1);
//		}
//	}
//
//	public static void checkUCookie(Selenium browser) throws Exception {
//		int i = 0;
//		while (true) {
//			try {
//				String currentCookValue = browser.getCookieByName("u");
//				System.out.println("checkCookie: u= " + currentCookValue + ";");
//				break;
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//				// System.exit(1);
//			}
//			Thread.sleep(1000);
//			i++;
//			if (i >= 100) {
//				System.out.println("Cookie timeout");
//				System.exit(1);
//			}
//		}
//	}
//
//	public static void checkLppCookie(Selenium browser, String title)
//			throws Exception {
//		if (title.compareTo("Piggyback") == 0) {
//			try {
//				String currentCookValue = browser.getCookieByName("lpp");
//				System.out.println("checkCookie: lpp= " + currentCookValue
//						+ ";");
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//				System.exit(1);
//			}
//		} else if (title.compareTo("Retargeting") == 0) {
//			try {
//				String currentCookValue = browser.getCookieByName("lpp");
//				System.out.println("checkCookie: lpp= " + currentCookValue
//						+ ";");
//				System.exit(1);
//			} catch (Exception e) {
//				System.out.println("checkCookie: lpp= cookie not fould;");
//			}
//		} else {
//			System.out
//					.println("ERROR. wrong <title> on page. sommething wrong. "
//							+ title);
//			System.exit(1);
//		}
//	}
//
//	public static boolean isHttps(String htmlFileName) throws Exception {
//		String line = "";
//		File f1 = new File(htmlFolder + htmlFileName);
//		if (f1.exists()) {
//			BufferedReader bufferedReader = null;
//			bufferedReader = new BufferedReader(new FileReader(f1.getPath()));
//			while ((line = bufferedReader.readLine()) != null) {
//				if (line.contains("https")) {
//					return true;
//				}
//			}
//
//		}
//
//		return false;
//
//	}
//
//}
