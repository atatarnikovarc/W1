package com.redaril.dmptf.tests.test.javascript;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(value = Parameterized.class)
public class TestCa2 extends BaseJs {
    private final static String logFile = "ca2.log";

    public TestCa2(HashMap<String, String> driverInfo) {
        this.driverInfo = driverInfo;
    }

    @Parameterized.Parameters
    public static List<Object[]> getParam() {
        System.setProperty(LogSystemProperty, logFile);
        LOG = LoggerFactory.getLogger(TestCa2.class);
        pathAtTomcat = "test-automation/javascript/_ca2/";
        pixelParam = "var _ca_id";
        return getDriversFromFile();
    }

    @Test
    public void testCA2_HTML() {
        LOG.info("====testCA2_HTML started===");
        assertTrue("====testCA2_HTML failed====", testHtml(universalHtml, true));
        LOG.info("====testCA2_HTML passed====");
    }

    @Test
    public void testCA2_HTML_ExistedUser() {
        LOG.info("====testCA2_HTML_ExistedUser started===");
        assertTrue("====testCA2_HTML_ExistedUser failed====", testHtml(universalHtml, false));
        LOG.info("====testCA2_HTML_ExistedUser passed====");
    }

    @Test
    public void testCA2_HTML5() {
        LOG.info("====testCA2_HTML5 started===");
        assertTrue("====testCA2_HTML5 failed====", testHtml(universalHtml5, true));
        LOG.info("====testCA2_HTML5 passed====");
    }

    @Test
    public void testCA2_XHTML() {
        LOG.info("====testCA2_XHTML started===");
        assertTrue("====testCA2_XHTML failed====", testHtml(universalXHtml, true));
        LOG.info("====testCA2_XHTML passed====");
    }


}



