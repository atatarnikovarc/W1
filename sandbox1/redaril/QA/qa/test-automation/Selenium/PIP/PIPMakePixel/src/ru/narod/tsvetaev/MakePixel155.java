package ru.narod.tsvetaev;

import org.junit.After;
import org.junit.Before;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import junit.framework.TestCase;
import java.lang.String;
import java.util.Calendar;
import java.util.Date;
import java.io.*;

public class MakePixel155 extends TestCase {
    private Selenium browser;
    private String addr = "http://10.50.150.155:8080/testsite/partners/login.jsp";
    private static String DataPixelPrefix = "testDataPixelID_";
    private static String CampaignPixelPrefix = "testCampaignPixelID_";
    private static String lastPixelIDFileName = "makepixel155_lastPixelID.txt";    
    private static String logFileName = "makepixel155_log.txt";    
    private static String DBLogin = "qacluster4_meta";    
    private static String DBPswd = "qacluster4_meta";   
    
	@Before
	public void setUp() throws Exception {
        //10.50.150.143
		browser = new DefaultSelenium("localhost",
    			4444, "*firefox", addr);
            browser.start();
            browser.setTimeout("60000");
	}

	@After
	public void tearDown() throws Exception {
        //browser.close();
        //browser.stop();
	}

	public void test() throws Exception {
		browser.setSpeed("750");
		browser.windowMaximize();
		browser.windowFocus();
		//System.out.println("Hello, World!");
		try {
			browser.open(addr);
        } catch (Exception e) {
            e.printStackTrace();
	    	System.out.println(e.getMessage());
    		browser.stop();
     		System.exit(1);
        }

////////////////////////////////////////////////////////////////////////////////

//        System.exit(1);

////////////////////////////////////////////////////////////////////////////////
        
        
        //check pixelID.
        /* if file makepixel155_lastPixelID exists, we take pixelID value
         * from file. make it +1 and use
         * if file not exists, we create it with value (pixelID), and use
         * that value as last pixelID
         */
        int pixelID = checkLastPixelID(100000);
        System.out.println("last PpixelID was: " + pixelID);
        
        //browser.stop();
        //System.exit(1);
	  
//common		//loginActionPixel (DATA PIXEL, CAMPAIGN PIXEL), id, dataPlatform (3),
//common		//isActive (Y, N), isHttps (Y, N), maxPiggybacks (5), ruleSelection (Piggyback, Retargeting)
//dataPixel		//dataPixelIsDataEnabler, dataPixelIsDataConsumer, dataPixelServerURL, dataPixelFormat (S, I, F),
//campaignPixel	//campaignPixelType (T, L, V, G), campaignPixelFormat (S, I), campaignPixelNDLNDR (Y, N)	

////////////////////////////////////////////////////////////////////////////////
/// DATA PIXEL /////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
if ( true ) {
        char dataPixelFromat[] = { 'S', 'I', 'F' }; 
       	char dataPixelIsHttps[] = { 'Y', 'N' };
      
        for(char chPF: dataPixelFromat) {
            for(char chDPHttps: dataPixelIsHttps) {
		        pixelID++;
		        this.makePixel( "DATA PIXEL", String.valueOf(pixelID), "3",
		        		 		"Y", String.valueOf(chDPHttps), "5", "Piggyback", "http://tsvetaev.narod.ru",
		        		 		"N", "N", String.valueOf(chPF),
		        		 		"", "", "");
		
		        pixelID++;
		        this.makePixel( "DATA PIXEL", String.valueOf(pixelID), "3",
						 		"Y", String.valueOf(chDPHttps), "5", "Retargeting", "http://tsvetaev.narod.ru",
						 		"N", "N", String.valueOf(chPF),
						 		"", "", "");
		
	        }
        }
}
////////////////////////////////////////////////////////////////////////////////
/// CAMPAIGN PIXEL /////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
        //char campaignPixelType[] = { 'T', 'L', 'V', 'G' }; 
        char campaignPixelType[] = { 'T' }; 
        char campaignPixelFormat[] = { 'S', 'I' };
        char campaignPixelNDLNDR[] = { 'Y', 'N' };
        char campaignPixelIsHttps[] = { 'Y', 'N' };
        
        for(char chPT: campaignPixelType) {
            for(char chPF: campaignPixelFormat) {
                for(char chCPHttps: campaignPixelIsHttps) {
                	for(char chNDLNDR: campaignPixelNDLNDR) {
                		pixelID++;
                		this.makePixel(	"CAMPAIGN PIXEL", String.valueOf(pixelID), "3",
    						"Y", String.valueOf(chCPHttps), "5", "Piggyback", "http://tsvetaev.narod.ru",
    						"", "", "",
    						String.valueOf(chPT), String.valueOf(chPF), String.valueOf(chNDLNDR));
                		pixelID++;
                		this.makePixel(	"CAMPAIGN PIXEL", String.valueOf(pixelID), "3",
    						"Y", String.valueOf(chCPHttps), "5", "Retargeting", "http://tsvetaev.narod.ru",
    						"", "", "",
    						String.valueOf(chPT), String.valueOf(chPF), String.valueOf(chNDLNDR));
                	}
                }
            }
        }

    }
	
