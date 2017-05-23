package com.redaril.optout;

import com.thoughtworks.selenium.BrowserConfigurationOptions;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.DefaultSelenium;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

import java.util.Properties;
import java.io.InputStream;
import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.ie.InternetExplorerDriver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.FieldNamingPolicy;

import com.redaril.junit.Parallelized;
import com.redaril.ondemand.ConnectionParameters;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.ie.InternetExplorerDriver;

@RunWith(Parallelized.class)
public class RedArilOptOutTest {
    private Selenium selenium;
    private String browser;
	private WebDriver driver;
    private String browserVersion;
    private String os;
    public static Properties browserProps = new Properties();
    private Properties parallelProps = new Properties();
    private String json;

    private String raMainAddr = "http://www.redaril.com";
    private String checkCookiesAddr = "http://p.raasnet.com/partners/info";
    private final static String alertMsgWithoutOptOut = "We have successfully executed your opt-out request and have placed an opt-out cookie on your computer. The opt-out cookie tells us not to collect your non-personally identifiable information for the purpose of tailoring online advertisements or content to create a more relevant Internet experience for you. Once you opt out of our Technology, you will no longer receive advertisements or content linked to your interests from Red Aril. Please note that if you delete, block or otherwise restrict cookies, or if you use a different computer or Internet browser, you may need to renew your opt-out choice.";
    private final static String alertMsgWithOptOut = "You have successfully opted-out earlier. Please return to the opt-out page if you delete your cookies.";
    
	public RedArilOptOutTest(String os, String browser, String version) throws Exception {
	        super();
	        this.browser = browser;
	        this.browserVersion = version;
	        this.os = os;
	 
	        InputStream is = this.getClass().getResourceAsStream("/parallel.properties");
	        parallelProps.load(is);
	 
	        if (parallelProps.getProperty("ondemand").equals("true")) {
	          Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();
	          ConnectionParameters od = new ConnectionParameters();
	          od.setBrowser(this.browser);
	          od.browserVersion = this.browserVersion;
	          od.jobName = this.getClass().getName();
	          od.os = this.os;
	          this.json = gson.toJson(od);
	        }
    }
    
	@Parameters
	public static LinkedList browsersStrings() throws Exception {
	      LinkedList browsers = new LinkedList();
	      
	      InputStream is = RedArilOptOutTest.class.getResourceAsStream("/browser.properties");
	      browserProps.load(is);
	 
	      String[] rawBrowserStrings = browserProps.getProperty("browsers").split(",");
	      for (String rawBrowserString : rawBrowserStrings) {
	        if (rawBrowserString.indexOf(";") != -1) {
	          String[] browserParts = rawBrowserString.split(";");
	          browsers.add(new String[] { browserParts[0], browserParts[1], browserParts[2] });
	        } else {
	          browsers.add(new String[] { rawBrowserString, "", "" });
	        }
	      }
	      return browsers;
	}
	
	@Before
    public void setUp() throws Exception {
		if ( this.browser.equals("*iexplore") ) {
			System.out.println("Starting IE");
			driver = new InternetExplorerDriver();
	    	selenium = new WebDriverBackedSelenium(driver, "http://ya.ru");
		} else if ( this.browser.equals("*googlechrome") ) {
			System.out.println("Starting Chrome");
	    	BrowserConfigurationOptions webSec = new BrowserConfigurationOptions();
			selenium = new DefaultSelenium("localhost", 4444, this.browser, "http://ya.ru");
	    	selenium.start(webSec.setCommandLineFlags("--disable-web-security"));
		} else {
			System.out.println("Starting FireFox");
			selenium = new DefaultSelenium("localhost", 4444, this.browser, "http://ya.ru");
	        selenium.start();
		}
//		selenium = new DefaultSelenium("localhost", 4444, this.browser, "http://ya.ru");
//        selenium.start();
        selenium.setTimeout("90000");
        selenium.setSpeed("750");
        selenium.windowMaximize();
        selenium.windowFocus();
	}
	
	@After
    public void tearDown() throws Exception {
        selenium.stop();
    }
	
