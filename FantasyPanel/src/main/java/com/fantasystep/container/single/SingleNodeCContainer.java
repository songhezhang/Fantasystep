package com.fantasystep.container.single;

import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent.Action;
import com.vaadin.ui.Alignment;

public class SingleNodeCContainer extends AbstractNodeCContainer
{
	private static final long serialVersionUID = 8933198309111241322L;

	public SingleNodeCContainer( Node node, Action action, FormMode mode )
	{
		this( node, action, mode, true );
	}

	public SingleNodeCContainer( Node node, Action action, FormMode mode, boolean withSaveAndCancelButtons )
	{
		super( node, action, mode );
		this.withSaveAndCancelButtons = withSaveAndCancelButtons;
		getForm();
	}

	@Override
	protected void initDisplay()
	{
		addBodyComponent( getForm(), Alignment.TOP_LEFT );
	}
}
