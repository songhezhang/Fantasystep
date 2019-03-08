package com.fantasystep.component.panel;

import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent;

@SuppressWarnings("serial")
public class ConcreteNodeEvent extends NodeEvent
{
	private Node	rootNode;

	private Node	targetNode;

	public ConcreteNodeEvent( NodeEvent event, Node rootNode, Node targetNode )
	{
		super( event.getAction(), event.getNode() );
		this.rootNode = rootNode;
		this.targetNode = targetNode;
	}

	public Node getRootNode()
	{
		return rootNode;
	}

	public Node getTargetNode()
	{
		return targetNode;
	}
}