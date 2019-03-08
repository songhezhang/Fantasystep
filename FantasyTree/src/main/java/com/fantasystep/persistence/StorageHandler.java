package com.fantasystep.persistence;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fantasystep.domain.Node;
import com.fantasystep.persistence.exception.PersistenceException;

public interface StorageHandler {

	abstract public Map<UUID, Map<String, Object>> read(
			Class<? extends Node> nodeClass, List<UUID> ids,
			Map<String, Object> node) throws PersistenceException;

	abstract public Map<String, Object> read(Class<? extends Node> nodeClass,
			UUID id, Map<String, Object> node) throws PersistenceException;

	abstract public boolean insert(Class<? extends Node> nodeClass,
			Map<UUID, Map<String, Object>> nodeList)
			throws PersistenceException;

	abstract public boolean insert(Class<? extends Node> nodeClass, UUID id,
			Map<String, Object> node) throws PersistenceException;

	abstract public boolean update(Class<? extends Node> nodeClass, UUID id,
			Map<String, Object> node) throws PersistenceException;

	abstract public boolean destroy(Class<? extends Node> nodeClass,
			List<UUID> ids) throws PersistenceException;

	abstract public boolean destroy(Class<? extends Node> nodeClass, UUID id)
			throws PersistenceException;

	abstract public void setup();

	abstract public void terminate();
}
