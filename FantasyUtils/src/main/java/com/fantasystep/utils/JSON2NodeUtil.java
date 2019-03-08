package com.fantasystep.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fantasystep.annotation.FantasyStep;
import com.fantasystep.domain.Node;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSON2NodeUtil {

	@SuppressWarnings("unchecked")
	public static <T extends Node> T json2Node(String jsonString,
			Class<T> nodeClass) {
		JSONObject fieldsJson = new JSONObject(jsonString);
		T node = null;

		try {
			node = nodeClass.newInstance();
			for (Field field : NodeClassUtil.getAllNodeFields(nodeClass)) {
				if (fieldsJson.isNull(field.getName()))
					continue;
				if (field.getType().equals(Boolean.class))
					field.set(node, fieldsJson.getBoolean(field.getName()));
				else if (field.getType().equals(String.class))
					field.set(node, fieldsJson.getString(field.getName()));
				else if (field.getType().equals(Double.class)
						|| field.getType().equals(Float.class))
					field.set(node, fieldsJson.getDouble(field.getName()));
				else if (field.getType().equals(Long.class))
					field.set(node, fieldsJson.getLong(field.getName()));
				else if (field.getType().equals(Integer.class))
					field.set(node, fieldsJson.getInt(field.getName()));
				else if (field.getType().equals(Date.class))
					field.set(node,
							new Date(fieldsJson.getLong(field.getName())));
				else if (field.getType().equals(UUID.class))
					field.set(node, UUID.fromString(fieldsJson.getString(field
							.getName())));
				else if (Enum.class.isAssignableFrom(field.getType()))
					field.set(node, Enum.valueOf(
							field.getAnnotation(FantasyStep.class).enumType(),
							fieldsJson.getString(field.getName())));
				else if (Node.class.isAssignableFrom(field.getType()))
					field.set(
							node,
							json2Node(fieldsJson.getJSONObject(field.getName())
									.toString(), (Class<? extends Node>) field
									.getType()));
				else if (Collection.class.isAssignableFrom(field.getType())) {
					if (List.class.isAssignableFrom(field.getType())) {
						List<Object> list = new ArrayList<Object>();

						JSONArray array = fieldsJson.getJSONArray(field
								.getName());
						if (field.getAnnotation(FantasyStep.class).listType() == HashMap.class) {
							list.addAll(JSONUtil.toList(array));
						} else {
							for (int i = 0; i < array.length(); i++)
								if (!array.get(i).toString().isEmpty())
									list.add(json2Object(
											array.get(i).toString(),
											field.getAnnotation(
													FantasyStep.class)
													.listType()));
						}
						field.set(node, list);
					} else if (Set.class.isAssignableFrom(field.getType())) {
						Set<Object> set = new HashSet<Object>();
						JSONArray array = fieldsJson.getJSONArray(field
								.getName());
						if (field.getAnnotation(FantasyStep.class).listType() == HashMap.class) {
							set.addAll(JSONUtil.toList(array));
						} else {
							for (int i = 0; i < array.length(); i++)
								if (!array.get(i).toString().isEmpty())
									set.add(json2Object(
											array.get(i).toString(),
											field.getAnnotation(
													FantasyStep.class)
													.listType()));
						}
						field.set(node, set);
					}
				} else if (Map.class.isAssignableFrom(field.getType())) {
					Map<Object, Object> map = null;
					JSONObject object = fieldsJson.getJSONObject(field
							.getName());
					try {
						map = new ObjectMapper().readValue(new StringReader(
								object.toString()), HashMap.class);
						if (field.getAnnotation(FantasyStep.class).mapType().length == 1) {
							Class<?> valueClass = field.getAnnotation(
									FantasyStep.class).mapType()[0];
							if (Node.class.isAssignableFrom(valueClass)) {
								Map<Object, Node> newMap = new HashMap<Object, Node>();
								for (Entry<Object, Object> entry : map
										.entrySet()) {
									newMap.put(
											entry.getKey(),
											json2Node(
													object.getJSONObject(
															entry.getKey()
																	.toString())
															.toString(),
													(Class<? extends Node>) valueClass));
								}
								field.set(node, newMap);
							} else {
								Map<Object, Object> newMap = new HashMap<Object, Object>();
								for (Entry<Object, Object> entry : map
										.entrySet()) {
									newMap.put(
											entry.getKey(),
											json2Object(
													object.getJSONObject(
															entry.getKey()
																	.toString())
															.toString(),
													valueClass));
								}
								field.set(node, newMap);
							}
						} else if (field.getAnnotation(FantasyStep.class)
								.mapType().length == 2) {
							Class<?> keyClass = field.getAnnotation(
									FantasyStep.class).mapType()[0];
							Class<?> valueClass = field.getAnnotation(
									FantasyStep.class).mapType()[1];
							if (Node.class.isAssignableFrom(keyClass)
									&& Node.class.isAssignableFrom(valueClass)) {
								Map<Node, Node> newMap = new HashMap<Node, Node>();
								for (Entry<Object, Object> entry : map
										.entrySet())
									newMap.put(
											json2Node(
													entry.getKey().toString(),
													(Class<? extends Node>) keyClass),
											json2Node(
													object.getJSONObject(
															entry.getKey()
																	.toString())
															.toString(),
													(Class<? extends Node>) valueClass));
								field.set(node, newMap);
							} else if (Node.class.isAssignableFrom(keyClass)) {
								Map<Node, Object> newMap = new HashMap<Node, Object>();
								for (Entry<Object, Object> entry : map
										.entrySet())
									newMap.put(
											json2Node(
													entry.getKey().toString(),
													(Class<? extends Node>) keyClass),
											json2Object(
													object.getJSONObject(
															entry.getKey()
																	.toString())
															.toString(),
													valueClass));
								field.set(node, newMap);
							} else if (Node.class.isAssignableFrom(valueClass)) {
								Map<Object, Node> newMap = new HashMap<Object, Node>();
								for (Entry<Object, Object> entry : map
										.entrySet())
									newMap.put(
											json2Object(entry.getKey()
													.toString(), keyClass),
											json2Node(
													object.getJSONObject(
															entry.getKey()
																	.toString())
															.toString(),
													(Class<? extends Node>) valueClass));
								field.set(node, newMap);
							} else {
								Map<Object, Object> newMap = new HashMap<Object, Object>();
								for (Entry<Object, Object> entry : map
										.entrySet())
									newMap.put(
											json2Object(entry.getKey()
													.toString(), keyClass),
											json2Object(
													object.getJSONObject(
															entry.getKey()
																	.toString())
															.toString(),
													valueClass));
								field.set(node, newMap);
							}
						} else
							field.set(node, map);
					} catch (JsonParseException e) {
						e.printStackTrace();
					} catch (JsonMappingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return node;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T json2Object(String jsonString, Class<T> clazz) {
		if (clazz.equals(Boolean.class))
			return (T) (Object) Boolean.parseBoolean(jsonString);
		else if (clazz.equals(String.class))
			return (T) (Object) jsonString;
		else if (clazz.equals(Double.class) || clazz.equals(Float.class))
			return (T) (Object) Double.parseDouble(jsonString);
		else if (clazz.equals(Long.class))
			return (T) (Object) Long.parseLong(jsonString);
		else if (clazz.equals(Integer.class))
			return (T) (Object) Integer.parseInt(jsonString);
		else if (clazz.equals(Date.class))
			return (T) (Object) new Date(Long.parseLong(jsonString, 0));
		else if (clazz.equals(UUID.class))
			return (T) (Object) UUID.fromString(jsonString);
		else if (clazz.equals(Object.class))
			return (T) (Object) jsonString;
		else if (Enum.class.isAssignableFrom(clazz))
			return (T) (Object) Enum.valueOf((Class<? extends Enum>) clazz,
					jsonString);

		JSONObject fieldsJson = new JSONObject(jsonString);
		T obj = null;

		try {
			obj = clazz.newInstance();
			for (Field field : NodeClassUtil.getAllFields(clazz)) {
				if (fieldsJson.isNull(field.getName()))
					continue;
				if (field.getType().equals(Boolean.class))
					field.set(obj, fieldsJson.getBoolean(field.getName()));
				else if (field.getType().equals(String.class))
					field.set(obj, fieldsJson.getString(field.getName()));
				else if (field.getType().equals(Double.class)
						|| field.getType().equals(Float.class))
					field.set(obj, fieldsJson.getDouble(field.getName()));
				else if (field.getType().equals(Long.class))
					field.set(obj, fieldsJson.getLong(field.getName()));
				else if (field.getType().equals(Integer.class))
					field.set(obj, fieldsJson.getInt(field.getName()));
				else if (field.getType().equals(Date.class))
					field.set(obj,
							new Date(fieldsJson.getLong(field.getName())));
				else if (field.getType().equals(UUID.class))
					field.set(obj, UUID.fromString(fieldsJson.getString(field
							.getName())));
				else if (Enum.class.isAssignableFrom(field.getType()))
					field.set(obj, Enum.valueOf(
							field.getAnnotation(FantasyStep.class).enumType(),
							fieldsJson.getString(field.getName())));
				else if (Node.class.isAssignableFrom(field.getType()))
					field.set(
							obj,
							json2Node(fieldsJson.getJSONObject(field.getName())
									.toString(), (Class<? extends Node>) field
									.getType()));
				else if (Collection.class.isAssignableFrom(field.getType())) {
					if (List.class.isAssignableFrom(field.getType())) {
						List<Object> list = new ArrayList<Object>();
						JSONArray array = fieldsJson.getJSONArray(field
								.getName());
						if (field.getAnnotation(FantasyStep.class).listType() == HashMap.class) {
							list.addAll(JSONUtil.toList(array));
						} else {
							for (int i = 0; i < array.length(); i++)
								if (!array.get(i).toString().isEmpty())
									list.add(json2Node(
											array.get(i).toString(),
											(Class<? extends Node>) field
													.getAnnotation(
															FantasyStep.class)
													.listType()));
						}
						field.set(obj, list);
					} else if (Set.class.isAssignableFrom(field.getType())) {
						Set<Object> set = new HashSet<Object>();
						JSONArray array = fieldsJson.getJSONArray(field
								.getName());
						if (field.getAnnotation(FantasyStep.class).listType() == HashMap.class) {
							set.addAll(JSONUtil.toList(array));
						} else {
							for (int i = 0; i < array.length(); i++)
								if (!array.get(i).toString().isEmpty())
									set.add(json2Node(
											array.get(i).toString(),
											(Class<? extends Node>) field
													.getAnnotation(
															FantasyStep.class)
													.listType()));
						}
						field.set(obj, set);
					}
				} else if (Map.class.isAssignableFrom(field.getType())) {
					Map<Object, Object> map = null;
					JSONObject object = fieldsJson.getJSONObject(field
							.getName());
					try {
						map = new ObjectMapper().readValue(new StringReader(
								object.toString()), HashMap.class);
						if (field.getAnnotation(FantasyStep.class).mapType().length == 1) {
							Class<?> valueClass = field.getAnnotation(
									FantasyStep.class).mapType()[0];
							if (Node.class.isAssignableFrom(valueClass)) {
								Map<Object, Node> newMap = new HashMap<Object, Node>();
								for (Entry<Object, Object> entry : map
										.entrySet()) {
									newMap.put(
											entry.getKey(),
											json2Node(
													object.getJSONObject(
															entry.getKey()
																	.toString())
															.toString(),
													(Class<? extends Node>) valueClass));
								}
								field.set(obj, newMap);
							} else {
								Map<Object, Object> newMap = new HashMap<Object, Object>();
								for (Entry<Object, Object> entry : map
										.entrySet()) {
									newMap.put(
											entry.getKey(),
											json2Object(
													object.getJSONObject(
															entry.getKey()
																	.toString())
															.toString(),
													valueClass));
								}
								field.set(obj, newMap);
							}
						} else if (field.getAnnotation(FantasyStep.class)
								.mapType().length == 2) {
							Class<?> keyClass = field.getAnnotation(
									FantasyStep.class).mapType()[0];
							Class<?> valueClass = field.getAnnotation(
									FantasyStep.class).mapType()[1];
							if (Node.class.isAssignableFrom(keyClass)
									&& Node.class.isAssignableFrom(valueClass)) {
								Map<Node, Node> newMap = new HashMap<Node, Node>();
								for (Entry<Object, Object> entry : map
										.entrySet())
									newMap.put(
											json2Node(
													entry.getKey().toString(),
													(Class<? extends Node>) keyClass),
											json2Node(
													object.getJSONObject(
															entry.getKey()
																	.toString())
															.toString(),
													(Class<? extends Node>) valueClass));
								field.set(obj, newMap);
							} else if (Node.class.isAssignableFrom(keyClass)) {
								Map<Node, Object> newMap = new HashMap<Node, Object>();
								for (Entry<Object, Object> entry : map
										.entrySet())
									newMap.put(
											json2Node(
													entry.getKey().toString(),
													(Class<? extends Node>) keyClass),
											json2Object(
													object.getJSONObject(
															entry.getKey()
																	.toString())
															.toString(),
													valueClass));
								field.set(obj, newMap);
							} else if (Node.class.isAssignableFrom(valueClass)) {
								Map<Object, Node> newMap = new HashMap<Object, Node>();
								for (Entry<Object, Object> entry : map
										.entrySet())
									newMap.put(
											json2Object(entry.getKey()
													.toString(), keyClass),
											json2Node(
													object.getJSONObject(
															entry.getKey()
																	.toString())
															.toString(),
													(Class<? extends Node>) valueClass));
								field.set(obj, newMap);
							} else {
								Map<Object, Object> newMap = new HashMap<Object, Object>();
								for (Entry<Object, Object> entry : map
										.entrySet())
									newMap.put(
											json2Object(entry.getKey()
													.toString(), keyClass),
											json2Object(
													object.getJSONObject(
															entry.getKey()
																	.toString())
															.toString(),
													valueClass));
								field.set(obj, newMap);
							}
						} else
							field.set(obj, map);
					} catch (JsonParseException e) {
						e.printStackTrace();
					} catch (JsonMappingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public static String node2Json(Node node) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writer(SerializationFeature.INDENT_OUTPUT);

		StringWriter sw = new StringWriter();
		try {
			objectMapper.writeValue(sw, node);
			return sw.getBuffer().toString();
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String object2Json(Object obj) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writer(SerializationFeature.INDENT_OUTPUT);

		StringWriter sw = new StringWriter();
		try {
			objectMapper.writeValue(sw, obj);
			return sw.getBuffer().toString();
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
//	 System.out.println(JSON2NodeUtil.json2Object("[ { \"aaa\" : \"ass\", \"bbb\" : \"ssss\" } ]",ArrayList.class));
//	 TestMongo2 tm = new TestMongo2();
//	 List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//	 Map<String, Object> map = new HashMap<String, Object>();
//	 map.put("a", "b");
//	 list.add(map);
//	 tm.setEee(list);
//	 JSONArray array = new JSONArray(
//	 "[ { \"aaa\" : \"ass\", \"bbb\" : \"ssss\" } ]");
//	 String s = JSON2NodeUtil.node2Json(tm);
//	 Node n = JSON2NodeUtil.json2Node(s, TestMongo2.class);
//	 System.out.println(s);
	 }
	// public static void main(String[] args) {
	// Map<UUID, Group> oo = new HashMap<UUID, Group>();
	// Group group = new Group();
	// group.setId(UUID.randomUUID());
	// group.setName("Name");
	// group.setAaa("aaa");
	// group.setBbb("bbb");
	// group.setGender(GenderEnum.MAN);
	// group.setCreatedDate(new Date());
	// group.setRemarks(Arrays.asList(new String[] { "aaaa", "bbbb" }));
	// Group group2 = new Group();
	// group2.setName("innner");
	// group2.setGender(GenderEnum.WOMAN);
	// group2.setRemarks(Arrays
	// .asList(new String[] { "aaaa", "bbbb", "cccccc" }));
	// group2.setAaa("aaa");
	// group2.setCreatedDate(new Date());
	// group2.setId(UUID.randomUUID());
	// oo.put(UUID.randomUUID(), group2);
	// oo.put(UUID.randomUUID(), group2);
	// group.setParent(group2);
	// group.setOo(oo);
	// group.setGroups(Arrays.asList(new UUID[] { UUID.randomUUID(),
	// UUID.randomUUID() }));
	// String jsonString = JSON2NodeUtil.node2Json(group);
	// logger.info(jsonString);
	//
	// Group g = JSON2NodeUtil.json2Node(jsonString, Group.class);
	// logger.info(g);
	// }
}