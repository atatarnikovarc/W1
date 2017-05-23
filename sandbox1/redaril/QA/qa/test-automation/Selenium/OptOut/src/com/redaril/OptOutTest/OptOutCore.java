package com.redaril.OptOutTest;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;

import junit.framework.TestCase;
import com.thoughtworks.selenium.Selenium;

public class OptOutCore extends TestCase {
    private Selenium browser;
    private WebDriver driver;
    private String browserName;
    private String raMainAddr = "http://www.redaril.com";
    private String checkCookiesAddr = "http://p.raasnet.com/partners/info";
    //private static String alertMsgWithoutOptOut = "We have successfully executed your opt-out request andhave placed an opt-out cookie on your computer. The opt-out cookie tells us not to collect your non-personallyidentifiable information for the purpose of tailoring online advertisements or content to create a more relevantInternet experience for you. Once you opt out of our Technology, you will no longer receive advertisements orcontent linked to your interests from Red Aril. Please note that if you delete, block or otherwise restrict cookies,or if you use a different computer or Internet browser, you may need to renew your opt-out choice.";
    private final static String alertMsgWithoutOptOut = "We have successfully executed your opt-out request and have placed an opt-out cookie on your computer. The opt-out cookie tells us not to collect your non-personally identifiable information for the purpose of tailoring online advertisements or content to create a more relevant Internet experience for you. Once you opt out of our Technology, you will no longer receive advertisements or content linked to your interests from Red Aril. Please note that if you delete, block or otherwise restrict cookies, or if you use a different computer or Internet browser, you may need to renew your opt-out choice.";
    private final static String alertMsgWithOptOut = "You have successfully opted-out earlier. Please return to the opt-out page if you delete your cookies.";
    
    public OptOutCore ( String browserNameFromMain,
    		 Selenium browserFromMain ) {
    	browser = browserFromMain;
    	browserName = browserNameFromMain;
    }
    
    public OptOutCore ( String browserNameFromMain,
    		Selenium browserFromMain, WebDriver driverFromMain ) {
    	browser = browserFromMain;
    	driver = driverFromMain;
    	browserName = browserNameFromMain;
    }
    
	public boolean execute() throws Exception {

		////////////////////////////////////////////////////////////////////////
		// PREPARE
    	System.out.println("INFO: prerare to test. Delete .raasnet.com cookies;");
		open(checkCookiesAddr);
    	browser.deleteAllVisibleCookies();
		
		////////////////////////////////////////////////////////////////////////
		// START
    	System.out.println("INFO: open RedAril main page;");
		open(raMainAddr);

		////////////////////////////////////////////////////////////////////////
    	System.out.println("INFO: open RedAril Opt-Out page;");
        browser.click("xpath=/html/body//div[@id='footer']//a[@href='/opt-out']");
        waitForPageLoadingElement("xpath=/html/body//div[@id='content']//a[@href='/opt-out?p=confirm']");

		////////////////////////////////////////////////////////////////////////
		System.out.println("INFO: click Opt-Out button;");
		// stage == 0 when we have no out-out cook
		// stage == 1 when we have out-out cook
		int stage = 0;
		optOut ( stage );
		
		////////////////////////////////////////////////////////////////////////
		System.out.println("INFO: check cookies;");
        open(checkCookiesAddr);
		if ( !checkOutOutCookie("9") ) return false;
        
		////////////////////////////////////////////////////////////////////////
    	System.out.println("INFO: open RedAril main page;");
		open(raMainAddr);

		////////////////////////////////////////////////////////////////////////
    	System.out.println("INFO: open RedAril Opt-Out page;");
        browser.click("xpath=/html/body//div[@id='footer']//a[@href='/opt-out']");
        waitForPageLoadingElement("xpath=/html/body//div[@id='content']//a[@href='/opt-out?p=confirm']");

		////////////////////////////////////////////////////////////////////////
		System.out.println("INFO: click Opt-Out button;");
		stage = 1;
		optOut ( stage );
        
		////////////////////////////////////////////////////////////////////////
		System.out.println("INFO: check cookies;");
        open(checkCookiesAddr);
		if ( !checkOutOutCookie("9") ) return false;
        
		////////////////////////////////////////////////////////////////////////
		System.out.println("INFO: set cookie o=0;");
    	browser.deleteAllVisibleCookies();
		
		////////////////////////////////////////////////////////////////////////
    	System.out.println("INFO: open RedAril Opt-Out page;");
		open(raMainAddr);
		
		////////////////////////////////////////////////////////////////////////
		System.out.println("INFO: check cookies;");
        open(checkCookiesAddr);
		if ( !checkOutOutCookie("0") ) return false;
		if ( !checkUCookie() ) return false;
		//if ( !checkLppCookie() ) return false;
		
		return true;
	}
	
