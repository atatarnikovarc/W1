package com.redaril.JIRA2764;

//import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import junit.framework.TestCase;
import java.lang.String;
import java.util.Calendar;
import java.util.Date;
import java.io.*;

public class MakePixel extends TestCase {
    
	private Selenium browser;
    private static final String DATA_PIXEL_NAME_PREFIX =	"testDataPixelID_";
    private static final String LAST_PIXEL_ID_FILENAME =	"makepixel_lastPixelID.log";
    private static final String LOG_FILENAME =				"makepixel.log";
    private static final String DB_HOST =					Config.getProperty("DB_HOST");
    private static final String DB_LOGIN =					Config.getProperty("DB_LOGIN");
    private static final String DB_PSWRD =					Config.getProperty("DB_PSWRD");
    private static final String DB_SID =					Config.getProperty("DB_SID");
    private String HTDOCS_HTML_PATH;
    private String TESTSITE_ADDR;
    private String ENV;
    private String CLUSTER;
    
    MakePixel ( Selenium browserFromMain,
    		String env,
    		String cluster,
    		String htmlFolder) {
    	HTDOCS_HTML_PATH = htmlFolder;
    	ENV = env;
    	CLUSTER = cluster;
    	browser = browserFromMain;
    	TESTSITE_ADDR = "http://" + ENV + ".testsite:8080/testsite/partners/login.jsp";
    	
    	try {
	    	File dir = new File(HTDOCS_HTML_PATH);
	    	String[] list = dir.list();
		    if (list.length != 0) {
			    File file;
			    for (int i = 0; i < list.length; i++) {
			    	file = new File(HTDOCS_HTML_PATH + list[i]);
			    	file.delete();
			    }
		    }
    	} catch (Exception e) {
    		System.out.println("Folder [" + HTDOCS_HTML_PATH + "] is empty.");
    	}
    	//System.exit(1);
    	browser.open(TESTSITE_ADDR);
    }
    
	private void login(String action) throws Exception {
        System.out.println("LOGIN");
		try {
	    	System.out.println("input ipdb");
			browser.type("xpath=/html//body/form/center//input[@name='ipdb']", DB_HOST);
        } catch (Exception e) {
	    	System.out.println(e.getMessage());
        }
		try {
	    	System.out.println("input sid");
			browser.type("xpath=/html//body/form/center//input[@name='sid']", DB_SID);
        } catch (Exception e) {
	    	System.out.println(e.getMessage());
        }
		try {
	    	System.out.println("input user");
			browser.type("xpath=/html//body/form/center//input[@name='user']", DB_LOGIN);
        } catch (Exception e) {
	    	System.out.println(e.getMessage());
        }
		try {
	    	System.out.println("input password");
			browser.type("xpath=/html//body/form/center//input[@name='pass']", DB_PSWRD);
        } catch (Exception e) {
	    	System.out.println(e.getMessage());
        }
		try {
	    	System.out.println("select action use '" + action + "'");
	    	browserSelect("name=action", "label=" + action);
        } catch (Exception e) {
	    	System.out.println(e.getMessage());
        }
		try {
	    	System.out.println("click ");
	    	browser.click("xpath=/html//body/form/center//input[@type='submit'  and contains(@text, 'Login')]");
	    } catch (Exception e) {
	    	System.out.println(e.getMessage());
        }
        
        System.out.println("wait while page loading");
        waitForPageLoadingText("Create New");
	}

	private void logout() throws Exception {
        System.out.println("LOGOUT");
		try {
	    	System.out.println("click ");
	    	browser.click("xpath=/html/body//a[contains(., 'EXIT')]");
	    } catch (Exception e) {
	    	System.out.println(e.getMessage());
        }
        
        System.out.println("wait while page loading");
        waitForPageLoadingText("Login form");
	}
	
