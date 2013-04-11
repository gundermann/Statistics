package com.statistics.timestatistics;

import java.util.ArrayList;
import java.util.List;

import com.statistics.timestatistics.dbcontroller.DBConnection;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;


public class ListOfexistingStatistics extends Activity {

	DBConnection dbc = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistic_list);
		
		dbc =  new DBConnection(getApplicationContext());
		
		Cursor result = dbc.getAllStatNames();
		
		final ListView existingStats = (ListView) findViewById(R.id.listofexistingstats);
		List<String> valueList = new ArrayList<String>();
		result.moveToFirst();
		for(int i = 0; i < result.getCount(); i++){
			valueList.add(result.getString(1));
			result.moveToNext();
		}
		
		ListAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner, valueList);
		existingStats.setAdapter(adapter);
		
		existingStats.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String tableName = existingStats.getItemAtPosition(arg2).toString()+String.valueOf(arg2+1);
				dbc.saveStateInDatabase(tableName, false);
				dbc.close();
				
				Intent in = new Intent(ListOfexistingStatistics.this, SelectedStatisticView.class);
				startActivity(in);
				
			}
		});
	}

	@Override
	public void onBackPressed(){
		dbc.close();
//		Intent in = new Intent(ListOfexistingStatistics.this, MainMenue.class);
//        startActivity(in);
        System.exit(0);
	}
}
