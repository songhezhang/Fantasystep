package com.fantasystep.persistence;

import java.util.List;
import java.util.UUID;

import com.fantasystep.domain.Node;

public interface TreeHandler extends StorageHandler{

	abstract public boolean delete(List<UUID> ids);

	abstract public boolean delete(UUID id);

	abstract public List<? extends Node> getTree(Node rootNode);

	abstract public List<? extends Node> getTree(UUID nodeId);

	abstract public boolean unDelete(List<UUID> ids);

	abstract public boolean unDelete(UUID id);
}
