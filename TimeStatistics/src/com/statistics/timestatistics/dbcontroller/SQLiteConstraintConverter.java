package com.statistics.timestatistics.dbcontroller;

public class SQLiteConstraintConverter {
	
	public static String convert(String constraint){
		String convertedConstraint;
		if ( constraint.equals("Text"))
			convertedConstraint = "text";
		else if ( constraint.equals("Zahlen"))
			convertedConstraint = "integer";
		else if ( constraint.equals( "Wahrheitswert" ))
			convertedConstraint = "text";
		else
			convertedConstraint = "";
		return convertedConstraint;
	}

}
