package com.statistics.timestatistics;

import java.util.List;

import com.common.StringModifier;
import com.statistics.timestatistics.business.ResetableStopwatch;
import com.statistics.timestatistics.dbcontroller.DBConnection;
import com.statistics.timestatistics.definition.ClockState;

import android.content.res.Configuration;
import android.content.res.Resources;
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
	
	/**
	 * The Position of the value-view evaluated by Id (start by 1)
	 */
	int counter = 1;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!isAcquisition()){
		setContentView(R.layout.table_view);
		updateLayout(this.getCurrentFocus());
		
		final String currentStatistic = StringModifier.deleteSpaces(loadSelectedTable());
		
		loadStaistic();
		prepareLayout(statistic.getAttributesWithoutIdAndTime());
		
		updateElements();
		
		getBtPrev().setOnClickListener(new AdapterView.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				counter--;
				updateElements();
			}
		});
		
		getBtNext().setOnClickListener(new AdapterView.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				counter++;
				updateElements();
			}
		});
		
		getBtNewValue().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DBConnection dbc = new DBConnection(getApplicationContext());
				dbc.saveStateInDatabase(currentStatistic, true);
				dbc.close();
				setAcquisition();
			}
		});
		
		}
	}
	

	private void updateElements(){
		LinearLayout linLay = (LinearLayout) findViewById(R.id.tableViewSpecificAttributes);
		
		if( statistic.getAllValues().isEmpty()){
			TextView tvNoValues = new TextView(getApplicationContext());
			tvNoValues.setText("No Values");
			LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			tvNoValues.setLayoutParams(lp);
			tvNoValues.setGravity(Gravity.CENTER);
			linLay.addView(tvNoValues);
		}
		else{
		int elementIndex = 1;
		for(int i = 0; i < statistic.getAttributeCount()-2; i++){
			((EditText) linLay.getChildAt(elementIndex)).setText(statistic.getValueWithoutTimeAt(counter, i));
		}
		
		clock = new ResetableStopwatch((Chronometer) findViewById(R.id.clockValue));
		updateClock();
		clock.showTimeWithNewBase();
		
		}
		
		if(counter == 1){
			getBtPrev().setEnabled(false);
			getBtNext().setEnabled(true);
		}
		
		else if ( counter == statistic.getValueCount()){
			getBtNext().setEnabled(false);
			getBtPrev().setEnabled(true);
		}
		else {
			getBtNext().setEnabled(true);
			getBtPrev().setEnabled(true);
		}
		
		
		if( statistic.getValueCount() == 0 || statistic.getValueCount() == 1 ){
			getBtNext().setEnabled(false);
			getBtPrev().setEnabled(false);
		}
		
	}
	@Override
	protected void updateClock() {
		if(isAcquisition())
			super.updateClock();
		clock.updateClock(statistic.getTimeAt(counter), new ClockState(0));
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
	
	@SuppressWarnings("deprecation")
	private void prepareLayout(List<String> attributes) {
		LinearLayout linLay = (LinearLayout) findViewById(R.id.tableViewSpecificAttributes);
		
		for(String attribute : attributes){
			LayoutParams layPara = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
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
		
		LayoutParams layPara = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		linLay.setLayoutParams(layPara);
		
		layPara = new LayoutParams(getWindowManager().getDefaultDisplay().getWidth()/2, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		getBtPrev().setLayoutParams(layPara);
		getBtNext().setLayoutParams(layPara);
	}

	@SuppressWarnings("deprecation")
	private void updateLayout(View view) {
		
		
		ImageButton btPrev = (ImageButton) findViewById(R.id.btPrevValue);
		ImageButton btNext = (ImageButton) findViewById(R.id.btNextValue);
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

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
