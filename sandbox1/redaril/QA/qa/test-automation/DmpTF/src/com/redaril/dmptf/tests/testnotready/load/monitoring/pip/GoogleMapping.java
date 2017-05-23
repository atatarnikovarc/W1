package com.redaril.dmptf.tests.testnotready.load.monitoring.pip;

import com.redaril.dmp.model.meta.DmpPiggyback;
import com.redaril.dmp.model.meta.Pixel;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.date.DateWrapper;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.network.appinterface.jmx.JMXWrapper;
import com.redaril.dmptf.util.network.appinterface.webservice.WSHelper;
import com.redaril.dmptf.util.network.protocol.ssh.SSHWrapper;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 02.04.13
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public class GoogleMapping {

    private static Logger LOG;
    private final static String PATH_CONFIG = "config" + File.separator;
    private final static String LogSystemProperty = "DmptfLogFile";
    private static String ENV;
    private static String ClusterId;
    private static WSHelper wsHelper;
    private final static long dataOwnerRAId = 4100;
    private final static long dataSourceRAInterestId = 1005;
    protected final static String pathWebserver = "10.50.150.130";
    protected final static String filePbHtml = "pb.html";
    private static int threadSysPb;
    private static int durationSysPb;
    private static int durationGMapping;
    private static String urlCST;
    private static int threadGMapping;
    private static String urlSysPb;
    private static String urlGMapping;
    private static String pipIp;
    private final static String FILE_PROPERTIES = "load.properties";
    private final static String ipHudson = "10.50.150.13";
    private static DefaultHttpClient httpClient;
    private static ConfigurationLoader config;
    private final static int pbCount = 3;
    private static List<com.redaril.dmp.model.meta.SystemPiggyback> systemPiggybackList;
    private static List<String> usersGMapping;
    private String urlToCreateUser;
    protected final static String FILE_PROPERTIES_APP = "app.properties";

    //threads
    // private static ExecutorService pool;

    @Before
    public void setup() {
        ConfigurationLoader conf = new ConfigurationLoader(PATH_CONFIG + "env.properties");
        ENV = conf.getProperty("env");
        ClusterId = conf.getProperty("configID");
        config = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES);
        ConfigurationLoader configEnv = new ConfigurationLoader(PATH_CONFIG + ENV + ".properties");
        pipIp = configEnv.getProperty("west.pip");
        System.setProperty(LogSystemProperty, "load.log");
        LogConfigurer.initLogback();
        LOG = LoggerFactory.getLogger(GoogleMapping.class);
        //  changeHosts();
        wsHelper = new WSHelper(ENV);
        init();
    }

    private void init() {
        //sysPb init
        //    threadSysPb = Integer.parseInt(config.getProperty("syspb.threads"));
        //    durationSysPb = Integer.parseInt(config.getProperty("syspb.duration"));
        // String url = config.getProperty("syspb.url");
        //   urlSysPb = url.replace("{pid}", getNewDataPixel(dataSourceRAInterestId));
        //   systemPiggybackList = getNewSystemPiggybacks(pbCount);
        //googleMapping init
        threadGMapping = Integer.parseInt(config.getProperty("gmap.threads"));
        durationGMapping = Integer.parseInt(config.getProperty("gmap.duration"));
        ConfigurationLoader configApp = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_APP);
        String baseDomain = configApp.getProperty("baseDomain");
        urlGMapping = "http://" + ENV + "." + ClusterId + ".p." + baseDomain + config.getProperty("gmap.url").replace("{port}", configApp.getProperty("httpPort"));
        urlCST = "http://" + ENV + "." + ClusterId + ".cst." + baseDomain + config.getProperty("gmap.urlCST").replace("{port}", configApp.getProperty("httpPort"));
        urlToCreateUser = "http://" + ENV + "." + ClusterId + ".p." + baseDomain + "/partners/universal/in?pid=9&ndl=hcasinc.com*";
        // urlToCreateUser = "http://env2.west.p.raasnet.com/partners/universal/in?ndl=hcasinc.com*&pid=85442";
        //todo: should we create httpClient for every test or use one?
        httpClient = createHttpClient(threadGMapping * 5);
    }

    private static void deleteExistedSysPb() {
        for (int i = 0; i < systemPiggybackList.size(); i++) {
            wsHelper.deleteSystemPiggyback(systemPiggybackList.get(i));
        }
    }

    //@Test
    public void testSysPb() {
        List<SysPbThread> threads = new ArrayList<SysPbThread>();
        ExecutorService pool = Executors.newFixedThreadPool(threadSysPb);
        LOG.info("duration = " + durationSysPb + " sec");
        LOG.info("threadCount = " + threadSysPb);
        LOG.info("url = " + urlSysPb);
        LOG.info("Start");
        for (int i = 0; i < threadSysPb; i++) {
            SysPbThread thread = new SysPbThread(durationSysPb, systemPiggybackList, urlSysPb, httpClient);
            pool.execute(thread);
            threads.add(thread);
        }
        wait(durationSysPb + 3);
        int requestCount = 0;
        int badRequestCount = 0;
        long requestTime = 0;
        for (SysPbThread pbThread : threads) {
            requestCount = requestCount + pbThread.getRequestCount();
            badRequestCount = badRequestCount + pbThread.getBadRequestCount();
            requestTime = requestTime + pbThread.getRequestTime();
        }
        LOG.info("requestCount = " + requestCount);
        LOG.info("badRequestCount = " + badRequestCount);
        LOG.info("requestTime = " + requestTime / 1000000000 + " sec");
        LOG.info("Average request time  = " + new Float(requestTime) / 1000000000 / requestCount);
        LOG.info("QPS = " + requestCount / durationSysPb);
        pool.shutdown();
        deleteExistedSysPb();
        assertTrue("!", requestCount / durationSysPb > 100);
    }

    //String URL = "http://" + ENV + "." + configID + "." + baseDomain + ":8080/partners/pixel?t=gcm&id=" + extUID + "&cver=1";
    @Test
    public void testGoogleMapping() {
        List<GoogleMappingThread> threads = new ArrayList<GoogleMappingThread>();
        ExecutorService poolMapping = Executors.newFixedThreadPool(threadGMapping);
        LOG.info("duration = " + durationGMapping + " sec");
        LOG.info("threadCount = " + threadGMapping);
        LOG.info("url = " + urlGMapping);
        LOG.info("Start");
        for (int i = 0; i < threadGMapping; i++) {
            GoogleMappingThread thread = new GoogleMappingThread(durationGMapping, urlToCreateUser, urlGMapping, urlCST, httpClient);
            poolMapping.execute(thread);
            threads.add(thread);
        }
        wait(durationGMapping + 3);
        int requestCount = 0;
        int badRequestCount = 0;
        long requestTime = 0;
        for (GoogleMappingThread pbThread : threads) {
            requestCount = requestCount + pbThread.getRequestCount();
            badRequestCount = badRequestCount + pbThread.getBadRequestCount();
            requestTime = requestTime + pbThread.getRequestTime();
        }
        LOG.info("requestCount = " + requestCount);
        LOG.info("badRequestCount = " + badRequestCount);
        LOG.info("requestTime = " + new Float(requestTime) / 1000000000 + " sec");
        LOG.info("Average request time  = " + new Float(requestTime) / 1000000000 / requestCount);
        LOG.info("QPS = " + new Float(requestCount) / durationGMapping);
        poolMapping.shutdown();
        assertTrue("!", requestCount / durationGMapping > 0);
    }

    // @AfterClass
    public static void teardown() {
        SSHWrapper sshWrapper = new SSHWrapper(ipHudson, "autotest", "812redaril");
        LOG.info("Revert file hosts");
        sshWrapper.putFile("defaultHosts", "hosts", "/etc/");
        sshWrapper.tearDown("defaultHosts");
        LOG.info("Revert Executed");
    }

    private DefaultHttpClient createHttpClient(int count) {
        PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(count);
        connectionManager.setMaxTotal(count);
        return new DefaultHttpClient(connectionManager);
    }

    private static void changeHosts() {
        SSHWrapper sshWrapper = new SSHWrapper(ipHudson, "autotest", "812redaril");
        sshWrapper.getFile("hosts", "hosts", "/etc/", true);
        //copy hosts which got from remote host
        FileHelper.getInstance().copyFile("hosts", "defaultHosts");
        //find all hostAdserver strings and comment it
        FileHelper.getInstance().findAndReplaceStringAtFile("hosts", " p.raasnet.com", pipIp + " p.raasnet.com");
        //transfer file hosts at remote host
        sshWrapper.putFile("hosts", "hosts", "/etc/");
        //delete local file
        sshWrapper.tearDown("hosts");
        LOG.info("changeHosts executed");

    }

