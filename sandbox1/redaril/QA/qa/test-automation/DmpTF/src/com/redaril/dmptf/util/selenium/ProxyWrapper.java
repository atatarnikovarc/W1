package com.redaril.dmptf.util.selenium;


import com.redaril.dmptf.util.file.FileHelper;
import org.browsermob.core.har.*;
import org.browsermob.proxy.ProxyServer;
import org.openqa.selenium.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.fail;

public class ProxyWrapper {
    private static Logger LOG;
    private ProxyServer server;
    protected final static String HOSTS_PATH = "data" + File.separator + "hosts" + File.separator;
    //! do not change k, because some client pb's tests will be failed
    private final static int k = 2;//min number of requests at any valid page

    public Proxy getProxy() {
        Proxy proxy = null;
        try {
            proxy = server.seleniumProxy();
        } catch (UnknownHostException e) {
            LOG.error("Can't create proxy");
        }
        return proxy;
    }

    private int generateRandomPort() {
        Random r = new Random();
        return r.nextInt(8976) + 1024;
    }

    public ProxyWrapper(String env, @Nullable String hosts) {
        LOG = LoggerFactory.getLogger(ProxyWrapper.class);
        // Logger LOG2 = LoggerFactory.getLogger("org.browsermob.proxy.ProxyServer");
        //LOG2.setLevel(Level.ERROR);
        boolean isCreated = false;
        int i = 0;
        while (!isCreated && i < 3) {
            try {
                int port = generateRandomPort();
                server = new ProxyServer(port);
                LOG.info("Create proxyServer");
                server.start();
                loadHosts(env, hosts);
                isCreated = true;
            } catch (Exception e) {
                LOG.error("Can't create proxyServer. Exception=" + e.toString());
                e.printStackTrace();
            }
            i++;
        }
    }

    public void stopProxyServer() {
        try {
            if (server != null) server.stop();
        } catch (Exception e) {
            LOG.error("Can't stop proxy server. Exception=" + e.getLocalizedMessage());
        }
    }

    public void loadHosts(String env, @Nullable String fileHosts) {
        String filename;
        if (fileHosts == null) filename = HOSTS_PATH + env + "hosts.txt";
        else filename = fileHosts;
        List<String> hosts = FileHelper.getInstance().getDataFromFile(filename);
        server.clearDNSCache();
        for (String host : hosts) {
            String ip = host.substring(0, host.indexOf(" "));
            String address = host.substring(host.indexOf(" ") + 1);
            server.remapHost(address, ip);
        }

    }

    public void watchUrl(String url) {
        server.newHar(url);
    }

    public void writeDataIntoFile(String filename) {
        Har har = server.getHar();
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(filename);
            har.writeTo(fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<HarEntry> getRequests() {
        Har har = server.getHar();
        return har.getLog().getEntries();
    }

    public int findRequestUrl(String url) {
        List<HarEntry> entries = getRequests();
        LOG.info("Try to find request = " + url);

        if (!isValidPage()) {
            LOG.error("Page is unavailable. Less than " + k + " request at page. Only " + entries.size() + " requests.");
            fail("Page is unavailable. Less than " + k + " request at page.");
            return -1;
        }
        int i = 1;
        for (HarEntry entry : entries) {
            HarRequest request = entry.getRequest();
            String requestUrl = request.getUrl();
            if (requestUrl.contains(url)) {
                LOG.info(i + " position. We found request = " + requestUrl);
                return i;
            }
            i++;
        }

        LOG.info("Can't find such request");
        return 0;

    }

    public boolean isValidPage() {
        List<HarEntry> entries = getRequests();
        return entries.size() >= k;
    }

    public String findCookieByNameAtRequests(String name) {
        List<HarEntry> entries = getRequests();
        LOG.info("Try to find cookie  = " + name);
        for (HarEntry entry : entries) {
            HarRequest request = entry.getRequest();
            List<HarCookie> list = request.getCookies();
            for (HarCookie cookie : list) {
                if (cookie.getName().equalsIgnoreCase(name)) {
                    LOG.info("Found value = " + cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        fail("Can't find cookie");
        return "";
    }

    public String findCookieByNameAtResponces(String name) {
        List<HarEntry> entries = getRequests();
        LOG.info("Try to find cookie  = " + name);
        LOG.info("Number of entries = " + entries.size());
        for (HarEntry entry : entries) {
            HarResponse response = entry.getResponse();

            List<HarCookie> list = response.getCookies();
            LOG.info("Number of cookies = " + list.size());
            for (HarCookie cookie : list) {
                LOG.info("Found cookie named " + cookie.getName());
                if (cookie.getName().equalsIgnoreCase(name)) {
                    LOG.info("Found value = " + cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        fail("Can't find cookie");
        return "";
    }

    public String findLastCookieByNameAtResponces(String name) {
        List<HarEntry> entries = getRequests();
        LOG.info("Try to find cookie  = " + name);
        LOG.info("Number of entries = " + entries.size());
        String myCookie = "";
        for (HarEntry entry : entries) {
            HarResponse response = entry.getResponse();

            List<HarCookie> list = response.getCookies();
            LOG.info("Number of cookies = " + list.size());
            for (HarCookie cookie : list) {
                LOG.info("Found cookie named " + cookie.getName());
                if (cookie.getName().equalsIgnoreCase(name)) {
                    LOG.info("Found value = " + cookie.getValue());
                    myCookie = cookie.getValue();
                }
            }
        }
        return myCookie;
    }

    public String findLastCookieByNameAtRequests(String name) {
        List<HarEntry> entries = getRequests();
        LOG.info("Try to find cookie  = " + name);
        LOG.info("Number of entries = " + entries.size());
        String myCookie = "";
        for (HarEntry entry : entries) {
            HarRequest request = entry.getRequest();
            List<HarCookie> list = request.getCookies();
            LOG.info("Number of cookies = " + list.size());
            for (HarCookie cookie : list) {
                LOG.info("Found cookie named " + cookie.getName());
                if (cookie.getName().equalsIgnoreCase(name)) {
                    LOG.info("Found value = " + cookie.getValue());
                    myCookie = cookie.getValue();
                }
            }
        }
        return myCookie;
    }

    //true if we get url1, then url2. Else - false
    public boolean getOrderOfRequests(String url1, String url2) {
        int i = findRequestUrl(url1);
        int j = findRequestUrl(url2);
        LOG.info("Url1 found at " + i + " position." + " Url2 found at " + j + " position.");
        if (i == 0 || j == 0) return false;
        else return i < j;
    }
}
