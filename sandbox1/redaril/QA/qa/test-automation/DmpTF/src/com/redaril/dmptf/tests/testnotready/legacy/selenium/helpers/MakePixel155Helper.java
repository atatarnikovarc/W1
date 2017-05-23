package com.redaril.dmptf.tests.testnotready.legacy.selenium.helpers;
//package com.redaril.dmptf.tests.test.legacy.selenium.helpers;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.PrintWriter;
//import java.util.Calendar;
//import java.util.Date;
//
//import com.redaril.dmptf.util.Config;
//import com.thoughtworks.selenium.Selenium;
//
//public class MakePixel155Helper {
//
//	public static void login(Selenium browser, String action) throws Exception {
//		System.out.println("LOGIN");
//		try {
//			System.out.println("input ipdb");
//			browser.type("xpath=/html//body/form/center//input[@name='ipdb']",
//					"10.50.150.90");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		try {
//			System.out.println("input sid");
//			browser.type("xpath=/html//body/form/center//input[@name='sid']",
//					"qacluster");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		try {
//			System.out.println("input user");
//			browser.type("xpath=/html//body/form/center//input[@name='user']",
//					"qacluster3_meta");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		try {
//			System.out.println("input password");
//			browser.type("xpath=/html//body/form/center//input[@name='pass']",
//					"qacluster3_meta");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		try {
//			System.out.println("select action use '" + action + "'");
//			browserSelect(browser, "name=action", "label=" + action);
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		try {
//			System.out.println("click ");
//			browser.click("xpath=/html//body/form/center//input[@type='submit'  and contains(@text, 'Login')]");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//
//		System.out.println("wait while page loading");
//		waitForPageLoadingText(browser, "Create New");
//	}
//
//	public static void logout(Selenium browser) throws Exception {
//		System.out.println("LOGOUT");
//		try {
//			System.out.println("click ");
//			browser.click("xpath=/html/body//a[contains(., 'EXIT')]");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//
//		System.out.println("wait while page loading");
//		waitForPageLoadingText(browser, "Login form");
//	}
//
//	public static void clickCreateNewPixel(Selenium browser) throws Exception {
//		System.out.println("click 'Create New'");
//		try {
//			browser.click("xpath=/html/body/a[contains(., 'Create New')]");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		System.out.println("wait while page loading");
//		waitForPageLoadingElement(browser,
//				"xpath=/html//body/form//input[@type='submit' and @value='save']");
//	}
//
//	public static void clickSave(Selenium browser) throws Exception {
//		try {
//			System.out.println("Click [SAVE] button");
//			browser.click("xpath=/html//body/form//input[@type='submit' and @value='save']");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		System.out.println("wait while page loading");
//		waitForPageLoadingText(browser, "saved successfully");
//	}
//
//	public static void fillFormDataPixel(
//			Selenium browser,
//			// common
//			String pixelNumber, String dataPlatform, String isActive,
//			String isHttps, String maxPiggybacks, String ruleSelection,
//			// dataPixel
//			String dataPixelIsDataEnabler, String dataPixelIsDataConsumer,
//			String dataPixelServerURL) throws Exception {
//		String DataPixelPrefix = Config.getInstance().getDataPixelPrefix();
//		makeDataPixelTemplate(pixelNumber, dataPlatform, isActive, isHttps,
//				maxPiggybacks, ruleSelection, dataPixelIsDataEnabler,
//				dataPixelIsDataConsumer, dataPixelServerURL);
//		System.out.println("DATA PIXEL. fill the form");
//		browser.type("name=id", pixelNumber);
//		System.out.println("id= " + pixelNumber);
//		browser.type("name=name", DataPixelPrefix + pixelNumber);
//		System.out.println("name= " + DataPixelPrefix + pixelNumber);
//		browserSelect(browser, "name=dataPlatform", "label=" + dataPlatform);
//		System.out.println("dataPlatform= " + dataPlatform);
//		browserSelect(browser, "name=isActive", "label=" + isActive);
//		System.out.println("isActive= " + isActive);
//		browserSelect(browser, "name=isHttps", "label=" + isHttps);
//		System.out.println("isHttps= " + isHttps);
//		browserSelect(browser, "name=isDataEnabler", "label="
//				+ dataPixelIsDataEnabler);
//		System.out.println("isDataEnabler= " + dataPixelIsDataEnabler);
//		browserSelect(browser, "name=isDataConsumer", "label="
//				+ dataPixelIsDataConsumer);
//		System.out.println("isDataConsumer= " + dataPixelIsDataConsumer);
//		browser.type("name=dataServerURL", dataPixelServerURL);
//		System.out.println("dataServerURL= " + dataPixelServerURL);
//		browser.type("name=outputDescr", "outputDescr_" + DataPixelPrefix
//				+ pixelNumber);
//		System.out.println("outputDescr= outputDescr_" + DataPixelPrefix
//				+ pixelNumber);
//		// browser.type("name=spotIds", "spotIds_testPixelID_" + pixelNumber);
//		// System.out.println("spotIds= spotIds_testPixelID_" + pixelNumber);
//		browser.type("name=maxPiggybacks", maxPiggybacks);
//		System.out.println("maxPiggybacks= " + maxPiggybacks);
//		clickSave(browser);
//	}
//
//	public static void fillFormCampaignPixel(
//			Selenium browser,
//			// common
//			String pixelNumber, String dataPlatform, String isActive,
//			String isHttps, String maxPiggybacks, String ruleSelection,
//			// CampaignPixel
//			String campaignPixelType, String campaignPixelFormat,
//			String campaignPixelNDLNDR) throws Exception {
//		makeCampaignPixelTemplate(pixelNumber, dataPlatform, isActive, isHttps,
//				maxPiggybacks, ruleSelection, campaignPixelType,
//				campaignPixelFormat, campaignPixelNDLNDR);
//		System.out.println("CAMPAIGN PIXEL. fill the form");
//		browser.type("name=id", pixelNumber);
//		System.out.println("id= " + pixelNumber);
//		browser.type("name=name", Config.getInstance().getCampaignPixelPrefix()
//				+ pixelNumber);
//		System.out.println("name= "
//				+ Config.getInstance().getCampaignPixelPrefix() + pixelNumber);
//		browserSelect(browser, "name=campaignPixelType", "label="
//				+ campaignPixelType);
//		System.out.println("campaignPixelType= " + campaignPixelType);
//		browserSelect(browser, "name=dataPlatform", "label=" + dataPlatform);
//		System.out.println("dataPlatform= " + dataPlatform);
//		browserSelect(browser, "name=pixelFormat", "label="
//				+ campaignPixelFormat);
//		System.out.println("pixelFormat= " + campaignPixelFormat);
//		browserSelect(browser, "name=isActive", "label=" + isActive);
//		System.out.println("isActive= " + isActive);
//		browserSelect(browser, "name=isHttps", "label=" + isHttps);
//		System.out.println("isHttps= " + isHttps);
//		browser.type("name=maxClkAge", "10");
//		System.out.println("maxClkAge= 10");
//		browser.type("name=maxImpAge", "10");
//		System.out.println("maxImpAge= 10");
//		browser.type("name=maxPiggybacks", maxPiggybacks);
//		System.out.println("maxPiggybacks= " + maxPiggybacks);
//		browserSelect(browser, "name=generateNdlNdr", "label="
//				+ campaignPixelNDLNDR);
//		System.out.println("generateNdlNdr= " + campaignPixelNDLNDR);
//		browser.type("name=maxPiggybacks", maxPiggybacks);
//		System.out.println("maxPiggybacks= " + maxPiggybacks);
//		clickSave(browser);
//	}
//
//	public static void makeDataPixelTemplate(
//			// common
//			String pixelNumber, String dataPlatform, String isActive,
//			String isHttps, String maxPiggybacks, String ruleSelection,
//			// dataPixel
//			String dataPixelIsDataEnabler, String dataPixelIsDataConsumer,
//			String dataPixelServerURL) throws Exception {
//		try {
//			File path = new File("pixels\\Templates");
//			path.mkdirs();
//			FileOutputStream fos = new FileOutputStream("pixels\\Templates\\"
//					+ pixelNumber + ".txt");
//			PrintWriter CampaignPixelTemplateFile = new PrintWriter(fos);
//			CampaignPixelTemplateFile.println("#TEMPLATE\r\n"
//					+ "#DATA PIXEL\r\n" + "id= "
//					+ pixelNumber
//					+ "\r\n"
//					+ "name= "
//					+ Config.getInstance().getDataPixelPrefix()
//					+ pixelNumber
//					+ "\r\n"
//					+ "ruleSelection= "
//					+ ruleSelection
//					+ "\r\n"
//					+ "dataPlatform= "
//					+ dataPlatform
//					+ "\r\n"
//					+ "isActive= "
//					+ isActive
//					+ "\r\n"
//					+ "isHttps= "
//					+ isHttps
//					+ "\r\n"
//					+ "dataPixelIsDataEnabler= "
//					+ dataPixelIsDataEnabler
//					+ "\r\n"
//					+ "dataPixelIsDataConsumer= "
//					+ dataPixelIsDataConsumer
//					+ "\r\n"
//					+ "dataPixelServerURL= "
//					+ dataPixelServerURL
//					+ "\r\n"
//					+ "maxPiggybacks= "
//					+ maxPiggybacks);
//			CampaignPixelTemplateFile.close();
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//	}
//
//	public static void makeCampaignPixelTemplate(
//			// common
//			String pixelNumber, String dataPlatform, String isActive,
//			String isHttps, String maxPiggybacks, String ruleSelection,
//			// CampaignPixel
//			String campaignPixelType, String campaignPixelFormat,
//			String campaignPixelNDLNDR) throws Exception {
//		try {
//			File path = new File("pixels\\Templates");
//			path.mkdirs();
//			FileOutputStream fos = new FileOutputStream("pixels\\Templates\\"
//					+ pixelNumber + ".txt");
//			PrintWriter CampaignPixelTemplateFile = new PrintWriter(fos);
//			CampaignPixelTemplateFile.println("#TEMPLATE\r\n"
//					+ "#CAMPAIGN PIXEL\r\n" + "id= "
//					+ pixelNumber
//					+ "\r\n"
//					+ "name= "
//					+ Config.getInstance().getCampaignPixelPrefix()
//					+ pixelNumber
//					+ "\r\n"
//					+ "ruleSelection= "
//					+ ruleSelection
//					+ "\r\n"
//					+ "campaignPixelType= "
//					+ campaignPixelType
//					+ "\r\n"
//					+ "dataPlatform= "
//					+ dataPlatform
//					+ "\r\n"
//					+ "pixelFormat= "
//					+ campaignPixelFormat
//					+ "\r\n"
//					+ "isActive= "
//					+ isActive
//					+ "\r\n"
//					+ "isHttps= "
//					+ isHttps
//					+ "\r\n"
//					+ "generateNdlNdr= "
//					+ campaignPixelNDLNDR
//					+ "\r\n" + "maxPiggybacks= " + maxPiggybacks);
//			CampaignPixelTemplateFile.close();
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//	}
//
//	public static void clickCreateNewRule(Selenium browser, String ruleType)
//			throws Exception {
//
//		System.out.println("click '" + ruleType + "'");
//
//		try {
//			browser.click("xpath=/html/body//a[contains(., '" + ruleType
//					+ "')]");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		System.out.println("wait while page loading");
//		waitForPageLoadingElement(browser,
//				"xpath=/html/body/span//a[@class='tabs selected' and contains(., '"
//						+ ruleType + "')]");
//
//		System.out.println("click 'Create New'");
//
//		try {
//			browser.click("xpath=/html/body//a[contains(., 'Create New')]");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		System.out.println("wait while page loading");
//		waitForPageLoadingElement(browser,
//				"xpath=/html//body/form//input[@type='submit' and @value='save']");
//
//	}
//
//	public static void createRuleConstraints(Selenium browser, String pixelID)
//			throws Exception {
//		clickCreateNewRule(browser, "Constraints");
//		browserSelect(browser, "name=partnerType", "label=Universal");
//		System.out.println("partnerType= Universal");
//		browserSelect(browser, "name=fieldLabel", "label=If pixel id");
//		System.out.println("fieldLabel= If pixel id");
//		browserSelect(browser, "name=operator", "label==");
//		System.out.println("operator= =");
//		browser.type("name=fieldValue", pixelID);
//		System.out.println("fieldValue= " + pixelID);
//		clickSave(browser);
//	}
//
//	static public void createRuleSuperConstraints(Selenium browser,
//			String pixelID) throws Exception {
//		clickCreateNewRule(browser, "Super constraints");
//		browser.type("name=label", "pixel_" + pixelID + "_SuperConstraintsRule");
//		System.out.println("label= pixel_" + pixelID + "_SuperConstraintsRule");
//		browserSelect(browser, "name=partnerType", "label=Universal");
//		System.out.println("partnerType= Universal");
//
//		Thread.sleep(5000);
//
//		browserSelect(browser, "name=choices", "label=Universal: partnerId = "
//				+ pixelID);
//		System.out.println("choices= Universal: partnerId = " + pixelID);
//		clickSave(browser);
//
//	}
//
//	public static void createRulePiggybacks(Selenium browser, String pixelID,
//			String dataServerURL) throws Exception {
//		clickCreateNewRule(browser, "Piggybacks");
//		browser.type("name=label", "pixel_" + pixelID + "_PiggybacksRule");
//		System.out.println("label= pixel_" + pixelID + "_PiggybacksRule");
//		browser.type("name=url", dataServerURL);
//		System.out.println("url= " + dataServerURL);
//		browser.type("name=weight", "99");
//		System.out.println("weight= 99");
//		browser.click("name=enabled");
//		System.out.println("click isEnabled");
//		browser.click("name=isCycled");
//		System.out.println("click isCycled");
//		browser.click("name=isUnlim");
//		System.out.println("click isUnlim");
//		browser.type("name=duration", "100");
//		System.out.println("duration= 100");
//
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(new Date());
//		String sDate = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))
//				+ "." + String.valueOf(calendar.get(Calendar.MONTH)) + "."
//				+ String.valueOf(calendar.get(Calendar.YEAR));
//		browser.type("name=startDate:date", sDate);
//		System.out.println("startDate:date= " + sDate);
//		browser.type("startDate:hours", "00");
//		System.out.println("startDate:hours= 00");
//		browser.type("startDate:minutes", "01");
//		System.out.println("startDate:minutes= 01");
//
//		int year = calendar.get(Calendar.YEAR);
//		year++;
//		sDate = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "."
//				+ String.valueOf(calendar.get(Calendar.MONTH)) + "."
//				+ String.valueOf(year);
//		browser.type("name=endDate:date", sDate);
//		System.out.println("endDate:date= " + sDate);
//		browser.type("endDate:hours", "00");
//		System.out.println("endDate:hours= 00");
//		browser.type("endDate:minutes", "01");
//		System.out.println("endDate:minutes= 01");
//
//		browser.type("description", "pixel_" + pixelID
//				+ "_PiggybacksRuleDescription");
//		System.out.println("description= pixel_" + pixelID
//				+ "_PiggybacksRuleDescription");
//		clickSave(browser);
//	}
//
//	public static void createRuleRetargeting(Selenium browser, String pixelID,
//			String ruleSelection) throws Exception {
//		clickCreateNewRule(browser, "Retargeting");
//		browser.type("name=label", "pixel_" + pixelID + "_RetargetingRule");
//		System.out.println("label= pixel_" + pixelID + "_RetargetingRule");
//		browser.type("name=cookieDuration", "100");
//		System.out.println("cookieDuration= 100");
//
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(new Date());
//		String sDate = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))
//				+ "." + String.valueOf(calendar.get(Calendar.MONTH)) + "."
//				+ String.valueOf(calendar.get(Calendar.YEAR));
//		browser.type("name=startDate:date", sDate);
//		System.out.println("startDate:date= " + sDate);
//		browser.type("startDate:hours", "00");
//		System.out.println("startDate:hours= 00");
//		browser.type("startDate:minutes", "01");
//		System.out.println("startDate:minutes= 01");
//
//		int year = calendar.get(Calendar.YEAR);
//		year++;
//		sDate = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "."
//				+ String.valueOf(calendar.get(Calendar.MONTH)) + "."
//				+ String.valueOf(year);
//		browser.type("name=endDate:date", sDate);
//		System.out.println("endDate:date= " + sDate);
//		browser.type("endDate:hours", "00");
//		System.out.println("endDate:hours= 00");
//		browser.type("endDate:minutes", "01");
//		System.out.println("endDate:minutes= 01");
//
//		clickSave(browser);
//	}
//
//	public static void createRule(Selenium browser, String pixelID,
//			String ruleSelection) throws Exception {
//		clickCreateNewRule(browser, "Rules");
//		browser.type("name=label", "pixel_" + pixelID + "_Rule");
//		System.out.println("label= pixel_" + pixelID + "_Rule");
//		browserSelect(browser, "name=ruletype", "label=" + ruleSelection);
//		System.out.println("ruletype= " + ruleSelection);
//
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(new Date());
//		int year = calendar.get(Calendar.YEAR);
//		year++;
//		String sDate = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))
//				+ "." + String.valueOf(calendar.get(Calendar.MONTH)) + "."
//				+ String.valueOf(year);
//		browser.type("name=endDate:date", sDate);
//		System.out.println("endDate:date= " + sDate);
//		browser.type("endDate:hours", "00");
//		System.out.println("endDate:hours= 00");
//		browser.type("endDate:minutes", "01");
//		System.out.println("endDate:minutes= 01");
//		browser.click("name=enabled");
//		System.out.println("click Enabled");
//
//		Thread.sleep(5000);
//
//		browserSelect(browser, "name=choices", "label=pixel_" + pixelID
//				+ "_SuperConstraintsRule");
//		System.out.println("choices= pixel_" + pixelID
//				+ "_SuperConstraintsRule");
//		if (ruleSelection == "Piggyback") {
//			browserSelect(browser, "name=data", "label=pixel_" + pixelID
//					+ "_PiggybacksRule");
//			System.out.println("data= pixel_" + pixelID + "_PiggybacksRule");
//		} else if (ruleSelection == "Retargeting") {
//			browserSelect(browser, "name=data", "label=pixel_" + pixelID
//					+ "_RetargetingRule");
//			System.out.println("data= pixel_" + pixelID + "_RetargetingRule");
//		}
//		clickSave(browser);
//	}
//
//	// common //loginActionPixel (DATA PIXEL, CAMPAIGN PIXEL), id, dataPlatform
//	// (3),
//	// common //isActive (Y, N), isHttps (Y, N), maxPiggybacks (5),
//	// ruleSelection (Piggyback, Retargeting)
//	// dataPixel //dataPixelIsDataEnabler, dataPixelIsDataConsumer,
//	// dataPixelServerURL,
//	// campaignPixel //campaignPixelType (T, L, V, G), campaignPixelFormat (S,
//	// I), campaignPixelNDLNDR (Y, N)
//
//	public static void makePixel(Selenium browser, String loginActionPixel,
//			String pixelID, String dataPlatform, String isActive,
//			String isHttps, String maxPiggybacks, String ruleSelection,
//			String dataPixelServerURL, String dataPixelIsDataEnabler,
//			String dataPixelIsDataConsumer, String campaignPixelType,
//			String campaignPixelFormat, String campaignPixelNDLNDR)
//			throws Exception {
//
//		String msg = "\r\n"
//				+ "-------------------------------------------------------------------------------\r\n"
//				+ "MAKE "
//				+ loginActionPixel
//				+ ".\r\n"
//				+ "id= "
//				+ pixelID
//				+ ";\r\n"
//				+ "pixel params:\r\n"
//				+ "dataPlatform= "
//				+ dataPlatform
//				+ ";\r\n"
//				+ "isActive= "
//				+ isActive
//				+ ";\r\n"
//				+ "isHttps= "
//				+ isHttps
//				+ ";\r\n"
//				+ "maxPiggybacks= "
//				+ maxPiggybacks
//				+ ";\r\n"
//				+ "ruleSelection= "
//				+ ruleSelection
//				+ ";\r\n"
//				+ "dataPixelIsDataEnabler= "
//				+ dataPixelIsDataEnabler
//				+ ";\r\n"
//				+ "dataPixelIsDataConsumer= "
//				+ dataPixelIsDataConsumer
//				+ ";\r\n"
//				+ "dataPixelServerURL= "
//				+ dataPixelServerURL
//				+ ";\r\n"
//				+ "campaignPixelType= "
//				+ campaignPixelType
//				+ ";\r\n"
//				+ "campaignPixelFormat= "
//				+ campaignPixelFormat
//				+ ";\r\n"
//				+ "campaignPixelNDLNDR= " + campaignPixelNDLNDR + ";\r\n\r\n";
//		logWrite(msg);
//		System.out.println(msg);
//
//		/*
//		 * Create Pixel
//		 */
//		login(browser, loginActionPixel);
//		clickCreateNewPixel(browser);
//
//		if (loginActionPixel == "DATA PIXEL") {
//			fillFormDataPixel(browser, pixelID, dataPlatform, isActive,
//					isHttps, maxPiggybacks, ruleSelection,
//					dataPixelIsDataEnabler, dataPixelIsDataConsumer,
//					dataPixelServerURL);
//
//		} else if (loginActionPixel == "CAMPAIGN PIXEL") {
//			fillFormCampaignPixel(browser, pixelID, dataPlatform, isActive,
//					isHttps, maxPiggybacks, ruleSelection, campaignPixelType,
//					campaignPixelFormat, campaignPixelNDLNDR);
//		} else {
//			System.out.println("ERROR in loginAction\r\n" + ruleSelection
//					+ " must be DATA PIXEL or CAMPAIGN PIXEL");
//		}
//		setLastPixelID(Integer.parseInt(pixelID));
//		logout(browser);
//		/*
//		 * Create Rule for Pixel
//		 */
//		login(browser, "RULES");
//		createRuleConstraints(browser, pixelID);
//		createRuleSuperConstraints(browser, pixelID);
//
//		if (ruleSelection == "Piggyback") {
//			createRulePiggybacks(browser, pixelID, dataPixelServerURL);
//		} else if (ruleSelection == "Retargeting") {
//			createRuleRetargeting(browser, pixelID, ruleSelection);
//		} else {
//			System.out.println("ERROR in ruleSelection\r\n" + ruleSelection
//					+ " must be Piggybacks or Retargeting");
//		}
//
//		createRule(browser, pixelID, ruleSelection);
//		logout(browser);
//	}
//
//	public static void logWrite(String msg) throws Exception {
//		try {
//			File file = new File(Config.getInstance().getLogFilename());
//			FileWriter writer = new FileWriter(file, true);
//			writer.write(msg);
//			writer.flush();
//			writer.close();
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//	}
//
//	public static int checkLastPixelID(int pixelID) throws Exception {
//		String lastPixelIDFileName = Config.getInstance()
//				.getLastPixelIdFilename();
//		File f1 = new File(lastPixelIDFileName);
//		if (f1.exists()) {
//			BufferedReader bufferedReader = null;
//			bufferedReader = new BufferedReader(new FileReader(
//					lastPixelIDFileName));
//			return Integer.parseInt(bufferedReader.readLine());
//		} else {
//			setLastPixelID(pixelID);
//			return pixelID;
//		}
//	}
//
//	public static void setLastPixelID(int pixelID) throws Exception {
//		try {
//			FileOutputStream fos = new FileOutputStream(Config.getInstance()
//					.getLastPixelIdFilename());
//			PrintWriter lastPixelIDFile = new PrintWriter(fos);
//			lastPixelIDFile.println(pixelID);
//			lastPixelIDFile.close();
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//	}
//
//	// TODO
//	public static void browserSelect(Selenium browser, String locator,
//			String value) throws Exception {
//		int count = 0;
//		while (true) {
//			try {
//				browser.select(locator, value);
//				break;
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//			}
//			Thread.sleep(500);
//			if (count == 100) {
//				System.out
//						.println("ERROR: Something wrong. Can`t use element: "
//								+ locator);
//				System.exit(1);
//			}
//			count++;
//		}
//	}
//
//	public static void waitForPageLoadingText(Selenium browser, String locator) {
//		long start = System.currentTimeMillis();
//		while (System.currentTimeMillis() < start + 60000) {
//			try {
//				if (browser.isTextPresent(locator))
//					return;
//			} catch (Exception e) {
//			}
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				throw new AssertionError(e);
//			}
//		}
//		throw new AssertionError("timeout");
//	}
//
//	public static void waitForPageLoadingElement(Selenium browser,
//			String locator) {
//		long start = System.currentTimeMillis();
//		while (System.currentTimeMillis() < start + 60000) {
//			try {
//				if (browser.isElementPresent(locator))
//					return;
//			} catch (Exception e) {
//			}
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				throw new AssertionError(e);
//			}
//		}
//		throw new AssertionError("timeout");
//	}
//
//}
