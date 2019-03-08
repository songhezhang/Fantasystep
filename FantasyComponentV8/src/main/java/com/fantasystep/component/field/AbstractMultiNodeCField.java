package com.fantasystep.component.field;

import java.util.ArrayList;
import java.util.List;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.domain.Node;

abstract public class AbstractMultiNodeCField extends AbstractCField
{
	// This list keeps all the nodes in this component
	private List<Node>			nodes;
	// This list keeps all the selected nodes
	private List<Node>			selectedNodes;

	public AbstractMultiNodeCField( FieldAttributeAccessor fieldAttributes, List<Node> nodes )
	{
		super( fieldAttributes );
		this.nodes = nodes;
	}

	public List<Node> getNodes()
	{
		if( nodes == null )
			nodes = new ArrayList<Node>();
		return nodes;
	}

	public List<Node> getSelectedNodes()
	{
		return selectedNodes;
	}

	public void setNodes( List<Node> nodes )
	{
		this.nodes = nodes;
	}

	public void setSelectedNodes( List<Node> selectedNodes )
	{
		this.selectedNodes = selectedNodes;
	}

}
