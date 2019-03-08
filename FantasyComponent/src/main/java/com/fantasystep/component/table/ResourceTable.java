package com.fantasystep.component.table;

import java.util.ArrayList;
import java.util.List;

import com.fantasystep.component.common.Searchable;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.MemberHolder;
import com.fantasystep.helper.NodeEvent;
import com.vaadin.data.Item;

public class ResourceTable extends AbstractMembersTable implements Searchable {
	private static final long serialVersionUID = -2831818806388074194L;

	public ResourceTable(Node root, Node target, List<Node> list,
			Class<? extends Node> nodeType) {
		this(root, target, list, nodeType, true);
	}

	public ResourceTable(Node root, Node target, List<Node> list,
			Class<? extends Node> nodeType, boolean isSource) {
		super(root, target, list, nodeType, isSource);
		setMultiSelect(true);

		List<String> visCols = new ArrayList<String>();
		visCols.add(LocalizationHandler.get(LabelUtil.LABEL_TITLES));

		if (this.isSource)
			visCols.add(LocalizationHandler.get(LabelUtil.LABEL_SOURCE));

		setVisibleColumns(visCols.toArray());
	}

	@Override
	public void applyContainerFilter(final String str) {
		getDataSource().addContainerFilter(new Filter() {
			private static final long serialVersionUID = 1834758266992675261L;

			@Override
			public boolean appliesToProperty(Object propertyId) {
				return propertyId != null
						&& propertyId.equals(LocalizationHandler
								.get(LabelUtil.LABEL_TITLES));
			}

			@Override
			public boolean passesFilter(Object itemId, Item item)
					throws UnsupportedOperationException {
				if (str.equalsIgnoreCase(""))
					return true;

				if (itemId instanceof Node)
					return (((Node) itemId).getLabel().toString().toLowerCase()
							.trim().contains(str.trim().toLowerCase()));
				return true;
			}
		});
	}

	@Override
	public void notify(NodeEvent event) {
		getDataSource().removeAllItems();
		setDataSource(getNodes());

		super.notify(event);
	}

	@Override
	public void removeContainerFilter() {
		getDataSource().removeAllContainerFilters();
	}

	@Override
	public void setSource(List<Node> members) {
		for (Node m : members)
			if (m instanceof MemberHolder
					&& targetNode.getId().equals(m.getParentId()))
				sourceMap.put(m, new SourceHolder(SourceType.OWNED_BY_PARENT,
						targetNode));
		super.setSource(members);
	}
}
