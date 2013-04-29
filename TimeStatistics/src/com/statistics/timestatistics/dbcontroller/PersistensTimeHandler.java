package com.statistics.timestatistics.dbcontroller;

import com.statistics.timestatistics.definition.ClockState;
import com.statistics.timestatistics.definition.NoClockStateException;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteException;

public class PersistensTimeHandler {

	private DBConnection dbc;

	private String tableName;
	
	public PersistensTimeHandler(DBConnection dbConnection, String table){
		this.tableName = table;
		this.dbc = dbConnection;
	}
	
	public long getSavedTime(){
		Long time = 0L;
		try{
		Cursor result = dbc.getReadableDatabase().rawQuery("select * from "+tableName, null);
		result.moveToFirst();
		time = Long.parseLong(result.getString(result.getColumnCount()-1));
		result.close();
		}
		catch(SQLiteException sqle ){
			System.out.println("No base saved");
			return time;
		}
		catch(CursorIndexOutOfBoundsException cioobe){
			System.out.println("No base saved");
			return time;
		}
		
		return time;
	}
	
	public ClockState getSettedStateOfClock(){
		ClockState state;
		try{
			Cursor result = dbc.getReadableDatabase().rawQuery("select * from timesaving789", null);
			result.moveToFirst();
			try{
				//Find the state one column before the last
				state = new ClockState(result.getString(result.getColumnCount()-2));
			}catch(NoClockStateException ncse){
				state = new ClockState(Integer.parseInt(result.getString(result.getColumnCount()-2)));
			}finally{
				result.close();
			}
		}
		catch(SQLiteException sqle){
			return new ClockState(0);
		}
		return state;
	}
	
	
}
