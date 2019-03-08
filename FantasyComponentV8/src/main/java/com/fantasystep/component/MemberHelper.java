package com.fantasystep.component;

import java.util.List;
import java.util.Map;

import com.fantasystep.domain.Node;

public interface MemberHelper
{
	public void addState( List<Node> nodes, NodeEnum.NodeState state );

	public List<Node> getSelectedNodes();

	public Map<Node,NodeEnum.NodeState> getStatesMap();

	public boolean isValidNode( Node node );

	public void removeState( List<Node> nodes );
}
