package com.fantasystep.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;

@SuppressWarnings("unchecked")
public class R2MBeanCopier {
	private BeanCopierClassAnalyser sourceClassAnalyser;
	private BeanCopierClassAnalyser destinationClassAnalyser;
	private List<String> extraFieldsToExclude;

	// static Logger logger = Logger.getLogger(R2MBeanCopier.class);

	public R2MBeanCopier() {

	}

	private void privCopyBean(Object source, Object destination) {
		sourceClassAnalyser = BeanCopierClassAnalyserFactory.getInstance()
				.getBeanCopierClassAnalyzer(source);
		destinationClassAnalyser = BeanCopierClassAnalyserFactory.getInstance()
				.getBeanCopierClassAnalyzer(destination);
		privCopyProperties(source, destination);
		copyCollections(source, destination);
		copyBeans(source, destination);
	}

	private void setExtraFieldsToExclude(List<String> extraFieldsToExclude) {
		this.extraFieldsToExclude = extraFieldsToExclude;
	}

	public static void copyBean(Object source, Object destination) {
		R2MBeanCopier bcjs = new R2MBeanCopier();
		bcjs.privCopyBean(source, destination);
	}

	public static void copyBean(Object source, Object destination,
			List<String> fieldsToExclude) {
		R2MBeanCopier bcjs = new R2MBeanCopier();
		bcjs.setExtraFieldsToExclude(fieldsToExclude);
		bcjs.privCopyBean(source, destination);
	}

	public static void copyBean(Object source, Object destination,
			String[] fieldsToExclude) {
		if (fieldsToExclude != null && fieldsToExclude.length > 0) {
			List<String> fte = Arrays.asList(fieldsToExclude);
			R2MBeanCopier.copyBean(source, destination, fte);
			return;
		} else {
			R2MBeanCopier.copyBean(source, destination);
			return;
		}
	}

	public static void copyProperties(Object source, Object destination) {
		R2MBeanCopier bcjs = new R2MBeanCopier();
		bcjs.initPrivCopyProperties(source, destination);

	}

	private void initPrivCopyProperties(Object source, Object destination) {
		sourceClassAnalyser = BeanCopierClassAnalyserFactory.getInstance()
				.getBeanCopierClassAnalyzer(source);
		destinationClassAnalyser = BeanCopierClassAnalyserFactory.getInstance()
				.getBeanCopierClassAnalyzer(destination);
		privCopyProperties(source, destination);

	}

	private void privCopyProperties(Object source, Object destination) {
		List<String> fieldsToExclude = sourceClassAnalyser
				.getGetterFieldsToExcludeAsList();
		if (this.extraFieldsToExclude != null)
			fieldsToExclude.addAll(this.extraFieldsToExclude);

		String[] ignoreProperties = fieldsToExclude
				.toArray(new String[fieldsToExclude.size()]);
		BeanUtils.copyProperties(source, destination, ignoreProperties);
	}

	private void copyCollections(Object source, Object destination) {
		Map<String, BeanCopierCollectionDescription> srcFieldMtdMap = sourceClassAnalyser
				.getCollectionGetterMap();
		Map<String, BeanCopierCollectionDescription> dstFieldMtdMap = destinationClassAnalyser
				.getCollectionSetterMap();

		for (String fieldName : srcFieldMtdMap.keySet()) {
			if (dstFieldMtdMap.containsKey(fieldName)) {
				BeanCopierCollectionDescription dstBccd = dstFieldMtdMap
						.get(fieldName);
				BeanCopierCollectionDescription srcBccd = srcFieldMtdMap
						.get(fieldName);
				Class<?> collectionCreationClass = dstBccd
						.getCollectionCreationClass();
				Class<?> beanCreationClass = dstBccd.getBeanCreationClass();
				try {
					Collection<Object> dstCollection = (Collection<Object>) collectionCreationClass
							.newInstance();
					Object[] params = {};
					Collection<?> srcCollection = (Collection<?>) srcBccd
							.getMethod().invoke(source, params);
					if (srcCollection != null) {
						for (Object src : srcCollection) {
							Object dst = beanCreationClass.newInstance();
							copyBean(src, dst);
							dstCollection.add(dst);
						}
					}
					dstBccd.getMethod().invoke(destination, dstCollection);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void copyBeans(Object source, Object destination) {
		Map<String, BeanCopierBeanClassDescription> sourceBeanMap = sourceClassAnalyser
				.getBeanclassGetterMap();
		Map<String, BeanCopierBeanClassDescription> destinationBeanMap = destinationClassAnalyser
				.getBeanclassSetterMap();

		for (String fieldName : sourceBeanMap.keySet()) {
			if (destinationBeanMap.containsKey(fieldName)) {
				BeanCopierBeanClassDescription destinationBcbcd = destinationBeanMap
						.get(fieldName);
				Class<?> destinationBeanCreationClass = destinationBcbcd
						.getBeanCreationClass();
				if (destinationBeanCreationClass.isEnum()) {
					try {
						BeanCopierBeanClassDescription sourceBcbcd = sourceBeanMap
								.get(fieldName);
						Object[] sourceParams = {};
						Object sourceObject = sourceBcbcd.getMethod().invoke(
								source, sourceParams);
						Object[] destParams = { sourceObject };
						destinationBcbcd.getMethod().invoke(destination,
								destParams);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				} else {
					try {
						Object destinationObject;
						destinationObject = destinationBeanCreationClass
								.newInstance();
						BeanCopierBeanClassDescription sourceBcbcd = sourceBeanMap
								.get(fieldName);
						Object[] sourceParams = {};
						Object sourceObject = sourceBcbcd.getMethod().invoke(
								source, sourceParams);
						if (sourceObject != null) {
							copyBean(sourceObject, destinationObject);
						} else {
							destinationObject = null;
						}
						Object[] destParams = { destinationObject };
						destinationBcbcd.getMethod().invoke(destination,
								destParams);

					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static Collection copyCollection(Collection src, Collection dst,
			Class<?> dstCreationClass) {
		R2MBeanCopier bc = new R2MBeanCopier();
		return bc.privCopyCollection(src, dst, dstCreationClass);
	}

	@SuppressWarnings("rawtypes")
	private Collection privCopyCollection(Collection src, Collection dst,
			Class<?> dstCreationClass) {
		for (Object srcObj : src) {
			Object dstObj;
			try {
				dstObj = dstCreationClass.newInstance();
				R2MBeanCopier.copyBean(srcObj, dstObj);
				dst.add(dstObj);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return dst;
	}
}
