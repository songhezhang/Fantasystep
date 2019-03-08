package com.fantasystep.container.multiple.relation;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fantasystep.component.panel.ConcreteNodeEvent;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.table.MembersTable;
import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.container.AbstractCContainer;
import com.fantasystep.container.multiple.AbstractTableCContainer;
import com.fantasystep.container.single.SingleNodeCContainer;
import com.fantasystep.domain.AbstractGroup;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.MemberHolder;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.helper.NodeEvent.Action;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.util.CNodeUtil;
import com.fantasystep.utils.NodeUtil;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

public class NodeMembersReadOnlyCContainer extends AbstractTableCContainer
{
	private static final long serialVersionUID = -4902908005321951980L;
	
	private Node				target;
	private Node				root;

	public NodeMembersReadOnlyCContainer( List<Node> members, String title )
	{
		super( members, Action.UPDATE, AbstractGroup.class, FormMode.TAB, title );
		
		setRoot( TreeHandler.getRootNodeByApplication() );
		setTarget( TreeHandler.getTargetNodeByApplication() );

		initDisplay();
	}

	// It will be invoked on Update click even, when somebody will try to update the Memberslist
	@Override
	protected AbstractCContainer getContainer( java.lang.Class<? extends Node> clazz )
	{
		return new SingleNodeCContainer( target, action, FormMode.TAB );
	}

	@Override
	protected HorizontalLayout getLinksLayout()
	{
		HorizontalLayout hz = new HorizontalLayout();
		addButton( hz, nodeClass, String.format( "%s %s", LocalizationHandler.get( LabelUtil.LABEL_UPDATE ), title ) );

		return hz;
	}

	@Override
	protected Table getNodeTable()
	{
		if( nodeTable == null )
		{
			// Note: Source would be false for member case
			nodeTable = new MembersTable( root, target, getNodeList(), Node.class, false );
			nodeTable.setColumnHeader( LocalizationHandler.get( LabelUtil.LABEL_TITLES ), LabelUtil.getDomainLabel( nodeClass ) );
			nodeTable.setHeight( "250px" );
		}
		return nodeTable;
	}

	@SuppressWarnings("unused")
	private Map<Node,String> getPathMap()
	{
		Map<Node,String> pathMap = new HashMap<Node,String>();
		for( Node n : getNodeList() )
			pathMap.put( n, NodeUtil.getMemberPath( n, TreeHandler.getRootNodeByApplication() ) );
		return pathMap;
	}

	@Override
	protected boolean hasPermission( Class<? extends Node> clz )
	{
		return CNodeUtil.getPermissionDescriptor( (Class<? extends Node>) clz ).hasUpdatePermission();
	}

	@Override
	public void notify( NodeEvent event )
	{
		this.root = ( (ConcreteNodeEvent) event ).getRootNode();
		this.target = ( (ConcreteNodeEvent) event ).getTargetNode();
		this.nodeList = NodeUtil.getMembers( (MemberHolder) target, root );

		this.nodeTable = null;

		getHeader().removeAllComponents();
		addHeaderTitle( LabelUtil.getDomainLabel( nodeClass ), CSSUtil.BLACK_FEATURED_TITLE );
		getBodyContainer().removeAllComponents();
		initDisplay();
	}

	public void setRoot( Node rootNode )
	{
		this.root = rootNode;
	}

	public void setTarget( Node targetNode )
	{
		this.target = targetNode;
	}
}
