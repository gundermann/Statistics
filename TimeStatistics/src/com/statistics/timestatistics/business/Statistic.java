package com.statistics.timestatistics.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Statistic {

	private String name;
	
	private List<String> attributes;
	
	private Map<Integer, List<String>> values;

	
	public Statistic(String name, List<String> attributes, Map<Integer, List<String>> values){
		this.name = name;
		this.attributes = attributes;
		this.values = values;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}

	public Map<Integer, List<String>> getAllValues() {
		return values;
	}

	public void setAllValues(Map<Integer, List<String>> values) {
		this.values = values;
	}
	
	public  List<String> getValues(int line) {
		return values.get(line);
	}

	public boolean isEmpty() {
		return values.isEmpty();
	}

	public int getAttributeCount() {
		return attributes.size();
	}

	public String getValueAt(int counter, int i) {
		return values.get(counter).get(i);
	}

	public int getValueCount() {
		return values.size();
	}
	
	public List<String> getValuesWithoutTimeAt(int i){
		List<String> dummyValues = new ArrayList<String>();
		dummyValues.addAll(values.get(i));
		dummyValues.remove(dummyValues.size()-1);
		return dummyValues;
	}
	
	public List<String> getAttributesWithoutIdAndTime(){
		List<String> dummyAttributes = new ArrayList<String>();
		dummyAttributes.addAll(attributes);
		dummyAttributes.remove(0);
		dummyAttributes.remove(dummyAttributes.size()-1);
		return dummyAttributes;
	}

	public void addValue(List<String> valuesToSave, Long time) {
		valuesToSave.add(String.valueOf(time));
		values.put(values.size()+1, valuesToSave); 
	}
	
	public long getTimeAt(int id){
		if(values.get(id) == null)
			return 0L;
		else
			return Long.parseLong(values.get(id).get(getAttributeCount()-2));
	}

	public String getValueWithoutTimeAt(int counter, int i) {
		List<String> valueListWithoutTime = new ArrayList<String>();
		valueListWithoutTime.addAll(values.get(counter));
		valueListWithoutTime.remove(valueListWithoutTime.size()-1);
		return valueListWithoutTime.get(i);
	}
	
	
}
