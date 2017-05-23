package com.redaril.dmptf.tests.testnotready.load.monitoring.pip;

import com.redaril.dmp.model.meta.SystemPiggyback;
import com.redaril.dmptf.tests.test.qualifiers.RegQualifierForTest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 25.04.13
 * Time: 16:00
 * To change this template use File | Settings | File Templates.
 */
public class QualifierThread implements Callable {
    private Logger LOG;
    private List<SystemPiggyback> piggybacks;
    private String request;
    private String urlToCreateUser;
    private String requestCST;
    private int duration;
    private int requestCount;
    private long requestTime;
    private int badRequestCount;
    private RegQualifierForTest qualifier;
    private DefaultHttpClient httpClient;
    private String PARTNERSINFO_URL_EX;
    private String QUALIFIER_URL_PREFIX;
    private int number;


    public QualifierThread(int number, RegQualifierForTest qualifier, DefaultHttpClient httpClient, String ENV, String ConfigId, String baseDomain, int port) {
        this.qualifier = qualifier;
        this.httpClient = httpClient;
        this.PARTNERSINFO_URL_EX = "http://" + ENV + ".west.p." + baseDomain + ":" + port + "/partners/info?ex=1";
        this.QUALIFIER_URL_PREFIX = "http://" + ENV + ".west.p." + baseDomain + ":" + port + "/partners/universal/in?pid=";
        this.number = number;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public int getBadRequestCount() {
        return badRequestCount;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public Boolean call() {
        LOG = LoggerFactory.getLogger(SysPbThread.class);
        String url = qualifier.getUrl();
        String interest = qualifier.getInterestName();
        String id = qualifier.getId();
        String ocook = "";
        String ucook = "";
        String userModel = "";
        httpClient.getCookieStore().clear();
        if (url.contains(QUALIFIER_URL_PREFIX + "&ndr"))
            fail("Can't get and create pixel for this Qualifier(ID = " + id + " )");
        try {
            HttpGet httpget = new HttpGet(url);
            httpClient.execute(httpget);
            httpget = new HttpGet(PARTNERSINFO_URL_EX);
            HttpResponse httpresponse = httpClient.execute(httpget);
            userModel = getBody(httpresponse.getEntity().getContent());
            ocook = getCookieValueByName(httpClient.getCookieStore().getCookies(), "o");
            ucook = getCookieValueByName(httpClient.getCookieStore().getCookies(), "u");
        } catch (Exception e) {
        }
        Boolean result = userModel.contains(interest.trim());
        if (result) {
            LOG.info("\r\n" + number + ". Start to check qualifier: " + id + "\r\n" + "interestCategory: " + interest + ";\r\n" + "URL: " + url + ";\r\n" + "cookie o=" + ocook + ";\r\n" + "cookie u=" + ucook + ";\r\n" + "userModel:" + userModel + "\r\n" + "qualifier ID = " + id + " is PASSED" + "\r\n" + "-----------------------------------------\r\n ");
            return true;
        } else {
            LOG.error("\r\n" + number + ". Start to check qualifier: " + id + "\r\n" + "interestCategory: " + interest + ";\r\n" + "URL: " + url + ";\r\n" + "cookie o=" + ocook + ";\r\n" + "cookie u=" + ucook + ";\r\n" + "userModel:" + userModel + "\r\n" + "qualifier ID = " + id + " is FAILED" + "\r\n" + "-----------------------------------------\r\n ");
            return false;
        }
    }

    private String getBody(InputStream in) {
        InputStreamReader inputStreamReader = new InputStreamReader(in);
        BufferedReader reader1 = new BufferedReader(inputStreamReader);
        String s;
        String body = "";
        try {
            while ((s = reader1.readLine()) != null) {
                body = body + s;
            }
            return body;
        } catch (IOException e) {
            LOG.info("Exception = " + e.getLocalizedMessage());
        }
        return body;
    }

    private String getCookieValueByName(List<Cookie> cookies, String name) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(name)) {
                return cookie.getValue();
            }
        }
        return "";

    }
}
