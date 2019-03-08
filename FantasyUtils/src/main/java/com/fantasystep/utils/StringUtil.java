package com.fantasystep.utils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class StringUtil {
	
	public static String join(Iterable<? extends Object> objs, String delimiter) {
		if (objs == null) {
			return "";
		}
		Iterator<? extends Object> iter = objs.iterator();
		StringBuilder buffer = new StringBuilder();
		buffer.append(iter.next());
		while (iter.hasNext())
			buffer.append(delimiter).append(iter.next());
		return buffer.toString();
	}
	
	public static<T> String join(Iterable<? extends T> objs, String delimiter, Stringifier<T> stringifier)
	{
		if (objs == null) {
			return "";
		}
		Iterator<? extends T> iter = objs.iterator();
		StringBuilder buffer = new StringBuilder();
		buffer.append(stringifier.toString(iter.next()));
		while (iter.hasNext())
			buffer.append(delimiter).append(iter.next());
		return buffer.toString();
	}
	
	public static String join(String delimiter, Object... objs) {
		ArrayList<Object> list = new ArrayList<Object>();
		Collections.addAll(list, objs);
		return join(delimiter, list);
	}
	
	public static interface Stringifier<T> {
		abstract public String toString(T t);
	}
}
