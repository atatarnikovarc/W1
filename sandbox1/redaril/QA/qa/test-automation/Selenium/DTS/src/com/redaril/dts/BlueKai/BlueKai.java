package com.redaril.dts.BlueKai;

import java.util.Date;
import junit.framework.TestCase;
import com.thoughtworks.selenium.Selenium;
import java.io.*;
import java.net.*;

public class BlueKai extends TestCase {

    private Selenium browser;
    private String ENV;
    private String PARTNERSINFO_URL;
    private String BKMAP_EAST_URL;
    private String BKMAP_WEST_URL;
    private String EAST_CST_URL;
    private String WEST_CST_URL;
    private String DTS_HOST;
    private static final int DTS_PORT = 8080;
    private static final int TIME_TO_WAIT = 2000;

    //TODO: check sys log
    
    public BlueKai ( Selenium browserFromMain, String env ) {
    	browser = browserFromMain;
    	ENV = env;
        PARTNERSINFO_URL =	"http://" + ENV + ".east.p.raasnet.com:8080/partners/info";
        BKMAP_EAST_URL =	"http://" + ENV + ".east.p.raasnet.com:8080/partners/bkmap";
        BKMAP_WEST_URL =	"http://" + ENV + ".west.p.raasnet.com:8080/partners/bkmap";
        EAST_CST_URL =		"http://" + ENV + ".east.cst.raasnet.com:8080/cacheservertester/cserver?uid=";
        WEST_CST_URL =		"http://" + ENV + ".west.cst.raasnet.com:8080/cacheservertester/cserver?uid=";
        DTS_HOST =			ENV + ".west.dts.raasnet.com";
    }
    
    public String getDescription () {
    	return "Test BlueKai mapping with RedAril";
    }
    
