package com.fantasystep.utils;


import com.fantasystep.helper.Validation;

public class ValidationUtil {
	public static String getValidateRegex(Validation alphanumeric) {
		switch (alphanumeric) {
		case EMAIL:
			return "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
		case ALPHA:
			return "^[a-zA-Z\\s]*$";
		case ALPHANUMERIC:
			return "^[a-zA-Z0-9\\s]*$";
		case DECIMAL:
			return "^[+-]?((\\d+(\\.\\d*)?)|(\\.\\d+))$";
		case IPADDRESS:
			return "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$";
		case NUMERIC:
			return "^[0-9]*$";
		case CUSTOM:
		case NONE:
		default:
		}
		return "";
	}
}
