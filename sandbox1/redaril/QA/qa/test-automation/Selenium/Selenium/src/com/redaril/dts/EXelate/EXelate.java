package com.redaril.dts.EXelate;

import static org.junit.Assert.fail;

import java.util.Date;

//import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import java.io.*;
import java.net.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.ie.InternetExplorerDriver;

public class EXelate {

    private Selenium browser;
    private WebDriver driver;
    private static final String ENV = "env1";
    //private static final String ENV = "env2";
    private String PARTNERSINFO_URL = "http://" + ENV + ".east.p.raasnet.com:8080/partners/info";
    private String EXMAP_EAST_URL = "http://" + ENV + ".east.p.raasnet.com:8080/partners/exelate";
    private String EXMAP_WEST_URL = "http://" + ENV + ".west.p.raasnet.com:8080/partners/exelate";
    private String EAST_CST_URL = "http://" + ENV + ".east.cst.raasnet.com:8080/cacheservertester/cserver?uid=";
    private String WEST_CST_URL = "http://" + ENV + ".west.cst.raasnet.com:8080/cacheservertester/cserver?uid=";
    private String DTS_HOST = ENV + ".west.dts.raasnet.com";
    private static final int DTS_PORT = 8080;
    private static final int TIME_TO_WAIT = 2000;

    @Before
	public void setUp() throws Exception {
//		browser = new DefaultSelenium("localhost",
//    			4444, "*firefox", "http://ya.ru");
//            browser.start();
    	driver = new InternetExplorerDriver();
    	browser = new WebDriverBackedSelenium(driver, "http://ya.ru");
            browser.setTimeout("60000");
    		browser.setSpeed("750");
    		browser.windowMaximize();
    		browser.windowFocus();
	}

	@After
	public void tearDown() throws Exception {
        //browser.close();
        browser.stop();
	}
	
