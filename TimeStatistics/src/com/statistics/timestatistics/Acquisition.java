package com.statistics.timestatistics;

import java.util.ArrayList;
import java.util.List;

import com.common.StringModifier;
import com.statistics.timestatistics.business.Statistic;
import com.statistics.timestatistics.business.ResetableStopwatch;
import com.statistics.timestatistics.dbcontroller.DBConnection;
import com.statistics.timestatistics.dbcontroller.PersistensStatisticHandler;
import com.statistics.timestatistics.dbcontroller.PersistensTimeHandler;
import com.statistics.timestatistics.dbcontroller.PersistensValueHandler;
import com.statistics.timestatistics.definition.NoClockStateException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.Toast;

public class Acquisition extends Activity{
	
	ResetableStopwatch clock;
	String time = null;
	boolean reset = true;
	int counter;
	protected AlertDialog dialog = null;
	private String savingTable = "timesaving789";
	protected Statistic statistic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(isAcquisition()){
			setAcquisition();
		}
	}
	public void setAcquisition(){
		setContentView(R.layout.acquisition);
		clock = new ResetableStopwatch((Chronometer) findViewById(R.id.clock));
		
		updateLayout(this.getCurrentFocus());

		loadStaistic();
		
		List<String> attributes = statistic.getAttributesWithoutIdAndTime();
		prepareLayout(attributes);
		
		updateClock();
		updateValues();
		discardSavedTime();
		
		/**
		 * Clears all Attributevalues and resets clock
		 */
		getBtClear().setOnClickListener(new AdapterView.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				clearAllElements();
				resetClock();
			}

		});
		
		/**
		 * Stops the clock
		 */
		getBtClockStartStop().setOnClickListener(new AdapterView.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				switch (clock.getState().getStateNumber()) {
				case 1:
					stopClock();
					break;
				default:
					startClock();
					break;
				}

			}

		});
		
		/**
		 * Applys the current time and values for current statistic
		 */
		getBtApply().setOnClickListener(new AdapterView.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				DBConnection db = new DBConnection(getApplicationContext());
				final PersistensStatisticHandler psh = new PersistensStatisticHandler(db, loadSelectedTable());
				if(clock.getState().getStateNumber() == 1)
					statistic.addValue(getValuesToSave(), clock.getTime());
				else
					statistic.addValue(getValuesToSave(), clock.getLastTime());
				psh.saveStatistic(statistic);
				db.close();

				resetClock();
				
				clearAllElements();
				
				Context context = getApplicationContext();
				CharSequence text = "Value saved";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}

		});
	}

	private void updateValues() {
		LinearLayout linLay = (LinearLayout) findViewById(R.id.acquisitionLayout);
		
		DBConnection dbc = new DBConnection(getApplicationContext());
		PersistensValueHandler pth = new PersistensValueHandler(dbc, getSavingTable());
		
		if(pth.readValues().size() > 0){
		int valueCounter = 0;
		for(int i = 0; i < linLay.getChildCount(); i++){
			if(linLay.getChildAt(i) instanceof EditText){
				((EditText) linLay.getChildAt(i)).setText(pth.readValues().get(valueCounter).toString());
				valueCounter++;
			}
		}
		}
		dbc.close();
	}

	private void stopClock() {
		clock.handleStopped();
		getBtClockStartStop().setText("Start");
	}
	
	private void resetClock() {
		clock.handleReset();
		getBtClockStartStop().setText("Start");		
	}
	
	private void startClock() {
		try {
			clock.handleStart(clock.getState());
			getBtClockStartStop().setText("Stop");	
		} catch (NoClockStateException e) {
			e.printStackTrace();
		}
	}
	
	protected void loadStaistic() {
		DBConnection db = new DBConnection(getApplicationContext());
		final PersistensStatisticHandler psh = new PersistensStatisticHandler(db, loadSelectedTable());
		this.statistic = psh.loadStatistic();
		db.close();
	}
	
	protected void updateClock() {
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
//		discardSavedTime();
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
	
	private Button getBtClockStartStop(){
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
			dialog = null;
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
	
	@SuppressWarnings("deprecation")
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
		}
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

	
	private void updateLayout(View view) {
    	Display display = getWindowManager().getDefaultDisplay();
    	
    	@SuppressWarnings("deprecation")
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(display.getWidth()/2, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

    	getBtApply().setLayoutParams(lp);
    	getBtClear().setLayoutParams(lp);
	}
}
