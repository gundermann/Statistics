package com.statistics.timestatistics;

import java.util.ArrayList;
import java.util.List;

import com.common.StringModifier;
import com.statistics.timestatistics.dbcontroller.DBConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class Acquisition extends Activity{
	
	Chronometer clock;
	String time = null;
	boolean reset = true;
	int counter;
	private AlertDialog dialog = null;
	private boolean round = true;
	private boolean dirty = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(isAcquisition()){
			setAcquisition();
		}
	}
	@SuppressWarnings("deprecation")
	public void setAcquisition(){
		setContentView(R.layout.acquisition);
		clock = (Chronometer) findViewById(R.id.clock);
		
		if(loadTime() != 0L){
			clock.setBase(SystemClock.elapsedRealtime()-loadTime());
			clock.start();
			clock.stop();
			loadValues();
			reset = false;
			setClockStarted(wasClockStarted());
		}
		else{
//			clock.setBase(SystemClock.elapsedRealtime());
		}
		
		updateLayout(this.getCurrentFocus());
		final String currentStatistic = getCurrentTable();
		List<String> attributes = loadAttributes(currentStatistic);
		prepareLayout(attributes);
		
		Display display = getWindowManager().getDefaultDisplay();
		
		ImageButton btClear = (ImageButton) findViewById(R.id.btClearFormular);
		btClear.setLayoutParams(new LinearLayout.LayoutParams(display.getWidth()/2, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		btClear.setOnClickListener(new AdapterView.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				clearAllElements();
			}

		});
		
		final Button btClock = (Button) findViewById(R.id.clockButton);
		btClock.setOnClickListener(new AdapterView.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if ( isClockStarted() ){
					setClockStarted(false);
					time = clock.getText().toString();
				}
				else{
//					clock.setBase(SystemClock.elapsedRealtime());
					setClockStarted(true);
					saveTimeAndValues();
				}

			}

		});
		
		
		ImageButton btApply = (ImageButton) findViewById(R.id.btApplyNewValue);
		btApply.setLayoutParams(new LinearLayout.LayoutParams(display.getWidth()/2, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		btApply.setOnClickListener(new AdapterView.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if ( isClockStarted() ){
					setClockStarted(false);
				}

				DBConnection db = new DBConnection(getApplicationContext());
				
				
				db.insertValueIntoStatistic(currentStatistic, getValuesToSave(), clock.getText().toString());
				db.close();

//				clock.setBase(SystemClock.elapsedRealtime());
				reset = true;
				clock.setText("00:00");
				clearAllElements();
			}
		});
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
	
	public Button getBtClock(){
		return (Button) findViewById(R.id.clockButton);
	}
	
	private void setClockStarted(boolean started) {
		if(started){
			clock.setBase(SystemClock.elapsedRealtime());
			if(reset || !round )
				clock.setBase(SystemClock.elapsedRealtime()-loadTime());
			clock.start();
			getBtClock().setText("Stop");
			reset = false;
			dirty = true;
		}
		else{
			clock.stop();
			getBtClock().setText("Start");
		}
	}
	
	@Override
	public void onBackPressed(){
		if ( dialog != null){
			dialog.cancel();
		}else{
		DBConnection dbc = new DBConnection(getApplicationContext());
		dbc.discardSaving();
		dbc.close();
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
		if (dirty ){
		List<String> attributes = new ArrayList<String>();
		attributes.addAll(loadAttributes(getCurrentTable()));
		attributes.add("started");
		
		
		List<String> values = new ArrayList<String>();
		values.addAll(getValuesToSave());
		values.add(String.valueOf(isClockStarted()));
		
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
		dbc.insertValueIntoStatistic("timesaving789", values, String.valueOf(clock.getBase()));
		dbc.close();
		}
	}
	
	
	
	private void loadValues(){
		DBConnection db = new DBConnection(this.getApplicationContext());
		Cursor result = db.getReadableDatabase().rawQuery("select * from timesaving789", null);
		
		result.moveToFirst();
		if ( result.getColumnCount() < 2){
		LinearLayout linLay = (LinearLayout) findViewById(R.id.acquisitionLayout);

		int elementIndex = 3;
		for(int i = 1; i < result.getColumnCount()-1; i++){
			((EditText) linLay.getChildAt(elementIndex)).setText(result.getString(i));
			elementIndex = elementIndex + 2;
		}
		}
		result.close();
		db.close();
	}
	
	private boolean wasClockStarted(){
		DBConnection dbc = new DBConnection(getApplicationContext());

		Cursor result = dbc.getReadableDatabase().rawQuery("select * from timesaving789", null);
		result.moveToFirst();
		boolean wasStarted = Boolean.parseBoolean(result.getString(1));
		
		result.close();
		dbc.getWritableDatabase().execSQL("DROP TABLE IF EXISTS timesaving789");
		dbc.close();
		
		return wasStarted;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if( isAcquisition() )
			saveTimeAndValues();
		
		super.onConfigurationChanged(newConfig);
		
		if( isAcquisition() )
			setAcquisition();
	}
	
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

	private boolean isClockStarted() {
		if( ((Button) findViewById(R.id.clockButton)).getText().toString().equals("Start") )
			return false;
		else
			return true;
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
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

    	Resources resources = getApplicationContext().getResources();
    	Display display = getWindowManager().getDefaultDisplay();
    	DisplayMetrics metrics = resources.getDisplayMetrics();
    	
    	lp.setMargins(Math.round((display.getWidth()/2 * (metrics.densityDpi/160f))/2) , 0, 0, 0);
    	lp.width = Math.round(((66 * (metrics.densityDpi/160f))));
    	lp.height = Math.round(((66 * (metrics.densityDpi/160f))));
    	btApply.setLayoutParams(lp);
    	btClear.setLayoutParams(lp);
	}
}
