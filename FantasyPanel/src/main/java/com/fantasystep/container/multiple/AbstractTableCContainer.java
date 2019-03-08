package com.fantasystep.container.multiple;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fantasystep.component.common.PopUpModel;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.component.utils.IconUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.LayoutUtil;
import com.fantasystep.container.AbstractCContainer;
import com.fantasystep.container.multiple.relation.AbstractMembersHolderCContainer;
import com.fantasystep.container.single.SingleNodeCContainer;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.MemberHolder;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.helper.NodeEvent.Action;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.util.CNodeUtil;
import com.fantasystep.utils.NodeClassUtil;
import com.fantasystep.utils.NodeUtil;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

public abstract class AbstractTableCContainer extends AbstractMultiNodeCContainer
{
	private static final long serialVersionUID = 3091635737598156102L;
	
	protected Map<Button,Class<? extends Node>>	buttonsMap		= new HashMap<Button,Class<? extends Node>>();
	protected AbstractCContainer				container;
	protected Table								nodeTable;
	protected Label								noneDataLabel	= LabelUtil.getNoneDataLabel();

	public AbstractTableCContainer( List<Node> nodeList, Action action, Class<? extends Node> nodeClass, FormMode mode )
	{
		super( nodeList, action, nodeClass, mode );
	}

	public AbstractTableCContainer( List<Node> nodeList, Action nodeAction, Class<? extends Node> nodeClass, FormMode mode, String title )
	{
		super( nodeList, nodeAction, nodeClass, mode, title );
	}

	@SuppressWarnings("unchecked")
	protected Button addButton( HorizontalLayout hz, Class<?> clz, String title )
	{
		Button btn = new Button( title );
		IconUtil.wrapIcon( btn, clz, IconUtil.MEDIUM_ICON_SIZE );
		btn.addClickListener( this );
		if( !hasPermission( (Class<? extends Node>) clz ) )
			btn.setEnabled( false );
		buttonsMap.put( btn, (Class<? extends Node>) clz );
		hz.setMargin( true );
		hz.addComponent( new Label( "&nbsp;&nbsp;", ContentMode.HTML ) );
		hz.addComponent( btn );
		return btn;
	}

	protected boolean hasPermission( Class<? extends Node> clz )
	{
		return CNodeUtil.getPermissionDescriptor( (Class<? extends Node>) clz ).hasInsertPermission();
	}

	@Override
	public void buttonClick( ClickEvent event )
	{
		if( buttonsMap.containsKey( event.getButton() ) )
		{
			this.container = getContainer( buttonsMap.get( event.getButton() ) );
			PopUpModel popup = new PopUpModel();

			Panel panel = LayoutUtil.addScrollablePanel( container, true );

			if( MemberHolder.class.isAssignableFrom( nodeClass ) || container instanceof AbstractMembersHolderCContainer )
			{
				panel.getContent().setWidth( LayoutUtil.CONST_MEMBERS_SIZE + "px" );
				popup.setWidth( LayoutUtil.PERCENTAGE_LARGE_SIZE + "%" );

			} else popup.setWidth( LayoutUtil.PERCENTAGE_MEDIUM_SIZE + "%" );

			popup.setCaption( LabelUtil.getDomainLabel( nodeClass ) );
			popup.add( panel );
			UI.getCurrent().setImmediate( true );
			UI.getCurrent().addWindow( popup );
		}
	}

	protected AbstractCContainer getContainer( Class<? extends Node> clazz )
	{
		Node n = NodeUtil.getNewNode( clazz, TreeHandler.getTargetNodeByApplication().getId() );
		return new SingleNodeCContainer( n, Action.INSERT, mode );
	}

	@SuppressWarnings("unchecked")
	protected HorizontalLayout getLinksLayout()
	{
		HorizontalLayout hz = new HorizontalLayout();
		Set<?>  childClass = NodeClassUtil.getSubClassesInJVM( nodeClass, true );
		if( childClass.isEmpty() )
			addButton( hz, nodeClass, String.format( "%s %s", LocalizationHandler.get( LabelUtil.LABEL_NEW ), title ) );
		else for( Object clz : childClass )
			addButton( hz, (Class<?>)clz, String.format( "%s %s", LocalizationHandler.get( LabelUtil.LABEL_NEW ), LabelUtil.getDomainLabel( (Class<? extends Node>) clz ) ) );

		return hz;
	}

	abstract protected Table getNodeTable();

	@Override
	protected void initDisplay()
	{
		HorizontalLayout links = getLinksLayout();
		links.setStyleName( CSSUtil.BUTTONS_BLOCK );
		links.setHeight( "80px" );
		addHeader( links, Alignment.TOP_LEFT );
		showIfVisible();
	};

	@Override
	protected void initForm()
	{
	}

	@Override
	public void notify( NodeEvent event )
	{
		this.getBodyContainer().removeAllComponents();
		( (com.fantasystep.component.panel.Listener) getNodeTable() ).notify( event );
		showIfVisible();
	}

	protected void showIfVisible()
	{
		if( !getNodeTable().isVisible() )
			addBodyComponent( noneDataLabel, Alignment.TOP_LEFT );
		else addBodyComponent( getNodeTable(), Alignment.TOP_LEFT );
	}
}
