package com.fantasystep.container.multiple.relation;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fantasystep.component.MemberHelper;
import com.fantasystep.component.NodeEnum;
import com.fantasystep.component.common.Searchable;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.table.AbstractMembersTable;
import com.fantasystep.component.table.ResourceTable;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.SearchUtil;
import com.fantasystep.domain.Node;
import com.fantasystep.domain.Resource;
import com.fantasystep.panel.TreeHandler;
import com.vaadin.ui.Component;

public class ResourceCContainer extends AbstractMembersHolderCContainer
{
	private static final long	serialVersionUID	= -3622559848661217366L;

	/**
	 * @param assignedResources
	 *            : already granted resources to user/group
	 */

	public ResourceCContainer( List<Node> assignedResources, Node targetNode, Class<? extends Resource> abstractClazz, FormMode mode )
	{
		this( assignedResources, targetNode, abstractClazz, mode, true );
	}

	public ResourceCContainer( List<Node> assignedResources, Node targetNode, Class<? extends Resource> abstractClazz, FormMode mode, boolean withSaveAndCancelButtons )
	{
		super( assignedResources, targetNode, abstractClazz, mode );
		this.withSaveAndCancelButtons = withSaveAndCancelButtons;
		getSourceContainer().addState( assignedResources, NodeEnum.NodeState.NOT_AVAILABLE );
		validateNodes();
		initDisplay();
	}

	private List<Resource> getResources()
	{
		try
		{
			@SuppressWarnings("unchecked")
			List<Resource> resources = TreeHandler.get().getResourcesByClass( (Class<? extends Resource>) this.nodeClass );

			return resources;
		} catch( Exception e )
		{
			TreeHandler.handleTreeException( e );
		}
		return null;
	}

	@Override
	protected MemberHelper getSourceContainer()
	{
		if( sourceContainer == null )
		{
			sourceContainer = new ResourceTable( TreeHandler.getRootNode(), TreeHandler.getTargetNode(), Arrays.asList( getResources().toArray( new Node[] {} ) ), this.nodeClass, false );
			( (ResourceTable) sourceContainer ).setPageLength( getResources().size() );
			( (ResourceTable) sourceContainer ).setColumnHeader( LocalizationHandler.get( LabelUtil.LABEL_TITLES ),
					String.format( "%s %s", LocalizationHandler.get( LabelUtil.LABEL_ADD ), LabelUtil.getDomainLabel( nodeClass ) ) );
			( (ResourceTable) sourceContainer ).setVisible( true );
			( (ResourceTable) sourceContainer ).setImmediate( true );

		}
		return sourceContainer;
	}

	@Override
	protected Component getSourceWrapper()
	{
		return SearchUtil.wrapTitleHeaderWithSearch( (Searchable) sourceContainer, LabelUtil.getDomainLabel( nodeClass ), 410, 300 );
	}

	@Override
	protected AbstractMembersTable getTargetContainer()
	{
		if( targetContainer == null )
		{
			targetContainer = new ResourceTable( TreeHandler.getRootNode(), TreeHandler.getTargetNode(), getNodeList(), this.nodeClass );
			targetContainer.setColumnHeader( LocalizationHandler.get( LabelUtil.LABEL_TITLES ), LabelUtil.getDomainLabel( nodeClass ) );
			targetContainer.setVisible( true );
		}
		return targetContainer;
	}

	@Override
	protected boolean isSourceEmpty()
	{
		return ( (ResourceTable) getSourceContainer() ).size() < 1;
	}

	@Override
	protected boolean isValidMove()
	{
		for( Node n : getSourceContainer().getSelectedNodes() )
			if( !getSourceContainer().isValidNode( n ) )
				return false;

		return true;
	}

	@Override
	protected void validateNodes()
	{
		List<Node> invalids = new ArrayList<Node>();
		for( Object o : ( (ResourceTable) getSourceContainer() ).getItemIds() )
			if( !( (Resource) o ).allows( TreeHandler.getTargetNodeByApplication() ) )
				invalids.add( (Node) o );

		getSourceContainer().addState(invalids, NodeEnum.NodeState.INVALID);
	}
}
