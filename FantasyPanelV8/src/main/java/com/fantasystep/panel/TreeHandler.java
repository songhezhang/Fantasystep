package com.fantasystep.panel;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.soap.SOAPFaultException;

import org.json.JSONObject;

import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.domain.Node;
import com.fantasystep.domain.User;
import com.fantasystep.exception.ValidationFailedException;
import com.fantasystep.persistence.TreeManagerSubclassHolder;
import com.fantasystep.persistence.client.TreeManagerDescriptor;
import com.fantasystep.persistence.exception.PermissionDeniedException;
import com.fantasystep.persistence.exception.PersistenceException;
import com.fantasystep.persistence.exception.RequiredFieldMissingException;
import com.fantasystep.persistence.exception.UnauthorizedException;
import com.fantasystep.persistence.exception.UniqueViolateException;
import com.fantasystep.utils.JSON2NodeUtil;
import com.fantasystep.utils.NodeClassUtil;
import com.fantasystep.utils.NodeUtil;
import com.fantasystep.utils.Option;
import com.fantasystep.utils.Option.InvalidOptionFormatException;
import com.fantasystep.utils.Option.URLOption;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

public class TreeHandler {

	public static String APPLICATION_BASE_PATH = "basepath";
	private static Map<String, Object> property;

	private static TreeManagerDescriptor treeManagerDescriptor = new TreeManagerDescriptor();

	static {
		try {
			Option.setConfigFileName("/etc/fantasystep/ui-treehandler.conf");

			URLOption TREEMANAGER_ADDRESS = new URLOption(
					"treemanager.address",
//					"http://192.168.99.100:8080/persistence-1.0.0/cxf/TreeManagerService?wsdl",
					"http://192.168.99.100:8080/persistence/cxf/TreeManagerService?wsdl",
					true, "TreeManager address");

			Option.load();

			treeManagerDescriptor
					.setServiceAddress(TREEMANAGER_ADDRESS.value());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InvalidOptionFormatException e) {
			e.printStackTrace();
		}
		property = new HashMap<String, Object>();
	}

	public static void addProperty(String propertyId, Object value) {
		property.put(propertyId, value);
	}

	public static TreeManagerSubclassHolder get() {
		return ((CApplication) UI.getCurrent()).getTreeManager();
	}

	public static TreeManagerDescriptor getDescriptor() {
		return treeManagerDescriptor;
	}

	public static String getApplicationBasePath() {
		return (String) getProperty(APPLICATION_BASE_PATH);
	}

	public static Object getProperty(String propertyId) {
		return property.get(propertyId);
	}

	public static Node getRootNode() {
		if (CApplication.getCurrent() == null)
			return null;
		return (Node) VaadinSession.getCurrent().getSession()
				.getAttribute(Node.ROOT_NODE_PROPERTY);
	}

	public static Node getRootNodeByApplication() {
		if (UI.getCurrent() == null)
			return getRootNode();
		else if ((Node) VaadinSession.getCurrent().getSession()
				.getAttribute(Node.ROOT_NODE_PROPERTY) != null)
			return (Node) VaadinSession.getCurrent().getSession()
					.getAttribute(Node.ROOT_NODE_PROPERTY);
		// else
		// try {
		// return TreeHandler.get().getUserTree();
		// } catch (UnauthorizedException e) {
		// e.printStackTrace();
		// } catch (PersistenceException e) {
		// e.printStackTrace();
		// } catch (PermissionDeniedException e) {
		// e.printStackTrace();
		// }
		return null;
	}

	public static Node getTargetNode() {
		if (CApplication.getCurrent() == null)
			return null;
		return (Node) VaadinSession.getCurrent().getSession()
				.getAttribute(Node.TARGET_NODE_PROPERTY);
	}

	public static Node getTargetNodeByApplication() {
		if (UI.getCurrent() == null)
			return getTargetNode();
		else if ((Node) VaadinSession.getCurrent().getSession()
				.getAttribute(Node.TARGET_NODE_PROPERTY) != null)
			return (Node) VaadinSession.getCurrent().getSession()
					.getAttribute(Node.TARGET_NODE_PROPERTY);
		// else
		// try
		// {
		// return TreeHandler.get().getUserTree();
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
		return null;
	}

	public static User getUserNode() {
		if (CApplication.getCurrent() == null)
			return null;
		return (User) VaadinSession.getCurrent().getSession()
				.getAttribute(User.CURRENT_USER_PROPERTY);
	}

	public static User getUserNodeByApplication() {
		if (UI.getCurrent() == null)
			return getUserNode();
		else if ((User) VaadinSession.getCurrent().getSession()
				.getAttribute(User.CURRENT_USER_PROPERTY) != null)
			return (User) VaadinSession.getCurrent().getSession()
					.getAttribute(User.CURRENT_USER_PROPERTY);
		// else
		// try
		// {
		// return TreeHandler.get().getUser();
		// } catch( UnauthorizedException e )
		// {
		// e.printStackTrace();
		// }
		return null;
	}

