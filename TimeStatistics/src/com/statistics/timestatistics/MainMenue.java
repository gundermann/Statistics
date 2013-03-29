package com.statistics.timestatistics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;


public class MainMenue extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menue);
		
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
                System.exit(0);
			}
		});
	}

	@Override
	public void onBackPressed() {
		System.exit(0);
	}
}
