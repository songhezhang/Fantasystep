package com.fantasystep.container.multiple.account;

import com.fantasystep.container.single.AbstractNodeCContainer;
import com.fantasystep.container.single.SingleNodeCContainer;
import com.fantasystep.domain.AbstractAccount;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.helper.NodeEvent.Action;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.utils.NodeUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class CheckboxAccountCContainer extends AbstractAccountCContainer
{

	public CheckboxAccountCContainer( NodeEvent.Action action, FormMode mode, Class<? extends Node> currentNodeClass )
	{
		super( action, mode, currentNodeClass );
	}

	@Override
	protected Component getNodeComponent( Class<? extends AbstractAccount> account )
	{
		CheckBox chk = new CheckBox( getLabel( account ) );
		chk.setValue( getAccount(account) != null );
		chk.setImmediate( true );
		chk.addValueChangeListener( new ValueChangeListener()
		{
			@Override
			public void valueChange( ValueChangeEvent event )
			{
				if( (Boolean) event.getProperty().getValue() )
				{
				} else
				{
				}
			}
		} );
		return chk;
	}

	@Override
	protected AbstractNodeCContainer getNodeContainer( Class<? extends AbstractAccount> clazz )
	{
		Node node = NodeUtil.getNewNode( clazz, TreeHandler.getTargetNodeByApplication().getId() );
		this.getNodeList().add( node );
		return new SingleNodeCContainer( node, Action.INSERT, mode );
	}

}
