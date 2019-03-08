package com.fantasystep.component.common;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class PopUpModel extends Window
{
	private static final long serialVersionUID = -8068767899917308081L;

	public PopUpModel()
	{
		this( "", 800, 700 );
		setSizeFull();
	}

	public PopUpModel( String title )
	{
		this( title, 800, 600);
		setSizeFull();
	}

	public PopUpModel( String title, int width, int height)
	{
		super( title );
		setModal( true );
		setWidth( width, Unit.PIXELS );
		setHeight( height, Unit.PIXELS );
		center();
	}

	public PopUpModel add( Component component )
	{
		if(getContent() == null)
			setContent(component);
		else ((VerticalLayout)getContent()).addComponent( component );

		return this;
	}
}