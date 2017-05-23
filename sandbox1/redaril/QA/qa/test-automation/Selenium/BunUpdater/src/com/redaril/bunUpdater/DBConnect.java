package com.redaril.bunUpdater;

//import java.io.BufferedReader;
//import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.sql.*;

public class DBConnect {
	final private static String driverName = "oracle.jdbc.driver.OracleDriver";
	final private static String dbHost = Config.getProperty("dbHost");
	final private static String dbPost = Config.getProperty("dbPort");
	final private static String dbSid = Config.getProperty("dbSid");
    //private String qualifierURLPrefix = "http://p.raasnet.com:8080/partners/universal/in?pid=9&ndl=http%3A//";
    private String DATE;
    private String ENV;
    private String LOGFILENAME;

	DBConnect ( String env, String date ) {
		ENV = env;
		DATE = date;
		LOGFILENAME = "bunLog_ " + DATE + ".log";
	}
	
	public int getQualifiersCount () throws Exception {
		Connection dbConn = connect();
		if ( dbConn == null  ) {
	        System.out.println("Connection FAILED");
	        System.exit(1);
		}
        System.out.println("Connection OK");
        int result = 0;
        try {
        	String sqlQuery = "SELECT count(1) " +
			"FROM nt_qualifier " +
			"JOIN nc_interest ON nc_interest.interest_id = nt_qualifier.interest_id " +
			"WHERE nt_qualifier.created_date_time >= TO_DATE('" + DATE + "','dd.mm.yy') " +
			"AND nt_qualifier.created_by_user_id = 107 " +
			"AND nt_qualifier.status_code_id = 95";
        	
        	Statement stmt = dbConn.createStatement();
	        ResultSet rset = stmt.executeQuery(sqlQuery);
	        rset.next();
        	println("Records Count= " + rset.getString(1));
        	result = Integer.parseInt(rset.getString(1));
	    } catch (Exception e) {
	        System.out.println("Exception:\n" + e.getMessage());
	        return 0;
	    }
	    return result;	
	}
	
	public boolean getNewQualifiers () throws Exception {
		String filename= "lastTimeAddedQualifiers" + "_" + DATE + ".ql";

		File fileToDelete = new File(filename);
		if (fileToDelete.exists()) {
			fileToDelete.delete();
		}
		
		Connection dbConn = connect();
		if ( dbConn == null  ) {
	        System.out.println("Connection FAILED");
	        System.exit(1);
		}
        System.out.println("Connection OK");

        try {
        	String sqlQuery = "SELECT nt_qualifier.base_url, nc_interest.name, " +
        			"nt_qualifier.qualifier_id, nt_qualifier.interest_id " +
        			"FROM nt_qualifier " +
        			"JOIN nc_interest ON nc_interest.interest_id = nt_qualifier.interest_id " +
        			"WHERE nt_qualifier.created_date_time >= TO_DATE('" + DATE + "','dd.mm.yy') " +
        			"AND nt_qualifier.created_by_user_id = 107 " +
        			"AND nt_qualifier.status_code_id = 95 " +
        			"ORDER BY nt_qualifier.created_date_time DESC";

        	Statement stmt = dbConn.createStatement();
	        ResultSet rset = stmt.executeQuery(sqlQuery);
	        while (rset.next()) {
	        	println(rset.getString(1) + "	" + rset.getString(2) + "	" + rset.getString(3) + "	" + rset.getString(4));
	        	appendStringToFile(filename, "\r\nQualifier URL: " + getQualifier(rset.getString(1)) + "\r\n" + 
	        			"## qualifier_id=" + rset.getString(3) + "; interest_id=" + rset.getString(4) + ";\r\n" +
	        			rset.getString(2) + "\r\n" +
	        			"--------------------------------------------------------------------------------\r\n");
	        }
	    } catch (Exception e) {
	        System.out.println("Exception:\n" + e.getMessage());
	        return false;
	    }
	    return true;
	}

	private String getDBLogin() {
		return ENV + "_meta";
	}
	private String getDBPassword() {
		return ENV + "_meta";
	}
	
	private Connection connect() {
	    try {
	    	Connection connection;
	        String url = "jdbc:oracle:thin:@" + dbHost + ":" + dbPost + ":" + dbSid;
	        Class.forName(driverName);
	        connection = DriverManager.getConnection(url, getDBLogin(), getDBPassword());
	        System.out.println("connecting: " + url);
	        if(connection.equals(null))
	    	    return null;		
	        else
	    	    return connection;		
	    } catch (ClassNotFoundException e) {
	        System.out.println("ClassNotFoundException");
	    } catch (SQLException e) {
	        System.out.println("SQLException\n" + e.getMessage());
	    }
	    return null;
	}

	private String getQualifier ( String line ) throws Exception {
		int start = line.lastIndexOf("http://");
		start+=7;
		int end = line.length();
		char cLine[] = new char[end - start]; 
		line.getChars(start, end, cLine, 0);
		//System.out.println("Qualifier: " + String.valueOf(cLine));
		return String.valueOf(cLine);
	}
	
	private void appendStringToFile ( String filename, String msg ) throws Exception {
		try {
    	    File file = new File(filename);
    	    FileWriter writer = new FileWriter(file, true);
    	    writer.write(msg);
    	    writer.flush();
    	    writer.close();
    	} catch (Exception e) {
	    	System.out.println(e.getMessage());
    	}
    }
	
	private void println ( String msg ) throws Exception {
		try {
    	    File file = new File(LOGFILENAME);
    	    FileWriter writer = new FileWriter(file, true);
    	    System.out.println(msg);
    	    writer.write(msg + "\r\n");
    	    writer.flush();
    	    writer.close();
    	} catch (Exception e) {
	    	System.out.println(e.getMessage());
    	}
    }
}
