package com.fantasystep.utils;

import java.util.HashMap;
import java.util.Map;

public class Environment {

	private static Map<String, String> map = new HashMap<String, String>();
	public static Object getProperty(String storageLocationProperty) {
		return map.get(storageLocationProperty);
	}

	public static void addProperty(String storageLocationProperty, String value) {
		map.put(storageLocationProperty, value);
	}
}
