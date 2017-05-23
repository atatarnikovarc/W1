package ru.narod.tsvetaev;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
//import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import junit.framework.TestCase;

public class CheckRequest extends TestCase {
    private Selenium browser;
    private String baseAddr = "http://ya.ru";
    private String checkCookiesAddr = "http://p.raasnet.com/partners/info";
    private String requestAddrPrefixHttp = "http://qa.mydomain.com/htmlTemplates/";
    private String requestAddrPrefixHttps = "https://qa.mydomain.com/htmlTemplates/";
    //private static String htmlFolder = "file:///D:/WorkSpace/eclipse_workspace/makePixel155/pixels/html/";
    private static String htmlFolder = "C:\\Program Files\\Apache Software Foundation\\Apache2.2\\htdocs\\htmlTemplates\\";
	@Before
	public void setUp() throws Exception {
        //10.50.150.143
		browser = new DefaultSelenium("localhost",
    			4444, "*firefox", baseAddr);
            browser.start();
            browser.setTimeout("60000");
	}

	@After
	public void tearDown() throws Exception {
        browser.close();
        browser.stop();
	}

	public void test() throws Exception {
		browser.setSpeed("750");
		browser.windowMaximize();
		browser.windowFocus();
		try {
			browser.open(baseAddr);
        } catch (Exception e) {
            e.printStackTrace();
	    	System.out.println(e.getMessage());
    		browser.stop();
     		System.exit(1);
        }
        
        checkRequests ();
	}

	private void checkRequests () throws Exception {

			File dir = new File(htmlFolder);
			String[] list = dir.list();
		    if (list.length == 0) return;
		    File file;
		    for (int i = 0; i < list.length; i++) {
		    	file = new File(htmlFolder + list[i]);
				//String addr = htmlFolder + "CAMPAIGN_100055_something.html";
		    	//System.out.println("addr= " + addr);
		    	//System.out.println("path= " + file.getPath());
		    	//System.out.println("name= " + file.getName());
		    	
				String requestAddrPrefix = "";
				if ( isHttps(file.getName()) ) {
					//System.out.println("HTTPS");
					requestAddrPrefix = requestAddrPrefixHttps;
				} else {
					//System.out.println("HTTP");
					requestAddrPrefix = requestAddrPrefixHttp;
				}
				
		    	System.out.println("\r\ndelete cookies"); 
				open(checkCookiesAddr);
				System.out.println("before delete, getCookie: " + browser.getCookie());
				deleteCookies();
				System.out.println("after delete, getCookie: " + browser.getCookie());

		    	System.out.println("open: " + requestAddrPrefix + file.getName()); 
		    	try {
			    	open( requestAddrPrefix + file.getName());
		    	} catch (Exception e) {
			    	System.out.println(e.getMessage());
		    	}
		    	
		    	String title = browser.getTitle();
				open(checkCookiesAddr);
				System.out.println(title);
				//System.out.println(browser.getCookie());
				checkOutOutCookie("0");
				checkUCookie();
				checkLppCookie(title);
		    //}
		    } //for
	}

	private void deleteCookies () throws Exception {
		//System.out.println("getCookie in deleteCookies: " + browser.getCookie());
        try {
        	browser.deleteAllVisibleCookies();
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }
        try {
        	browser.deleteCookie("o", "/");
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }
        try {
        	browser.deleteCookie("u", "/");
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }
        try {
        	browser.deleteCookie("lpp", "/");
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }
	}

	private void open (String addr) throws Exception {
        try {
        	browser.open(addr);
        	if ( browser.isConfirmationPresent() ) {
        		System.out.println(browser.getConfirmation());
        	}
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        	System.exit(1);
        }
	}

	private void checkOutOutCookie (String cookValue) throws Exception {
		try {
			String currentCookValue = browser.getCookieByName("o");
	    	System.out.println("checkCookie: o= " + currentCookValue + ";");
    		if ( cookValue.compareTo(currentCookValue) != 0 ) {
            	System.out.println("ERROR: wrong \"o\" cook value");
            	System.exit(1);
    		}
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        	System.exit(1);
        }
	}
	
	private void checkUCookie () throws Exception {
		int i = 0;
		while ( true ) {
			try {
				String currentCookValue = browser.getCookieByName("u");
		    	System.out.println("checkCookie: u= " + currentCookValue + ";");
		    	break;
	        } catch (Exception e) {
	        	System.out.println(e.getMessage());
	        	//System.exit(1);
	        }
	        Thread.sleep(1000);
	        i++;
	        if ( i >= 100 ) {
				System.out.println("Cookie timeout");
				System.exit(1);
	        }
		}
	}
	
	private void checkLppCookie (String title) throws Exception {
			if ( title.compareTo("Piggyback") == 0 ) {
				try {
					String currentCookValue = browser.getCookieByName("lpp");
			    	System.out.println("checkCookie: lpp= " + currentCookValue + ";");
		        } catch (Exception e) {
		        	System.out.println(e.getMessage());
		        	System.exit(1);
		        }
			} else if ( title.compareTo("Retargeting") == 0 ) {
				try {
					String currentCookValue = browser.getCookieByName("lpp");
			    	System.out.println("checkCookie: lpp= " + currentCookValue + ";");
		        	System.exit(1);
		        } catch (Exception e) {
		        	System.out.println("checkCookie: lpp= cookie not fould;");
		        }
			} else {
		    	System.out.println("ERROR. wrong <title> on page. sommething wrong. " + title);
	        	System.exit(1);
			}
	}

	private boolean isHttps (String htmlFileName) throws Exception {
		String line = "";
		File f1 = new File(htmlFolder + htmlFileName);
		if (f1.exists()) {
			BufferedReader bufferedReader = null;
			bufferedReader = new BufferedReader(new FileReader(f1.getPath()));
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("https")) {
					return true;
				}
			}
			
		} 
		
		return false;
		/*else {
			return false;
		}*/		
	}
	
}
