package com.redaril.dmptf.tests.test.snapshot;

import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.network.protocol.ssh.SSHWrapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 28.06.13
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
public class ImportSnapshot {
    protected final static String CONFIG_PATH = "config" + File.separator;
    protected final static String logFile = "import.log";
    protected final static String FILE_PROPERTIES_ENV = "env.properties";
    protected static ConfigurationLoader envConfigurationLoader;
    protected static String ENV;
    protected static String envConfigID;
    protected static String branch;
    protected final static String LogSystemProperty = "DmptfLogFile";
    private static Logger LOG;
    private final static String hostDB = "qadb.qa.phsg.coreaudience.com";
    private static String pathImportLogs;
    private SSHWrapper sshWrapper;

    private void checkErrLogFile(String filename, long size) {
        boolean isFind = false;
        int delay = 0;
        while (!isFind && delay < 8) {
            isFind = sshWrapper.checkFileExist(filename, pathImportLogs, true, false);
            if (isFind == false) delay++;
        }
        sshWrapper.getFile(filename, "err.log", pathImportLogs, true);
        File file = new File("err.log");
        if (size == 0) {//here we check error log
            if (file.length() == size) {
                LOG.info("At " + filename + " NO ERRORS");
                file.delete();
            } else fail("Found errors at " + pathImportLogs + filename);
        } else {//here we check other logs
            if (file.length() < size) {
                LOG.info("At " + filename + " NO ERRORS");
                file.delete();
            } else {
                fail("Found a lot of records at " + pathImportLogs + filename);
            }
        }
    }

    private void executeImport(String schema){
        sshWrapper.executeCommand("cd /opt/QA/DbUpdate && upgrade_" + ENV + "_"+schema+".sh", 3000);
        checkErrLogFile(ENV + "_"+schema+"_import_err.log", 0);
        checkErrLogFile(ENV + "_"+schema+"_import_wrn.log", 1000);
    }

    private void executeUpgradeScripts(){
        sshWrapper.executeCommand("cd /opt/QA/DbUpdate/dmp/ && " + "svn switch --username buildmaster --password adm@st3r https://source.icrossing.com/svn/ra-csc/trunk/Auxiliary/Schema/upgrade/dmpdb/SR_" + branch,2000);
        sshWrapper.executeCommand("cd /opt/QA/DbUpdate/dmp/ && " + "svn up --username buildmaster --password adm@st3r", 2000);
        sshWrapper.executeCommand("cd /opt/QA/DbUpdate/dmp/ && " + "sqlplus "+ENV+"_dmp/"+ENV+"_dmp@qadb.qa.phsg.coreaudience.com/qacluster @SR_"+branch+"_Upgrade.sql > sql.log", 15000);

    }

    @Test
    public void testImport() {
        System.setProperty(LogSystemProperty, logFile);
        LogConfigurer.initLogback();
        LOG = LoggerFactory.getLogger(ImportSnapshot.class);
        envConfigurationLoader = new ConfigurationLoader(CONFIG_PATH + FILE_PROPERTIES_ENV);
        ENV = envConfigurationLoader.getProperty("env");
        branch = envConfigurationLoader.getProperty("branch");
        pathImportLogs = "/opt/QA/DbUpdate/" + ENV + "_recovery_log/";
        sshWrapper = new SSHWrapper(hostDB, "autotest", "812redaril");
        sshWrapper.executeCommand("cd /opt/QA/DbUpdate && rm -f "+ pathImportLogs+"/*",null);
        executeImport("dmp");
        executeImport("meta");
        executeImport("flex");
        executeUpgradeScripts();
    }
}
