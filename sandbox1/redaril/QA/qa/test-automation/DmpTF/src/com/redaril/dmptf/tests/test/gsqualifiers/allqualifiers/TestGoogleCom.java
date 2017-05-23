package com.redaril.dmptf.tests.test.gsqualifiers.allqualifiers;

import com.redaril.dmptf.tests.support.gsqualifiers.BasicTestGeneralSearchQualifier;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class TestGoogleCom extends BasicTestGeneralSearchQualifier {
    @Before
    public void setup() {
        logFile = "allQualifiers.log";
        baseSetup();
        getAllGeneralSearchQualifiers();
        domain = getDomainsByEqual("google.com", pathToSql + "getAllDomains.sql").get(0);
    }

    @Test
    public void test() {
        Logger LOG = LoggerFactory.getLogger(TestGoogleCom.class);
        LOG.info("======Google.com tests started.");
        LOG.info("Domain = " + domain.getRegex());
        checkQualifiers();
        assertTrue("======Google.com tests failed. Count of failed = " + failedQualifiers.size(), failedQualifiers.size() == 0);
        LOG.info("======Google.com tests passed.");
    }
}