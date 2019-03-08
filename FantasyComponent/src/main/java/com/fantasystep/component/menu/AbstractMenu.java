package com.fantasystep.component.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.peter.contextmenu.ContextMenu;

import com.fantasystep.annotation.AnnotationsParser;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.IconUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.domain.Node;
import com.fantasystep.utils.NodeUtil;
import com.fantasystep.utils.PermissionUtil.PermissionDescriptor;
import com.vaadin.server.ThemeResource;

public abstract class AbstractMenu extends ContextMenu implements ContextMenu.ContextMenuItemClickListener
{
	private static final long serialVersionUID = 3463158977017456001L;

	public enum MenuAction
	{
		ACTIVATE( "LABEL_ACTIVATE" ), DEACTIVATE( "LABEL_DE_ACTIVATE" ), DELETE( "LABEL_DELETE" ), DESTROY( "LABEL_DESTROY" ), HIDE( "LABEL_HIDE_DELETED_ITEMS" ), IMPORT( "LABEL_IMPORT" ), INSERT(
				"LABEL_CREATE_NEW" ), SHOW( "LABEL_SHOW_DELETED_ITEMS" ), UNDELETE( "LABEL_UNDELETE" ), UPDATE( "LABEL_UPDATE" );

		private String	label;

		MenuAction( String label )
		{
			setLabel( LocalizationHandler.get( label ) );
		}

		public String getLabel()
		{
			return label;
		}

		public void setLabel( String label )
		{
			this.label = label;
		}
	}

	public static class MenuEvent
	{
		private final MenuAction			menuAction;
		private final Class<? extends Node>	nodeType;

		public MenuEvent( Class<? extends Node> nodeType, MenuAction menuAction )
		{
			this.nodeType = nodeType;
			this.menuAction = menuAction;
		}

		public MenuEvent( MenuAction menuAction )
		{
			this( null, menuAction );
		}

		public MenuAction getMenuAction()
		{
			return menuAction;
		}

		public Class<? extends Node> getNodeType()
		{
			return nodeType;
		}
	}

	protected final Map<ContextMenuItem,MenuEvent>	itemsMap	= new HashMap<ContextMenuItem,MenuEvent>();
	protected MenuHelper							menuHelper;

	public AbstractMenu( MenuHelper menuHelper )
	{
		this.menuHelper = menuHelper;
		this.addItemClickListener( this );
	}

	protected ContextMenuItem addMenuItem( MenuAction menuAction, Class<? extends Node> clazz, ContextMenuItem parentItem )
	{
		ContextMenuItem item = null;
		String caption = "";
		if( null == parentItem && ( menuAction.equals( MenuAction.INSERT ) ) )
			caption = menuAction.getLabel();
		else if( menuAction.equals( MenuAction.INSERT ) )
			caption = LabelUtil.getDomainLabel( clazz );
		else if( !menuAction.equals( MenuAction.INSERT ) )
		{
			if( menuHelper.getSelectedNodes().size() == 1 )
			{
				String label = menuHelper.getSelectedNodes().iterator().next().getLabel();
				caption = String.format( "%s %s", LocalizationHandler.get( menuAction.getLabel() ), LocalizationHandler.get( label ) );
			} else caption = String.format( "%s %s %s", LocalizationHandler.get( menuAction.getLabel() ), menuHelper.getSelectedNodes().size(), LocalizationHandler.get( LabelUtil.LABEL_ITEMS ) );
		}

		// if there is no any parent menu item
		if( parentItem == null )
		{
			item = addItem( caption );
			// implement logic in child classes if needed
			onItemRendering( item, menuAction );

		} else item = parentItem.addItem( caption );

		if( item != null && ( menuAction.equals( MenuAction.INSERT ) || menuAction.equals( MenuAction.UPDATE ) ) )
			itemsMap.put( item, new MenuEvent( clazz, menuAction ) );
		else itemsMap.put( item, new MenuEvent( null, menuAction ) );

		String icon = getMenuIcon( menuAction, AnnotationsParser.getAttributes(clazz).getIcon() == null ? clazz.getSimpleName().toLowerCase() + ".png" : AnnotationsParser.getAttributes(clazz).getIcon() ); // getting icon
		item.setIcon( new ThemeResource( String.format( "%s%s", IconUtil.SMALL_ICONS_PATH, icon ) ) );

		return item;
	}

