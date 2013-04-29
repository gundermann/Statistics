package com.statistics.timestatistics.dbcontroller;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteException;

public class PersistensValueHandler {
	
	private DBConnection dbc;

	private String tableName;

	public PersistensValueHandler(DBConnection dbc, String savingTable) {
		this.dbc = dbc;
		tableName = savingTable;
	}

	public List<String> readValues() {
		List<String> values = new ArrayList<String>();
		try{
		Cursor result = dbc.getReadableDatabase().rawQuery("select * from " + tableName, null);
		
		result.moveToFirst();
		
		//Last values are clockstate and time
		for( int i = 1 ; i < result.getColumnCount()-2; i++){
			values.add(result.getString(i));
		}

		result.close();
		}
		catch(SQLiteException sqle ){
			System.out.println("No values saved");
			return values;
		}
		catch(CursorIndexOutOfBoundsException cioobe){
			System.out.println("No values saved");
			return values;
		}
			
		return values;
	}

}
