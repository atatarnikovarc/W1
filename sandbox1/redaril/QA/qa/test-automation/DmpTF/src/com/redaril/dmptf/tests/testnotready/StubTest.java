//package com.redaril.dmptf.tests.testnotready;
//
//import com.redaril.dmptf.tests.support.Piggybacks;
//import com.redaril.dmptf.util.network.lib.httpunit.HttpUnitWrapper;
//
//import org.slf4j.Logger;
//import org.junit.Before;
//import org.junit.Test;
//
//public class StubTest extends Piggybacks {
//    protected static HttpUnitWrapper session;
//    @Before
//    public void setUp() {
//        //log into dts.log
//        System.setProperty("DmptfLogFile", "StubTest.log");
//        super.setUp();
//        //LOG = LoggerFactory.getLogger(StubTest.class);
//        session = new HttpUnitWrapper();
//    }
//	@Test
//	public void test() {
//        String url = "http://p.raasnet.com/partners/info?ex=1";
//        session.goToUrl(url);
//        String mycookie = session.getCookieValueByName(ocookie);
//        System.out.println("cookie="+mycookie);
//	}
//}
