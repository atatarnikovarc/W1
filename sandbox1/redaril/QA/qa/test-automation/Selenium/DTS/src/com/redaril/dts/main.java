package com.redaril.dts;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import junit.framework.TestCase;

import com.redaril.dts.BlueKai.*;
import com.redaril.dts.EXelate.*;

public class main extends TestCase {
    private Selenium browser;
    private static final String ENV = "env1";
    //private static final String ENV = "env2";
    
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

	@Test
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
         * BlueKai
         * =====================================================================
         */
        BlueKai bk = new BlueKai(browser, ENV);
        System.out.println(bk.getDescription());
        if ( !bk.execute() ) {
	    	System.out.println("ERROR: test BlueKai mapping is FAILED.");
	    	fail("ERROR: test EXelate mapping is FAILED.");  
        }

        /* 
         * =====================================================================
         * EXelate
         * =====================================================================
         */
        EXelate ex = new EXelate(browser, ENV);
        System.out.println(ex.getDescription());
        if ( !ex.execute() ) {
	    	System.out.println("ERROR: test EXelate mapping is FAILED.");
	    	fail("ERROR: test EXelate mapping is FAILED.");  
        }

	}
}
