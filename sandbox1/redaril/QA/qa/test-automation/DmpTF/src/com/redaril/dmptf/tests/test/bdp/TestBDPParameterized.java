package com.redaril.dmptf.tests.test.bdp;

import com.redaril.dmptf.tests.support.bdp.BaseBDP;
import com.redaril.dmptf.tests.support.etl.EtlLogAnalyzer;
import com.redaril.dmptf.tests.support.etl.log.ETLLog;
import com.redaril.dmptf.tests.support.etl.model.Model;
import com.redaril.dmptf.tests.support.etl.model.Record;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.network.protocol.ssh.SSHWrapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(value = Parameterized.class)
public class TestBDPParameterized extends BaseBDP {
    private static Logger LOG;

    public TestBDPParameterized(HashMap<String, String> ipData) {
        this.ipData = ipData;
    }

    private static ETLLog pattern;

    @Rule
    public TestName name = new TestName();

    @Parameterized.Parameters
    public static List<Object[]> getParam() {
        System.setProperty(LogSystemProperty, logFileParam);
        LogConfigurer.initLogback();
        LOG = LoggerFactory.getLogger(TestBDPParameterized.class);
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
        //create model and pattern to check ETL
        model = new Model();
        pattern = model.getModel(XMLModel, ModelClassName);
        return getIPFromFile();
    }

    //TEST'S FLOW
    //1. очистить базу и перезагрузить cleanBDPCache()
    //3. подсунуть файл -
    //4. запустить скрипт +
    //5. подождать 5-10сек
    //7. сравнить результат

    private boolean checkEtlLog() {
        if (ipData.get("isISP").equalsIgnoreCase("1")) {
            SSHWrapper sshWrapperETL = new SSHWrapper(hostETLBDP, "autotest", "812redaril");
            sshWrapperETL.getFile(etlFileName, localEtlFileName, pathToEtlLogs, false);
            // update pattern with values of test
            Record record = pattern.getRecord();
            //set Values into pattern
            //unfortunately, you should to check name of field at userBusinessDataCall.xml
            //name must be equals name at exchangeMappingCall.xml!
            String ip = ipData.get("ip");
            String business_name = ipData.get("business_name");
            String industry_code = ipData.get("industry_code");
            record.getFieldByName("request_ip").setValue(ip);
            record.getFieldByName("business_name").setValue(business_name);
            record.getFieldByName("business_industry_code").setValue(industry_code);
            pattern.setRecord(record);
            LOG.info("Try to find record with ip = " + ip + " , business_name = " + business_name + " , business_industry_code = " + industry_code);
            //check log
            Boolean isLogChecked = logAnalyzer.checkLog(localEtlFileName, pattern);
            if (isLogChecked) LOG.info("Check ETL is PASSED");
            else LOG.error("Check ETL is FAILED");
            sshWrapper.tearDown(localEtlFileName);
            return isLogChecked;
        } else {
            LOG.info("IP shouldn't be added. So we don't check ETL");
            return true;
        }
    }

    @Test
    public void testIP() {
        LOG.info(name.getMethodName() + " STARTED");
        String tempFile = "ip.txt";
        cleanBDPCache();
        List<String> ip = new ArrayList<String>();
        ip.add(ipData.get("ip"));
        LOG.info("Test ip = " + ipData.get("ip"));
        LOG.info("Create file with ip and put it into BDP host");
        int attempt = 0;
        boolean isChecked = false;
        while (attempt < 3 & !isChecked) {
            FileHelper.getInstance().createFile(tempFile, ip);
            sshWrapper.putFile(tempFile, tempFile, runScriptPath);
            FileHelper.getInstance().deleteFile(tempFile);
            LOG.info("Execute shell run.sh");
            sshWrapper.executeCommand(shellBdpCommand,null);
            LOG.info("Wait while generatedIPFile not analyzed by BDP");
            sshWrapper.checkFileExist(generatedIPFile, ipPath, false, true);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                LOG.info("Can't sleep thread");
            }
            isChecked = checkParamDB(40) & checkEtlLog();
            attempt++;
        }
        assertTrue("Test failed, IP: " + ipData.get("ip"), isChecked);
        LOG.info(name.getMethodName() + " PASSED");
    }
}
