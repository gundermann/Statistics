package com.common;

public class StringModifier {
	
	public static String deleteSpaces(String tableName) {
		if (tableName.contains(" ")){
			StringBuilder builderWithoutSpaces = new StringBuilder();
			for(Character c : tableName.toCharArray()){
				if(!c.equals(' '))
					builderWithoutSpaces.append(c);
			}
			tableName = builderWithoutSpaces.toString();
		}
		return tableName;
	}

	public static boolean isValidDatabaseName(String statisticName) {
		if(statisticName.equals("") || !startsWithNumber(statisticName))
			return false;
		else
			return true;
	}

	public static boolean startsWithNumber(String statisticName) {
		if(statisticName.startsWith("0"))
			return true;
		else if(statisticName.startsWith("1"))
			return true;
		else if(statisticName.startsWith("2"))
			return true;
		else if(statisticName.startsWith("3"))
			return true;
		else if(statisticName.startsWith("4"))
			return true;
		else if(statisticName.startsWith("5"))
			return true;
		else if(statisticName.startsWith("6"))
			return true;
		else if(statisticName.startsWith("7"))
			return true;
		else if(statisticName.startsWith("8"))
			return true;
		else if(statisticName.startsWith("9"))
			return true;
		else 
			return false;
	}

}
