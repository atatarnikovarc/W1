package app.database;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import app.util.Config;

public class Data {
	private static Data instance = new Data();
	private Connection connection;

	private Data() {
		String url = "jdbc:mysql://" + Config.getInstance().getDbHost() + ":"
				+ Config.getInstance().getDbPort() + "/"
				+ Config.getInstance().getDbName();
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(url, Config.getInstance()
					.getDbUser(), Config.getInstance().getDbPassword());

			executeLoad();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static Data getInstance() {
		return instance;
	}

	public void executeLoad() {
		this.deleteAll();
		this.loadData();
	}

	private void loadData() {
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(Config.getInstance()
					.getSqlsFile());
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			Statement stmt;
			stmt = connection.createStatement();
			
			while ((strLine = br.readLine()) != null) 
				stmt.executeUpdate(strLine);
				
			stmt.close();
			// Close the input stream
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void deleteAll() {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("DELETE FROM Luggage");

			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void printLuggageById(String id) {
		Statement stmt;
		try {
			stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM Luggage where id = " + id
							+ " and isProcessed = 0");
			System.out.println("Luggage info: ");

			if (!rs.next()) {
				System.out.println("No luggage info available");
			} else {
				System.out.println("   " + "ID" + "  " + "HAND" + "  "
						+ "NON-HAND");
				do {
					System.out.println(rs.getRow() + ". " + rs.getString("id")
							+ "    " + rs.getString("hand_luggage") + " \t"
							+ rs.getString("luggage"));
					makeLuggageProcessed(rs);
				} while (rs.next());
			}

			System.out.println();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void makeLuggageProcessed(ResultSet rs) {
		try {
			rs.updateInt("isProcessed", 1);
			rs.updateRow();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
