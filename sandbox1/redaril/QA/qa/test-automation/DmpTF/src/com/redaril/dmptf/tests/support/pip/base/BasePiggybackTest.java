package com.redaril.dmptf.tests.support.pip.base;

import com.redaril.dmp.model.meta.DataConsumer;
import com.redaril.dmp.model.meta.PlatformClient;
import com.redaril.dmptf.tests.support.etl.EtlLogAnalyzer;
import com.redaril.dmptf.tests.support.etl.log.DmpUserModel;
import com.redaril.dmptf.tests.support.etl.log.ETLLog;
import com.redaril.dmptf.tests.support.etl.log.asRequestDataPartnerProd;
import com.redaril.dmptf.tests.support.etl.model.Model;
import com.redaril.dmptf.tests.support.etl.model.Record;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.database.oracle.OracleWrapper;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.network.appinterface.jmx.JMXWrapper;
import com.redaril.dmptf.util.network.appinterface.webservice.WSHelper;
import com.redaril.dmptf.util.network.protocol.ssh.SSHWrapper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class BasePiggybackTest extends BaseSeleniumTest {

    protected final static String sourceDrivers = SOURCE_PATH + "driversProxy.txt";
    protected static WSHelper wsHelper;
    protected static boolean isBaseSetup;
    protected final static String urlRedirect = "example.com/?";
    protected final static String urlPixel = "http://qa-10.qa.coreaudience.com:8080/automation/Env-1/pip/pb.html";
    protected final static String urlPixelHTTPS = "http://qa-10.qa.coreaudience.com:8080/automation/Env-1/pip/pbs.html";
    protected static JMXWrapper jmxWrapper;
    protected static JMXWrapper jmxWrapperCST;
    protected static OracleWrapper oracleWrapper;
    protected final static String SOURCE_SQL = "data" + File.separator + "piggybacks" + File.separator;
    protected final static String cleanSysPbDB = SOURCE_SQL + "updateSystemPbWeight.sql";
    protected final static String cleanClientPbDB = SOURCE_SQL + "updateClientPbWeight.sql";
    protected final static String pathWebserver = "qa-10.qa.coreaudience.com";
    protected final static String pathPbHtml = "/var/lib/tomcat6/webapps/ROOT/automation/Env-1/pip/";
    protected final static String filePbHtml = "pb.html";
    protected static HashMap<String, String> oneDriverInfo;
    protected static PlatformClient platformClient;
    protected static DataConsumer dataConsumer;
    //for checking etl
    protected final static String requestDataPartnerProdEtlFile = "asRequestDataPartnerProd";  //name of asRequestDataPartnerProd file
    protected final static String dmpEtlFile = "dmpUserModel";  //name of DmpUserModel file
    protected final static String dmpEtllocal = dmpEtlFile + ".log";  //name of local ETL file
    protected final static String requestDataPartnerProdEtlFileLocal = requestDataPartnerProdEtlFile + ".log";  //name of local ETL file
    protected final static String etlpath = "/var/log/etl/";
    private static final Class ModelClassName = asRequestDataPartnerProd.class;
    private static final Class DMPModelClassName = DmpUserModel.class;
    protected static Model model;
    private static final String XMLModel = "data" + File.separator + "etl" + File.separator + "asRequestDataPartnerProd.xml";
    private static final String XMLDMPModel = "data" + File.separator + "etl" + File.separator + "dmpUserModel.xml";
    protected static EtlLogAnalyzer fileAnalyzer;
    protected static SSHWrapper sshWrapperETL;
    protected final static int num = 3;//number of executing tests if it fails
    private static Logger basePbLOG;
    protected static int port;

    private void cleanExistedPb() {
        if (oracleWrapper == null) oracleWrapper = new OracleWrapper(ENV, "dmp");
        String script = FileHelper.getInstance().getDataWithoutParams(cleanSysPbDB);
        basePbLOG.info("Clean System piggybacks. Script = " + script);
        oracleWrapper.executeUpdate(script);
        script = FileHelper.getInstance().getDataWithoutParams(cleanClientPbDB);
        basePbLOG.info("Clean Client piggybacks. Script = " + script);
        oracleWrapper.executeUpdate(script);
    }

    public void setUpPiggybacks() {
        if (!isBaseSetup) {
            wsHelper = new WSHelper(ENV);
            basePbLOG = LoggerFactory.getLogger(BasePiggybackTest.class);
            ConfigurationLoader configApp = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_APP);
            port = Integer.valueOf(configApp.getProperty("httpPort"));
            cleanExistedPb();
            //ETL
            ConfigurationLoader config = new ConfigurationLoader(PATH_CONFIG + ENV + ".properties");
            String ipETL = config.getProperty("host.etl");
            fileAnalyzer = new EtlLogAnalyzer();
            model = new Model();
            sshWrapperETL = new SSHWrapper(ipETL, "autotest", "812redaril");
            isBaseSetup = true;
            platformClient = wsHelper.createPublisher();
            dataConsumer = wsHelper.createDataConsumer(platformClient);
        }
    }

    protected boolean checkDmpUserModelETL(String uid, String pixel_id, @Nullable String extraParamsJson) {

        // get pattern
        ETLLog pattern = model.getModel(XMLDMPModel, DMPModelClassName);
        Record record = pattern.getRecord();
        //set Values into pattern
        record.getFieldByName("created_date_time").setValue(null);
        record.getFieldByName("cookie_user_id").setValue(uid);
        record.getFieldByName("pixel_id").setValue(pixel_id);
        record.getFieldByName("extra_params_json").setValue(extraParamsJson);
        pattern.setRecord(record);
        basePbLOG.info("Try to find record with cookie_user_id = " + uid + " , pixel_id = " + pixel_id + " , extra_params_json = " + extraParamsJson);
        //check log
        boolean isCheck = fileAnalyzer.checkLog(dmpEtllocal, pattern);
        sshWrapperETL.tearDown(dmpEtllocal);
        return isCheck;
    }

    protected boolean checkAsRequestDataPartnerProd(String uid, String pixel_id, String pbId) {
        // get pattern
        ETLLog pattern = model.getModel(XMLModel, ModelClassName);
        Record record = pattern.getRecord();
        //set Values into pattern
        record.getFieldByName("created_date_time").setValue(null);
        record.getFieldByName("cookie_user_id").setValue(uid);
        record.getFieldByName("ok").setValue("Y");
        record.getFieldByName("partner_id").setValue(pixel_id);
        record.getFieldByName("piggybacks").setValue(pbId);
        pattern.setRecord(record);
        basePbLOG.info("Try to find record with cookie_user_id = " + uid + " , pixel_id = " + pixel_id + " , piggyback(s) Id = " + pbId);
        //check log
        boolean isCheck = fileAnalyzer.checkLog(requestDataPartnerProdEtlFileLocal, pattern);
        sshWrapperETL.tearDown(requestDataPartnerProdEtlFileLocal);
        return isCheck;
    }

    protected static void getFirstDriverFromFile() {
        //get data from file into List<String>
        Object[] array = new Object[1];
        List<String> ipList = FileHelper.getInstance().getDataFromFile(sourceDrivers);
        //end get data
        //parse first line into hashmap, all hashmaps put into array[1] and into List() (structure List<Object[]>)
        String[] params;
        HashMap<String, String> data = new HashMap<String, String>();
        params = ipList.get(0).split(";");
        for (int j = 0; j < columnNames.size(); j++) {
            data.put(columnNames.get(j), params[j]);
        }
        oneDriverInfo = data;
    }

    protected static void reloadpip() {
        jmxWrapper.execCommand("doReload");
        jmxWrapper.waitForReloading();
    }

    protected static void reloadcst() {
        jmxWrapperCST.execCommand("doReload");
        jmxWrapperCST.waitForReloading();
    }

    protected static void wait(int sec) {
        try {
            Thread.sleep(1000 * sec);
        } catch (InterruptedException e) {
            basePbLOG.info("Can't sleep thread");
        }
    }
}
