package com.redaril.dmptf.tests.test.bun;

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
 * Date: 20.02.13
 * Time: 15:40
 * To change this template use File | Settings | File Templates.
 */
public class TestBunUpdater {
    private static OracleWrapper bunDb;
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
    private final static String logFileParam = "bunUpdater.log";
    private final static String LogSystemProperty = "DmptfLogFile";

    private void addItemIntoDB(OracleWrapper db, String file, List<String> params, String logMessage) {
        String script = FileHelper.getInstance().getDataWithParams(pathToSql + file, params);
        db.executeUpdate(script);
        LOG.info(logMessage + " Id = " + params.get(0));
    }

    private void createUser(OracleWrapper db) {
        user = new ArrayList<String>();
        //data is added to the order as in the table NB_User
        user.add("777");
        user.add("'autoUser'");
        user.add("'password'");
        user.add("'Y'");
        user.add("'Y'");
        user.add("TO_DATE('31.12.20','dd.mm.yy')");
        user.add("'auto'");
        user.add("'test'");
        user.add("'auto123'");
        user.add("'mr.'");
        user.add("'yksenofontov@redaril.com'");
        user.add("'none'");
        user.add("null");
    }

    private void createInterestAtBun(OracleWrapper db) {
        interest = new ArrayList<String>();
        interest.add("777");
        interest.add("'autoInterest'");
        interest.add("'8723'");
        interest.add("'New Tree:AutoTest'");
        interest.add("'CatID777'");
        interest.add("null");
        interest.add("null");
        interest.add("null");
        interest.add("null");
        interest.add("'autotest123'");
        interest.add("null");
        interest.add("null");
        interest.add("1005"); //dataSource=RA
        interest.add("777");
    }

    private void createQualifiersAtBun(OracleWrapper db) {
        regQualifier = new ArrayList<String>();
        regQualifier.add("77777");
        regQualifier.add("'autoregQualifier'");
        regQualifier.add("'TYPICAL_QUALIFIER'");
        regQualifier.add("55");
        regQualifier.add("777");
        regQualifier.add("null");
        regQualifier.add("null");
        regQualifier.add("8");
        regQualifier.add("'http://autotest.com'");
        regQualifier.add("TO_DATE('01.01.13','dd.mm.yy')");
        regQualifier.add("777");
        regQualifier.add("TO_DATE('01.01.13','dd.mm.yy')");
        regQualifier.add("null");
        regQualifier.add("777");
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
        gsQualifier.add("777");
        gsQualifier.add("null");
        gsQualifier.add("null");
        gsQualifier.add("8");
        gsQualifier.add("'http://autotest.com'");
        gsQualifier.add("TO_DATE('01.01.13','dd.mm.yy')");
        gsQualifier.add("777");
        gsQualifier.add("TO_DATE('01.01.13','dd.mm.yy')");
        gsQualifier.add("null");
        gsQualifier.add("777");
        gsQualifier.add("1");
        gsQualifier.add("'N'");
        gsQualifier.add("83");
        gsQualifier.add("90");
        gsQualifier.add("95");
        gsQualifier.add("0");
        gsQualifier.add("100");
    }

