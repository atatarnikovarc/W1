package com.redaril.dmptf.tests.test.javascript;

import com.redaril.dmp.model.meta.*;
import com.redaril.dmp.model.meta.dto.DataConsumerDTO;
import com.redaril.dmp.model.meta.dto.DataSaleDTO;
import com.redaril.dmptf.tests.support.pip.base.BaseSeleniumTest;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.network.appinterface.jmx.JMXWrapper;
import com.redaril.dmptf.util.network.appinterface.rest.RestWrapper;
import com.redaril.dmptf.util.network.appinterface.webservice.WSHelper;
import com.redaril.dmptf.util.network.lib.httpunit.HttpUnitWrapper;
import com.redaril.dmptf.util.network.protocol.ssh.SSHWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class BaseDfpAsync extends BaseSeleniumTest {

    protected static WSHelper wsHelper;
    protected static RestWrapper restWrapper;
    protected static boolean isBaseSetup;
    protected final static String request = "http://env1.webserver:8000/automation/js/";
    protected static JMXWrapper jmxWrapper;
    protected static JMXWrapper jmxWrapperCST;
    protected final static String SOURCE_SQL = "data" + File.separator + "piggybacks" + File.separator;
    protected final static String pathWebserver = "10.50.150.130";
    protected final static String pathHtmls = "/var/lib/tomcat5/webapps/ROOT/automation/js/";
    protected final static int num = 3;//number of executing tests if it fails
    protected static Logger baseDfpLOG;
    protected static IntegrationPixel integrationPixel;
    private static String urlToCreateUser;
    protected static DataSource dataSource;
    protected static DataConsumer dataConsumer;
    protected static String userWithCats;
    private static Category category;
    private static Category category2;
    protected final static String rasegsHtml = "rasegs.html";
    protected final static String rasegsHtml5 = "rasegs-html5.html";
    protected final static String rasegsXhtml = "rasegs-xhtml.jsp";


    public void setUpDfp() {
        if (!isBaseSetup) {
            wsHelper = new WSHelper(ENV);
            baseDfpLOG = LoggerFactory.getLogger(BaseDfpAsync.class);
            restWrapper = new RestWrapper(ENV);
            ConfigurationLoader configApp = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_APP);
            String baseDomain = configApp.getProperty("baseDomain");
            String port = configApp.getProperty("httpPort");
            urlToCreateUser = "http://" + ENV + "." + configID + ".p." + baseDomain + ":" + port + "/partners/pixel?id=CAESEOS8DIt22VoL5E4ei5mPYhA&cver=1&t=gcm";
            baseDfpLOG.info("Create precondition of tests DfpAsync.");
            PlatformClient platformClient = wsHelper.createPublisher();
            dataConsumer = wsHelper.createDataConsumer(platformClient);
            //
            Set<DataConsumerDTO> setConsumers = new HashSet<DataConsumerDTO>();
            DataConsumerDTO consumerDTO = new DataConsumerDTO();
            consumerDTO.setId(dataConsumer.getId());
            setConsumers.add(consumerDTO);
            //
            Set<AudienceGroup> setAG = new HashSet<AudienceGroup>();
            setAG.add(wsHelper.createAudienceGroup(dataConsumer.getDataOwner()));
            PricingMethod pricingMethod = wsHelper.getPricingMethodByName("CPU");
            SourceType sourceType = wsHelper.getSourceTypeByName("1st party qualifiable");
            dataSource = wsHelper.createDataSource(dataConsumer.getDataOwner(), pricingMethod, sourceType);
            DataType dataType = wsHelper.getDataTypeByName("Unknown");
            category = wsHelper.createCategory(null, dataType, dataSource);
            category2 = wsHelper.createCategory(null, dataType, dataSource);
            long asId = wsHelper.createAudienceSegment(dataConsumer.getId(), "kvp=as1");
            AudienceSegment as = wsHelper.getAudienceSegmentById(asId);
            long asId2 = wsHelper.createAudienceSegment(dataConsumer.getId(), "kvp=as2");
            AudienceSegment as2 = wsHelper.getAudienceSegmentById(asId2);
            long sectionId = wsHelper.createSection(asId, category);
            long sectionId2 = wsHelper.createSection(asId2, category2);
            long advId = wsHelper.createAdvertiser(platformClient);
            long advCampId = wsHelper.createAdvertiserCampaign(platformClient, advId);
            AdvertiserCampaign advCamp = new AdvertiserCampaign();
            advCamp.setId(advCampId);
            long advCampId2 = wsHelper.createAdvertiserCampaign(platformClient, advId);
            AdvertiserCampaign advCamp2 = new AdvertiserCampaign();
            advCamp2.setId(advCampId2);
            ImpressionPixel iPixel = wsHelper.createDMPImpressionPixel(dataConsumer, setAG, advCampId);
            ImpressionPixel iPixel2 = wsHelper.createDMPImpressionPixel(dataConsumer, setAG, advCampId);
            long dataCampId = wsHelper.createDataCampaign(dataConsumer.getId(), as, iPixel, dataConsumer.getDataOwner(), sectionId, setAG);
            long dataCampId2 = wsHelper.createDataCampaign(dataConsumer.getId(), as2, iPixel2, dataConsumer.getDataOwner(), sectionId2, setAG);
            integrationPixel = wsHelper.createAdServerIntegrationPixel(dataConsumer, setAG);
            DataSaleDTO dataSale = restWrapper.createDataSale(dataConsumer.getDataOwner(), setConsumers, dataSource);
            jmxWrapper = new JMXWrapper(ENV, configID, "pip");
            jmxWrapperCST = new JMXWrapper(ENV, configID, "cst");
            reloadpip();
            reloadcst();
            createUserWithCats();
            SSHWrapper sshWrapper = new SSHWrapper(pathWebserver);

            //delete previous version of files, svn up, and change pixel_id at html pages
            sshWrapper.executeCommand("cd /var/lib/tomcat5/webapps/ROOT/automation/js && rm -rf ras* && svn up",1000);
            baseDfpLOG.info("Change pixelId at " + rasegsHtml);
            sshWrapper.getFile(rasegsHtml, rasegsHtml, pathHtmls, true);
            FileHelper.getInstance().findAndReplaceAtFile(rasegsHtml, "{pixel_id}", integrationPixel.getTagId().toString());
            sshWrapper.putFile(rasegsHtml, rasegsHtml, pathHtmls);
            FileHelper.getInstance().deleteFile(rasegsHtml);

            baseDfpLOG.info("Change pixelId at " + rasegsHtml5);
            sshWrapper.getFile(rasegsHtml5, rasegsHtml5, pathHtmls, true);
            FileHelper.getInstance().findAndReplaceAtFile(rasegsHtml5, "{pixel_id}", integrationPixel.getTagId().toString());
            sshWrapper.putFile(rasegsHtml5, rasegsHtml5, pathHtmls);
            FileHelper.getInstance().deleteFile(rasegsHtml5);

            baseDfpLOG.info("Change pixelId at " + rasegsXhtml);
            sshWrapper.getFile(rasegsXhtml, rasegsXhtml, pathHtmls, true);
            FileHelper.getInstance().findAndReplaceAtFile(rasegsXhtml, "{pixel_id}", integrationPixel.getTagId().toString());
            sshWrapper.putFile(rasegsXhtml, rasegsXhtml, pathHtmls);
            FileHelper.getInstance().deleteFile(rasegsXhtml);

            baseDfpLOG.info("Create precondition was successfully executed.");
            isBaseSetup = true;
        }
    }

    protected static void reloadpip() {
        jmxWrapper.execCommand("doReload");
        jmxWrapper.waitForReloading();
    }

    protected static void reloadcst() {
        jmxWrapperCST.execCommand("doReload");
        jmxWrapperCST.waitForReloading();
    }

    protected static List<Object[]> getDriversFromFile(String sourceDrivers) {
        //get data from file into List<String>
        List<Object[]> list = new ArrayList<Object[]>();
        List<String> ipList = FileHelper.getInstance().getDataFromFile(sourceDrivers);
        //end get data
        //parse every line into hashmap, all hashmaps put into array[1] and into List() (structure List<Object[]>)
        String[] params;
        for (String aipList : ipList) {
            HashMap<String, String> data = new HashMap<String, String>();
            params = aipList.split(";");
            //if browser can(or not) be used with proxy add it to list
            for (int j = 0; j < columnNames.size(); j++) {
                data.put(columnNames.get(j), params[j]);
            }
            Object[] array = new Object[1];
            array[0] = data;
            list.add(array);
        }
        return list;
    }

    private void createUserWithCats() {
        HttpUnitWrapper httpUnitWrapper = new HttpUnitWrapper();
        httpUnitWrapper.deleteAllCookies();
        httpUnitWrapper.goToUrl(urlToCreateUser);
        String ucookie = httpUnitWrapper.getCookieValueByName("u");
        baseDfpLOG.info("U Cookie = " + ucookie);
        // httpUnitWrapper.goToUrl("http://" + ENV + "." + configID + ".cst.raasnet.com/cacheservertester/tool/index.html");

        String data = "Uid,ClusterId,Categories\n";
        if (configID.equalsIgnoreCase("west"))
            data = data + ucookie + ",1,\"" + dataConsumer.getDataOwner().getId() + "," + dataSource.getId() + "," + category.getId() + ",;";
        else
            data = data + ucookie + ",2,\"" + dataConsumer.getDataOwner().getId() + "," + dataSource.getId() + "," + category.getId() + ",;";
        String data2;
        if (configID.equalsIgnoreCase("west"))
            data2 = dataConsumer.getDataOwner().getId() + "," + dataSource.getId() + "," + category2.getId() + ",\"";
        else data2 = dataConsumer.getDataOwner().getId() + "," + dataSource.getId() + "," + category2.getId() + ",\"";
        httpUnitWrapper.submitFormWithData("http://" + ENV + "." + configID + ".cst.raasnet.com/cacheservertester/tool/index.html", "upload", "data", data + data2);
        userWithCats = ucookie;
        // wait(3000);
    }
}
