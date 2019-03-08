package com.fantasystep.component.layout;

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public abstract class AbtractContainerLayout extends VerticalLayout
{
	private static final long serialVersionUID = 4526117590372269013L;
	private VerticalLayout		footer				= new VerticalLayout();
	private VerticalLayout		header				= new VerticalLayout();
	private Label				lbltitle			= new Label();

	public AbtractContainerLayout()
	{
		super();
		setMargin( false );
		setSizeFull();
	}

	public void addFooter( Component component, Alignment alignment )
	{
		getFooter().addComponent( component );
		getFooter().setComponentAlignment( component, alignment );

	}

	public void addHeader( Component component, Alignment alignment )
	{
		getHeader().addComponent( component );
		getHeader().setComponentAlignment( component, alignment );
	}

	public void addHeaderTitle( String title, String styleName )
	{
		lbltitle.setValue( title );
		lbltitle.setStyleName( styleName );
		getHeader().addComponent( lbltitle );
		getHeader().setComponentAlignment( lbltitle, Alignment.TOP_LEFT );
	}

	protected void bindLayout()
	{
		addComponent( getHeader() );
		addComponent( getBodyContainer() );
		addComponent( getFooter() );
		setExpandRatio( getBodyContainer(), 1f );
	}

	public abstract AbstractComponentContainer getBodyContainer();

	public VerticalLayout getFooter()
	{
		return footer;
	}

	public VerticalLayout getHeader()
	{
		return header;
	}
}
