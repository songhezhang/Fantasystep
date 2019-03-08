package com.fantasystep.container.multiple.relation;


import java.util.List;

import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.container.multiple.AbstractTableCContainer;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent.Action;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.util.CNodeUtil;
import com.vaadin.ui.HorizontalLayout;

public abstract class AbstractMembersReadOnlyCContainer extends AbstractTableCContainer
{
	private static final long serialVersionUID = 5882818415738204123L;
	
	protected Node	targetNode;
	protected Node	rootNode;

	public AbstractMembersReadOnlyCContainer( List<Node> members, Class<? extends Node> clazz )
	{
		this( members, clazz, LabelUtil.getDomainLabel( clazz ) );
	}

	public AbstractMembersReadOnlyCContainer( List<Node> members, Class<? extends Node> clazz, String title )
	{
		super( members, Action.UPDATE, clazz, FormMode.TAB, title );
		targetNode = TreeHandler.getTargetNode();
		rootNode = TreeHandler.getRootNode();
	}

	@Override
	protected boolean hasPermission( Class<? extends Node> clz )
	{
		return CNodeUtil.getPermissionDescriptor( (Class<? extends Node>) clz ).hasUpdatePermission();
	}

	@Override
	protected HorizontalLayout getLinksLayout()
	{
		HorizontalLayout hz = new HorizontalLayout();
		addButton( hz, nodeClass, String.format( "%s %s", LocalizationHandler.get( LabelUtil.LABEL_UPDATE ), title ) );

		return hz;
	}
}
