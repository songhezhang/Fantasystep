package com.fantasystep.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.reflections.Reflections;

import com.fantasystep.annotation.DomainClass;
import com.fantasystep.domain.Node;

public class NodeClassUtil {

	private static final Reflections reflections = new Reflections("com.fantasystep");
	private static Map<String, List<Field>> classFieldsCache = new ConcurrentHashMap<String, List<Field>>();
	private static Map<String, List<Field>> nodeClassFieldsCache = new ConcurrentHashMap<String, List<Field>>();
	private static Map<String, List<Field>> dynamicEntityClassFieldsCache = new ConcurrentHashMap<String, List<Field>>();
	private static Map<String, Class<? extends Node>> dynamicEntityClassMap = new ConcurrentHashMap<String, Class<? extends Node>>();
	
	public static boolean setupDynamicEntityClass(Map<String, Class<? extends Node>> classMap) {
		if(classMap == null || classMap.isEmpty())
			return false;
		dynamicEntityClassFieldsCache.clear();
		for(Class<?> clazz : classMap.values())
			dynamicEntityClassFieldsCache.put(clazz.getName(), getAllFieldsInRealTime(clazz));
		dynamicEntityClassMap = classMap;
		return true;
	}
	
	public static Class<? extends Node> getDynamicEntityClassByFullName(String fullName) {
		return dynamicEntityClassMap.get(fullName);
	}
	
	public static List<Class<? extends Node>> getValidDynamicEntityChildren(Class<? extends Node> clazz) {
		List<Class<? extends Node>> list = new ArrayList<Class<? extends Node>>();
		for(Class<? extends Node> c : dynamicEntityClassMap.values())
			if(Arrays.asList(c.getAnnotation(DomainClass.class).validParents()).contains(clazz))
				list.add(c);
		return list;
	}
	
	public static List<Field> getAllFieldsFromString(String key) {
		if(key == null || key.isEmpty())
			return null;
		if(nodeClassFieldsCache.containsKey(key))
			return nodeClassFieldsCache.get(key);
		else if(classFieldsCache.containsKey(key))
			classFieldsCache.get(key);
		else if(dynamicEntityClassFieldsCache.containsKey(key))
			dynamicEntityClassFieldsCache.get(key);
		return null;
	}
	
	public static List<Field> getAllFields(Class<?> clazz) {
		if(classFieldsCache.containsKey(clazz.getName()))
			return classFieldsCache.get(clazz.getName());
		else if(dynamicEntityClassFieldsCache.containsKey(clazz.getName()))
			return dynamicEntityClassFieldsCache.get(clazz.getName());
		else {
			List<Field> list = getAllFieldsInRealTime(clazz);
			classFieldsCache.put(clazz.getName(), list);
			return list;
		}
	}
	
	public static List<String> getAllNodeFieldNames(Class<? extends Node> nodeClazz) {
		List<String> strs = new ArrayList<String>();
		for(Field f : getAllNodeFields(nodeClazz))
			strs.add(f.getName());
		return strs;
	}
	
	public static List<Field> getAllFieldsInRealTime(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		for (Field field : clazz.getDeclaredFields())
		{
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			if(field.getAnnotations().length == 0)
				continue;
			field.setAccessible(true);
			fields.add(field);
		}
		if (clazz.getSuperclass() != null)
			fields.addAll(getAllFields(clazz.getSuperclass()));
		return fields;
	}
	
	public static List<Field> getAllNodeFields(Class<? extends Node> nodeClass) {
		if(nodeClassFieldsCache.containsKey(nodeClass.getName()))
			return nodeClassFieldsCache.get(nodeClass.getName());
		else if(dynamicEntityClassFieldsCache.containsKey(nodeClass.getName()))
			return dynamicEntityClassFieldsCache.get(nodeClass.getName());
		else {
			List<Field> list = getAllNodeFieldsInRealTime(nodeClass);
			nodeClassFieldsCache.put(nodeClass.getName(), list);
			return list;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Field> getAllNodeFieldsInRealTime(Class<? extends Node> nodeClass) {
		List<Field> fields = new ArrayList<Field>();
		for (Field field : nodeClass.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			if(field.getAnnotations().length == 0)
				continue;
			field.setAccessible(true);
			fields.add(field);
		}
		if (nodeClass.getSuperclass() != null) {
			if (nodeClass.getSuperclass().equals(Node.class))
				fields.addAll(getAllNodeFields(Node.class));
			else
				fields.addAll(getAllNodeFields((Class<? extends Node>) nodeClass
						.getSuperclass()));
		}
		return fields;
	}
	
	public static Field getField(Class<?> clazz, String fieldName) {
		for (Field field : getAllFields(clazz))
			if(field.getName().equals(fieldName))
				return field;
		return null;
	}
	
	public static<T> Set<Class<? extends T>> getSubClassesInJVM(Class<T> clazz)
	{
		Set<Class<? extends T>> set = new HashSet<Class<? extends T>>();
		for(Class<? extends T> c : reflections.getSubTypesOf(clazz))
			if(!Modifier.isAbstract(c.getModifiers()))
				set.add(c);
		return set;
	}
	
	public static<T> Set<Class<? extends T>> getSubClassesInJVM(Class<T> clazz, boolean include)
	{
		Set<Class<? extends T>> set = reflections.getSubTypesOf(clazz);
		if(include)
			set.add(clazz);
		return set;
	}
	
	public static Set<Class<? extends Node>> getNodeClassInJVM()
	{
		Set<Class<? extends Node>> set = getSubClassesInJVM(Node.class);
		set.add(Node.class);
		return set;
	}
	
	public static Node getSerializationNode(Node node) {
		Node n = new Node();
		n.setId(node.getId());
		n.setParentId(node.getParentId());
		n.setSerializationNode(JSON2NodeUtil.node2Json(node));
		return n;
	}
	
	public static Node getDeserializationNode(Node node) {
		return JSON2NodeUtil.json2Node(node.getSerializationNode(), NodeClassUtil.getDynamicEntityClassByFullName(new JSONObject(node.getSerializationNode()).get("type").toString()));
	}
	
	public static void main(String[] args) throws IOException {
//		Reflections reflections = new Reflections("com.fantasystep");
//
////		Set<Class<? extends Node>> subTypes = reflections.getSubTypesOf(Node.class);
////		for(Class<? extends Node> clazz : subTypes)
////			logger.info(clazz.getName());
//		Set<Class<? extends Object>> objTypes = reflections.getSubTypesOf(Object.class);
//		for(Class<? extends Object> clazz : objTypes)
//			logger.info(clazz.getName());
//		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(FantasyStep.class);
//		for(Class<?> clazz : annotated)
//			logger.info(clazz.getName());
//		long start = System.currentTimeMillis();
//		logger.info(getAllNodeFieldNames(Node.class));
//		logger.info((System.currentTimeMillis() - start) / 1000.0);
//		logger.info(Node.class.getName());
	}
}
