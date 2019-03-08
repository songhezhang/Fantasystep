package com.fantasystep.util;

import java.util.ArrayList;
import java.util.List;

import com.fantasystep.domain.Node;
import com.fantasystep.domain.Resource;
import com.fantasystep.domain.User;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.panel.CApplication;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.persistence.exception.PermissionDeniedException;
import com.fantasystep.persistence.exception.PersistenceException;
import com.fantasystep.persistence.exception.UnauthorizedException;
import com.fantasystep.utils.NodeUtil;
import com.fantasystep.utils.PermissionUtil;
import com.fantasystep.utils.PermissionUtil.PermissionDescriptor;

public class CNodeUtil {

	public static List<Node> getAssignedResourcesByClass(Node node,
			Class<? extends Resource> filterType) {
		try {
			List<Node> resources = new ArrayList<Node>();
			for (Resource r : TreeHandler.get().getAssignedResourcesByClass(
					node, filterType))
				if (r.allows(node))
					resources.add(r);

			return resources;
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		} catch (PersistenceException e) {
			e.printStackTrace();
		} catch (PermissionDeniedException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean hasWindowsResources(Node node) {
		// try
		// {
		// for( Resource r : TreeHandler.get().getAssignedResources( node ) )
		// for( Class<? extends AbstractAccount> clazz : r.getRequirements() )
		// if( AbstractWindowsAccount.class.isAssignableFrom( clazz ) )
		// return true;
		// } catch( UnauthorizedException e )
		// {
		// e.printStackTrace();
		// } catch( PersistenceException e )
		// {
		// e.printStackTrace();
		// } catch( PermissionDeniedException e )
		// {
		// e.printStackTrace();
		// }

		return false;
	}

	public static boolean isRelatedToWindowsGroupOrResource(Node node) {
//		Node root = TreeHandler.getRootNode();
		// if( hasWindowsResources( node ) )
		// return true;

//		if (UINodeUtil.hasWindowsMemberships(node, root, AbstractGroup.class))
//			return true;

		return false;
	}

	public static PermissionDescriptor getPermissionDescriptor(
			List<Node> nodeList) {
		User user = TreeHandler.getUserNode();
		Node root = TreeHandler.getRootNode();

		List<PermissionDescriptor> descriptors = new ArrayList<PermissionDescriptor>();

		for (Node n : nodeList)
			descriptors.add(PermissionUtil.getPermissionDescriptor(user, n,
					n.getClass(), root));

		return PermissionDescriptor.mergePermissionDescriptiors(descriptors);
	}

	public static PermissionDescriptor getPermissionDescriptor(
			Class<? extends Node> clazz) {
		User user = TreeHandler.getUserNode();
		Node root = TreeHandler.getRootNode();
		Node target = TreeHandler.getTargetNode();

		return PermissionUtil
				.getPermissionDescriptor(user, target, clazz, root);
	}

	public static void updateTree(Node root, Node newNode,
			NodeEvent.Action action) {
		synchronized (CApplication.getCurrent()) {
			Node oldNode = NodeUtil.getNode(newNode.getId(), root);
			Node parent = NodeUtil.getNode(newNode.getParentId(), root);
			try {
				switch (action) {
				case DELETE:
					parent.removeChild(oldNode);
					newNode.setDeleted(true);

					for (Node n : newNode.getChildren())
						n.setDeleted(true);

					parent.addChild(newNode);

					break;
				case UNDELETE:
					parent.removeChild(oldNode);
					newNode.setDeleted(false);

					for (Node n : newNode.getChildren())
						n.setDeleted(false);

					parent.addChild(newNode);
					break;

				case UPDATE:
					Node oldParent = NodeUtil.getNode(oldNode.getParentId(),
							root);
					oldParent.removeChild(oldNode);
					parent.addChild(newNode);
					break;

				case INSERT:
					parent.addChild(newNode);
					break;

				case DESTROY:
					parent.removeChild(oldNode);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void updateTable(Node newNode, List<Node> list,
			NodeEvent.Action action, Class<? extends Node> clazz) {
		Node oldNode = null;
		for (Node node : list)
			if (node.getId().equals(newNode.getId())) {
				oldNode = node;
				break;
			}
		switch (action) {
		case DELETE:
		case UNDELETE:
		case UPDATE:
			if (oldNode != null) {
				list.remove(oldNode);
				list.add(newNode);
			}
			break;
		case INSERT:
			boolean hasSameParent = false;
			for (Node node : list)
				if (node.getParentId().equals(newNode.getParentId())
						&& clazz.isAssignableFrom(newNode.getClass())) {
					hasSameParent = true;
					break;
				}
			if (hasSameParent)
				list.add(newNode);
			break;
		case DESTROY:
			if (oldNode != null)
				list.remove(oldNode);
			break;
		}
	}
}
