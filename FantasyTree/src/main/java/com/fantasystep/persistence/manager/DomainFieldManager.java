package com.fantasystep.persistence.manager;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fantasystep.annotation.AnnotationsParser;
import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.annotation.SerializationType;
import com.fantasystep.annotation.Storage;
import com.fantasystep.domain.Node;
import com.fantasystep.utils.ClassUtil;
import com.fantasystep.utils.DateUtil;
import com.fantasystep.utils.EncryptionUtil;
import com.fantasystep.utils.NodeClassUtil;

public class DomainFieldManager {
	public static final String DELIMITER = "%&&%";
	private static DomainFieldManager domainFieldManager;

	public static DomainFieldManager getInstance() {
		if (domainFieldManager == null)
			domainFieldManager = new DomainFieldManager();
		return domainFieldManager;
	}

	public Map<String, Object> convertFromDomainToMap(Node node) {
		Map<String, Object> map = new HashMap<String, Object>();
		for(Field field : NodeClassUtil.getAllNodeFields(node.getClass()))
			try {
				FieldAttributeAccessor accessor = AnnotationsParser.getAttributes(node.getClass(), field.getName());
				if(accessor.getSerializationType() == SerializationType.DATE)
					map.put(field.getName(), DateUtil.STARDARD_DATE_FORMAT.format(field.get(node)));
				else if(accessor.getSerializationType() == SerializationType.TIMESTAMP)
					map.put(field.getName(), DateUtil.STARDARD_TIMESTAMP_FORMAT.format(field.get(node)));
				else if(field.getType().equals(UUID.class) && field.get(node) != null)
					map.put(field.getName(), field.get(node).toString());
				else if(accessor.getEncrypted())
					map.put(field.getName(), EncryptionUtil.digest("MD5", field.get(node).toString()));
				else map.put(field.getName(), field.get(node));
				
			} catch (IllegalArgumentException | IllegalAccessException e) {
			}
		return map;
	}

