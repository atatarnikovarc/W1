package com.redaril.dmptf.tests.test.qualifierUpdaterJob;

import com.redaril.dmptf.tests.testnotready.qualifiers.builder.JMX;
import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.database.oracle.OracleWrapper;
import com.redaril.dmptf.util.file.FileHelper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 05.03.13
 * Time: 11:32
 * To change this template use File | Settings | File Templates.
 */
public class TestQualifierUpdaterJob {
    private static OracleWrapper dmpDb;
    private static OracleWrapper cscDb;
    private static List<String> user;
    private static List<String> interest;
    private static List<String> regQualifier;
    private static List<String> gsQualifier;
    private static List<String> gsQualPhrase;
    private static List<String> gsQualPhrase2;
    private final static String PATH_CONFIG = "config" + File.separator;
    private final static String FILE_PROPERTIES_ENV = "env.properties";
    private static Logger LOG;
    private final static String pathToSql = "data" + File.separator + "qualifierTransfer" + File.separator;
    private static Boolean isSetup = false;
    private final static String logFileParam = "qualUpdJob.log";
    private final static String LogSystemProperty = "DmptfLogFile";

    private void addItemIntoDB(OracleWrapper db, String file, List<String> params, String logMessage) {
        String script = FileHelper.getInstance().getDataWithParams(pathToSql + file, params);
        db.executeUpdate(script);
        LOG.info(logMessage + " Id = " + params.get(0));
    }

    private void createQualifiers() {
        regQualifier = new ArrayList<String>();
        regQualifier.add("77777");
        regQualifier.add("'autoregQualifier'");
        regQualifier.add("'TYPICAL_QUALIFIER'");
        regQualifier.add("55");
        regQualifier.add(interest.get(0));
        regQualifier.add("null");
        regQualifier.add("null");
        regQualifier.add("8");
        regQualifier.add("'http://autotest.com'");
        regQualifier.add("TO_DATE('01.01.13','dd.mm.yy')");
        regQualifier.add(user.get(0));
        regQualifier.add("TO_DATE('01.01.13','dd.mm.yy')");
        regQualifier.add("null");
        regQualifier.add(user.get(0));
        regQualifier.add("1");
        regQualifier.add("'N'");
        regQualifier.add("83");
        regQualifier.add("90");
        regQualifier.add("95");
        regQualifier.add("0");
        regQualifier.add("100");
        gsQualifier = new ArrayList<String>();
        gsQualifier.add("77778");
        gsQualifier.add("'autogsQualifier'");
        gsQualifier.add("'GENERAL_SEARCH'");
        gsQualifier.add("61");
        gsQualifier.add(interest.get(0));
        gsQualifier.add("null");
        gsQualifier.add("null");
        gsQualifier.add("8");
        gsQualifier.add("'http://autotest.com'");
        gsQualifier.add("TO_DATE('01.01.13','dd.mm.yy')");
        gsQualifier.add(user.get(0));
        gsQualifier.add("TO_DATE('01.01.13','dd.mm.yy')");
        gsQualifier.add("null");
        gsQualifier.add(user.get(0));
        gsQualifier.add("1");
        gsQualifier.add("'N'");
        gsQualifier.add("83");
        gsQualifier.add("90");
        gsQualifier.add("95");
        gsQualifier.add("0");
        gsQualifier.add("100");
    }

    private static void deleteItem(OracleWrapper db, String file, String param) {
        List<String> params = new ArrayList<String>();
        params.add(param);
        String script = FileHelper.getInstance().getDataWithParams(pathToSql + file, params);
        db.executeUpdate(script);
    }

    private List<String> getItem(OracleWrapper db, String file, List<String> item) {
        List<String> params = new ArrayList<String>();
        params.add(item.get(0));
        int size = item.size();
        String script = FileHelper.getInstance().getDataWithParams(pathToSql + file, params);
        ResultSet resultSet = db.executeSelect(script);
        params.clear();
        try {
            while (resultSet.next()) {
                int i = 1;
                while (i < size + 1) {
                    params.add(resultSet.getString(i));
                    i++;
                }

            }
            return params;
        } catch (SQLException e) {
            return params;
        }
    }

    private String getQualCount(OracleWrapper db, List<String> params) {
        String file;
        if (db.getDbName().equalsIgnoreCase("dmp")) file = "getQualifiersCountDmp.sql";
        else file = "getQualifiersCount.sql";
        String script = FileHelper.getInstance().getDataWithParams(pathToSql + file, params);
        ResultSet resultSet = db.executeSelect(script);
        String count = "";
        try {
            while (resultSet.next()) {
                count = resultSet.getString(1);
            }
            return count;
        } catch (SQLException e) {
            LOG.error("Can't get qualifier's count from db");
            return count;
        }
    }

