package com.redaril.qualifiers.validation.regular;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebDriverBackedSelenium;
//import org.openqa.selenium.ie.InternetExplorerDriver;

import java.io.*;
import java.net.URLEncoder;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.redaril.utils.jmx.*;
import com.redaril.utils.ConfigurationLoader;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class RegularQualifiersValidation {
    private Selenium browser;
//    private WebDriver driver;
    
//==============================================================================

	//	FIELD TO CHANGE 
	//	
	//	ENV - which environment will be used. env1 or env2
	//	CLUSTER - partners to use. west or east
	//	DATE - date of last check. format: dd.mm.yy
	//	(usually, previous day of date last mail request)

    private static final String ENV = "env2";
    private static final String CLUSTER = "west";
    private static final String DATE = "08.06.12"; // dd.mm.yy

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

    private static final String driverName = "oracle.jdbc.driver.OracleDriver";

    private static final String PARTNERSINFO_URL = "http://" + ENV + ".west.p.raasnet.com:8080/partners/info";
    private static final String PARTNERSINFO_EX_URL = "http://" + ENV + ".west.p.raasnet.com:8080/partners/info?ex=1";
    private String QUALIFIER_URL_PREFIX;

	private final static String LOG4J_RESOURCE_PATH = "resources\\log4j\\";
    private final static String LOG4J_PROPERTIES_FILE = "log4j.properties";
	private static final Logger log = Logger.getLogger(RegularQualifiersValidation.class);
    
    private final static String RESOURCE_PATH = "resources\\qualifiers\\validation\\regular\\";
    private final static String OUTPUT_PATH = "output\\qualifiers\\validation\\regular\\";
    private static final String LOGFILENAME = OUTPUT_PATH + "bunLog_ " + DATE + ".log";
    private static final String ERRORFILENAME = OUTPUT_PATH + "bunLog_ " + DATE + "_Errors.log";
    private static final String QUALIFIER_LIST_FILE = RESOURCE_PATH + "basicQualifiersList.txt"; //list of qualifiers
    private static final String QUALIFIER_INFO_FILE = RESOURCE_PATH + "basicQualifiersWithCategory.txt"; //qualifiers and InterestCategory
    private final static String PROPERTIES_FILE= "qualifiers.properties";
    private final static String QUALIFIERS_SQL_FILE= "getNewRegularQualifiers.sql";
    private final static String QUALIFIERS_COUNT_SQL_FILE= "getNewRegularQualifiersCount.sql";
    private List<String> failedQualifiersList;
    
    private ConfigurationLoader config = 
    	new ConfigurationLoader(RESOURCE_PATH + PROPERTIES_FILE);

    
	@Before
//	public void setUpIE() throws Exception {
//		driver = new InternetExplorerDriver();
//		browser = new WebDriverBackedSelenium(driver, "http://ya.ru");
//    }
	public void setUp() throws Exception {

		File outputFolder = new File(OUTPUT_PATH);
		outputFolder.mkdirs();
		
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

	@Test
	public void test() throws Exception {

		browser.setSpeed("750");
		browser.windowMaximize();
		browser.windowFocus();

		File propertiesFile=new File(LOG4J_RESOURCE_PATH, LOG4J_PROPERTIES_FILE);
	    PropertyConfigurator.configure(propertiesFile.toString());

		failedQualifiersList = new ArrayList<String>();
		
		JMX jmx = new JMX(ENV);

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

		/* check Qualifiers using file basicQualifiersWithCategory.txt */
		if (log.isInfoEnabled()) log.info("- CHECK BASIC QUALIFIERS -------------------");
		qualifiersCount = getQualifiersCountFromFile(QUALIFIER_INFO_FILE);
		if ( (errors = checkQualifiers(QUALIFIER_INFO_FILE, qualifiersCount)) != 0 ) {
			log.error("Some error was happened... Errors Count = " + errors);
		}

		System.exit(1);

		Thread.sleep(5000);

		if (log.isInfoEnabled()) log.info("- IMPORT NEW QUALIFIERS FROM BUN DB --------");
    	importNewQualifiers();

    	Thread.sleep(5000);
    	
		if (log.isInfoEnabled()) log.info("- REBOOT TAXONOMYLOADER --------------------");
    	jmx.reboot("taxonomyLoader");

    	Thread.sleep(5000);

		if (log.isInfoEnabled()) log.info("- WAIT FOR QUALIFIER UPDATER JOB -----------");
    	jmx.reboot("QualifierUpdaterJob");

    	Thread.sleep(5000);

		if (log.isInfoEnabled()) log.info("- REBOOT PARTNERS --------------------------");
    	jmx.reboot("partners", CLUSTER);

		Thread.sleep(5000);

		if (log.isInfoEnabled()) log.info("- GET NEW QUALIFIERS -----------------------");
		errors = 0;
		qualifiersCount = getNewRegularQualifiersCount();
		if ( !getNewRegularQualifiers() ) {
			log.error("Some error was happaened in getNewQualifiers date= " + DATE);
			System.exit(1);
		}

		Thread.sleep(5000);

//    	println("\r\n" +
//    			"============================================\r\n" +
//    			"= START LOGRECEIVERS =======================\r\n" +
//    			"============================================");
//		Logreceiver p = new Logreceiver(ENV, CLUSTER, OUTPUT_PATH);
//		p.setDate(DATE);
//		p.start("partners");
//		
//		Logreceiver t = new Logreceiver(ENV, OUTPUT_PATH);
//		t.setDate(DATE);
//		t.start("taxonomyloader");
//
//		Logreceiver d = new Logreceiver(ENV, OUTPUT_PATH);
//		d.setDate(DATE);
//		d.start("DmpModel");
//		Thread.sleep(5000);
		
		if (log.isInfoEnabled()) log.info("- CHECK NEW QUALIFIERS ---------------------");
		if ( (errors = checkQualifiers(OUTPUT_PATH + "lastTimeAddedQualifiers" + "_" + DATE + ".ql", qualifiersCount)) != 0 ) {
			log.error("Some error was happened... Errors Count = " + errors);
		}
		
//    	println("\r\n" +
//    			"============================================\r\n" +
//    			"= STOP LOGRECEIVERS ========================\r\n" +
//    			"============================================");
//		p.stop();
//		t.stop();
//		d.stop();

		if (log.isInfoEnabled()) {
			log.info("- SUMMARY ----------------------------------\r\n" +
					"Qualifiers was checked: " + qualifiersCount + "\r\n" +
					"Qualifiers was failed: " + errors);
			if ( failedQualifiersList.size() > 0 ) {
				log.info("ID List of failed qualifiers:");
				for(int j = 0; j < failedQualifiersList.size(); j++)
					log.info("		" + failedQualifiersList.get(j));
			}
			log.info("--------------------------------------------");
		}
		if (log.isInfoEnabled()) log.info("FINISH.");
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
		int errorCount = 0;
		int iCount = 0;
		
		File qualifiersInfoFile = new File(filename);
		if (qualifiersInfoFile.exists()) {
			BufferedReader bufferedReader = null;
			bufferedReader = new BufferedReader(new FileReader(filename));
			
			String line = "";
			String interestCategory = "";
			String qualifierID = "";
			String qualifier = "";

			while ( (line = bufferedReader.readLine()) != null ) {
				// if line contain qualifier, get qualifier and open it
				if ( line.contains("Qualifier URL:") ) {

					//delete all cookies
					browser.open(PARTNERSINFO_URL);
					browser.deleteAllVisibleCookies();
					
					Thread.sleep(1000);
					
					//get qualifier from line
					qualifier = "";
					if (line.contains("%")) {
						qualifier = getQualifier(line);
					} else {
						qualifier = URLEncoder.encode(getQualifier(line),"UTF-8"); 
					}
					
					//get pid for request
					// if qualifier owner INC.COM use pixel with same owner
					if (qualifier.contains("inc.com")) {
						setPID("84673");
					} else {
						setPID("9");
					}

					browser.open(QUALIFIER_URL_PREFIX + qualifier);
					iCount++;
					
					if (log.isInfoEnabled()) 
						log.info("step " + iCount + " from " + qCount + "; " +
								"Errors Happend: " + errorCount+ "; " +
								"Qualifier: " + qualifier);

					Thread.sleep(1000);

					browser.open(PARTNERSINFO_EX_URL);
					interestCategory = browser.getBodyText();

					if (log.isInfoEnabled())
						log.debug("pageContent:\r\n" + interestCategory);
					
				// if line contain separator do nothing
				} else if ( line.contains("----------") ||
						line.isEmpty() ) {
					interestCategory = "";
				
				// check interest category
				} else if ( line.contains("##") ) {
						qualifierID = line.substring(line.indexOf("=")+1, line.indexOf(";"));
					//if (log.isInfoEnabled()) log.debug(tmpStr);
				} else {
					// if page content contain category it is OK
					if ( interestCategory.contains(line) ) {
						if (log.isInfoEnabled())
							log.info("interestCategory: \"" + line + "\"" +
									" for qualifier ID = " + qualifierID + " is PASSED");
					} else {
						// alarm... can`t found category
						log.error("interestCategory: " + line +
								" for qualifier ID = " + qualifierID + " is FAILED");
						failedQualifiersList.add(qualifierID);
						qualifierID = "";
						errorCount++;
					}
				}
			}
		} else {
			log.error("File " + filename + " not found.");
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
					appendStringToFile(QUALIFIER_INFO_FILE, "\r\n");
					browser.open(PARTNERSINFO_URL);
					browser.deleteAllVisibleCookies();
					Thread.sleep(1000);
					//System.out.println("Get qualifier: " + line);
					if (log.isInfoEnabled())
						log.info("Qualifier URL: " + line);
					appendStringToFile(QUALIFIER_INFO_FILE, "Qualifier URL: " + line + "\r\n");
					if (line.contains("inc.com")) {
						setPID("84673");
					} else {
						setPID("9");
					}					
					if (log.isInfoEnabled())
						log.info("Open: " + QUALIFIER_URL_PREFIX + line);
					browser.open(QUALIFIER_URL_PREFIX + line);
					browser.open(PARTNERSINFO_EX_URL);
					String bodyText = browser.getBodyText();
					if (log.isInfoEnabled())
						log.info(bodyText);
					appendStringToFile(QUALIFIER_INFO_FILE, bodyText + "\r\n");
					if (log.isInfoEnabled())
						log.info(separator);
					appendStringToFile(QUALIFIER_INFO_FILE, separator + "\r\n");
					Thread.sleep(1000);
				}
			} else {
				log.error("File \"" + QUALIFIER_LIST_FILE + "\"not found");
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
    		log.error(e.getMessage());
    	}
    }

////////////////////////////////////////////////////////////////////////////////
// for db //////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

	public int getNewRegularQualifiersCount () throws Exception {
		Connection dbConn = dataBaseConnect();
		if ( dbConn == null  ) {
			log.error("Connection FAILED");
	        System.exit(1);
		}
		if (log.isDebugEnabled()) log.debug("Connection OK");
        int result = 0;
        try {
        	Statement stmt = dbConn.createStatement();
	        ResultSet rset = stmt.executeQuery(gerSQLScript(QUALIFIERS_COUNT_SQL_FILE));
	        rset.next();
	        if (log.isDebugEnabled()) log.debug("Records Count= " + rset.getString(1));
        	result = Integer.parseInt(rset.getString(1));
	    } catch (Exception e) {
	    	log.error("Exception:\n" + e.getMessage());
	        return 0;
	    }
	    return result;	
	}
	
	public boolean getNewRegularQualifiers () throws Exception {
		String filename= OUTPUT_PATH + "lastTimeAddedQualifiers" + "_" + DATE + ".ql";
		
		File fileToDelete = new File(filename);
		if (fileToDelete.exists()) {
			fileToDelete.delete();
		}
		
		Connection dbConn = dataBaseConnect();
		if ( dbConn == null  ) {
			log.error("Data base connection FAILED");
	        System.exit(1);
		}
		if (log.isDebugEnabled()) log.debug("Data base connection OK");

        try {
        	Statement stmt = dbConn.createStatement();
	        ResultSet rset = stmt.executeQuery(gerSQLScript(QUALIFIERS_SQL_FILE));
	        while (rset.next()) {
	        	if (log.isDebugEnabled()) log.debug(rset.getString(1) + "	" + 
	        			rset.getString(2) + "	" + rset.getString(3) + "	" + 
	        			rset.getString(4));
	        	appendStringToFile(filename, "\r\nQualifier URL: " + getQualifierdb(rset.getString(1)) + "\r\n" + 
	        			"## qualifier_id=" + rset.getString(3) + "; interest_id=" + rset.getString(4) + ";\r\n" +
	        			rset.getString(2) + "\r\n" +
	        			"--------------------------------------------------------------------------------\r\n");
	        }
	    } catch (Exception e) {
	    	log.error("Exception:\n" + e.getMessage());
	        return false;
	    }
	    return true;
	}

	private String getDBLogin() {
		return ENV + "_meta";
	}
	private String getDBPassword() {
		return ENV + "_meta";
	}
	
	private Connection dataBaseConnect() {
	    try {
	    	Connection connection;

	    	String url = "jdbc:oracle:thin:@" + 
	        	config.getProperty("dbHost") + ":" + 
	        	config.getProperty("dbPort") + ":" + 
	        	config.getProperty("dbSid");
	        
	        Class.forName(driverName);
	        connection = DriverManager.getConnection(url, getDBLogin(), getDBPassword());
	        if (log.isDebugEnabled()) log.debug("connecting: " + url);
	        if(connection.equals(null))
	    	    return null;		
	        else
	    	    return connection;		
	    } catch (ClassNotFoundException e) {
	    	log.error("ClassNotFoundException: " + e.getLocalizedMessage());
	    } catch (SQLException e) {
	    	log.error("SQLException: " + e.getMessage());
	    }
	    return null;
	}

	public void importNewQualifiers () throws Exception {
		Connection dbConn = dataBaseConnect();
		if ( dbConn == null  ) {
			log.error("Data base connection FAILED");
	        System.exit(1);
		}

		if (log.isDebugEnabled()) log.debug("Data base connection OK");
		
		if (log.isInfoEnabled()) log.info("Wait it can take a few minutes");

		try {
        	CallableStatement cstmt = dbConn.prepareCall("{call BUNUPDATER()}");
        	try {
        		cstmt.execute();
        	} catch (Exception e) {
        		log.error("Exception: " + e.getMessage());
        	}
    		if (log.isInfoEnabled()) log.info("Stored procedure named BunUpdater successfully completed");
        	
	    } catch (Exception e) {
	    	log.error("Exception:" + e.getMessage());
	    }
	}
	
	private String getQualifierdb ( String line ) throws Exception {
		int start = line.lastIndexOf("http://");
		start+=7;
		int end = line.length();
		char cLine[] = new char[end - start]; 
		line.getChars(start, end, cLine, 0);
		//System.out.println("Qualifier: " + String.valueOf(cLine));
		return String.valueOf(cLine);
	}
	
	private String gerSQLScript ( String scriptName ) throws Exception {
		String line = "";
		String sql = "";
		File scriptFile = new File(RESOURCE_PATH + scriptName);
		if (scriptFile.exists()) {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(scriptFile));
			while ( (line = bufferedReader.readLine()) != null ) {
				if (line.contains("{DATE}"))
					line = line.replace("{DATE}", DATE);
				sql = sql.concat(line.trim() + " ");

//				if (line.contains("{") && line.contains("}")) {
//					
//					System.out.println(line.substring(line.indexOf("{"), line.indexOf("}")+1));
//					System.out.println(line.substring(line.indexOf("{")+1, line.indexOf("}")));
//					System.out.println(RegularQualifiersValidation.class.getDeclaredField("DATE"));
//					System.out.println(RegularQualifiersValidation.class.getField("DATE"));
//					
//					line.replace(
//							line.substring(line.indexOf("{"), line.indexOf("}")), 
//							String.valueOf(RegularQualifiersValidation.class.getField(
//									line.substring(line.indexOf("{")+1, line.indexOf("}"))))
//					);
//				}
			}
			bufferedReader.close();
		} else {
			log.error("ERROR: Can`t find file: " + scriptName);
		}
		return sql;
	}
	
}