	@Test
	public void testEXelateMapping() throws Exception {

		// GET RAUID and EXUID for WEST
		
		System.out.println("\r\nDELETE cookies");
		browser.open(PARTNERSINFO_URL);
		browser.deleteAllVisibleCookies();

		Thread.sleep(TIME_TO_WAIT);
		
		System.out.println("\r\nGET WEST EXelate UID");
		browser.open(EXMAP_WEST_URL);
		final String westEXUID = getEXUID();
		System.out.println("WEST EXUID= " + westEXUID);

		Thread.sleep(TIME_TO_WAIT);
		
		System.out.println("\r\nGET WEST RedAril UID");
		final String westRaUID = browser.getCookieByName("u");
		System.out.println("WEST raUID= " + westRaUID);
		
		Thread.sleep(TIME_TO_WAIT);

		// GET RAUID and EXUID for EAST

		System.out.println("\r\nDELETE cookies");
		browser.open(PARTNERSINFO_URL);
		browser.deleteAllVisibleCookies();

		Thread.sleep(TIME_TO_WAIT);

		System.out.println("\r\nGET EAST EXelate UID");
		browser.open(EXMAP_EAST_URL);
		final String eastEXUID = getEXUID();
		System.out.println("EAST EXUID= " + eastEXUID);

		Thread.sleep(TIME_TO_WAIT);
		
		System.out.println("\r\nGET EAST RedAril UID");
		final String eastRaUID = browser.getCookieByName("u");
		System.out.println("EAST raUID= " + eastRaUID);
		
		Thread.sleep(TIME_TO_WAIT);

		System.out.println("\r\nDELETE cookies");
		browser.open(PARTNERSINFO_URL);
		browser.deleteAllVisibleCookies();

		Thread.sleep(TIME_TO_WAIT);

		// CHECK mapping user westRaUID for environment
		
		System.out.println("\r\nCheck mapping for west");
		browser.open(PARTNERSINFO_URL);
		browser.deleteAllVisibleCookies();
		browser.createCookie("u=" + westRaUID, "path=/; domain=.raasnet.com; expires=Tue, 23-Jun-2037 20:06:01 GMT;");
		browser.createCookie("o=0", "path=/; domain=.raasnet.com; expires=Tue, 23-Jun-2037 20:06:01 GMT;");

		browser.open(WEST_CST_URL + westRaUID);
		if (browser.isTextPresent("Mapped with exchanges:") && browser.isTextPresent("EXelate")) {
			System.out.println("west user: " + westRaUID + " mapped on west");
		} else {
			System.out.println("ERROR! west user: " + westRaUID + " not mapped on west");
			fail();
		}
		browser.open(WEST_CST_URL + eastRaUID);
		if (browser.isTextPresent("Mapped with exchanges:") && browser.isTextPresent("EXelate")) {
			System.out.println("ERROR! east user: " + eastRaUID + " mapped on west");
			fail();
		} else {
			System.out.println("east user: " + eastRaUID + " not mapped on west");
		}
		
		// CHECK mapping user eastRaUID for environment

		System.out.println("\r\nCheck mapping for east");
		browser.open(PARTNERSINFO_URL);
		browser.deleteAllVisibleCookies();
		browser.createCookie("u=" + eastRaUID, "path=/; domain=.raasnet.com; expires=Tue, 23-Jun-2037 20:06:01 GMT;");
		browser.createCookie("o=0", "path=/; domain=.raasnet.com; expires=Tue, 23-Jun-2037 20:06:01 GMT;");

		browser.open(EAST_CST_URL + eastRaUID);
		if (browser.isTextPresent("Mapped with exchanges:") && browser.isTextPresent("EXelate")) {
			System.out.println("east user: " + eastRaUID + " mapped on east");
		} else {
			System.out.println("ERROR! east user: " + eastRaUID + " not mapped on east");
			fail();
		}
		browser.open(EAST_CST_URL + westRaUID);
		if (browser.isTextPresent("Mapped with exchanges:") && browser.isTextPresent("EXelate")) {
			System.out.println("ERROR! west user: " + westRaUID + " mapped on east");
			fail();
		} else {
			System.out.println("west user: " + westRaUID + " not mapped on east");
		}
		
		// Send POST
		
		System.out.println("\r\nSEND POST to WEST for user mapped on WEST uid=" + westRaUID);
		browser.open(PARTNERSINFO_URL);
		browser.deleteAllVisibleCookies();
		browser.createCookie("u=" + eastRaUID, "path=/; domain=.raasnet.com; expires=Tue, 23-Jun-2037 20:06:01 GMT;");
		browser.createCookie("o=0", "path=/; domain=.raasnet.com; expires=Tue, 23-Jun-2037 20:06:01 GMT;");
		final String westUserRequest = getPOST(DTS_HOST, DTS_PORT, westEXUID, westRaUID);
		sendPOST(DTS_HOST, DTS_PORT, westUserRequest);

		Thread.sleep(TIME_TO_WAIT);

		System.out.println("\r\nCHECK WEST user model, for user mapped on WEST uid=" + westRaUID);
		System.out.println(WEST_CST_URL + westRaUID);
    	browser.open(WEST_CST_URL + westRaUID);
    	if (browser.isTextPresent("Categories size: 7") &&
    			browser.isTextPresent("Data owner: 4100") &&
    			browser.isTextPresent("Data source: 1003") &&
    			browser.isTextPresent("Category: 10017854") &&
    			browser.isTextPresent("Category: 10017368") &&
    			browser.isTextPresent("Category: 10018101") &&
    			browser.isTextPresent("Category: 10017868") &&
    			browser.isTextPresent("Category: 10017867") &&
    			browser.isTextPresent("Category: 10017378") &&
    			browser.isTextPresent("Category: 10017855")) {
        	System.out.println(browser.getBodyText());
        	System.out.println("PASSED");
        } else {
        	System.out.println(browser.getBodyText());
        	System.out.println("FAILED");
			fail();
        }

		System.out.println("\r\nCHECK EAST user model, for user mapped on WEST uid=" + westRaUID);
		System.out.println(EAST_CST_URL + westRaUID);
    	browser.open(EAST_CST_URL + westRaUID);
    	if (browser.isTextPresent("No cache server mapping in userdata for uid: " + westRaUID)) {
        	System.out.println(browser.getBodyText());
        	System.out.println("PASSED");
        } else {
        	System.out.println(browser.getBodyText());
        	System.out.println("FAILED");
			fail();
        }
    	
    	// Send POST
    	
		System.out.println("\r\nSEND POST to WEST for user mapped on EAST uid=" + eastRaUID);
		browser.open(PARTNERSINFO_URL);
		browser.deleteAllVisibleCookies();
		browser.createCookie("u=" + eastRaUID, "path=/; domain=.raasnet.com; expires=Tue, 23-Jun-2037 20:06:01 GMT;");
		browser.createCookie("o=0", "path=/; domain=.raasnet.com; expires=Tue, 23-Jun-2037 20:06:01 GMT;");
		final String eastUserRequest = getPOST(DTS_HOST, DTS_PORT, eastEXUID, eastRaUID);
		sendPOST(DTS_HOST, DTS_PORT, eastUserRequest);

		Thread.sleep(TIME_TO_WAIT);

		System.out.println("\r\nCHECK EAST user model, for user mapped on EAST uid=" + eastRaUID);
    	System.out.println(EAST_CST_URL + eastRaUID);
    	browser.open(EAST_CST_URL + eastRaUID);
    	if (browser.isTextPresent("Categories size: 7") &&
    			browser.isTextPresent("Data owner: 4100") &&
    			browser.isTextPresent("Data source: 1003") &&
    			browser.isTextPresent("Category: 10017854") &&
    			browser.isTextPresent("Category: 10017368") &&
    			browser.isTextPresent("Category: 10018101") &&
    			browser.isTextPresent("Category: 10017868") &&
    			browser.isTextPresent("Category: 10017867") &&
    			browser.isTextPresent("Category: 10017378") &&
    			browser.isTextPresent("Category: 10017855")) {
        	System.out.println(browser.getBodyText());
        	System.out.println("PASSED");
        } else {
        	System.out.println(browser.getBodyText());
        	System.out.println("FAILED");
			fail();
        }

		System.out.println("\r\nCHECK WEST user model, for user mapped on EAST uid=" + eastRaUID);
		System.out.println(WEST_CST_URL + eastRaUID);
    	browser.open(WEST_CST_URL + eastRaUID);
    	if (browser.isTextPresent("No cache server mapping in userdata for uid: " + eastRaUID)) {
        	System.out.println(browser.getBodyText());
        	System.out.println("PASSED");
        } else {
        	System.out.println(browser.getBodyText());
        	System.out.println("FAILED");
			fail();
        }

    	System.out.println("\r\nFINISH");
	}
	
