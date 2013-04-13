package com.statistics.timestatistics;

import java.util.ArrayList;
import java.util.List;

import com.common.StringModifier;
import com.statistics.timestatistics.dbcontroller.DBConnection;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelectedStatisticView extends Acquisition {
	
	int counter = 0;
 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!isAcquisition()){
		setContentView(R.layout.table_view);
		updateLayout(this.getCurrentFocus());
		
		final String currentStatistic = StringModifier.deleteSpaces(loadSelectedTable());
		List<String> attributes = loadAttributes(currentStatistic);
		prepareLayout(attributes);
		
		
		StringBuilder sql = new StringBuilder();
		sql.append("select * from " + currentStatistic);
		
		DBConnection db = new DBConnection(getApplicationContext());
		final Cursor result = db.getReadableDatabase().rawQuery(sql.toString(), null);
		
		updateElements(result);
		
		getBtPrev().setOnClickListener(new AdapterView.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				counter--;
				updateElements(result);
			}
		});
		
		getBtNext().setOnClickListener(new AdapterView.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				counter++;
				updateElements(result);
			}
		});
		
		getBtNewValue().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DBConnection dbc = new DBConnection(getApplicationContext());
				dbc.saveStateInDatabase(currentStatistic, true);
				dbc.close();
//				Intent in = new Intent(SelectedStatisticView.this, Acquisition.class);
//		        startActivity(in);
//		        closeContextMenu();
				setAcquisition();
			}
		});
		
		db.close();
		}
	}
	

	private void updateElements(Cursor result){
		result.moveToPosition(counter);
		
		LinearLayout linLay = (LinearLayout) findViewById(R.id.tableViewSpecificAttributes);

		if( result.getCount() == 0){
			TextView tvNoValues = new TextView(getApplicationContext());
			tvNoValues.setText("No Values");
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			tvNoValues.setLayoutParams(lp);
			tvNoValues.setGravity(Gravity.CENTER);
		}
		else{
		int elementIndex = 1;
		for(int i = 1; i < result.getColumnCount()-1; i++){
			((EditText) linLay.getChildAt(elementIndex)).setText(result.getString(i));
		}
		
		Chronometer clock = (Chronometer) findViewById(R.id.clockValue);
		clock.setText(result.getString(result.getColumnCount()-1));
		
		}
		
		if(counter == 0){
			getBtPrev().setEnabled(false);
			getBtNext().setEnabled(true);
		}
		
		else if ( counter == result.getCount()-1){
			getBtNext().setEnabled(false);
			getBtPrev().setEnabled(true);
		}
		else {
			getBtNext().setEnabled(true);
			getBtPrev().setEnabled(true);
		}
		
		
		if( result.getCount() == 0 || result.getCount() == 1 ){
			getBtNext().setEnabled(false);
			getBtPrev().setEnabled(false);
		}
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		if(!isAcquisition())
			updateLayout(this.getCurrentFocus());
	}
	
	private ImageButton getBtPrev(){
		return (ImageButton) findViewById(R.id.btPrevValue);
	}
	
	private ImageButton getBtNext(){
		return (ImageButton) findViewById(R.id.btNextValue);
	}
	
	private Button getBtNewValue(){
		return (Button) findViewById(R.id.btAppendValue);
	}
	
	private void prepareLayout(List<String> attributes) {
		LinearLayout linLay = (LinearLayout) findViewById(R.id.tableViewSpecificAttributes);
		
		for(String attribute : attributes){
			LayoutParams layPara = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			TextView tv = new TextView(getApplicationContext());
			tv.setText(attribute + ":");
			tv.setTextColor(R.color.text);
			tv.setTextSize(25);
			linLay.setLayoutParams(layPara);
			linLay.addView(tv);
			
			EditText et = new EditText(getApplicationContext());
			et.setLayoutParams(layPara);
			et.setEnabled(false);
			linLay.addView(et);
		}
		
		LayoutParams layPara = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		linLay.setLayoutParams(layPara);
		
		layPara = new LayoutParams(getWindowManager().getDefaultDisplay().getWidth()/2, LayoutParams.WRAP_CONTENT);
		getBtPrev().setLayoutParams(layPara);
		getBtNext().setLayoutParams(layPara);
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
	
		db.close();
		return list;
	}
	
//	private String loadSelectedTable() {
//		DBConnection db = new DBConnection(getApplicationContext());
//		Cursor result = db.getSavedInstance();
//		result.moveToFirst();
//		String currentStatistic = result.getString(0);
//		
//		db.close();
//		return currentStatistic;
//	}
	
	@SuppressWarnings("deprecation")
	private void updateLayout(View view) {
		
		
		ImageButton btPrev = (ImageButton) findViewById(R.id.btPrevValue);
		ImageButton btNext = (ImageButton) findViewById(R.id.btNextValue);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

    	Resources resources = getApplicationContext().getResources();
    	Display display = getWindowManager().getDefaultDisplay();
    	DisplayMetrics metrics = resources.getDisplayMetrics();
    	
    	lp.setMargins(Math.round((display.getWidth()/2 * (metrics.densityDpi/160f))/2) , 0, 0, 0);
    	lp.width = Math.round(((66 * (metrics.densityDpi/160f))));
    	lp.height = Math.round(((66 * (metrics.densityDpi/160f))));
    	btPrev.setLayoutParams(lp);
    	btNext.setLayoutParams(lp);
	}

}
