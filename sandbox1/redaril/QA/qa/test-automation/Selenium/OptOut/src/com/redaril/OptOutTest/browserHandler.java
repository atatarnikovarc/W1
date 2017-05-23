package com.redaril.OptOutTest;

import org.openqa.selenium.WebDriver;

import com.thoughtworks.selenium.Selenium;

public class browserHandler {
	private Selenium browser;
	private WebDriver driver;
	
	browserHandler ( Selenium seleniumBrowser, WebDriver wDriver ) {
		this.browser = seleniumBrowser;
		this.driver = wDriver;
	}

	browserHandler ( Selenium seleniumBrowser ) {
		this.browser = seleniumBrowser;
		this.driver = null;
	}
	
	public Selenium getBrowser () {
		return browser;
	}
	
	public void setBrowser (Selenium seleniumBrowser) {
		this.browser = seleniumBrowser;
	}

	public WebDriver getWebDriver () {
		return driver;
	}
	
	public void setWebDriver (WebDriver wDriver) {
		this.driver = wDriver;
	}
	
}
