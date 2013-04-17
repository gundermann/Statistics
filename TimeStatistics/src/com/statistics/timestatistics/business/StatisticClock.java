package com.statistics.timestatistics.business;

import com.statistics.timestatistics.definition.ClockState;
import com.statistics.timestatistics.definition.NoClockStateException;

import android.os.SystemClock;
import android.widget.Chronometer;

public class StatisticClock{
	
	private Long lastTime;
	
	private Chronometer clock;
	
	private ClockState state;
	
	public StatisticClock(Chronometer clock) {
		this.clock = clock;
	}
	
	
	public void updateClock(Long time, ClockState state){
		this.state = state;
		this.lastTime = time;
	}
	
	/**
	 * Resets the Clock to 00:00
	 */
	public void handleReset() {
		this.setState(0);
		this.setLastTime(0L);
		clock.stop();
	}

	/**
	 * Stops the clock
	 */
	public void handleStopped() {
		this.setState(2);
		this.setLastTime(SystemClock.elapsedRealtime()-clock.getBase());
		clock.stop();
	}

	
	/**
	 * if clock was initialized the clock begin from 00:00
	 * if clock was stopped the clock begins from saved time
	 * @param state
	 * @throws NoClockStateException
	 */
	public void handleStart(ClockState state) throws NoClockStateException {
		this.setState(1);
		switch (state.getStateNumber()) {
		case 0:
			this.setLastTime(0L);
			clock.setBase(SystemClock.elapsedRealtime());
			break;

		case 1:
			clock.setBase(SystemClock.elapsedRealtime()-this.lastTime);
			break;
			
		case 2:
			clock.setBase(SystemClock.elapsedRealtime()-this.lastTime);
			break;
		default:
			throw new NoClockStateException();
		}
		
		clock.start();
		
	}
	
	
	public Chronometer getClock() {
		return clock;
	}

	public ClockState getState() {
		return state;
	}

	public void setState(int state) {
		this.state = new ClockState(state);
	}

	public Long getTime() {
		return SystemClock.elapsedRealtime()-clock.getBase();
	}
	
	
	public void setLastTime(Long time) {
		this.lastTime = time;
	}


	public void showTime() {
		clock.setBase(SystemClock.elapsedRealtime()-this.lastTime);
		clock.start();
		clock.stop();
	}


	public Long getLastTime() {
		return lastTime;
	}
	

	
}