	private void clickCreateNewPixel() throws Exception {
        System.out.println("click 'Create New'");
        try {
        	browser.click("xpath=/html/body/a[contains(., 'Create New')]");
	    } catch (Exception e) {
	    	System.out.println(e.getMessage());
	    }
        System.out.println("wait while page loading");
        waitForPageLoadingElement("xpath=/html//body/form//input[@type='submit' and @value='save']");
	}
	
	private void clickSave() throws Exception {
        try {
        	System.out.println("Click [SAVE] button");
        	browser.click("xpath=/html//body/form//input[@type='submit' and @value='save']");
	    } catch (Exception e) {
	    	System.out.println(e.getMessage());
	    }
        System.out.println("wait while page loading");
        waitForPageLoadingText("saved successfully");
	}
	
	private void fillFormDataPixel (
			//common
			String pixelNumber, String dataPlatform,
			String isActive, String isHttps, String maxPiggybacks, String ruleSelection,
			//dataPixel
			String dataPixelIsDataEnabler, String dataPixelIsDataConsumer,
			String dataPixelServerURL, String dataPixelFormat
			) throws Exception {
    	
		makeDataPixelHTMLTemplate(pixelNumber, dataPixelFormat);
		
		System.out.println("DATA PIXEL. fill the form");
		browser.type("name=id", pixelNumber);
        	System.out.println("id= " + pixelNumber);
		browser.type("name=name", DATA_PIXEL_NAME_PREFIX + pixelNumber);
        	System.out.println("name= "+ DATA_PIXEL_NAME_PREFIX + pixelNumber);
		browserSelect("name=dataPlatform", "label=" + dataPlatform);
        	System.out.println("dataPlatform= " + dataPlatform);
		browserSelect("name=isActive", "label=" + isActive);
        	System.out.println("isActive= " + isActive);
		browserSelect("name=isHttps", "label=" + isHttps);
        	System.out.println("isHttps= " + isHttps);
		browserSelect("name=isDataEnabler", "label=" + dataPixelIsDataEnabler);
        	System.out.println("isDataEnabler= " + dataPixelIsDataEnabler);
		browserSelect("name=isDataConsumer", "label=" + dataPixelIsDataConsumer);
        	System.out.println("isDataConsumer= " + dataPixelIsDataConsumer);
		browser.type("name=dataServerURL", dataPixelServerURL);
        	System.out.println("dataServerURL= " + dataPixelServerURL);
		browser.type("name=outputDescr", "outputDescr_" + DATA_PIXEL_NAME_PREFIX + pixelNumber);
        	System.out.println("outputDescr= outputDescr_" + DATA_PIXEL_NAME_PREFIX + pixelNumber);
		//browser.type("name=spotIds", "spotIds_testPixelID_" + pixelNumber);
        //	System.out.println("spotIds= spotIds_testPixelID_" + pixelNumber);
		browser.type("name=maxPiggybacks", maxPiggybacks);
        	System.out.println("maxPiggybacks= " + maxPiggybacks);
   		browserSelect("name=pixelFormat", "label=" + dataPixelFormat);
        	System.out.println("pixelFormat= " + dataPixelFormat);
       	this.clickSave();
	}
	
