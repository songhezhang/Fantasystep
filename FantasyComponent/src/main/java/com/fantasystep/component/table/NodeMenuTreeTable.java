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
public class NodeMenuTreeTable extends NodeTreeTable implements MenuHelper {
	private ItemClickHandler handler;

	public NodeMenuTreeTable(Class<? extends Node> nodeClass,
			List<Node> nodeList) {
		super(nodeClass, nodeList);
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
