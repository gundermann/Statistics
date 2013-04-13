package com.statistics.timestatistics.business;

import android.content.Context;
import android.widget.Chronometer;

public class StatisticClock extends Chronometer{
	
	private Long time;
	
	public StatisticClock(Context context) {
		super(context);
	}
	
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	

	
}
