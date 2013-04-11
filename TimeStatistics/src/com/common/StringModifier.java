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

}
