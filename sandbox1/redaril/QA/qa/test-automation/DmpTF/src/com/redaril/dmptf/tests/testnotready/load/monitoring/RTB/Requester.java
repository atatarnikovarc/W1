package com.redaril.dmptf.tests.testnotready.load.monitoring.RTB;

import com.redaril.monitoring.TimePeriodicMetric;
import com.redaril.nio.client.NioClientSync2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Requester<T, V> {

    private final NioClientSync2 sender;
    private volatile int cnt;

    public Requester(String ip, int port, int numConnections, RTBProtocol protocol, boolean closeConnection) throws Exception {

        sender = new NioClientSync2(ip, port, numConnections, protocol);
        sender.setAlwaysCloseConnection(closeConnection);
    }

    public int getFeedSize() {
        return sender.getConnectionCount();
    }

    public V sendAndReceive(T bidRequest) throws Exception {
        if (cnt % 10000 == 0)
            System.out.println("qps=" + ((TimePeriodicMetric) sender.getMetric()).getCurrentQps()
                    + " t=" + ((TimePeriodicMetric) sender.getMetric()).getCurrentAverageHandlingTime()
                    + " tmax=" + ((TimePeriodicMetric) sender.getMetric()).getCurrentMaximumHandlingTime()
                    + " timeout=" + ((TimePeriodicMetric) sender.getMetric()).getCurrentTimeouts());
        final Future<Object> objectFuture = sender.send(4500, bidRequest);
        V response = null;
        try {
            response = (V) objectFuture.get();
        } catch (ExecutionException e) {
            // e.getCause().printStackTrace();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        cnt++;
        return response;
    }


}
