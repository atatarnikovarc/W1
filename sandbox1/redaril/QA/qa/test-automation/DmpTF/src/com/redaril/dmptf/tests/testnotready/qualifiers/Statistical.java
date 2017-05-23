//package com.redaril.dmptf.tests.testnotready.qualifiers;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//
//import com.redaril.dmptf.tests.support.MainTest;
//
//
//import com.redaril.dmptf.util.configuration.ConfigurationLoader;
//import com.redaril.dmptf.util.network.lib.httpunit.HttpUnitWrapper;
//
//import org.slf4j.Logger;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;
//
//public class Statistical extends MainTest {
//
//    private static Logger LOG;
//    private final static String COMMON_PATH = File.separator + "qualifiers" + File.separator + "validation" + File.separator + "statistical" + File.separator;
//    private final static String RESOURCE_PATH = "data" + COMMON_PATH;
//    private static int ITTERATIONCOUNT;
//    private static String QUALIFIER_LIST_FILE;
//    private static String PARTNERS_INFO_URL_EX;
//    private static String QUALIFIER_URL_PREFIX;
//    private final static String logFile = "statistical.log";
//    protected static ConfigurationLoader testClassConfigurationLoader;
//    protected final static String testClassProperties = "qualifiers.properties";
//    protected static HttpUnitWrapper session;
//    protected static Boolean isInit = false;
//
//    @Before
//    public void setUp() {
//        if (!isInit) {
//            System.setProperty(LogSystemProperty, logFile);
//            testClassConfigurationLoader = new ConfigurationLoader(CONFIG_PATH + testClassProperties);
//            ITTERATIONCOUNT = Integer.valueOf(testClassConfigurationLoader.getProperty("ITTERATIONCOUNT"));
//            QUALIFIER_LIST_FILE = testClassConfigurationLoader.getProperty("QUALIFIER_LIST_FILE");
//            super.setUp();
//            PARTNERS_INFO_URL_EX = "http://" + ENV + "." + envConfigID + ".p.raasnet.com:8080/partners/info?ex=1";
//            QUALIFIER_URL_PREFIX = "http://" + ENV + "." + envConfigID + ".p.raasnet.com:8080/partners/universal/in?pid=84680&ndl=http%3A//";
//            LOG = LoggerFactory.getLogger(Statistical.class);
//            session = new HttpUnitWrapper();
//            isInit = true;
//        }
//    }
//    @Test
//    public void testStatisticalQualifiers() {
//
//        LOG.info("- PROPERTIES -------------------------------");
//        LOG.info("ENV: " + ENV);
//        LOG.info("envConfigID: " + envConfigID);
//        LOG.info("ITTERATIONCOUNT: " + ITTERATIONCOUNT);
//        File qlf = new File(RESOURCE_PATH + QUALIFIER_LIST_FILE);
//        if (qlf.exists()) {
//            try {
//                BufferedReader bufferedReader;
//                bufferedReader = new BufferedReader(new FileReader(RESOURCE_PATH + QUALIFIER_LIST_FILE));
//                String line;
//                String pageContent;
//                String domain;
//                String interest;
//                int probability = 0;
//                int catchCount = 0;
//                int iCount = 0;
//                while ((line = bufferedReader.readLine()) != null) {
//                    LOG.debug(line);
//                    String[] arr = line.split(",");
//                    domain = arr[0].trim();
//                    interest = arr[1].trim();
//                    probability = Integer.valueOf(arr[2].trim());
//                    iCount = 0;
//                    catchCount = 0;
//                    String cookieValue;
//                    while (iCount < ITTERATIONCOUNT) {
//                        session.goToUrl(PARTNERS_INFO_URL_EX);
//                        session.deleteAllCookies();
//                        session.goToUrl(QUALIFIER_URL_PREFIX + domain);
//                        iCount++;
//                        pageContent = session.getResponsePage(PARTNERS_INFO_URL_EX);
//                        cookieValue = session.getCookieValueByName(ucookie);
//                        if (pageContent.contains(interest)) {
//                            catchCount++;
//                        }
//                        LOG.debug("iteration= " + iCount +
//                                "; catchCount= " + catchCount +
//                                "; Probability= " + probability +
//                                "; uCook= " + cookieValue + ";");
//                        LOG.debug("PageContent:\r\n" + pageContent);
//                    }
//                    LOG.info("Qualifier: " + line + "\r\nExpected probability=" + probability + "; " +
//                            "Actual probability=" + iCount * 100 / catchCount);
//                }
//                assertTrue(probability == iCount * 100 / catchCount);
//            } catch (IOException e) {
//                LOG.error("Can't get qualifiers from file. Exception = "+ e.getLocalizedMessage());
//                fail("Can't get qualifiers from file.");
//            }
//        }
//    }
//}
