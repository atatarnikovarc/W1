package com.redaril.dmptf.tests.test.gsqualifiers.allqualifiers;

import com.redaril.dmptf.tests.support.gsqualifiers.BasicTestGeneralSearchQualifier;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class TestFoxstartCom extends BasicTestGeneralSearchQualifier {
    @Before
    public void setup() {
        logFile = "allQualifiers.log";
        baseSetup();
        getAllGeneralSearchQualifiers();
        domain = getDomainsByEqual("uk.foxstart.com", pathToSql + "getAllDomains.sql").get(0);
    }

    @Test
    public void test() {
        Logger LOG = LoggerFactory.getLogger(TestFoxstartCom.class);
        LOG.info("======Foxstart.com tests started.");
        LOG.info("Domain = " + domain.getRegex());
        checkQualifiers();
        assertTrue("======Foxstart.com tests failed. Count of failed = " + failedQualifiers.size(), failedQualifiers.size() == 0);
        LOG.info("======Foxstart.com tests passed.");
    }
}