    private void executeQualifierUpdaterJob(String ENV) {
        JMX jmx = new JMX(ENV);
        boolean isReboot = false;
        try {
            isReboot = jmx.reboot("QualifierUpdaterJob");
        } catch (Exception e) {
            LOG.error("Can't execute QualifierUpdaterJob. Exception = " + e.getLocalizedMessage());
        }
        if (isReboot) LOG.info("Reboot QualifierUpdaterJob successfully executed");

    }

    private static void deleteItems(OracleWrapper db) {
        if (db.getDbName() != "dmp") {
            deleteItem(db, "deleteQualifier.sql", gsQualifier.get(0));
            deleteItem(db, "deleteQualifier.sql", regQualifier.get(0));
        } else {
            //        deleteItem(db,"deleteGSQualifierDmpDb.sql",gsQualifier.get(0));
            //        deleteItem(db,"deleteRegQualifierDmpDb.sql",regQualifier.get(0));
        }
    }

    @Before
    public void setup() {
        if (!isSetup) {
            System.setProperty(LogSystemProperty, logFileParam);
            LOG = LoggerFactory.getLogger(TestQualifierUpdaterJob.class);
            ConfigurationLoader configEnv = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
            String ENV = configEnv.getProperty("env");
            cscDb = new OracleWrapper(ENV, "csc");
            dmpDb = new OracleWrapper(ENV, "dmp");
            LOG.info("========SETUP========");
            List<String> param = new ArrayList<String>();
            param.add("(Select max(interest_id) from nc_interest)");
            interest = getItem(cscDb, "getInterest.sql", param);
            param.clear();
            param.add("(Select max(user_id) from nb_user)");
            user = getItem(cscDb, "getUser.sql", param);
            createQualifiers();
            //LOG.info("Delete at Dmp");
            // deleteItems(dmpDb);
            LOG.info("Delete at CSC");
            //        deleteItems(cscDb);
            //        addItemIntoDB(cscDb, "createQualifier.sql", regQualifier, "RegQualifier was created successfully.");
            //        addItemIntoDB(cscDb, "createQualifier.sql", gsQualifier, "GSQualifier was created successfully.");
            // addItemIntoDB(cscDb, "createQualifierPhrase.sql", gsQualPhrase, "GSPhrase was created successfully.");
            // addItemIntoDB(cscDb, "createQualifierPhrase.sql", gsQualPhrase2, "GSPhrase2 was created successfully.");
            //        executeQualifierUpdaterJob(ENV);
            LOG.info("=====END OF SETUP======");
            isSetup = true;
        }
    }

    @AfterClass
    public static void tearDown() {
        LOG.info("ALL TESTS ARE COMPLETE.");
        LOG.info("Delete at Dmp");
        deleteItems(dmpDb);
        LOG.info("Delete at CSC");
        deleteItems(cscDb);
    }

