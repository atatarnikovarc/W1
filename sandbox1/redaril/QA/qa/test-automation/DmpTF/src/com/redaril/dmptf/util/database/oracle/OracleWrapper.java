package com.redaril.dmptf.util.database.oracle;

import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.database.sqlite.SqliteWrapper;
import com.redaril.dmptf.util.date.DateWrapper;
import com.redaril.dmptf.util.file.FileHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class OracleWrapper {

    //PATH
    private final static String PATH_CONFIG = "config" + File.separator;
    private final static String PATH_OUTPUT = "output" + File.separator + "oraclewrapper" + File.separator;

    //FILE
    private final static String FILE_PROPERTIES_ENV = "env.properties";
    private final static String FILE_SQLITE_DB = "regular_qualifiers.db";
    private final static String FILE_QUALIFIERS_SQL = "data" + File.separator + "qualifiers" + File.separator +
            "regular" + File.separator + "getNewRegularQualifiers.sql";
    private final static String FILE_PIXEL_ID_SQL = "data" + File.separator + "qualifiers" + File.separator +
            "regular" + File.separator + "getPixelIDbyDataSource.sql";

    //CONFIG
    private Logger LOG;
    private String ENV;
    private String dataBase;
    //WRAPPER
    private Connection connection;
    private SqliteWrapper sqlite;
    private Statement stmt;
    private String host;
    private String port;
    private String sid;

    public String getDbName() {
        return dataBase;
    }

    public void closeStatement() {
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            LOG.info("Can't close statement. Exception = " + e.getLocalizedMessage());
        }
    }

    public SqliteWrapper getSqlite() {
        if (sqlite == null) {
            sqlite = new SqliteWrapper();
        }
        return sqlite;
    }

    public Connection getConnection() {
        if (connection == null) {
            connection = dataBaseConnect(dataBase);
        }
        return connection;
    }

    public OracleWrapper(String env, String dataBase) {
        LOG = LoggerFactory.getLogger(OracleWrapper.class);
        ENV = env;
        if (dataBase.equalsIgnoreCase("bun") && env.equalsIgnoreCase("prod")) {
            host = "oradb02.ic";
            port = "1521";
            sid = "bun";
        } else {
            ConfigurationLoader localConfigurationLoader = new ConfigurationLoader(PATH_CONFIG + ENV + ".properties");
            host = localConfigurationLoader.getProperty("dbHost");
            port = localConfigurationLoader.getProperty("dbPort");
            sid = localConfigurationLoader.getProperty("dbSid");
        }

        this.dataBase = dataBase;
    }

    public String getPixelIDbyDataSource(String dataSource) {
        String res = "";
        try {
            Statement stmt = getConnection().createStatement();
            LOG.debug("Get pixelID for data_source= " + dataSource);
            List<String> params = new ArrayList<String>();
            params.add(dataSource);
            ResultSet rset = stmt.executeQuery(FileHelper.getInstance().getDataWithParams(FILE_PIXEL_ID_SQL, params));
            while (rset.next()) {
                res = rset.getString("TAG_ID");
            }
            rset.close();
            stmt.close();
            return res;
        } catch (SQLException e) {
            LOG.warn("Can't get pixel from DB. Exception: " + e.getMessage());
            return null;
        }
    }

    public String switchQuote(String in) {
        return in.replace("\"", "$");
    }

    public String unswitchQuote(String in) {
        return in.replace("$", "\"");
    }

    public boolean getNewRegularQualifiers(String begin, @Nullable String end, @Nullable String modifiedDate) {
        getSqlite().setOutputPath(PATH_OUTPUT);
        getSqlite().setSqliteDBFile(FILE_SQLITE_DB);
        try {
            ConfigurationLoader configQualifiers = new ConfigurationLoader(PATH_CONFIG + "app.properties");
            Statement stmt = getConnection().createStatement();
            List<String> params = new ArrayList<String>();
            params.add(begin);
            if (end != null) {
                params.add(end);
            } else {
                end = DateWrapper.getPreviousDateDDMMYY(1);
                params.add(end);
            }
            if (modifiedDate != null) params.add(modifiedDate);
            else params.add("");
            String script = FileHelper.getInstance().getDataWithParams(FILE_QUALIFIERS_SQL, params);
            LOG.info(script);
            ResultSet rset = stmt.executeQuery(script);
            String ignoreChecked = configQualifiers.getProperty("ignoreChecked");
            while (rset.next()) {
                LOG.debug(switchQuote(rset.getString(1)) + "	" +
                        switchQuote(rset.getString(2)) + "	" + switchQuote(rset.getString(3)) + "	" +
                        switchQuote(rset.getString(4)) + "	" + switchQuote(rset.getString(5)));
                if (getSqlite().isChecked(rset.getString(3))) {
                    if (ignoreChecked.equals("true")) {
                        getSqlite().executeUpdate("insert into checked values (null, " +
                                "\"" + switchQuote(rset.getString(3)) + "\"," +
                                "\"" + switchQuote(rset.getString(1)) + "\"," +
                                "\"" + switchQuote(rset.getString(2)) + "\"," +
                                "\"" + switchQuote(rset.getString(4)) + "\"," +
                                "\"" + switchQuote(rset.getString(5)) + "\");");
                    } else {
                        getSqlite().executeUpdate("insert into toCheck values (null, " +
                                "\"" + switchQuote(rset.getString(3)) + "\"," +
                                "\"" + switchQuote(rset.getString(1)) + "\"," +
                                "\"" + switchQuote(rset.getString(2)) + "\"," +
                                "\"" + switchQuote(rset.getString(4)) + "\"," +
                                "\"" + switchQuote(rset.getString(5)) + "\");");
                        getSqlite().executeUpdate("delete from checked where id = \"" +
                                rset.getString(3) + "\";");
                    }
                } else {
                    getSqlite().executeUpdate("insert into toCheck values (null, " +
                            "\"" + switchQuote(rset.getString(3)) + "\"," +
                            "\"" + switchQuote(rset.getString(1)) + "\"," +
                            "\"" + switchQuote(rset.getString(2)) + "\"," +
                            "\"" + switchQuote(rset.getString(4)) + "\"," +
                            "\"" + switchQuote(rset.getString(5)) + "\");");
                    getSqlite().executeUpdate("delete from checked where id = \"" +
                            switchQuote(rset.getString(3)) + "\";");
                }
            }
        } catch (SQLException e) {
            LOG.error("Can't get regular qualifiers. Exception: " + e.getMessage());
            return false;
        }
        return true;
    }

    private String getCscDBLogin() {
        return ENV + "_meta";
    }

    private String getCscDBPassword() {
        return ENV + "_meta";
    }

    private String getDmpDBLogin() {
        return ENV + "_dmp";
    }

    private String getBunDBLogin() {
        return ENV + "_bun";
    }

    private String getBunDBPassword() {
        return ENV + "_bun";
    }

    private String getDmpDBPassword() {
        return ENV + "_dmp";
    }

    private Connection dataBaseConnect(String dataBase) {
        try {
            Connection connection;
            String url = "jdbc:oracle:thin:@" +
                    host + ":" +
                    port + ":" +
                    sid;

            Class.forName("oracle.jdbc.driver.OracleDriver");
            if (dataBase.equalsIgnoreCase("csc")) {
                connection = DriverManager.getConnection(url, getCscDBLogin(), getCscDBPassword());
            } else if (dataBase.equalsIgnoreCase("dmp")) {
                connection = DriverManager.getConnection(url, getDmpDBLogin(), getDmpDBPassword());
            } else if (dataBase.equalsIgnoreCase("bun")) {
                if (ENV.equalsIgnoreCase("prod")) {
                    connection = DriverManager.getConnection(url, "bun_metadb", "bun_metadb");
                } else connection = DriverManager.getConnection(url, getBunDBLogin(), getBunDBPassword());
            } else {
                connection = null;
            }
            LOG.debug("connecting to " + dataBase + ": " + url);
            return connection;
        } catch (ClassNotFoundException e) {
            LOG.error("Can't find OracleDriver class");
            fail("Can't find OracleDriver class");
            return null;
        } catch (SQLException e) {
            LOG.error("Can't get connection to DB. Connection params = " + "jdbc:oracle:thin:@" +
                    host + ":" +
                    port + ":" +
                    sid);
            fail("Can't get connection to DB");
            return null;
        }
    }

    public void executeUpdate(String script) {
        try {
            stmt = getConnection().createStatement();
            stmt.executeUpdate(script);
            stmt.close();
        } catch (SQLException e) {
            if (stmt != null) try {
                stmt.close();
            } catch (SQLException e1) {
            }
            LOG.error("Can't execute script = " + script);
            LOG.error("Exception = " + e.getLocalizedMessage());
            fail("Can't execute script = " + script + " .Exception = " + e.getLocalizedMessage());
        }
    }

    public ResultSet executeSelect(String script) {
        ResultSet set = null;
        try {
            stmt = getConnection().createStatement();
            set = stmt.executeQuery(script);
            return set;
        } catch (SQLException e) {
            if (set != null)
                try {
                    set.close();
                } catch (SQLException e1) {
                }
            LOG.error("Can't execute script = " + script);
            LOG.error("Exception = " + e.getLocalizedMessage());
            fail("Can't execute script = " + script + " .Exception = " + e.getLocalizedMessage());
        }
        return null;
    }

}
