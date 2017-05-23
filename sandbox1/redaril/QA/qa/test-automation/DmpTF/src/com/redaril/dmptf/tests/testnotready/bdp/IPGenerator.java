package com.redaril.dmptf.tests.testnotready.bdp;

import com.redaril.dmptf.tests.support.bdp.BaseBDP;
import com.redaril.dmptf.tests.support.etl.EtlLogAnalyzer;
import com.redaril.dmptf.tests.support.etl.log.ETLLog;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.network.protocol.ssh.SSHWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 27.03.13
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
 */
@RunWith(value = Parameterized.class)
public class IPGenerator extends BaseBDP {
    private static Logger LOG;

    public IPGenerator(HashMap<String, String> ipData) {
        this.ipData = ipData;
    }

    private static ETLLog pattern;

    @Parameterized.Parameters
    public static List<Object[]> getParam() {
        System.setProperty(LogSystemProperty, logFileParam);
        LogConfigurer.initLogback();
        LOG = LoggerFactory.getLogger(IPGenerator.class);
        envConfigurationLoader = new ConfigurationLoader(CONFIG_PATH + FILE_PROPERTIES_ENV);
        ENV = envConfigurationLoader.getProperty("env");
        envConfigID = envConfigurationLoader.getProperty("configID");
        if (testClassConfigurationLoader == null) {
            testClassConfigurationLoader = new ConfigurationLoader(CONFIG_PATH + testClassProperties);
            setupTestClassProperties();
        }
        logAnalyzer = new EtlLogAnalyzer();
        ConfigurationLoader currentEnv = new ConfigurationLoader(CONFIG_PATH + ENV + ".properties");
        hostBDP = currentEnv.getProperty("host.bdp");
        sshWrapper = new SSHWrapper(hostBDP, "autotest", "812redaril");
        return getIPFromFile();
    }

    protected static List<Object[]> getIPFromFile() {
        List<Object[]> list = new ArrayList<Object[]>();
        List<String> ipList = new ArrayList<String>();
        ipList.add("200.24.24.2;7;0;0;0;0;Universidad De Antioquia;1");
        //end get data

        //parse every line into hashmap, all hashmaps put into array[1] and into List() (structure List<Object[]>)
        String[] params;
        for (String aipList : ipList) {
            HashMap<String, String> data = new HashMap<String, String>();
            params = aipList.split(";");
            for (int j = 0; j < columnNames.size(); j++) {
                data.put(columnNames.get(j), params[j]);
            }
            Object[] array = new Object[1];
            array[0] = data;
            list.add(array);
        }
        return list;
    }

    @Test
    public void findIPs() {
        String tempFile = "ip.txt";
        long start = System.nanoTime();
        List<String> approvedIP = new ArrayList<String>();
        while ((System.nanoTime() - start) / 1000000000 < 600) {
            cleanBDPCache();
            List<String> ip = new ArrayList<String>();
            Random randomGenerator = new Random();
            String generatedIp;
            generatedIp = randomGenerator.nextInt(255) + ".";
            generatedIp = generatedIp + randomGenerator.nextInt(255) + ".";
            generatedIp = generatedIp + randomGenerator.nextInt(255) + ".";
            generatedIp = generatedIp + randomGenerator.nextInt(255);
            ip.add(generatedIp);
            // LOG.info("Test ip = " + generatedIp);
            // LOG.info("Create file with ip and put it into BDP host");
            FileHelper.getInstance().createFile(tempFile, ip);
            sshWrapper.putFile(tempFile, tempFile, runScriptPath);
            FileHelper.getInstance().deleteFile(tempFile);
            //LOG.info("Execute shell run.sh");
            sshWrapper.executeCommand(shellBdpCommand,null);
            // LOG.info("Wait while generatedIPFile not analyzed by BDP");
            sshWrapper.checkFileExist(generatedIPFile, ipPath, false, true);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                LOG.info("Can't sleep thread");
            }
            Boolean isCheckDB = checkParamDB(20);
            if (isCheckDB) {
                LOG.info(generatedIp + " is found");
                approvedIP.add(generatedIp);
            } else LOG.error(generatedIp + " is not found");
        }
        LOG.info("IP'S:");
        for (String ip : approvedIP) {
            LOG.info(ip);
        }
    }
}
