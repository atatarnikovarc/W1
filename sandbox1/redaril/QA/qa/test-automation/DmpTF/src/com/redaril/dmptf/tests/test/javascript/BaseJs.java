package com.redaril.dmptf.tests.test.javascript;

import com.redaril.dmptf.tests.support.pip.base.BaseSeleniumTest;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.database.oracle.OracleWrapper;
import com.redaril.dmptf.util.date.DateWrapper;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.network.appinterface.jmx.JMXWrapper;
import com.redaril.dmptf.util.network.appinterface.webservice.WSHelper;
import com.redaril.dmptf.util.network.protocol.ssh.SSHWrapper;
import com.redaril.dmptf.util.selenium.ProxyWrapper;
import com.redaril.dmptf.util.selenium.WebDriverWrapper;
import org.junit.Before;
import org.slf4j.Logger;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 20.06.13
 * Time: 12:53
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseJs extends BaseSeleniumTest {
    protected static Logger LOG;
    protected static HashMap<String, String> driverInfo;
    protected static boolean isSetup;
    protected final static String universalHtml = "html.html";
    protected final static String universalHtml5 = "html5.html";
    protected final static String universalXHtml = "xhtml.html";
    private final static int num = 3;//number of executing tests if it fails
    private static String dataPixelId;
    private static String systemPbUrl;
    private final static String dataSourceId = "1005";
    private static String baseDomain;
    private static int port;
    private static OracleWrapper oracleWrapper;
    protected final static String SOURCE_SQL = "data" + File.separator + "piggybacks" + File.separator;
    protected final static String cleanSysPbDB = SOURCE_SQL + "updateSystemPbWeight.sql";
    protected final static String cleanClientPbDB = SOURCE_SQL + "updateClientPbWeight.sql";
    protected final static String pathWebserver = "qa-10.qa.coreaudience.com";
    protected static String pathAtTomcat;
    private static String httpURL;
    private static String pathHtml;
    protected static String pixelParam;
    protected final static String sourceDrivers = SOURCE_PATH + "driversProxy.txt";
    private static SSHWrapper sshWebserver;

    @Before
    public void setup() {
        if (!isSetup) {
            httpURL = "http://" + pathWebserver + ":8080/" + pathAtTomcat;
            pathHtml = "/var/lib/tomcat6/webapps/ROOT/" + pathAtTomcat;
            configEnv = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
            ENV = configEnv.getProperty("env");
            configID = configEnv.getProperty("configID");
            ConfigurationLoader configApp = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_APP);
            port = Integer.valueOf(configApp.getProperty("httpPort"));
            baseDomain = configApp.getProperty("baseDomain");
            oracleWrapper = new OracleWrapper(ENV, "dmp");
            cleanExistedPb();
            webDriverWrapper = new WebDriverWrapper(ENV);
            getDataPixel();
            changeParamAtHtmlPage(universalHtml, pixelParam, pixelParam + "=" + dataPixelId + ";");
            changeParamAtHtmlPage(universalHtml5, pixelParam, pixelParam + "=" + dataPixelId + ";");
            changeParamAtHtmlPage(universalXHtml, pixelParam, pixelParam + "=" + dataPixelId + ";");
            createPb();
            //add p0.raasnet.com as webserver into hosts
            FileHelper.getInstance().copyFile("data/hosts/" + ENV + "hosts.txt", "hosts");
            FileHelper.getInstance().findAndReplaceStringAtFile("hosts", "p0.raasnet.com", "10.33.4.47 p0.raasnet.com");
            proxyWrapper = new ProxyWrapper(ENV, "hosts");
            sshWebserver = new SSHWrapper(pathWebserver, "autotest", "812redaril");
            isSetup = true;
        }
        webDriverWrapper.getDriver(driverInfo, proxyWrapper.getProxy(), "hosts");
        LOG.info("OS = " + driverInfo.get("OS"));
        LOG.info("Version = " + driverInfo.get("version"));
        LOG.info("Browser = " + driverInfo.get("browser"));
    }

    protected boolean testHtml(String fileName, boolean isNewUser) {
        boolean isPassed = false;
        // execute test num times, because sometimes we get bad connection
        int i = 0;
        while (i < num && !isPassed) {
            if (webDriverWrapper.getCookieByName("u") != null) webDriverWrapper.deleteAllCookies();
            if (!isNewUser) {
                createUser();
            }
            proxyWrapper.watchUrl(httpURL + fileName);
            LOG.info("Go to url = " + httpURL + fileName);
            webDriverWrapper.getPage(httpURL + fileName);
            wait(5000);
            int isFind = proxyWrapper.findRequestUrl(systemPbUrl);
            isPassed = isFind > 0;
            i++;
        }
        return isPassed;
    }

    private void cleanExistedPb() {
        String script = FileHelper.getInstance().getDataWithoutParams(cleanSysPbDB);
        LOG.info("Clean System piggybacks. Script = " + script);
        oracleWrapper.executeUpdate(script);
        script = FileHelper.getInstance().getDataWithoutParams(cleanClientPbDB);
        LOG.info("Clean Client piggybacks. Script = " + script);
        oracleWrapper.executeUpdate(script);
    }

    private void getDataPixel() {
        List<String> params = new ArrayList<>();
        params.add(dataSourceId);
        String script = FileHelper.getInstance().getDataWithParams("data/qualifiers/regular/getPixelIDbyDataSource.sql", params);
        ResultSet set = oracleWrapper.executeSelect(script);
        try {
            while (set.next()) {
                dataPixelId = set.getString(1);
            }
            set.close();
            oracleWrapper.closeStatement();
        } catch (SQLException e) {
            LOG.error(e.getLocalizedMessage());
        }
        if (dataPixelId == null) {
            fail("Can't get dataPixel by dataSource = " + dataSourceId);
        }
    }

    private void createPb() {
        systemPbUrl = "example.com/?" + DateWrapper.getRandom();
        WSHelper wsHelper = new WSHelper(ENV);
        wsHelper.createSystemPiggyback(systemPbUrl);
        wait(10000);
        JMXWrapper jmxWrapper = new JMXWrapper(ENV, configID, "pip");
        jmxWrapper.execCommand("doReload");
        jmxWrapper.waitForReloading();
    }

    private void createUser() {
        webDriverWrapper.deleteAllCookies();
        String urlToCreateUser = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/universal/in?pid=9&ndl=hcasinc.com*";
        LOG.info("Create user. URL = " + urlToCreateUser);
        webDriverWrapper.getPage(urlToCreateUser);
        LOG.info("Created user = " + webDriverWrapper.getCookieByName("u"));
    }

    protected void changeParamAtHtmlPage(String fileName, String find, String replace) {
        sshWebserver = new SSHWrapper(pathWebserver, "autotest", "812redaril");
        sshWebserver.getFile(fileName, fileName, pathHtml, true);
        FileHelper.getInstance().findAndReplaceStringAtFile(fileName, find, replace);
        sshWebserver.putFile(fileName, fileName, pathHtml);
        FileHelper.getInstance().deleteFile(fileName);
        //end
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
}
