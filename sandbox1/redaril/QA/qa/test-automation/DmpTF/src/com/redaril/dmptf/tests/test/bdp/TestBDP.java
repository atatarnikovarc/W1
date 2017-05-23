package com.redaril.dmptf.tests.test.bdp;

import com.redaril.dmptf.tests.support.bdp.BaseBDP;
import com.redaril.dmptf.tests.support.etl.model.Model;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.date.DateWrapper;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.network.protocol.ssh.SSHWrapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestBDP extends BaseBDP {
    private static Logger LOG;
    private static Boolean isInitLog = false;
    private String tempFile = "ip.txt";
    ;

    @Rule
    public TestName name = new TestName();

    @Before
    public void setUp() {
        initLog();
        super.setUp();
        if (model == null) model = new Model();
        sshWrapper = new SSHWrapper(hostBDP, "autotest", "812redaril");
    }

    private void initLog() {
        if (!isInitLog) {
            System.setProperty(LogSystemProperty, logFileBase);
            LogConfigurer.initLogback();
            LOG = LoggerFactory.getLogger(TestBDP.class);
            isInitLog = true;
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOG.info("Can't sleep the thread.");
        }
    }

    @Test
    public void testAllIPs() {
        LOG.info(name.getMethodName() + " STARTED");
        List<Object[]> list = getIPFromFile();
        cleanBDPCache();
        List<String> ip = new ArrayList<String>();
        Integer countPositive = 0;
        for (Object[] aList : list) {
            String ipstring = ((HashMap) aList[0]).get("ip").toString();
            ip.add(ipstring);
            if (((HashMap) aList[0]).get("isISP").toString().equalsIgnoreCase("1")) countPositive++;
            LOG.info("Test ip = " + ipstring);
        }
        LOG.info("Create file with IPs and put it into BDP host");
        FileHelper.getInstance().createFile(tempFile, ip);
        sshWrapper.putFile(tempFile, tempFile, runScriptPath);
        FileHelper.getInstance().deleteFile(tempFile);
        LOG.info("Execute shell run.sh");
        sshWrapper.executeCommand(shellBdpCommand,null);
        LOG.info("Wait while generatedIPFile not analyzed by BDP");
        sshWrapper.checkFileExist(generatedIPFile, ipPath, false, true);
        assertTrue("Test failed", checkCountDB(countPositive, 60));
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testUpdateBefore20Days() {
        LOG.info(name.getMethodName() + " STARTED");
        List<Object[]> list = getIPFromFile();
        cleanBDPCache();
        List<String> ip = new ArrayList<String>();
        Boolean isFind = false;
        Integer i = 0;
        while (!isFind && i < list.size()) {
            if (((HashMap) list.get(i)[0]).get("isISP").toString().equalsIgnoreCase("1")) {
                ipData = (HashMap<String, String>) list.get(i)[0];
                ip.add(ipData.get("ip"));
                LOG.info("Test ip = " + ipData.get("ip"));
                isFind = true;
            }
            i++;
        }
        LOG.info("Create file with IP and put it into BDP host");
        FileHelper.getInstance().createFile(tempFile, ip);
        sshWrapper.putFile(tempFile, tempFile, runScriptPath);
        FileHelper.getInstance().deleteFile(tempFile);
        LOG.info("Execute shell run.sh");
        sshWrapper.executeCommand(shellBdpCommand,null);
        LOG.info("Wait while generatedIPFile not analyzed by BDP");
        sshWrapper.checkFileExist(generatedIPFile, ipPath, false, true);
        Boolean isCheckDB = checkCountDB(1, 60);
        if (isCheckDB) LOG.info("Firstly added ip is correct");
        List<String> listUpdate = new ArrayList<String>();
        listUpdate.add("1");
        ipData.put("industry_code", "1");
        listUpdate.add("2");
        ipData.put("business_size", "2");
        listUpdate.add("'" + DateWrapper.getPreviousDateDDMMYY(-5) + "'");
        listUpdate.add("3");
        ipData.put("value_min", "3");
        listUpdate.add("4");
        ipData.put("value_max", "4");
        listUpdate.add("5");
        ipData.put("zip", "5");
        listUpdate.add("6");
        ipData.put("city", "6");
        listUpdate.add("'" + ipData.get("business_name") + "'");
        listUpdate.add("'" + ipData.get("business_name") + "'");
        String scriptUpdate = FileHelper.getInstance().getDataWithParams(updateScript, listUpdate);
        oreacleWrapper.executeUpdate(scriptUpdate);
        jmxWrapper.execCommand("reloadData");
        checkCountDB(1, 60);
        LOG.info("Execute shell run.sh");
        sshWrapper.executeCommand(shellBdpCommand,null);
        //DB update is too slow
        sleep(20000);
        isCheckDB = checkParamDB(60);
        assertTrue("Test failed", isCheckDB);
        LOG.info(name.getMethodName() + " PASSED");
    }

    @Test
    public void testUpdateAfter20Days() {
        LOG.info(name.getMethodName() + " STARTED");
        List<Object[]> list = getIPFromFile();
        cleanBDPCache();
        List<String> ip = new ArrayList<String>();
        Boolean isFind = false;
        Integer i = 0;
        while (!isFind && i < list.size()) {
            if (((HashMap) list.get(i)[0]).get("isISP").toString().equalsIgnoreCase("1")) {
                ipData = (HashMap<String, String>) list.get(i)[0];
                ip.add(ipData.get("ip"));
                LOG.info("Test ip = " + ipData.get("ip"));
                isFind = true;
            }
            i++;
        }
        LOG.info("Create file with IP and put it into BDP host");
        FileHelper.getInstance().createFile(tempFile, ip);
        sshWrapper.putFile(tempFile, tempFile, runScriptPath);
        FileHelper.getInstance().deleteFile(tempFile);
        LOG.info("Execute shell run.sh");
        sshWrapper.executeCommand(shellBdpCommand,null);
        LOG.info("Wait while generatedIPFile not analyzed by BDP");
        sshWrapper.checkFileExist(generatedIPFile, ipPath, false, true);
        Boolean isCheckDB = checkCountDB(1, 60);
        if (isCheckDB) LOG.info("Firstly added ip is correct");
        List<String> listUpdate = new ArrayList<String>();
        listUpdate.add("1");
        listUpdate.add("2");
        listUpdate.add("'" + DateWrapper.getPreviousDateDDMMYY(-35) + "'");
        listUpdate.add("3");
        listUpdate.add("4");
        listUpdate.add("5");
        listUpdate.add("6");
        listUpdate.add("'" + ipData.get("business_name") + "'");
        listUpdate.add("'" + ipData.get("business_name") + "'");
        String scriptUpdate = FileHelper.getInstance().getDataWithParams(updateScript, listUpdate);
        oreacleWrapper.executeUpdate(scriptUpdate);
        jmxWrapper.execCommand("reloadData");
        LOG.info("Execute shell run.sh");
        sshWrapper.executeCommand(shellBdpCommand,null);
        //DB update is too slow
        sleep(20000);
        isCheckDB = checkParamDB(180);
        assertTrue(name.getMethodName() + " FAILED", isCheckDB);
        LOG.info(name.getMethodName() + " PASSED");
    }
}
