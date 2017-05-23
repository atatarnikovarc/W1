package com.redaril.dmptf.util.database.sqlite;

import com.redaril.dmptf.tests.support.etl.EtlLogAnalyzer;
import com.redaril.dmptf.util.file.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

import static org.junit.Assert.fail;

public class SqliteWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(SqliteWrapper.class);
    private String PATH_OUTPUT;
    private String FILE_SQLITE_DB;
    private String PATH_RESOURCE;
    public Connection sqlite;
    private static EtlLogAnalyzer fileAnalyzer;

    public SqliteWrapper() {
        try {
            Class.forName("org.sqlite.JDBC");
            fileAnalyzer = new EtlLogAnalyzer();
        } catch (ClassNotFoundException e) {
            LOG.error("Can't find JDBC class.");
            fail("Can't find JDBC class.");
        }
    }

    public void setOutputPath(String output) {
        PATH_OUTPUT = output;
        LOG.debug("set output folder to: " + PATH_OUTPUT);
    }

//	public void setResourcePath ( String resource ) {
//		PATH_RESOURCE = resource;
//		LOG.debug("set resource folder to: " + PATH_RESOURCE);
//	}

    public void setSqliteDBFile(String filename) {
        FILE_SQLITE_DB = filename;
        LOG.debug("set sqlite db: " + FILE_SQLITE_DB);
    }

    private void close() {
        try {
            sqlite.close();
        } catch (SQLException e) {
            LOG.error("Can't close SQLite connection");
        }
    }

    public boolean isChecked(String id) {
        ResultSet rs;
        int count = 0;
        try {
            sqlite = DriverManager.getConnection("jdbc:sqlite:" + PATH_OUTPUT + FILE_SQLITE_DB);
            Statement stat = sqlite.createStatement();
            rs = stat.executeQuery("select count(1) from checked where id=\"" + id + "\";");
            count = Integer.valueOf(rs.getString(1));
            rs.close();
        } catch (SQLException e) {
            LOG.error("Can't connect(or execute query) to SQLite. Connection params = " + "jdbc:sqlite:" + PATH_OUTPUT + FILE_SQLITE_DB + "  Query =" + "select count(1) from checked where id=\"" + id + "\";");
        } finally {
            this.close();
        }

        return count > 0;
    }

    public int getRowCount(String table) {
        ResultSet rs;
        int count = 0;
        try {
            sqlite = DriverManager.getConnection("jdbc:sqlite:" + PATH_OUTPUT + FILE_SQLITE_DB);
            Statement stat = sqlite.createStatement();
            rs = stat.executeQuery("select count(1) from " + table + ";");
            count = Integer.valueOf(rs.getString(1));
            rs.close();
        } catch (SQLException e) {
            LOG.error("Can't connect to SQLite. Connection params = " + "jdbc:sqlite:" + PATH_OUTPUT + FILE_SQLITE_DB);
            fail("Can't connect to SQLite. Connection params = " + "jdbc:sqlite:" + PATH_OUTPUT + FILE_SQLITE_DB);
        } finally {
            this.close();
        }
        return count;
    }

    public RowSet executeQuery(String query) {
        RowSet rowSet = new RowSet();
        ResultSet rs;
        try {
            sqlite = DriverManager.getConnection("jdbc:sqlite:" + PATH_OUTPUT + FILE_SQLITE_DB);
            Statement stat = sqlite.createStatement();
            rs = stat.executeQuery(query);
            rowSet.parseResultSetRow(rs);
            rs.close();
        } catch (SQLException e) {
            LOG.error("Can't connect(or execute query) to SQLite. Connection params = " + "jdbc:sqlite:" + PATH_OUTPUT + FILE_SQLITE_DB + "  Query =" + query);
            fail("Can't connect(or execute query) to SQLite. Connection params = " + "jdbc:sqlite:" + PATH_OUTPUT + FILE_SQLITE_DB + "  Query =" + query);
        } finally {
            this.close();
        }
        return rowSet;
    }

    public void executeUpdate(String query) {
        try {
            sqlite = DriverManager.getConnection("jdbc:sqlite:" + PATH_OUTPUT + FILE_SQLITE_DB);
            Statement stat = sqlite.createStatement();
            stat.executeUpdate(query);
        } catch (SQLException e) {
            LOG.error("Can't connect(or execute query) to SQLite. Connection params = " + "jdbc:sqlite:" + PATH_OUTPUT + FILE_SQLITE_DB + "  Query =" + query);
            fail("Can't connect(or execute query) to SQLite. Connection params = " + "jdbc:sqlite:" + PATH_OUTPUT + FILE_SQLITE_DB + "  Query =" + query);
        } finally {
            this.close();
        }
    }

    public void executeSqlScriptFile(String filename) {
        String[] sql;
        try {
            sql = FileHelper.getInstance().getDataWithoutParams(filename).split("; ");
            sqlite = DriverManager.getConnection("jdbc:sqlite:" + PATH_OUTPUT + FILE_SQLITE_DB);
            Statement stat = sqlite.createStatement();

            for (String sqlCommand : sql) {
                LOG.debug("sql: " + sqlCommand);
                stat.executeUpdate(sqlCommand);
            }

        } catch (SQLException e) {
            LOG.error("Can't connect(or execute query) to SQLite. Connection params = " + "jdbc:sqlite:" + PATH_OUTPUT + FILE_SQLITE_DB + "  Script from file =" + filename);
            fail("Can't connect(or execute query) to SQLite. Connection params = " + "jdbc:sqlite:" + PATH_OUTPUT + FILE_SQLITE_DB + "  Script from file =" + filename);
        } finally {
            this.close();
        }
    }


    public void moveCheckedQualifier(String ID) {
        RowSet rowSet = new RowSet();
        rowSet = this.executeQuery("select * from toCheck where id = \"" + ID + "\";");
        while (rowSet.next()) {
            this.executeUpdate("insert into checked values (null, " +
                    "\"" + rowSet.getRow("id") + "\"," +
                    "\"" + rowSet.getRow("url") + "\"," +
                    "\"" + rowSet.getRow("interest") + "\"," +
                    "\"" + rowSet.getRow("interestID") + "\"," +
                    "\"" + rowSet.getRow("dataSource") + "\");");
            this.executeUpdate("delete from toCheck " + "where id = \"" + ID + "\";");
        }
    }

}

