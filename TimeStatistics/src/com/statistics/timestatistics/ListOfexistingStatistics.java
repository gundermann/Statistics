package com.statistics.timestatistics;

import java.util.ArrayList;
import java.util.List;

import com.statistics.timestatistics.dbcontroller.DBConnection;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;


public class ListOfexistingStatistics extends Activity {

	DBConnection dbc = new DBConnection(getApplicationContext());
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistic_list);
		
		Cursor result = dbc.getAllStatNames();
		
		ListView existingStats = (ListView) findViewById(R.id.listofexistingstats);
		List<String> valueList = new ArrayList<String>();
		result.moveToFirst();
		for(int i = 0; i < result.getCount(); i++){
			valueList.add(result.getString(1));
			result.moveToNext();
		}
		
		ListAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, valueList);
		existingStats.setAdapter(adapter);
	}

	@Override
	public void onBackPressed(){
		dbc.close();
		Intent in = new Intent(ListOfexistingStatistics.this, MainMenue.class);
        startActivity(in);
        System.exit(0);
	}
}
