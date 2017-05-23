package com.redaril.qualifiers.builder;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.redaril.jmx.*;
import com.redaril.ssh.SSH;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import java.io.*;
import java.net.URLEncoder;
import com.redaril.utils.ConfigurationLoader;

public class QualifiersBuilder {

    private final static String ENV = "env2";
    
    private Selenium browser;
    private final static String RESOURCE_PATH = "resources\\qualifiers\\builder\\";
    private final static String OUTPUT_PATH = "C:\\JavaOutput\\qualifiers\\builder\\";
    private final static String PROPERTIES_FILE= "ssh.properties";
    
    //PID - Pixel ID
    //adap.tv = 2105
    //adap.tv = 40169
    private final static String PID = "9";
    
    private final static String BASE_ADDR = "http://192.168.0.107:9080/qualbuilder/";
    private final static String CQID_FILE = "createdQualifiersID.log";

    private final static String PARTNERSINFO_EX_URL = "http://" + ENV +
    	".west.p.raasnet.com:8080/partners/info?ex=1";
    private final static String QUALIFIER_URL_PREFIX = "http://" + ENV +
    	".west.p.raasnet.com:8080/partners/universal/in?pid=" + PID + "&ndl=";
    private final static String QUALIFIER_BASE_URL = "http://tsvetaev.narod.ru";
    //private final static String QUALIFIER_BASE_URL = "http://tsvetaev.narod.ru/about$me.html";
    private final static String QUALIFIER_DOMAIN = "tsvetaev.narod.ru";
    
    @Before
	public void setUp() throws Exception {
        //10.50.150.143
		browser = new DefaultSelenium("localhost",
    			4444, "*firefox", BASE_ADDR);
            browser.start();
            browser.setTimeout("60000");
	}
	
	@After
	public void tearDown() throws Exception {
		browser.stop();
	}
	
//	@Test
//	public void getTitleQB() throws Exception {
//		browser.open("http://ya.ru");
//        System.out.println("getTitleQB: " + browser.getTitle() + ";");
//        
//    	//ConfigurationLoader.initConfiguration(RESOURCE_PATH + PROPERTIES_FILE);
//        ConfigurationLoader config = new ConfigurationLoader(RESOURCE_PATH + PROPERTIES_FILE);
//        System.out.println(config.getProperty("sshRDHost"));
//        System.out.println(config.getProperty("forceReboot"));
//        
//	}
	
