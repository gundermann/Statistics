package com.statistics.timestatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.common.StringModifier;
import com.statistics.timestatistics.business.Statistic;
import com.statistics.timestatistics.dbcontroller.DBConnection;
import com.statistics.timestatistics.dbcontroller.PersistensStatisticHandler;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class FormularForSettingUpNewStatistic extends Acquisition{
	
	private List<String> attributes = new ArrayList<String>();
	
	@Override
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
			@SuppressLint("UseSparseArrays")
			@Override
			public void onClick(View v) {
				String statisticName = ((EditText) findViewById(R.id.etNewStatisticName)).getText().toString();
				
				if( StringModifier.isValidDatabaseName(statisticName) ){
					openNoTitleDialog();
				}
				else{
				addNewAttribute("ID");
				addNewAttribute(((EditText) findViewById(R.id.etNewAttributeName)).getText().toString());
				addNewAttribute("time");
				
				if(!allAttributedValid()){
					openWrongAttributeDialog();
				}
				else{
				DBConnection dbc = new DBConnection(getApplicationContext());
				dbc.insertStatistic("statistics", statisticName );

				//Tablename = Given name + ID
				String tableName = "";
				Cursor result = dbc.getAllStatNames();
				result.moveToLast();
				tableName = statisticName + result.getString(0);

				statistic = new Statistic(tableName, attributes, new HashMap<Integer, List<String>>());
				 
				PersistensStatisticHandler psh = new PersistensStatisticHandler(dbc, statistic.getName());
				psh.saveStatistic(statistic); 
				
				dbc.saveStateInDatabase(tableName, true);
				dbc.close();

				setAcquisition();
				}
				}
			}



		});
		}
	}

	private boolean allAttributedValid() {
		for(String attributeName : attributes){
			if(StringModifier.startsWithNumber(attributeName))
				return false;
		}
		return true;
	}
	
	private void openWrongAttributeDialog(){
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.dialog_wrong_attribute, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);

		alertDialogBuilder.setView(promptsView);

		alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			    @Override
				public void onClick(DialogInterface dialog,int id) {
			    	dialog.cancel();
			    	dialog = null;
			    }

			  });

		setDialog(alertDialogBuilder.create());
		dialog.show();	
		
		attributes.clear();
	}

	private void openNoTitleDialog() {
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.dialog_no_title, null);
//		final EditText attName = (EditText) promptsView
//				.findViewById(R.id.etNewAttributeName);
//		final EditText attType = (EditText) promptsView
//				.findViewById(R.id.etNewAttributeType);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);

		alertDialogBuilder.setView(promptsView);

		alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			    @Override
				public void onClick(DialogInterface dialog,int id) {
			    	dialog.cancel();
			    	dialog = null;
			    }

			  });

		setDialog(alertDialogBuilder.create());
		dialog.show();		
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
