package com.statistics.timestatistics;

import java.util.ArrayList;
import java.util.List;

import com.common.StringModifier;
import com.statistics.timestatistics.business.StatisticClock;
import com.statistics.timestatistics.dbcontroller.DBConnection;
import com.statistics.timestatistics.dbcontroller.PersistensTimeHandler;
import com.statistics.timestatistics.definition.NoClockStateException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Acquisition extends Activity{
	
	StatisticClock clock;
	String time = null;
	boolean reset = true;
	int counter;
	protected AlertDialog dialog = null;
	private String savingTable = "timesaving789";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(isAcquisition()){
			setAcquisition();
		}
	}
	public void setAcquisition(){
		setContentView(R.layout.acquisition);
		clock = new StatisticClock((Chronometer) findViewById(R.id.clock));
		
		updateLayout(this.getCurrentFocus());
		
		final String currentStatistic = getCurrentTable();
		List<String> attributes = loadAttributes(currentStatistic);
		prepareLayout(attributes);

		updateClock();
		
		getBtClear().setOnClickListener(new AdapterView.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				clearAllElements();
			}

		});
		
		getBtClockStartStop().setOnClickListener(new AdapterView.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				switch (clock.getState().getStateNumber()) {
				case 1:
					clock.handleStopped();
					getBtClockStartStop().setText("Start");
					break;
				default:
					try {
						clock.handleStart(clock.getState());
						getBtClockStartStop().setText("Stop");
					} catch (NoClockStateException e) {
						e.printStackTrace();
					}
					break;
				}

			}

		});
		
		
		getBtApply().setOnClickListener(new AdapterView.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				DBConnection db = new DBConnection(getApplicationContext());
				
				
				db.insertValueIntoStatistic(currentStatistic, getValuesToSave(), clock.getTime().toString());
				db.close();

				clock.handleStopped();
				clearAllElements();
			}
		});
	}
	
	private void updateClock() {
		DBConnection dbc = new DBConnection(getApplicationContext());
		PersistensTimeHandler pth = new PersistensTimeHandler(dbc, getSavingTable());
		clock.updateClock(pth.getSavedTime(), pth.getSettedStateOfClock());
		dbc.close();
		if( clock.getState().getStateNumber()==1 ){
			try {
				clock.handleStart(clock.getState());
				getBtClockStartStop().setText("Stop");
			} catch (NoClockStateException e) {
				e.printStackTrace();
			}
		}
		else if( clock.getState().getStateNumber() == 2 ){
			clock.showTime();
		}
		discardSavedTime();
	}
	
	private void discardSavedTime() {
		DBConnection dbc = new DBConnection(getApplicationContext());
		try {
			dbc.getWritableDatabase().execSQL("drop table "+getSavingTable());		
		} catch (SQLiteException sqle) {
			System.out.println("No Time saved");
		}finally{
			dbc.close();
		}
	}
	private String getSavingTable() {
		return savingTable ;
	}
	protected void setDialog(AlertDialog alertDialog) {
		dialog  = alertDialog;
	}
	
	private String getCurrentTable() {
		return StringModifier.deleteSpaces(loadSelectedTable());
	}
	public List<String> getValuesToSave(){
		List<String> valuesToSave = new ArrayList<String>();
		LinearLayout linLay = (LinearLayout) findViewById(R.id.acquisitionLayout);
		for(int i = 0; i < linLay.getChildCount(); i++){
			if( linLay.getChildAt(i) instanceof EditText){
				valuesToSave.add(((EditText) linLay.getChildAt(i)).getText().toString());
			}
		}

		return valuesToSave;
	}
	
	public Button getBtClockStartStop(){
		return (Button) findViewById(R.id.clockButton);
	}
	
	private ImageButton getBtClear(){
		return (ImageButton) findViewById(R.id.btClearFormular);
	}
	
	private ImageButton getBtApply(){
		return (ImageButton) findViewById(R.id.btApplyNewValue);
	}
	
	public PersistensTimeHandler getTimeHandler(){
		DBConnection dbc =  new DBConnection(getApplicationContext());
		PersistensTimeHandler persistensTimeHandler = new PersistensTimeHandler(dbc, getCurrentTable());
		dbc.close();
		return persistensTimeHandler;
	}
	
	
	@Override
	public void onBackPressed(){
		if ( dialog != null){
			dialog.cancel();
		}else{
		DBConnection dbc = new DBConnection(getApplicationContext());
		dbc.discardSaving();
		dbc.close();
		discardSavedTime();
		System.exit(0);
		}
	} 

	protected boolean isAcquisition() {
		try{
		DBConnection db = new DBConnection(getApplicationContext());
		Cursor result = db.getSavedInstance();
		result.moveToFirst();
		boolean isAcquisition = Boolean.parseBoolean(result.getString(1));
		
		result.close();
		db.close();
		return isAcquisition;
		}
		catch(SQLiteException slqe){
			return false;
		}
	}
	
	/**
	 * Cleans every cleanable Element (clock and edittexts)
	 */
	private void clearAllElements() {
		LinearLayout linLay = (LinearLayout) findViewById(R.id.acquisitionLayout);
		
		for(int i = 0; i < linLay.getChildCount(); i++){
			if( linLay.getChildAt(i) instanceof EditText ){
				((EditText) linLay.getChildAt(i)).setText("");
			}
			else if ( linLay.getChildAt(i) instanceof Chronometer ){
				((Chronometer) linLay.getChildAt(i)).setText("");
				((Chronometer) linLay.getChildAt(i)).setBase(SystemClock.elapsedRealtime());
				((Chronometer) linLay.getChildAt(i)).stop();
			}
			else if ( linLay.getChildAt(i) instanceof Button){
				((Button) linLay.getChildAt(i)).setText("Start");
			}
		}
		
	}
	
	private void saveTimeAndValues(){
		List<String> attributes = new ArrayList<String>();
		attributes.addAll(loadAttributes(getCurrentTable()));
		attributes.add("state");
		
		
		List<String> values = new ArrayList<String>();
		values.addAll(getValuesToSave());
		values.add(String.valueOf(clock.getState().getStateNumber()));
		
		DBConnection dbc = new DBConnection(this.getApplicationContext());
		try{
			dbc.getWritableDatabase().execSQL("drop table timesaving789");
		}
		catch(SQLiteException sqle){
			System.out.println("Table doesn't exists");
			
		}
		dbc.createNewTable("timesaving789", attributes);
		dbc.close();
		dbc = new DBConnection(this.getApplicationContext());
		if ( clock.getState().getStateNumber() == 1)
			dbc.insertValueIntoStatistic("timesaving789", values, String.valueOf(clock.getTime()));
		else
			dbc.insertValueIntoStatistic("timesaving789", values, String.valueOf(clock.getLastTime()));
		dbc.close();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if( isAcquisition() )
			saveTimeAndValues();
		
		super.onConfigurationChanged(newConfig);
		
		if( isAcquisition() ){
			setAcquisition();
			handleClock();
		}
	}
	
	private void handleClock() {
	}
	/**
	 * prepares the layout for the acquisition of new values for current statistic
	 * creates for every attribute a textview and an edittext
	 * @param attributes
	 */
	private void prepareLayout(List<String> attributes) {
		LinearLayout linLay = (LinearLayout) findViewById(R.id.acquisitionLayout);
		
		for(String attribute : attributes){
			android.widget.FrameLayout.LayoutParams layPara = new android.widget.FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			TextView tv = new TextView(getApplicationContext());
			tv.setText(attribute + ":");
			tv.setTextColor(R.color.text);
			tv.setTextSize(25);
			linLay.setLayoutParams(layPara);
			linLay.addView(tv);
			
			EditText et = new EditText(getApplicationContext());
			et.setLayoutParams(layPara);
			linLay.addView(et);
		}
		
		android.widget.FrameLayout.LayoutParams layPara = new android.widget.FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		linLay.setLayoutParams(layPara);
	}

	/**
	 * Loads attributes of current statistic
	 * @param table
	 * @return
	 */
	private List<String> loadAttributes(String table) {
		List<String> list = new ArrayList<String>();
		
		String sql = "select * from " + table;
		
		DBConnection db = new DBConnection(getApplicationContext());
		
		Cursor result = db.getReadableDatabase().rawQuery(sql, null);
		
		//Das erste Attribut ist die Id und das letzte die Zeit
		for(int i = 1; i < result.getColumnCount()-1; i++){
			list.add(result.getColumnName(i).toString());
		}
	
		result.close();
		db.close();
		return list;
	}

	protected String loadSelectedTable() {
		DBConnection db = new DBConnection(getApplicationContext());
		Cursor result = db.getSavedInstance();
		result.moveToFirst();
		String currentStatistic = result.getString(0);
		
		result.close();
		db.close();
		return currentStatistic;
	}

	
	@SuppressWarnings("deprecation")
	private void updateLayout(View view) {
		
		
		ImageButton btApply = (ImageButton) findViewById(R.id.btApplyNewValue);
		ImageButton btClear = (ImageButton) findViewById(R.id.btClearFormular);

    	Display display = getWindowManager().getDefaultDisplay();
    	
    	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(display.getWidth()/2, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
//    	lp.setMargins(Math.round((display.getWidth()/2 * (metrics.densityDpi/160f))/2) , 0, 0, 0);
//    	lp.width = Math.round(((66 * (metrics.densityDpi/160f))));
//    	lp.height = Math.round(((66 * (metrics.densityDpi/160f))));
    	btApply.setLayoutParams(lp);
    	btClear.setLayoutParams(new LinearLayout.LayoutParams(display.getWidth()/2, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
	}
}
