package com.fantasystep.utils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class ObjectDumper {

	private ObjectDumper() {
	}

	public static void dumpObjectSysOut(Object object) {
		ObjectDumper od = new ObjectDumper();
		od.privDumpObjectSysOut(object);
	}

	private void privDumpObjectSysOut(Object obj) {
		System.out.println("<Class name=\"" + obj.getClass().getName() + "\">");
		try {
			privDObjectSysOut(obj, true);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		System.out.print("</Class>");
	}

	@SuppressWarnings("rawtypes")
	private void privDObjectSysOut(Object obj, boolean newline)
			throws IllegalArgumentException, IllegalAccessException {

		for (Field field : obj.getClass().getFields()) {
			String fieldName = "fieldname=\"" + field.getName() + "\"";
			String fieldClass = "fieldtype=\"" + field.getType().getName()
					+ "\"";
			fieldName = fieldName + " " + fieldClass;

			if (String.class.isAssignableFrom(field.getType())) {
				System.out.print("<String " + fieldName + " value=\"");
				String val = (String) field.get(obj);
				if (obj != null) {
					System.out.print(val);
				} else {
					System.out.print("null");
				}
				System.out.print("\"/>");

			} else if (Collection.class.isAssignableFrom(field.getType())) {
				Collection c = (Collection) field.get(obj);
				System.out.print("<Collection " + fieldName + ">");
				if (c != null) {
					for (Object co : c) {
						System.out.print("<item value=\"");
						if (co != null) {
							System.out.print(co);
						} else {
							System.out.print("null");
						}
						System.out.print("/>");
					}
					System.out.print("</Collection>");
				}
			} else if (Map.class.isAssignableFrom(field.getType())) {
				Map m = (Map) field.get(obj);
				System.out.print("<Map " + fieldName + ">");
				if (m != null) {
					for (Object key : m.keySet()) {
						if (key != null) {
							System.out.print("<Item key=\"" + key + "\">");
							privDObjectSysOut(m.get(key), false);
						} else {
							System.out.print("<Item key=\"null\">");
						}
						System.out.print("</item>");
					}
					System.out.print("</Map>");
				}
			} else {
				System.out.print("<Field " + fieldName + " value=\"");
				Object val = field.get(obj);
				if (val != null) {
					System.out.print(val);
				} else {
					System.out.print("null");
				}
				System.out.print("\"/>");
			}
			if (newline)
				System.out.println();
		}
	}
}
