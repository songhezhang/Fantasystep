package com.fantasystep.component.table;

import java.util.ArrayList;
import java.util.List;

import com.fantasystep.component.common.ItemClickHandler;
import com.fantasystep.component.menu.AbstractMenu;
import com.fantasystep.component.menu.MenuHelper;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class NodeMenuTable extends NodeTable implements MenuHelper {

	private ItemClickHandler handler;

	public NodeMenuTable(Node root, List<Node> nodes,
			Class<? extends Node> nodeType) {
		super(root, nodes, nodeType);
	}

	public VerticalLayout getContent() {
		return getHandler().getContent();
	}

	private ItemClickHandler getHandler() {
		return handler;
	}

	@Override
	public List<Node> getSelectedNodes() {
		return getHandler().getSelectedNodes();
	}

	@Override
	public boolean isHideMenuItem() {
		return false;
	}

	@Override
	public void notify(NodeEvent event) {
		super.notify(event);
		List<Node> list = new ArrayList<Node>(getSelectedNodes());
		for (Node n : list)
			unselect(n);
	}

	public void setMenu(AbstractMenu menu) {
		this.handler = new ItemClickHandler(this, menu);
	}

	@Override
	public void triggerHideMenuItem() {
	}
}