	@Override
	public void contextMenuItemClicked(ContextMenuItemClickEvent event)
	{
		ContextMenuItem item = (ContextMenuItem)event.getSource();
		MenuEvent e = itemsMap.get( item );

//		if(MenuAction.INSERT.getLabel().equals(item.getData()) || MenuAction.IMPORT.getLabel().equals(item.getData()) || e == null )
		if(item.hasSubMenu())
			return;

		handleMenuAction( e );
	}

	protected List<Class<? extends Node>> getChildClazzez()
	{
		List<Class<? extends Node>> clzList = new ArrayList<Class<? extends Node>>();

		Node currentNode = menuHelper.getSelectedNodes().get( 0 );
		if( currentNode != null )
			for( Class<? extends Node> c : NodeUtil.getValidChildren( currentNode.getClass() ) )
				if( !AnnotationsParser.getAttributes( c ).getIsPropertyNode() )
					clzList.add( c );
		clzList.addAll(getValidDynamicChidren(currentNode.getClass()));
		Collections.sort( clzList, new Comparator<Class<?>>()
		{
			@Override
			public int compare( Class<?> o1, Class<?> o2 )
			{
				DomainClass dc1 = o1.getAnnotation( DomainClass.class );
				DomainClass dc2 = o2.getAnnotation( DomainClass.class );
				return dc1.label().compareTo( dc2.label() );
			}
		} );
		return clzList;
	}
	
	protected List<Class<? extends Node>> getValidDynamicChidren(Class<? extends Node> class1) {
		return new ArrayList<Class<? extends Node>>();
	}

	private String getMenuIcon( MenuAction menuAction, String clazzName )
	{
		if( menuAction.equals( MenuAction.INSERT ) )
			return clazzName;
		else if( menuAction.equals( MenuAction.DEACTIVATE ) )
			return IconUtil.ICON_DEACTIVATE;
		else return String.format( "%s.png", menuAction.toString().toLowerCase() );
	}

	abstract protected PermissionDescriptor getPermissionDescriptor();

	abstract protected void handleMenuAction( MenuEvent event );

	protected boolean isAllDeleted()
	{
		for( Node n : menuHelper.getSelectedNodes() )
			if( !n.isDeleted() )
				return false;
		return true;
	}

	protected boolean isAllUnDeleted()
	{
		for( Node n : menuHelper.getSelectedNodes() )
			if( n.isDeleted() )
				return false;
		return true;
	}

	protected abstract void onItemRendering( ContextMenuItem item, MenuAction menuAction );

	protected void setEnabledItems( boolean enabled )
	{
		for( ContextMenuItem item : itemsMap.keySet() )
			item.setEnabled( enabled );
	}

	// For normal NodeTree including all functionalities
	public void showMenu( int posX, int posY )
	{
		showMenu( posX, posY, true );
	}

	// For special case with or without hide functionality
	public void showMenu( int posX, int posY, boolean hasHideFunc )
	{
		showMenu( posX, posY, hasHideFunc, true );
	}

