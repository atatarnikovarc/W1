package com.redaril.dmptf.tests.testnotready.db;

import com.redaril.dmptf.util.configuration.LogConfigurer;
import com.redaril.dmptf.util.database.oracle.OracleWrapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 17.04.13
 * Time: 10:39
 * To change this template use File | Settings | File Templates.
 */
public class TestCategoryAvailability {
    private static Logger LOG;
    protected final static String LogSystemProperty = "DmptfLogFile";
    private static OracleWrapper ora;

    private long getCategoryNumber(String ds) {
        String script_bizo = "select count(*) from category where buyable = '1' and deleted = '0' and data_source = " + ds;
        ResultSet rset = ora.executeSelect(script_bizo);
        String x = "0";
        try {
            //while (rset.next()) {
            rset.next();
            x = rset.getString(1);
            rset.close();
        } catch (SQLException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            ora.closeStatement();
        }
        return Long.valueOf(x);
    }

    @BeforeClass
    public static void setup() {
        System.setProperty(LogSystemProperty, "cat.log");
        LogConfigurer.initLogback();
        LOG = LoggerFactory.getLogger(TestCategoryAvailability.class);
        ora = new OracleWrapper("env1", "dmp");
    }

    @Test
    public void testCategoryNumberBizo() {
        String dataSource = "'1006'";
        long count = getCategoryNumber(dataSource);
        //List<String> params = new ArrayList<String>();
        assertTrue("Test Failed", count < 350);
        assertTrue("Test Failed", count > 290);
    }

    @Test
    public void testCategoryNumberDatonics() {
        String dataSource = "'1010'";
        long count = getCategoryNumber(dataSource);
        //List<String> params = new ArrayList<String>();
        assertTrue("Test Failed", count < 370);
        assertTrue("Test Failed", count > 300);
    }

    @Test
    public void testCategoryNumberDemandBase() {
        String dataSource = "'1004'";
        long count = getCategoryNumber(dataSource);
        //List<String> params = new ArrayList<String>();
        assertTrue("Test Failed", count < 300);
        assertTrue("Test Failed", count > 240);
    }

    @Test
    public void testCategoryNumberDigitalEnvoy() {
        String dataSource = "'1009'";
        long count = getCategoryNumber(dataSource);
        //List<String> params = new ArrayList<String>();
        assertTrue("Test Failed", count < 4750);
        assertTrue("Test Failed", count > 4680);
    }

    @Test
    public void testCategoryNumberExelate() {
        String dataSource = "'1003'";
        long count = getCategoryNumber(dataSource);
        //List<String> params = new ArrayList<String>();
        assertTrue("Test Failed", count<1950);
        assertTrue("Test Failed", count>1820);
    }

    @Test
    public void testCategoryNumberIXINetworkRequest() {
        String dataSource = "'1012'";
        long count = getCategoryNumber(dataSource);
        //List<String> params = new ArrayList<String>();
        assertTrue("Test Failed", count < 150);
        assertTrue("Test Failed", count > 100);
    }

    @Test
    public void testCategoryNumberIXIOpenMarketRequest() {
        String dataSource = "'1011'";
        long count = getCategoryNumber(dataSource);
        //List<String> params = new ArrayList<String>();
        assertTrue("Test Failed", count < 250);
        assertTrue("Test Failed", count > 200);
    }

    @Test
    public void testCategoryNumberLiveRampRequest() {
        String dataSource = "'1001'";
        long count = getCategoryNumber(dataSource);
        //List<String> params = new ArrayList<String>();
        assertTrue("Test Failed", count < 150);
        assertTrue("Test Failed", count > 100);
    }

    @Test
    public void testCategoryNumberRedArilInterest() {
        String dataSource = "'1005'";
        long count = getCategoryNumber(dataSource);
        //List<String> params = new ArrayList<String>();
        assertTrue("Test Failed", count < 1860);
        assertTrue("Test Failed", count > 1800);
    }

    @Test
    public void testCategoryNumberTargusInfo() {
        String dataSource = "'1007'";
        long count = getCategoryNumber(dataSource);
        //List<String> params = new ArrayList<String>();
        assertTrue("Test Failed", count < 300);
        assertTrue("Test Failed", count > 210);
    }

}