//    private static List<String> getNewUsers(int number) {
//        List<String> users = new ArrayList<String>();
//        HttpClient httpClient = new HttpClient();
//        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
//        String uri = "http://" + ENV + "." + ClusterId + ".p.raasnet.com/partners/pixel?id=CAESEOS8DIt22VoL5E4ei5mPYhA&cver=1&t=gcm";
//        HttpMethod httpMethod;
//        for (int i = 0; i < number; i++) {
//            httpMethod = new GetMethod(uri);
//            try {
//                httpClient.executeMethod(httpMethod);
//            } catch (IOException e) {
//                LOG.info("Can't go to URL. Exception = " + e.getLocalizedMessage());
//            }
//            users.add(httpClient.getState().getCookies()[0].getValue());
//            LOG.info("i = " + i + " user = " + httpClient.getState().getCookies()[0].getValue());
//            httpMethod.releaseConnection();
//            httpClient.getState().clearCookies();
//        }
//        return users;
//    }

    private static List<com.redaril.dmp.model.meta.SystemPiggyback> getNewSystemPiggybacks(int number) {
        List<com.redaril.dmp.model.meta.SystemPiggyback> list = new ArrayList<com.redaril.dmp.model.meta.SystemPiggyback>();
        for (int i = 0; i < number; i++) {
            com.redaril.dmp.model.meta.SystemPiggyback pb = wsHelper.createSystemPiggyback("SysPb" + DateWrapper.getRandom());
            list.add(pb);
        }
        return list;
    }

    private static List<DmpPiggyback> getNewDmpPiggybacks(long dataOwnerId, Set<Pixel> pixels, int number) {
        List<DmpPiggyback> list = new ArrayList<DmpPiggyback>();
        for (int i = 0; i < number; i++) {
            DmpPiggyback pb = wsHelper.createClientPiggyback(dataOwnerId, pixels, "SysPb" + DateWrapper.getRandom());
            list.add(pb);
        }
        return list;
    }

    private static String getNewDataPixel(long dataSourceId) {
        return wsHelper.createDataPixel(Long.toString(dataSourceId));
    }

    private static List<String> getNewUsers(int count, String urlToCreateUser, DefaultHttpClient httpClient) {
        List<String> list = new ArrayList<String>();
        int errors = 0;
        long start = System.nanoTime();
        while (list.size() < count && errors <= (count / 2)) {
            httpClient.getCookieStore().clear();
            try {
                HttpGet httpget = new HttpGet("http://p.raasnet.com/partners/universal/in?pid=9&ndl=http%3A%2F%2Fenv1.webserver%3A8000%2Fautomation%2FEnv-1%2Fpip%2Fpb.html");
                httpClient.execute(httpget);
            } catch (IOException e) {
            }
            List<Cookie> cookieList = httpClient.getCookieStore().getCookies();
            boolean isFind = false;
            for (Cookie cookie : cookieList) {
                if (cookie.getName().equalsIgnoreCase("u")) {
                    list.add(cookie.getValue());
                    isFind = true;
                    break;
                }
            }
            if (!isFind) errors++;
        }
        if (list.size() < count) {
            LOG.error("Errors ar creating users.");
            fail("Errors ar creating users. UrlToCreate = " + urlToCreateUser);
            return null;
        } else return list;
    }

    private static List<com.redaril.dmp.model.meta.SystemPiggyback> getExistedSystemPiggybacks(int number) {
        // I added here most used LoadSystemPiggyback's, for example Google, IXI, RapLeaf,Datonics, Bizo...
        List<Long> existedSystemPb = new ArrayList<Long>();
        existedSystemPb.add(3658574L);
        existedSystemPb.add(3658589L);
        existedSystemPb.add(3658679L);
        existedSystemPb.add(3830266L);
        existedSystemPb.add(3658477L);
        existedSystemPb.add(3658487L);
        existedSystemPb.add(3658534L);
        existedSystemPb.add(3658636L);
        existedSystemPb.add(3658574L);
        existedSystemPb.add(3680035L);
        List<com.redaril.dmp.model.meta.SystemPiggyback> list = new ArrayList<com.redaril.dmp.model.meta.SystemPiggyback>();
        for (Long id : existedSystemPb) {
            list.add(wsHelper.getSystemPiggyback(id));
        }
        if (list.size() < number) return list;
        else return list.subList(0, number);
    }

    private static List<DmpPiggyback> getExistedDmpPiggybacks(int number, long dataOwnerId) {
        List<DmpPiggyback> list = wsHelper.getDmpPiggybacks(dataOwnerId);
        if (list.size() < number) {
            //LOG.info("Find " + list.size() + "piggybacks for dataOwner = " + dataOwnerId);
            return list;
        }
        return list.subList(0, number - 1);
    }

    private static void reloadPIP() {
        JMXWrapper jmxWrapper = new JMXWrapper(ENV, ClusterId, "pip");
        jmxWrapper.execCommand("doReload");
        jmxWrapper.waitForReloading();
    }

    private static void reloadCST() {
        JMXWrapper jmxWrapperCST = new JMXWrapper(ENV, ClusterId, "cst");
        jmxWrapperCST.execCommand("doReload");
        jmxWrapperCST.waitForReloading();
    }

//    private static void setupSystemPbs(String pathToFile, String pixelId) {
//        SSHWrapper sshWrapper = new SSHWrapper(pathWebserver);
//        sshWrapper.getFile(filePbHtml, filePbHtml, pathToFile, true);
//        FileHelper.getInstance().findAndReplaceStringAtFile(filePbHtml, "_pixel", "_pixel=" + pixelId + ";");
//        sshWrapper.putFile(filePbHtml, filePbHtml, pathToFile);
//        FileHelper.getInstance().deleteFile(filePbHtml);
//
//    }

    private static void wait(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
