package com.redaril.dmptf.tests.test.rtb;

import com.redaril.dmptf.tests.support.etl.EtlLogAnalyzer;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.database.oracle.OracleWrapper;
import com.redaril.dmptf.util.date.DateWrapper;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.network.appinterface.jmx.JMXWrapper;
import com.redaril.dmptf.util.network.protocol.ssh.SSHWrapper;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class TestRTB {
    protected static boolean isSetup;
    protected static String ENV;
    protected static String envConfigId;
    protected final static String PATH_CONFIG = "config" + File.separator;
    protected final static String LogSystemProperty = "DmptfLogFile";
    private static Logger LOG;
    protected static JMXWrapper jmxRTBWrapper;
    protected static JMXWrapper jmxPacingManagerWrapper;
    protected static OracleWrapper cscOracleWrapper;
    protected static OracleWrapper dmpOracleWrapper;
    protected final static String FILE_PROPERTIES = "rtb.properties";
    protected static SSHWrapper sshWrapper;
    protected static EtlLogAnalyzer logAnalyzer;
    protected final static String FILE_PROPERTIES_ENV = "env.properties";
    protected static ConfigurationLoader configEnv;
    protected static ConfigurationLoader config;
    private final static String scriptname = "data" + File.separator + "rtb" + File.separator + "insert.sql";
    private final static String selectCount = "data" + File.separator + "rtb" + File.separator + "selectCount.sql";
    private final static String delete = "data" + File.separator + "rtb" + File.separator + "delete.sql";
    private final static String campId = "103575";
    private final static String adGroupId = "1111111111";
    private final static String adId = "101565";
    private final static String site = "http://some.gcn.site.com";
    private final static String pathToScript = File.separator + "home" + File.separator + "csc" + File.separator +
            "QA" + File.separator + "RTB" + File.separator + "google" + File.separator + "2.4.1" + File.separator +
            "requesters" + File.separator + "automation" + File.separator;
    private final static String logFile = "rtb.log";

    @Before
    public void setUp() {
        if (!isSetup) {
            FileHelper.getInstance().deleteFile("output" + File.separator + "logs" + File.separator + logFile);
            System.setProperty("DmptfLogFile", logFile);
            configEnv = new ConfigurationLoader(PATH_CONFIG
                    + FILE_PROPERTIES_ENV);
            config = new ConfigurationLoader(PATH_CONFIG
                    + FILE_PROPERTIES);
            ENV = configEnv.getProperty("env");
            envConfigId = configEnv.getProperty("configID");
            LogConfigurer.initLogback();
            LOG = LoggerFactory.getLogger(TestRTB.class);
            logAnalyzer = new EtlLogAnalyzer();
            //jmxRTBWrapper = new JMXWrapper(ENV, "rtb");
            jmxPacingManagerWrapper = new JMXWrapper(ENV, envConfigId, "pacingmanager");
            cscOracleWrapper = new OracleWrapper(ENV, "csc");
            dmpOracleWrapper = new OracleWrapper(ENV, "dmp");
            sshWrapper = new SSHWrapper(config.getProperty("requester.host"), "root", "redbull812");
            isSetup = true;
        }
    }

    @Test
    public void test() {
        cleanUpRequesterLogs();
        cleanUpPmAllocation();
        applyPmAllocation();
        reloadPacingManager();
        updateGeneratorPy(adGroupId);
        updateSendRequestSh();
        LOG.info("execute shellBdpCommand");
        sshWrapper.executeCommand("cd " + pathToScript + " && ./sendRequest.sh",null);
        checkResult();
    }

    private void cleanUpPmAllocation() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(campId);
        list.add(DateWrapper.getPreviousDATEYYMMDD(0));
        String script = FileHelper.getInstance().getDataWithParams(selectCount, list);
        ResultSet rowSet = cscOracleWrapper.executeSelect(script);
        try {
            if (rowSet.next()) {
                String count = rowSet.getString("COUNT");
                if (!count.equalsIgnoreCase("48")) {
                    script = FileHelper.getInstance().getDataWithParams(delete, list);
                    cscOracleWrapper.executeUpdate(script);
                }
            }
        } catch (SQLException e) {
            LOG.error("Can't get data from PM_ALLOCATION. Exception = " + e.getLocalizedMessage());
            fail("Can't get data from PM_ALLOCATION.");
        }
    }

    private void applyPmAllocation() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(campId);
        list.add(DateWrapper.getPreviousDATEYYMMDD(0));
        String script = FileHelper.getInstance().getDataWithParams(scriptname, list);
        for (int k = 1; k < 3; k++) { //to generate 1,2 cluster at script
            String scriptWithCluster = script.replace("{2}", String.valueOf(k));//string with filled cluster

            for (int i = 0; i < 24; i++) {  //to generate 24 hours at script
                String value = String.valueOf(i);
                if (Math.floor(i / 10) < 1) {
                    value = "0" + value;
                }
                String final_script = scriptWithCluster.replace("{3}", value); //string with filled cluster and hours
                cscOracleWrapper.executeUpdate(final_script);
            }
        }
        LOG.info("PM_Allocation was updated successfully.");
    }

    private void cleanUpRequesterLogs() {
        String rmOldFiles = "cd /home/csc/QA/RTB/google/2.4.1/requesters/automation/ && rm -f *.log *.html";
        sshWrapper.executeCommand(rmOldFiles,null);
        LOG.info("Command was executed. Command = " + rmOldFiles);
    }

    private void checkResult() {
        boolean good = checkGood(adGroupId, adId, site);
        boolean snippet = checkSnippet();
        LOG.info("good, snippet : " + good + " " + snippet);
    }

    private boolean checkGood(String adGroupId, String ad, String site) {
        sshWrapper.getFile("good", "good.log", "/home/csc/QA/RTB/google/2.4.1/requesters/automation/", false);
        List<String> data = FileHelper.getInstance().getDataFromFileAfterCriteria("good.log", "BidResponse:");
        FileHelper.getInstance().deleteFile("good.log");
        return data.contains("  buyer_creative_id: \"" + ad + "\"") && data.contains("  click_through_url: \"" + site + "\"") &&
                data.contains("    adgroup_id: " + adGroupId);
    }

    private boolean checkSnippet() {
        sshWrapper.getFile("snippet", "snippet.html", pathToScript, false);
        Long size = FileHelper.getInstance().getFileSize("snippet.html");
        FileHelper.getInstance().deleteFile("snippet.html");
        return size > 200;
    }

    private void updateSendRequestSh() {
        String ip = config.getProperty(ENV + ".rtb.host");
        String toReplace = "perl requester.py --url=http://" + ip + ":8080/api/google --max_qps=1 --requests=1";
        sshWrapper.getFile("sendRequest.sh", "sendRequest.sh", pathToScript, true);
        FileHelper.getInstance().findAndReplaceStringAtFile("sendRequest.sh", "perl requester.py",
                toReplace);
        sshWrapper.putFile("sendRequest.sh", "sendRequest.sh", pathToScript);
        //delete local file
        FileHelper.getInstance().deleteFile("sendRequest.sh");
        LOG.info("updateSendRequestSh executed");
    }

    private void updateGeneratorPy(String adGroupId) {
        sshWrapper.getFile("generator.py", "generator.py", pathToScript, true);
        FileHelper.getInstance().findAndReplaceStringAtFile("generator.py", "adgroup_array =",
                "    adgroup_array = [" + adGroupId + "]");
        sshWrapper.putFile("generator.py", "generator.py", pathToScript);
        //delete local file
        FileHelper.getInstance().deleteFile("generator.py");
        LOG.info("updateGeneratorPy executed");
    }

    private void reloadPacingManager() {
        if (jmxPacingManagerWrapper.execCommand("doReload") != null) {
            jmxPacingManagerWrapper.waitForReloading();
            LOG.info("PacingManager reloaded successfully.");
        } else {
            LOG.error("Can't reload Pacing Manager.");
            fail("Can't reload Pacing Manager.");
        }
    }

}
