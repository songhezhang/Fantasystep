package com.fantasystep.container.multiple.relation;


import java.util.List;

import com.fantasystep.component.panel.ConcreteNodeEvent;
import com.fantasystep.component.table.ResourceTable;
import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.container.AbstractCContainer;
import com.fantasystep.domain.Node;
import com.fantasystep.domain.Resource;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.util.CNodeUtil;
import com.vaadin.ui.Table;

public class ResourceReadOnlyCContainer extends AbstractMembersReadOnlyCContainer
{
	private static final long serialVersionUID = -1750321538344332402L;

	public ResourceReadOnlyCContainer( List<Node> members, Class<? extends Node> clazz )
	{
		super( members, clazz, LabelUtil.getDomainLabel( clazz ) );
		initDisplay();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected AbstractCContainer getContainer( java.lang.Class<? extends Node> clazz )
	{
		return new ResourceCContainer( getNodeList(), TreeHandler.getTargetNode(), (Class<? extends Resource>) clazz, FormMode.TAB );
	}

	@Override
	protected Table getNodeTable()
	{
		if( nodeTable == null )
		{
			nodeTable = new ResourceTable( rootNode, this.targetNode, getNodeList(), nodeClass );
			nodeTable.setHeight( "250px" );
		}
		return nodeTable;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void notify( NodeEvent event )
	{
		this.rootNode = ( (ConcreteNodeEvent) event ).getRootNode();
		this.targetNode = ( (ConcreteNodeEvent) event ).getTargetNode();

		this.nodeList = CNodeUtil.getAssignedResourcesByClass( this.targetNode, (Class<? extends Resource>) nodeClass );
		this.nodeTable = null;

		getHeader().removeAllComponents();
		addHeaderTitle( LabelUtil.getDomainLabel( nodeClass ), CSSUtil.BLACK_FEATURED_TITLE );
		getBodyContainer().removeAllComponents();
		initDisplay();
	}
}
