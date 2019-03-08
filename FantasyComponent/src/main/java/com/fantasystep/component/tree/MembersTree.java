package com.fantasystep.component.tree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fantasystep.component.MemberHelper;
import com.fantasystep.component.NodeEnum;
import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.domain.Node;
import com.vaadin.ui.Tree;

@SuppressWarnings("serial")
public class MembersTree extends NodeTree implements MemberHelper
{
	class ItemStateStyle implements ItemStyleGenerator
	{
		@Override
		public String getStyle( Tree source, Object itemId )
		{
			if( !getStatesMap().isEmpty() && getStatesMap().get( itemId ) != null )
			{
				if( getStatesMap().get( itemId ).equals( NodeEnum.NodeState.INVALID ) )
					return CSSUtil.INVALID_ITALIC_TREE_ITEM; // will change it into gray

				if( getStatesMap().get( itemId ).equals( NodeEnum.NodeState.NOT_AVAILABLE ) )
					return CSSUtil.NOT_AVAILABLE_ITALIC_TREE_ITEM; // will change it into green

			}
			return "";
		}
	}

	protected Map<Node,NodeEnum.NodeState>	nodesStates	= new HashMap<Node,NodeEnum.NodeState>();

	public MembersTree( Node rootNode, @SuppressWarnings("unchecked") Class<? extends Node>... filteredTypes )
	{
		this( rootNode, Arrays.asList( filteredTypes ) );
		setItemStyleGenerator( new ItemStateStyle() );
	}

	public MembersTree( Node rootNode, List<Class<? extends Node>> filteredTypes )
	{
		super( rootNode, filteredTypes );
	}

	@Override
	public void addState( List<Node> nodes, NodeEnum.NodeState state )
	{
		for( Node node : nodes )
			getStatesMap().put( node, state );
		markAsDirty();
	}

	@Override
	public Map<Node,NodeEnum.NodeState> getStatesMap()
	{
		return nodesStates;
	}

	@Override
	public boolean isValidNode( Node node )
	{
		return !( getStatesMap().get( node ) != null && ( getStatesMap().get( node ).equals( NodeEnum.NodeState.INVALID ) || getStatesMap().get( node ).equals( NodeEnum.NodeState.NOT_AVAILABLE ) ) );
	}

	@Override
	public void removeState( List<Node> nodes )
	{
		for( Node n : nodes )
			getStatesMap().remove( n );
		markAsDirty();
	}
}