	private boolean checkOutOutCookie (String cookValue) throws Exception {
		try {
			System.out.println(browser.getCookie());
			String currentCookValue = browser.getCookieByName("o");
	    	System.out.println("checkCookie: o= " + currentCookValue + ";");
    		if ( cookValue.compareTo(currentCookValue) != 0 ) {
            	System.out.println("ERROR: wrong \"o\" cook value");
         		return false;
    		}
    		return true;
        } catch (Exception e) {
        	System.out.println(e.getMessage());
     		return false;
        }
	}
	
	private boolean checkUCookie () throws Exception {
		try {
			String currentCookValue = browser.getCookieByName("u");
	    	System.out.println("checkCookie: u= " + currentCookValue + ";");
	    	return true;
        } catch (Exception e) {
        	System.out.println(e.getMessage());
     		return false;
        }
	}
	
	private boolean checkLppCookie () throws Exception {
		try {
			String currentCookValue = browser.getCookieByName("lpp");
	    	System.out.println("checkCookie: lpp= " + currentCookValue + ";");
	    	return true;
        } catch (Exception e) {
        	System.out.println(e.getMessage());
     		return false;
        }
	}

	private void open (String addr) throws Exception {
        try {
        	Thread.sleep(5000);
        	browser.open(addr);
        	Thread.sleep(5000);
        } catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
        	System.exit(1);
        }
	}
	
	private boolean checkAlertMessage ( int state, String msg ) throws Exception {
		if ( state == 0 ) {
			if ( msg.compareTo(alertMsgWithoutOptOut) == 0 ) {
				System.out.println("INFO: alertMessage is correct;");
				System.out.println("DEBUG: alertMessage is: " + msg + ";");
				return true;
			} else {
				System.out.println("ERROR: alertMessage is incorrect;" +
						"\r\n" + "Current alert message: " + msg +
						"\r\n" + "Template alert message: " + alertMsgWithoutOptOut);
				return false;
			}
		} else if ( state == 1 ) {
			if ( msg.compareTo(alertMsgWithOptOut) == 0 ) {
				System.out.println("INFO: alertMessage is correct;");
				System.out.println("DEBUG: alertMessage is: " + msg + ";");
				return true;
			} else {
				System.out.println("ERROR: alertMessage is incorrect;" +
						"\r\n" + "Current alert message: " + msg +
						"\r\n" + "Template alert message: " + alertMsgWithoutOptOut);
				return false;
			}
		} else {
			System.out.println("ERROR: Wrong state value;");
			return false;
		}
	}

	private void optOut ( int stage ) throws Exception {
		if ( browserName == "iexplore" ) {
			checkOptOutIE(stage);
		} else {
			checkOptOut(stage);
		}
	}
	
	private void checkOptOutIE ( int stage ) throws Exception {
		try {
	    	browser.focus("link=Opt Out");
			browser.mouseOver("link=Opt Out");
			browser.click("link=Opt Out");
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
		
		//wait until Alert will appear
//		String alertMsg = "";
//		while ( true ) {
//			try {
//				alertMsg = driver.switchTo().alert().getText();
//				System.out.println("webdriver alertMsg: " + alertMsg);
////				+ "\r\n" + 
////						"browser alertMsg: " + browser.getAlert().toString());
//				break;
//			} catch (Exception e) {
//		    	//System.out.println(e.getMessage());
//		    }
//			Thread.sleep(500);
//		}

		Alert alert = driver.switchTo().alert();
		// Get the text from the alert
		String alertText = alert.getText();
		// Accept the alert
		//alert.accept();
		
		System.out.println("alertText: " + alertText);
		
		if ( !checkAlertMessage (stage, driver.switchTo().alert().getText()) ) {
			System.exit(1);
		}
		driver.switchTo().alert().accept();
	}

	private void checkOptOut ( int stage ) throws Exception {
		try {
	        browser.focus("link=Opt Out");
	        browser.mouseOver("link=Opt Out");
	        browser.click("link=Opt Out");
			long start =  System.currentTimeMillis();
		    while (System.currentTimeMillis() < start  + 60000) {
				try {
					if (browser.isTextPresent("We have successfully executed your opt-out request") == true) {
				        if ( !checkAlertMessage (stage, browser.getAlert().toString()) ) {
				        	System.exit(1);
				        }
						break;
					}
					if (browser.isTextPresent("You have successfully opted-out earlier. Please return to the opt-out page if you delete your cookies") == true) {
				        if ( !checkAlertMessage (stage, browser.getAlert().toString()) ) {
				        	System.exit(1);
				        }
						break;
					}
					if (browser.isTextPresent("When you opt out") == true) {
				        if ( !checkAlertMessage (stage, browser.getAlert().toString()) ) {
				        	System.exit(1);
				        }
						break;
					}
				}
				catch (Exception e) {}
				Thread.sleep(500);
			}
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }
	}
	
	private void waitForPageLoadingElement(String locator) {
		long start =  System.currentTimeMillis();
	    while (System.currentTimeMillis() < start  + 60000) {
	        try { if (browser.isElementPresent(locator)) return; } catch  (Exception e) {}
	        try { Thread.sleep(1000); } catch  (InterruptedException e) { throw new AssertionError(e); }
	    }
	    throw  new AssertionError("timeout");
	}

}
