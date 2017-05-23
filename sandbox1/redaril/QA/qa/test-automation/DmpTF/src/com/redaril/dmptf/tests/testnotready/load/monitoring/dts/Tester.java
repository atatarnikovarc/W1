package com.redaril.dmptf.tests.testnotready.load.monitoring.dts;

/**
 * Created with IntelliJ IDEA.
 * User: atatarnikov
 * Date: 3/1/13
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User: Ilya Muromtsev
 * Date: Jan 13, 2011
 * Time: 8:59:28 PM
 */
public class Tester {
    private static ExecutorService pool;
    private static final int MAX_PACKET_SIZE = 1024;

    public static void main(String[] args) throws IOException {
        if (args.length < 5) {
            System.out.println("java Tester <nThreads> <nRequests> <maxWaitTime (ms)> <file with Template> <url>");
            return;
        }
        final int nThreads = Integer.parseInt(args[0]);
        pool = Executors.newFixedThreadPool(nThreads);
        System.out.println("Pool initialized with " + nThreads + " threads");

        int nRequests = Integer.parseInt(args[1]);
        final long maxWaitTime = Long.parseLong(args[2]);

        String templateFileName = args[3];
        final String template = getContent(new FileInputStream(templateFileName));
        final String url = args[4];

        AtomicReference<Map<Integer, Integer>> codeMap = new AtomicReference<Map<Integer, Integer>>(new HashMap<Integer, Integer>());
        AtomicInteger timeouts = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < nRequests; ++i) {
            pool.execute(new Worker(maxWaitTime, i, nThreads, codeMap, timeouts, url, template));
        }
        pool.shutdown();
        try {
            pool.awaitTermination(maxWaitTime * nRequests, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            pool.shutdownNow();
        } finally {
            System.out.printf("\nTest executed in %.3f seconds\n", 1E-03 * (System.currentTimeMillis() - startTime));
            System.out.println("\nPassed: ");
            for (Map.Entry<Integer, Integer> entry : codeMap.get().entrySet()) {
                System.out.printf("\t code %d --> %d times: %.2f%%\n", entry.getKey(), entry.getValue(), 100.0f * entry.getValue() / nRequests);
            }
            System.out.println("Timed out: " + timeouts.get() + "/" + nRequests);
        }
    }

    @NotNull
    private static String getContent(InputStream stream) throws IOException {
        OutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[MAX_PACKET_SIZE];
        int add;
        while ((add = stream.read(b)) != -1) {
            bos.write(b, 0, add);
        }
        stream.close();
        return bos.toString();
    }

    static ThreadLocal<HttpClient> tclient = new ThreadLocal<HttpClient>();


    static class Worker implements Runnable {
        long maxWaitTime;
        int i;
        int nThreads;
        private AtomicReference<Map<Integer, Integer>> codeMap = new AtomicReference<Map<Integer, Integer>>();
        private AtomicInteger timeouts;
        private String url;
        private String template;

        Worker(long maxWaitTime, int i, int nThreads, AtomicReference<Map<Integer, Integer>> codeMap, AtomicInteger timeouts, String url, String template) {
            this.maxWaitTime = maxWaitTime;
            this.i = i;
            this.nThreads = nThreads;
            this.codeMap = codeMap;
            this.timeouts = timeouts;
            this.url = url;
            this.template = template;
        }

        @Override
        public void run() {
            if (tclient.get() == null) {
                tclient.set(new HttpClient());
            }
            HttpClient client = tclient.get();
            PostMethod method = new PostMethod(url);
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(1, false));
            method.setRequestEntity(new ByteArrayRequestEntity(template.getBytes(), "application/json"));

            long ms = System.currentTimeMillis();
            try {
                int code = client.executeMethod(method);
                Integer codeCount = codeMap.get().get(code);
                if (codeCount == null) {
                    codeCount = 0;
                }
                codeMap.get().put(code, ++codeCount);
            } catch (IOException e) {
                e.printStackTrace();
                //pool.shutdownNow();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } finally {
                long res = System.currentTimeMillis() - ms;
                if (res > maxWaitTime) {
                    System.out.println("\n" + i + ": Timeout: " + res + " ms");
                    timeouts.incrementAndGet();
                } else {
                    System.out.print("." + (i % nThreads == 0 ? "\n" : ""));
                }
            }
        }
    }

}
