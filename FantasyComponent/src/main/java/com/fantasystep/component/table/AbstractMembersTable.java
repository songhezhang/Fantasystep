package com.fantasystep.component.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.fantasystep.component.MemberHelper;
import com.fantasystep.component.NodeEnum;
import com.fantasystep.component.common.ItemClickHandler;
import com.fantasystep.component.panel.Listener;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.UINodeUtil;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.MemberHolder;
import com.fantasystep.helper.NodeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public abstract class AbstractMembersTable extends NodeTable implements
		MemberHelper, Listener {

	private class CellStyle implements CellStyleGenerator {
		private static final long serialVersionUID = -2036656761991690843L;

		@Override
		public String getStyle(Table source, Object itemId, Object propertyId) {
			if (!nodesStates.isEmpty() && nodesStates.get(itemId) != null) {
				if (nodesStates.get(itemId).equals(
						NodeEnum.NodeState.NOT_AVAILABLE))
					return CSSUtil.NOT_AVAILABEL_ITALIC_TABLE_ITEM;

				if (nodesStates.get(itemId).equals(NodeEnum.NodeState.INVALID))
					return CSSUtil.INVALID_ITALIC_TABLE_ITEM;
			}
			return "";
		}
	}

	protected class SourceColumn implements ColumnGenerator {
		private static final long serialVersionUID = 960476481420793520L;

		@Override
		public Component generateCell(Table source, Object itemId,
				Object columnId) {
			SourceHolder h = sourceMap.get(itemId);
			String str = "";

			if (h != null) {
				String creater = (h.getSourceNode() != null) ? h
						.getSourceNode().getLabel() : "?";

				switch (h.getType()) {
				case INHERITED:
					str = CSSUtil.wrapStyle(String.format("%s %s %s", h
							.getType().getLabel(), LocalizationHandler
							.get(LabelUtil.LABEL_FROM), creater),
							CSSUtil.YELLOW_ITALIC_TEXT);
					break;
				case PERSONAL:
					str = CSSUtil.wrapStyle(h.getType().getLabel(),
							CSSUtil.GREEN_ITALIC_TEXT);
					break;
				case OWNED_BY_PARENT:
					str = CSSUtil.wrapStyle(String.format("%s %s", h.getType()
							.getLabel(), creater), CSSUtil.BLUE_ITALIC_TEXT);
					break;
				default:
					break;
				}
			} else
				str = String.format("%s", SourceType.OUT_OF_SCOPE.getLabel());

			return new Label(str, ContentMode.HTML);
		}
	}

	class SourceHolder {
		private Node sourceNode;
		private SourceType type;

		public SourceHolder(SourceType type, Node sourceNode) {
			super();
			this.type = type;
			this.sourceNode = sourceNode;
		}

		private Node getSourceNode() {
			return sourceNode;
		}

		private SourceType getType() {
			return type;
		}
	}

	public enum SourceType {
		INHERITED("LABEL_INHERITED"), OUT_OF_SCOPE("?"), OWNED_BY_PARENT(
				"LABEL_OWNED_BY"), PERSONAL("LABEL_PERSONAL");

		private String label;

		private SourceType(String label) {
			this.label = label;
		}

		private String getLabel() {
			return LocalizationHandler.get(label);
		}
	}

	private static final long serialVersionUID = 1L;
	protected ItemClickHandler handler = new ItemClickHandler(this);
	protected boolean isSource;
	protected Map<Node, NodeEnum.NodeState> nodesStates = new HashMap<Node, NodeEnum.NodeState>();
	protected Node rootNode;

	protected Map<Node, SourceHolder> sourceMap = new HashMap<Node, SourceHolder>();
	protected Node targetNode;

	/**
	 * @param list
	 *            - default loaded list
	 */

	public AbstractMembersTable(Node rootNode, Node targetNode,
			List<Node> list, Class<? extends Node> nodeType) {
		this(rootNode, targetNode, list, nodeType, false);
	}

	public AbstractMembersTable(Node rootNode, Node targetNode,
			List<Node> list, Class<? extends Node> nodeType, boolean isSource) {
		super(rootNode, list, nodeType);
		this.rootNode = rootNode;
		this.targetNode = targetNode;

		setColumnReorderingAllowed(false);
		setColumnCollapsingAllowed(false);

		addGeneratedColumn(LocalizationHandler.get(LabelUtil.LABEL_TITLES),
				new TitleGenerator());
//		setColumnWidth( LocalizationHandler.get( LabelUtil.LABEL_TITLES ), 350 );

		setCellStyleGenerator(new CellStyle());
		setVisibleColumns(new Object[] { LocalizationHandler
				.get(LabelUtil.LABEL_TITLES) });

		if (isSource) {
			this.isSource = isSource;
			setSource(list);
			addGeneratedColumn(LocalizationHandler.get(LabelUtil.LABEL_SOURCE),
					new SourceColumn());
		}
	}

	public void addNodes(List<Node> selectedNodes) {
		if (isSource)
			setSource(selectedNodes, SourceType.PERSONAL);

		for (Node n : selectedNodes)
			super.addItem(n);
	}

	@Override
	public void addState(List<Node> nodes, NodeEnum.NodeState state) {
		for (Node node : nodes)
			getStatesMap().put(node, state);

		// repainting is not working here, its better to remove and assign items
		// again for repainting.
		getDataSource().removeAllItems();
		setDataSource(getNodes());

	}

	public List<UUID> getNodesIds() {
		List<UUID> list = new ArrayList<UUID>();
		for (Object item : getDataSource().getItemIds())
			list.add(((Node) item).getId());
		return list;
	}

	@Override
	public List<Node> getSelectedNodes() {
		return this.handler.getSelectedNodes();
	}

	@Override
	public Map<Node, NodeEnum.NodeState> getStatesMap() {
		return nodesStates;
	}

	@Override
	public boolean isValidNode(Node node) {
		return !(getStatesMap().get(node) != null && (getStatesMap().get(node)
				.equals(NodeEnum.NodeState.INVALID) || getStatesMap().get(node)
				.equals(NodeEnum.NodeState.NOT_AVAILABLE)));
	}

	@Override
	public void notify(NodeEvent event) {
		if (isSource) {
			setSource(new ArrayList<Node>(getNodes()));
			removeGeneratedColumn(LocalizationHandler
					.get(LabelUtil.LABEL_SOURCE));
			addGeneratedColumn(LocalizationHandler.get(LabelUtil.LABEL_SOURCE),
					new SourceColumn());
		}

		markAsDirty();
	}

	public void removeSelectedNodes() {
		synchronized (getSelectedNodes()) {
			Iterator<Node> it = getSelectedNodes().iterator();
			while (it.hasNext())
				getDataSource().removeItem(it.next());
		}
		markAsDirty();
	}

	@Override
	public void removeState(List<Node> nodes) {
		for (Node n : nodes)
			getStatesMap().remove(n);

		// temporarily added
		getDataSource().removeAllItems();
		setDataSource(getNodes());
	}

	public void setSource(List<Node> members) {
		for (Node m : members)
			if (m instanceof MemberHolder
					&& ((MemberHolder) m).getMembers().contains(
							targetNode.getId()))
				sourceMap.put(m, new SourceHolder(SourceType.PERSONAL,
						targetNode));

		for (Entry<MemberHolder, Node> e : UINodeUtil.getInheritedNodesMap(
				members, targetNode, rootNode).entrySet())
			sourceMap.put((Node) e.getKey(), new SourceHolder(
					SourceType.INHERITED, e.getValue()));
	}

	public void setSource(List<Node> selectedNodes, SourceType type) {
		for (Node n : selectedNodes)
			sourceMap.put(n, new SourceHolder(type, targetNode));
	}
}
