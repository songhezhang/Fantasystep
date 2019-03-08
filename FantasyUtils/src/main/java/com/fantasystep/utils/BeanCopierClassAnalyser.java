package com.fantasystep.utils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanCopierClassAnalyser {

	private static final String beanPkgIndication = "com.fantasystep.domain";
	private Object object;
	private List<Method> setters = new ArrayList<Method>();
	private List<Method> getters = new ArrayList<Method>();
	private List<Method> collectionSetters = new ArrayList<Method>();
	private Map<String, BeanCopierCollectionDescription> collectionSetterMap = new HashMap<String, BeanCopierCollectionDescription>();
	private List<Method> collectionGetters = new ArrayList<Method>();
	private Map<String, BeanCopierCollectionDescription> collectionGetterMap = new HashMap<String, BeanCopierCollectionDescription>();
	private List<Method> beanSetters = new ArrayList<Method>();
	private Map<String, BeanCopierBeanClassDescription> beanclassSetterMap = new HashMap<String, BeanCopierBeanClassDescription>();
	private List<Method> beanGetters = new ArrayList<Method>();
	private Map<String, BeanCopierBeanClassDescription> beanclassGetterMap = new HashMap<String, BeanCopierBeanClassDescription>();
	private List<String> getterFieldsToExclude = new ArrayList<String>();
	private List<String> setterFieldsToExclude = new ArrayList<String>();

	public BeanCopierClassAnalyser(Object object) {
		this.object = object;
	}

	public String[] getGetterFieldsToExclude() {
		String[] strArr = new String[getterFieldsToExclude.size()];
		System.arraycopy(getterFieldsToExclude.toArray(), 0, strArr, 0,
				getterFieldsToExclude.size());
		return strArr;
	}

	public String[] getSetterFieldsToExclude() {
		String[] strArr = new String[setterFieldsToExclude.size()];
		System.arraycopy(setterFieldsToExclude.toArray(), 0, strArr, 0,
				setterFieldsToExclude.size());
		return strArr;
	}

	public List<String> getGetterFieldsToExcludeAsList() {
		return getterFieldsToExclude;
	}

	public List<String> getSetterFieldsToExcludeAsList() {
		return setterFieldsToExclude;
	}

	public static BeanCopierClassAnalyser getAnalyser(Object object) {
		return new BeanCopierClassAnalyser(object);
	}

	public void analyse() {
		findGetSetMethods();
		findCollectionMethods();
		findBeanMethods();
	}

	private void findGetSetMethods() {
		for (Method mtd : object.getClass().getMethods()) {

			if (mtd.getName().startsWith("set")
					&& mtd.getParameterTypes().length == 1)
				setters.add(mtd);

			if (mtd.getName().startsWith("get")
					&& mtd.getParameterTypes().length == 0)
				getters.add(mtd);
		}
	}

	private void findCollectionMethods() {
		for (Method mtd : setters) {
			for (Class<?> type : mtd.getParameterTypes()) {
				if (Collection.class.isAssignableFrom(type)) {
					collectionSetters.add(mtd);
					String fieldName = convertMethodNameToField(mtd);
					BeanCopierCollectionDescription description = new BeanCopierCollectionDescription(
							fieldName, mtd, getCollectionCreationClass(type),
							getGenericsClassTypeForSetter(mtd));
					collectionSetterMap.put(fieldName, description);
					setterFieldsToExclude.add(fieldName);

				}
			}
		}
		setters.removeAll(collectionSetters);
		for (Method mtd : getters) {
			Class<?> type = mtd.getReturnType();
			if (Collection.class.isAssignableFrom(type)) {

				collectionGetters.add(mtd);
				String fieldName = convertMethodNameToField(mtd);
				BeanCopierCollectionDescription description = new BeanCopierCollectionDescription(
						fieldName, mtd, getCollectionCreationClass(type),
						getGenericsClassTypeForSetter(mtd));
				collectionGetterMap.put(fieldName, description);
				getterFieldsToExclude.add(fieldName);
			}
		}
		getters.removeAll(collectionGetters);
	}

	private Class<?> getCollectionCreationClass(Class<?> type) {
		if (!type.isInterface()) {
			try {
				@SuppressWarnings("unused")
				Object o = type.newInstance();
				return type;
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
		}
		if (Set.class.isAssignableFrom(type)) {
			return HashSet.class;
		}
		if (List.class.isAssignableFrom(type)) {
			return ArrayList.class;
		}
		return null;
	}

	private Class<?> getGenericsClassTypeForSetter(Method setter) {
		Type[] genericParameterTypes = setter.getGenericParameterTypes();
		for (Type genericParameterType : genericParameterTypes) {
			if (genericParameterType instanceof ParameterizedType) {
				ParameterizedType aType = (ParameterizedType) genericParameterType;
				Type[] parameterArgTypes = aType.getActualTypeArguments();
				for (Type parameterArgType : parameterArgTypes) {
					return (Class<?>) parameterArgType;
				}
			}
		}
		return null;
	}

	private String convertMethodNameToField(Method mtd) {
		String fieldName = mtd.getName();
		return fieldName.substring(3, 4).toLowerCase() + fieldName.substring(4);
	}

	private void findBeanMethods() {
		for (Method mtd : setters) {
			for (Class<?> type : mtd.getParameterTypes()) {
				if (type.getName().startsWith(beanPkgIndication)) {
					beanSetters.add(mtd);
					String fieldName = convertMethodNameToField(mtd);
					BeanCopierBeanClassDescription bcbcd = new BeanCopierBeanClassDescription(
							fieldName, mtd, type);
					beanclassSetterMap.put(fieldName, bcbcd);
					setterFieldsToExclude.add(fieldName);
				}
			}
		}
		setters.removeAll(beanSetters);

		for (Method mtd : getters) {
			Class<?> type = mtd.getReturnType();
			if (type.getName().startsWith(beanPkgIndication)) {
				String fieldName = convertMethodNameToField(mtd);
				BeanCopierBeanClassDescription bcbcd = new BeanCopierBeanClassDescription(
						fieldName, mtd, type);
				beanGetters.add(mtd);
				beanclassGetterMap.put(fieldName, bcbcd);
				getterFieldsToExclude.add(convertMethodNameToField(mtd));
			}
		}
		getters.removeAll(beanGetters);
	}

	public Map<String, BeanCopierCollectionDescription> getCollectionSetterMap() {
		return collectionSetterMap;
	}

	public Map<String, BeanCopierCollectionDescription> getCollectionGetterMap() {
		return collectionGetterMap;
	}

	public Map<String, BeanCopierBeanClassDescription> getBeanclassSetterMap() {
		return beanclassSetterMap;
	}

	public Map<String, BeanCopierBeanClassDescription> getBeanclassGetterMap() {
		return beanclassGetterMap;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" Setters: " + setters.toString());
		sb.append("\n Getters: " + getters.toString());
		sb.append("\n Collection Setters: " + collectionSetters.toString());
		sb.append("\n Collection Getters: " + collectionGetters.toString());
		sb.append("\n Bean Setters" + beanSetters.toString());
		sb.append("\n Bean Getters" + beanGetters.toString());
		sb.append("\n Setter Fields to Exclude: "
				+ setterFieldsToExclude.toString());
		sb.append("\n Getter Fields to Exclude: "
				+ getterFieldsToExclude.toString());

		return sb.toString();
	}
}
