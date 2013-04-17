package com.statistics.timestatistics.definition;


public class ClockState {

	private int stateNumber;
	
	private String state;
	
	
	public ClockState(int stateNumber){
		switch (stateNumber) {
		case 0:
			setState("initialized");
			break;

		case 1:
			setState("activated");
			break;
			
		case 2:
			setState("stopped");
			break;
		}
		
		setStateNumber(stateNumber);
	}
	
	
	public ClockState(String state) throws NoClockStateException{
		if (state.equals("initialized") || state.equals(""))
			setStateNumber(0);
		else if (state.equals("activated"))
			setStateNumber(1);
		else if (state.equals("stopped"))
			setStateNumber(2);
		else{
			throw new NoClockStateException();
		}
		
		setState(state);
	}

	public int getStateNumber() {
		return stateNumber;
	}

	public void setStateNumber(int stateNumber) {
		this.stateNumber = stateNumber;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	
}
