package com.redaril.dmptf.tests.support.pip.base;

import com.redaril.dmptf.tests.support.etl.EtlLogAnalyzer;
import com.redaril.dmptf.tests.support.etl.model.Model;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.network.lib.httpunit.HttpUnitWrapper;
import com.redaril.dmptf.util.network.protocol.ssh.SSHWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


abstract public class BaseCookieMapping {

    //FILE and PATH
    protected final static String PATH_CONFIG = "config" + File.separator;

    private final static String FILE_PROPERTIES_ENV = "env.properties";
    protected final static String FILE_PROPERTIES_APP = "app.properties";
    private final static String logFile = "cookiemapping.log";
    protected final static String LogSystemProperty = "DmptfLogFile";

    //TEST

    //cookies
    protected final static String ucookie = "u";
    protected String mycookie;

    //etl
    protected static EtlLogAnalyzer fileAnalyzer;
    protected static Model model;

    //other
    protected static String CST;
    protected static SSHWrapper SSH;
    protected static HttpUnitWrapper session;

    //CONFIGURATION
    protected static ConfigurationLoader testClassConfigurationLoader;
    protected static String ENV;
    protected static String configID;
    protected static String baseDomain;
    private static Logger LOG;
    protected static Boolean isInit = false;
    protected static int port;

    public void setUp() {
        if (!isInit) {
            ConfigurationLoader envConfigurationLoader = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
            ENV = envConfigurationLoader.getProperty("env");
            configID = envConfigurationLoader.getProperty("configID");
            ConfigurationLoader configApp = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_APP);
            port = Integer.valueOf(configApp.getProperty("httpPort"));
            baseDomain = configApp.getProperty("baseDomain");
            System.setProperty(LogSystemProperty, logFile);
            LogConfigurer.initLogback();
            LOG = LoggerFactory.getLogger(BaseCookieMapping.class);
            CST = "http://" + ENV + "." + configID + ".cst." + baseDomain + ":" + port + "/cacheservertester/cserver?uid=me";
            session = new HttpUnitWrapper();
            createRAUser();
            testClassConfigurationLoader = new ConfigurationLoader(PATH_CONFIG + ENV + ".properties");
            String hostToCheckEtl = testClassConfigurationLoader.getProperty("host.etl");
            SSH = new SSHWrapper(hostToCheckEtl, "autotest", "812redaril");
            fileAnalyzer = new EtlLogAnalyzer();
            model = new Model();
            isInit = true;
        }
    }

    protected void createRAUser() {
        String urlToCreateUser = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/universal/in?pid=9&ndl=hcasinc.com*";
        session.deleteAllCookies();
        LOG.info("----Create new RA User----");
        session.goToUrl(urlToCreateUser);
    }

    //uses to get value of parameter, when we know this name
    public String getValueFromString(String url, String uid) {
        return url.substring(url.lastIndexOf(uid) + uid.length() + 1);
    }

    //uses when extUID gets from source URL
    public Boolean checkSimpleMapping(String url, String extUid) {
        LOG.info("Go to URL = " + url);
        session.goToUrl(url);
        mycookie = session.getCookieValueByName(ucookie);
        LOG.info("Get cookie from browser ucookie = " + mycookie);
        LOG.info("Get Mapping from CST = " + CST);
        String userModel = session.getResponsePage(CST);
        Boolean isMap = userModel.contains(extUid);
        LOG.info("Verificate mapping");
        if (isMap) {
            LOG.info("Mapping is correct.");
        } else {
            LOG.error("Mapping is incorrect.");
        }
        return isMap;
    }

    //uses when extUID gets from source URL and should be redirect after request
    public Boolean checkSimpleMappingWithRedirect(String url, String extUid, String redirectURL) {
        LOG.info("Go to URL = " + url);
        session.goToUrl(url);
        String curUrl = session.getCurrentUrl();
        LOG.info("Get URL after redirect. URL = " + curUrl);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {

        }
        mycookie = session.getCookieValueByName(ucookie);
        LOG.info("Get cookie from browser ucookie = " + mycookie);
        LOG.info("Get Mapping from CST = " + CST);
        String userModel = session.getResponsePage(CST);
        LOG.info("Verificate mapping and redirect URL");
        Boolean isMap = userModel.contains(extUid) && curUrl.equalsIgnoreCase(redirectURL);
        if (isMap) {
            LOG.info("Mapping and redirect is correct.");
        } else {
            LOG.error("Mapping or redirect is incorrect.");
        }
        return (isMap);
    }

    //uses when extUID gets from URL after request
    //at response we send extUID
    public String checkMappingURL(String url, String extUid) {
        LOG.info("Go to URL = " + url);
        String getLoc = session.getLocation(url);
        LOG.info("Get current URL. URL = " + getLoc);
        String uidValue = getValueFromString(getLoc, extUid);
        LOG.info("Get UID from URL. UID = " + uidValue);
        mycookie = session.getCookieValueByName(ucookie);
        LOG.info("Get cookie from browser ucookie = " + mycookie);
        LOG.info("Verificate mapping");
        String userModel = session.getResponsePage(CST);
        if (userModel.contains(uidValue)) {
            LOG.info("Mapping is correct.");
        } else {
            LOG.error("Mapping is incorrect.");
        }
        return uidValue;
    }
}
