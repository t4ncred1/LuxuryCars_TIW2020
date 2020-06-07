package it.polimi.tiw.utils;

import java.util.ArrayList;
import java.util.List;

public class DynamicJsonObject {

	private List<String> attributes;
	
	public DynamicJsonObject() {
		attributes = new ArrayList<>();
	}
	
	public void addAttribute(String name, String value) {
		String newAtt;
		newAtt = "\"" + name + "\":" + "\"" + value + "\"";
		attributes.add(newAtt);
	}
	
	public String getJsonString() {
		String returnString;
		returnString = "{";
		for(int j = 0; j < attributes.size()-1; j++){
			returnString = returnString + attributes.get(j) + ",";
		}
		returnString = returnString + attributes.get(attributes.size()-1);
		returnString = returnString + "}";
		return returnString;
	}

}