	private void sendPOST (String host, int port, String request) throws Exception {
		String response = "";
        try {
            Socket socketHttp = new Socket(host, port);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socketHttp.getOutputStream()), true);
            System.out.println("- request ---------------------------------------");
            System.out.print(request);
            System.out.println("- end of request --------------------------------");
            out.print(request);
            out.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(socketHttp.getInputStream()));
            System.out.println("- response --------------------------------------");
            boolean bPrint = true;
            while (!(br.ready()))
                Thread.sleep(1000);
            while ((response = br.readLine()) != null) {
                if ( bPrint ) System.out.println(response); 
                if ( response.compareTo("Connection: close") == 0 ) bPrint = false;
            }
            br.close();
            out.close();
            socketHttp.close();
            System.out.println("- end of response -------------------------------");
        } catch (Exception e) {
            System.err.println("Exception : " + e);
        }
	}

	private String getEXUID () throws Exception {
		String sEXURL = browser.getLocation();
		System.out.println("exURL= " + sEXURL);
		int start = sEXURL.lastIndexOf("=");
		start++;
		int end = sEXURL.length();
		char cBkUID[] = new char[end - start]; 
		sEXURL.getChars(start, end, cBkUID, 0);
		return String.valueOf(cBkUID);
	}
	
	private String getDate () throws Exception {
		return new Date().toString();
	}
	
	private String getPOST (String host, int port, String sEXUID, String sRaUID) throws Exception {
		String sDate = getDate();
		String sPostBody = 
			"{\r\n" +
			"\"pixelCount\": 1,\r\n" +
			"\"pixels\": [\r\n" +
			"{\r\n" +
			"\"xuid\": \"" + sEXUID + "\",\r\n" +
			"\"buid\": \"" + sRaUID + "\",\r\n" +
			"\"segments\": [1314, 5, 134, 390],\r\n" +
			"\"timestamp\": \"" + sDate + "\"\r\n" +
			"}\r\n" +
			"]\r\n" +
			"}\r\n\r\n";
		
		return "POST /dts/exelate HTTP/1.1\r\n" +
		"User-Agent: Fiddler\r\n" +
		"Content-Type: application/JSON\r\n" +
		"Host: " + host + ":" + port + "\r\n" +
		"Content-Length: " + sPostBody.length() + "\r\n\r\n" + sPostBody;
	}
	
	
}