	@Test
	public void testTitle () throws Exception {
        this.selenium.open("/");
        System.out.println("testTitle: " +
        		System.getProperty("os.name") + "; " +
        		System.getProperty("user.name") + "; " + 
        		this.browser + "; " + 
        		this.browserVersion  + "; " +
        		selenium.getTitle() + ";");
//        Thread.sleep(2000);
//        System.out.println("testTitle: " + this.browser + " " + this.selenium.getTitle());
	}
	
//	@Test
//	public void testEnv () throws Exception {
//        this.selenium.open("/");
//        
//        System.out.println("Cross browser testing: \r\n" +
//        		"browser version is " + this.browserVersion + "\r\n" +
//        		"title is " + this.selenium.getTitle());
//        Thread.sleep(2000);
//        System.out.println("Cross browser testing: \r\n" +
//        		"os is " + this.os + "\r\n" +
//        		"title is " + this.selenium.getTitle());
//	}
	
//	@Test
//	public void testOptOut () throws Exception {
//		////////////////////////////////////////////////////////////////////////
//		// PREPARE
//    	System.out.println("INFO: prerare to test. Delete .raasnet.com cookies;");
//		open(checkCookiesAddr);
//    	selenium.deleteAllVisibleCookies();
//		
//		////////////////////////////////////////////////////////////////////////
//		// START
//    	System.out.println("INFO: open RedAril main page;");
//		open(raMainAddr);
//
//		////////////////////////////////////////////////////////////////////////
//    	System.out.println("INFO: open RedAril Opt-Out page;");
//        selenium.click("xpath=/html/body//div[@id='footer']//a[@href='/opt-out']");
//        waitForPageLoadingElement("xpath=/html/body//div[@id='content']//a[@href='/opt-out?p=confirm']");
//
//		////////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: click Opt-Out button;");
//		// stage == 0 when we have no out-out cook
//		// stage == 1 when we have out-out cook
//		int stage = 0;
//		optOut ( stage );
//		
//		////////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: check cookies;");
//        open(checkCookiesAddr);
//		if ( !checkOutOutCookie("9") ) fail("ERROR: check o cook failed.");
//        
//		////////////////////////////////////////////////////////////////////////
//    	System.out.println("INFO: open RedAril main page;");
//		open(raMainAddr);
//
//		////////////////////////////////////////////////////////////////////////
//    	System.out.println("INFO: open RedAril Opt-Out page;");
//        selenium.click("xpath=/html/body//div[@id='footer']//a[@href='/opt-out']");
//        waitForPageLoadingElement("xpath=/html/body//div[@id='content']//a[@href='/opt-out?p=confirm']");
//
//		////////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: click Opt-Out button;");
//		stage = 1;
//		optOut ( stage );
//        
//		////////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: check cookies;");
//        open(checkCookiesAddr);
//		if ( !checkOutOutCookie("9") ) fail("ERROR: check o cook failed.");
//        
//		////////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: set cookie o=0;");
//    	selenium.deleteAllVisibleCookies();
//		
//		////////////////////////////////////////////////////////////////////////
//    	System.out.println("INFO: open RedAril Opt-Out page;");
//		open(raMainAddr);
//		
//		////////////////////////////////////////////////////////////////////////
//		System.out.println("INFO: check cookies;");
//        open(checkCookiesAddr);
//		if ( !checkOutOutCookie("0") ) fail("ERROR: check o cook failed.");
//		if ( !checkUCookie() ) fail("ERROR: check u cook failed.");
//		//if ( !checkLppCookie() ) return false;	
//		}
//	
	private boolean checkOutOutCookie (String cookValue) throws Exception {
		try {
			System.out.println(selenium.getCookie());
			String currentCookValue = selenium.getCookieByName("o");
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
			String currentCookValue = selenium.getCookieByName("u");
	    	System.out.println("checkCookie: u= " + currentCookValue + ";");
	    	return true;
        } catch (Exception e) {
        	System.out.println(e.getMessage());
     		return false;
        }
	}

	private void open (String addr) throws Exception {
        try {
        	Thread.sleep(5000);
        	selenium.open(addr);
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
			checkOptOut(stage);
	}
	
	private void checkOptOut ( int stage ) throws Exception {
		try {
	        selenium.focus("link=Opt Out");
	        selenium.mouseOver("link=Opt Out");
	        selenium.click("link=Opt Out");
			long start =  System.currentTimeMillis();
		    while (System.currentTimeMillis() < start  + 60000) {
				try {
					if (selenium.isTextPresent("We have successfully executed your opt-out request") == true) {
				        if ( !checkAlertMessage (stage, selenium.getAlert().toString()) ) {
				        	System.exit(1);
				        }
						break;
					}
					if (selenium.isTextPresent("You have successfully opted-out earlier. Please return to the opt-out page if you delete your cookies") == true) {
				        if ( !checkAlertMessage (stage, selenium.getAlert().toString()) ) {
				        	System.exit(1);
				        }
						break;
					}
					if (selenium.isTextPresent("When you opt out") == true) {
				        if ( !checkAlertMessage (stage, selenium.getAlert().toString()) ) {
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
	        try { if (selenium.isElementPresent(locator)) return; } catch  (Exception e) {}
	        try { Thread.sleep(1000); } catch  (InterruptedException e) { throw new AssertionError(e); }
	    }
	    throw  new AssertionError("timeout");
	}
}
