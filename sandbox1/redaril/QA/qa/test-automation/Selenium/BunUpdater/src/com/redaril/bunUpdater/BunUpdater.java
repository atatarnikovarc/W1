package com.redaril.bunUpdater;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.ie.InternetExplorerDriver;
import java.io.*;
import java.net.URLEncoder;

import junit.framework.TestCase;

import com.redaril.jmx.*;
import com.redaril.ssh.*;
import com.redaril.logreceiver.*;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class BunUpdater extends TestCase {
    private Selenium browser;
    private WebDriver driver;
    
    
//==============================================================================

	//	FIELD TO CHANGE 
	//	
	//	ENV - which environment will be used. env1 or env2
	//	CLUSTER - partners to use. west or east
	//	DATE - date of last check. format: dd.mm.yy
	//	(usually, previous day of date last mail request)

    private static final String ENV = "env2";
    private static final String CLUSTER = "west";
    private static final String DATE = "17.04.12"; // dd.mm.yy

//==============================================================================

    
    
//    for inc.com
//    http://env2.dmpui/dmpadmin/
//    login/pswd qa_int/qa_inc
//   
//    client: mansueto
//    Managed Account: Inc.com
//    Data Consumer: Inc.com
//    
//    pixel id: 84673
    
    private static final String PARTNERSINFO_URL = "http://" + ENV + ".west.p.raasnet.com:8080/partners/info";
    private static final String PARTNERSINFO_EX_URL = "http://" + ENV + ".west.p.raasnet.com:8080/partners/info?ex=1";
    private static final String QUALIFIER_LIST_FILE = "basicQualifiersList.txt"; //list of qualifiers
    private static final String QUALIFIER_INFO_FILE = "basicQualifiersWithCategory.txt"; //qualifiers and InterestCategory
    private static final String LOGFILENAME = "logs\\bunLog_ " + DATE + ".log";
    private static final String ERRORFILENAME = "logs\\bunLog_ " + DATE + "_Errors.log";
    private String QUALIFIER_URL_PREFIX;

	private final static String SSH_USERNAME = Config.getProperty("sshUsername");
	private final static String SSH_PASSWORD = Config.getProperty("sshPassword");
	private final static String DB_HOST = Config.getProperty("dbHost");
	private final static String DB_PORT = Config.getProperty("dbPort");
	private final static String DB_SID = Config.getProperty("dbSid");

    
	@Before
	public void setUpIE() throws Exception {
		driver = new InternetExplorerDriver();
		browser = new WebDriverBackedSelenium(driver, "http://ya.ru");
    }
	
	public void setUp() throws Exception {
        //10.50.150.143
		browser = new DefaultSelenium("localhost",
    			4444, "*firefox", PARTNERSINFO_URL);
            browser.start();
            browser.setTimeout("60000");
	}
	
	@After
	public void tearDown() throws Exception {
		browser.stop();
	}

	private void setPID (String PID) {
		QUALIFIER_URL_PREFIX = "http://" + ENV + ".west.p.raasnet.com:8080/partners/universal/in?pid=" + PID + "&ndl=http%3A//";
	}
	
	public void test() throws Exception {
		browser.setSpeed("750");
		browser.windowMaximize();
		browser.windowFocus();

		int errors = 0;
		int qualifiersCount = 0;

		/*
		 * auxiliary method
		 * use for first run to prepare data to compare with it in test in future 
		 * use it at Correctly working build
		 * to get for Qualifiers their InterestCategory
		 * for creation db of Qualifiers and InterestCategory
		 * 
		 * method use file basicQualifiersList.txt and
		 * will make file basicQualifiersWithCategory.txt
		 */
		//getQualifiersInterestCategory();

		println("\r\n" +
    			"============================================\r\n" +
    			"= CHECK BASIC QUALIFIERS ===================\r\n" +
    			"============================================");
		/* check Qualifiers using file basicQualifiersWithCategory.txt */
		qualifiersCount = getQualifiersCountFromFile(QUALIFIER_INFO_FILE);
		if ( (errors = checkQualifiers(QUALIFIER_INFO_FILE, qualifiersCount)) != 0 ) {
			println("Error!!! Some error was happened...\r\n" +
						"Errors Count = " + errors);
		}
		
		/*
		 * bun updater script to get new qualifiers
		 * this script copy all bun_meta_db to env_meta_db
		 */ 
    	println("\r\n" +
    			"============================================\r\n" +
    			"= IMPORT NEW QUALIFIERS FROM BUN DB ========\r\n" +
    			"============================================");
		SSH cluster = new SSH(ENV, SSH_USERNAME, SSH_PASSWORD, DB_HOST, DB_PORT, DB_SID);
		cluster.importNewQualifiers();

		JMX jmx = new JMX(ENV);
    	println("\r\n" +
    			"============================================\r\n" +
    			"= REBOOT TAXONOMYLOADER ====================\r\n" +
    			"============================================");
    	jmx.reboot("taxonomyLoader");
		Thread.sleep(5000);

		println("\r\n" +
    			"============================================\r\n" +
    			"= WAIT FOR QUALIFIER UPDATER JOB ===========\r\n" +
    			"============================================");
    	jmx.reboot("QualifierUpdaterJob");
		Thread.sleep(5000);

		println("\r\n" +
    			"============================================\r\n" +
    			"= REBOOT PARTNERS ==========================\r\n" +
    			"============================================");
    	jmx.reboot("partners", "west");
		Thread.sleep(5000);

    	println("\r\n" +
    			"============================================\r\n" +
    			"= GET NEW QUALIFIERS =======================\r\n" +
    			"============================================");
		DBConnect dbc = new DBConnect(ENV, DATE); 
		errors = 0;
		qualifiersCount = dbc.getQualifiersCount();
		if ( !dbc.getNewQualifiers() ) {
			println("\r\nSome error was happaened in getNewQualifiers date= " + DATE);
			fail("Some error was happaened in getNewQualifiers date= " + DATE);
			System.exit(1);
		}

    	println("\r\n" +
    			"============================================\r\n" +
    			"= START LOGRECEIVERS =======================\r\n" +
    			"============================================");
		Logreceiver p = new Logreceiver(ENV, CLUSTER);
		p.setDate(DATE);
		p.start("partners");
		
		Logreceiver t = new Logreceiver(ENV);
		t.setDate(DATE);
		t.start("taxonomyloader");

		Logreceiver d = new Logreceiver(ENV);
		d.setDate(DATE);
		d.start("DmpModel");
				
		Thread.sleep(5000);
		
		println("\r\n" +
    			"============================================\r\n" +
    			"= CHECK NEW QUALIFIERS =====================\r\n" +
				"============================================");
		if ( (errors = checkQualifiers("lastTimeAddedQualifiers" + "_" + DATE + ".ql", qualifiersCount)) != 0 ) {
			println("\r\nError!!! Some error was happened...\r\n" +
					"Errors Count = " + errors);
			fail("Error!!! Some error was happened...\r\n" +
					"Errors Count = " + errors);
		}

		
    	println("\r\n" +
    			"============================================\r\n" +
    			"= STOP LOGRECEIVERS ========================\r\n" +
    			"============================================");
		p.stop();
		t.stop();
		d.stop();

		println("FINISH.");
	}
	
	private String getQualifier ( String line ) throws Exception {
		int start = line.lastIndexOf("URL:");
		start+=5;
		int end = line.length();
		char cLine[] = new char[end - start]; 
		line.getChars(start, end, cLine, 0);
		return String.valueOf(cLine);
	}

	private int getQualifiersCountFromFile (String filename) throws Exception {
		String line = "";
		int i = 0;
		File qif = new File(filename);
		if (qif.exists()) {
			BufferedReader bufferedReader = null;
			bufferedReader = new BufferedReader(new FileReader(filename));
			while ( (line = bufferedReader.readLine()) != null ) {
				if (line.contains("Qualifier URL"))
					i++;
			}
			bufferedReader.close();
		}
		return i;	
	}
	
	private int checkQualifiers ( String filename, int qCount ) throws Exception {
		//check exists qualifiers and category
		int errorCount = 0;
		int iCount = 0;
		
		File qif = new File(filename);
		if (qif.exists()) {
			BufferedReader bufferedReader = null;
			bufferedReader = new BufferedReader(new FileReader(filename));
			
			String tmpStr = "";
			String line = "";
			String interestCategory = "";
			String pageContent1 = "";
			String pageContent2 = "";
			String ifErrorHeader = "";
			String ifErrorMiddle = "";
			String ifErrorQualifierID = "";
			String ifErrorEnd = "";
			while ( (line = bufferedReader.readLine()) != null ) {
				// if line contain qualifier, get qualifier and open it
				if ( line.contains("Qualifier URL:") ) {
					println("");
					browser.open(PARTNERSINFO_URL);
					browser.deleteAllVisibleCookies();
					Thread.sleep(1000);
					
					String qualifier = "";
					if (line.contains("%")) {
						qualifier = getQualifier(line);
					} else {
						qualifier = URLEncoder.encode(getQualifier(line),"UTF-8"); 
					}
					
					if (qualifier.contains("inc.com")) {
						setPID("84673");
					} else {
						setPID("9");
					}

					System.out.println();
					browser.open(QUALIFIER_URL_PREFIX + qualifier);
					iCount++;
					pageContent1 = browser.getBodyText();
					
					tmpStr = "--------------------------------------------------------------------------------\r\n" +
							"step " + iCount + " from " + qCount + "; Errors Happend: " + errorCount+ ";\r\n" +
							"Qualifier: " + qualifier + "\r\n" +
							"URL: " + QUALIFIER_URL_PREFIX + qualifier + "\r\n" +
							"pageContent:\r\n" +
							pageContent1 + "\r\n";
					
					ifErrorHeader = tmpStr;
					println(tmpStr);
					
					Thread.sleep(1000);

					browser.open(PARTNERSINFO_EX_URL);
					interestCategory = browser.getBodyText();
					pageContent2 = browser.getBodyText();
					tmpStr = "URL: " + PARTNERSINFO_EX_URL + "\r\n" +
							"pageContent:\r\n" +
							pageContent2 + "\r\n";
					println(tmpStr);
					ifErrorMiddle = tmpStr;
					
				// if line contain separator do nothing
				} else if ( line.contains("----------") ||
						line.isEmpty() ) {
					interestCategory = "";
				
				// check interest category
				} else if ( line.contains("##") ) {
					println(line);
					ifErrorQualifierID = line;
				} else {
					// if page content contain category from file OK
					if ( interestCategory.contains(line) ) {
						tmpStr = "interestCategory from " + filename + 
								": " + line + " DONE";
						println(tmpStr);
						ifErrorEnd = tmpStr;
					// alarm... can`t found category
					} else {
						tmpStr = "interestCategory from " + filename + 
								": " + line + " FAIL";
						println(tmpStr);
						ifErrorEnd = tmpStr;
						printlnError(ifErrorHeader + ifErrorMiddle + ifErrorQualifierID + ifErrorEnd);
						ifErrorQualifierID = "";
						errorCount++;
					}
				}
			}
		} else {
			println("File " + filename + " not found.");
			return -1;
		}
		return errorCount;
	}
	
	private void getQualifiersInterestCategory () throws Exception {
		
	//auxiliary method
	//use it at Correctly working build
	//to get for Qualifiers their InterestCategory
	//used for creation db of Qualifiers and InterestCategory
		
		/*//to get info about Qualifiers
			for example:
			Qualifier URL: http://p.raasnet.com:8080/partners/universal/in?pid=9&ndl=http%3A//sctax.org/ =&t=f
			Red Aril Interest->Finance->Tax Information
			Red Aril Interest->Finance
		*/
			File qlf = new File(QUALIFIER_LIST_FILE);
			if (qlf.exists()) {
				BufferedReader bufferedReader = null;
				bufferedReader = new BufferedReader(new FileReader(QUALIFIER_LIST_FILE));
				
				String line;
				String separator = "--------------------------------------------------------------------------------";
				while ( (line = bufferedReader.readLine()) != null ) {
					//System.out.println("\r\nDELETE cookies");
					println("");
					appendStringToFile(QUALIFIER_INFO_FILE, "\r\n");
					browser.open(PARTNERSINFO_URL);
					browser.deleteAllVisibleCookies();
					Thread.sleep(1000);
					//System.out.println("Get qualifier: " + line);
					println("Qualifier URL: " + line);
					appendStringToFile(QUALIFIER_INFO_FILE, "Qualifier URL: " + line + "\r\n");
					if (line.contains("inc.com")) {
						setPID("84673");
					} else {
						setPID("9");
					}					
					println("Open: " + QUALIFIER_URL_PREFIX + line);
					browser.open(QUALIFIER_URL_PREFIX + line);
					browser.open(PARTNERSINFO_EX_URL);
					String bodyText = browser.getBodyText();
					println(bodyText);
					appendStringToFile(QUALIFIER_INFO_FILE, bodyText + "\r\n");
					println(separator);
					appendStringToFile(QUALIFIER_INFO_FILE, separator + "\r\n");
					Thread.sleep(1000);
				}
			} else {
				println("File not found.");
			}
	}

	private void appendStringToFile ( String filename, String msg ) throws Exception {
		try {
    	    File file = new File(filename);
    	    FileWriter writer = new FileWriter(file, true);
    	    writer.write(msg);
    	    writer.flush();
    	    writer.close();
    	} catch (Exception e) {
    		println(e.getMessage());
    	}
    }

	private void println ( String msg ) throws Exception {
		try {
    	    File file = new File(LOGFILENAME);
    	    FileWriter writer = new FileWriter(file, true);
    	    System.out.println(msg);
    	    writer.write(msg + "\r\n");
    	    writer.flush();
    	    writer.close();
    	} catch (Exception e) {
	    	System.out.println(e.getMessage());
    	}
    }

	private void printlnError ( String msg ) throws Exception {
		try {
    	    File file = new File(ERRORFILENAME);
    	    FileWriter writer = new FileWriter(file, true);
    	    //System.out.println(msg);
    	    writer.write(msg + "\r\n");
    	    writer.flush();
    	    writer.close();
    	} catch (Exception e) {
	    	System.out.println(e.getMessage());
    	}
    }
}
