package com.redaril.dts.EXelate;

import java.util.Date;
import junit.framework.TestCase;
import com.thoughtworks.selenium.Selenium;
import java.io.*;
import java.net.*;

public class EXelate extends TestCase {

    private Selenium browser;
    private String ENV;
    private String PARTNERSINFO_URL;
    private String EXMAP_EAST_URL;
    private String EXMAP_WEST_URL;
    private String EAST_CST_URL;
    private String WEST_CST_URL;
    private String DTS_HOST;
    private static final int DTS_PORT = 8080;
    private static final int TIME_TO_WAIT = 2000;

    //TODO: check sys log

    public EXelate ( Selenium browserFromMain, String env ) {
    	browser = browserFromMain;
    	ENV = env;
        PARTNERSINFO_URL =	"http://" + ENV + ".east.p.raasnet.com:8080/partners/info";
        EXMAP_EAST_URL =	"http://" + ENV + ".east.p.raasnet.com:8080/partners/exelate";
        EXMAP_WEST_URL =	"http://" + ENV + ".west.p.raasnet.com:8080/partners/exelate";
        EAST_CST_URL =		"http://" + ENV + ".east.cst.raasnet.com:8080/cacheservertester/cserver?uid=";
        WEST_CST_URL =		"http://" + ENV + ".west.cst.raasnet.com:8080/cacheservertester/cserver?uid=";
        DTS_HOST =			ENV + ".west.dts.raasnet.com";
    }
    
    public String getDescription () {
    	return "Test EXelate mapping with RedAril";
    }    
    
	public boolean execute() throws Exception {

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
			return false;
		}
		browser.open(WEST_CST_URL + eastRaUID);
		if (browser.isTextPresent("Mapped with exchanges:") && browser.isTextPresent("EXelate")) {
			System.out.println("ERROR! east user: " + eastRaUID + " mapped on west");
			return false;
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
			return false;
		}
		browser.open(EAST_CST_URL + westRaUID);
		if (browser.isTextPresent("Mapped with exchanges:") && browser.isTextPresent("EXelate")) {
			System.out.println("ERROR! west user: " + westRaUID + " mapped on east");
			return false;
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
			return false;
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
			return false;
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
			return false;
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
			return false;
        }

    	System.out.println("\r\nFINISH");
		return true;
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