	public boolean execute() throws Exception {

		// GET RAUID and BKUID for WEST
		
		System.out.println("\r\nDELETE cookies");
		browser.open(PARTNERSINFO_URL);
		browser.deleteAllVisibleCookies();

		Thread.sleep(TIME_TO_WAIT);
		
		System.out.println("\r\nGET WEST BlueKai UID");
		browser.open(BKMAP_WEST_URL);
		final String westBkUID = getBkUID();
		System.out.println("WEST bkUID= " + westBkUID);

		Thread.sleep(TIME_TO_WAIT);
		
		System.out.println("\r\nGET WEST RedAril UID");
		final String westRaUID = browser.getCookieByName("u");
		System.out.println("WEST raUID= " + westRaUID);

		Thread.sleep(TIME_TO_WAIT);

		// GET RAUID and BKUID for EAST

		System.out.println("\r\nDELETE cookies");
		browser.open(PARTNERSINFO_URL);
		browser.deleteAllVisibleCookies();

		Thread.sleep(TIME_TO_WAIT);

		System.out.println("\r\nGET EAST BlueKai UID");
		browser.open(BKMAP_EAST_URL);
		final String eastBkUID = getBkUID();
		System.out.println("EAST bkUID= " + eastBkUID);

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
		if (browser.isTextPresent("Mapped with exchanges:") && browser.isTextPresent("BlueKai")) {
			System.out.println("west user: " + westRaUID + " mapped on west");
		} else {
			System.out.println("ERROR! west user: " + westRaUID + " not mapped on west");
			return false;
		}
		browser.open(WEST_CST_URL + eastRaUID);
		if (browser.isTextPresent("Mapped with exchanges:") && browser.isTextPresent("BlueKai")) {
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
		if (browser.isTextPresent("Mapped with exchanges:") && browser.isTextPresent("BlueKai")) {
			System.out.println("east user: " + eastRaUID + " mapped on east");
		} else {
			System.out.println("ERROR! east user: " + eastRaUID + " not mapped on east");
			return false;
		}
		browser.open(EAST_CST_URL + westRaUID);
		if (browser.isTextPresent("Mapped with exchanges:") && browser.isTextPresent("BlueKai")) {
			System.out.println("ERROR! west user: " + westRaUID + " mapped on east");
			return false;
		} else {
			System.out.println("west user: " + westRaUID + " not mapped on east");
		}
		
		// Send POST
		
		System.out.println("\r\nSEND POST to WEST for user mapped on WEST uid=" + westRaUID);
		browser.open(PARTNERSINFO_URL);
		browser.deleteAllVisibleCookies();
		browser.createCookie("u=" + westRaUID, "path=/; domain=.raasnet.com; expires=Tue, 23-Jun-2037 20:06:01 GMT;");
		browser.createCookie("o=0", "path=/; domain=.raasnet.com; expires=Tue, 23-Jun-2037 20:06:01 GMT;");
		final String westUserRequest = getPOST(DTS_HOST, DTS_PORT, westBkUID, westRaUID);
		sendPOST(DTS_HOST, DTS_PORT, westUserRequest);

		Thread.sleep(TIME_TO_WAIT);

		System.out.println("\r\nCHECK WEST user model, for user mapped on WEST uid=" + westRaUID);
		System.out.println(WEST_CST_URL + westRaUID);
    	browser.open(WEST_CST_URL + westRaUID);
    	if (browser.isTextPresent("Categories size: 7") &&
    			browser.isTextPresent("Data owner: 4100") &&
    			browser.isTextPresent("Data source: 1002") &&
    			browser.isTextPresent("Category: 10003913") &&
    			browser.isTextPresent("Category: 10004117") &&
    			browser.isTextPresent("Category: 10015085") &&
    			browser.isTextPresent("Category: 10015530") &&
    			browser.isTextPresent("Category: 10003569") &&
    			browser.isTextPresent("Category: 10004115") &&
    			browser.isTextPresent("Category: 10003570")) {
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
		final String eastUserRequest = getPOST(DTS_HOST, DTS_PORT, eastBkUID, eastRaUID);
		sendPOST(DTS_HOST, DTS_PORT, eastUserRequest);

		Thread.sleep(TIME_TO_WAIT);

		System.out.println("\r\nCHECK EAST user model, for user mapped on EAST uid=" + eastRaUID);
    	System.out.println(EAST_CST_URL + eastRaUID);
    	browser.open(EAST_CST_URL + eastRaUID);
    	if (browser.isTextPresent("Categories size: 7") &&
    			browser.isTextPresent("Data owner: 4100") &&
    			browser.isTextPresent("Data source: 1002") &&
    			browser.isTextPresent("Category: 10003913") &&
    			browser.isTextPresent("Category: 10004117") &&
    			browser.isTextPresent("Category: 10015085") &&
    			browser.isTextPresent("Category: 10015530") &&
    			browser.isTextPresent("Category: 10003569") &&
    			browser.isTextPresent("Category: 10004115") &&
    			browser.isTextPresent("Category: 10003570")) {
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

	private String getBkUID () throws Exception {
		String sBkURL = browser.getLocation();
		System.out.println("bkURL= " + sBkURL);
		int start = sBkURL.lastIndexOf("=");
		start++;
		int end = sBkURL.length();
		char cBkUID[] = new char[end - start]; 
		sBkURL.getChars(start, end, cBkUID, 0);
		return String.valueOf(cBkUID);
	}
	
	private String getDate () throws Exception {
		return new Date().toString();
	}
	
	private String getPOST (String host, int port, String sBkUID, String sRaUID) throws Exception {
		String sDate = getDate();
		String sPostBody = 
			"{\"DeliveryTime\": \""+ sDate +"\",\r\n" +
			"\"DestinationId\": 1,\r\n" +
			"\"PixelCount\": 1,\r\n" +
			"\"Pixels\": [ {\r\n" +
			"\"BkUuid\": \"" + sBkUID + "\",\r\n" +
			"\"CampaignId\": 1234,\r\n" +
			"\"CategoryId\": \"9999,02134,116666,5353\",\r\n" +
			"\"PartnerUuid\": \"" + sRaUID + "\",\r\n" +
			"\"PixelId\": 9151,\r\n" +
			"\"PixelUrl\": \"http://www.sample-adnet.com/pixel.gif?cats=1573\",\r\n" +
			"\"Rank\": 4,\r\n" +
			"\"Timestamp\": \"" + sDate + "\"\r\n" +
			"}  ] }\r\n\r\n";
		
		return "POST /dts/bluekai HTTP/1.1\r\n" +
		"User-Agent: Fiddler\r\n" +
		"Content-Type: application/JSON\r\n" +
		"Host: " + host + ":" + port + "\r\n" +
		"Content-Length: " + sPostBody.length() + "\r\n\r\n" + sPostBody;
	}
	
	
}
