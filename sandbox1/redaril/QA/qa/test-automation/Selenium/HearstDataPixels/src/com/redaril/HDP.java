package com.redaril;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
//import org.openqa.selenium.WebDriver;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class HDP extends TestCase {
    private String pinfoUrl = "http://p.raasnet.com/partners/info";
    private Selenium browser;
    private String hdplf = "HearstDataPixelsList";
    //private String urlPrefix = "http://p.raasnet.com/partners/universal/in?ndl=http%3A//oddschecker.com/wnba&pid=";
    private String urlPrefix = "http://p.raasnet.com/partners/universal/in?pid=";
//    private WebDriver driver;

	@Before
	public void setUp() throws Exception {
        //10.50.150.143
		browser = new DefaultSelenium("localhost",
    			4444, "*firefox", "http://ya.ru");
            browser.start();
            browser.setTimeout("60000");
	}

	@After
	public void tearDown() throws Exception {
		browser.stop();
	}

	public void test() throws Exception {
		browser.setSpeed("750");
		browser.windowMaximize();
		browser.windowFocus();
		
		browser.open(pinfoUrl);
		browser.deleteAllVisibleCookies();

		String logfilename = getLogFileName();
		
		File qlf = new File(hdplf);
		if (qlf.exists()) {
			BufferedReader bufferedReader = null;
			bufferedReader = new BufferedReader(new FileReader(hdplf));
			
			DBConnect dbc = new DBConnect(); 
			String line;
			while ( (line = bufferedReader.readLine()) != null ) {
				System.out.println("");
				appendStringToFile(logfilename, "\r\n");
				browser.open(pinfoUrl);
				browser.deleteAllVisibleCookies();
				Thread.sleep(1000);
				System.out.println("PID= " + line + ";\r\nURL: " + urlPrefix + line);
				appendStringToFile(logfilename, "PID= " + line + ";\r\nURL: " + urlPrefix + line + "\r\n");
				browser.open(urlPrefix + line);
				//System.out.println("--------------------------------------------------------------------------------");
				String dbRecord = dbc.evalSelect("select * from nc_data_pixel where pixel_id = " + line);
				System.out.println("\r\nMeta db record:\r\n" + dbRecord);
				appendStringToFile(logfilename, "\r\nMeta db record:\r\n" + dbRecord + "\r\n");
				System.out.println("\r\nPage content:\r\n" + browser.getBodyText());
				appendStringToFile(logfilename, "\r\nPage content:\r\n" + browser.getBodyText() + "\r\n");
				System.out.println("\r\nPage source:\r\n" + browser.getHtmlSource());
				appendStringToFile(logfilename, "\r\nPage source:\r\n" + browser.getHtmlSource() + "\r\n");
				System.out.println("--------------------------------------------------------------------------------");
				appendStringToFile(logfilename, "--------------------------------------------------------------------------------\r\n");
				Thread.sleep(1000);
			}
		} else {
			System.out.println("File not found.");
		}
		
	}

	private String getLogFileName () throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int iMonth = calendar.get(Calendar.MONTH);
		iMonth++;
		return "LOG_" + String.valueOf(calendar.get(Calendar.YEAR)) +
			String.valueOf(iMonth) +
			String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "_" +
			String.valueOf(calendar.getTimeInMillis()) + ".txt";
	}
	
	private void appendStringToFile ( String filename, String msg ) throws Exception {
		try {
    	    File file = new File(filename);
    	    FileWriter writer = new FileWriter(file, true);
    	    writer.write(msg);
    	    writer.flush();
    	    writer.close();
    	} catch (Exception e) {
	    	System.out.println(e.getMessage());
    	}
    }	
}
