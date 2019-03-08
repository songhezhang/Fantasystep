package com.fantasystep.utils;

import java.lang.reflect.Method;

public class BeanCopierBeanClassDescription {
	private String fieldName;
	private Method method;
	private Class<?> beanCreationClass;

	public BeanCopierBeanClassDescription(String fieldName, Method method,
			Class<?> beanCreationClass) {
		super();
		this.fieldName = fieldName;
		this.method = method;
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

	public Class<?> getBeanCreationClass() {
		return beanCreationClass;
	}

	public void setBeanCreationClass(Class<?> beanCreationClass) {
		this.beanCreationClass = beanCreationClass;
	}
}
