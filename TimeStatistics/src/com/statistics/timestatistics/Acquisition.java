package com.statistics.timestatistics;

import com.statistics.timestatistics.dbcontroller.DBConnection;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class Acquisition extends Activity{

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acquisition);
		updateLayout(this.getCurrentFocus());
		
		String currentStatistic =  loadSelectedTable();
		
		System.out.println(currentStatistic);
		
		
		
		ImageButton btApply = (ImageButton) findViewById(R.id.btApplyNewValue);
		ImageButton btClear = (ImageButton) findViewById(R.id.btClearFormular);
	}

	private String loadSelectedTable() {
		DBConnection db = new DBConnection(getApplicationContext());
		Cursor result = db.getSavedInstance();
		result.moveToFirst();
		String currentStatistic = result.getString(0);
		
		db.discardSaving();
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
