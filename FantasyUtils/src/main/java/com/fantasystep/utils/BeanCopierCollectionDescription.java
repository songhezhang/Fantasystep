package com.fantasystep.utils;

import java.lang.reflect.Method;

public class BeanCopierCollectionDescription {
	private String fieldName;
	private Method method;
	private Class<?> collectionCreationClass;
	private Class<?> beanCreationClass;

	public BeanCopierCollectionDescription(String fieldName, Method method,
			Class<?> collectionCreationClass, Class<?> beanCreationClass) {
		super();
		this.fieldName = fieldName;
		this.method = method;
		this.collectionCreationClass = collectionCreationClass;
		this.beanCreationClass = beanCreationClass;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Class<?> getCollectionCreationClass() {
		return collectionCreationClass;
	}

	public void setCollectionCreationClass(Class<?> collectionCreationClass) {
		this.collectionCreationClass = collectionCreationClass;
	}

	public Class<?> getBeanCreationClass() {
		return beanCreationClass;
	}

	public void setBeanCreationClass(Class<?> beanCreationClass) {
		this.beanCreationClass = beanCreationClass;
	}
}
