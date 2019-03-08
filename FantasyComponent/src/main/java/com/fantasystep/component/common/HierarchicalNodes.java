package com.fantasystep.component.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fantasystep.annotation.DomainClass;
import com.fantasystep.component.NodeEnum.NodePropertyType;
import com.fantasystep.component.utils.IconUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.NodeSorter;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.PropertyHelper;
import com.fantasystep.utils.NodeUtil;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.Resource;

public class HierarchicalNodes extends HierarchicalContainer implements Sortable
{
	private static final long serialVersionUID = 7550647571550840113L;
	
	private Set<Node>					deletedNodes		= new HashSet<Node>();
	private List<Class<? extends Node>>	filteredTypes;
	private String						filterStr;
	private boolean						isHide;
	private NodePropertyType			nodePropertyType;

	private Node[]						roots;

	public HierarchicalNodes( NodePropertyType nodePropertyType, boolean isHide, Node... roots )
	{
		this( nodePropertyType, null, isHide, roots );
	}

	public HierarchicalNodes( NodePropertyType nodePropertyType, List<Class<? extends Node>> filteredTypes, boolean isHide, Node... roots )
	{
		this.nodePropertyType = nodePropertyType;
		this.roots = roots;
		this.filteredTypes = filteredTypes;
		setHide( isHide );

		addContainerProperty( LabelUtil.LABEL_NAME, String.class, null );
		addContainerProperty( LabelUtil.LABEL_ICON, Resource.class, null );

		for( String p : getCustomProperties() )
			addContainerProperty( p, String.class, null );
		for( Node n : this.roots )
			buildContainer( n, n );
		this.sort();
	}

	@SuppressWarnings("unchecked")
	public void bindCustomedProperty( Item item, Node node )
	{
		item.getItemProperty( LabelUtil.LABEL_NAME ).setValue( node.getLabel() );
		item.getItemProperty( LabelUtil.LABEL_ICON ).setValue( IconUtil.getSmallSizeIcon(node) );
		for( String property : getCustomProperties() )
			item.getItemProperty( property ).setValue( getPropertyValue( node, property ) );
	}

	public void buildContainer( Node parent, Node node )
	{
		buildContainer( parent, node, null );
	}

	private void buildContainer( Node parent, Node node, Map<Node,Boolean> map )
	{
		if( node.isDeleted() )
			deletedNodes.add( node );

		if( this.filteredTypes != null && !this.filteredTypes.contains( node.getClass() ) )
			return;

		if( isHide() && node.isDeleted() )
			return;

		if( map != null && !map.containsKey( node ) )
			return;

		Item item = addItem( node );
		if( item != null )
			bindCustomedProperty( item, node );

		if( !parent.equals( node ) )
			setParent( node, parent );
		
		if( getValidChildrens( node ).size() > 0 )
			for( Node child : getValidChildrens( node ) )
				buildContainer( node, child, map );
		else
			setChildrenAllowed( node, false );
	}

	public Set<String> getCustomProperties()
	{
		return new HashSet<String>();
	}

	public Set<Node> getDeletedNodes()
	{
		return deletedNodes;
	}

	public String getFilterStr()
	{
		return filterStr;
	}

	private Object getPropertyValue( Node nd, String key )
	{
		StringBuilder builder = new StringBuilder();

		if( nd instanceof PropertyHelper )// getting values from current node
			for( String prop : ( (PropertyHelper) nd ).getPropertyMappings().get( key ) )
			{
				BeanItem<Node> bean = new BeanItem<Node>( nd );
				Object val = bean.getItemProperty( prop ).getValue();
				builder.append( String.format( "%s ", ( val == null ) ? "" : val ) );
			}
		return builder.toString();
	}

	private List<Node> getValidChildrens( Node node )
	{
		List<Node> list = new ArrayList<Node>();
		for( Node n : node.getChildren() )
//			if( isValidNodePropertyType( n.getType() ) )
				list.add( n );

		return list;
	}

	private void handleFilter( Node root, Map<Node,Boolean> map, String filterStr )
	{
		if( root.getClass().getAnnotation( DomainClass.class ).isPropertyNode() )
			return;

		if( this.filteredTypes != null && !this.filteredTypes.contains( root.getClass() ) )
			return;

		if( root.getLabel() == null )
			return;

		if( root.getLabel().toLowerCase().indexOf( filterStr.toLowerCase() ) != -1 )
			validParent( root, map );

		for( Node child : root.getChildren() )
			handleFilter( child, map, filterStr );
	}

	public boolean isHide()
	{
		return isHide;
	}

	@SuppressWarnings("unused")
	private boolean isValidNodePropertyType( Class<? extends Node> nodeClazz )
	{
		if(nodeClazz.getAnnotation( DomainClass.class ) == null)
			return false;
		return nodePropertyType.equals( NodePropertyType.PROPERTY );
	}

	public void rebuildContainerWithFilterStr( String filterStr )
	{
		this.filterStr = filterStr;
		removeAllItems();
		getDeletedNodes().clear();
		if( filterStr.trim().isEmpty() )
		{
			for( Node n : this.roots )
				buildContainer( n, n );
		} else
		{
			for( Node n : this.roots )
			{
				Map<Node,Boolean> map = new HashMap<Node,Boolean>();
				handleFilter( n, map, filterStr );
				buildContainer( n, n, map );
			}
		}
		this.sort();
	}

	public void setHide( boolean isHide )
	{
		this.isHide = isHide;
	}

	public void sort()
	{
		setItemSorter( new NodeSorter() );
		sort( new Object[] { LabelUtil.LABEL_NAME }, new boolean[] { true } );
	}

	private void validParent( Node node, Map<Node,Boolean> map )
	{
		map.put( node, true );
		Node parent = null;
		for( Node root : roots )
		{
			parent = NodeUtil.getNode( node.getParentId(), root );
			if( parent != null )
				break;
		}
		if( parent != null && !map.containsKey( parent ) )
			validParent( parent, map );
	}
}