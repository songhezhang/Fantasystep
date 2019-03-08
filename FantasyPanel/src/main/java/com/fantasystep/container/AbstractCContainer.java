package com.fantasystep.container;


import com.fantasystep.component.layout.SimpleContainerLayout;
import com.fantasystep.component.panel.Listener;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.helper.NodeEvent.Action;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public abstract class AbstractCContainer extends SimpleContainerLayout implements ClickListener, Listener
{
	public enum FormMode
	{
		SINGLE_FORM, TAB, WIZARD
	}

	protected NodeEvent.Action	action;
	protected Button			cancel	= new Button( LocalizationHandler.get( LabelUtil.LABEL_CANCEL ), this );

	protected Button			edit	= new Button( LocalizationHandler.get( LabelUtil.LABEL_EDIT ), this );

	protected FormMode			mode;
	protected Button			save	= new Button( LocalizationHandler.get( LabelUtil.LABEL_SAVE ), this );

	protected boolean			withSaveAndCancelButtons;

	public AbstractCContainer( NodeEvent.Action action, FormMode mode )
	{
		this.action = action;
		this.mode = mode;
		setMargin( true );
		getHeader().setStyleName( CSSUtil.GRANDIENT_BOTTOM_LINE );
	}

	public Action getAction()
	{
		return action;
	}

	protected abstract void initDisplay();

	protected abstract void initForm();
	
	public abstract Class<? extends Node> getNodeClass();

	@Override
	public void setReadOnly( boolean readOnly )
	{
		save.setVisible( !readOnly );
		cancel.setVisible( !readOnly );
		edit.setVisible( readOnly );
	}

	public boolean validContainerForm()
	{
		// For child class to override.
		return true;
	}
	
	protected void closeWindow() {
		UI.getCurrent().getWindows().toArray(new Window[]{})[UI.getCurrent().getWindows().size() - 1].close();
	}
}