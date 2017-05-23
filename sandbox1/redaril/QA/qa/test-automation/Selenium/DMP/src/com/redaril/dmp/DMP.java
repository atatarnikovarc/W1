package com.redaril.dmp;

import java.io.File;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

import com.redaril.csv.CsvReader;
import com.thoughtworks.selenium.Selenium;
import org.openqa.selenium.*;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

//for xml
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DMP extends TestCase {
    private Selenium browser;
    private WebDriver driver;
    
    private final static String ENV = "env1";
    private final static String NAME = "valya";
    private final static int Timeout = 60000;

    private static final boolean DEBUG = true;
    
    private String baseAddr = "http://" + ENV + ".dmpui";

    //private final static String REPORTS_PATH = "Resources/Reports/";
    private final static String REPORT_TEMPLATE_PATH = "Resources/ReportTemplates/";
    private final static String REPORT_PROPERTI_PATH = "Resources/ReportProperties/";
    
    @Before
	public void setUp() throws Exception {
		//10.50.150.143
    	//BrowserConfigurationOptions webSec = new BrowserConfigurationOptions();

    	DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
    	ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
    	WebDriver driver = new InternetExplorerDriver(ieCapabilities);

    	//driver = new InternetExplorerDriver();
    	browser = new WebDriverBackedSelenium(driver, baseAddr);

    	//browser2 = new DefaultSelenium("localhost",
    		//	4444, "*iexplore", baseAddr);
    	//browser.start(webSec.setCommandLineFlags("--disable-web-security"));
    	//browser2.start();
        //browser.setTimeout("60000");
    }

	@After
	public void tearDown() throws Exception {
//        browser.close();
//        browser.stop();
	}


	public void test() throws Exception {
		browser.setSpeed("750");
		browser.windowMaximize();
		browser.windowFocus();
    	browser.setTimeout("120000");
		long startTime = 0;
		
    	System.out.println("/* Open page */");
		try {
			browser.open(baseAddr);
					//+ "/login.html");
		} catch (Exception e) {
			System.out.println("Exception: open main page: " + e.getMessage());
		}

		System.out.println("/* Wait while page loading */");
		startTime =  System.currentTimeMillis();
		while ( true ) {

			boolean isTextPresent = false;
			try {
				isTextPresent = browser.isTextPresent("Sign In");
			} catch (Exception e) {
				System.out.println("Exception: browser.isTextPresent(Sign In): " +
						e.getLocalizedMessage());
			}
			//if (browser.isTextPresent("Sign In")) {
			if ( isTextPresent ) {
				break;
			} else if (browser.isTextPresent("Welcome, ")){
				try { browser.click("id=logout"); } catch (Exception e) {}
				//System.out.println("Exception: try to logout: " + e.getMessage());
			} else if ( System.currentTimeMillis() > startTime  + Timeout ) {
		    	System.out.println("ERROR: Page loading Timeout.");
				fail("Timeout");
		    	System.exit(1);
			}
			Thread.sleep(5000);
		}
		

		Thread.sleep(5000);
		System.out.println("/* Authorize */");
		try {
			browser.type("id=name", NAME);
			browser.type("id=password", NAME);
		} catch (Exception e) {
			System.out.println("Exception: Authorize from: " + e.getMessage());
		}
		
		System.out.println("/* Click \"Sign In\" and Wait while page loading */");
		startTime =  System.currentTimeMillis();
		while ( true ) {
			
			/* click "Sign in" button */
//			if (browser.isElementPresent("xpath=/html/body//button[@type='button']")) {
//				try {
//					System.out.println("DEBUG: Click \"Sign In\"");
//					browser.focus("xpath=/html/body//button[@type='button']");
//					//browser.click("xpath=/html/body//button[@type='button']");
//					Thread.sleep(500);
//					browser.mouseDown("xpath=/html/body//button[@type='button']");
//					Thread.sleep(500);
//					browser.mouseUp("xpath=/html/body//button[@type='button']");
//					Thread.sleep(500);
//				} catch (Exception e) {
////					System.out.println("Exception: Click \"Sign In\" button: " +
////							e.getMessage());
//				}
//			}
			
			try {
//				browser.focus("xpath=/html/body//button");
//				browser.mouseOver("xpath=/html/body//button");
//				browser.click("xpath=/html/body//button");
//				Thread.sleep(1000);
//				browser.focus("xpath=/html/body//button");
//				browser.mouseOver("xpath=/html/body//button");
				browser.keyPress("xpath=/html/body//button", "\\13");
			} catch ( Exception e ) {}
			
			/* Check logon user */
			if (browser.isTextPresent("Welcome, " + NAME)){
	        	System.out.println("DEBUG: \"Sign In\" successfull");
				break;
			}
			
			if ( System.currentTimeMillis() > startTime  + Timeout ) {
		    	System.out.println("ERROR: Page loading Timeout.");
				fail("Timeout");
		    	System.exit(1);
			}
			
			Thread.sleep(2000);
		}

		int ErrorsCount = 0;
		
//		if (!openPageFromHeader("Analytics", "Analytics Reports List", "Analytics Reports"))
//			ErrorsCount++;
		
////////////////////////////////////////////////////////////////////////////////
// PROCESS XML TAMPLATES ///////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
//TODO:
		
////		// get xml templates list
////		File dir = new File(REPORTS_PATH);
////		String[] list = dir.list();
////			    if (list.length == 0) {
////			    	System.out.println("Error. Have no .xml at " + REPORTS_PATH);
////			    	System.exit(1);
////			    }
////
////		// process xml templates
////	    File file;
////	    for (int i = 0; i < list.length; i++) {
////	    	file = new File(REPORTS_PATH + list[i]);
////
////	    	System.out.println("/* Start */");
////			if ( !createReport(file.getAbsolutePath()) ) {
////				System.out.println("NO");
////			} else {
////				System.out.println("YES");
////			}
////	    	System.out.println("/* Finish */");
////	    }
//		
//		// get xml templates list
//		File dir = new File(REPORT_PROPERTI_PATH);
//		String[] propertiFilesList = dir.list();
//			    if (propertiFilesList.length == 0) {
//			    	System.out.println("Error. Have no .properties at " + REPORT_PROPERTI_PATH);
//			    	System.exit(1);
//			    }
//
//		// process xml templates
//	    File file;
//	    for (int i = 0; i < propertiFilesList.length; i++) {
//	    	file = new File(REPORT_PROPERTI_PATH + propertiFilesList[i]);
//
//	    	System.out.println("/* Start */");
//			if ( !createReport(file.getAbsolutePath()) ) {
//				System.out.println("NO");
//			} else {
//				System.out.println("YES");
//			}
//	    	System.out.println("/* Finish */");
//	    	Thread.sleep(5000);
//	    }
		
		File dir = new File(REPORT_PROPERTI_PATH);
		String[] propertiFilesList = dir.list();
			    if (propertiFilesList.length == 0) {
			    	System.out.println("Error. Have no properties file at " + REPORT_PROPERTI_PATH);
			    	System.exit(1);
			    }

	    File propertiesFile;
	    File templateFile;
	    for (int i = 0; i < propertiFilesList.length; i++) {
	    	propertiesFile = new File(REPORT_PROPERTI_PATH + propertiFilesList[i]);
	    	templateFile = new File(REPORT_TEMPLATE_PATH + propertiesFile.getName() + ".xml");

	    	if ( propertiesFile.exists() && templateFile.exists() ) {
	    		
		    	System.out.println("/* Start */");
				if ( !createReport(propertiesFile, templateFile) ) {
					System.out.println("NO");
				} else {
					System.out.println("YES");
				}
		    	System.out.println("/* Finish */");
		    	Thread.sleep(5000);
	    		

	    	}
	    }
	    	
		System.exit(1);

		if (!openPageFromHeader("Analytics", "New Audience Categories Report", "Create Audience Categories"))
			ErrorsCount++;
		
		if (!openPageFromHeader("Analytics", "New Active Audience Segments Report", "Create Active Audience Segments"))
			ErrorsCount++;
		
		if (!openPageFromHeader("Analytics", "New Audience by Channel Report", "Create Audience by Channel"))
			ErrorsCount++;
		
		if (!openPageFromHeader("Analytics", "New Segment Forecast Report", "Create Segment Forecast"))
			ErrorsCount++;
		
		if (!openPageFromHeader("Analytics", "New Audience Segment Overlap Report", "Create Audience Segment Overlap"))
			ErrorsCount++;
		
		if (!openPageFromHeader("Analytics", "New Cross Categories Report", "Create Cross Categories"))
			ErrorsCount++;
		
		if (!openPageFromHeader("Data Usage", "Data Usage Reports List", "Data Usage Reports"))
			ErrorsCount++;
		
		if (!openPageFromHeader("Data Usage", "New Data Usage Report", "Create Data Usage"))
			ErrorsCount++;
		
		if (!openPageFromHeader("Audience Activation", "Data Campaigns", "Data Campaigns"))
			ErrorsCount++;
		
		if (!openPageFromHeader("Audience Activation", "Audience Segments", "Audience Segments"))
			ErrorsCount++;
				
		if (!openPageFromHeader("Tag Management", "Tags", "My Tags"))
			ErrorsCount++;
		
		if (!openPageFromHeader("Tag Management", "Piggyback Rules", "Piggyback Rules"))
			ErrorsCount++;
		
		if (!openPageFromHeader("", "Admin users", "Users Manager"))
			ErrorsCount++;

		
		if ( ErrorsCount > 0 ) {
			System.out.println("\r\nERROR: " + ErrorsCount + " page(s) did`y load correctly.");
			fail("ERROR: " + ErrorsCount + " page(s) did`y load correctly.");
		} else {
			System.out.println("\r\nFINISH: All pages was opened.");
		}

        Thread.sleep(5000);
	}

	private boolean createReport ( File propertiesFile, File templateFile ) throws Exception {
		boolean result = true;

		CsvReader report = new CsvReader(propertiesFile.getAbsolutePath());
    	report.readHeaders();

    	NodeList nodeList = null;
		Node node = null;
		Element control = null;
		String reportName = null;
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(templateFile.getAbsolutePath());
        document.getDocumentElement().normalize();

        // get report name from .xml template file
		nodeList = document.getElementsByTagName("report");
        node = nodeList.item(0);
        control = (Element)node;
        reportName = control.getAttribute("name");
        nodeList = null;
		node = null;

		// get all controls from .xml template file
		nodeList = document.getElementsByTagName("control");
        
		// read all lines from properties file
		while (report.readRecord()) {

	        if (!openPageFromHeader("Analytics", "New " + reportName,
	        		"Create " + reportName)) {
				return false;
			} else {
				System.out.println("/* Fill the report form */");
				//inspect all controls from template
		        for(int tmp = 0; tmp < nodeList.getLength(); tmp++) {
		            node = nodeList.item(tmp);
		            if(node.getNodeType() == Node.ELEMENT_NODE) {
		                control = (Element)node;
		                
		                if (DEBUG) System.out.println("\r\n" +
		                		"DEBUG: control name: " + control.getAttribute("name") + "\r\n" +
		                		"DEBUG: control type: " + control.getAttribute("type") + "\r\n" +
		                		"DEBUG: control id: " + control.getAttribute("id") + "\r\n" +
		                		"DEBUG: control val: " + report.get(control.getAttribute("name")));
		                
		                if ( control.getAttribute("type").equals("checkbox") ) {
		        			setCheckBoxState(control.getAttribute("id"),
		        					Boolean.parseBoolean(report.get(control.getAttribute("name"))));
		                } else if ( control.getAttribute("type").equals("combobox") ) {
		                	setOptions(control.getAttribute("id"),
		                			report.get(control.getAttribute("name")));
		                } else if ( control.getAttribute("type").equals("textbox") ) {
		                	browser.type(control.getAttribute("id"),
		                			report.get(control.getAttribute("name")));
		                } else {
		                	
		                }
		            }
		        }
			}
			browser.click("xpath=/html/body//a[@class='saveButton btn btn-primary' and contains(.,'Save')]");
        }
        report.close();
		return result;
	}


	private boolean openPageFromHeader (String span, String link, String pageContent) throws Exception {

		Thread.sleep(10000);
		boolean result = false;
		System.out.println("/* Open \"" + link + "\" */");

		//TODO:
		while ( true ) {
			
			while ( true ) {
				try {
					if ( !pageContent.equals("Users Manager") )
						browser.click("xpath=/html/body//span[contains(., '" + span + "')]");
					browser.click("link=" + link);
					break;
				} catch (Exception e) {}
				Thread.sleep(1000);
			}
		
			System.out.println("/* Wait while page loading */");
			long startTime = System.currentTimeMillis(); 
			while ( true ) {
				if (browser.isTextPresent(pageContent)){
		        	if (DEBUG) System.out.println("DEBUG: Page content \"" + pageContent + "\" OK");
		        	result = true;
					break;
				} else if ( System.currentTimeMillis() > startTime  + Timeout ) {
			    	System.out.println("ERROR: checkPage: \"" + pageContent + "\" page loading Timeout.");
			    	result = false;
				}
				Thread.sleep(1000);
			}
			
			if ( result ) break;
		}
		return result;
	}
	
	private void click ( String id ) throws Exception {
			try {
				if (DEBUG) System.out.println("DEBUG: Click at " +
						"locator = \"" + id + "\";");
				
				browser.click("id=" + id);
				Thread.sleep(1000);
			} catch (Exception e) {
				System.out.println("ERROR: Click: Can`t click at " +
						"locator = \"" + id + "\";\r\n" +
						"Exception: " + e.getMessage());
		    	System.exit(1);
			}
		Thread.sleep(1000);
	}

	private void setCheckBoxState ( String id, boolean orderedState ) throws Exception {

		if ( id.isEmpty() ) {
			System.out.println("ERROR: Can`t get ID for " +
					"locator = \"" + id + "\";");
	    	System.exit(1);
		}
		
		if ( !browser.isEditable(id) ) {
			System.out.println("INFO: Control \"" + id + "\" is disabled.");
		} else {
			
			int clickFailCount = 0;
			int CLICK_COUNT_RANGE = 5;
			boolean startState = browser.isChecked(id);
			boolean currentState = false;
			
			if (DEBUG) System.out.println("orderedState= " + orderedState + "\r\n" +
					"startState= " + startState);

			if ( orderedState == startState ) {
				
				clickFailCount = 0;
				while ( true ) {
					currentState = browser.isChecked(id);
					if (DEBUG) System.out.println("startState= " + startState + "\r\n" +
							"currentState= " + currentState);
					click(id);
					currentState = browser.isChecked(id);
					if (DEBUG) System.out.println("startState= " + startState + "\r\n" +
							"currentState= " + currentState);
					if ( currentState == startState ) {
						clickFailCount++;
						if (DEBUG) System.out.println("click fail count");
					} else {
						if (DEBUG) System.out.println("break");
						break;
					}
					if ( clickFailCount == CLICK_COUNT_RANGE ) {
						System.out.println("ERROR: Can`t change check box state by click");
				    	System.exit(1);
					}
					Thread.sleep(1000);
				}
				
				startState = browser.isChecked(id);
				clickFailCount = 0;
				while ( true ) {
					currentState = browser.isChecked(id);
					if (DEBUG) System.out.println("startState= " + startState + "\r\n" +
							"currentState= " + currentState);
					click(id);
					currentState = browser.isChecked(id);
					if (DEBUG) System.out.println("startState= " + startState + "\r\n" +
							"currentState= " + currentState);
					if ( currentState == startState ) {
						clickFailCount++;
					} else {
						break;
					}
					if ( clickFailCount == CLICK_COUNT_RANGE ) {
						System.out.println("ERROR: Can`t change check box state by click");
				    	System.exit(1);
					}
					Thread.sleep(1000);
				}
				if ( currentState == orderedState ) {
					if (DEBUG) System.out.println("DEBUG: Check box \"" + id + "\" " +
							"works good.");
				} else {
					System.out.println("ERROR: Can`t change check box state by click");
			    	System.exit(1);
				}
			} else {
				clickFailCount = 0;
				while ( true ) {
					click(id);
					currentState = browser.isChecked(id);
					if ( currentState == startState ) {
						clickFailCount++;
					} else {
						break;
					}
					if ( clickFailCount == CLICK_COUNT_RANGE ) {
						System.out.println("ERROR: Can`t change check box state by click");
				    	System.exit(1);
					}
					Thread.sleep(1000);
				}
				if ( currentState == orderedState) {
					if (DEBUG) System.out.println("DEBUG: Check box \"" + id + "\" " +
							"works good.");
				} else {
					System.out.println("ERROR: Can`t change check box state by click");
			    	System.exit(1);
				}
			}
		}// if not disabled
	}
	
	private void setOptions (  String id, String value ) throws Exception {

		if ( !browser.isVisible(id)) {
			System.out.println("INFO: Control \"" + id + "\" is disabled.");
		} else {
		
			click(id);
			
			String locator = null;
			String locatorSelected = null;
			
			if ( value.contains("checkbox-all-") ) {
				locator = value;
			} else {
				locator = "xpath=/html/body" +
					"//div[@class='x-combo-list-item' and contains(.,'" + value + "')]";
				locatorSelected = "xpath=/html/body" +
					"//div[@class='x-combo-list-item x-combo-selected' and contains(.,'" + value + "')]";
			}
			
			if ( id.contains("startDate") ) {
	//			locator = "xpath=/html/body/div[@id='ext-comp-1030']" +
	//					"//td[@class='x-date-active']//span[contains(.,'" + value + "')]";
	//			locator = "xpath=/html/body//div[@id='ext-comp-1030']" +
	//			"//a[@class='x-date-date' and contains(.,'" + value + "')]";
	//			locator = "xpath=/html/body//div[@id='ext-comp-1030']" +
	//					"//span[contains(.,'" + value + "')]";
	//			locator = "xpath=/html//div[@id='ext-comp-1030']" +
	//					"//table[contains(.,'S')]/tbody/tr[2]/td/table/tbody/tr[1]/td[1]/a";
	//			locator = "xpath=/html//div[@id='ext-comp-1030']" +
	//					"//table[count(thead)=1]//tr/td[@class != 'x-date-disabled']" +
	//					"//.[text()='" + value + "']";
	//			locator = "xpath=/html//div[@id='ext-comp-1030']" +
	//					"//table[contains(.,'S')]//tr/td[@class != 'x-date-disabled']" +
	//					"//.[text()='" + value + "']";
	//			locator = "xpath=/html/body//div[@class='x-date-picker x-unselectable']" +
	//					"//table[count(thead)=1]//tr/td[@class != 'x-date-disabled']" +
	//					"//a//.[text()='" + value + "']";
				
				locator = "xpath=/html/body//div[contains(@style,'visibility: visible')]" +
						"//div[@class='x-date-picker x-unselectable']//table[count(thead)=1]" +
						"//tr/td[@class != 'x-date-disabled']//a//.[text()='" + value + "']";
				
	//			String[] links = browser.getAllLinks();
	//			if ( links.length > 0 ) {
	//				for (int i = 0; i < links.length; i++ ) 
	//					System.out.println("DEBUG: links[" + i + "]= " + links[i]); 
	//			}
	
			} else if ( id.contains("endDate") ) {
	//			locator = "xpath=/html/body//div[@id='ext-comp-1034']" +
	//					"//a[@class='x-date-date' and contains(.,'" + value + "')]";
	//			locator = "xpath=/html/body//div[@id='ext-comp-1034']" +
	////					"//span[contains(.,'" + value + "')]";
	//			locator = "xpath=/html//div[@id='ext-comp-1034']" +
	//					"//table[count(thead)=1]//tr/td[@class != 'x-date-disabled']" +
	//					"//.[text()='" + value + "']";
				
				locator = "xpath=/html/body//div[contains(@style,'visibility: visible')]" +
						"//div[@class='x-date-picker x-unselectable']//table[count(thead)=1]" +
						"//tr/td[@class != 'x-date-disabled']//a//.[text()='" + value + "']";			
			}
			
			if (DEBUG) System.out.println("DEBUG: Locator= " + locator);
			
			if ( browser.isElementPresent(locator)) {
				//System.out.println("isElementPresent(locator) " + browser.isElementPresent(locator));
				try {
					if (DEBUG) System.out.println("DEBUG: Click at " +
							"locator = \"" + locator + "\";");
					browser.click(locator);
				} catch (Exception e) {
					System.out.println("ERROR: Can`t click at " +
							"locator = \"" + locator + "\";\r\n" +
							"Exception: " + e.getMessage());
			    	System.exit(1);
				}
			} else if ( browser.isElementPresent(locatorSelected)) {
				//System.out.println("isElementPresent(locatorSelected) " + browser.isElementPresent(locatorSelected));
				try {
					if (DEBUG) System.out.println("DEBUG: Click at " +
							"locatorSelected = \"" + locatorSelected + "\";");
					browser.click(locatorSelected);
				} catch (Exception e) {
					System.out.println("ERROR: Can`t click at " +
							"locatorSelected = \"" + locatorSelected + "\";\r\n" +
							"Exception: " + e.getMessage());
			    	System.exit(1);
				}
			} else {
				System.out.println("ERROR: Can`t find locator = \"" + locator + "\"; " +
						"value = \"" + value + "\";");
		    	System.exit(1);
			}
		}// if is editable
	}
}
