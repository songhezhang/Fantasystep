package com.fantasystep.component.utils;

import com.fantasystep.annotation.AnnotationsParser;
import com.fantasystep.component.common.PopUpModel;
import com.fantasystep.domain.AbstractGroup;
import com.fantasystep.domain.Node;
import com.fantasystep.domain.Resource;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class LayoutUtil
{

	public final static int	CONST_MEMBERS_SIZE		= 1380;
	public final static int	PERCENTAGE_LARGE_SIZE	= 90;	// must apply in percentage
	public final static int	PERCENTAGE_MEDIUM_SIZE	= 50;	// must apply in percentage

	public static VerticalLayout addContainerLayout( Component component, Alignment alignment )
	{
		VerticalLayout l = new VerticalLayout();
		l.setSizeFull();
		l.setMargin(true);
		l.addComponent( component );
		l.setComponentAlignment( component, alignment );
		return l;
	}

	public static Panel addScrollablePanel( Component component )
	{
		return addScrollablePanel( "", component, false, null );
	}

	public static Panel addScrollablePanel( Component component, boolean isTransparent )
	{
		return addScrollablePanel( "", component, isTransparent, null );
	}

	public static Panel addScrollablePanel( Component component, boolean isTransparent, Integer height )
	{
		return addScrollablePanel( "", component, isTransparent, height );
	}

	public static Panel addScrollablePanel( Component component, Integer height )
	{
		return addScrollablePanel( "", component, false, height );
	}

	public static Panel addScrollablePanel( String caption, Component component, boolean isTransparent, Integer height )
	{
		Panel p = new Panel();
		p.setCaption( caption );
		p.setSizeFull();
		p.setContent( component );
		if( isTransparent )
			p.setStyleName( CSSUtil.TRANSPARENT_PANEL );

		if( height != null )
			p.setHeight( height + "px" );

		return p;
	}

	public static String wrapFieldsLayout( Node n )
	{
		BeanItem<Node> bean = new BeanItem<Node>( n );

		String s = "";
		for( Object ob : AnnotationsParser.getUIControlFieldNames( n.getClass() ) )
		{
			Object val = bean.getItemProperty( ob ).getValue();

			if( !( val instanceof Boolean ) )
				s = String.format( "%s %s", s, bean.getItemProperty( ob ).getValue() );
		}

		return s;
	}

	// only returns search layout
	public static Component wrapSearchLayout( Component source )
	{
		VerticalLayout searchPanel = new VerticalLayout();
		searchPanel.setSizeFull();
		searchPanel.setStyleName( CSSUtil.SEARCH_PANEL );
		searchPanel.setHeight( "45px" );
		searchPanel.addComponent( source );

		return searchPanel;
	}

	public static void wrapSize( PopUpModel popup, Class<? extends Node> nodeClass )
	{
		if( Resource.class.isAssignableFrom( nodeClass ) || AbstractGroup.class.isAssignableFrom( nodeClass ) )
			popup.setWidth( LayoutUtil.PERCENTAGE_LARGE_SIZE + "%" );
		else
			popup.setWidth( LayoutUtil.PERCENTAGE_MEDIUM_SIZE + "%" );
	}

}
