package com.fantasystep.utils;

import java.util.HashMap;
import java.util.Map;

public class BeanCopierClassAnalyserFactory {
	private static BeanCopierClassAnalyserFactory instance = null;

	protected BeanCopierClassAnalyserFactory() {
	}

	public static BeanCopierClassAnalyserFactory getInstance() {
		if (instance == null)
			instance = new BeanCopierClassAnalyserFactory();
		return instance;
	}

	private Map<Class<?>, BeanCopierClassAnalyser> beanCopierClassAnalysers = new HashMap<Class<?>, BeanCopierClassAnalyser>();

	public BeanCopierClassAnalyser getBeanCopierClassAnalyzer(Object object) {
		if (beanCopierClassAnalysers.containsKey(object.getClass()))
			return beanCopierClassAnalysers.get(object.getClass());
		else {
			BeanCopierClassAnalyser bcca = new BeanCopierClassAnalyser(object);
			bcca.analyse();
			this.beanCopierClassAnalysers.put(object.getClass(), bcca);
			return this.beanCopierClassAnalysers.get(object.getClass());
		}
	}
}
