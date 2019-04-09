package com.fantasystep.persistence.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fantasystep.annotation.Storage;
import com.fantasystep.domain.Node;
import com.fantasystep.persistence.StorageHandler;
import com.fantasystep.persistence.exception.PersistenceException;
import com.fantasystep.persistence.manager.DomainFieldManager;
import com.fantasystep.persistence.mongo.config.MongoConfig;
import com.fantasystep.utils.JSON2NodeUtil;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@SuppressWarnings("deprecation")
public class MongoStorageHandler implements StorageHandler {
	
	private static Logger logger = LoggerFactory.getLogger(MongoStorageHandler.class);

	@SuppressWarnings("unchecked")
	@Override
	public Map<UUID, Map<String, Object>> read(Class<? extends Node> nodeClass,
			List<UUID> ids, Map<String, Object> node)
			throws PersistenceException {
		DBCollection coll = MongoConfig.getInstance().getDB().getCollection(nodeClass.getSimpleName());
		List<DBObject> pipeline = new ArrayList<DBObject>();
		if(node != null && !node.isEmpty())
			pipeline.add(new BasicDBObject("$match", new BasicDBObject(node)));
		if(ids != null && !ids.isEmpty()) {
			List<String> list = new ArrayList<String>();
			for(UUID id : ids)
				list.add(id.toString());
			pipeline.add(new BasicDBObject("$match", new BasicDBObject("id", new BasicDBObject("$in", list.toArray()))));
		}

        Map<UUID, Map<String, Object>> map = new LinkedHashMap<UUID, Map<String, Object>>();
		if(pipeline.size() > 0) {
	        AggregationOutput output = coll.aggregate(pipeline);
	        for (DBObject result : output.results()) {
	        	map.put(UUID.fromString(result.get("id").toString()), (Map<String, Object>)result.toMap());
	        	logger.debug(result.toString());
	        }
		} else {
			DBCursor cursor = coll.find();
			try {
			   while(cursor.hasNext()) {
				   DBObject obj = cursor.next();
				   map.put(UUID.fromString(obj.get("id").toString()), (Map<String, Object>)obj.toMap());
			   }
			} finally {
			   cursor.close();
			}
		}
		return map;
	}

	@Override
	public Map<String, Object> read(Class<? extends Node> nodeClass, UUID id,
			Map<String, Object> node) throws PersistenceException {
		Map<UUID, Map<String, Object>> result = read(nodeClass, Arrays.asList(new UUID[]{id}), node);
		return result.isEmpty() ? null : result.get(id);
	}

	@Override
	public boolean insert(Class<? extends Node> nodeClass,
			Map<UUID, Map<String, Object>> nodeList)
			throws PersistenceException {
		DBCollection coll = MongoConfig.getInstance().getDB().getCollection(nodeClass.getSimpleName());
		BulkWriteOperation builder = coll.initializeOrderedBulkOperation();
		for(Map<String, Object> map : nodeList.values()) {
			DBObject dbObject = (DBObject)com.mongodb.util.JSON.parse(JSON2NodeUtil.object2Json(map));
			builder.insert(dbObject);
		}
		BulkWriteResult result = builder.execute();
        logger.debug("Multiple insert write result : " + result);
		return result.isAcknowledged();
	}

	@Override
	public boolean insert(Class<? extends Node> nodeClass, UUID id,
			Map<String, Object> node) throws PersistenceException {
		DBCollection coll = MongoConfig.getInstance().getDB().getCollection(nodeClass.getSimpleName());
		DBObject dbObject = (DBObject)com.mongodb.util.JSON.parse(JSON2NodeUtil.object2Json(node));
		WriteResult result = coll.insert(dbObject);
		logger.debug("Single insert write result : " + result);
		return result.wasAcknowledged();
	}

	@Override
	public boolean update(Class<? extends Node> nodeClass, UUID id,
			Map<String, Object> node) throws PersistenceException {
		DBCollection coll = MongoConfig.getInstance().getDB().getCollection(nodeClass.getSimpleName());
		WriteResult result = coll.update(new BasicDBObject("id", id.toString()), new BasicDBObject("$set", (DBObject)com.mongodb.util.JSON.parse(JSON2NodeUtil.object2Json(node))));
		logger.debug("Single update write result : " + result);
		return result.wasAcknowledged();
	}

	@Override
	public boolean destroy(Class<? extends Node> nodeClass, List<UUID> ids)
			throws PersistenceException {
		DBCollection coll = MongoConfig.getInstance().getDB().getCollection(nodeClass.getSimpleName());
		BulkWriteOperation builder = coll.initializeOrderedBulkOperation();
		for(UUID id : ids)
			builder.find(new BasicDBObject("id", id.toString())).removeOne();
		BulkWriteResult result = builder.execute();
        logger.debug("Multiple destroy write result : " + result);
        return result.isAcknowledged();
	}

