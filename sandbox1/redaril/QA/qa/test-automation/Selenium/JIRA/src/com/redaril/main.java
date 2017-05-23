package com.redaril;

import org.junit.After;
import org.junit.Before;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import junit.framework.TestCase;

import com.redaril.JIRA2745.*;
import com.redaril.JIRA2764.*;

public class main extends TestCase {
    private Selenium browser;
    //private static final String ENV = "env1";
    private static final String ENV = "env2";
    private static final String CLUSTER = "east";
    //private static final String CLUSTER = "west";
    
    @Before
	public void setUp() throws Exception {
		browser = new DefaultSelenium("localhost",
    			4444, "*firefox", "http://ya.ru");
            browser.start();
            browser.setTimeout("60000");
	}

	@After
	public void tearDown() throws Exception {
        browser.close();
        browser.stop();
	}

	public void test() throws Exception {
		browser.setSpeed("750");
		browser.windowMaximize();
		browser.windowFocus();
		try {
			browser.open("http://ya.ru");
        } catch (Exception e) {
            e.printStackTrace();
	    	System.out.println(e.getMessage());
    		browser.stop();
     		System.exit(1);
        }

        /* 
         * =====================================================================
         * JIRA 2745
         * =====================================================================
         */
        
//        to check parts.js scripts

//        script plase to
//        C:\Program Files\Apache Software Foundation\Apache2.2\htdocs\JIRA\2745\partners\20120425

//        html put to
//        C:\Program Files\Apache Software Foundation\Apache2.2\htdocs\JIRA\2745\html\20120425

//        in hosts
//        127.0.0.1		p0.raasnet.com
//        127.0.0.1		p1.raasnet.com
//        10.50.150.152	p.raasnet.com // west partners

//        delete all cookies
//        open:
//        	http://p0.raasnet.com/JIRA/2745/html/20120425/test.html
//        RA cookies O U LPP shoud be set
//        
//        in html use existed pixel id
        
        JIRA2745 j2745 = new JIRA2745(browser, ENV, CLUSTER, "20120315_3");
        System.out.println("Test JIRA: " + j2745.getDescription());
        if ( !j2745.execute() ) {
	    	System.out.println("ERROR: JIRA2745 FAILED.");
	    	fail("ERROR: JIRA2745 FAILED.");
	    }

        /* 
         * =====================================================================
         * JIRA 2764
         * =====================================================================
         */
//        JIRA2764 j2764 = new JIRA2764(browser, ENV, CLUSTER);
//        System.out.println("Test JIRA: " + j2764.getDescription());
//        if ( !j2764.execute() ) {
//	    	System.out.println("ERROR: JIRA2764 FAILED.");
//	    	fail("ERROR: JIRA2764 FAILED.");
//        }

	}
}
