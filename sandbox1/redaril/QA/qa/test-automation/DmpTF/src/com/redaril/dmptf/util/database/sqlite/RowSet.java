package com.redaril.dmptf.util.database.sqlite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.fail;

public class RowSet {
    private static final Logger LOG = LoggerFactory.getLogger(RowSet.class);
    private List<HashMap<String, String>> rowSet;
    private HashMap<String, String> row;
    private int rowPosition;

    public RowSet() {
        row = new HashMap<String, String>();
        rowSet = new ArrayList<HashMap<String, String>>();
        rowPosition = -1;
    }

    public void parseResultSetRow(ResultSet rs) {
        try {
            while (rs.next()) {
                row = getResultSetRow(rs);
                rowSet.add(row);
            }
        } catch (SQLException e) {
            LOG.error("Can't parse result set into HashMap. " + e.getMessage());
            fail("Can't parse result set into HashMap.");

        }
    }

    private HashMap<String, String> getResultSetRow(ResultSet rs) {
        HashMap<String, String> rowLocal = new HashMap<String, String>();
        try {
            int i = 1;
            int columnCount = rs.getMetaData().getColumnCount();
            while (i <= columnCount) {
                rowLocal.put(rs.getMetaData().getColumnName(i), rs.getString(i));
                i++;
            }
        } catch (SQLException e) {
            LOG.error("Can't parse result set into HashMap. " + e.getMessage());
            fail("Can't parse result set into HashMap.");
        }
        return rowLocal;
    }

    public boolean next() {
        rowPosition++;
        return rowPosition < rowSet.size();
    }

//	public String getRow ( int columnIndex ) {
//		row = rowSet.get(rowPosition);
//		return row.get(columnIndex);
//	}

    public String getRow(String columnLabel) {
        row = rowSet.get(rowPosition);
        return row.get(columnLabel);
    }

//	public int size () {
//		row = rowSet.get(rowPosition);
//		return row.size();
//	}

}
