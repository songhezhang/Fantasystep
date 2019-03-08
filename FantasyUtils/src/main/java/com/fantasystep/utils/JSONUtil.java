package com.fantasystep.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {
	
    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();
        if(object == JSONObject.NULL)
            return map;
        @SuppressWarnings("unchecked")
		Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            if(value == null)
            	map.put(key, "");
            else
            	map.put(key, value);
        }
        return map;
    }
    
    public static JSONObject toJSON(String jsonStr) {
    	if(jsonStr == null)
    		return null;
    	jsonStr.replace("null", "\"\"");
    	return new JSONObject(jsonStr);
    }
    
    public static JSONObject toJSON(Map<String, Object> map) {
    	return new JSONObject(map);
    }
    
    public static JSONObject toJSON(Collection<Object> list) {
    	return new JSONObject(list);
    }
    
    public static List<?> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            if(value != null)
            	list.add(value);
        }
        return list;
    }
}
