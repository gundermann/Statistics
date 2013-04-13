package com.statistics.timestatistics;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class TimeStatistics extends Activity {

	public Runnable r;
	public Thread t;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_statistics_main);
		correctLayout(this.findViewById(android.R.id.content));
		 r = new Runnable(){
	            @Override
	            public void run(){
	                    try{
	                        Thread.sleep(3000);
	                        Intent in = new Intent(TimeStatistics.this, MainMenue.class);
	                        startActivity(in);

	                    } catch (InterruptedException e) {
	                        
	                    }
	                }
	            };
	}

	@Override
	protected void onStart() {
		super.onStart();
		t = new Thread(r);
		t.start();
	}
	
	@SuppressWarnings("deprecation")
	private void correctLayout(View view) {
		
		LinearLayout linlay = (LinearLayout) ((FrameLayout) view).getChildAt(0);
		
    	Display display = getWindowManager().getDefaultDisplay();
    	
		linlay.getChildAt(0).setPadding(0, display.getHeight()/3, 0, 0);
		
		linlay.getChildAt(1).setPadding(0, display.getHeight()/2, 0, 0);
	}
	
	@Override
	public void onBackPressed() {
		System.exit(0);
	}

	

}
