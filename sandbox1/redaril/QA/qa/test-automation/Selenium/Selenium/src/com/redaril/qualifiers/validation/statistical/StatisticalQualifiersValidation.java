package com.redaril.qualifiers.validation.statistical;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class StatisticalQualifiersValidation {

//==============================================================================

//	FIELD TO CHANGE 
//	
//	ENV - which environment will be used. env1 or env2
//	CLUSTER - partners to use. west or east
//	ITTERATIONCOUNT - how much times we will send qualifier
//	QUALIFIER_LIST_FILE - file with statistical qualifiers data
//
//	QUALIFIER_LIST_FILE content example:
//	lovemaniatv.com, Red Aril Interest->Modeled Gender->Female->Confidence level: low, 28
//	
//	domain - lovemaniatv.com
//	interest category - Red Aril Interest->Modeled Gender->Female->Confidence level: low
//	probability - 28
	
	private static final String ENV = "env2";
	private static final String CLUSTER = "west";
    private final static int ITTERATIONCOUNT = 100;
	private static final String QUALIFIER_LIST_FILE = "statistalQualifiersList.ql";

//==============================================================================

	
	private Selenium browser;
	private static final String PARTNERS_INFO_URL_EX = "http://" + ENV + "." + CLUSTER + ".p.raasnet.com:8080/partners/info?ex=1";
    private static final String QUALIFIER_URL_PREFIX = "http://" + ENV + "." + CLUSTER + ".p.raasnet.com:8080/partners/universal/in?pid=9&ndl=http%3A//";
    private final static String RESOURCE_PATH = "resources\\qualifiers\\validation\\regular\\";
    private final static String OUTPUT_PATH = "C:\\JavaOutput\\qualifiers\\validation\\regular\\";
	
	@Before
	public void setUp() throws Exception {
		browser = new DefaultSelenium("localhost",
    			4444, "*firefox", "http://ya.ru");
            browser.start();
            browser.setTimeout("60000");
    		browser.setSpeed("750");
    		browser.windowMaximize();
    		browser.windowFocus();
	}

	@After
	public void tearDown() throws Exception {
		browser.stop();
	}

//	@Test
//	public void getTitleSQ() throws Exception {
//		browser.open("http://ya.ru");
//        System.out.println("getTitleSQ: " + browser.getTitle() + ";");
//	}
	
	@Test
	public void test() throws Exception {
		File qlf = new File(RESOURCE_PATH + QUALIFIER_LIST_FILE);
		if (qlf.exists()) {
			BufferedReader bufferedReader = null;
			bufferedReader = new BufferedReader(new FileReader(RESOURCE_PATH + QUALIFIER_LIST_FILE));
			
			String line;
			String pageContent = "";
			String uCook = "";
			String domain = "";
			String interest = "";
			int probability = 0;
			int catchCount;
			int iCount;
			
			//String separator = "--------------------------------------------------------------------------------";
			while ( (line = bufferedReader.readLine()) != null ) {
				System.out.println(line);
				String[] arr = line.split(",");

				domain = arr[0].trim();
				interest = arr[1].trim();
				probability = Integer.valueOf(arr[2].trim());
				iCount = 0;
				catchCount = 0;
				
				while ( iCount < ITTERATIONCOUNT ) {
					browser.open(PARTNERS_INFO_URL_EX);
					browser.deleteAllVisibleCookies();
					
					//System.out.println("cookies after delete before request: " + browser.getCookie());
					browser.open(QUALIFIER_URL_PREFIX + domain);
					iCount++;
					//System.out.println("cookies after delete after request: " + browser.getCookie());

					browser.open(PARTNERS_INFO_URL_EX);
					uCook = browser.getCookieByName("u");
					//System.out.println("cookies after delete: " + browser.getCookie());
					pageContent = browser.getBodyText();

					if (pageContent.contains(interest)) {
						catchCount++;
					}

					System.out.println("i= " + iCount +
							";	catchCount= " + catchCount +
							";	uCook= " + uCook +
							";	Probability= " + probability);
		 
					pageContent = "";
					uCook = "";
				}
			}
		}
	}
}