    @Test
    public void testCountRegQualifiers() {
        List<String> paramsDMP = new ArrayList<String>();
        paramsDMP.add("REGEX_QUALIFIER");
        paramsDMP.add("3021");
        List<String> paramsCSC = new ArrayList<String>();
        paramsCSC.add("55");
        String countCsc = getQualCount(cscDb, paramsCSC);
        String countDmp = getQualCount(dmpDb, paramsDMP);
        assertTrue("testCountRegQualifiers failed. Count at dmp = " + countDmp + " . Count at csc = " + countCsc, countCsc.equalsIgnoreCase(countDmp));
        LOG.info("testCountRegQualifiers passed. Count of RegQualifiers = " + countCsc);
    }

//    @Test
//    public void testCountGSQualifiers() {
//        List<String> paramsDMP = new ArrayList<String>();
//        paramsDMP.add("GENERAL_SEARCH_QUALIFIER");
//        paramsDMP.add("3021");
//        List<String> paramsCSC = new ArrayList<String>();
//        paramsCSC.add("61");
//        String countCsc = getQualCount(cscDb, paramsCSC);
//        String countDmp = getQualCount(dmpDb, paramsDMP);
//        assertTrue("testCountGSQualifiers failed. Count at dmp = " + countDmp + " . Count at csc = " + countCsc, countCsc.equalsIgnoreCase(countDmp));
//        LOG.info("testCountGSQualifiers passed. Count of RegQualifiers = " + countCsc);
//    }

//    @Test
//    public void testCountUsers() {
//        String countCsc = getCount(cscDb, "getUserCount.sql");
//        String countBun = getCount(bunDb, "getUserCount.sql");
//        assertTrue("testCountUsers failed. Count at bun = " + countBun + " . Count at csc = " + countCsc, countCsc.equalsIgnoreCase(countBun));
//        LOG.info("testCountUsers passed. Count of Users = " + countCsc);
//    }
//
//    @Test
//    public void testCountInterests() {
//        String countCsc = getCount(cscDb, "getInterestCount.sql");
//        String countBun = getCount(bunDb, "getInterestCount.sql");
//        assertTrue("testCountInterests failed. Count at bun = " + countBun + " . Count at csc = " + countCsc, countCsc.equalsIgnoreCase(countBun));
//        LOG.info("testCountInterests passed. Count of Interests = " + countCsc);
//    }
//
//    @Test
//    public void testCountQualifierPhrases() {
//        String countCsc = getCount(cscDb, "getQualifierPhraseCount.sql");
//        String countBun = getCount(bunDb, "getQualifierPhraseCount.sql");
//        assertTrue("testCountQualifierPhrases failed. Count at bun = " + countBun + " . Count at csc = " + countCsc, countCsc.equalsIgnoreCase(countBun));
//        LOG.info("testCountQualifierPhrases passed. Count of Phrases = " + countCsc);
//    }
//    @Test
//    public void testTransferRegQualifier() {
//        List<String> regQualifierBun = getItem(bunDb, "getQualifier.sql",regQualifier);
//        List<String> regQualifierCsc = getItem(cscDb, "getQualifier.sql",regQualifier);
//        assertTrue("testTransferRegQualifier failed. Qualifiers are not equals.", regQualifierBun.equals(regQualifierCsc));
//        LOG.info("testTransferRegQualifier passed.");
//    }
//
//    @Test
//    public void testTransferGSQualifier() {
//        List<String> gsQualifierBun = getItem(bunDb, "getQualifier.sql",gsQualifier);
//        List<String> gsQualifierCsc = getItem(cscDb, "getQualifier.sql",gsQualifier);
//        assertTrue("testTransferGSQualifier failed. Qualifiers are not equals.", gsQualifierBun.equals(gsQualifierCsc));
//    }
//
//    @Test
//    public void testTransferQualifierPhrase() {
//        List<String> phraseBun = getItem(bunDb, "getQualifierPhrase.sql",gsQualPhrase);
//        List<String> phraseCsc = getItem(cscDb, "getQualifierPhrase.sql",gsQualPhrase);
//        assertTrue("testTransferQualifierPhrase failed. Qualifier's 1 phrase are not equals.", phraseBun.equals(phraseCsc));
//        phraseBun = getItem(bunDb, "getQualifierPhrase.sql",gsQualPhrase2);
//        phraseCsc = getItem(cscDb, "getQualifierPhrase.sql",gsQualPhrase2);
//        assertTrue("testTransferQualifierPhrase failed. Qualifier's 2 phrase are not equals.", phraseBun.equals(phraseCsc));
//        LOG.info("testTransferQualifierPhrase passed.");
//    }
//
//    @Test
//    public void testTransferUser() {
//        List<String> userBun = getItem(bunDb, "getUser.sql",user);
//        List<String> userCsc = getItem(cscDb, "getUser.sql",user);
//        assertTrue("testTransferUser failed. Users are not equals.", userBun.equals(userCsc));
//        LOG.info("testTransferUser passed.");
//    }
//
//    @Test
//    public void testTransferInterest() {
//        List<String> interestBun = getItem(bunDb, "getInterest.sql",interest);
//        List<String> interestCsc = getItem(cscDb, "getInterest.sql",interest);
//        List<String> fixedInterestBun = new ArrayList<String>();
//        for (String str : interestBun) {
//            if (str != null) {
//                fixedInterestBun.add(str);
//            }
//        }
//        String strDescr = fixedInterestBun.get(5);
//        fixedInterestBun.remove(5);
//        fixedInterestBun.add(strDescr);
//
//        List<String> fixedInterestCsc = new ArrayList<String>();
//        for (String str : interestCsc) {
//            if (str != null) {
//                fixedInterestCsc.add(str);
//            }
//        }
//        assertTrue("testTransferInterest failed. Interests are not equals.", fixedInterestCsc.equals(fixedInterestBun));
//        LOG.info("testTransferInterest passed.");
//    }
}
