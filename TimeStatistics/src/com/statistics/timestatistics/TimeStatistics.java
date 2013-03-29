package com.statistics.timestatistics;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
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
	
	private void correctLayout(View view) {
		
		LinearLayout linlay = (LinearLayout) ((FrameLayout) view).getChildAt(0);
		
		Resources resources = getApplicationContext().getResources();
    	Display display = getWindowManager().getDefaultDisplay();
    	DisplayMetrics metrics = resources.getDisplayMetrics();
    	
    	
		linlay.getChildAt(0).setPadding(0, display.getWidth()-Math.round((44* (metrics.densityDpi/160f))), 0, 0);
	}
	
	@Override
	public void onBackPressed() {
		System.exit(0);
	}

	

}
