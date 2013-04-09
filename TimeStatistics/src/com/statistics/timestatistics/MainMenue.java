package com.statistics.timestatistics;

import com.statistics.timestatistics.dbcontroller.DBConnection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;


public class MainMenue extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menue);
		updateLayout();
		
		Button btNewStatistic = (Button) findViewById(R.id.btnewstat);
		btNewStatistic.setOnClickListener(new AdapterView.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in = new Intent(MainMenue.this, FormularForSettingUpNewStatistic.class);
                startActivity(in);
			}
		});
		
		Button btShowStatistic = (Button) findViewById(R.id.btshowstats);
		btShowStatistic.setOnClickListener(new AdapterView.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in = new Intent(MainMenue.this, ListOfexistingStatistics.class);
                startActivity(in);
			}
		});
		
	}


	@SuppressWarnings("deprecation")
	private void updateLayout() {
		Button btNew = (Button) findViewById(R.id.btnewstat);
		Button btShow = (Button) findViewById(R.id.btshowstats);
		Display display = getWindowManager().getDefaultDisplay();
    	
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, (LayoutParams.WRAP_CONTENT));
		params.setMargins(display.getWidth()/10, (display.getHeight()/5), display.getWidth()/10, 0);
		
		btNew.setLayoutParams(params);
		btShow.setLayoutParams(params);
	}

	@Override
	public void onBackPressed() {
		System.exit(0);
	}
}
