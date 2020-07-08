/*  _______ _______          __                                    
 * |__   __|_   _\ \        / /                                    
 *    | |    | |  \ \  /\  / /                                     
 *    | |    | |   \ \/  \/ /                                      
 *    | |   _| |_   \  /\  /                                       
 *    |_|  |_____|   \/  \/   
 * 
 * exam project - a.y. 2019-2020
 * Politecnico di Milano
 * 
 * Tancredi Covioli   mat. 944834
 * Alessandro Dangelo mat. 945149
 * Luca Gambarotto    mat. 928094
 */

package it.polimi.tiw.utils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Luca Gambarotto <luca.gambarotto@mail.polimi.it>
 * @version 1.0
 * 
 * This class allows to create and maintain objects to be converted
 * in JSON object
 */
public class DynamicJsonObject {

	/**
	 * The attributes that must be stored in the object
	 */
	private List<String> attributes;
	
	/**
	 * Class constructor
	 */
	public DynamicJsonObject() {
		attributes = new ArrayList<>();
	}
	
	/**
	 * This method add a new parameter in the object
	 * @param name name of the parameter
	 * @param value value of the parameter
	 */
	public void addAttribute(String name, String value) {
		String newAtt;
		newAtt = "\"" + name + "\":" + "\"" + value + "\"";
		attributes.add(newAtt);
	}
	
	/**
	 * This method convert the object, including all the
	 * parameter added to it, in a JSON string
	 * @return the JSON string representing the object
	 */
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