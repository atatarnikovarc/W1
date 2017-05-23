package com.redaril.dmptf.tests.test.gsqualifiers.alldomains;

import com.redaril.dmptf.tests.support.gsqualifiers.BasicTestGeneralSearchQualifier;
import com.redaril.dmptf.tests.support.gsqualifiers.GeneralSearchDomain;
import com.redaril.dmptf.tests.support.gsqualifiers.GeneralSearchQualifier;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestAllDomains extends BasicTestGeneralSearchQualifier {
    private Logger LOG;

    @Before
    public void setup() {
        logFile = "allDomains.log";
        baseSetup();
        LOG = LoggerFactory.getLogger(TestAllDomains.class);
        getOneGeneralSearchQualifiers();
        getDomainsByScript(pathToSql + "getAllDomains.sql");

    }

    protected void submitForm(String baseUrl, int attempt) {
    }

    @Test
    public void test() {
        List<GeneralSearchDomain> failedDomains = new ArrayList<GeneralSearchDomain>();
        int i = 0;
        String request;
        GeneralSearchQualifier qualifier = qualifiers.get(0);
        LOG.info("Check domains by qualifier with Id = " + qualifier.getId());
        int count = qualifiers.size();
        for (GeneralSearchDomain domain : domains) {
            LOG.debug("-----Check " + i + " qualifier from " + count + ".------");
            request = domain.getRequestUrl();
            boolean isChecked = checkQualifier(qualifier, request.replace("[domain]", domain.getRegex()));
            if (!isChecked) {
                failedDomains.add(domain);
                LOG.info(i + ". Failed Domain ID = " + domain.getId() + ", Regex = " + domain.getRegex() + ", RequestUrl = " + domain.getRequestUrl());
            } else
                LOG.info(i + ". Passed Domain ID = " + domain.getId() + ", Regex = " + domain.getRegex() + ", RequestUrl = " + domain.getRequestUrl());
            i++;
        }
        if (failedDomains.size() != 0) {
            int k = 1;
            LOG.error("======Failed domains");
            for (GeneralSearchDomain domain : failedDomains) {
                LOG.info(k + ". Failed Domain ID = " + domain.getId() + ", Regex = " + domain.getRegex() + ", RequestUrl = " + domain.getRequestUrl());
                k++;
            }
        }
        if (invalidDomains.size() != 0) {
            int k = 1;
            LOG.error("======Invalid domains");
            for (GeneralSearchDomain domain : invalidDomains) {
                LOG.info(k + ". Invalid Domain ID = " + domain.getId() + ", Regex = " + domain.getRegex() + ", RequestUrl = " + domain.getRequestUrl());
                k++;
            }
        }
        assertTrue("======All DOMAINS tests failed. Count of failed = " + failedDomains.size() + ". Count of invalid = " + invalidDomains.size(), (failedDomains.size() == 0) & (invalidDomains.size() == 0));
        LOG.info("======All DOMAINS tests passed.");
    }
}