    private void createGSPhrase(OracleWrapper db) {
        gsQualPhrase = new ArrayList<String>();
        gsQualPhrase.add("777777");
        gsQualPhrase.add(gsQualifier.get(0));
        gsQualPhrase.add("'autotest'");
        gsQualPhrase.add("null");
        gsQualPhrase.add("null");
        gsQualPhrase.add("null");
        gsQualPhrase.add("null");
        gsQualPhrase.add("91");
        gsQualPhrase.add("null");
        gsQualPhrase2 = new ArrayList<String>();
        gsQualPhrase2.add("777778");
        gsQualPhrase2.add(gsQualifier.get(0));
        gsQualPhrase2.add("'autotest'");
        gsQualPhrase2.add("'12345'");
        gsQualPhrase2.add("null");
        gsQualPhrase2.add("null");
        gsQualPhrase2.add("null");
        gsQualPhrase2.add("92");
        gsQualPhrase2.add("null");
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
            resultSet.close();
            db.closeStatement();
            return params;
        } catch (SQLException e) {
            return params;
        }
    }

    private String getQualCount(OracleWrapper db, String type) {
        List<String> params = new ArrayList<String>();
        params.add(type);
        int size = 1;
        String script = FileHelper.getInstance().getDataWithParams(pathToSql + "getQualifiersCount.sql", params);
        ResultSet resultSet = db.executeSelect(script);
        params.clear();
        String count = "";
        try {
            while (resultSet.next()) {
                count = resultSet.getString(1);
            }
            resultSet.close();
            db.closeStatement();
            return count;
        } catch (SQLException e) {
            LOG.error("Can't get qualifier's count from db");
            return count;
        }
    }

    private String getCount(OracleWrapper db, String file) {
        int size = 1;
        String script = FileHelper.getInstance().getDataWithoutParams(pathToSql + file);
        ResultSet resultSet = db.executeSelect(script);
        String count = "";
        try {
            while (resultSet.next()) {
                count = resultSet.getString(1);
            }
            resultSet.close();
            db.closeStatement();
            return count;
        } catch (SQLException e) {
            LOG.info("Can't get count from db");
            return count;
        }
    }

    private void executeBunUpdater() {
        try {
            LOG.info("Run BunUpdater");
            cscDb.getConnection().prepareCall("{call bunupdater_env3bun()}").execute();
        } catch (SQLException e) {
            LOG.error("Can't execute BUNUPDATER. Exception = " + e.getLocalizedMessage());
        }
    }

    private static void deleteItems(OracleWrapper db) {
        deleteItem(db, "deleteQualifierPhrase.sql", gsQualPhrase.get(0));
        deleteItem(db, "deleteQualifierPhrase.sql", gsQualPhrase2.get(0));
        deleteItem(db, "deleteQualifier.sql", gsQualifier.get(0));
        deleteItem(db, "deleteQualifier.sql", regQualifier.get(0));
        deleteItem(db, "deleteInterest.sql", interest.get(0));
        deleteItem(db, "deleteUser.sql", user.get(0));
    }

    @Before
    public void setup() {
        if (!isSetup) {
            System.setProperty(LogSystemProperty, logFileParam);
            LOG = LoggerFactory.getLogger(TestBunUpdater.class);
            ConfigurationLoader configEnv = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES_ENV);
            String ENV = configEnv.getProperty("env");
            cscDb = new OracleWrapper(ENV, "csc");
            bunDb = new OracleWrapper(ENV, "bun");
            LOG.info("========SETUP========");
            createUser(bunDb);
            createInterestAtBun(bunDb);
            createQualifiersAtBun(bunDb);
            createGSPhrase(bunDb);
            deleteItems(bunDb);
            deleteItems(cscDb);
            addItemIntoDB(bunDb, "createUser.sql", user, "User was created successfully.");
            addItemIntoDB(bunDb, "createInterest.sql", interest, "Interest was created successfully.");
            addItemIntoDB(bunDb, "createQualifier.sql", regQualifier, "RegQualifier was created successfully.");
            addItemIntoDB(bunDb, "createQualifier.sql", gsQualifier, "GSQualifier was created successfully.");
            addItemIntoDB(bunDb, "createQualifierPhrase.sql", gsQualPhrase, "GSPhrase was created successfully.");
            addItemIntoDB(bunDb, "createQualifierPhrase.sql", gsQualPhrase2, "GSPhrase2 was created successfully.");
            executeBunUpdater();
            LOG.info("=====END OF SETUP======");
            isSetup = true;
        }
    }

    @AfterClass
    public static void tearDown() {
        LOG.info("ALL TESTS ARE COMPLETE.");
        LOG.info("Delete at Bun");
        deleteItems(bunDb);
        LOG.info("Delete at CSC");
        deleteItems(cscDb);
    }

    @Test
    public void testCountRegQualifiers() {
        String countCsc = getQualCount(cscDb, "55");
        String countBun = getQualCount(bunDb, "55");
        assertTrue("testCountRegQualifiers failed. Count at bun = " + countBun + " . Count at csc = " + countCsc, countCsc.equalsIgnoreCase(countBun));
        LOG.info("testCountRegQualifiers passed. Count of RegQualifiers = " + countCsc);
    }

    @Test
    public void testCountUsers() {
        String countCsc = getCount(cscDb, "getUserCount.sql");
        String countBun = getCount(bunDb, "getUserCount.sql");
        assertTrue("testCountUsers failed. Count at bun = " + countBun + " . Count at csc = " + countCsc, countCsc.equalsIgnoreCase(countBun));
        LOG.info("testCountUsers passed. Count of Users = " + countCsc);
    }

    @Test
    public void testCountInterests() {
        String countCsc = getCount(cscDb, "getInterestCount.sql");
        String countBun = getCount(bunDb, "getInterestCount.sql");
        assertTrue("testCountInterests failed. Count at bun = " + countBun + " . Count at csc = " + countCsc, countCsc.equalsIgnoreCase(countBun));
        LOG.info("testCountInterests passed. Count of Interests = " + countCsc);
    }

    @Test
    public void testCountQualifierPhrases() {
        String countCsc = getCount(cscDb, "getQualifierPhraseCount.sql");
        String countBun = getCount(bunDb, "getQualifierPhraseCount.sql");
        assertTrue("testCountQualifierPhrases failed. Count at bun = " + countBun + " . Count at csc = " + countCsc, countCsc.equalsIgnoreCase(countBun));
        LOG.info("testCountQualifierPhrases passed. Count of Phrases = " + countCsc);
    }

    @Test
    public void testCountGSQualifiers() {
        String countCsc = getQualCount(cscDb, "61");
        String countBun = getQualCount(bunDb, "61");
        assertTrue("testCountGSQualifiers failed. Count at bun = " + countBun + " . Count at csc = " + countCsc, countCsc.equalsIgnoreCase(countBun));
        LOG.info("testCountGSQualifiers passed. Count of GSQualifiers = " + countCsc);
    }

    @Test
    public void testTransferRegQualifier() {
        List<String> regQualifierBun = getItem(bunDb, "getQualifier.sql", regQualifier);
        List<String> regQualifierCsc = getItem(cscDb, "getQualifier.sql", regQualifier);
        assertTrue("testTransferRegQualifier failed. Qualifiers are not equals.", regQualifierBun.equals(regQualifierCsc));
        LOG.info("testTransferRegQualifier passed.");
    }

    @Test
    public void testTransferGSQualifier() {
        List<String> gsQualifierBun = getItem(bunDb, "getQualifier.sql", gsQualifier);
        List<String> gsQualifierCsc = getItem(cscDb, "getQualifier.sql", gsQualifier);
        assertTrue("testTransferGSQualifier failed. Qualifiers are not equals.", gsQualifierBun.equals(gsQualifierCsc));
    }

    @Test
    public void testTransferQualifierPhrase() {
        List<String> phraseBun = getItem(bunDb, "getQualifierPhrase.sql", gsQualPhrase);
        List<String> phraseCsc = getItem(cscDb, "getQualifierPhrase.sql", gsQualPhrase);
        assertTrue("testTransferQualifierPhrase failed. Qualifier's 1 phrase are not equals.", phraseBun.equals(phraseCsc));
        phraseBun = getItem(bunDb, "getQualifierPhrase.sql", gsQualPhrase2);
        phraseCsc = getItem(cscDb, "getQualifierPhrase.sql", gsQualPhrase2);
        assertTrue("testTransferQualifierPhrase failed. Qualifier's 2 phrase are not equals.", phraseBun.equals(phraseCsc));
        LOG.info("testTransferQualifierPhrase passed.");
    }

    @Test
    public void testTransferUser() {
        List<String> userBun = getItem(bunDb, "getUser.sql", user);
        List<String> userCsc = getItem(cscDb, "getUser.sql", user);
        assertTrue("testTransferUser failed. Users are not equals.", userBun.equals(userCsc));
        LOG.info("testTransferUser passed.");
    }

    @Test
    public void testTransferInterest() {
        List<String> interestBun = getItem(bunDb, "getInterest.sql", interest);
        List<String> interestCsc = getItem(cscDb, "getInterest.sql", interest);
        List<String> fixedInterestBun = new ArrayList<String>();
        for (String str : interestBun) {
            if (str != null) {
                fixedInterestBun.add(str);
            }
        }
        String strDescr = fixedInterestBun.get(5);
        fixedInterestBun.remove(5);
        fixedInterestBun.add(strDescr);

        List<String> fixedInterestCsc = new ArrayList<String>();
        for (String str : interestCsc) {
            if (str != null) {
                fixedInterestCsc.add(str);
            }
        }
        assertTrue("testTransferInterest failed. Interests are not equals.", fixedInterestCsc.equals(fixedInterestBun));
        LOG.info("testTransferInterest passed.");
    }
}