	@Ignore
	public void test() throws Exception {
		browser.setSpeed("750");
		browser.windowMaximize();
		browser.windowFocus();
		long startTime = 0;
		long QID = 0; // qualifier ID
		
		String filename= CQID_FILE;
		File fileToDelete = new File(filename);
		if (fileToDelete.exists()) {
			fileToDelete.delete();
		}

//==============================================================================
//==============================================================================
//==============================================================================
	if ( true ) {
    	System.out.println("\r\n" +
    			"============================================\r\n" +
    			"= CREATE QUALIFIERS ========================\r\n" +
    			"============================================");
		/*
		 * Login
		 */
		browser.open(BASE_ADDR);
		browser.type("username", "atsvetaev");
		browser.type("password", "password(0)");
		//browser.click("xpath=/html/body/div[@id='content']/form//input[@type='submit']");
		browser.click("xpath=/html/body/div[@id='content']/form//input[@type='submit' and @value='Submit']");
		//browser.click("xpath=/html/body/div[@id='content']/form/table/tbody/tr[3]/td/input");
		if ( browser.isTextPresent("Data is reloading now") ) {
	    	System.out.println("WARNING: Qualifier Builder reloading Data now. " +
	    			"Try again after 5-10 min.");
	    	System.exit(1);
		}
		
		/*
		 * Check old qualifiers. if exists delete them
		 */
		browser.open("/qualbuilder/myqualifiers");
		browser.type("domain", QUALIFIER_DOMAIN);
		browser.click("xpath=/html/body/div[@id='content']/form//input[@name='search']");
		while ( browser.isTextPresent("Delete") ) {
			browser.click("xpath=/html/body/div[@id='content']/form//a[contains(., 'Delete')]");
			Thread.sleep(5000);
		}
		
		/*
		 * Create Qualifier
		 */
		browser.open("/qualbuilder/myeditqualifier");
		browser.select("dataSourceSelectId", "Modeled Data");
		browser.select("interestSelectId", "Modeled Age");
		browser.select("pop", "ANY");
		browser.type("domains", QUALIFIER_DOMAIN);
		browser.type("baseUrl", QUALIFIER_BASE_URL);
		browser.click("xpath=/html/body/div[@id='content']/form//input[@name='submit']");

		/*
		 * Save Created Qualifier ID to File
		 */
		startTime =  System.currentTimeMillis();
		while ( true ) {
			if ( browser.isTextPresent("Qualifier saved successfully") ) {
	    	    QID = getCreatedQalifierID();
				System.out.println("Created Qualifier ID: " + QID);
				appendFile(CQID_FILE, QID);
				break;
			} else if ( System.currentTimeMillis() > startTime  + 60000 ) {
		    	System.out.println("ERROR: Can`f find \"Qualifier saved successfully\"" +
    			" to get auqlifier ID");
		    	System.exit(1);
			}
			Thread.sleep(500);
		}
		
		/*
		 * Create Qualifier
		 */
		browser.open("/qualbuilder/myeditqualifier");
		browser.select("dataSourceSelectId", "Modeled Data");
		browser.select("interestSelectId", "Modeled Gender");
		browser.select("pop", "ANY");
		browser.type("domains", QUALIFIER_DOMAIN);
		browser.type("baseUrl", QUALIFIER_BASE_URL);
		browser.click("xpath=/html/body/div[@id='content']/form//input[@name='submit']");
		Thread.sleep(3000);

		/*
		 * Save Created Qualifier ID to File
		 */
		startTime =  System.currentTimeMillis();
		while ( true ) {
			if ( browser.isTextPresent("Qualifier saved successfully") ) {
	    	    QID = getCreatedQalifierID();
				System.out.println("Created Qualifier ID: " + QID);
				appendFile(CQID_FILE, QID);
				break;
			} else if ( System.currentTimeMillis() > startTime  + 60000 ) {
		    	System.out.println("ERROR: Can`f find \"Qualifier saved successfully\"" +
    			" to get auqlifier ID");
		    	System.exit(1);
			}
			Thread.sleep(500);
		}

		/*
		 * Logout
		 */
		browser.open("/qualbuilder/mylogout");
	}
	
//==============================================================================
//==============================================================================
//==============================================================================
		ConfigurationLoader config = new ConfigurationLoader(RESOURCE_PATH + PROPERTIES_FILE);
  
		System.out.println("\r\n" +
				"============================================\r\n" +
				"= IMPORT NEW QUALIFIERS FROM BUN DB ========\r\n" +
				"============================================");
		SSH cluster = new SSH(ENV,
				config.getProperty("sshUsername"),
				config.getProperty("sshPassword"),
				config.getProperty("dbHost"),
				config.getProperty("dbPort"),
				config.getProperty("dbSid"));
		cluster.importNewQualifiers();
	
		JMX jmx = new JMX(ENV);
		System.out.println("\r\n" +
				"============================================\r\n" +
				"= REBOOT TAXONOMYLOADER ====================\r\n" +
				"============================================");
		jmx.reboot("taxonomyLoader");
		Thread.sleep(5000);
	
		System.out.println("\r\n" +
				"============================================\r\n" +
				"= WAIT FOR QUALIFIER UPDATER JOB ===========\r\n" +
				"============================================");
		jmx.reboot("QualifierUpdaterJob");
		Thread.sleep(5000);
	
		System.out.println("\r\n" +
				"============================================\r\n" +
				"= REBOOT PARTNERS ==========================\r\n" +
				"============================================");
		jmx.reboot("partners", "west");
		Thread.sleep(5000);
		
//==============================================================================
//==============================================================================
//==============================================================================
		
    	System.out.println("\r\n" +
    			"============================================\r\n" +
    			"= CHECK QUALIFIERS =========================\r\n" +
    			"============================================");
		/*
		 * Delete cookies
		 */
    	browser.open(PARTNERSINFO_EX_URL);
		browser.deleteAllVisibleCookies();
		Thread.sleep(1000);

		String url = QUALIFIER_URL_PREFIX +
				URLEncoder.encode(QUALIFIER_BASE_URL,"UTF-8");
		System.out.println("open: " + url);
		browser.open(url);
//		browser.open(QUALIFIER_URL_PREFIX +
//				URLEncoder.encode(QUALIFIER_BASE_URL,"UTF-8"));
		browser.open(PARTNERSINFO_EX_URL);

		String pageContent = browser.getBodyText();
		System.out.println("pageContent:\r\n" + pageContent);

		if ( !browser.isTextPresent("Modeled Data->Modeled Gender") ||
				!browser.isTextPresent("Modeled Data->Modeled Age")) {
	    	System.out.println("ERROR: Page content is incorrect");
		} else {
			System.out.println("INFO: QualifierBuilder test PASSED.");
		}
		
		System.out.println("FINISH.");
	}
	
	private int getCreatedQalifierID() {
		String line = browser.getText("xpath=/html/body/div[@id='content']/div[@id='message']");
		String[] arr = line.split(" ");
		return Integer.parseInt(arr[3]);
	}

//	private void appendFile ( String filename, String msg ) throws Exception {
//		try {
//    	    File file = new File(filename);
//    	    FileWriter writer = new FileWriter(file, true);
//    	    writer.write(msg);
//    	    writer.flush();
//    	    writer.close();
//    	} catch (Exception e) {
//	    	System.out.println(e.getMessage());
//    	}
//    }
	
	private void appendFile ( String filename, long num ) throws Exception {
		try {
    	    File file = new File(filename);
    	    FileWriter writer = new FileWriter(file, true);
    	    writer.write(String.valueOf(num) + "\r\n");
    	    writer.flush();
    	    writer.close();
    	} catch (Exception e) {
	    	System.out.println(e.getMessage());
    	}
    }
}
