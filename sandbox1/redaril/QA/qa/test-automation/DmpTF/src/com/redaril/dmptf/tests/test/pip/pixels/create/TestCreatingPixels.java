package com.redaril.dmptf.tests.test.pip.pixels.create;

import com.redaril.dmp.model.meta.*;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.network.appinterface.webservice.WSHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 20.06.13
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public class TestCreatingPixels {

    protected static Logger LOG;
    private final static String PATH_CONFIG = "config" + File.separator;
    private final static String LogSystemProperty = "DmptfLogFile";
    private static Boolean isInit = false;
    private final static String FILE_PROPERTIES_ENV = "env.properties";
    private static ConfigurationLoader configEnv;
    private static String ENV;
    private static String configID;
    private static WSHelper wsHelper;

    //for pixels
    private static PlatformClient platformClient;
    private static DataConsumer dataConsumer;
    private static long advId;
    private static long adCampId;
    private static AdvImpressionPixel advImpressionPixel;
    private static ImpressionPixel impressionPixel;
    private static Set<AudienceGroup> setAG;
    private final static String productKey = "prod";
    private final static String productCategoryKey = "prodcat";

    protected final static String FILE_PROPERTIES_APP = "app.properties";

    @Rule
    public TestName name = new TestName();

    @Before
    public void setUp() {
        if (!isInit) {
            configEnv = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
            String logFile = "advPixel.log";
            System.setProperty(LogSystemProperty, logFile);
            LogConfigurer.initLogback();
            ENV = configEnv.getProperty("env");
            configID = configEnv.getProperty("configID");
            wsHelper = new WSHelper(ENV);
            LOG = LoggerFactory.getLogger(TestCreatingPixels.class);
            //setup for advertiser' pixels
            platformClient = wsHelper.createAdvertiser();
            advId = wsHelper.createAdvertiser(platformClient);
            dataConsumer = wsHelper.createDataConsumer(platformClient);
            setAG = new HashSet<>();
            setAG.add(wsHelper.createAudienceGroup(dataConsumer.getDataOwner()));
            adCampId = wsHelper.createAdvertiserCampaign(platformClient, advId);
            impressionPixel = wsHelper.createDMPImpressionPixel(dataConsumer, setAG,adCampId);
            isInit = true;
        }
    }

    //ADV

    @Test
    public void createConversionPixel() {
        LOG.info(name.getMethodName() + " STARTED");
        Set<AdvImpressionPixel> impressionPixelSet = new HashSet<AdvImpressionPixel>();
        impressionPixelSet.add(advImpressionPixel);
        AdvConversionPixel pixel = wsHelper.createConversionPixel(dataConsumer.getDataOwner(), setAG);
        LOG.info("ConversionPixel ID = " + pixel.getTagId());
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void createImpressionPixel() {
        LOG.info(name.getMethodName() + " STARTED");
        advImpressionPixel = wsHelper.createImpressionPixel(dataConsumer.getDataOwner(), setAG);
        LOG.info("ImpressionPixel ID = " + advImpressionPixel.getTagId());
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void createClickPixel() {
        LOG.info(name.getMethodName() + " STARTED");
        AdvClickPixel pixel = wsHelper.createClickPixel(dataConsumer.getDataOwner(), setAG,MediaPartner.CORE_AUDIENCE);
        LOG.info("advClickPixel ID = " + pixel.getTagId());
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void createTrackingPixel() {
        LOG.info(name.getMethodName() + " STARTED");
        TrackingPixel pixel = wsHelper.createTrackingPixel(dataConsumer.getDataOwner(), setAG, productKey, productCategoryKey);
        LOG.info("trackingPixel ID = " + pixel.getTagId());
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void createAdServerIntegrationTag() {
        LOG.info(name.getMethodName() + " STARTED");
        IntegrationPixel pixel = wsHelper.createAdServerIntegrationPixel(dataConsumer, setAG);
        LOG.info("AdServerIntegrationTag ID = " + pixel.getTagId());
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void createDataPixel() {
        LOG.info(name.getMethodName() + " STARTED");
        DataPixel pixel = wsHelper.createDataPixel(dataConsumer.getDataOwner(), setAG);
        LOG.info("DataPixel ID = " + pixel.getTagId());
        LOG.info(name.getMethodName() + " PASSED");
    }

    //DMP

    @Test
    public void createDMPImpressionPixel() {
        LOG.info(name.getMethodName() + " STARTED");
        impressionPixel = wsHelper.createDMPImpressionPixel(dataConsumer, setAG,adCampId);
        LOG.info("DMPImpressionPixel ID = " + impressionPixel.getTagId());
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void createDMPConversionPixel() {
        LOG.info(name.getMethodName() + " STARTED");
        Set<ImpressionPixel> impressionPixelSet = new HashSet<>();
        impressionPixelSet.add(impressionPixel);
        ConversionPixel pixel = wsHelper.createDMPConversionPixel(dataConsumer, setAG,impressionPixelSet);
        LOG.info("DMPConversionPixel ID = " + pixel.getTagId());
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void createDMPClickPixel() {
        LOG.info(name.getMethodName() + " STARTED");
        Set<ImpressionPixel> impressionPixelSet = new HashSet<>();
        impressionPixelSet.add(impressionPixel);
        ClickPixel pixel = wsHelper.createDMPClickPixel(dataConsumer, setAG,impressionPixelSet);
        LOG.info("DMPClickPixel ID = " + pixel.getTagId());
        LOG.info(name.getMethodName() + " PASSED");
    }

}