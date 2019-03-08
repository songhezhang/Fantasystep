package com.fantasystep.component.field.custom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.NodeEnum;
import com.fantasystep.component.field.AbstractMultiNodeCField;
import com.fantasystep.component.panel.Listener;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.table.MembersTable;
import com.fantasystep.component.tree.MembersTree;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.MembersUiUtil;
import com.fantasystep.component.utils.SearchUtil;
import com.fantasystep.component.utils.UINodeUtil;
import com.fantasystep.domain.Group;
import com.fantasystep.domain.Node;
import com.fantasystep.domain.User;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.utils.NodeUtil;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

public class NodeMembersCField extends AbstractMultiNodeCField {

	public class NodeMembersField extends AbstractCustomField implements
			ClickListener, Listener {
		
		private static final long serialVersionUID = -4295280756923161004L;
		
		private List<Node> memberNodes;
		private Button mvLeftBtn = new Button(String.format("<< %s ",
				LocalizationHandler.get(LabelUtil.LABEL_REMOVE_ITEMS)));
		private Button mvRightBtn = new Button(String.format("%s >>",
				LocalizationHandler.get(LabelUtil.LABEL_ADD_ITEMS)));
		private MembersTree sourceContainer;
		private MembersTable targetContainer;

		/**
		 * @param caption
		 *            - caption of field
		 * @param memberNodes
		 *            - default nodes for targetContainer - user is already the
		 *            member of provided nodes
		 */
		public NodeMembersField(String caption, List<Node> memberNodes) {
			super(caption, List.class);
			this.memberNodes = memberNodes; // member nodes will be passed to
											// targetcontainer
			bindRequest();
		}

		@SuppressWarnings("unchecked")
		private void bindRequest() {
			getField().setValue(getTargetContainer().getNodesIds());
			setComponentLayout();
			// setting up differnt states for already existing nodes
			getSourceContainer().addState(
					new ArrayList<Node>(getTargetContainer().getDataSource()
							.getItemIds()), NodeEnum.NodeState.NOT_AVAILABLE);
			validateNodes();
			getSourceContainer().expandTree();
			getSourceContainer().markAsDirty();
		}

		@Override
		public void buttonClick(ClickEvent event) {
			if (event.getButton() == mvRightBtn) {
				if (isValidMove()) {
					// move nodes to targetContainer
					getTargetContainer().addNodes(
							getSourceContainer().getSelectedNodes());
					// change nodes states in sourceContainer
					getSourceContainer().addState(
							new ArrayList<Node>(getSourceContainer()
									.getSelectedNodes()),
							NodeEnum.NodeState.NOT_AVAILABLE);

					// updating field value
					getField().setValue(getTargetContainer().getNodesIds());

				} else
					Notification.show(
							LocalizationHandler.get(LabelUtil.LABEL_ALERT)
									+ "!", LocalizationHandler
									.get(LabelUtil.LABEL_INVALID_MOVE),
							Type.ERROR_MESSAGE);
			} else if (event.getButton() == mvLeftBtn) {
				// remove nodes from targetContainer
				getTargetContainer().removeSelectedNodes();
				// nodes states will be changed
				getSourceContainer().removeState(
						getTargetContainer().getSelectedNodes());

				// updating field value
				getField().setValue(getTargetContainer().getNodesIds());
			}
		}

		@SuppressWarnings("unchecked")
		private MembersTree getSourceContainer() {
			if (sourceContainer == null)
				this.sourceContainer = new MembersTree(rootNode, Node.class,
						(Class<? extends Node>) User.class,
						(Class<? extends Node>) Group.class);
			return sourceContainer;
		}

		private Component getSourceWrapper() {
			return SearchUtil.wrapTitleHeaderWithSearch(getSourceContainer(),
					LocalizationHandler.get(LabelUtil.LABEL_MEMBERS), 410, 300);
		}

		private MembersTable getTargetContainer() {
			if (targetContainer == null) {
				targetContainer = new MembersTable(rootNode, targetNode,
						this.memberNodes, Node.class, false);
				targetContainer.setColumnHeader(LabelUtil.LABEL_TITLES,
						LocalizationHandler.get(LabelUtil.LABEL_MEMBERS));
				targetContainer.setVisible(true);
			}
			return targetContainer;
		}

		@Override
		public Object getValue() {
			return getTargetContainer().getNodesIds();
		}

		private boolean isParentExists(Node nd) {
			if (getTargetContainer().getDataSource().getItemIds().contains(nd))
				return true;

			if (nd != null && nd.getParentId() != null) {
				Node pn = NodeUtil.getNode(nd.getParentId(), rootNode);
				return isParentExists(pn);
			}
			return false;
		}

		private boolean isValidMove() {
			for (Node nd : getSourceContainer().getSelectedNodes())
				if (isParentExists(nd))
					return false;
			for (Node nd : getSourceContainer().getSelectedNodes())
				if (getSourceContainer().getStatesMap().get(nd) != null
						&& getSourceContainer().getStatesMap().get(nd)
								.equals(NodeEnum.NodeState.INVALID)) {
					return false;
				}
			return true;
		}

		@Override
		public void notify(NodeEvent event) {
			sourceContainer.notify(event);
			targetContainer.notify(event);
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

			VerticalLayout wrapper = new VerticalLayout();
			HorizontalLayout hz = new HorizontalLayout();
			hz.setWidth("100%");

			wrapper.addComponent(hz);
			wrapper.setSpacing(true);
			wrapper.addComponent(MembersUiUtil.getHorizontalLagend());

			setCompositionRoot(wrapper);

			Component source = getSourceWrapper();
			hz.addComponent(source);
			hz.setComponentAlignment(source, Alignment.TOP_LEFT);

			hz.addComponent(vl);
			hz.setComponentAlignment(vl, Alignment.MIDDLE_CENTER);

			MembersTable target = getTargetContainer();
			target.setWidth("750px");
			target.setHeight("450px");
			hz.addComponent(target);
			hz.setComponentAlignment(target, Alignment.TOP_LEFT);
			hz.setExpandRatio(target, 1.0f);

		}

		private void setNodes(List<Node> nodes) {
			this.memberNodes = nodes;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void setValue(Object newValue) throws ReadOnlyException,
				ConversionException {
			if (newValue instanceof Collection) {
				setNodes(NodeUtil.getMembers(new ArrayList((Collection) newValue), rootNode));
				bindRequest();
			}
		}

		private void validateNodes() {
			List<Node> invalids = new ArrayList<Node>();//UINodeUtil.getParentsAndChildren(targetNode, rootNode);
			// if target node does not contain windows account then disable all
			// windows accounts.
			// because non window node can never be a member of windows node
			if (UINodeUtil.hasWindowAccount(targetNode)) {
				for (Object n : getSourceContainer().getItemIds())
					if (!invalids.contains(n)
							&& !UINodeUtil.hasWindowAccount((Node) n))
						invalids.add((Node) n);
			}

			// Note window nodes can be a member of non windows
			getSourceContainer().addState(invalids, NodeEnum.NodeState.INVALID);
		}
	}

	private Node rootNode;

	private Node targetNode;

	public NodeMembersCField(Node rootNode, Node targetNode,
			FieldAttributeAccessor fieldAttributes, List<Node> nodes) {
		super(fieldAttributes, nodes);
		this.rootNode = rootNode;
		this.targetNode = targetNode;
	}

	@Override
	public void initField() {
		field = new NodeMembersField(this.fieldAttributes.getLabel(),
				getNodes());
	}
}
