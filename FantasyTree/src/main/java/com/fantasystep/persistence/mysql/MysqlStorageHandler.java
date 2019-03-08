package com.fantasystep.persistence.mysql;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.fantasystep.annotation.AnnotationsParser;
import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.annotation.MultiValueOptions;
import com.fantasystep.annotation.Storage;
import com.fantasystep.annotation.StorageModelType;
import com.fantasystep.annotation.ValueOptions;
import com.fantasystep.domain.Node;
import com.fantasystep.domain.SubNode;
import com.fantasystep.persistence.StorageHandler;
import com.fantasystep.persistence.exception.PersistenceException;
import com.fantasystep.persistence.manager.DomainFieldManager;
import com.fantasystep.persistence.mysql.config.MysqlConfig;

public class MysqlStorageHandler extends MysqlHandler implements StorageHandler {

	// private static final Logger logger = Logger.getLogger(
	// MysqlStorageHandler.class );

	@Override
	public boolean destroy(Class<? extends Node> nodeClass, List<UUID> ids)
			throws PersistenceException {
		String nodeName = nodeClass.getSimpleName();
		if (null == ids || ids.isEmpty())
			return true;
		if (!hasMysqlProperties(nodeClass))
			return true;

		Connection c = null;
		try {
			c = MysqlConfig.getInstance().createConnection();

			Statement s = c.createStatement();
			StringBuffer sqlDelete = new StringBuffer(" DELETE FROM `");
			sqlDelete.append(nodeName.toLowerCase()).append("` WHERE id IN ('");
			boolean isFirst = true;
			for (UUID uuid : ids) {
				if (isFirst)
					isFirst = false;
				else
					sqlDelete.append("', '");
				sqlDelete.append(uuid);
			}
			sqlDelete.append("')");

			if (s.execute(sqlDelete.toString()))
				return true;
			else
				return false;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getCause());
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new PersistenceException(e.getCause());
				}
			}
		}
	}

	@Override
	public boolean destroy(Class<? extends Node> nodeClass, UUID id)
			throws PersistenceException {
		String nodeName = nodeClass.getSimpleName();

		if (!hasMysqlProperties(nodeClass))
			return true;

		Connection c = null;
		try {
			c = MysqlConfig.getInstance().createConnection();

			Statement s = c.createStatement();
			StringBuffer sqlDelete = new StringBuffer(" DELETE FROM `");
			sqlDelete.append(nodeName.toLowerCase()).append("` WHERE id = '");
			sqlDelete.append(id).append("'");

			if (s.execute(sqlDelete.toString()))
				return true;
			else
				return false;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getCause());
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new PersistenceException(e.getCause());
				}
			}
		}
	}

	private String getStringIfIsEnumList(Map<String, Object> node,
			Class<? extends Node> nodeClass, String fieldName) {
		Object obj = node.get(fieldName);
		if (obj == null)
			return null;

		Map<String, Class<?>> nodeTypes = DomainFieldManager.getInstance()
				.lookupDomainFieldByStorage(nodeClass, Storage.MYSQL);

		Class<?> listType = null;
		Class<? extends ValueOptions> valueOptions = null;
		StorageModelType storageMode = null;

		if (nodeTypes.get(fieldName) != null
				&& fieldName.indexOf(DomainFieldManager.DELIMITER) != -1)
			return null;
		listType = AnnotationsParser.getAttributes(nodeClass, fieldName)
				.getListType();
		valueOptions = AnnotationsParser.getAttributes(nodeClass, fieldName)
				.getValueOptions();
		storageMode = AnnotationsParser.getAttributes(nodeClass, fieldName)
				.getStorageModel();
		if ((listType != null && (Enum.class.isAssignableFrom(listType)))
				|| (storageMode == StorageModelType.LIST
						&& valueOptions != null && MultiValueOptions.class
							.isAssignableFrom(valueOptions))) {
			String s = obj.toString().replace(",", ";");
			return s.substring(1, s.length() - 1);
		} else
			return null;
	}

	private boolean hasMysqlProperties(Class<? extends Node> nodeClass) {
		Map<String, Class<?>> nodeTypes = DomainFieldManager.getInstance()
				.lookupDomainFieldByStorage(nodeClass, Storage.MYSQL);
		if (null == nodeTypes || nodeTypes.isEmpty())
			return false;
		else
			return true;
	}

	@Override
	public boolean insert(Class<? extends Node> nodeClass,
			Map<UUID, Map<String, Object>> nodeList)
			throws PersistenceException {
		String nodeName = nodeClass.getSimpleName();

		if (!hasMysqlProperties(nodeClass))
			return true;

		Connection c = null;
		try {
			c = MysqlConfig.getInstance().createConnection();

			Map<String, Class<?>> nodeTypes = DomainFieldManager.getInstance()
					.lookupDomainFieldByStorage(nodeClass, Storage.MYSQL);

			Statement s = c.createStatement();
			for (Entry<UUID, Map<String, Object>> record : nodeList.entrySet()) {
				Map<String, Object> node = record.getValue();
				StringBuffer sqlInsert = new StringBuffer(" INSERT INTO `");
				sqlInsert.append(nodeName.toLowerCase()).append("` ")
						.append(" (");

				boolean isFirst = true;
				for (Entry<String, Class<?>> entry : nodeTypes.entrySet()) {
					if (!this.isValidField(node, nodeClass, entry.getKey()))
						continue;

					if (isFirst)
						isFirst = false;
					else
						sqlInsert.append(", ");
					sqlInsert.append(entry.getKey());
				}

				sqlInsert.append(") VALUES (");

				isFirst = true;
				for (Entry<String, Class<?>> entry : nodeTypes.entrySet()) {
					if (!this.isValidField(node, nodeClass, entry.getKey()))
						continue;

					if (isFirst)
						isFirst = false;
					else
						sqlInsert.append(", ");

					String tmp = getStringIfIsEnumList(node, nodeClass,
							entry.getKey());
					if (tmp != null)
						sqlInsert.append("'").append(tmp).append("'");
					else if (node.get(entry.getKey()) == null)
						sqlInsert.append("NULL");
					else if (this.isStringField(entry.getValue(),
							node.get(entry.getKey())))
						sqlInsert.append("'").append(node.get(entry.getKey()))
								.append("'");
					else
						sqlInsert.append(node.get(entry.getKey()));
				}
				sqlInsert.append(") ");

				s.addBatch(sqlInsert.toString().replaceAll("\\\\", "\\\\\\\\"));
			}

			s.executeBatch();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getCause());
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new PersistenceException(e.getCause());
				}
			}
		}
	}

	@Override
	public boolean insert(Class<? extends Node> nodeClass, UUID id,
			Map<String, Object> node) throws PersistenceException {
		String nodeName = nodeClass.getSimpleName();

		if (!hasMysqlProperties(nodeClass))
			return true;

		Connection c = null;
		try {
			c = MysqlConfig.getInstance().createConnection();
			Map<String, Class<?>> nodeTypes = DomainFieldManager.getInstance()
					.lookupDomainFieldByStorage(nodeClass, Storage.MYSQL);

			Statement s = c.createStatement();
			StringBuffer sqlInsert = new StringBuffer(" INSERT INTO `");
			sqlInsert.append(nodeName.toLowerCase()).append("` (");

			boolean isFirst = true;
			for (Entry<String, Class<?>> entry : nodeTypes.entrySet()) {
				if (!this.isValidField(node, nodeClass, entry.getKey()))
					continue;

				if (isFirst)
					isFirst = false;
				else
					sqlInsert.append(", ");
				sqlInsert.append(entry.getKey().toLowerCase());
			}

			sqlInsert.append(") VALUES (");

			isFirst = true;
			for (Entry<String, Class<?>> entry : nodeTypes.entrySet()) {
				if (!this.isValidField(node, nodeClass, entry.getKey()))
					continue;

				if (isFirst)
					isFirst = false;
				else
					sqlInsert.append(", ");

				String tmp = getStringIfIsEnumList(node, nodeClass,
						entry.getKey());
				if (tmp != null)
					sqlInsert.append("'").append(tmp).append("'");
				else if (node.get(entry.getKey()) == null)
					sqlInsert.append("NULL");
				else if (this.isStringField(entry.getValue(),
						node.get(entry.getKey())))
					sqlInsert.append("'").append(node.get(entry.getKey()))
							.append("'");
				else
					sqlInsert.append(node.get(entry.getKey()));
			}

			sqlInsert.append(") ");

			if (s.execute(sqlInsert.toString().replaceAll("\\\\", "\\\\\\\\")))
				return true;
			else
				return false;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getCause());
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new PersistenceException(e.getCause());
				}
			}
		}
	}

	private boolean isValidField(Map<String, Object> node,
			Class<? extends Node> nodeClass, String fieldName) {
		if (!node.containsKey(fieldName))
			return false;

		Class<?> listType = null;
		Map<String, Class<?>> nodeTypes = DomainFieldManager.getInstance()
				.lookupDomainFieldByStorage(nodeClass, Storage.MYSQL);
		try {
			if (nodeTypes.get(fieldName) != null
					&& fieldName.indexOf(DomainFieldManager.DELIMITER) != -1)
				return true;
			listType = AnnotationsParser.getAttributes(nodeClass, fieldName)
					.getListType();
			if (listType != null && Node.class.isAssignableFrom(listType))
				return false;
			else
				return true;
		} catch (SecurityException e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public Map<UUID, Map<String, Object>> read(Class<? extends Node> nodeClass,
			List<UUID> ids, Map<String, Object> node)
			throws PersistenceException {
		if (!hasMysqlProperties(nodeClass))
			return null;

		return read(nodeClass, ids, node, null, null, true, false);
	}

	@SuppressWarnings("unchecked")
	private Map<UUID, Map<String, Object>> read(
			Class<? extends Node> nodeClass, List<UUID> ids,
			Map<String, Object> node, Connection c, Statement s,
			boolean needClose, boolean withMultiNodeList)
			throws PersistenceException {
		Map<UUID, Map<String, Object>> result = new HashMap<UUID, Map<String, Object>>();
		Map<String, Class<? extends SubNode>> multiNode = new HashMap<String, Class<? extends SubNode>>();
		try {
			if (c == null)
				c = MysqlConfig.getInstance().createConnection();
			if (s == null)
				s = c.createStatement();
			Map<String, Class<?>> nodeTypes = DomainFieldManager.getInstance()
					.lookupDomainFieldByStorage(nodeClass, Storage.MYSQL);

			StringBuffer sqlQuery = new StringBuffer(" SELECT ");

			boolean isFirst = true;
			for (Entry<String, Class<?>> entry : nodeTypes.entrySet()) {
				FieldAttributeAccessor accessor = AnnotationsParser
						.getAttributes(nodeClass, entry.getKey());
				Class<?> listType = null;
				if (accessor != null)
					listType = accessor.getListType();
				if (accessor != null && listType != null) {
					if (SubNode.class.isAssignableFrom(listType)
							&& withMultiNodeList) {
						multiNode.put(entry.getKey(),
								(Class<? extends SubNode>) listType);
						continue;
					} else if (Node.class.isAssignableFrom(listType)
							|| Class.class.isAssignableFrom(listType))
						continue;
				}

				if (isFirst)
					isFirst = false;
				else
					sqlQuery.append(", ");
				sqlQuery.append(entry.getKey().toLowerCase()).append(" ")
						.append(entry.getKey().toUpperCase());
			}

			sqlQuery.append(" FROM `")
					.append(nodeClass.getSimpleName().toLowerCase())
					.append("` ");
			sqlQuery.append(" WHERE 1=1 ");

			if (null != ids && !ids.isEmpty()) {
				sqlQuery.append("AND id IN ('");
				isFirst = true;
				for (UUID id : ids) {
					if (isFirst)
						isFirst = false;
					else
						sqlQuery.append("', '");
					sqlQuery.append(id);
				}
				sqlQuery.append("') ");
			}

			if (null != node && !node.isEmpty()) {
				for (Entry<String, Object> entry : node.entrySet()) {
					if (entry.getValue() == null || entry.getKey().equals("id"))
						continue;
					if (!nodeTypes.containsKey(entry.getKey()))
						continue;

					sqlQuery.append(" AND ");
					sqlQuery.append(entry.getKey()).append(" = ");

					String tmp = getStringIfIsEnumList(node, nodeClass,
							entry.getKey());
					if (tmp != null)
						sqlQuery.append("'").append(tmp).append("'");
					else if (this.isStringField(nodeTypes.get(entry.getKey()),
							entry.getValue()))
						sqlQuery.append("'").append(entry.getValue())
								.append("' ");
					else
						sqlQuery.append(entry.getValue());

				}
			}

			ResultSet rs = s.executeQuery(sqlQuery.toString());
			while (rs.next()) {
				Map<String, Object> record = new HashMap<String, Object>();
				for (Entry<String, Class<?>> entry : nodeTypes.entrySet()) {
					FieldAttributeAccessor accessor = AnnotationsParser
							.getAttributes(nodeClass, entry.getKey());
					Class<?> listType = null;
					Class<? extends ValueOptions> valueOptions = null;
					StorageModelType storageMode = null;
					if (accessor != null) {
						listType = accessor.getListType();
						valueOptions = accessor.getValueOptions();
						storageMode = accessor.getStorageModel();
					}
					if (storageMode == StorageModelType.LIST
							&& valueOptions != null
							&& MultiValueOptions.class
									.isAssignableFrom(valueOptions)) {
						Object tmp = rs.getObject(entry.getKey().toUpperCase());
						if (tmp != null) {
							String[] enums = tmp.toString().split(";");
							if (enums != null)
								record.put(entry.getKey(), Arrays.asList(enums));
						}
					} else if (listType != null) {
						if (Enum.class.isAssignableFrom(listType)) {
							Object tmp = rs.getObject(entry.getKey()
									.toUpperCase());
							if (tmp != null) {
								String[] enums = tmp.toString().split(";");
								if (enums != null)
									record.put(entry.getKey(),
											Arrays.asList(enums));
							}
						} else if (SubNode.class.isAssignableFrom(listType)
								&& withMultiNodeList) {
							String foreignkey = AnnotationsParser
									.getAttributes(listType).getForeignKey();
							if (foreignkey != null && !foreignkey.equals("")) {
								Map<String, Object> condition = new HashMap<String, Object>();
								condition.put(foreignkey,
										(String) record.get("id"));
								for (Entry<String, Class<? extends SubNode>> multiNodeEntry : multiNode
										.entrySet()) {
									Object multiNodeList = read(
											(Class<? extends Node>) multiNodeEntry
													.getValue(), null,
											condition, c, null, false,
											withMultiNodeList).values();
									record.put(multiNodeEntry.getKey(),
											multiNodeList);
								}
							}
						} else if (UUID.class.equals(listType) || Node.class.isAssignableFrom(listType)
								|| Class.class.isAssignableFrom(listType)) {
							record.put(entry.getKey(),
									rs.getObject(entry.getKey().toUpperCase()));
						}
					} else if (accessor != null
							&& accessor.getSerializationMaximumLength() >= MysqlStorageHandler.LONGTEXT_LENGTH) {
						Clob clob = rs.getClob(entry.getKey().toUpperCase());
						if (clob == null)
							record.put(entry.getKey(), "");
						else
							record.put(
									entry.getKey(),
									new String(clob.getSubString(1,
											(int) clob.length())));
					} else
						record.put(entry.getKey(),
								rs.getObject(entry.getKey().toUpperCase()));
				}

				result.put((UUID) UUID.fromString((String) record.get("id")),
						record);

			}

			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getCause());
		} finally {
			if (c != null && needClose) {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new PersistenceException(e.getCause());
				}
			}
		}
	}

	@Override
	public Map<String, Object> read(Class<? extends Node> nodeClass, UUID id,
			Map<String, Object> node) throws PersistenceException {
		if (!hasMysqlProperties(nodeClass))
			return null;

		List<UUID> list = new ArrayList<UUID>();
		list.add(id);
		Map<UUID, Map<String, Object>> result = this.read(nodeClass, list,
				node, null, null, true, true);
		return result.isEmpty() ? null : result.get(id);
	}

	@Override
	public void setup() {
		super.setup(Storage.MYSQL);

	}

	@Override
	public void terminate() {

	}

	@Override
	public boolean update(Class<? extends Node> nodeClass, UUID id,
			Map<String, Object> node) throws PersistenceException {
		String nodeName = nodeClass.getSimpleName();

		if (!hasMysqlProperties(nodeClass))
			return true;

		Connection c = null;
		try {
			c = MysqlConfig.getInstance().createConnection();
			Map<String, Class<?>> nodeTypes = DomainFieldManager.getInstance()
					.lookupDomainFieldByStorage(nodeClass, Storage.MYSQL);

			Statement s = c.createStatement();
			StringBuffer sqlUpdate = new StringBuffer("  UPDATE `");
			sqlUpdate.append(nodeName.toLowerCase()).append("` SET ");

			boolean isFirst = true;
			for (Entry<String, Class<?>> entry : nodeTypes.entrySet()) {
				if (!this.isValidField(node, nodeClass, entry.getKey())
						|| entry.getKey().equals("id"))
					continue;

				if (isFirst)
					isFirst = false;
				else
					sqlUpdate.append(", ");

				sqlUpdate.append(entry.getKey().toLowerCase()).append(" = ");

				String tmp = getStringIfIsEnumList(node, nodeClass,
						entry.getKey());
				if (tmp != null)
					sqlUpdate.append("'").append(tmp).append("'");
				else if (node.get(entry.getKey()) == null)
					sqlUpdate.append("NULL");
				else if (isStringField(entry.getValue(),
						node.get(entry.getKey())))
					sqlUpdate.append("'").append(node.get(entry.getKey()))
							.append("'");
				else
					sqlUpdate.append(node.get(entry.getKey()));
			}
			sqlUpdate.append(" WHERE id = '").append(id).append("' ");

			String query = sqlUpdate.toString().replaceAll("\\\\", "\\\\\\\\");

			if (s.execute(query))
				return true;
			else
				return false;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getCause());
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new PersistenceException(e.getCause());
				}
			}
		}
	}
	
	@Override
	protected void handleReservedSql(List<String> tables, StringBuffer generateSql) {
		if (tables.size() == 1 && tables.get(0).equals("node")) {
			generateSql.append("INSERT INTO `node`(`id`, `deleted`, `label`, `parentid`, `type`, `createddate`, `lastmodifieddate`) VALUES ('e3e8ae71-46da-46ef-ad76-c9dc6d1b7853','false','Fantasystep','null','class com.fantasystep.domain.Node','2019-02-26 00:00:00','2019-02-26 00:00:00');");
			generateSql.append("INSERT INTO `node`(`id`, `deleted`, `label`, `parentid`, `type`, `createddate`, `lastmodifieddate`) VALUES ('48ab94da-604f-443b-8672-fe6c478fe2a2','false','','e3e8ae71-46da-46ef-ad76-c9dc6d1b7853','class com.fantasystep.domain.Group','2019-02-26 00:00:00','2019-02-26 00:00:00');");
			generateSql.append("INSERT INTO `group`(`id`, `name`) VALUES ('48ab94da-604f-443b-8672-fe6c478fe2a2','Family');");
			generateSql.append("INSERT INTO `node`(`id`, `deleted`, `label`, `parentid`, `type`, `createddate`, `lastmodifieddate`) VALUES ('72bedae8-85e0-11e4-928e-0242ac11000b','false','','48ab94da-604f-443b-8672-fe6c478fe2a2','class com.fantasystep.domain.User','2019-02-26 00:00:00','2019-02-26 00:00:00');");
			generateSql.append("INSERT INTO `user`(`id`, `activated`, `adminnode`, `gender`, `lastname`, `birthday`) VALUES ('72bedae8-85e0-11e4-928e-0242ac11000b','true','e3e8ae71-46da-46ef-ad76-c9dc6d1b7853','WOMAN','Huang','1983-5-26');");
		}
	}
}