	@Override
	public boolean destroy(Class<? extends Node> nodeClass, UUID id)
			throws PersistenceException {
		DBCollection coll = MongoConfig.getInstance().getDB().getCollection(nodeClass.getSimpleName());
		DBObject obj = coll.findOne(new BasicDBObject("id", id.toString()));
		if(obj != null) {
			WriteResult result = coll.remove(obj);
	        logger.debug("Single destroy write result : " + result);
			return result.wasAcknowledged();
		} else return false;
	}

	@Override
	public void setup() {
		DB db = MongoConfig.getInstance().getDB();
		for(Class<? extends Node> clazz : DomainFieldManager.getInstance().lookupDomainClassByStorage(Storage.MONGO).keySet())
			db.getCollection(clazz.getSimpleName());
	}

	@Override
	public void terminate() {
		MongoConfig.getInstance().getClient().close();
	}
	
//	public static void main(String[] args) throws Exception {
//		String a = "{\"home\":{\"left\":\"-1700px\",\"top\":\"-2100px\",\"rotate\":\"-30deg\",\"scale\":\"1.0\"},\"1-2-1\":{\"animation\":{\"left\":\"-100px\",\"top\":\"20px\",\"rotate\":\"0deg\",\"scale\":\"1.0\"},\"captionIndex\":10},\"1-5-2\":{\"animation\":{\"left\":\"-3500px\",\"top\":\"-800px\",\"rotate\":\"90deg\",\"scale\":\"1.0\"},\"captionIndex\":16},\"1-9-1\":{\"animation\":{\"left\":\"-1400px\",\"top\":\"0px\",\"rotate\":\"-30deg\",\"scale\":\"1.0\"},\"captionIndex\":6},\"1-13-2\":{\"animation\":{\"left\":\"-3500px\",\"top\":\"-3500px\",\"rotate\":\"90deg\",\"scale\":\"1.0\"},\"captionIndex\":4},\"2-1-4\":{\"animation\":{\"left\":\"-900px\",\"top\":\"-3800px\",\"rotate\":\"-90deg\",\"scale\":\"1.0\"},\"captionIndex\":1},\"2-7-2\":{\"animation\":{\"left\":\"-2900px\",\"top\":\"-1500px\",\"rotate\":\"90deg\",\"scale\":\"1.0\"},\"captionIndex\":7},\"3-3-4\":{\"animation\":{\"left\":\"-1500px\",\"top\":\"-3150px\",\"rotate\":\"-90deg\",\"scale\":\"1.0\"},\"captionIndex\":9},\"3-9-2\":{\"animation\":{\"left\":\"-2200px\",\"top\":\"-2150px\",\"rotate\":\"90deg\",\"scale\":\"1.0\"},\"captionIndex\":8},\"3-13-1\":{\"animation\":{\"left\":\"-3700px\",\"top\":\"-1300px\",\"rotate\":\"0deg\",\"scale\":\"1.0\"},\"captionIndex\":5},\"4-1-1\":{\"animation\":{\"left\":\"300px\",\"top\":\"-1950px\",\"rotate\":\"0deg\",\"scale\":\"1.0\"},\"captionIndex\":3},\"4-5-4\":{\"animation\":{\"left\":\"-1700px\",\"top\":\"-2100px\",\"rotate\":\"-30deg\",\"scale\":\"1.0\"},\"captionIndex\":-1},\"4-11-2\":{\"animation\":{\"left\":\"-2100px\",\"top\":\"-2700px\",\"rotate\":\"60deg\",\"scale\":\"1.0\"},\"captionIndex\":14},\"4-13-4\":{\"animation\":{\"left\":\"-4050px\",\"top\":\"-1850px\",\"rotate\":\"0deg\",\"scale\":\"1.0\"},\"captionIndex\":11},\"5-6-4\":{\"animation\":{\"left\":\"-2900px\",\"top\":\"-2110px\",\"rotate\":\"-90deg\",\"scale\":\"1.0\"},\"captionIndex\":2},\"6-1-4\":{\"animation\":{\"left\":\"-3500px\",\"top\":\"-3800px\",\"rotate\":\"-90deg\",\"scale\":\"1.0\"},\"captionIndex\":17},\"6-3-1\":{\"animation\":{\"left\":\"-450px\",\"top\":\"-3285px\",\"rotate\":\"0deg\",\"scale\":\"1.0\"},\"captionIndex\":13},\"6-7-4\":{\"animation\":{\"left\":\"-3500px\",\"top\":\"-1760px\",\"rotate\":\"-90deg\",\"scale\":\"1.0\"},\"captionIndex\":12},\"5-9-1\":{\"animation\":{\"left\":\"-2700px\",\"top\":\"-2350px\",\"rotate\":\"-30deg\",\"scale\":\"1.0\"},\"captionIndex\":15}}";
//		DBObject b = (DBObject)com.mongodb.util.JSON.parse(a);
//		System.out.println(b.toString());
//	}
//		MongoStorageHandler handler = new MongoStorageHandler();
//		Map<String, Object> map = handler.read(Menulink.class, UUID.fromString("f0ae7f45-6018-48aa-aa72-3d30582305ac"), null);
//		System.out.println(map.get("linkJson"));
//		System.out.println(map.get("linkJson").getClass().getCanonicalName());
//		JSONArray array = new JSONArray(map.get("linkJson").toString());
//		List<?> lt = JSONUtil.toList(array);
//		List<Node> list = new ArrayList<Node>();
//		MongoStorageHandler handler = new MongoStorageHandler();
//		Map<UUID, Map<String, Object>> map = handler.read(TestMongo2.class, new ArrayList<UUID>(), null);
//		List<Node> list = new ArrayList<Node>();
//		for(Entry<UUID, Map<String, Object>> entry : map.entrySet()) {
//			if(!((Map<String, Object>)entry.getValue()).get("aaa").toString().equals("asdf"))
//				continue;
//			Node node = new TestMongo2();
//			DomainFieldManager.getInstance().convertFromMapToDomain( node, (Map<String, Object>)entry.getValue() );
//			List l = ((TestMongo2)node).getEee();
//			Node dd = NodeClassUtil.getSerializationNode(node);
//			String a = JSON2NodeUtil.node2Json(node);
//			logger.info(a);
//			Node nn = JSON2NodeUtil.json2Node(a, TestMongo2.class);
//			list.add(node);
//		}
//		System.out.println();
//		User user = new User();
//		UUID id = UUID.randomUUID();
//		logger.info(id);
//		user.setId(id);
//		Map<UUID, Group> oo = new HashMap<UUID, Group>();
//		Group g = new Group();
//		g.setId(UUID.randomUUID());
//		g.setAaa("aaa");
//		g.setBbb("bbb");
//		g.setCreatedDate(new Date());
//		oo.put(UUID.randomUUID(), g);
//		user.setOo(oo);
//		String tmp = JSON2NodeUtil.node2Json(user);
//		handler.insert(User.class, null, JSONUtil.toMap(JSONUtil.toJSON(tmp.replace("null", "\"\""))));
//		handler.destroy(User.class, UUID.fromString("6f56fd13-7e85-41c1-aa06-4bfceb891839"));
//		for(int i = 0; i < 3; i++) {
//			Map<String, Object> node = new HashMap<String, Object>();
//			node.put("id", UUID.randomUUID().toString());
//			node.put("source", "aaa");
//			handler.insert(Entity.class, null, node);
//		}
//		handler.update(User.class, UUID.fromString("a8d7e585-4ab9-434a-b1fd-55b016604557"), node);
//		Map<Class<?>, String> mm = new HashMap<Class<?>, String>();
//		Map<String, String> codes = new HashMap<String, String>();
//		String codeEconomy = handler.read(Entity.class, UUID.fromString("ed533cbd-814a-462d-bae2-334aa0df636a"), null).get("sourceCode").toString();
//		String codeShare = handler.read(Entity.class, UUID.fromString("9999e329-2d9f-4403-9821-0e8889439ed2"), null).get("sourceCode").toString();
//		String codeStock = handler.read(Entity.class, UUID.fromString("4cbed231-62e9-4af9-8577-71d72a18e30f"), null).get("sourceCode").toString();
//		String codeDeveloper = handler.read(Entity.class, UUID.fromString("9e1a6d2e-0afc-437d-af31-25d901fd2db8"), null).get("sourceCode").toString();
//		codes.put("com.fantasystep.domain.Economy", codeEconomy);
//		codes.put("com.fantasystep.domain.Share", codeShare);
//		codes.put("com.fantasystep.domain.Stock", codeStock);
//		codes.put("com.fantasystep.domain.Developer", codeDeveloper);
//		
//		Class<?> clazz = JCompiler.getInstance().registerClass("com.fantasystep.domain.Developer", codes);
//		Object obj = clazz.newInstance();
//		logger.info(Node.class.isAssignableFrom(obj.getClass()));
//		Node n = (Node) obj;
//		String s = JSON2NodeUtil.node2Json(n);
//		logger.info(s);
//		logger.info(clazz.getCanonicalName());
//		Class<?> fieldClass = clazz.getDeclaredField("node").getType();
//		logger.info(fieldClass);
//		Class<?> cc = Class.forName("com.fantasystep.domain.Node");
//		logger.info(fieldClass.equals(cc));
//		mm.put(clazz, "old");
//		logger.info(obj);
//		System.out.println(handler.read(Entity.class, UUID.fromString("4cbed231-62e9-4af9-8577-71d72a18e30f"), null));
//		Class<?> clazzNew = JCompiler.getInstance().registerClass("com.fantasystep.domain.Stock", handler.read(Entity.class, UUID.fromString("4cbed231-62e9-4af9-8577-71d72a18e30f"), null).get("sourceCode").toString());
//		Object objNew = clazzNew.newInstance();
//		Field f = clazzNew.getDeclaredField("code");
//		f.setAccessible(true);
//		f.set(objNew, "aaaa");
//		
////		mm.put(clazzNew, "new");
//		System.out.println(objNew);
//		logger.info(mm);
		
//	}
}
