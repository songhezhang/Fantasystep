package com.fantasystep.component.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.fantasystep.component.menu.AbstractMenu;
import com.fantasystep.domain.Node;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

public class ItemClickHandler implements ValueChangeListener, ItemClickListener
{
	private static final long serialVersionUID = -2235606859137908168L;
	
	private Node			clickedNode;
	private AbstractSelect	component;
	private AbstractMenu	menu;
	private List<Node>		selectedNodes	= new ArrayList<Node>();

	public ItemClickHandler( AbstractSelect component )
	{
		this( component, null );
	}

	public ItemClickHandler( AbstractSelect component, AbstractMenu menu )
	{
		this.menu = menu;
		this.component = component;
		component.addValueChangeListener( this );

		if( component instanceof Table )
			( (Table) component ).addItemClickListener( (ItemClickListener) this );

		if( component instanceof Tree )
			( (Tree) component ).addItemClickListener( (ItemClickListener) this );
	}

	public Node getClickedNode()
	{
		return clickedNode;
	}

	private VerticalLayout layout = null;
	
	public VerticalLayout getContent()
	{
		if(layout == null) {
			layout = new VerticalLayout();
			layout.addComponent( component );
			if( menu != null )
				menu.setAsContextMenuOf(component);
		}
		return layout;
	}

	public List<Node> getSelectedNodes()
	{
		return selectedNodes;
	}

	private void handleRightClick( ItemClickEvent event )
	{
		if( menu != null )
			menu.showMenu( event.getClientX(), event.getClientY() );
	}

	protected void handlLeftClick( List<Node> selectedNodes )
	{

	}

	private boolean isValidParentChildCriterian()
	{
		if( getSelectedNodes() != null && getSelectedNodes().size() > 1 )
			for( Node n : getSelectedNodes() )
				for( Node c : getSelectedNodes() )
					if( n.getChildren().contains( c ) )
						return false;
		return true;
	}

	@Override
	public void itemClick( ItemClickEvent event )
	{
		this.clickedNode = (Node) event.getItemId();
		if( event.getSource().equals( this.component ) )
		{
			if( event.getButton() == MouseButton.RIGHT )
			{
				if( !getSelectedNodes().contains( this.clickedNode ) )
				{
					for( Node n : new ArrayList<Node>( getSelectedNodes() ) )
						this.component.unselect( n );
					this.component.select( this.clickedNode );
				}

				getSelectedNodes().remove( this.clickedNode );
				getSelectedNodes().add( this.clickedNode );

				handleRightClick( event );
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void valueChange( Property.ValueChangeEvent event )
	{
		AbstractSelect tr = (AbstractSelect) event.getProperty();

		if( tr.getValue() == null )
			return;

		if( tr.getValue().equals( this.selectedNodes ) )
			return;

		this.selectedNodes.clear();

		if( !( tr.getValue() instanceof Collection ) )
			this.selectedNodes.add( (Node) tr.getValue() );

		else if( ( (Set<?>) tr.getValue() ).size() > 0 )
		{
			this.selectedNodes.addAll( (Collection<? extends Node>) tr.getValue() );
			if( menu != null && !isValidParentChildCriterian() )
			{
				this.selectedNodes.removeAll( (Collection<? extends Node>) tr.getValue() );
				for( Node n : (Collection<? extends Node>) tr.getValue() )
					this.component.unselect( n );
			}
		}

		if( this.selectedNodes.isEmpty() )
			return;

		if( this.selectedNodes.size() == 1 )
			this.clickedNode = this.selectedNodes.get( 0 );

		handlLeftClick( this.selectedNodes );

	}
}
