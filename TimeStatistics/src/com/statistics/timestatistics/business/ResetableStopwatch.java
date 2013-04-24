package com.statistics.timestatistics.business;

import android.widget.Chronometer;

public class ResetableStopwatch extends Stopwatch{
	
	
	public ResetableStopwatch(Chronometer clock) {
		super(clock);
	}
	
	

	/**
	 * Resets the Clock to 00:00
	 */
	public void handleReset() {
		this.setState(0);
		this.setLastTime(0L);
		getClock().stop();
		showTime();
	}
	
}
