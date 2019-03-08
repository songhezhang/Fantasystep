package com.fantasystep.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.fantasystep.domain.Node;
import com.fantasystep.utils.NodeClassUtil;

public class AnnotationsParser {

	public static FieldAttributeAccessor getAttributes(Class<?> clazz,
			String fieldName) {
		Field field = NodeClassUtil.getField(clazz, fieldName);
		if(field == null)
			return null;
		FieldAttributeAccessor accessor = new FieldAttributeAccessor();
		
		if(field.getDeclaredAnnotations().length == 0)
			return null;
		for(Annotation annotation : field.getDeclaredAnnotations()) {
			for(Method method : annotation.getClass().getDeclaredMethods()) {
				Field f = null;
				try {
					f = FieldAttributeAccessor.class.getDeclaredField(method.getName());
					f.setAccessible(true);
					Object val = method.invoke(annotation, new Object[]{});
					if(val != null && !val.equals(void.class) && !val.equals(""))
						f.set(accessor, val);
				} catch (NoSuchFieldException e) {
					continue;
				} catch (SecurityException e) {
					continue;
				} catch (IllegalArgumentException e) {
					continue;
				} catch (IllegalAccessException e) {
					continue;
				} catch (InvocationTargetException e) {
					continue;
				}
			}
		}
		return accessor;
	}

	public static DomainAttributeAccessor getAttributes(Class<?> listType) {
		DomainAttributeAccessor accessor = new DomainAttributeAccessor();
		for(Annotation annotation : listType.getDeclaredAnnotations()) {
			for(Method method : annotation.getClass().getDeclaredMethods()) {
				Field f = null;
				try {
					f = DomainAttributeAccessor.class.getDeclaredField(method.getName());
					f.setAccessible(true);
					Object val = method.invoke(annotation, new Object[]{});
					if(val != null && !val.equals(void.class) && !val.equals(""))
						f.set(accessor, val);
				} catch (NoSuchFieldException e) {
					continue;
				} catch (SecurityException e) {
					continue;
				} catch (IllegalArgumentException e) {
					continue;
				} catch (IllegalAccessException e) {
					continue;
				} catch (InvocationTargetException e) {
					continue;
				}
			}
		}
		return accessor;
	}

	public static List<String> getUIControlFieldNames(Class<? extends Node> clazz) {
		List<String> list = new ArrayList<String>();
		for(Field field : NodeClassUtil.getAllNodeFields(clazz))
			if(field.getAnnotation(FantasyView.class) != null)
				list.add(field.getName());
		return list;
	}

	public static List<Field> getUIControlField(Class<? extends Node> clazz) {
		List<Field> list = new ArrayList<Field>();
		for(Field field : NodeClassUtil.getAllNodeFields(clazz))
			if(field.getAnnotation(FantasyView.class) != null)
				list.add(field);
		return list;
	}

	public static boolean isDocumented(java.lang.reflect.Field field) {
		return false;
	}
}
