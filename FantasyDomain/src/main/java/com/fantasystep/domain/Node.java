package com.fantasystep.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fantasystep.annotation.ControlType;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.annotation.FantasyStep;
import com.fantasystep.annotation.FantasyView;
import com.fantasystep.annotation.SerializationType;
import com.fantasystep.annotation.Storage;
import com.fantasystep.exception.ValidationFailedException;
//import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import com.fasterxml.jackson.databind.ser.std.DateSerializer;

@DomainClass(label = "LABEL_FANTASYSTEP", icon = "gdm.png")
@XmlRootElement(namespace="http://persistence.fantasystep.com/domain")
@XmlAccessorType(XmlAccessType.FIELD)
public class Node implements Serializable {

	private static final long serialVersionUID = 4261001890831422183L;

	public static final String ROOT_NODE_PROPERTY = "root_node";

	public static final String TARGET_NODE_PROPERTY = "target_node";

	@FantasyStep(storage = Storage.TREE, serializationMaximumLength = 40, required = true, sharedKey = true, storageName = "fantasystepId")
	@FantasyView(controlType = ControlType.LABEL, order = 0, label = "LABEL_ID")
	private UUID id;
	
	@FantasyStep(storage = Storage.TREE, serializationMaximumLength = 40, required = true, storageName = "fantasystepParentId")
	private UUID parentId;
	
	@FantasyStep(storage = Storage.TREE)
	private String label;
	
	@FantasyStep(storage = Storage.TREE)
	private Date createdDate;
	
	@FantasyStep(storage = Storage.TREE)
	private Date lastModifiedDate;

	private List<Node> children = new ArrayList<Node>();
	
	@FantasyStep(storage = Storage.TREE, serializationMaximumLength = 16, serializationType = SerializationType.BOOLEAN)
	private Boolean deleted = false;
	
	@FantasyStep(storage = Storage.TREE, serializationMaximumLength = 2048)
	private Class<? extends Node> type = this.getClass();
	
	@FantasyStep(storage = Storage.TREE, propertyField = false)
	private String comment;
	
	private String serializationNode;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

//	@JsonSerialize(using = DateSerializer.class)
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

//	@JsonSerialize(using = DateSerializer.class)
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		Node node = (Node) obj;

		if (this.id == null || node.getId() == null)
			return false;
		return this.id.equals(node.getId());
	}

	@Override
	public int hashCode() {
		if (id == null)
			return super.hashCode();
		int hash = 7;
		hash = 31 * hash + id.hashCode();
//		hash = 31 * hash + (null == name ? 0 : name.hashCode());
		return hash;
	}

	public UUID getParentId() {
		return parentId;
	}

	public void setParentId(UUID parentId) {
		this.parentId = parentId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Node> getChildren() {
		return children;
	}

	public List<Node> getChildren(Class<? extends Node> clazz) {
		List<Node> list = new ArrayList<Node>();
		for (Node node : children)
			if (node.getClass().toString().equals(clazz.toString()))
				list.add(node);
		return list;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public void addChild(Node child) throws ValidationFailedException {
		this.children.add(child);
	}

	public void removeChild(Node child) {
		this.children.remove(child);
	}

	public Boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Class<? extends Node> getType() {
		return this.getClass();
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getSerializationNode() {
		return serializationNode;
	}

	public void setSerializationNode(String serializationNode) {
		this.serializationNode = serializationNode;
	}
}
