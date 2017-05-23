package com.redaril.dmptf.tests.support.bdp;

import com.redaril.dmptf.tests.support.etl.EtlLogAnalyzer;
import com.redaril.dmptf.tests.support.etl.log.UserBusinessDataCall;
import com.redaril.dmptf.tests.support.etl.model.Model;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.database.oracle.OracleWrapper;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.network.appinterface.jmx.JMXWrapper;
import com.redaril.dmptf.util.network.protocol.ssh.SSHWrapper;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.fail;

abstract public class BaseBDP {

    //PATH
    private final static String SOURCE_PATH = "data" + File.separator + "bdp" + File.separator;
    protected final static String CONFIG_PATH = "config" + File.separator;

    //FILENAMES

    //sqls
    private final static String cleanScript = SOURCE_PATH + "clean_nc_business_data.sql";
    private final static String countScript = SOURCE_PATH + "count_nc_business_data.sql";
    protected static String selectScript = SOURCE_PATH + "select_nc_business_data.sql";
    protected static String updateScript = SOURCE_PATH + "update_nc_business_data.sql";

    //logs
    protected final static String logFileParam = "bdpParam.log";
    protected final static String logFileBase = "bdpBase.log";

    //properties
    protected final static String FILE_PROPERTIES_ENV = "env.properties";
    protected final static String testClassProperties = "bdp.properties";

    protected final static String LogSystemProperty = "DmptfLogFile";
    private static Logger LOG;

    //ETL
    protected static String etlFileName;  //name of ETL file
    protected static String pathToEtlLogs;
    protected static String localEtlFileName;  //name of local ETL file
    protected static String XMLModel = "data" + File.separator + "etl" + File.separator + "userBusinessDataCall.xml";
    protected static Class ModelClassName = UserBusinessDataCall.class;
    protected static Model model;
    protected static EtlLogAnalyzer logAnalyzer;

    //TEST
    protected HashMap<String, String> ipData;
    protected final static List<String> columnNames = Arrays.asList("ip", "industry_code", "business_size", "value_min", "value_max", "zip", "business_name", "isISP");
    protected static boolean isSetup;
    protected static String runScriptPath;
    protected static String shellBdpCommand;
    protected static String ipPath;
    protected static String generatedIPFile;
    protected static String sourceBDP = SOURCE_PATH + File.separator + "ip.txt";

    //WRAPPERS
    protected static JMXWrapper jmxWrapper;
    protected static SSHWrapper sshWrapper;
    protected static OracleWrapper oreacleWrapper;

    //CONFIGURATION
    protected static ConfigurationLoader testClassConfigurationLoader;
    protected static ConfigurationLoader envConfigurationLoader;
    protected static String hostBDP;
    protected static String hostETLBDP;
    protected static String ENV;
    protected static String envConfigID;

    @Before
    public void setUp() {
        if (!isSetup) {
            envConfigurationLoader = new ConfigurationLoader(CONFIG_PATH + FILE_PROPERTIES_ENV);
            ENV = envConfigurationLoader.getProperty("env");
            envConfigID = envConfigurationLoader.getProperty("configID");
            LOG = LoggerFactory.getLogger(BaseBDP.class);
            if (testClassConfigurationLoader == null) {
                testClassConfigurationLoader = new ConfigurationLoader(CONFIG_PATH + testClassProperties);
                setupTestClassProperties();
            }
            logAnalyzer = new EtlLogAnalyzer();
            jmxWrapper = new JMXWrapper(ENV, envConfigID, "bdp");
            oreacleWrapper = new OracleWrapper(ENV, "csc");
            isSetup = true;
        }
    }

    protected static void setupTestClassProperties() {
        ConfigurationLoader currentEnv = new ConfigurationLoader(CONFIG_PATH + ENV + ".properties");
        hostBDP = currentEnv.getProperty("host.bdp");
        hostETLBDP = currentEnv.getProperty("host.etl");
        runScriptPath = testClassConfigurationLoader.getProperty("runScriptPath");
        ipPath = testClassConfigurationLoader.getProperty("ipPath");
        generatedIPFile = testClassConfigurationLoader.getProperty("generatedIPFile");
        etlFileName = testClassConfigurationLoader.getProperty("etlFile");
        pathToEtlLogs = testClassConfigurationLoader.getProperty("etlpath");

        shellBdpCommand = "cd " + runScriptPath + " && ./run.sh";
        localEtlFileName = etlFileName + ".log";
    }

