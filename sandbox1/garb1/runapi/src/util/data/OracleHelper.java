package util.data;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.sql.PreparedStatement;
import oracle.jdbc.driver.OracleTypes;
import util.CommonHelper;

public class OracleHelper {
	private static OracleHelper instance = new OracleHelper();

	public static OracleHelper getInstance() {
		return instance;
	}

	private OracleHelper() {

	}

	public boolean executeStoredProcedure(HashMap h) {
		return true;
	}

	public String getValue(String table, String condition) {
		return null;
	}

	public boolean setValue(String table, String condition, String newValue) {
		return true;
	}

	public boolean delete(String table, String condition) {
		return true;
	}

	public int corp_addCorporation(String login) {
		int corp_id = 0;
		try {
			String procString = "begin CORPORATION.add(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); end;";

			CallableStatement stmt = CommonHelper.getInstance()
					.getCorpDbConnection().prepareCall(procString);
			stmt.setInt(1, 1); //administrator_id - any value
			stmt.setInt(2, 1); //interface_id - any value
			stmt.setString(3, "192.168.11.11"); //ip - any value
			stmt.setString(4, "ccc" + Calendar.getInstance().getTimeInMillis()); //corporation_name
			stmt.setString(5, "$"); //default_melody_id - KPV
			stmt.setString(6, "10:00:00"); //default_melody_start_time
			stmt.setString(7, "18:00:00"); //default_melody_stop_time
			stmt.setString(8, login); //login
			stmt.setString(9, "d41d8cd98f00b204e9800998ecf8427e");//password
			stmt.setString(10, corp_getMSISDN(71339999992l));//msisdn
			stmt.setString(11, "aaaN");//name
			stmt.setString(12, "dd@jj.ru");//e-mail
			stmt.registerOutParameter(13, OracleTypes.NUMBER); //o_result
			stmt.registerOutParameter(14, OracleTypes.NUMBER); //o_corporation_id
			
			stmt.execute();
			corp_id = stmt.getInt(14);
			stmt.close();
		} catch (Exception e) {
			System.err.println("can't execute corp_addCorporation stored procedure: " + e);
		}
		return corp_id;
	}
	
	public int corp_blockCorporation(int corpID) {
		int result = 0;
		try {
			String procString = "begin CORPORATION.block(?, ?, ?, ?, ?); end;";

			CallableStatement stmt = CommonHelper.getInstance()
					.getCorpDbConnection().prepareCall(procString);
			stmt.setInt(1, corpID); //corporation_id
			stmt.setInt(2, 1); //administrator_id - any value
			stmt.setInt(3, 1); //interface_i - any value
			stmt.setString(4, "192.168.11.11"); //ip - any value
			stmt.registerOutParameter(5, OracleTypes.NUMBER); //o_result
					
			stmt.execute();
			result = stmt.getInt(5);
			stmt.close();
		} catch (Exception e) {
			System.err.println("can't execute corp_blockCorporation stored procedure: " + e);
		}
		return result;
	}
	
	public int corp_delCorporation(int corpID) {
		int result = 0;
		try {
			String procString = "begin CORPORATION.delete(?, ?, ?, ?, ?); end;";

			CallableStatement stmt = CommonHelper.getInstance()
					.getCorpDbConnection().prepareCall(procString);
			stmt.setInt(1, corpID); //corporation_id
			stmt.setInt(2, 1); //administrator_id - any value
			stmt.setInt(3, 1); //interface_i - any value
			stmt.setString(4, "192.168.11.11"); //ip - any value
			stmt.registerOutParameter(5, OracleTypes.NUMBER); //o_result
					
			stmt.execute();
			result = stmt.getInt(5);
			stmt.close();
		} catch (Exception e) {
			System.err.println("can't execute corp_delCorporation stored procedure: " + e);
		}
		return result;
	}

