package com.statistics.timestatistics.dbcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.StringModifier;
import com.statistics.timestatistics.business.Statistic;

import android.annotation.SuppressLint;
import android.database.Cursor;

public class PersistensStatisticHandler {

	private DBConnection dbc;

	private StringBuilder sql = new StringBuilder();
	
	private String name;
	
	private Cursor valueCursor;

	public PersistensStatisticHandler(DBConnection dbConnection, String table){
		this.dbc = dbConnection;
		this.name = table;
		
	}
	
	/**
	 * Saves a Statistic into database
	 * @param statistic
	 */
	public void saveStatistic(Statistic statistic){
		dbc.getWritableDatabase().execSQL("drop table " + StringModifier.deleteSpaces(statistic.getName()));
		
		createNewTableForStatistics(statistic);
		
		for(int line = 0; line < statistic.getValueCount(); line++){
		sql = new StringBuilder();
		sql.append("insert into ");
		sql.append(StringModifier.deleteSpaces(statistic.getName()));
		sql.append(" values (");
		sql.append(statistic.getValueAt(line, 0));
		for(int attribute=1; attribute < statistic.getAttributeCount(); attribute++){
			sql.append(", ");
			sql.append(statistic.getValueAt(line, attribute));
		}

		sql.append(")");
		
		dbc.getWritableDatabase().execSQL(sql.toString());
		}
	}
	
	/**
	 * Creates a new statistic-table into database
	 * @param statistic
	 */
	private void createNewTableForStatistics(Statistic statistic) {

		dbc.createNewTable(StringModifier.deleteSpaces(statistic.getName()), statistic.getAttributesWithoutIdAndTime());
		sql = new StringBuilder();
		  
		sql.append("create table "
				+ StringModifier.deleteSpaces(statistic.getName()) + "(ID integer primary key autoincrement ");
		  
		for(String attribute : statistic.getAttributesWithoutIdAndTime()){
		  sql.append(", ");
		  sql.append(attribute);
		  sql.append(" Text");
		}
		  
		sql.append(", time Text);");
		
		dbc.getWritableDatabase().execSQL(sql.toString());
	}

	/**
	 * Loads the saved values of a Statistic into an new Statistic-Object
	 * @return Statistic
	 */
	@SuppressLint("UseSparseArrays")
	public Statistic loadStatistic(){
		List<String> attributes = new ArrayList<String>();
		Map<Integer, List<String>> values = new HashMap<Integer, List<String>>();
		
		sql.delete(0, sql.length()-1);
		sql.append("select * from ");
		sql.append(StringModifier.deleteSpaces(name));
		
		Cursor result = getValueQueryResult();
		result.moveToFirst();
		
		for(int i = 0; i < result.getColumnCount(); i++){
			attributes.add(result.getColumnName(i));
		}
		
		for(int line = 0; line < result.getCount(); line++){
			List<String> valueList = new ArrayList<String>();
			for( int column = 1; column < result.getColumnCount(); column++){
				valueList.add(result.getString(column));
			}
			values.put(Integer.valueOf(result.getString(0)), valueList);
			result.moveToNext();
		}
		
		Statistic statistic = new Statistic(this.name, attributes, values);
		
		result.close();
		return statistic;
	}
	
	public long getSavedTime(){
		return valueCursor.getLong(valueCursor.getColumnCount()-1);
	}
	
	public List<String> getSavedAttributed(){
		List<String> valueList = new ArrayList<String>();
		
		for(int counter = 1; counter < valueCursor.getColumnCount()-1; counter++){
			valueList.add(valueCursor.getString(counter));
		}
		
		return valueList;
	}
	
	private Cursor getValueQueryResult(){
		return dbc.getReadableDatabase().rawQuery(sql.toString(), null);
	}
	
	public boolean isEmpty(){
		return valueCursor.getColumnCount() == 0;
	}
	
	public void nextValue() {
		if(hasNext()){
			valueCursor.moveToNext();
		}
	}
	
	public void prevValue(){
		if(hasPrev()){
			valueCursor.moveToNext();
		}
	}
	
	public boolean hasNext(){
		if(valueCursor.getPosition() != valueCursor.getCount()-1){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean hasPrev(){
		if(valueCursor.getPosition() != 0){
			return true;
		}
		else{
			return false;
		}
	}
}