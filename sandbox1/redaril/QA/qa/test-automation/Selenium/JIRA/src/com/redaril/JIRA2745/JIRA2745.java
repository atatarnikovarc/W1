package com.redaril.JIRA2745;

import java.io.File;

import com.thoughtworks.selenium.Selenium;
import junit.framework.TestCase;

public class JIRA2745 extends TestCase {

	private Selenium browser;
	private String ENV;
	private String CLUSTER;
    private String SCRIPTS_TO_CHECK;
    private String HTDOCS_HTML_PATH;
    private String HTDOCS_SCRIPT_PATH;
    private String URL_PREFIX_HTTP;
    private String SRC_PREFIX_HTTP;
    private String PARTNERSINFO_URL;
    private String HTDOCS_PATH = Config.getProperty("HTDOCS_PATH") + "\\";

    public JIRA2745 (Selenium browserFromMain, String env, String cluster, String scriptToCheck) {
    	browser = browserFromMain;
    	ENV = env;
    	CLUSTER = cluster;
    	SCRIPTS_TO_CHECK = scriptToCheck;
    	HTDOCS_HTML_PATH = HTDOCS_PATH + "2745\\html\\" + SCRIPTS_TO_CHECK + "\\";
    	HTDOCS_SCRIPT_PATH = HTDOCS_PATH + "2745\\partners\\" + SCRIPTS_TO_CHECK + "\\";
        URL_PREFIX_HTTP = "http://p0.raasnet.com/JIRA/2745/html/" + SCRIPTS_TO_CHECK + "/";
        SRC_PREFIX_HTTP = "http://p0.raasnet.com/JIRA/2745/partners/" + SCRIPTS_TO_CHECK + "/";
        PARTNERSINFO_URL = "http://" + ENV + "." + CLUSTER + ".p.raasnet.com:8080/partners/info";
    }
    
    public String getDescription () {
    	return "task: DMPPROJECT-2759\r\n" +
    	"link: http://depot.redaril.com:8080/browse/DMPPROJECT-2759\r\n" +
    	"test plan: DMPPROJECT-2745\r\n" +
    	"link: http://depot.redaril.com:8080/browse/DMPPROJECT-2745";
    }
    
	public boolean execute() throws Exception {
		browser.setSpeed("750");
		browser.windowMaximize();
		browser.windowFocus();
		try {
			browser.open(PARTNERSINFO_URL);
        } catch (Exception e) {
            e.printStackTrace();
	    	System.out.println(e.getMessage());
    		browser.stop();
     		return false;
        }

        /* 
         * =====================================================================
         * CREATE DATA PIXELS FOR TEST
         * =====================================================================
         */

        // if you have actual pixels in db with html templates and don`t need
        // to create new. use if ( false ) {    
        if ( true ) {
	        MakePixel mp = new MakePixel(browser, ENV, HTDOCS_SCRIPT_PATH, HTDOCS_HTML_PATH, SRC_PREFIX_HTTP);
	        int pixelID = mp.checkLastPixelID(200000);
	        System.out.println("last PixelID was: " + pixelID);
	        
	        char dataPixelFromat[] = { 'S', 'I', 'F' }; 
	       	char dataPixelIsHttps[] = { 'N' };
	      
	        for(char chPF: dataPixelFromat) {
	            for(char chDPHttps: dataPixelIsHttps) {
			        pixelID++;
			        mp.makePixel( "DATA PIXEL", String.valueOf(pixelID), "3",
			        		 		"Y", String.valueOf(chDPHttps), "5", "Piggyback", "http://tsvetaev.narod.ru",
			        		 		"N", "N", String.valueOf(chPF));
		        }
	        }
        }

        /* 
         * =====================================================================
         * CHECK REQUESTS
         * =====================================================================
         */        
        File dir = new File(HTDOCS_HTML_PATH);
		String[] list = dir.list();
	    if (list.length == 0) {
	    	System.out.println("Error. Have no .html at " + HTDOCS_HTML_PATH);
	    	return false;
	    }
	    File file;
	    for (int i = 0; i < list.length; i++) {
	    	file = new File(HTDOCS_HTML_PATH + list[i]);
	    	
			System.out.println("\r\nDELETE cookies");
			browser.open(PARTNERSINFO_URL);
			browser.deleteAllVisibleCookies();

	    	System.out.println("INFO open: " + URL_PREFIX_HTTP + file.getName()); 
	    	try {
		    	open(URL_PREFIX_HTTP + file.getName());
	    	} catch (Exception e) {
		    	System.out.println(e.getMessage());
	    	}
	    	
	    	String title = browser.getTitle();
			open(PARTNERSINFO_URL);
			System.out.println(title);
			if ( !checkOutOutCookie("0") ) return false;
			if ( !checkUCookie() ) return false;
			if ( !checkLppCookie() ) return false;
	    }
		return true;
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

	private boolean checkOutOutCookie (String cookValue) throws Exception {
		try {
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
	
}
