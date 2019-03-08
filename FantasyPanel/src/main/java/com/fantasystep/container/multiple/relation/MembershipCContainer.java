package com.fantasystep.container.multiple.relation;


import java.util.List;

import com.fantasystep.component.MemberHelper;
import com.fantasystep.component.NodeEnum;
import com.fantasystep.component.common.Searchable;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.table.AbstractMembersTable;
import com.fantasystep.component.table.MembersTable;
import com.fantasystep.component.tree.MembersTree;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.SearchUtil;
import com.fantasystep.component.utils.UINodeUtil;
import com.fantasystep.domain.AbstractGroup;
import com.fantasystep.domain.Group;
import com.fantasystep.domain.Node;
import com.fantasystep.domain.Organization;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.utils.NodeUtil;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class MembershipCContainer extends AbstractMembersHolderCContainer
{

	public MembershipCContainer( List<Node> nodeList, Node targetNode, FormMode mode )
	{
		this( nodeList, targetNode, mode, true );
	}

	public MembershipCContainer( List<Node> nodeList, Node targetNode, FormMode mode, boolean withSaveAndCancelButtons )
	{
		super( nodeList, targetNode, AbstractGroup.class, mode );
		this.withSaveAndCancelButtons = withSaveAndCancelButtons;
		getSourceContainer().addState( nodeList, NodeEnum.NodeState.NOT_AVAILABLE );
		validateNodes();
		initDisplay();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected MemberHelper getSourceContainer()
	{
		if( sourceContainer == null )
		{
			sourceContainer = new MembersTree( TreeHandler.getRootNode(), Node.class, Group.class, Organization.class );
			( (MembersTree) sourceContainer ).expandTree();
			// ( (MembersTree) sourceContainer ).setWidth( "100%" ); 
		}
		return sourceContainer;
	}

	// Note: we need to refactor this wrapper
	@Override
	protected Component getSourceWrapper()
	{
		return SearchUtil.wrapTitleHeaderWithSearch( (Searchable) getSourceContainer(), LabelUtil.getDomainLabel( nodeClass ), 410, 300 );
	}

	@Override
	protected AbstractMembersTable getTargetContainer()
	{
		if( targetContainer == null )
		{
			targetContainer = new MembersTable( TreeHandler.getRootNode(), TreeHandler.getTargetNode(), getNodeList(), this.nodeClass );
			targetContainer.setColumnHeader( LocalizationHandler.get( LabelUtil.LABEL_TITLES ), LabelUtil.getDomainLabel( nodeClass ) );
			targetContainer.setVisible( true );
		}
		return targetContainer;
	}

	private boolean isParentExists( Node nd )
	{
		if( getTargetContainer().getDataSource().getItemIds().contains( nd ) )
			return true;

		if( nd != null && nd.getParentId() != null )
		{
			Node pn = NodeUtil.getNode( nd.getParentId(), TreeHandler.getRootNodeByApplication() );
			return isParentExists( pn );
		}
		return false;
	}

	@Override
	protected boolean isSourceEmpty()
	{
		return ( (MembersTree) getSourceContainer() ).size() < 1;
	}

	@Override
	protected boolean isValidMove()
	{
		for( Node n : getSourceContainer().getSelectedNodes() )
			if( isParentExists( n ) )
				return false;

		for( Node n : getSourceContainer().getSelectedNodes() )
			if( !getSourceContainer().isValidNode( n ) )
				return false;

		return true;
	}

	@Override
	protected void validateNodes()
	{
		Node target = TreeHandler.getTargetNodeByApplication();
		Node root = TreeHandler.getRootNodeByApplication();
		List<Node> invalids = UINodeUtil.getParentsAndChildren( target, root );

		// if target node does not contain windows account then disable all windows accounts.
		// because non window node can never be a member of windows node
		if( !UINodeUtil.hasWindowAccount( target ) )
			for( Object n : ( (MembersTree) getSourceContainer() ).getItemIds() )
				if( !invalids.contains( n ) && UINodeUtil.hasWindowAccount( (Node) n ) )
					invalids.add( (Node) n );

		getSourceContainer().addState( invalids, NodeEnum.NodeState.INVALID );
	}
}
