package com.fantasystep.component.tree;

import java.util.List;

import com.fantasystep.annotation.DomainClass;
import com.fantasystep.component.NodeEnum;
import com.fantasystep.component.common.HierarchicalNodes;
import com.fantasystep.component.common.ItemClickHandler;
import com.fantasystep.component.common.Searchable;
import com.fantasystep.component.menu.MenuHelper;
import com.fantasystep.component.panel.ConcreteNodeEvent;
import com.fantasystep.component.panel.Listener;
import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.LayoutUtil;
import com.fantasystep.component.utils.SearchUtil;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

public class NodeTree extends Tree implements Listener, MenuHelper, Searchable
{
	private static final long serialVersionUID = -8783447505858268152L;

	class TreeCssStyle implements ItemStyleGenerator
	{
		private static final long serialVersionUID = 870147172463895493L;

		@Override
		public String getStyle(Tree source, Object itemId) {
			if( ( (HierarchicalNodes) getContainerDataSource() ).getDeletedNodes().contains( itemId ) )
				return CSSUtil.DELETED_ITALIC_TREE_ITEM;
			else
				return "";
		}
	}

	private List<Class<? extends Node>>	filteredTypes;
	private ItemClickHandler			handler;
	protected HierarchicalNodes			nodesContainer;
	private Node						rootNode;

	public NodeTree( Node node )
	{
		this( node, null );
	}

	public NodeTree( Node rootNode, List<Class<? extends Node>> filteredTypes )
	{
		setRootNode( rootNode );
		this.filteredTypes = filteredTypes;
		setItemStyleGenerator( new TreeCssStyle() );
		setContainerDataSource( getNodesContainer() );
		setItemCaptionPropertyId( LabelUtil.LABEL_NAME );
		setItemIconPropertyId( LabelUtil.LABEL_ICON );
		setImmediate( true );
		setMultiSelect( true );
		setHandler( new ItemClickHandler( this ) );
	}

	@Override
	public void applyContainerFilter( String str )
	{
		( (HierarchicalNodes) NodeTree.this.getContainerDataSource() ).rebuildContainerWithFilterStr( str );
	}

	public void expandTree()
	{
		for( Object item : getNodesContainer().getItemIds() )
			if( !isExpanded( item ) )
				expandItem( item );
	}

	protected ItemClickHandler getHandler()
	{
		return handler;
	}

	protected HierarchicalNodes getNodesContainer()
	{
		if( this.nodesContainer == null )
			this.nodesContainer = new HierarchicalNodes( NodeEnum.NodePropertyType.NON_PROPERTY, filteredTypes, true, getRootNode() );
		return this.nodesContainer;
	}

	public Node getRootNode()
	{
		return rootNode;
	}

	@Override
	public List<Node> getSelectedNodes()
	{
		return getHandler().getSelectedNodes();
	}

	protected Component getTree()
	{
		return this;
	}

	public Component getTreeWithSearch()
	{
		VerticalLayout vl = new VerticalLayout();
		vl.addComponent( LayoutUtil.wrapSearchLayout( SearchUtil.getSearchBox( this ) ) );
		vl.addComponent( getTree() );

		return vl;
	}

	@Override
	public boolean isHideMenuItem()
	{
		return ( (HierarchicalNodes) this.getContainerDataSource() ).isHide();
	}

	@Override
	public void notify( NodeEvent event )
	{
//		NodeUtil.updateTree( getRootNode(), event.getNode(), event.getAction() );
		setRootNode( ( (ConcreteNodeEvent) event ).getRootNode() );
		if( event.getNode().getClass().getAnnotation( DomainClass.class ).isPropertyNode() )
			return;

		repaintComponent();
	}

	// public Component wrappedSearchBox()
	// {
	// VerticalLayout searchPanel = new VerticalLayout();
	// searchPanel.setSizeFull();
	// searchPanel.setStyleName( CSSUtil.SEARCH_PANEL );
	// searchPanel.setHeight( "45px" );
	// searchPanel.addComponent( getSearchBox() );
	//
	// return searchPanel;
	// }

	@Override
	public void removeContainerFilter()
	{

	}

	protected void repaintComponent()
	{
		getNodesContainer().removeAllItems();
		getNodesContainer().getDeletedNodes().clear();
		if( getNodesContainer().getFilterStr() != null && !getNodesContainer().getFilterStr().isEmpty() )
			getNodesContainer().rebuildContainerWithFilterStr( getNodesContainer().getFilterStr() );
		else
		{
			getNodesContainer().buildContainer( getRootNode(), getRootNode() );
			getNodesContainer().sort();
			this.expandTree();
		}
	}

	public void setHandler( ItemClickHandler handler )
	{
		this.handler = handler;
	}

	public void setRootNode( Node rootNode )
	{
		this.rootNode = rootNode;
	}

	@Override
	public void triggerHideMenuItem()
	{
		( (HierarchicalNodes) this.getContainerDataSource() ).setHide( !( (HierarchicalNodes) this.getContainerDataSource() ).isHide() );
		repaintComponent();
	}
}
