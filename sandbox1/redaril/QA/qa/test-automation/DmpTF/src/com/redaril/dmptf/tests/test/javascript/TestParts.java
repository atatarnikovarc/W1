package com.redaril.dmptf.tests.test.javascript;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(value = Parameterized.class)
public class TestParts extends BaseJs {

    private final static String logFile = "parts.log";
    private final static String paramType = "_type=";

    public TestParts(HashMap<String, String> driverInfo) {
        this.driverInfo = driverInfo;
    }

    @Parameterized.Parameters
    public static List<Object[]> getParam() {
        System.setProperty(LogSystemProperty, logFile);
        LOG = LoggerFactory.getLogger(TestParts.class);
        pathAtTomcat = "test-automation/javascript/parts/";
        pixelParam = "_pixel";
        return getDriversFromFile();
    }

    @Test
    public void testParts_HTML_Script() {
        LOG.info("====testParts_HTML_Script started===");
        changeParamAtHtmlPage(universalHtml, paramType, paramType + "\"script\";");
        assertTrue("====testParts_HTML_Script failed====", testHtml(universalHtml, true));
        LOG.info("====testParts_HTML_Script passed====");
    }

    @Test
    public void testParts_HTML_Image() {
        LOG.info("====testParts_HTML_Image started===");
        changeParamAtHtmlPage(universalHtml, paramType, paramType + "\"image\";");
        assertTrue("====testParts_HTML_Image failed====", testHtml(universalHtml, true));
        LOG.info("====testParts_HTML_Image passed====");
    }

    @Test
    public void testParts_HTML_Frame() {
        LOG.info("====testParts_HTML_Frame started===");
        changeParamAtHtmlPage(universalHtml, paramType, paramType + "\"frame\";");
        assertTrue("====testParts_HTML_Frame failed====", testHtml(universalHtml, true));
        LOG.info("====testParts_HTML_Frame passed====");
    }

    @Test
    public void testParts_HTML_ExistedUser() {
        LOG.info("====testParts_HTML_ExistedUser started===");
        changeParamAtHtmlPage(universalHtml, paramType, paramType + "\"script\";");
        assertTrue("====testParts_HTML_ExistedUser failed====", testHtml(universalHtml, false));
        LOG.info("====testParts_HTML_ExistedUser passed====");
    }

    @Test
    public void testParts_HTML5_Script() {
        LOG.info("====testParts_HTML5 started===");
        changeParamAtHtmlPage(universalHtml5, paramType, paramType + "\"script\";");
        assertTrue("====testParts_HTML5 failed====", testHtml(universalHtml5, true));
        LOG.info("====testParts_HTML5 passed====");
    }

    @Test
    public void testParts_HTML5_Image() {
        LOG.info("====testParts_HTML5_Image started===");
        changeParamAtHtmlPage(universalHtml5, paramType, paramType + "\"image\";");
        assertTrue("====testParts_HTML5_Image failed====", testHtml(universalHtml5, true));
        LOG.info("====testParts_HTML5_Image passed====");
    }

    @Test
    public void testParts_HTML5_Frame() {
        LOG.info("====testParts_HTML5_Frame started===");
        changeParamAtHtmlPage(universalHtml5, paramType, paramType + "\"frame\";");
        assertTrue("====testParts_HTML5_Frame failed====", testHtml(universalHtml5, true));
        LOG.info("====testParts_HTML5_Frame passed====");
    }

    @Test
    public void testParts_XHTML_Script() {
        LOG.info("====testParts_XHTML_Script started===");
        changeParamAtHtmlPage(universalXHtml, paramType, paramType + "\"script\";");
        assertTrue("====testParts_XHTML_Script failed====", testHtml(universalXHtml, true));
        LOG.info("====testParts_XHTML_Script passed====");
    }

    @Test
    public void testParts_XHTML_Image() {
        LOG.info("====testParts_XHTML_Image started===");
        changeParamAtHtmlPage(universalXHtml, paramType, paramType + "\"image\";");
        assertTrue("====testParts_XHTML_Image failed====", testHtml(universalXHtml, true));
        LOG.info("====testParts_XHTML_Image passed====");
    }

    @Test
    public void testParts_XHTML_Frame() {
        LOG.info("====testParts_XHTML_Frame started===");
        changeParamAtHtmlPage(universalXHtml, paramType, paramType + "\"frame\";");
        assertTrue("====testParts_XHTML_Frame failed====", testHtml(universalXHtml, true));
        LOG.info("====testParts_XHTML_Frame passed====");
    }

}



