package com.statistics.timestatistics;

import java.util.ArrayList;
import java.util.List;

import com.statistics.timestatistics.dbcontroller.DBConnection;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Display;
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
	
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acquisition);
		updateLayout(this.getCurrentFocus());
		
		final String currentStatistic =  loadSelectedTable();
		List<String> attributes = loadAttributes(currentStatistic);
		prepareLayout(attributes);
		
		System.out.println(currentStatistic);
		
    	Display display = getWindowManager().getDefaultDisplay();
		
		ImageButton btApply = (ImageButton) findViewById(R.id.btApplyNewValue);
		btApply.setLayoutParams(new LinearLayout.LayoutParams(display.getWidth()/2, LayoutParams.WRAP_CONTENT));
		btApply.setOnClickListener(new AdapterView.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				StringBuilder sql = new StringBuilder();
				sql.append("insert into " + currentStatistic + " values(");
				
				LinearLayout linLay = (LinearLayout) findViewById(R.id.acquisitionLayout);
				for(int i = 0; i < linLay.getChildCount(); i++){
					if( linLay.getChildAt(i) instanceof EditText){
						sql.append(((EditText) linLay.getChildAt(i)).toString() + ", ");
					}
				}
				sql.append(")");
				
				DBConnection db = new DBConnection(getApplicationContext());
				db.getWritableDatabase().execSQL(sql.toString());
			}
		});
		
		ImageButton btClear = (ImageButton) findViewById(R.id.btClearFormular);
		btClear.setLayoutParams(new LinearLayout.LayoutParams(display.getWidth()/2, LayoutParams.WRAP_CONTENT));
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
				clock = (Chronometer) findViewById(R.id.clock);
				
				if ( isClockStarted() ){
					clock.stop();
					btClock.setText("Start");
					time = clock.getText().toString();
				}
				else{
					clock.start();
					btClock.setText("Stop");
				}
				
			}

		});
		
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
			}
		}
		
	}

	private void prepareLayout(List<String> attributes) {
		LinearLayout linLay = (LinearLayout) findViewById(R.id.acquisitionLayout);
		
		for(String attribute : attributes){
			android.widget.FrameLayout.LayoutParams layPara = new android.widget.FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			TextView tv = new TextView(getApplicationContext());
			tv.setText(attribute + ":");
			tv.setTextColor(R.color.text);
			linLay.setLayoutParams(layPara);
			linLay.addView(tv);
			
			EditText et = new EditText(getApplicationContext());
			et.setLayoutParams(layPara);
			linLay.addView(et);
		}
		
		android.widget.FrameLayout.LayoutParams layPara = new android.widget.FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
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
	
		return list;
	}

	private boolean isClockStarted() {
		if( ((Button) findViewById(R.id.clockButton)).getText().toString().equals("Start") )
			return false;
		else
			return true;
	}

	private String loadSelectedTable() {
		DBConnection db = new DBConnection(getApplicationContext());
		Cursor result = db.getSavedInstance();
		result.moveToFirst();
		String currentStatistic = result.getString(0);
		
		db.discardSaving();
		db.close();
		return currentStatistic;
	}

	private void updateLayout(View view) {
		
		
		ImageButton btApply = (ImageButton) findViewById(R.id.btApplyNewValue);
		ImageButton btClear = (ImageButton) findViewById(R.id.btClearFormular);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

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