	// For special case with or without hide functionality and onlyDeleteCheck. For Wizard Update onlyDeleteCheck is
	// true since there is no meaning to
	// destroy nodes on wizard.
	public void showMenu( int posX, int posY, boolean hasHideFunc, boolean onlyDeleteCheck )
	{
		for( ContextMenuItem item : itemsMap.keySet() )
			this.removeItem( item );
		itemsMap.clear();

		PermissionDescriptor permissions = getPermissionDescriptor();
		/** Note: for insert update and import case there would be be only one item in selectedNodesList */
		Node firstNode = menuHelper.getSelectedNodes().iterator().next();
		Class<? extends Node> fcl = firstNode.getClass();

		if( permissions.hasInsertPermission() && !getChildClazzez().isEmpty() )
		{
			ContextMenuItem createMenuItem = addMenuItem( MenuAction.INSERT, fcl, null );

			for( Class<? extends Node> cl : getChildClazzez() )
				addMenuItem( MenuAction.INSERT, cl, createMenuItem );

			if( ( menuHelper.getSelectedNodes().size() > 1 || firstNode.isDeleted() ) )
				createMenuItem.setEnabled( false );
		}
		if( permissions.hasUpdatePermission() )
		{
			ContextMenuItem updateItem = addMenuItem( MenuAction.UPDATE, fcl, null );

			if( ( menuHelper.getSelectedNodes().size() > 1 ) || firstNode.isDeleted() )
				updateItem.setEnabled( false );
		}
		if( onlyDeleteCheck )
		{
			if( permissions.hasDeletePermission() && isAllUnDeleted() )
				addMenuItem( MenuAction.DELETE, fcl, null );
			if( permissions.hasDestroyPermission() && isAllDeleted() )
			{
				addMenuItem( MenuAction.UNDELETE, fcl, null );
				addMenuItem( MenuAction.DESTROY, fcl, null );
			}
		} else
		{
			if( permissions.hasDeletePermission() )
				addMenuItem( MenuAction.DELETE, fcl, null );

		}

		if( hasHideFunc )
		{
			ContextMenuItem hideShowItem;
			if( menuHelper.isHideMenuItem() )
			{
				hideShowItem = addItem( MenuAction.SHOW.getLabel() );
				ThemeResource res = new ThemeResource( String.format( "%s%s", IconUtil.SMALL_ICONS_PATH, IconUtil.ICON_ACTIVATE ) );
				hideShowItem.setIcon( res );
				itemsMap.put( hideShowItem, new MenuEvent( MenuAction.SHOW ) ); // next action would be hide
			} else
			{
				hideShowItem = addItem( MenuAction.HIDE.getLabel() );
				itemsMap.put( hideShowItem, new MenuEvent( MenuAction.HIDE ) );
			}
		}

		this.open( posX, posY );
	}

	// This method skip permission check since when you added in wizard there is no permission at that time.
	public void showMenuForWizardInsert( int posX, int posY )
	{
		for( ContextMenuItem item : itemsMap.keySet() )
			this.removeItem( item );
		itemsMap.clear();

		Node firstNode = menuHelper.getSelectedNodes().iterator().next();
		Class<? extends Node> fcl = firstNode.getClass();

		if( !getChildClazzez().isEmpty() )
		{
			ContextMenuItem createMenuItem = addMenuItem( MenuAction.INSERT, fcl, null );

			for( Class<? extends Node> cl : getChildClazzez() )
				addMenuItem( MenuAction.INSERT, cl, createMenuItem );

			if( ( menuHelper.getSelectedNodes().size() > 1 || firstNode.isDeleted() ) )
				createMenuItem.setEnabled( false );
		}
		ContextMenuItem updateItem = addMenuItem( MenuAction.UPDATE, fcl, null );

		if( ( menuHelper.getSelectedNodes().size() > 1 ) || firstNode.isDeleted() )
			updateItem.setEnabled( false );
		if( isAllUnDeleted() )
			addMenuItem( MenuAction.DELETE, fcl, null );
		if( isAllDeleted() )
		{
			addMenuItem( MenuAction.UNDELETE, fcl, null );
			addMenuItem( MenuAction.DESTROY, fcl, null );
		}

		this.showMenu( posX, posY );
	}

	public void showMenuForWizardUpdate( int posX, int posY )
	{
		showMenu( posX, posY, false, false );
	}
}
