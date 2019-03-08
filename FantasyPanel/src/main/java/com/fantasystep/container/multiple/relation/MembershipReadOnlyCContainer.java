package com.fantasystep.container.multiple.relation;


import java.util.List;

import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.table.MembersTable;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.UINodeUtil;
import com.fantasystep.container.AbstractCContainer;
import com.fantasystep.domain.AbstractGroup;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.panel.TreeHandler;
import com.vaadin.ui.Table;

public class MembershipReadOnlyCContainer extends AbstractMembersReadOnlyCContainer
{
	private static final long serialVersionUID = 5394944415246327924L;

	public MembershipReadOnlyCContainer( List<Node> members, String title )
	{
		super( members, AbstractGroup.class, title );
		initDisplay();
	}

	@Override
	protected AbstractCContainer getContainer( java.lang.Class<? extends Node> clazz )
	{
		return new MembershipCContainer( getNodeList(), TreeHandler.getTargetNode(), FormMode.TAB );
	}

	@Override
	protected Table getNodeTable()
	{
		if( nodeTable == null )
		{
			nodeTable = new MembersTable( TreeHandler.getRootNode(), TreeHandler.getTargetNode(), getNodeList(), Node.class, true );
			nodeTable.setColumnHeader( LocalizationHandler.get( LabelUtil.LABEL_TITLES ), LabelUtil.getDomainLabel( nodeClass ) );
			nodeTable.setHeight( "250px" );
		}
		return nodeTable;
	}

	// private Map<Node,String> getPathMap()
	// {
	// Map<Node,String> pathMap = new HashMap<Node,String>();
	// for( Node n : getNodeList() )
	// pathMap.put( n, NodeUtil.getMemberPath( n, TreeHandler.getRootNodeByApplication( getApplication() ) ) );
	// return pathMap;
	// }

	@SuppressWarnings("unchecked")
	@Override
	public void notify( NodeEvent event )
	{
		Node targetNode = TreeHandler.getTargetNodeByApplication();
		Node root = TreeHandler.getRootNodeByApplication();

		this.nodeList = UINodeUtil.getValidatedMemberships( targetNode, root, AbstractGroup.class );

		( (MembersTable) getNodeTable() ).setNodes( nodeList );
		( (MembersTable) getNodeTable() ).notify( event );

		// we can use the same way like ResourceReadOnlyContainer
	}
}