    protected Integer getSizeNcBusinessData() {
        LOG.info("Check table nc_business_data");
        String script = FileHelper.getInstance().getDataWithoutParams(countScript);
        ResultSet resultSet = oreacleWrapper.executeSelect(script);
        try {
            if (resultSet.next()) {
                int i = resultSet.getInt("count");
                resultSet.close();
                oreacleWrapper.closeStatement();
                return i;
            }
            fail("Can't get count of table from DB");
            return null;
        } catch (SQLException e) {
            LOG.error("Can't get count of table from DB. Exception = " + e.getLocalizedMessage());
        } finally {
            try {
                resultSet.close();
            } catch (SQLException e) {
            }
        }
        fail("Can't get count of table from DB");
        return null;
    }

    protected void cleanBDPCache() {
        Integer count = getSizeNcBusinessData();
        if (count == 0) {
            LOG.info("Table nc_business_data is empty");
        } else {
            LOG.info("Table nc_business_data is not empty. There are " + count + " records.");
            LOG.info("Clean table nc_business_data");
            String script = FileHelper.getInstance().getDataWithoutParams(cleanScript);
            oreacleWrapper.executeUpdate(script);
        }
        String result = jmxWrapper.execCommand("reloadData");
        if (!result.contains("0")) {
            LOG.error("Some data found at db after cleaning");
            fail("Some data found at db after cleaning");
        }
    }

    protected Boolean checkParamDB(Integer wait) {
        LOG.info("Check DB");
        Boolean isCheck;
        String script = FileHelper.getInstance().getDataWithoutParams(selectScript);
        ResultSet rowSet = oreacleWrapper.executeSelect(script);
        try {
            int count = 0;
            Boolean isISP = ipData.get("isISP").equals("1");
            if (isISP) {
                count = 1;
            }
            int k = 0;
            while (!getSizeNcBusinessData().equals(count) && k < wait) {
                try {
                    Thread.sleep(1000);
                    k++;
                    LOG.info("Sleep before DB will be updated. " + k + "sec.");
                } catch (InterruptedException e) {
                    LOG.info("Can't sleep the thread.");
                }
            }
            if (k >= wait) {
                LOG.error("DB was not updated!");
                return false;
            }

            if (isISP) {
                LOG.info("Table should contains 1 record(s). We found " + count + " record(s).");
                if (count != 1) {
                    isCheck = false;
                } else {
                    k = 0;
                    isCheck = true;
                    while (isCheck && k < 60) {
                        if (rowSet.next()) {
                            for (int i = 1; i < 7; i++) {
                                String columnName = columnNames.get(i);
                                String actual = rowSet.getString(columnName);
                                String expected = ipData.get(columnName);
                                if (!actual.equalsIgnoreCase(expected)) {
                                    isCheck = false;
                                }
                            }
                        }
                        if (isCheck) {
                            LOG.info("All required fields were founded.");
                            LOG.info("Check DB is PASSED");
                            return true;
                        }
                        k++;
                        LOG.info("Sleep before DB will be updated. " + k + "sec.");
                        isCheck = true;
                    }
                    LOG.error("Check DB is FAILED");
                    return isCheck;
                }
            } else {
                isCheck = count == 0;
                LOG.info("Table should contains 0 record(s). We found " + count + " record(s).");
            }
            rowSet.close();
            oreacleWrapper.closeStatement();
        } catch (SQLException e) {
            LOG.error("Can't get data from SQL response. Exception = " + e.getLocalizedMessage());
            return false;
        }
        return isCheck;
    }

    protected Boolean checkCountDB(Integer expCount, Integer wait) {
        LOG.info("Check count at DB");
        Integer i = 0;
        while (!getSizeNcBusinessData().equals(expCount) && i < wait) {
            try {
                Thread.sleep(1000);
                i++;
                LOG.info("Sleep before DB will be updated. " + i + "sec.");
            } catch (InterruptedException e) {
                LOG.info("Can't sleep the thread.");
            }
        }
        if (i < wait) {
            LOG.info("Count DB Passed");
            return true;
        } else {
            LOG.error("Count DB FAILED!");
            return false;
        }

    }

    protected static List<Object[]> getIPFromFile() {
        //get data from file ip.txt into List<String>
        List<Object[]> list = new ArrayList<Object[]>();
        List<String> ipList = FileHelper.getInstance().getDataFromFile(sourceBDP);
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
}

