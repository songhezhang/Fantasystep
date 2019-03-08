package com.fantasystep.utils;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;

public class ClassUtil {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object initObj(Class<?> clazz, Object value) {
		if(value == null)
			return null;
		if(clazz == null)
			clazz = String.class;
		if(clazz.equals(String.class))
			return value.toString();
		if(Enum.class.isAssignableFrom(clazz))
			return Enum.valueOf((Class<? extends Enum>)clazz, value.toString());
		if(clazz.equals(UUID.class)) 
			return UUID.fromString(value.toString());
		if(clazz.equals(byte[].class) )
			return new String((byte[])value);
		if(clazz.equals(Class.class))
			try {
				if(value.toString().startsWith("class "))
					return Class.forName(value.toString().substring(5));
				else return Class.forName(value.toString());
			} catch (ClassNotFoundException e1) {
				return null;
			}
		if(clazz.equals(Boolean.class))
			return Boolean.valueOf(value.toString());
		if(clazz.equals(Date.class))
			try {
				return DateUtil.STARDARD_DATE_FORMAT.parse(value.toString());
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		if(clazz.equals(Double.class))
			return Double.parseDouble(value.toString());
		if(clazz.equals(Long.class))
			try {
				return Long.parseLong(value.toString());
			} catch (NumberFormatException e) {
				return Long.parseLong(value.toString());
			}
		if(clazz.equals(Float.class))
			return Float.parseFloat(value.toString());
		if(clazz.equals(Integer.class))
			return Integer.parseInt(value.toString());
		
		return value;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Collection fromStringToCollection(Class<? extends Collection> colClazz, Class<?> clazz, String string) {
		Collection coll = null;
		try {
			coll = colClazz.newInstance();
			if(clazz != null && Map.class.isAssignableFrom(clazz))
				coll.addAll(JSONUtil.toList(new JSONArray(string)));
			else if(string.startsWith("[") && string.endsWith("]")) {
				String tmp = string.substring(1, string.length() - 1);
				if(tmp.contains(",")) {
					for(String s : tmp.split(",")) {
						String vv = s.trim();
						if(vv.startsWith("\"") && vv.endsWith("\""))
							vv = vv.substring(1, vv.length() - 1);
						coll.add(initObj(clazz, vv));
					}
				} else if (!tmp.isEmpty()) {
					String vv = tmp.trim();
					if(vv.startsWith("\"") && vv.endsWith("\""))
						vv = vv.substring(1, vv.length() - 1);
					if(!vv.isEmpty())
						coll.add(initObj(clazz, vv));
				}
			} else {
				if(string.contains(",")) {
					for(String s : string.split(",")) {
						String vv = s.trim();
						if(vv.startsWith("\"") && vv.endsWith("\""))
							vv = vv.substring(1, vv.length() - 1);
						coll.add(initObj(clazz, vv));
					}
				} else if (!string.isEmpty())
					coll.add(initObj(clazz, string));
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return coll;
	}
	
	public static String fromCollectionToString(Collection<Object> coll) {
		return "[" + StringUtil.join(coll, ",") + "]";
	}
}
