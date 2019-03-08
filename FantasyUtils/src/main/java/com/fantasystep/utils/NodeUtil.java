package com.fantasystep.utils;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.fantasystep.annotation.DomainClass;
import com.fantasystep.domain.AbstractGroup;
import com.fantasystep.domain.DynamicDomain;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.MemberHolder;

public class NodeUtil {
	public static List<MemberHolder> getMemberships(Node destroyNode, Node node) {
		List<MemberHolder> list = new ArrayList<MemberHolder>();
		for(Node n :getChildren(node, node))
			if(n instanceof MemberHolder && ((MemberHolder)n).getMembers().contains(destroyNode.getId()))
				list.add((MemberHolder)n);
		return list;
	}

	public static Object getAttribute(Node nodeByID, String alias) {
		Field f = NodeClassUtil.getField(nodeByID.getClass(), alias);
		try {
			return f.get(nodeByID);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Node getNodeByFieldValue(Field field, Object object, Node node) {
		if(object == null)
			return null;
		for(Node n : getChildren(node, node))
			if(NodeClassUtil.getAllNodeFields(n.getClass()).contains(field))
				try {
					if(object.equals(field.get(n)))
						return n;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					continue;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					continue;
				}
		return null;
	}

	public static List<Node> getParents(Node node, Node root) {
		List<Node> list = new ArrayList<Node>();
		if(node.getParentId() != null) {
			Node parent = getNode(node.getParentId(), root);
			if(parent != null) {
				list.add(parent);
				list.addAll(getParents(parent, root));
			}
		}
		return list;
	}

	public static List<Node> getChildren(Node currentNode, Node root) {
		List<Node> list = new ArrayList<Node>();
		Node n = getNode(currentNode.getId(), root);
		if(n != null && n.getChildren() != null) {
			list.addAll(n.getChildren());
			for(Node node : n.getChildren())
				list.addAll(getChildren(node, root));
			return list;
		} else return list;
	}
	
	public static void removeDynamicChildren(Node node) {
		for(Node n : new ArrayList<Node>(node.getChildren())) {
			removeDynamicChildren(n);
			if(n instanceof DynamicDomain)
				node.getChildren().remove(n);
		}
	}

	public static List<Class<? extends Node>> getValidChildren(Class<? extends Node> class1) {
		List<Class<? extends Node>> list = new ArrayList<Class<? extends Node>>();
		for( Class<? extends Node> clazz : NodeClassUtil.getSubClassesInJVM(Node.class) ) {
			DomainClass dc = clazz.getAnnotation(DomainClass.class);
			if(dc == null)
				continue;
			if((dc.validParents() != null && Arrays.asList(dc.validParents()).contains(class1)) || class1.equals(Node.class))
				list.add(clazz);
		}
		return list;
	}

	public static Node getNode(UUID nodeId, List<Node> nodeList) {
		for(Node node : nodeList)
			if(node.getId().equals(nodeId))
				return node;
		return null;
	}
	
	public static Node getNode(Node target, Node root) {
		if(target == null || target.getId() == null)
			return null;
		return getNode(target.getId(), root);
	}
	
	public static Node getNode(UUID id, Node root) {
		if(root.getId().equals(id))
			return root;
		else if(root.getChildren() != null)
			for(Node node : root.getChildren())
			{
				Node n = getNode(id, node);
				if(n != null)
					return n;
			}
		return null;
	}

	public static List<Node> filterNodes(List<Node> asList,
			Class<? extends AbstractGroup>[] filterType) {
		List<Class<? extends AbstractGroup>> groups = Arrays.asList(filterType);
		List<Node> list = new ArrayList<Node>();
		for(Node n : asList)
			if(groups.contains(n.getClass()))
				list.add(n);
		return list;
	}
	
	public static String getMemberPath(Node n, Node rootNode) {
		List<Node> list = getParents(n, rootNode);
		Collections.reverse(list);
		StringBuffer builder = new StringBuffer();

		for (Node node : list) {
			if (builder.length() > 0) {
				builder.append(" > ").append(node.getLabel());
			}
			builder.append(node.getLabel());
		}
        return builder.toString();
	}

	public static List<Node> getMembers(MemberHolder node, Node root) {
		List<Node> list = new ArrayList<Node>();
		for(UUID id : node.getMembers())
			list.add(getNode(id, root));
		return list;
	}
	
	public static List<Node> getMembers(List<UUID> ids, Node root) {
		List<Node> list = new ArrayList<Node>();
		for(UUID id : ids)
			list.add(getNode(id, root));
		return list;
	}

	public static Node getNewNode(Class<? extends Node> nodeType, UUID id) {
		Node node = null;
		try {
			node = nodeType.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		node.setParentId(id);
		return node;
	}

	public static List<Node> getChildren(Node node, Node root,
			boolean b) {
		List<Node> list = getChildren(node, root);
		if(b)
			list.add(node);
		return list;
	}
}
