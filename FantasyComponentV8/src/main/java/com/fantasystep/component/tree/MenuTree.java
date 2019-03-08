package com.fantasystep.component.tree;

import java.util.ArrayList;
import java.util.List;

import com.fantasystep.domain.Node;
import com.vaadin.ui.Component;

public class MenuTree extends NodeTree {
	private static final long serialVersionUID = 4369367354889982057L;

	public MenuTree(Node node) {
		super(node);
	}

	@Override
	public Component getTree() {
		return getHandler().getContent();
	}

	@Override
	protected void repaintComponent() {
		super.repaintComponent();
		List<Node> list = new ArrayList<Node>(getSelectedNodes());
		for (Node n : list)
			unselect(n);

		select(getHandler().getClickedNode());
	}
}
