package com.redaril.dmptf.tests.test.pip.piggyback;

import com.redaril.dmp.model.meta.*;
import com.redaril.dmptf.tests.support.pip.base.BasePiggybackTest;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.date.DateWrapper;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.network.appinterface.jmx.JMXWrapper;
import com.redaril.dmptf.util.network.protocol.ssh.SSHWrapper;
import com.redaril.dmptf.util.selenium.ProxyWrapper;
import com.redaril.dmptf.util.selenium.WebDriverWrapper;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.junit.Assert.assertTrue;

@RunWith(value = Parameterized.class)
public class PiggybackBrowser extends BasePiggybackTest {
    private HashMap<String, String> driverInfo;
    private static String systemPbUrl;
    private static String clientPbUrl;
    private static boolean isPbBrowserSetup;
    private static boolean isAtPage = false;
    private static Set<AudienceGroup> setAG;
    private static DataPixel dataPixel;
    private static SystemPiggyback systemPiggyback;
    private static DmpPiggyback clientPiggyback;
    private static boolean isSetup;
    private final static String logFile = "piggybackBrowser.log";
    private static Logger browserPbLOG;

    public PiggybackBrowser(HashMap<String, String> driverInfo) {
        this.driverInfo = driverInfo;
    }

    protected static List<Object[]> getDriversFromFile() {
        //get data from file into List<String>
        List<Object[]> list = new ArrayList<Object[]>();
        List<String> ipList = FileHelper.getInstance().getDataFromFile(sourceDrivers);
        //end get data
        //parse every line into hashmap, all hashmaps put into array[1] and into List() (structure List<Object[]>)
        String[] params;
        for (String aipList : ipList) {
            HashMap<String, String> data = new HashMap<String, String>();
            params = aipList.split(";");
            //if browser can(or not) be used with proxy add it to list
            for (int j = 0; j < columnNames.size(); j++) {
                data.put(columnNames.get(j), params[j]);
            }
            Object[] array = new Object[1];
            array[0] = data;
            list.add(array);
        }
        return list;
    }

    private void goToPixelPage() {
        if (!isAtPage) {
            String url = urlPixel + "?" + DateWrapper.getRandom();
            proxyWrapper.watchUrl(url);
            browserPbLOG.info("Go to url = " + url);
            webDriverWrapper.getPage(url);
            wait(10);
        }
        isAtPage = !isAtPage;

    }

    @Rule
    public TestName name = new TestName();

    @BeforeClass
    public static void setUp() {
        if (!isSetup) {
            configEnv = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
            ENV = configEnv.getProperty("env");
            configID = configEnv.getProperty("configID");
            webDriverWrapper = new WebDriverWrapper(ENV);
            proxyWrapper = new ProxyWrapper(ENV, null);
            isSetup = true;
        }
    }

    @Before
    public void setup() {
        if (!isPbBrowserSetup) {
            //create data for tests
            browserPbLOG = LoggerFactory.getLogger(PiggybackBrowser.class);
            systemPbUrl = urlRedirect + DateWrapper.getRandom();
            wait(1);
            clientPbUrl = urlRedirect + DateWrapper.getRandom();
            super.setUpPiggybacks();
            setAG = new HashSet<AudienceGroup>();
            setAG.add(wsHelper.createAudienceGroup(dataConsumer.getDataOwner()));
            dataPixel = wsHelper.createDataPixel(dataConsumer.getDataOwner(), setAG);
            systemPiggyback = wsHelper.createSystemPiggyback(systemPbUrl);
            Set<Pixel> pixels = new HashSet<Pixel>();
            pixels.add(dataPixel);
            clientPiggyback = wsHelper.createClientPiggyback(dataConsumer.getDataOwner().getId(), pixels, clientPbUrl);
            wait(2);
            //end of creating
            jmxWrapper = new JMXWrapper(ENV, configID, "pip");
            reloadpip();
            //set pixelId into pb.html
            browserPbLOG.info("Change pixelId at pb.html");
            SSHWrapper sshWrapper = new SSHWrapper(pathWebserver, "autotest", "812redaril");
            sshWrapper.getFile(filePbHtml, filePbHtml, pathPbHtml, true);
            FileHelper.getInstance().findAndReplaceStringAtFile(filePbHtml, "_pixel", "_pixel=" + dataPixel.getTagId() + ";");
            sshWrapper.putFile(filePbHtml, filePbHtml, pathPbHtml);
            FileHelper.getInstance().deleteFile(filePbHtml);
            //end

            isPbBrowserSetup = true;
        }
        webDriverWrapper.getDriver(driverInfo, proxyWrapper.getProxy(), null);
        goToPixelPage();
    }

    @Parameterized.Parameters
    public static List<Object[]> getParam() {
        System.setProperty(LogSystemProperty, logFile);
        LogConfigurer.initLogback();
        browserPbLOG = LoggerFactory.getLogger(PiggybackBrowser.class);
        return getDriversFromFile();
    }

    @Test
    public void testSystemPiggyback() {
        browserPbLOG.info(name.getMethodName() + " STARTED");
        browserPbLOG.info("OS = " + driverInfo.get("OS"));
        browserPbLOG.info("Version = " + driverInfo.get("version"));
        browserPbLOG.info("Browser = " + driverInfo.get("browser"));
        boolean isPassed = false;
        // execute test num times, because sometimes we get bad connection
        int i = 0;
        while (i < num && !isPassed) {
            int isFind = proxyWrapper.findRequestUrl(systemPbUrl);
            isPassed = isFind > 0;
            i++;
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        browserPbLOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testClientPiggyback() {
        browserPbLOG.info(name.getMethodName() + " STARTED");
        browserPbLOG.info("OS = " + driverInfo.get("OS"));
        browserPbLOG.info("Version = " + driverInfo.get("version"));
        browserPbLOG.info("Browser = " + driverInfo.get("browser"));
        boolean isPassed = false;
        // execute test num times, because sometimes we get bad connection
        int i = 0;
        while (i < num && !isPassed) {
            int isFind = proxyWrapper.findRequestUrl(clientPbUrl);
            isPassed = isFind > 0;
            i++;
        }
        assertTrue(name.getMethodName() + " FAILED", isPassed);
        browserPbLOG.info(name.getMethodName() + " PASSED");
    }

    @AfterClass
    public static void tearDownSystemPbs() {
        wsHelper.deleteSystemPiggyback(systemPiggyback);
        wsHelper.deleteDmpPiggyback(clientPiggyback);
        wsHelper.deleteDataPixel(dataPixel);
        for (AudienceGroup audienceGroup : setAG) {
            wsHelper.deleteAudienceGroup(audienceGroup);
        }
    }
}