	private void login(String action) throws Exception {
        System.out.println("LOGIN");
		try {
	    	System.out.println("input ipdb");
			browser.type("xpath=/html//body/form/center//input[@name='ipdb']", "10.50.150.90");
        } catch (Exception e) {
	    	System.out.println(e.getMessage());
        }
		try {
	    	System.out.println("input sid");
			browser.type("xpath=/html//body/form/center//input[@name='sid']", "qacluster");
        } catch (Exception e) {
	    	System.out.println(e.getMessage());
        }
		try {
	    	System.out.println("input user");
			browser.type("xpath=/html//body/form/center//input[@name='user']", DBLogin);
        } catch (Exception e) {
	    	System.out.println(e.getMessage());
        }
		try {
	    	System.out.println("input password");
			browser.type("xpath=/html//body/form/center//input[@name='pass']", DBPswd);
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
		this.makeDataPixelTemplate(pixelNumber, dataPlatform, isActive, isHttps,
				maxPiggybacks, ruleSelection, dataPixelIsDataEnabler,
				dataPixelIsDataConsumer, dataPixelServerURL, dataPixelFormat);
		System.out.println("DATA PIXEL. fill the form");
		browser.type("name=id", pixelNumber);
        	System.out.println("id= " + pixelNumber);
		browser.type("name=name", DataPixelPrefix + pixelNumber);
        	System.out.println("name= "+ DataPixelPrefix + pixelNumber);
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
		browser.type("name=outputDescr", "outputDescr_" + DataPixelPrefix + pixelNumber);
        	System.out.println("outputDescr= outputDescr_" + DataPixelPrefix + pixelNumber);
		//browser.type("name=spotIds", "spotIds_testPixelID_" + pixelNumber);
        //	System.out.println("spotIds= spotIds_testPixelID_" + pixelNumber);
		browser.type("name=maxPiggybacks", maxPiggybacks);
        	System.out.println("maxPiggybacks= " + maxPiggybacks);
   		browserSelect("name=pixelFormat", "label=" + dataPixelFormat);
        	System.out.println("pixelFormat= " + dataPixelFormat);
       	this.clickSave();
	}
	
	private void fillFormCampaignPixel (
			//common
			String pixelNumber, String dataPlatform,
			String isActive, String isHttps, String maxPiggybacks, String ruleSelection,
			//CampaignPixel
			String campaignPixelType, String campaignPixelFormat,
			String campaignPixelNDLNDR
			) throws Exception {
		this.makeCampaignPixelTemplate(pixelNumber, dataPlatform, isActive, isHttps,
				maxPiggybacks, ruleSelection, campaignPixelType, campaignPixelFormat, campaignPixelNDLNDR);
		System.out.println("CAMPAIGN PIXEL. fill the form");
		browser.type("name=id", pixelNumber);
        	System.out.println("id= " + pixelNumber);
		browser.type("name=name", CampaignPixelPrefix + pixelNumber);
        	System.out.println("name= " + CampaignPixelPrefix + pixelNumber);
		browserSelect("name=campaignPixelType", "label=" + campaignPixelType);
        	System.out.println("campaignPixelType= " + campaignPixelType);
        browserSelect("name=dataPlatform", "label=" + dataPlatform);
        	System.out.println("dataPlatform= " + dataPlatform);
		browserSelect("name=pixelFormat", "label=" + campaignPixelFormat);
			System.out.println("pixelFormat= " + campaignPixelFormat);
        browserSelect("name=isActive", "label=" + isActive);
        	System.out.println("isActive= " + isActive);
		browserSelect("name=isHttps", "label=" + isHttps);
        	System.out.println("isHttps= " + isHttps);
		browser.type("name=maxClkAge", "10");
        	System.out.println("maxClkAge= 10");
		browser.type("name=maxImpAge", "10");
        	System.out.println("maxImpAge= 10");
		browser.type("name=maxPiggybacks", maxPiggybacks);
        	System.out.println("maxPiggybacks= " + maxPiggybacks);			
    	browserSelect("name=generateNdlNdr", "label=" + campaignPixelNDLNDR);
        	System.out.println("generateNdlNdr= " + campaignPixelNDLNDR);
		browser.type("name=maxPiggybacks", maxPiggybacks);
        	System.out.println("maxPiggybacks= " + maxPiggybacks);
       	this.clickSave();
	}

	private void makeDataPixelTemplate (
			//common
			String pixelNumber, String dataPlatform,
			String isActive, String isHttps, String maxPiggybacks, String ruleSelection,
			//dataPixel
			String dataPixelIsDataEnabler, String dataPixelIsDataConsumer,
			String dataPixelServerURL, String dataPixelFormat
			) throws Exception {
    	try {
    		File path = new File("pixels\\Templates");
    		path.mkdirs();
    		FileOutputStream fos = new FileOutputStream("pixels\\Templates\\" + pixelNumber + ".txt");
	    	PrintWriter DataPixelTemplateFile = new PrintWriter(fos);
	    	DataPixelTemplateFile.println(
	    			"#TEMPLATE\r\n" +
	    			"#DATA PIXEL\r\n" +
	    			"id= " + pixelNumber + "\r\n" +
	    			"name= " + DataPixelPrefix + pixelNumber + "\r\n" +
	    			"ruleSelection= " + ruleSelection + "\r\n" +
	    			"dataPlatform= " + dataPlatform + "\r\n" +
	    			"isActive= " + isActive + "\r\n" +
	    			"isHttps= " + isHttps + "\r\n" +
	    			"dataPixelFormat= " + dataPixelFormat + "\r\n" +
        			"dataPixelIsDataEnabler= " + dataPixelIsDataEnabler + "\r\n" +
	    			"dataPixelIsDataConsumer= " + dataPixelIsDataConsumer + "\r\n" +
	    			"dataPixelServerURL= " + dataPixelServerURL + "\r\n" +
        			"maxPiggybacks= " + maxPiggybacks
	    	);
	    	DataPixelTemplateFile.close();
    	} catch (Exception e) {
	    	System.out.println(e.getMessage());
    	}		
	}
	
	private void makeCampaignPixelTemplate (
			//common
			String pixelNumber, String dataPlatform,
			String isActive, String isHttps, String maxPiggybacks, String ruleSelection,
			//CampaignPixel
			String campaignPixelType, String campaignPixelFormat,
			String campaignPixelNDLNDR
			) throws Exception {
    	try {
    		File path = new File("pixels\\Templates");
    		path.mkdirs();
    		FileOutputStream fos = new FileOutputStream("pixels\\Templates\\" + pixelNumber + ".txt");
	    	PrintWriter CampaignPixelTemplateFile = new PrintWriter(fos);
	    	CampaignPixelTemplateFile.println(
	    			"#TEMPLATE\r\n" +
	    			"#CAMPAIGN PIXEL\r\n" +
	    			"id= " + pixelNumber + "\r\n" +
	    			"name= " + CampaignPixelPrefix + pixelNumber + "\r\n" +
	    			"ruleSelection= " + ruleSelection + "\r\n" +
	    			"campaignPixelType= " + campaignPixelType + "\r\n" +
	    			"dataPlatform= " + dataPlatform + "\r\n" +
	    			"pixelFormat= " + campaignPixelFormat + "\r\n" +
	    			"isActive= " + isActive + "\r\n" +
	    			"isHttps= " + isHttps + "\r\n" +
        			"generateNdlNdr= " + campaignPixelNDLNDR + "\r\n" +
        			"maxPiggybacks= " + maxPiggybacks
	    	);
	    	CampaignPixelTemplateFile.close();
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
	
	private void createRuleRetargeting(String pixelID, String ruleSelection) throws Exception {
		this.clickCreateNewRule("Retargeting");
			browser.type("name=label", "pixel_" + pixelID + "_RetargetingRule");
				System.out.println("label= pixel_" + pixelID + "_RetargetingRule");
			browser.type("name=cookieDuration", "100");
				System.out.println("cookieDuration= 100");
	
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
	
//common		//loginActionPixel (DATA PIXEL, CAMPAIGN PIXEL), id, dataPlatform (3),
//common		//isActive (Y, N), isHttps (Y, N), maxPiggybacks (5), ruleSelection (Piggyback, Retargeting)
//dataPixel		//dataPixelIsDataEnabler, dataPixelIsDataConsumer, dataPixelServerURL, dataPixelFormat (S, I, F),
//campaignPixel	//campaignPixelType (T, L, V, G), campaignPixelFormat (S, I), campaignPixelNDLNDR (Y, N)	

	private void makePixel(
		String loginActionPixel, String pixelID, String dataPlatform,
		String isActive, String isHttps, String maxPiggybacks, String ruleSelection, String dataPixelServerURL,
		String dataPixelIsDataEnabler, String dataPixelIsDataConsumer, String dataPixelFormat,
		String campaignPixelType, String campaignPixelFormat, String campaignPixelNDLNDR
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
				"dataPixelServerURL= " + dataPixelServerURL + ";\r\n" +
				"campaignPixelType= " + campaignPixelType + ";\r\n" +
				"campaignPixelFormat= " + campaignPixelFormat + ";\r\n" +
				"campaignPixelNDLNDR= " + campaignPixelNDLNDR + ";\r\n\r\n";
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
        	
        } else if ( loginActionPixel == "CAMPAIGN PIXEL" ) {
        	this.fillFormCampaignPixel(pixelID, dataPlatform, isActive, isHttps, maxPiggybacks, ruleSelection,
        			campaignPixelType, campaignPixelFormat, campaignPixelNDLNDR);
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
        } else if ( ruleSelection == "Retargeting" ) {
            this.createRuleRetargeting( pixelID, ruleSelection );
        } else {
			System.out.println("ERROR in ruleSelection\r\n" +
					ruleSelection + " must be Piggybacks or Retargeting");
        }
        
        this.createRule( pixelID, ruleSelection );
        this.logout();
	}
	
	private void logWrite ( String msg ) throws Exception {
		try {
	    	File file = new File(logFileName);
	    	FileWriter writer = new FileWriter(file, true);
		    writer.write(msg);
		    writer.flush();
		    writer.close();
	    } catch (Exception e) {
	    	System.out.println(e.getMessage());
	    }
	}
	
	private int checkLastPixelID ( int pixelID ) throws Exception {
		File f1 = new File(lastPixelIDFileName);
		if (f1.exists()) {
			BufferedReader bufferedReader = null;
			bufferedReader = new BufferedReader(new FileReader(lastPixelIDFileName));
			return Integer.parseInt(bufferedReader.readLine());
		} else {
			setLastPixelID(pixelID);
			return pixelID;
		}
	}
	
	private void setLastPixelID ( int pixelID ) throws Exception {
    	try {
	    	FileOutputStream fos = new FileOutputStream(lastPixelIDFileName);
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