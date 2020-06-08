package it.polimi.tiw.utils;

import java.util.regex.*;

public class RegexpChecker {

	public static final String EMAIL = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";
	
	public static boolean checkExpression(String regexp, String input) {

		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(input);

		return m.matches();
	}
	
	
}
