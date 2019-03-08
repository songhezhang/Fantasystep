package com.fantasystep.helper;

import java.io.Serializable;

import com.fantasystep.domain.Node;

public class NodeEvent implements Serializable {

	private static final long serialVersionUID = 6656306311236441853L;
	
	private Action action;
	private Node node;

	public NodeEvent(Action action, Node node) {
		this.action = action;
		this.node = node;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public static enum Action {
		INSERT, UPDATE, DELETE, UNDELETE, DESTROY
	}
}
