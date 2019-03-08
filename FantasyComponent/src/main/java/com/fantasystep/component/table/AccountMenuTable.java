package com.fantasystep.component.table;


import java.util.ArrayList;
import java.util.List;

import com.fantasystep.component.common.ItemClickHandler;
import com.fantasystep.component.menu.AbstractMenu;
import com.fantasystep.component.menu.MenuHelper;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class AccountMenuTable extends NodeTable implements MenuHelper
{
	private ItemClickHandler	handler;

	public AccountMenuTable( Node root, List<Node> list, Class<? extends Node> nodeType )
	{
		super( root, list, nodeType );
		setColumnReorderingAllowed( false );
		setColumnCollapsingAllowed( false );

		addGeneratedColumn( LocalizationHandler.get( LabelUtil.LABEL_TITLES ), new TitleGenerator() );
		setColumnWidth( LocalizationHandler.get( LabelUtil.LABEL_TITLES ), 350 );

		setVisibleColumns( new Object[] { LocalizationHandler.get( LabelUtil.LABEL_TITLES ) } );
	}

	public VerticalLayout getContent()
	{
		return getHandler().getContent();
	}

	private ItemClickHandler getHandler()
	{
		return handler;
	}

	@Override
	public List<Node> getSelectedNodes()
	{
		return getHandler().getSelectedNodes();
	}

	@Override
	public boolean isHideMenuItem()
	{
		return false;
	}

	@Override
	public void notify( NodeEvent event )
	{
		super.notify( event );
		List<Node> list = new ArrayList<Node>( getSelectedNodes() );
		for( Node n : list )
			unselect( n );
	}

	public void setMenu( AbstractMenu menu )
	{
		this.handler = new ItemClickHandler( this, menu );
	}

	@Override
	public void triggerHideMenuItem()
	{
	}
}
