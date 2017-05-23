package com.redaril.OptOutTest;

import com.thoughtworks.selenium.Selenium;
import junit.framework.TestCase;

public class RedArilOptOutTest extends TestCase {
	Selenium browser;
	OptOutCore optOutCheck;
	
	public void test () throws Exception {
		testOptOut("firefox");
		testOptOut("googlechrome");
		testOptOut("iexplore");
	}
	
	private void testOptOut ( String browserName ) throws Exception {
		System.out.println("\r\n=============================================" +
				"\r\nCHECK OPT-OUT " + browserName.toUpperCase() + " BROWSER\r\n");
		
		browserHandler bh = new Browser().getBrowserHandler(browserName);
		
		if ( browserName == "iexplore" )
			optOutCheck = new OptOutCore(browserName, bh.getBrowser(), bh.getWebDriver());
		else
			optOutCheck = new OptOutCore(browserName, bh.getBrowser());

		try {
			if (!optOutCheck.execute()) {
				System.out.println("ERROR: CHECK OPT-OUT FOR " +
						browserName.toUpperCase() +	" BROWSER: FAILED");
				System.exit(1);
			} else {
				System.out.println("INFO: CHECK OPT-OUT FOR " +
						browserName.toUpperCase() +	" BROWSER: PASSED");
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
		} finally {
			bh.getBrowser().stop();
		}
		Thread.sleep(3000);
	}
}
