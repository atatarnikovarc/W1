package com.redaril.dmptf.tests.testnotready.load.monitoring.pip;

import com.redaril.dmp.model.meta.SystemPiggyback;
import com.redaril.dmptf.util.date.DateWrapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 25.03.13
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class GoogleMappingThread implements Runnable {
    private Logger LOG;
    private List<SystemPiggyback> piggybacks;
    private String request;
    private String urlToCreateUser;
    private String requestCST;
    private int duration;
    private int requestCount;
    private long requestTime;
    private int badRequestCount;
    private DefaultHttpClient httpClient;

    /*duration - in sec, how long make a loading
    request to make GET-request
    httpClient - to connect to remote host
    */
    public GoogleMappingThread(int duration, String urlToCreateUser, String request, String requestCST, DefaultHttpClient httpClient) {
        this.request = request;
        this.urlToCreateUser = urlToCreateUser;
        this.requestCST = requestCST;
        this.duration = duration;
        this.httpClient = httpClient;
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

    public void run() {
        LOG = LoggerFactory.getLogger(SysPbThread.class);
        long startThread = System.nanoTime();
        requestCount = 0;
        requestTime = 0;
        long start, end;
        badRequestCount = 0;
        String bodyResponse;
        //create user
//        CookieStore cookieStore = new BasicCookieStore();
//        HttpContext localContext = new BasicHttpContext();
//        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
//        //create user
//        HttpGet httpget = new HttpGet(urlToCreateUser);
//        try {
//            httpClient.execute(httpget,localContext);
//        } catch (IOException e) {
//            LOG.info("IOException while get " + urlToCreateUser);
//        }
//        String ucookie="";
//        for (Cookie cookie : cookieStore.getCookies()) {
//            if (cookie.getName().equalsIgnoreCase("u")) {
//                ucookie = cookie.getValue();
//                break;
//            }
//        }
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        //create user
        HttpGet httpget = new HttpGet(urlToCreateUser);
        try {
            httpClient.execute(httpget, localContext);
        } catch (IOException e) {
            LOG.info("IOException while get " + urlToCreateUser);
        }
        String ucookie = "";
        for (Cookie cookie : cookieStore.getCookies()) {
            if (cookie.getName().equalsIgnoreCase("u")) {
                ucookie = cookie.getValue();
                break;
            }
        }
        // LOG.info("Ucookie = " + ucookie);
        while ((System.nanoTime() - startThread) / 1000000000 < duration) {
            boolean isGood = false;
            try {
                //google mapping
                String generatedId = DateWrapper.getRandom();
                httpget = new HttpGet(request + generatedId);
                start = System.nanoTime();
                LOG.info("request-");
                httpClient.execute(httpget, localContext);
                LOG.info("request+");
                end = System.nanoTime();
                //get model from CST
                if (ucookie != "") {
                    httpget = new HttpGet(requestCST + ucookie);
                    HttpResponse httpresponse = httpClient.execute(httpget);
                    bodyResponse = getBody(httpresponse.getEntity().getContent());
                    if (bodyResponse.contains(generatedId)) {
                        requestCount++;
                        isGood = true;
                        requestTime = requestTime + (end - start);
                    }
                }
            } catch (IOException e) {
                LOG.debug(e.getLocalizedMessage());
            } finally {
                if (!isGood) badRequestCount++;
                // httpClient.getCookieStore().clear();
            }
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
}
