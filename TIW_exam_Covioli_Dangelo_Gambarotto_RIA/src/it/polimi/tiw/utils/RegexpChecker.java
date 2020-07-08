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

import java.util.regex.*;


/**
 * @author Luca Gambarotto <luca.gambarotto@mail.polimi.it>
 * @version 1.0
 * 
 * This class allows to check if a defined string is compliant
 * with the desired regular expression. In this version of the class
 * only the EMAIL pattern has been included, but the class can be
 * easily extended to contain other patterns defined by the
 * programmer.
 */

public class RegexpChecker {

	/**
	 * List of patterns that can be used during the check phase
	 * In this implementation only the EMAIL pattern has been added
	 */
	public static final String EMAIL = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";
	
	/**
	 * static method that allows to check if a given string is
	 * compliant with a pre-defined regular expression pattern
	 * @param regexp the pattern to be used (ex. EMAIL)
	 * @param input the string to be checked
	 * @return true if the string is compliant with the pattern, false otherwise.
	 */
	public static boolean checkExpression(String regexp, String input) {

		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(input);

		return m.matches();
	}
	
	
}
