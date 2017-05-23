package com.redaril.dmptf.tests.testnotready.load.monitoring.RTB.google;

import com.google.protobuf.ByteString;
import com.redaril.dmptf.tests.testnotready.load.monitoring.RTB.RTBProtocol;
import com.redaril.dmptf.tests.testnotready.load.monitoring.RTB.protobuf.GoogleRealtimeBidding;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class GoogleRtbProtocol extends RTBProtocol {

    public static final long AD_GROUP_ID = 1499395748L;
    private String ip;

    public GoogleRtbProtocol(String ip) {
        this.ip = ip;
    }

    @Override
    public Object write(ByteBuffer buf, int timeout, Object... payload) throws Exception {
        buf.put("POST /api/google HTTP/1.1\r\n".getBytes());
        buf.put(("Host: " + ip + "\r\n").getBytes());
        buf.put("Content-Type: application/octet-stream\r\n".getBytes());
        buf.put("Accept-Encoding: gzip\r\n".getBytes());

        //GoogleRealtimeBidding.BidRequest bidRequest = GoogleRealtimeBidding.BidRequest.parseFrom(Hex.decodeHex("1210507bf763000a02fc0a342bca700007972203cf0d7e329d014d6f7a696c6c612f342e302028636f6d70617469626c653b204d53494520382e303b2057696e646f7773204e5420352e313b2054726964656e742f342e303b20475442372e343b20496e666f506174682e333b202e4e455420434c5220322e302e35303732373b202e4e455420434c5220332e352e33303732393b202e4e455420434c5220332e302e343530362e32313532292c677a697028676665293a025553420555532d4e4a4a064e6577746f6e50f5035a26687474703a2f2f7777772e7370616e697368646963742e636f6d2f7472616e736c6174696f6e6202656e6a0808f1091567ddd13e6a0808b405156ef6b43e6a07086c155758723e728801080110ac0218fa01220c07202728292a160e1012131432450a1c3341475c5e60718001810182018f019001910192019301940195019801b301c201c601e101e201e301e401e501b102e701e801e901ea01eb01ec01ed01ee01ab02af023a0713040a1f0818124a0b10f4fd9cef0d28e0a1910160006a02fc5b70afd386bc0679cf37693c56e3b3197800b20115437572696f73697479204d656469612c20496e632eba01149b47f937aff5ef648526ccc217a557e5d37e344ec00102c80190feffffffffffffff01b802dfb23ec00200c802d702d102322e9c3f4fd40818".toCharArray()));

        GoogleRealtimeBidding.BidRequest.Builder requestBuilder = GoogleRealtimeBidding.BidRequest.newBuilder()
                .setId(ByteString.copyFrom("ToGbHQANbD0K7F5CsDV3-Q==", "UTF-8"))
                .setGoogleUserId("CAESEDJFeh2ZQvGe8IcWcTP915w")
                .setDEPRECATEDCountry("US")
                .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.202 Safari/535.1")
//            .setIp( ByteString.copyFrom( new byte[]{ 65, (byte) 242,16,26 } ) )
                .setIp(ByteString.copyFrom(new byte[]{70, (byte) 181, 105, 1}))
                .setUrl("candystand.com")
                .setDEPRECATEDCity("sankt peter")
                .addAdslot(
                        GoogleRealtimeBidding.BidRequest.AdSlot.newBuilder()
                                .setId(996)
                                .addWidth(160)
                                .addHeight(600)
                                .setSlotVisibility(GoogleRealtimeBidding.BidRequest.AdSlot.SlotVisibility.ABOVE_THE_FOLD)
                                .addMatchingAdData(
                                        GoogleRealtimeBidding.BidRequest.AdSlot.MatchingAdData.newBuilder()
                                                .setAdgroupId(AD_GROUP_ID)
                                )
                        //.addExcludedAttribute(48)
                ).addAdslot(
                        GoogleRealtimeBidding.BidRequest.AdSlot.newBuilder()
                                .setId(997)
                                .addWidth(160)
                                .addHeight(600)
                                .addWidth(300)
                                .addHeight(250)
                                .setSlotVisibility(GoogleRealtimeBidding.BidRequest.AdSlot.SlotVisibility.ABOVE_THE_FOLD)
                                .addMatchingAdData(
                                        GoogleRealtimeBidding.BidRequest.AdSlot.MatchingAdData.newBuilder()
                                                .setAdgroupId(AD_GROUP_ID)
                                )
                ).addAdslot(
                        GoogleRealtimeBidding.BidRequest.AdSlot.newBuilder()
                                .setId(998)
                                .addWidth(300)
                                .addHeight(250)
                                .setSlotVisibility(GoogleRealtimeBidding.BidRequest.AdSlot.SlotVisibility.ABOVE_THE_FOLD)
                                .addMatchingAdData(
                                        GoogleRealtimeBidding.BidRequest.AdSlot.MatchingAdData.newBuilder()
                                                .setAdgroupId(AD_GROUP_ID)
                                )
                ).addAdslot(
                        GoogleRealtimeBidding.BidRequest.AdSlot.newBuilder()
                                .setId(999)
                                .addWidth(728)
                                .addHeight(90)
                                .setSlotVisibility(GoogleRealtimeBidding.BidRequest.AdSlot.SlotVisibility.ABOVE_THE_FOLD)
                                .addMatchingAdData(
                                        GoogleRealtimeBidding.BidRequest.AdSlot.MatchingAdData.newBuilder()
                                                .setAdgroupId(AD_GROUP_ID)
                                )
                );
        GoogleRealtimeBidding.BidRequest bidRequest = requestBuilder.build();

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
                GoogleRealtimeBidding.BidResponse bidResponse = GoogleRealtimeBidding.BidResponse.parseFrom(bbuf);
                return bidResponse;
            }
        return null;
    }


    @Override
    public Object createBidRequest() {
        return null;
    }
}
