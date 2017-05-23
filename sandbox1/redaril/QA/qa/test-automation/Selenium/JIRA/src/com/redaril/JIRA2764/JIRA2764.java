package com.redaril.JIRA2764;

import java.io.File;

import com.thoughtworks.selenium.Selenium;
import junit.framework.TestCase;

public class JIRA2764 extends TestCase {

	private Selenium browser;
    private String ENV;
    private String CLUSTER;
    private String HTDOCS_HTML_PATH;
    private String URL_PREFIX_HTTP;
    private String PARTNERSINFO_URL;
    private String HTDOCS_PATH = Config.getProperty("HTDOCS_PATH") + "\\";

    public JIRA2764 (Selenium browserFromMain, String env, String cluster) {
    	browser = browserFromMain;
    	ENV = env;
    	CLUSTER = cluster;
    	HTDOCS_HTML_PATH  = HTDOCS_PATH + "2764\\";
        URL_PREFIX_HTTP = "http://" + ENV + "." + CLUSTER + ".p.raasnet.com:8080/partners/universal/in?";
        PARTNERSINFO_URL = "http://" + ENV + "." + CLUSTER + ".p.raasnet.com:8080/partners/info";
    }
    
    public String getDescription () {
    	return "task: DMPPROJECT-2764\r\n" +
    	"link: http://depot.redaril.com:8080/browse/DMPPROJECT-2764\r\n" +
    	"issue: DMPPROJECT-2760\r\n" +
    	"link: http://depot.redaril.com:8080/browse/DMPPROJECT-2760" +
    	"issue: DMPPROJECT-2441" +
    	"link: http://depot.redaril.com:8080/browse/DMPPROJECT-2441";
    	
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
        if ( false ) {
	        MakePixel mp = new MakePixel(browser, ENV, CLUSTER, HTDOCS_HTML_PATH);
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
	    String filename;
	    for (int i = 0; i < list.length; i++) {
	    	file = new File(HTDOCS_HTML_PATH + list[i]);
	    	
			System.out.println("\r\nDELETE cookies");
			browser.open(PARTNERSINFO_URL);
			browser.deleteAllVisibleCookies();

			System.out.println("\r\nCREATE cookies o=9 .raasnet.com");
			browser.createCookie("o=9", "path=/; domain=.raasnet.com; expires=Tue, 23-Jun-2037 20:06:01 GMT;");
			
	    	filename = file.getName();
	    	int separator = filename.lastIndexOf("_");

	    	char pixelNumber[] = new char[separator]; 
			filename.getChars(0, separator, pixelNumber, 0);
			
			separator++;
			char pixelFormat[] = new char[filename.length() - separator]; 
			filename.getChars(separator, filename.length(), pixelFormat, 0);
			
			System.out.println("pixelNumber= " + String.valueOf(pixelNumber));
			System.out.println("pixelFormat= " + String.valueOf(pixelFormat));

			System.out.println("URL= " + URL_PREFIX_HTTP +
					"pid=" + String.valueOf(pixelNumber) +
					"&t=" + String.valueOf(pixelFormat).toLowerCase());
			browser.open(URL_PREFIX_HTTP +
					"pid=" + String.valueOf(pixelNumber) +
					"&t=" + String.valueOf(pixelFormat).toLowerCase());
			System.out.println("page content: " + browser.getBodyText());
			System.out.println("html source: " + browser.getHtmlSource());
	    }
		return true;
	}
	
}
