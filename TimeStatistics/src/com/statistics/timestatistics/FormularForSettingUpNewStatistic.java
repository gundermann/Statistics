package com.statistics.timestatistics;

import java.util.HashMap;

import com.statistics.timestatistics.dbcontroller.DBConnection;
import com.statistics.timestatistics.definition.DataType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class FormularForSettingUpNewStatistic extends Activity{
	
	//Zwischenspeicher
	private HashMap<String, String> attributes = new HashMap<String, String>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_statistic);
		
		ArrayAdapter<DataType> typeAdapter = new ArrayAdapter<DataType>(getApplicationContext(), R.layout.spinner, DataType.values());
		final Spinner typeSpinner = (Spinner) findViewById(R.id.spinNewAttributeType);
		typeSpinner.setAdapter(typeAdapter);
		
//		Button btNewAttribute = (Button) findViewById(R.id.btNewAttribute);
//		btNewAttribute.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				openDialogForNewAttritubte();
//			}
//		});
		
		Button btApplyNewStatistic = (Button) findViewById(R.id.btApplyNewStatistic);
		btApplyNewStatistic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addNewAttribute(((EditText) findViewById(R.id.etNewAttributeName)).getText().toString(), (DataType) typeSpinner.getSelectedItem());
				
				String statisticName = ((EditText) findViewById(R.id.etNewStatisticName)).getText().toString();
				
				DBConnection dbc = new DBConnection(getApplicationContext());
				dbc.insertStatistic("statistics", statisticName );

				String tableName = "";
				Cursor result = dbc.getAllStatNames();
				result.moveToFirst();
				  for(int i = 0; i< result.getCount();i++){
					  if( statisticName.equals(result.getString(1))){
						  tableName = statisticName + result.getString(0);
						  System.out.println(tableName);
						  break;
					  }
					  result.moveToNext();
				  }
				
				dbc.createNewTable(tableName, attributes);
				
				dbc.saveStateInDatabase(tableName);
				Intent in = new Intent(FormularForSettingUpNewStatistic.this, Acquisition.class);
		        startActivity(in);
//		        System.exit(0);
//				new Acquisition();
			}
		});
	}
	
//	private void openDialogForNewAttritubte() {
//		LayoutInflater li = LayoutInflater.from(this);
//		View promptsView = li.inflate(R.layout.dialog_new_attribute, null);
//		
//		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//				this);
//
//		alertDialogBuilder.setView(promptsView);
//
//		final EditText attName = (EditText) promptsView
//				.findViewById(R.id.etNewAttributeName);
//		final EditText attType = (EditText) promptsView
//				.findViewById(R.id.etNewAttributeType);
//
//		alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
//				new DialogInterface.OnClickListener() {
//			    public void onClick(DialogInterface dialog,int id) {
//			    	addNewAttribute(attName.getText().toString(), attType.getText().toString());
//			    	dialog.cancel();
//			    }
//
//			  })
//			.setNegativeButton("Cancel",
//			  new DialogInterface.OnClickListener() {
//			    public void onClick(DialogInterface dialog,int id) {
//				dialog.cancel();
//			    }
//			  });
//
//		AlertDialog alertDialog = alertDialogBuilder.create();
//
//		alertDialog.show();		
//	}
	
	private void addNewAttribute(String name, DataType type) {
		attributes.put(name, type.toString());
	}

	@Override
	public void onBackPressed(){
		Intent in = new Intent(FormularForSettingUpNewStatistic.this, MainMenue.class);
        startActivity(in);
        System.exit(0);
	}
}
