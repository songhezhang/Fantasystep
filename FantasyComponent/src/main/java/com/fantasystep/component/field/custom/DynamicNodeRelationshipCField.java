package com.fantasystep.component.field.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.field.AbstractMultiNodeCField;
import com.fantasystep.component.panel.Listener;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.table.NodeTable;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class DynamicNodeRelationshipCField extends AbstractMultiNodeCField {

	private List<Node> targetNodes = new ArrayList<Node>();
	private List<Node> allNodes = new ArrayList<Node>();
	private Class<? extends Node> type;

	public DynamicNodeRelationshipCField(
			FieldAttributeAccessor fieldAttributes, List<Node> allNodes,
			Class<? extends Node> type) {
		super(fieldAttributes, allNodes);
		this.allNodes = allNodes;
		this.type = type;
	}

	private void initNodes(List<UUID> ids, List<Node> allNodes) {
		List<Node> list = new ArrayList<Node>();
		targetNodes.clear();
		for (Node n : allNodes)
			if (ids.contains(n.getId()))
				targetNodes.add(n);
			else
				list.add(n);
		setNodes(list);
	}

	@Override
	public void initField() {
		field = new DynamicNodeRelationshipField(
				this.fieldAttributes.getLabel());
	}

	public class DynamicNodeRelationshipField extends AbstractCustomField
			implements ClickListener, Listener {
		private static final long serialVersionUID = 4920073332610208256L;

		private Button mvLeftBtn = new Button(String.format("<< %s ",
				LocalizationHandler.get(LabelUtil.LABEL_REMOVE_ITEMS)));
		private Button mvRightBtn = new Button(String.format("%s >>",
				LocalizationHandler.get(LabelUtil.LABEL_ADD_ITEMS)));
		private NodeTable sourceContainer;
		private NodeTable targetContainer;

		public DynamicNodeRelationshipField(String caption) {
			super(caption, List.class);
			bindRequest();
		}
		
		private void bindRequest() {
			getField().setValue(getTargetContainer().getNodeIds());
			setComponentLayout();

			getSourceContainer().markAsDirty();
			getTargetContainer().markAsDirty();
		}

		@Override
		public void buttonClick(ClickEvent event) {
			if (event.getButton() == mvRightBtn) {
				Node movingNode = (Node)getSourceContainer().getValue();
				if(movingNode == null)
					return;
				getSourceContainer().unselect(movingNode);
				getNodes().remove(movingNode);
				getSourceContainer().setDataSource(getNodes());
				targetNodes.add(movingNode);
				getTargetContainer().setDataSource(targetNodes);
			} else if (event.getButton() == mvLeftBtn) {
				Node movingNode = (Node)getTargetContainer().getValue();
				if(movingNode == null)
					return;
				getTargetContainer().unselect(movingNode);
				getNodes().add(movingNode);
				getSourceContainer().setDataSource(getNodes());
				targetNodes.remove(movingNode);
				getTargetContainer().setDataSource(targetNodes);
			}
			List<UUID> list = new ArrayList<UUID>();
			for(Node n : targetNodes)
				list.add(n.getId());
			getField().setValue(list);
		}

		private NodeTable getSourceContainer() {
			if (sourceContainer == null)
				sourceContainer = new NodeTable(null, getNodes(), type);
			return sourceContainer;
		}

		private NodeTable getTargetContainer() {
			if (targetContainer == null)
				targetContainer = new NodeTable(null, targetNodes, type);
			return targetContainer;
		}

		@Override
		public Object getValue() {
			return getField().getValue();
		}

		@Override
		public void notify(NodeEvent event) {
//			sourceContainer.notify(event);
//			targetContainer.notify(event);
		}

		private void setComponentLayout() {
			mvLeftBtn.setWidth("145px");
			mvRightBtn.setWidth("145px");
			mvLeftBtn.addClickListener((Button.ClickListener) this);
			mvRightBtn.addClickListener((Button.ClickListener) this);

			VerticalLayout vl = new VerticalLayout();
			vl.addComponent(mvRightBtn);
			vl.addComponent(new Label("&nbsp;", ContentMode.HTML)); // space
			vl.addComponent(mvLeftBtn);

			vl.setComponentAlignment(mvRightBtn, Alignment.MIDDLE_CENTER);
			vl.setComponentAlignment(mvLeftBtn, Alignment.MIDDLE_CENTER);
			vl.setWidth("205px");

			HorizontalLayout hz = new HorizontalLayout();
			hz.setWidth("100%");

			setCompositionRoot(hz);

			NodeTable source = getSourceContainer();
			source.setWidth("750px");
			source.setHeight("450px");
			hz.addComponent(source);
			hz.setComponentAlignment(source, Alignment.TOP_LEFT);

			hz.addComponent(vl);
			hz.setComponentAlignment(vl, Alignment.MIDDLE_CENTER);

			NodeTable target = getTargetContainer();
			// Table target = new Table();
			target.setWidth("750px");
			target.setHeight("450px");
			hz.addComponent(target);
			hz.setComponentAlignment(target, Alignment.TOP_RIGHT);
			hz.setExpandRatio(target, 1.0f);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void setValue(Object newValue) throws ReadOnlyException,
				ConversionException {
			List<UUID> ids = (List<UUID>) newValue;
			initNodes(ids, allNodes);
			bindRequest();
			getSourceContainer().setDataSource(getNodes());
			getTargetContainer().setDataSource(targetNodes);
		}
	}
}
