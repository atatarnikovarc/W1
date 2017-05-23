package com.redaril.OptOutTest;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.thoughtworks.selenium.BrowserConfigurationOptions;

public class Browser {

	private Selenium browser;
	private WebDriver driver;
	
	public browserHandler getBrowserHandler ( String browserName ) {
		browserHandler bh;
		
		if ( browserName.equals("firefox") ) {
			browser = new DefaultSelenium("localhost",
					4444, "*firefox", "http://ya.ru");
			browser.start();
			bh = new browserHandler(browser); 
			
		} else if ( browserName.equals("googlechrome") ) {
	    	BrowserConfigurationOptions webSec = new BrowserConfigurationOptions();
			browser = new DefaultSelenium("localhost",
					4444, "*googlechrome", "http://ya.ru");
	    	browser.start(webSec.setCommandLineFlags("--disable-web-security"));
			bh = new browserHandler(browser); 
	    	
		} else if ( browserName.equals("iexplore") ) {
//	    	DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
//	    	ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
//	    	driver = new InternetExplorerDriver(ieCapabilities);
//
//	    	driver = new InternetExplorerDriver();
//	    	browser = new WebDriverBackedSelenium(driver, "http://ya.ru");
			driver = new InternetExplorerDriver();
	    	browser = new WebDriverBackedSelenium(driver, "http://ya.ru");
	    	
			bh = new browserHandler(browser, driver); 

		} else {
			System.out.println("How to use:\r\n" +
					"browser =  new browser().getBrowserHandler(\"browserName\");\r\n" +
					"browserName = \"firefox\" | \"googlechrome\" | \"iexplore\";\r\n" +
					"This method will return Selenium object with browser handler.\r\n");
			return null;
		}
        browser.setTimeout("60000");
		browser.setSpeed("750");
		browser.windowMaximize();
		browser.windowFocus();
		return bh;
	}
}
