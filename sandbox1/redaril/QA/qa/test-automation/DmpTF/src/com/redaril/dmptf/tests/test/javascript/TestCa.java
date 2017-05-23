package com.redaril.dmptf.tests.test.javascript;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(value = Parameterized.class)
public class TestCa extends BaseJs {
    private final static String logFile = "ca2.log";

    public TestCa(HashMap<String, String> driverInfo) {
        this.driverInfo = driverInfo;
    }

    @Parameterized.Parameters
    public static List<Object[]> getParam() {
        System.setProperty(LogSystemProperty, logFile);
        LOG = LoggerFactory.getLogger(TestParts.class);
        pathAtTomcat = "test-automation/javascript/_ca/";
        pixelParam = "var _ca_id";
        return getDriversFromFile();
    }

    @Test
    public void testCA_HTML() {
        LOG.info("====testCA_HTML started===");
        assertTrue("====testCA_HTML failed====", testHtml(universalHtml, true));
        LOG.info("====testCA_HTML passed====");
    }

    @Test
    public void testCA_HTML_ExistedUser() {
        LOG.info("====testCA_HTML_ExistedUser started===");
        assertTrue("====testCA_HTML_ExistedUser failed====", testHtml(universalHtml, false));
        LOG.info("====testCA_HTML_ExistedUser passed====");
    }

    @Test
    public void testCA_HTML5() {
        LOG.info("====testCA_HTML5 started===");
        assertTrue("====testCA_HTML5 failed====", testHtml(universalHtml5, true));
        LOG.info("====testCA_HTML5 passed====");
    }

    @Test
    public void testCA_XHTML() {
        LOG.info("====testCA_XHTML started===");
        assertTrue("====testCA_XHTML failed====", testHtml(universalXHtml, true));
        LOG.info("====testCA_XHTML passed====");
    }

}