	private void makeDataPixelHTMLTemplate (
			String pixelNumber, String pixelFormat
			) throws Exception {
    	try {
    		File path = new File(HTDOCS_HTML_PATH);
    		path.mkdirs();
	    	if (pixelFormat.equals("I")) {
	    		FileOutputStream fos = new FileOutputStream(HTDOCS_HTML_PATH +
	    				pixelNumber + "_" + pixelFormat + ".html");
		    	PrintWriter DataPixelTemplateFile = new PrintWriter(fos);
		    	DataPixelTemplateFile.println(
		    			"<HTML>\r\n" +
		    			"<HEAD>\r\n" +
		    			"<TITLE>Image Pixel</TITLE>\r\n" +
		    			"</HEAD>\r\n" +
		    			"<BODY>\r\n" +
		    			"<h1>Image Pixel part id=" + pixelNumber + "</h1>\r\n" +
		    			"<img width=\"0\" height=\"0\" border=\"0\" " +
		    			"src=\"http://" + ENV + "." + CLUSTER + ".p.raasnet.com:8080/partners/universal/in?pid=" + pixelNumber + "&t=i\"/>\r\n" +
		    			"</BODY>\r\n" +
		    			"</HTML>");
		    	DataPixelTemplateFile.close();
	    	} else {
	    		FileOutputStream fos = new FileOutputStream(HTDOCS_HTML_PATH +
	    				pixelNumber + "_" + pixelFormat);
		    	PrintWriter DataPixelTemplateFile = new PrintWriter(fos);
		    	DataPixelTemplateFile.println(pixelNumber + "_" + pixelFormat);
		    	DataPixelTemplateFile.close();
	    	}
    	} catch (Exception e) {
	    	System.out.println(e.getMessage());
    	}		
	}
	
	private void clickCreateNewRule(String ruleType) throws Exception {
		
		System.out.println("click '" + ruleType + "'");
        
		try {
        	browser.click("xpath=/html/body//a[contains(., '" + ruleType + "')]");
	    } catch (Exception e) {
	    	System.out.println(e.getMessage());
	    }
        System.out.println("wait while page loading");
        waitForPageLoadingElement("xpath=/html/body/span//a[@class='tabs selected' and contains(., '" + ruleType + "')]");        
        
		System.out.println("click 'Create New'");
		
        try {
        	browser.click("xpath=/html/body//a[contains(., 'Create New')]");
	    } catch (Exception e) {
	    	System.out.println(e.getMessage());
	    }
        System.out.println("wait while page loading");
        waitForPageLoadingElement("xpath=/html//body/form//input[@type='submit' and @value='save']");        
        
	}
	
	private void createRuleConstraints(String pixelID) throws Exception {
		this.clickCreateNewRule("Constraints");
			browserSelect("name=partnerType", "label=Universal");
				System.out.println("partnerType= Universal");
			browserSelect("name=fieldLabel", "label=If pixel id");
	    		System.out.println("fieldLabel= If pixel id");
			browserSelect("name=operator", "label==");
	    		System.out.println("operator= =");
			browser.type("name=fieldValue", pixelID);
	    		System.out.println("fieldValue= " + pixelID);
    	this.clickSave();
	}
	
	private void createRuleSuperConstraints(String pixelID) throws Exception {
		this.clickCreateNewRule("Super constraints");
			browser.type("name=label", "pixel_" + pixelID + "_SuperConstraintsRule");
				System.out.println("label= pixel_" + pixelID + "_SuperConstraintsRule");
			browserSelect("name=partnerType", "label=Universal");
	    		System.out.println("partnerType= Universal");

	    	Thread.sleep(5000);

	    	browserSelect("name=choices", "label=Universal: partnerId = " + pixelID);
					System.out.println("choices= Universal: partnerId = " + pixelID);
    	this.clickSave();
		
	}

