package com.statistics.timestatistics.dbcontroller;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;

public class TimeLoader {

	private Long base;
	
	private boolean started;
	
	private DBConnection dbc;

	private String tableName;
	
	public TimeLoader(DBConnection dbConnection, String table){
		this.tableName = table;
		this.dbc = dbConnection;
		loadTime();
	}
	
	private void loadTime(){
		try{
		Cursor result = dbc.getReadableDatabase().rawQuery("select * from "+tableName, null);
		result.moveToFirst();
		setBase(Long.parseLong(result.getString(result.getColumnCount()-1)));
		result.close();
		}
		catch(SQLiteException sqle){
			System.out.println("No base saved");
		}finally{
			dbc.close();
		}
		
	}
	
	public Long getBase() {
		return base;
	}

	public void setBase(Long base) {
		this.base = base;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}
	
	
}
