package com.fantasystep.form;


import java.util.HashMap;
import java.util.Map;

import com.fantasystep.component.panel.Listener;
import com.fantasystep.container.AbstractCContainer;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.helper.NodeEvent.Action;
import com.vaadin.ui.VerticalLayout;

public class AbstractCForm extends VerticalLayout implements Listener
{
	private static final long serialVersionUID = -7263252940886932860L;
	
	protected NodeEvent.Action				action;
	private Map<String,AbstractCContainer>	containersMap;
	protected int							currentPage	= 0;

	protected Map<String,String>			formLabel	= new HashMap<String,String>();

	public AbstractCForm()
	{
		setSizeFull();
	}

	public AbstractCForm( Map<String,AbstractCContainer> containersMap, Action action )
	{
		setContainersMap( containersMap );
		this.action = action;
	}

	public Action getAction()
	{
		return action;
	}

	public Map<String,AbstractCContainer> getContainersMap()
	{
		return containersMap;
	}

	@Override
	public void notify( NodeEvent event )
	{
		for( AbstractCContainer container : containersMap.values() )
			container.notify( event );
	}

	public void setContainersMap( Map<String,AbstractCContainer> containersMap )
	{
		this.containersMap = containersMap;
	}

	public void setFormLabel( Map<String,String> formLabel )
	{
		this.formLabel = formLabel;
	}
}
