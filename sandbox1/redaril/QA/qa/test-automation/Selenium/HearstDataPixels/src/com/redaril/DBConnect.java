package com.redaril;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;

public class DBConnect {
	final private static String driverName = "oracle.jdbc.driver.OracleDriver";
	final private static String dbHost = Config.getProperty("dbHost");
	final private static String dbPost = Config.getProperty("dbPort");
	final private static String dbLogin = Config.getProperty("dbLogin");
	final private static String dbPswd = Config.getProperty("dbPswd");
	final private static String dbSid = Config.getProperty("dbSid");
    private String qualifierURLPrefix = "http://p.raasnet.com:8080/partners/universal/in?pid=9&ndl=http%3A//";
    
	DBConnect () {
		
	}
	
	public String evalSelect ( String query ) throws Exception {
		Connection dbConn = connect();
		if ( dbConn == null  ) {
	        System.out.println("Connection FAILED");
	        System.exit(1);
		}
        System.out.println("Connection OK");
        
        try {
//TODO sqlQuery to get category for current data owner
        	String sqlQuery = query;
        	String res = "";
	        Statement stmt = dbConn.createStatement();
	        ResultSet rset = stmt.executeQuery(sqlQuery);
	        while (rset.next()) {
	        	try {res = res + rset.getString(1) + "	";} catch (Exception e) {}
	        	try {res = res + rset.getString(2) + "	";} catch (Exception e) {}
	        	try {res = res + rset.getString(3) + "	";} catch (Exception e) {}
	        	try {res = res + rset.getString(4) + "	";} catch (Exception e) {}
	        	try {res = res + rset.getString(5) + "	";} catch (Exception e) {}
	        	try {res = res + rset.getString(6) + "	";} catch (Exception e) {}
	        	try {res = res + rset.getString(7) + "	";} catch (Exception e) {}
	        	try {res = res + rset.getString(8) + "	";} catch (Exception e) {}
	        	try {res = res + rset.getString(9) + "	";} catch (Exception e) {}
	        	try {res = res + rset.getString(10) + "	";} catch (Exception e) {}
	        	try {res = res + rset.getString(11) + "	";} catch (Exception e) {}
	        	try {res = res + rset.getString(12) + "	";} catch (Exception e) {}
	        }
	        
		    dbConn.close();
	        return res;
	    } catch (Exception e) {
	        System.out.println("Exception:\r\n" + e.getMessage());
		    dbConn.close();
		    return e.getMessage();
	    }
	}
	
	
	public String getQualifierForDataOwner ( String dataowner ) throws Exception {
		Connection dbConn = connect();
		if ( dbConn == null  ) {
	        System.out.println("Connection FAILED");
	        System.exit(1);
		}
        System.out.println("Connection OK");
        
        try {
//TODO sqlQuery to get category for current data owner
        	String sqlQuery = "select REGEX from regex_qualifier where external_id='34361';";
	        Statement stmt = dbConn.createStatement();
	        ResultSet rset = stmt.executeQuery(sqlQuery);
	        //System.out.println(rset.getString(1));
		    dbConn.close();
	        return rset.getString(1);
	    } catch (Exception e) {
	        System.out.println("Exception:\r\n" + e.getMessage());
		    dbConn.close();
		    return e.getMessage();
	    }
	}
	
	public boolean getNewQualifiers ( String clusterName, String date ) throws Exception {
		String filename= "lastTimeAddedQualifiers" + "_" + date + ".txt";

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

        /*
         * 
SELECT nt_qualifier.modified_date_time, nt_qualifier.qualifier_id, nt_qualifier.base_url, nt_qualifier.interest_id, nc_interest.name, nc_interest.parent_id from NT_QUALIFIER join nc_interest on nc_interest.interest_id = nt_qualifier.interest_id
WHERE nt_qualifier.modified_date_time >=  TO_DATE('08.12.11','dd.mm.yy') ORDER BY  nt_qualifier.modified_date_time desc;
--
--WHERE nt_qualifier.modified_date_time >=  TO_DATE('08.12.11','dd.mm.yy') and  nt_qualifier.qualifier_id=1954245 ORDER BY  nt_qualifier.modified_date_time desc;
--WHERE nt_qualifier.modified_date_time >=  TO_DATE('08.12.11','dd.mm.yy') and nt_qualifier.interest_id = 9667 ORDER BY  nt_qualifier.modified_date_time desc;

SELECT nt_qualifier.base_url, nc_interest.name, nt_qualifier.qualifier_id, nt_qualifier.interest_id from NT_QUALIFIER join nc_interest on nc_interest.interest_id = nt_qualifier.interest_id
WHERE nt_qualifier.modified_date_time >=  TO_DATE('08.12.11','dd.mm.yy') ORDER BY  nt_qualifier.modified_date_time desc;


         */
        
        try {
	        String sqlQuery = "SELECT nt_qualifier.base_url, nc_interest.name, nt_qualifier.qualifier_id, nt_qualifier.interest_id from NT_QUALIFIER join nc_interest on nc_interest.interest_id = nt_qualifier.interest_id WHERE nt_qualifier.modified_date_time >=  TO_DATE('" + date + "','dd.mm.yy') ORDER BY  nt_qualifier.modified_date_time desc";
	        Statement stmt = dbConn.createStatement();
	        ResultSet rset = stmt.executeQuery(sqlQuery);
	        while (rset.next()) {
	        	System.out.println(rset.getString(1) + "	" + rset.getString(2));
	        	appendStringToFile(filename, "\r\nQualifier URL: " + qualifierURLPrefix + getQualifier(rset.getString(1)) + "\r\n" + 
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
	
	private Connection connect() {
	    try {
	    	Connection connection;
	        String url = "jdbc:oracle:thin:@" + dbHost + ":" + dbPost + ":" + dbSid;
	        Class.forName(driverName);
	        connection = DriverManager.getConnection(url, dbLogin, dbPswd);
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
}