	public int corp_addRepresentative(int corpID, String login) {
		int admin_id = 0;
		try {
			String procString = "begin CORP_REPRESENTATIVE.add(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); end;";

			CallableStatement stmt = CommonHelper.getInstance()
					.getCorpDbConnection().prepareCall(procString);
			stmt.setInt(1, corpID); //corporation_id
			stmt.setInt(2, 1); //administrator_id - any value
			stmt.setInt(3, 1); //interface_id - any value
			stmt.setString(4, "192.168.11.11"); //ip - any value
			stmt.setString(5, login); //login
			stmt.setString(6, "d41d8cd98f00b204e9800998ecf8427e"); //password - empty password hash
			stmt.setString(7, corp_getMSISDN(73329999992l)); //msisdn
			stmt.setString(8, "1834"); //name - any value
			stmt.setString(9, "hh@dd.ru"); //e-mail - any value
			stmt.registerOutParameter(10, OracleTypes.NUMBER); //o_result
			stmt.registerOutParameter(11, OracleTypes.NUMBER); //o_representative_id
			
			stmt.execute();
			admin_id = stmt.getInt(11);
			stmt.close();
    	} catch (Exception e) {
			System.err.println("can't execute add_representative stored procedure: " + e);
		}
		return admin_id;
	}

	public int corp_getMaxIndex(String table, String field) {
		int index = 0;
		try {
			String sql = "select max(" + field + ") from " + table;
			PreparedStatement stmt = CommonHelper.getInstance()
					.getCorpDbConnection().prepareStatement(sql);
			stmt.executeQuery();
			ResultSet rs = stmt.getResultSet();
			rs.next();
			index = rs.getInt(1); //result - is a number
			stmt.close();
		} catch (Exception e) {
			System.err.println("can't get max index: " + e);
		}

		return index;
	}
	
	public void corp_moveMSISDNsToZERO(java.util.ArrayList<Object> msisdns) {
		//cycle for each msisdn to change his state to zero
	}

	public boolean corp_isExist(String table, String field, String value) {
		boolean result = false;
		try {
			String sql = "select count(*) from " + table + " where " + field + "=" + value;
			PreparedStatement stmt = CommonHelper.getInstance()
					.getCorpDbConnection().prepareStatement(sql);
			stmt.executeQuery();
			ResultSet rs = stmt.getResultSet();
			rs.next();
			int index = rs.getInt(1); //result - is a number
			if (index != 0)
				result = true;
			stmt.close();
		} catch (Exception e) {
			System.err.println("can't check corp_isExist: " + e);
		}
		
		return result;
	}
	
	public String corp_getMSISDN(long msisdn) {
		long new_msisdn = msisdn;
		while (corp_isExist("corp_representatives", "msisdn", Long.toString(new_msisdn)))
			new_msisdn += 1;
		
		return Long.toString(new_msisdn);
	}
	
	public String corp_getExistingLogin() {
		String result = null;
		try {
			String sql = "select * from corp_representatives";
			PreparedStatement stmt = CommonHelper.getInstance()
					.getCorpDbConnection().prepareStatement(sql);
			stmt.executeQuery();
			ResultSet rs = stmt.getResultSet();
			rs.next();
			result = rs.getString(3);
			stmt.close();
		} catch (Exception e) {
			System.err.println("can't check corp_isExist: " + e);
		}
		
		return result;
	}
	
	public String corp_getExistingMSISDN() {
		String result = null;
		try {
			String sql = "select * from corp_representatives";
			PreparedStatement stmt = CommonHelper.getInstance()
					.getCorpDbConnection().prepareStatement(sql);
			stmt.executeQuery();
			ResultSet rs = stmt.getResultSet();
			rs.next();
			result = rs.getString(6);
			stmt.close();
		} catch (Exception e) {
			System.err.println("can't check corp_isExist: " + e);
		}
		
		return result;
	}
	
	public String corp_getMSISDN_By_Login(String login) {
		String result = null;
		
		try {
			String sql = "select msisdn from corp_representatives where login=" + "'" + login + "'";
			PreparedStatement stmt = CommonHelper.getInstance()
					.getCorpDbConnection().prepareStatement(sql);
			stmt.executeQuery();
			ResultSet rs = stmt.getResultSet();
			rs.next();
			result = rs.getString(1);
			stmt.close();
		} catch (Exception e) {
			System.err.println("can't get MSISDN by ID: " + e);
		}
		
		return result;
	}
	
	/*
	 * String sql = "select * from corp"; PreparedStatement prpStmt =
	 * conn.prepareStatement(sql); prpStmt.executeQuery(); ResultSet rs =
	 * prpStmt.getResultSet(); //stmt.registerOutParameter(1,
	 * OracleTypes.CURSOR); // print the results while (rs.next()) {
	 * System.out.println(rs.getInt(1) + "\t" + rs.getString(2) + "\t" +
	 * rs.getInt(3)); } prpStmt.close();
	 */
}
