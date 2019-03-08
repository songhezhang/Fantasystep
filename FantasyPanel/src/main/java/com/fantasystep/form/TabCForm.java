package com.fantasystep.form;


import java.util.ArrayList;
import java.util.Map.Entry;

import com.fantasystep.component.utils.IconUtil;
import com.fantasystep.container.AbstractCContainer;
import com.fantasystep.container.single.AbstractNodeCContainer;
import com.fantasystep.domain.Node;
import com.fantasystep.panel.TreeHandler;
import com.vaadin.server.Resource;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

public class TabCForm extends AbstractCForm implements SelectedTabChangeListener
{
	private static final long serialVersionUID = -9110778706848557676L;
	
	private TabSheet	tabSheet	= new TabSheet();

	public TabCForm()
	{
		super();
		tabSheet.setImmediate( true );
		tabSheet.setSizeFull();
		addComponent( tabSheet );
	}

	@Override
	public void selectedTabChange( SelectedTabChangeEvent event )
	{
		TabSheet tabsheet = event.getTabSheet();
		Component tab = tabsheet.getSelectedTab();
		currentPage = new ArrayList<AbstractCContainer>( getContainersMap().values() ).indexOf( tab );
	}

	@SuppressWarnings("unchecked")
	public void setCurrentPage()
	{
		tabSheet.removeAllComponents();
		Node targetNode = TreeHandler.getTargetNodeByApplication();
		tabSheet.setEnabled( !targetNode.isDeleted() );

		for( Entry<String,AbstractCContainer> tabMap : getContainersMap().entrySet() )
		{
			// Note: it should be fixed by getting tab classes, we have no access here for tab classes
			if( tabMap.getValue() instanceof AbstractNodeCContainer )
			{
				AbstractNodeCContainer container = (AbstractNodeCContainer)tabMap.getValue();
				tabSheet.addTab( tabMap.getValue(), formLabel.get( tabMap.getKey() ), IconUtil.getMediumSizeIcon(container.getNode().getClass()) );
			} else
			{
				Resource res = null;
				try {
					res = IconUtil.getMediumSizeIcon((Class<? extends Node>)Class.forName("com.fantasystep.domain." + tabMap.getKey()) );
				} catch (ClassNotFoundException e) {
					res = IconUtil.getMediumSizeIcon(tabMap.getValue().getNodeClass());
				}
				tabSheet.addTab( tabMap.getValue(), formLabel.get( tabMap.getKey() ), res );
			}
		}
		// tabSheet.setSelectedTab( getContainersMap().values().toArray( new AbstractCContainer[] {} )[currentPage] );
	}
}
