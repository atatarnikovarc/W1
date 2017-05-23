package com.redaril.dmptf.tests.testnotready.legacy.selenium;
//package com.redaril.dmptf.tests.test.legacy.selenium;
//
//import org.junit.After;
//import org.junit.Before;
//
//import com.redaril.dmptf.tests.test.legacy.selenium.helpers.MakePixel155Helper;
//import com.redaril.dmptf.util.Config;
//import com.thoughtworks.selenium.DefaultSelenium;
//import com.thoughtworks.selenium.Selenium;
//import junit.framework.TestCase;
//import java.lang.String;
//
//public class MakePixel155 extends TestCase {
//	private Selenium browser;
//	private String addr = Config.getInstance().getAddr();
//
//	@Before
//	public void setUp() throws Exception {
//		// 10.50.150.143
//		browser = new DefaultSelenium("localhost", 4444, "*firefox", addr);
//		browser.start();
//		browser.setTimeout("60000");
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		// browser.close();
//		// browser.stop();
//	}
//
//	public void test() throws Exception {
//		browser.setSpeed("750");
//		browser.windowMaximize();
//		browser.windowFocus();
//		// System.out.println("Hello, World!");
//		try {
//			browser.open(addr);
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//			browser.stop();
//			System.exit(1);
//		}
//
//		// check pixelID.
//		/*
//		 * if file makepixel155_lastPixelID exists, we take pixelID value from
//		 * file. make it +1 and use if file not exists, we create it with value
//		 * (pixelID), and use that value as last pixelID
//		 */
//		int pixelID = MakePixel155Helper.checkLastPixelID(100000);
//		System.out.println("last PpixelID was: " + pixelID);
//		pixelID++;
//		System.out.println("start with PpixelID= " + pixelID + "\r\n");
//
//		// TODO
//		// browser.stop();
//		// System.exit(1);
//
//		// common //loginActionPixel (DATA PIXEL, CAMPAIGN PIXEL), id,
//		// dataPlatform (3),
//		// common //isActive (Y, N), isHttps (Y, N), maxPiggybacks (5),
//		// ruleSelection (Piggyback, Retargeting), dataPixelServerURL,
//		// dataPixel //dataPixelIsDataEnabler, dataPixelIsDataConsumer,
//		// campaignPixel //campaignPixelType (T, L, V, G), campaignPixelFormat
//		// (S, I), campaignPixelNDLNDR (Y, N)
//
//		// //////////////////////////////////////////////////////////////////////////////
//		// / DATA PIXEL
//		// /////////////////////////////////////////////////////////////////
//		// //////////////////////////////////////////////////////////////////////////////
//		if (true) {
//			MakePixel155Helper.makePixel(browser, "DATA PIXEL",
//					String.valueOf(pixelID), "3", "Y", "N", "5", "Piggyback",
//					"http://tsvetaev.narod.ru", "N", "N", "", "", "");
//
//			pixelID++;
//			MakePixel155Helper.makePixel(browser, "DATA PIXEL",
//					String.valueOf(pixelID), "3", "Y", "Y", "5", "Piggyback",
//					"http://tsvetaev.narod.ru", "N", "N", "", "", "");
//
//			pixelID++;
//			MakePixel155Helper.makePixel(browser, "DATA PIXEL",
//					String.valueOf(pixelID), "3", "Y", "N", "5", "Retargeting",
//					"http://tsvetaev.narod.ru", "N", "N", "", "", "");
//
//			pixelID++;
//			MakePixel155Helper.makePixel(browser, "DATA PIXEL",
//					String.valueOf(pixelID), "3", "Y", "Y", "5", "Retargeting",
//					"http://tsvetaev.narod.ru", "N", "N", "", "", "");
//		}
//		// //////////////////////////////////////////////////////////////////////////////
//		// / CAMPAIGN PIXEL
//		// /////////////////////////////////////////////////////////////
//		// //////////////////////////////////////////////////////////////////////////////
//		// char campaignPixelType[] = { 'T', 'L', 'V', 'G' };
//		char campaignPixelType[] = { 'T' };
//		char campaignPixelFormat[] = { 'S', 'I' };
//		char campaignPixelNDLNDR[] = { 'Y', 'N' };
//		char isHttps[] = { 'Y', 'N' };
//
//		for (char chPT : campaignPixelType) {
//			for (char chPF : campaignPixelFormat) {
//				for (char chHttps : isHttps) {
//					for (char chNDLNDR : campaignPixelNDLNDR) {
//						pixelID++;
//						MakePixel155Helper.makePixel(browser, "CAMPAIGN PIXEL",
//								String.valueOf(pixelID), "3", "Y",
//								String.valueOf(chHttps), "5", "Piggyback",
//								"http://tsvetaev.narod.ru", "", "",
//								String.valueOf(chPT), String.valueOf(chPF),
//								String.valueOf(chNDLNDR));
//						pixelID++;
//						MakePixel155Helper.makePixel(browser, "CAMPAIGN PIXEL",
//								String.valueOf(pixelID), "3", "Y",
//								String.valueOf(chHttps), "5", "Retargeting",
//								"http://tsvetaev.narod.ru", "", "",
//								String.valueOf(chPT), String.valueOf(chPF),
//								String.valueOf(chNDLNDR));
//					}
//				}
//			}
//		}
//
//	}
//
//}