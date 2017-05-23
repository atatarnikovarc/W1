package com.redaril.dmptf.tests.testnotready.load.monitoring.RTB.openx;

import com.google.protobuf.ByteString;
import com.redaril.dmptf.tests.testnotready.load.monitoring.RTB.RTBProtocol;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class OpenXRtbProtocol extends RTBProtocol {

    private String ip;

    public OpenXRtbProtocol(String ip) {
        this.ip = ip;
    }

    @Override
    public Object write(ByteBuffer buf, int timeout, Object... payload) throws Exception {
        buf.put("POST /api/openx HTTP/1.1\r\n".getBytes());
        buf.put(("Host: " + ip + "\r\n").getBytes());
        buf.put("X-OpenX-Id: c93892fa-5ab6-3f13-2041-7ca35d6d0f0a\r\n".getBytes());
        buf.put("Content-Type: application/octet-stream\r\n".getBytes());

        OpenXRealtimeBidding.BidRequest.Builder requestBuilder = OpenXRealtimeBidding.BidRequest.newBuilder()
                .setApiVersion(1)
                .setAuctionId("101f97ff-21b2-4449-94c2-1fda528bdfb8")
                .setUserCookieId("c93892fa-5ab6-3f13-2041-7ca35d6d0f0a")
                .setUserGeoCountry("us")
                .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.202 Safari/535.1")
                .setUserIpAddress(ByteString.copyFrom(new byte[]{63, 119, 58, 24}))
                .setAdWidth(300)
                .setAdHeight(250)
                .setPubWebsiteId("98a75421-7f6a-4f48-96cb-7f3b41a4e57c")
                .addMatchingAdIds(
                        OpenXRealtimeBidding.AdId.newBuilder()
                                .setCampaignId(70430)
                                .setPlacementId(170360)
                                .setCreativeId(171920)
                ).addMatchingAdIds(
                        OpenXRealtimeBidding.AdId.newBuilder()
                                .setCampaignId(69780)
                                .setPlacementId(170420)
                                .setCreativeId(170420)
                ).addMatchingAdIds(
                        OpenXRealtimeBidding.AdId.newBuilder()
                                .setCampaignId(69780)
                                .setPlacementId(170400)
                                .setCreativeId(170400)
                );
        OpenXRealtimeBidding.BidRequest bidRequest = requestBuilder.build();

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bidRequest.writeTo(bos);
        buf.put(("Content-Length: " + bos.size() + "\r\n\r\n").getBytes());
        buf.put(bos.toByteArray());
        return bidRequest;
    }

    @Override
    public Object read(ByteBuffer buf, Object request) throws Exception {
        for (int i = 0; i < buf.position() - 4; i++)
            if (buf.get(i) == 13 && buf.get(i + 1) == 10 && buf.get(i + 2) == 13 && buf.get(i + 3) == 10) {
                buf.flip();
                buf.position(i + 4);
                byte[] bbuf = new byte[buf.limit() - buf.position()];
                buf.get(bbuf);
                OpenXRealtimeBidding.BidResponse bidResponse = OpenXRealtimeBidding.BidResponse.parseFrom(bbuf);
                return request;
            }
        return null;
    }


    @Override
    public Object createBidRequest() {
        return null;
    }
}