	public static void handleTreeException(Exception e) {

		if (e instanceof UniqueViolateException)
			Notification.show(LocalizationHandler
					.get(LabelUtil.LABEL_UNIQUE_VOILATE_ERROR),
					Type.WARNING_MESSAGE);
		else if (e instanceof RequiredFieldMissingException)
			Notification.show(
					LocalizationHandler.get(LabelUtil.LABEL_VALIDATE_REQUIRED),
					Type.WARNING_MESSAGE);
		else if (e instanceof ValidationFailedException)
			Notification.show(
					LocalizationHandler.get(LabelUtil.LABEL_VALIDATE_FAILED),
					Type.WARNING_MESSAGE);
		else if (e instanceof UnauthorizedException)
			Notification.show(LocalizationHandler
					.get(LabelUtil.LABEL_NOT_AUTHORIZED_TREE_MANAGER_ACTION),
					Type.WARNING_MESSAGE);
		else if (e instanceof PersistenceException)
			Notification.show(
					LocalizationHandler.get(LabelUtil.LABEL_ACTION_FAILED),
					Type.WARNING_MESSAGE);
		else if (e instanceof SOAPFaultException)
			Notification.show(
					LocalizationHandler.get(LabelUtil.LABEL_ACTION_FAILED),
					Type.WARNING_MESSAGE);
		else if (e instanceof PermissionDeniedException)
			Notification.show(
					LocalizationHandler.get(LabelUtil.LABEL_PERMISSION_DENEID),
					Type.WARNING_MESSAGE);
		else if (e instanceof InvalidValueException)
			Notification.show(
					LocalizationHandler.get(LabelUtil.LABEL_VALIDATE_FAILED),
					Type.WARNING_MESSAGE);
		else
			Notification.show(
					LocalizationHandler.get(LabelUtil.LABEL_ACTION_FAILED),
					Type.WARNING_MESSAGE);

		e.printStackTrace();
	}

	public static void removeProperty(String propertyId) {
		property.remove(propertyId);
	}

	public static void setApplicationBasePath(String path) {
		addProperty(APPLICATION_BASE_PATH, path);
	}

	public static void updateTreeFromStorage() {
		updateTreeFromStorage(false);
	}
	public static void updateTreeFromStorage(boolean updateDynamicClasses) {
		try {
			Node root = TreeHandler.get().getUserTree();
			VaadinSession.getCurrent().getSession()
					.setAttribute(Node.ROOT_NODE_PROPERTY, root);
			if(updateDynamicClasses)
				((CApplication)UI.getCurrent()).refreshDynamicClassesMap();
			for(Node node : NodeUtil.getChildren(root, root)) {
				if(node.getSerializationNode() != null) {
					List<Node> children = node.getChildren();
					Node n = JSON2NodeUtil.json2Node(node.getSerializationNode(), NodeClassUtil.getDynamicEntityClassByFullName(new JSONObject(node.getSerializationNode()).get("type").toString()));
					n.setChildren(children);
					Node parent = NodeUtil.getNode(node.getParentId(), root);
					parent.removeChild(node);
					try {
						parent.addChild(n);
					} catch (ValidationFailedException e) {
						e.printStackTrace();
					}
				}
			}
			if (TreeHandler.getTargetNode() == null)
				VaadinSession.getCurrent().getSession()
						.setAttribute(Node.TARGET_NODE_PROPERTY, root);
			else
				VaadinSession.getCurrent().getSession()
						.setAttribute(
								Node.TARGET_NODE_PROPERTY,
								NodeUtil.getNode(TreeHandler.getTargetNode(),
										root));
			VaadinSession.getCurrent().getSession()
					.setAttribute(User.CURRENT_USER_PROPERTY,
							TreeHandler.get().getUser());
		} catch (UnauthorizedException e1) {
			Notification.show(String.format("%s%s",
					LocalizationHandler.get(LabelUtil.LABEL_NOT_AUTHORIZED),
					"!"), LocalizationHandler
					.get(LabelUtil.LABEL_NOT_AUTHORIZED_TO_ACCESS_PAGE),
					Type.ERROR_MESSAGE);
		} catch (PersistenceException e1) {
			e1.printStackTrace();
		} catch (PermissionDeniedException e1) {
			Notification.show(String.format(
					LocalizationHandler.get(LabelUtil.LABEL_FAILED), "!"),
					LocalizationHandler
							.get(LabelUtil.LABEL_USER_PASSWORD_INCORRECT),
					Type.ERROR_MESSAGE);
		}
	}
}
