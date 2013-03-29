package com.statistics.timestatistics.dbcontroller;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;


public class DBConnection extends SQLiteOpenHelper{
	public static final String TAG = DBConnection.class.getSimpleName();
	
	//Names and attributes of table main
	public static String TABLE_NAME = "statistics";
	public static final String _ID = "id";
	public static final String STAT_NAME = "name";
	public static final String DATE = "date";

	private Cursor c;
	
  	//Name and Version of Database
  	private static final String DATABASE_NAME = "costs.db";
  	private static final int DATABASE_VERSION = 1;

  	// Database creation sql statement
  	private static StringBuilder DATABASE_CREATE = new StringBuilder();

  public DBConnection(Context context) {
	  super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  DATABASE_CREATE.append("create table "
	  			+ TABLE_NAME + "(" + _ID
	  			+ " integer primary key autoincrement, " 
	  			+ STAT_NAME + " text, "
	  			+ DATE + " date default CURRENT_DATE);");
  }


  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE.toString());
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(DBConnection.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    onCreate(db);
  }
  
  public void insert(String name){
	  long rowId = -1;
	  
	  try{
	  SQLiteDatabase db = getWritableDatabase();
	  
	  ContentValues values = new ContentValues();
	  values.put(STAT_NAME, name);
	  
	  rowId = db.insert(TABLE_NAME, null, values);
	  }catch (SQLException se){
		  Log.e(TAG, "insert()", se);
	  }finally{
		  Log.d(TAG, "insert(): rowID=" +rowId);
	  }
  }
  
  public void createNewTable(String name, HashMap<String, String> attributes){
	  TABLE_NAME = name;
	  DATABASE_CREATE = new StringBuilder();
	  
	  DATABASE_CREATE.append("create table "
	  			+ TABLE_NAME + "(" + _ID
	  			+ " integer primary key autoincrement ");
	  
	  for(String attribute : attributes.keySet()){
		  DATABASE_CREATE.append(", ");
		  DATABASE_CREATE.append(attribute);
		  DATABASE_CREATE.append(" ");
		  DATABASE_CREATE.append(attributes.get(attribute).toString());
	  }
	  
	  DATABASE_CREATE.append(", time text);");
	  
	  onCreate(getWritableDatabase());
  }

  private Cursor selectAllStatNames(){
	  SQLiteDatabase db = getReadableDatabase();
	  
	  Cursor c = db.rawQuery("SELECT name FROM " + TABLE_NAME, null);
	  return c; 
  }

  public Cursor getAllStatNames() {
	  c = selectAllStatNames();
	  return c;
  }
  
}