	private void createRulePiggybacks(String pixelID, String dataServerURL) throws Exception {
		this.clickCreateNewRule("Piggybacks");
			browser.type("name=label", "pixel_" + pixelID + "_PiggybacksRule");
				System.out.println("label= pixel_" + pixelID + "_PiggybacksRule");
			browser.type("name=url", dataServerURL);
				System.out.println("url= " + dataServerURL);
			browser.type("name=weight", "99");
				System.out.println("weight= 99");
			browser.click("name=enabled");
				System.out.println("click isEnabled");
			browser.click("name=isCycled");
				System.out.println("click isCycled");
			browser.click("name=isUnlim");
				System.out.println("click isUnlim");
			browser.type("name=duration", "100");
				System.out.println("duration= 100");
				
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			int iMonth = calendar.get(Calendar.MONTH);
			iMonth++;
			String sDate = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "." + 
			String.valueOf(iMonth) + "." +
			String.valueOf(calendar.get(Calendar.YEAR));
			browser.type("name=startDate:date", sDate);
				System.out.println("startDate:date= " + sDate);
			browser.type("startDate:hours", "00");
				System.out.println("startDate:hours= 00");
			browser.type("startDate:minutes", "01");
				System.out.println("startDate:minutes= 01");
				
			int iYear = calendar.get(Calendar.YEAR);
			iYear++;
			sDate = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "." + 
			String.valueOf(iMonth) + "." +
			String.valueOf(iYear);
			browser.type("name=endDate:date", sDate);
				System.out.println("endDate:date= " + sDate);
			browser.type("endDate:hours", "00");
				System.out.println("endDate:hours= 00");
			browser.type("endDate:minutes", "01");
				System.out.println("endDate:minutes= 01");
				
			browser.type("description", "pixel_" + pixelID + "_PiggybacksRuleDescription");
				System.out.println("description= pixel_" + pixelID + "_PiggybacksRuleDescription");
    	this.clickSave();
	}
	
	private void createRule(String pixelID, String ruleSelection) throws Exception {
		this.clickCreateNewRule("Rules");
			browser.type("name=label", "pixel_" + pixelID + "_Rule");
				System.out.println("label= pixel_" + pixelID + "_Rule");
			browserSelect("name=ruletype", "label=" + ruleSelection);
				System.out.println("ruletype= " + ruleSelection);
	
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			int iYear = calendar.get(Calendar.YEAR);
			iYear++;
			int iMonth = calendar.get(Calendar.MONTH);
			iMonth++;
			String sDate = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "." + 
			String.valueOf(iMonth) + "." +
			String.valueOf(iYear);
			browser.type("name=endDate:date", sDate);
				System.out.println("endDate:date= " + sDate);
			browser.type("endDate:hours", "00");
				System.out.println("endDate:hours= 00");
			browser.type("endDate:minutes", "01");
				System.out.println("endDate:minutes= 01");
			browser.click("name=enabled");
				System.out.println("click Enabled");
	
			Thread.sleep(5000);
				
			browserSelect("name=choices", "label=pixel_" + pixelID + "_SuperConstraintsRule");
				System.out.println("choices= pixel_" + pixelID + "_SuperConstraintsRule");
			if ( ruleSelection == "Piggyback" ) {
				browserSelect("name=data", "label=pixel_" + pixelID + "_PiggybacksRule");
					System.out.println("data= pixel_" + pixelID + "_PiggybacksRule");
			} else if ( ruleSelection == "Retargeting" ) {
				browserSelect("name=data", "label=pixel_" + pixelID + "_RetargetingRule");
					System.out.println("data= pixel_" + pixelID + "_RetargetingRule");
			}
		this.clickSave();
	}
	
