package com.fantasystep.container.multiple;


import java.util.ArrayList;
import java.util.List;

import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.container.AbstractCContainer;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent;

public abstract class AbstractMultiNodeCContainer extends AbstractCContainer
{
	private static final long serialVersionUID = -4532303322631247229L;
	
	protected Class<? extends Node>	nodeClass;
	protected List<Node>			nodeList;
	protected String				title;

	public AbstractMultiNodeCContainer( List<Node> nodeList, NodeEvent.Action nodeAction, Class<? extends Node> nodeClass, FormMode mode )
	{
		this( nodeList, nodeAction, nodeClass, mode, LabelUtil.getDomainLabel( nodeClass ) );
	}

	public AbstractMultiNodeCContainer( List<Node> nodeList, NodeEvent.Action nodeAction, Class<? extends Node> nodeClass, FormMode mode, String title )
	{
		super( nodeAction, mode );
		this.nodeList = ( nodeList == null ) ? new ArrayList<Node>() : nodeList;
		this.nodeClass = nodeClass;
		this.title = title;
		addHeaderTitle( this.title, CSSUtil.BLACK_FEATURED_TITLE );
	}

	public AbstractMultiNodeCContainer( NodeEvent.Action action, Class<? extends Node> nodeClass, FormMode mode )
	{
		this( null, action, nodeClass, mode );
	}

	public void addNode( Node node )
	{
		this.nodeList.add( node );
	}

	@Override
	public Class<? extends Node> getNodeClass()
	{
		return nodeClass;
	}

	public List<Node> getNodeList()
	{
		return nodeList;
	}
}
