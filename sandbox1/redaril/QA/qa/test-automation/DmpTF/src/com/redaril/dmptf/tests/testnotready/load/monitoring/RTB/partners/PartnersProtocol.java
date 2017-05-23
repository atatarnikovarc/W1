package com.redaril.dmptf.tests.testnotready.load.monitoring.RTB.partners;

import com.redaril.dmptf.tests.testnotready.load.monitoring.RTB.RTBProtocol;

import java.nio.ByteBuffer;

public class PartnersProtocol extends RTBProtocol {

    private String ip;

    public PartnersProtocol(String ip) {
        this.ip = ip;
    }

    @Override
    public Object write(ByteBuffer buf, int timeout, Object... payload) throws Exception {
        buf.put("GET /partners/universal/in?pid=9 HTTP/1.1\r\n".getBytes());
        buf.put(("Host: " + ip + "\r\n").getBytes());
        buf.put("Accept-Encoding: gzip, deflate\r\n".getBytes());
        buf.put("User-Agent: Mozilla/5.0 (Windows NT 6.1; rv:8.0) Gecko/20100101 Firefox/8.0\r\n".getBytes());
        buf.put("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n".getBytes());
        buf.put("Cookie: o=0; u=157380486628577\r\n\r\n".getBytes());
        return null;
    }

    @Override
    public Object read(ByteBuffer buf, Object request) throws Exception {
        for (int i = 0; i < buf.position() - 4; i++)
            if ((buf.get(i) == 13 && buf.get(i + 1) == 10 && buf.get(i + 2) == 13 && buf.get(i + 3) == 10) || (buf.get(i) == 10 && buf.get(i + 1) == 10)) {
                buf.flip();
                buf.position(i + 4);
                byte[] bbuf = new byte[buf.limit() - buf.position()];
                buf.get(bbuf);
                return bbuf;
            }
        return null;
    }


    @Override
    public Object createBidRequest() {
        return null;
    }
}
