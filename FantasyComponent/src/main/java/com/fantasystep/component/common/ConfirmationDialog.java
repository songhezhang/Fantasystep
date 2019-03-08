package com.fantasystep.component.common;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public final class ConfirmationDialog extends Window implements Button.ClickListener
{
	private static final long serialVersionUID = 6140729028428943743L;

	public interface ConfirmationDialogCallback
	{
		void response( boolean ok );
	}

	private static final String					CONFIRMATION_DIALOG_HEIGHT	= "200px";
	private static final String					CONFIRMATION_DIALOG_WIDTH	= "320px";
	private static final String					ONE_HUNDRED_PERCENT			= "100%";
	private final ConfirmationDialogCallback	callback;
	private final Button						cancelButton;

	private final Button						okButton;

	public ConfirmationDialog( final String caption, final String question, final String okLabel, final String cancelLabel, final ConfirmationDialogCallback callback )
	{
		super( caption );
		setWidth( CONFIRMATION_DIALOG_WIDTH );
		setHeight( CONFIRMATION_DIALOG_HEIGHT );
		okButton = new Button( okLabel, this );
		cancelButton = new Button( cancelLabel, this );
		setModal( true );

		this.callback = callback;

		if( question != null )
			((Layout)this.getContent()).addComponent( new Label( question ) );

		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing( true );
		buttonLayout.addComponent( okButton );
		buttonLayout.addComponent( cancelButton );
		((Layout)this.getContent()).addComponent( buttonLayout );
		( (VerticalLayout) getContent() ).setHeight( ONE_HUNDRED_PERCENT );
		( (VerticalLayout) getContent() ).setComponentAlignment( buttonLayout, Alignment.BOTTOM_CENTER );
	}

	public void buttonClick( final ClickEvent event )
	{
		if( getParent() != null )
			this.detach();
		callback.response( event.getSource() == okButton );
	}
}
