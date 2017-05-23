package com.redaril.dmptf.tests.testnotready.qualifiers.builder;
//package com.redaril.dmptf.tests.qualifiers.builder;
//
//import org.slf4j.Logger;
//import org.apache.log4j.PropertyConfigurator;
//import org.junit.After;
//import org.junit.Before;
//
//import com.redaril.dmptf.util.jmx.*;
//import com.thoughtworks.selenium.DefaultSelenium;
//import com.thoughtworks.selenium.Selenium;
//import java.io.*;
//import java.net.URLEncoder;
//import java.sql.CallableStatement;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//import junit.framework.TestCase;
//
//import com.redaril.dmptf.util.ConfigurationLoader;
//
//public class QualifiersBuilder extends TestCase {
//
//    private final static String COMMON_PATH = File.separator +
//		    "qualifiers" + File.separator +
//		    "builder" + File.separator;
//    private final static String CONFIG_PATH = "testClassConfigurationLoader" + File.separator;
//    private final static String OUTPUT_PATH = "output" + COMMON_PATH;
//
//    private final static String PROPERTIES_FILE = "testClassConfigurationLoader/qualifiers(env-1).properties";
//    private static ConfigurationLoader testClassConfigurationLoader =
//    	new ConfigurationLoader(CONFIG_PATH + PROPERTIES_FILE);
//
//    private static final String ENV = testClassConfigurationLoader.getProperty("ENV");
//    private static final String envConfigID = testClassConfigurationLoader.getProperty("envConfigID");
//
//    private Selenium browser;
//
//    private static final String driverName = "oracle.jdbc.driver.OracleDriver";
//
//    private final static String LOG4J_PROPERTIES_FILE = "testClassConfigurationLoader/log4j.properties";
//	private static final Logger log = LoggerFactory.getLogger(QualifiersBuilder.class);
//
//	//PID - Pixel ID
//    //adap.tv = 2105
//    //adap.tv = 40169
//    private final static String PID = "9";
//
//    private final static String BASE_ADDR = "http://192.168.0.107:9080/qualbuilder/";
//    private final static String CQID_FILE = OUTPUT_PATH + "createdQualifiersID.log";
//
//    private final static String PARTNERSINFO_EX_URL = "http://" + ENV +
//    	".west.p.raasnet.com:8080/partners/info?ex=1";
//    private final static String QUALIFIER_URL_PREFIX = "http://" + ENV +
//    	".west.p.raasnet.com:8080/partners/universal/in?pid=" + PID + "&ndl=";
//    private final static String QUALIFIER_BASE_URL = "http://tsvetaev.narod.ru";
//    //private final static String QUALIFIER_BASE_URL = "http://tsvetaev.narod.ru/about$me.html";
//    private final static String QUALIFIER_DOMAIN = "tsvetaev.narod.ru";
//
//    @Before
//	public void setUp() throws Exception {
//        //10.50.150.143
//
//		File outputFolder = new File(OUTPUT_PATH);
//		outputFolder.mkdirs();
//
//		browser = new DefaultSelenium("localhost",
//    			4444, "*firefox", BASE_ADDR);
//            browser.start();
//            browser.setTimeout("60000");
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		browser.stop();
//	}
//
////	@Test
////	public void getTitleQB() throws Exception {
////		browser.open("http://ya.ru");
////        System.out.println("getTitleQB: " + browser.getTitle() + ";");
////
////    	//ConfigurationLoader.initConfiguration(RESOURCE_PATH + PROPERTIES_FILE);
////        ConfigurationLoader testClassConfigurationLoader = new ConfigurationLoader(RESOURCE_PATH + PROPERTIES_FILE);
////        System.out.println(testClassConfigurationLoader.getProperty("sshRDHost"));
////        System.out.println(testClassConfigurationLoader.getProperty("forceReboot"));
////
////	}
//
////	@Test
//	public void test() throws Exception {
//		browser.setSpeed("750");
//		browser.windowMaximize();
//		browser.windowFocus();
//		long startTime = 0;
//		long QID = 0; // qualifier ID
//
//		File propertiesFile=new File(CONFIG_PATH, LOG4J_PROPERTIES_FILE);
//	    PropertyConfigurator.configure(propertiesFile.toString());
//
//		String filename= CQID_FILE;
//		File fileToDelete = new File(filename);
//		if (fileToDelete.exists()) {
//			fileToDelete.delete();
//		}
//
////==============================================================================
////==============================================================================
////==============================================================================
//	if ( true ) {
//		if (log.isInfoEnabled()) log.info("- CREATE QUALIFIERS ------------------------");
//		/*
//		 * Login
//		 */
//		browser.open(BASE_ADDR);
//		browser.type("username", "atsvetaev");
//		browser.type("password", "password(0)");
//		//browser.click("xpath=/html/body/div[@id='content']/form//input[@type='submit']");
//		browser.click("xpath=/html/body/div[@id='content']/form//input[@type='submit' and @value='Submit']");
//		//browser.click("xpath=/html/body/div[@id='content']/form/table/tbody/tr[3]/td/input");
//		if ( browser.isTextPresent("Data is reloading now") ) {
//			log.warn("Qualifier Builder reloading Data now. Try again after 5-10 min.");
//	    	System.exit(1);
//		}
//
//		/*
//		 * Check old qualifiers. if exists delete them
//		 */
//		browser.open("/qualbuilder/myqualifiers");
//		browser.type("domain", QUALIFIER_DOMAIN);
//		browser.click("xpath=/html/body/div[@id='content']/form//input[@name='search']");
//		while ( browser.isTextPresent("Delete") ) {
//			browser.click("xpath=/html/body/div[@id='content']/form//a[contains(., 'Delete')]");
//			Thread.sleep(5000);
//		}
//
//		/*
//		 * Create Qualifier
//		 */
//		browser.open("/qualbuilder/myeditqualifier");
//		browser.select("dataSourceSelectId", "Modeled Data");
//		browser.select("interestSelectId", "Modeled Age");
//		browser.select("pop", "ANY");
//		browser.type("domains", QUALIFIER_DOMAIN);
//		browser.type("baseUrl", QUALIFIER_BASE_URL);
//		browser.click("xpath=/html/body/div[@id='content']/form//input[@name='submit']");
//
//		/*
//		 * Save Created Qualifier ID to File
//		 */
//		startTime =  System.currentTimeMillis();
//		while ( true ) {
//			if ( browser.isTextPresent("Qualifier saved successfully") ) {
//	    	    QID = getCreatedQalifierID();
//	    	    if (log.isInfoEnabled()) log.info("Created Qualifier ID: " + QID);
//				appendFile(CQID_FILE, QID);
//				break;
//			} else if ( System.currentTimeMillis() > startTime  + 60000 ) {
//				log.error("Can`f find \"Qualifier saved successfully\"" +
//    			" to get auqlifier ID");
//		    	System.exit(1);
//			}
//			Thread.sleep(500);
//		}
//
//		/*
//		 * Create Qualifier
//		 */
//		browser.open("/qualbuilder/myeditqualifier");
//		browser.select("dataSourceSelectId", "Modeled Data");
//		browser.select("interestSelectId", "Modeled Gender");
//		browser.select("pop", "ANY");
//		browser.type("domains", QUALIFIER_DOMAIN);
//		browser.type("baseUrl", QUALIFIER_BASE_URL);
//		browser.click("xpath=/html/body/div[@id='content']/form//input[@name='submit']");
//		Thread.sleep(3000);
//
//		/*
//		 * Save Created Qualifier ID to File
//		 */
//		startTime =  System.currentTimeMillis();
//		while ( true ) {
//			if ( browser.isTextPresent("Qualifier saved successfully") ) {
//	    	    QID = getCreatedQalifierID();
//	    	    if (log.isInfoEnabled()) log.info("Created Qualifier ID: " + QID);
//				appendFile(CQID_FILE, QID);
//				break;
//			} else if ( System.currentTimeMillis() > startTime  + 60000 ) {
//				log.error("Can`f find \"Qualifier saved successfully\"" +
//    			" to get auqlifier ID");
//		    	System.exit(1);
//			}
//			Thread.sleep(500);
//		}
//
//		/*
//		 * Logout
//		 */
//		browser.open("/qualbuilder/mylogout");
//	}
//
////==============================================================================
////==============================================================================
////==============================================================================
//
//	JMX jmx = new JMX(ENV);
//
//	Thread.sleep(5000);
//
//	if (log.isInfoEnabled()) log.info("- IMPORT NEW QUALIFIERS FROM BUN DB --------");
//	importNewQualifiers();
//
//	Thread.sleep(5000);
//
//	if (log.isInfoEnabled()) log.info("- REBOOT TAXONOMYLOADER --------------------");
//	jmx.reboot("taxonomyLoader");
//
//	Thread.sleep(5000);
//
//	if (log.isInfoEnabled()) log.info("- WAIT FOR QUALIFIER UPDATER JOB -----------");
//	jmx.reboot("QualifierUpdaterJob");
//
//	Thread.sleep(5000);
//
//	if (log.isInfoEnabled()) log.info("- REBOOT PARTNERS --------------------------");
//	jmx.reboot("partners", envConfigID);
//
//	Thread.sleep(5000);
//
////==============================================================================
////==============================================================================
////==============================================================================
//
//	if (log.isInfoEnabled()) log.info("- CHECK QUALIFIERS -------------------------");
//		/*
//		 * Delete cookies
//		 */
//    	browser.open(PARTNERSINFO_EX_URL);
//		browser.deleteAllVisibleCookies();
//		Thread.sleep(1000);
//
//		String url = QUALIFIER_URL_PREFIX +
//				URLEncoder.encode(QUALIFIER_BASE_URL,"UTF-8");
//		if (log.isDebugEnabled()) log.debug("open: " + url);
//		browser.open(url);
////		browser.open(QUALIFIER_URL_PREFIX +
////				URLEncoder.encode(QUALIFIER_BASE_URL,"UTF-8"));
//		browser.open(PARTNERSINFO_EX_URL);
//
//		String pageContent = browser.getBodyText();
//		if (log.isDebugEnabled()) log.debug("pageContent:\r\n" + pageContent);
//
//		if ( !browser.isTextPresent("Modeled Data->Modeled Gender") ||
//				!browser.isTextPresent("Modeled Data->Modeled Age")) {
//	    	log.error("ERROR: Page content is incorrect");
//		} else {
//			if (log.isInfoEnabled()) log.info("QualifierBuilder test PASSED.");
//		}
//
//		if (log.isInfoEnabled()) log.info("FINISH.");
//	}
//
//	private int getCreatedQalifierID() {
//		String line = browser.getText("xpath=/html/body/div[@id='content']/div[@id='message']");
//		String[] arr = line.split(" ");
//		return Integer.parseInt(arr[3]);
//	}
//
////	private void appendFile ( String filename, String msg ) throws Exception {
////		try {
////    	    File file = new File(filename);
////    	    FileWriter writer = new FileWriter(file, true);
////    	    writer.write(msg);
////    	    writer.flush();
////    	    writer.close();
////    	} catch (Exception e) {
////	    	System.out.println(e.getMessage());
////    	}
////    }
//
//	private String getDBLogin() {
//		return ENV + "_meta";
//	}
//	private String getDBPassword() {
//		return ENV + "_meta";
//	}
//	private Connection dataBaseConnect() {
//	    try {
//	    	Connection connection;
//
//	    	String url = "jdbc:oracle:thin:@" +
//	        	testClassConfigurationLoader.getProperty("dbHost") + ":" +
//	        	testClassConfigurationLoader.getProperty("dbPort") + ":" +
//	        	testClassConfigurationLoader.getProperty("dbSid");
//
//	        Class.forName(driverName);
//	        connection = DriverManager.getConnection(url, getDBLogin(), getDBPassword());
//	        if (log.isDebugEnabled()) log.debug("connecting: " + url);
//	        if(connection.equals(null))
//	    	    return null;
//	        else
//	    	    return connection;
//	    } catch (ClassNotFoundException e) {
//	    	log.error("ClassNotFoundException: " + e.getLocalizedMessage());
//	    } catch (SQLException e) {
//	    	log.error("SQLException: " + e.getMessage());
//	    }
//	    return null;
//	}
//	private void importNewQualifiers () throws Exception {
//		Connection dbConn = dataBaseConnect();
//		if ( dbConn == null  ) {
//			log.error("Data base connection FAILED");
//	        System.exit(1);
//		}
//
//		if (log.isDebugEnabled()) log.debug("Data base connection OK");
//
//		if (log.isInfoEnabled()) log.info("Wait it can take a few minutes");
//
//		try {
//        	CallableStatement cstmt = dbConn.prepareCall("{call BUNUPDATER()}");
//        	try {
//        		cstmt.execute();
//        	} catch (Exception e) {
//        		log.error("Exception: " + e.getMessage());
//        	}
//    		if (log.isInfoEnabled()) log.info("Stored procedure named BunUpdater successfully completed");
//
//	    } catch (Exception e) {
//	    	log.error("Exception:" + e.getMessage());
//	    }
//	}
//
//	private void appendFile ( String filename, long num ) throws Exception {
//		try {
//    	    File file = new File(filename);
//    	    FileWriter writer = new FileWriter(file, true);
//    	    writer.write(String.valueOf(num) + "\r\n");
//    	    writer.flush();
//    	    writer.close();
//    	} catch (Exception e) {
//	    	System.out.println(e.getMessage());
//    	}
//    }
//}
