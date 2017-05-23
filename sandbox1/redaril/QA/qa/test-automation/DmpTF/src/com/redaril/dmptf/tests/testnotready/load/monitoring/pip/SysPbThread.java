package com.redaril.dmptf.tests.testnotready.load.monitoring.pip;

import com.redaril.dmp.model.meta.SystemPiggyback;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
 * Date: 20.03.13
 * Time: 10:47
 * To change this template use File | Settings | File Templates.
 */
public class SysPbThread implements Runnable {
    private Logger LOG;
    private List<SystemPiggyback> piggybacks;
    private String request;
    private int duration;
    private int requestCount;
    private long requestTime;
    private int badRequestCount;
    private DefaultHttpClient httpClient;

    /*duration - in sec, how long make a loading
      list of piggyback to check
      request to make GET-request
      httpClient - to connect to remote host
      */
    public SysPbThread(int duration, List<SystemPiggyback> list, String request, DefaultHttpClient httpClient) {
        this.piggybacks = list;
        this.request = request;
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
        while ((System.nanoTime() - startThread) / 1000000000 < duration) {
            httpClient.getCookieStore().clear();
            try {
                bodyResponse = "";
                HttpGet httpget = new HttpGet(request);
                start = System.nanoTime();
                HttpResponse httpresponse = httpClient.execute(httpget);
                end = System.nanoTime();
                bodyResponse = getBody(httpresponse.getEntity().getContent());
                if (checkResponse(bodyResponse)) {
                    requestCount++;
                    requestTime = requestTime + (end - start);
                } else {
                    badRequestCount++;
                }
            } catch (IOException e) {
                LOG.debug(e.getLocalizedMessage());
            }
        }
    }

    private boolean checkResponse(String body) {
        for (SystemPiggyback pb : piggybacks) {
            String url = pb.getUrl();
            if (url.contains("{")) url = url.substring(0, url.indexOf("{"));
            if (!body.contains(url)) return false;
        }
        return true;
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
