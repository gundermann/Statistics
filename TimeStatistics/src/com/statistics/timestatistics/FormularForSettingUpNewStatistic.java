package com.statistics.timestatistics;

import java.util.ArrayList;
import java.util.List;

import com.common.StringModifier;
import com.statistics.timestatistics.dbcontroller.DBConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class FormularForSettingUpNewStatistic extends Acquisition{
	
	//Zwischenspeicher
//	private HashMap<String, String> attributes = new HashMap<String, String>();
	private List<String> attributes = new ArrayList<String>();
	private static int DIALOG_TITLE = 1;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!isAcquisition()){
		setContentView(R.layout.new_statistic);
		
//		ArrayAdapter<DataType> typeAdapter = new ArrayAdapter<DataType>(getApplicationContext(), R.layout.spinner, DataType.values());
//		final Spinner typeSpinner = (Spinner) findViewById(R.id.spinNewAttributeType);
//		typeSpinner.setAdapter(typeAdapter);
		
//		Button btNewAttribute = (Button) findViewById(R.id.btNewAttribute);
//		btNewAttribute.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				openDialogForNewAttritubte();code
//			}
//		});
		
		Button btApplyNewStatistic = (Button) findViewById(R.id.btApplyNewStatistic);
		btApplyNewStatistic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String statisticName = ((EditText) findViewById(R.id.etNewStatisticName)).getText().toString();
				if( statisticName.equals("") ){
					openNoTitleDialog();
				}
				else{
//				addNewAttribute(((EditText) findViewById(R.id.etNewAttributeName)).getText().toString(), (DataType) typeSpinner.getSelectedItem());
				addNewAttribute(((EditText) findViewById(R.id.etNewAttributeName)).getText().toString());
				
				DBConnection dbc = new DBConnection(getApplicationContext());
				dbc.insertStatistic("statistics", statisticName );

				String tableName = "";
				Cursor result = dbc.getAllStatNames();
				result.moveToFirst();
				  for(int i = 0; i< result.getCount();i++){
					  if( statisticName.equals(result.getString(1))){
						  tableName = statisticName + result.getString(0);
						  tableName = StringModifier.deleteSpaces(tableName);
						  System.out.println(tableName);
						  break;
					  }
					  result.moveToNext();
				  }
				
				dbc.createNewTable(tableName, attributes);
				
				dbc.saveStateInDatabase(tableName, true);
				dbc.close();
//				Intent in = new Intent(FormularForSettingUpNewStatistic.this, Acquisition.class);
//		        startActivity(in);
				setAcquisition();
//		        System.exit(0);
//				new Acquisition();
				}
			}


		});
		}
	}

	private void openNoTitleDialog() {
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.dialog_no_title, null);
		final EditText attName = (EditText) promptsView
				.findViewById(R.id.etNewAttributeName);
		final EditText attType = (EditText) promptsView
				.findViewById(R.id.etNewAttributeType);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);

		alertDialogBuilder.setView(promptsView);

		alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	dialog.cancel();
			    }

			  });

		AlertDialog alertDialog = alertDialogBuilder.create();
		setDialog(alertDialog);
		alertDialog.show();		
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
	
	

	private void addNewAttribute(String name) {
//		attributes.put(name, type.toString());
		if ( !name.equals(""))
			attributes.add(name);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
	}
	
}