	public void makePixel(
		String loginActionPixel, String pixelID, String dataPlatform,
		String isActive, String isHttps, String maxPiggybacks, String ruleSelection, String dataPixelServerURL,
		String dataPixelIsDataEnabler, String dataPixelIsDataConsumer, String dataPixelFormat
		) throws Exception {
        
		String msg = "\r\n" +
				"-------------------------------------------------------------------------------\r\n" +
				"MAKE " + loginActionPixel + ".\r\n" +
				"id= " + pixelID + ";\r\n" +
				"pixel params:\r\n" +
				"dataPlatform= " + dataPlatform + ";\r\n" +
				"isActive= " + isActive + ";\r\n" +
				"isHttps= " + isHttps + ";\r\n" +
				"dataPixelFormat= " + dataPixelFormat + ";\r\n" +
				"maxPiggybacks= " + maxPiggybacks + ";\r\n" +
				"ruleSelection= " + ruleSelection + ";\r\n" +
				"dataPixelIsDataEnabler= " + dataPixelIsDataEnabler + ";\r\n" +
				"dataPixelIsDataConsumer= " + dataPixelIsDataConsumer + ";\r\n" +
				"dataPixelServerURL= " + dataPixelServerURL + ";\r\n";
		logWrite(msg);
		System.out.println(msg);

		/*
         * Create Pixel
         */
		this.login( loginActionPixel );
        this.clickCreateNewPixel();

        if ( loginActionPixel == "DATA PIXEL" ) {
        	this.fillFormDataPixel(pixelID, dataPlatform, isActive, isHttps, maxPiggybacks, ruleSelection,
        			dataPixelIsDataEnabler, dataPixelIsDataConsumer, dataPixelServerURL, dataPixelFormat);
        	
        } else {
			System.out.println("ERROR in loginAction\r\n" +
					ruleSelection + " must be DATA PIXEL or CAMPAIGN PIXEL");
        }
        this.setLastPixelID(Integer.parseInt(pixelID));
        this.logout();
        /*
         * Create Rule for Pixel
         */
        this.login( "RULES" );
        this.createRuleConstraints( pixelID );
        this.createRuleSuperConstraints( pixelID );

        if ( ruleSelection == "Piggyback" ) {
            this.createRulePiggybacks( pixelID, dataPixelServerURL );
        } else {
			System.out.println("ERROR in ruleSelection\r\n" +
					ruleSelection + " must be Piggybacks");
        }
        
        this.createRule( pixelID, ruleSelection );
        this.logout();
	}
	
	private void logWrite ( String msg ) throws Exception {
		try {
	    	File file = new File(LOG_FILENAME);
	    	FileWriter writer = new FileWriter(file, true);
		    writer.write(msg);
		    writer.flush();
		    writer.close();
	    } catch (Exception e) {
	    	System.out.println(e.getMessage());
	    }
	}
	
	public int checkLastPixelID ( int pixelID ) throws Exception {
		File f1 = new File(LAST_PIXEL_ID_FILENAME);
		if (f1.exists()) {
			BufferedReader bufferedReader = null;
			bufferedReader = new BufferedReader(new FileReader(LAST_PIXEL_ID_FILENAME));
			return Integer.parseInt(bufferedReader.readLine());
		} else {
			setLastPixelID(pixelID);
			return pixelID;
		}
	}
	
	private void setLastPixelID ( int pixelID ) throws Exception {
    	try {
	    	FileOutputStream fos = new FileOutputStream(LAST_PIXEL_ID_FILENAME);
	    	PrintWriter lastPixelIDFile = new PrintWriter(fos);
	    	lastPixelIDFile.println(pixelID);
	    	lastPixelIDFile.close();
    	} catch (Exception e) {
	    	System.out.println(e.getMessage());
    	}
    }

	private void browserSelect ( String locator, String value ) throws Exception {
		int count = 0;
		while (true) {
			try {
		    	browser.select(locator, value);
		    	break;
		    } catch (Exception e) {
		    	System.out.println(e.getMessage());
	        }
			Thread.sleep(500);
			if ( count == 100 ) {
				System.out.println("ERROR: Something wrong. Can`t use element: " + locator);
				System.exit(1);
			}
			count++;
		}
	}
	
	private void waitForPageLoadingText(String locator) {
	    long start =  System.currentTimeMillis();
	    while (System.currentTimeMillis() < start  + 60000) {
	        try { if (browser.isTextPresent(locator)) return; } catch  (Exception e) {}
	        try { Thread.sleep(1000); } catch  (InterruptedException e) { throw new AssertionError(e); }
	    }
	    throw  new AssertionError("timeout");
	}

	private void waitForPageLoadingElement(String locator) {
		long start =  System.currentTimeMillis();
	    while (System.currentTimeMillis() < start  + 60000) {
	        try { if (browser.isElementPresent(locator)) return; } catch  (Exception e) {}
	        try { Thread.sleep(1000); } catch  (InterruptedException e) { throw new AssertionError(e); }
	    }
	    throw  new AssertionError("timeout");
	}

}