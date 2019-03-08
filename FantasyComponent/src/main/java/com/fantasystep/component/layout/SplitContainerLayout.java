package com.fantasystep.component.layout;

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;

public class SplitContainerLayout extends AbtractContainerLayout
{
	private static final long serialVersionUID = -1817453164718510395L;
	
	private HorizontalSplitPanel	horiPanel;
	private VerticalLayout			leftComponent		= new VerticalLayout();

	public SplitContainerLayout()
	{
		super();
		( (HorizontalSplitPanel) getBodyContainer() ).setFirstComponent( leftComponent );
		// leftComponent.setMargin( true );
		bindLayout();
	}

	public void addLeftComponent( Component component, Alignment alignment )
	{
		leftComponent.addComponent( component );
		leftComponent.setComponentAlignment( component, alignment );
	}

	// Note: added temporarily
	public void addRightHeader( Component component, Alignment alignment )
	{

	}

	//
	// public void addRightComponent( Component component, Alignment alignment )
	// {
	// ( (HorizontalSplitPanel) getBodyContainer() ).setSecondComponent( leftComponent );
	// }

	@Override
	public AbstractComponentContainer getBodyContainer()
	{
		if( horiPanel == null )
		{
			horiPanel = new HorizontalSplitPanel();
			horiPanel.setSplitPosition( 20 );
			horiPanel.setSizeFull();

		}
		return horiPanel;
	}

}