	public Node convertFromMapToDomain(Node node, Map<String, Object> value) {
		if(node == null || value == null)
			return null;
		for(Field field : NodeClassUtil.getAllNodeFields(node.getClass())) {
			try {
				FieldAttributeAccessor accessor = AnnotationsParser.getAttributes(node.getClass(), field.getName());
				if(value.containsKey(field.getName())) {
					field.set(node, getValue(field, value.get(field.getName()), accessor));
				} else {
					String storageName = accessor.getStorageName();
					if(storageName != null)
						field.set(node, getValue(field, value.get(storageName), accessor));
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return node;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object getValue(Field field, Object value, FieldAttributeAccessor accessor) {
		if(value == null)
			return null;
		if(accessor.getEnumType() != null && !accessor.getEnumType().equals(Enum.class))
			return Enum.valueOf((Class<? extends Enum>)accessor.getEnumType(), value.toString());
		if(field.getType().equals(UUID.class))
			return UUID.fromString(value.toString());
		if(value.getClass().equals(byte[].class) && accessor.getEncrypted())
			return new String((byte[])value);
		if(field.getType().equals(Class.class))
			try {
				if(value.toString().startsWith("class "))
					return Class.forName(value.toString().substring(5));
				else return Class.forName(value.toString());
			} catch (ClassNotFoundException e1) {
				return null;
			}
		if (List.class.isAssignableFrom(field.getType())) {
			return ClassUtil.fromStringToCollection(ArrayList.class, accessor.getListType(), value.toString());
		} else if (Set.class.isAssignableFrom(field.getType())) {
			return ClassUtil.fromStringToCollection(HashSet.class, accessor.getListType(), value.toString());
		}
		
		SerializationType type = accessor.getSerializationType();
		if(type == SerializationType.USE_FIELD_TYPE) {
			if(field.getType().equals(Boolean.class))
				type = SerializationType.BOOLEAN;
			else if(field.getType().equals(Date.class))
				type = SerializationType.DATE;
			else if(field.getType().equals(Long.class) || field.getType().equals(Integer.class))
				type = SerializationType.INTEGER;
			else if(field.getType().equals(Float.class))
				type = SerializationType.DECIMAL;
			else if(field.getType() == byte[].class)
				return new String((byte[])value);
			else if(List.class.isAssignableFrom(field.getType()) || Set.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(field.getType()))
				return value;
			else type = SerializationType.STRING;
		}
		switch(type) {
		case BINARY:
			return value.toString().toCharArray();
		case BOOLEAN:
			return Boolean.valueOf(value.toString());
		case DATE:
			try {
				return DateUtil.STARDARD_DATE_FORMAT.parse(value.toString());
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		case TIMESTAMP:
			try {
				return DateUtil.STARDARD_DATE_FORMAT.parse(value.toString());
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		case DECIMAL:
			return Double.parseDouble(value.toString());
		case INTEGER:
			return Long.parseLong(value.toString());
		case STRING:
			return value.toString();
		default:
			return null;
		}
	}

	public Storage getStorageForSubNode() {
		return Storage.MYSQL;
	}

	public Map<String, Object> filterMapByStorage(Map<String, Object> nodeMap,
			Storage storage, Class<? extends Node> clazz) {
		Map<String, Object> map = new HashMap<String, Object>();
		for(Field field : NodeClassUtil.getAllNodeFields(clazz))
			try {

				FieldAttributeAccessor accessor = AnnotationsParser.getAttributes(clazz, field.getName());
				if((accessor.getStorage() == storage || accessor.getSharedKey()) && nodeMap.containsKey(field.getName())) {
					if(storage == Storage.LDAP && accessor.getStorageName() != null){
						map.put(accessor.getStorageName(), nodeMap.get(field.getName()));
					} else map.put(field.getName(), nodeMap.get(field.getName()));
				} 
				//Special case for LDAP accept all fields with a storageName as a property. 
				else if(storage == Storage.LDAP && accessor.getStorageName() != null && nodeMap.containsKey(field.getName())){
					map.put(accessor.getStorageName(), nodeMap.get(field.getName()));
				} 
			} catch (IllegalArgumentException e) {
			}
		return map;
	}

	public Map<Class<? extends Node>,Map<String,Class<?>>> lookupDomainClassByStorage(Storage storage) {
		Map<Class<? extends Node>,Map<String,Class<?>>> map = new HashMap<Class<? extends Node>,Map<String,Class<?>>>();
		for(Class<? extends Node> nodeClazz : NodeClassUtil.getNodeClassInJVM()) {
			for(Field field : NodeClassUtil.getAllNodeFields(nodeClazz)) {
				FieldAttributeAccessor accessor = AnnotationsParser.getAttributes(nodeClazz, field.getName());
				if(accessor != null && (accessor.getStorage() == storage || accessor.getSharedKey())) {
					if(!map.containsKey(nodeClazz))
						map.put(nodeClazz, new HashMap<String,Class<?>>());
					if(storage == Storage.LDAP && accessor.getStorageName() != null){
						map.get(nodeClazz).put(accessor.getStorageName(), field.getType());
					} else map.get(nodeClazz).put(field.getName(), field.getType());
				}
			}
		}
		if(storage != Storage.TREE)
			map.remove(Node.class);
		else {
			Map<String, Class<?>> tmp = map.get(Node.class);
			map.clear();
			if(tmp != null)
				map.put(Node.class, tmp);
		}
		return map;
	}

	public Map<String, Class<?>> lookupDomainFieldByStorage(
			Class<? extends Node> nodeClass, Storage storage) {
		Map<String, Class<?>> map = new HashMap<String, Class<?>>();
		for(Field field : NodeClassUtil.getAllNodeFields(nodeClass)) {
			FieldAttributeAccessor accessor = AnnotationsParser.getAttributes(nodeClass, field.getName());
			if(accessor != null && (accessor.getStorage() == storage || accessor.getSharedKey())) {
				if(storage == Storage.LDAP && accessor.getStorageName() != null){
					map.put(accessor.getStorageName(), field.getType());
				} else map.put(field.getName(), field.getType());
			}
		}
		return map;
	}
}
