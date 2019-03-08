package com.fantasystep.container.multiple.account;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fantasystep.annotation.AnnotationsParser;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.common.PopUpModel;
import com.fantasystep.component.field.custom.LabelCField;
import com.fantasystep.component.menu.AbstractMenu;
import com.fantasystep.component.menu.NodeMenu;
import com.fantasystep.component.panel.ConcreteNodeEvent;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.table.AccountMenuTable;
import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.LayoutUtil;
import com.fantasystep.container.AbstractCContainer;
import com.fantasystep.container.multiple.AbstractTableCContainer;
import com.fantasystep.container.single.AbstractNodeCContainer;
import com.fantasystep.container.single.SingleNodeCContainer;
import com.fantasystep.domain.AbstractAccount;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.helper.NodeEvent.Action;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.util.CNodeUtil;
import com.fantasystep.utils.NodeClassUtil;
import com.fantasystep.utils.NodeUtil;
import com.fantasystep.utils.PermissionUtil.PermissionDescriptor;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class AccountTableCContainer extends AbstractTableCContainer
{
	protected enum NodeActionType
	{
		ACTIVATE( "LABEL_ACTIVATE" ), DE_ACTIVATE( "LABEL_DE_ACTIVATE" );
		private String	label;

		private NodeActionType( String label )
		{
			setLabel( label );
		}

		public String getLabel()
		{
			return label;
		}

		private void setLabel( String label )
		{
			this.label = label;
		}
	}

	protected Map<Class<? extends AbstractAccount>,AbstractNodeCContainer>	accountMap	= new HashMap<Class<? extends AbstractAccount>,AbstractNodeCContainer>();
	private Class<? extends Node>											accountType;
	private Node															targetNode;

	protected Map<Class<? extends AbstractAccount>,String>					validAccountsMap;

	public AccountTableCContainer( List<Node> nodeList, Action nodeAction, Class<? extends Node> nodeClass, FormMode mode, Node targetNode )
	{
		super( nodeList, nodeAction, nodeClass, mode );
		this.accountType = targetNode.getClass();
		this.targetNode = targetNode;
		initAccountList();
		initForm();
		initDisplay();
	}

	private AbstractAccount getAccount( Class<? extends AbstractAccount> accountClazz )
	{
		for( Node node : nodeList )
		{
			AbstractAccount account = (AbstractAccount) node;
			if( account.getClass().equals( accountClazz ) )
				return account;
		}

		return null;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	protected AbstractCContainer getContainer( Class<? extends Node> clazz )
	{
		final Class<? extends AbstractAccount> account = (Class<? extends AbstractAccount>) clazz;
		Node node = null;
		if( getAccount( account ) != null )
			node = getAccount( account );
		else
		{
			if( mode == FormMode.WIZARD && action == Action.INSERT )
			{
				node = NodeUtil.getNewNode( account, this.targetNode.getId() );
				// ( (ParentListener) node ).update( targetNode );
			} else
			{
				Node targetNode = TreeHandler.getTargetNodeByApplication();
				node = NodeUtil.getNewNode( account, targetNode.getId() );
				// ( (ParentListener) node ).update( targetNode );
			}
		}
		if( mode == FormMode.TAB )
			return new SingleNodeCContainer( node, getAccount( account ) != null ? Action.UPDATE : Action.INSERT, mode )
			{
				private BeanItem<Node>	bean	= null;

				@Override
				protected Field createFieldWithPropertyId( Node node, Object propertyId )
				{
					Field field = null;
					FieldAttributeAccessor attributes = AnnotationsParser.getAttributes( node.getClass(), propertyId.toString());
					if( attributes.getAlias() != null && !attributes.getAlias().equals( "" ) )
						field = new LabelCField( attributes ).getField();
					else
						field = super.createFieldWithPropertyId( node, propertyId );
					field.setValue( getBean().getItemProperty( propertyId ).getValue() );

					return field;
				}

				private BeanItem<Node> getBean()
				{
					if( bean == null )
						bean = new BeanItem<Node>( getNode() );
					return bean;
				}
			};
		else
			return new SingleNodeCContainer( node, getAccount( account ) != null ? Action.UPDATE : Action.INSERT, mode )
			{
				private BeanItem<Node>	bean	= null;

				@SuppressWarnings("deprecation")
				@Override
				public void buttonClick( ClickEvent event )
				{
					Button source = event.getButton();

					if( source == save )
					{
						if( !getForm().isValid() )
						{
							Notification.show( LocalizationHandler.get( LabelUtil.LABEL_VALIDATE_ERROR ), Type.WARNING_MESSAGE );
							return;
						}
						getForm().commit();
						AccountTableCContainer.this.nodeList.add( getNode() );
						closeWindow();
						AccountTableCContainer.this.getHeader().removeAllComponents();
						AccountTableCContainer.this.getBodyContainer().removeAllComponents();
						AccountTableCContainer.this.addHeaderTitle( AccountTableCContainer.this.title, CSSUtil.BLACK_FEATURED_TITLE );
						( (AccountMenuTable) getNodeTable() ).setVisible( true );
						( (AccountMenuTable) getNodeTable() ).addNewItem( node );
						AccountTableCContainer.this.initDisplay();
					} else if( source == edit )
						setReadOnly( false );
					else if( source == cancel )
						closeWindow();
				}

				@Override
				protected Field createFieldWithPropertyId( Node node, Object propertyId )
				{
					Field field = null;
					FieldAttributeAccessor attributes = AnnotationsParser.getAttributes( node.getClass(), propertyId.toString());
					if( attributes.getAlias() != null && !attributes.getAlias().equals( "" ) )
						field = new LabelCField( attributes ).getField();
					else
						field = super.createFieldWithPropertyId( node, propertyId );
					field.setValue( getBean().getItemProperty( propertyId ).getValue() );

					return field;
				};

				private BeanItem<Node> getBean()
				{
					if( bean == null )
						bean = new BeanItem<Node>( getNode() );
					return bean;
				}
			};
	};

	@SuppressWarnings("unchecked")
	@Override
	protected HorizontalLayout getLinksLayout()
	{
		HorizontalLayout hz = new HorizontalLayout();
		for( Class<?> clz : NodeClassUtil.getSubClassesInJVM(this.accountType))
		{
			if( getAccount( (Class<? extends AbstractAccount>) clz ) != null )
				continue;
			Button b = addButton( hz, clz, "" );
			b.setDescription( String.format( "%s %s", LocalizationHandler.get( NodeActionType.ACTIVATE.getLabel() ), LabelUtil.getDomainLabel( (Class<? extends Node>) clz ) ) );
			if( targetNode.getParentId() != null )
			{
				Node parent = NodeUtil.getNode( targetNode.getParentId(), TreeHandler.getRootNode() );
				if( parent != null )
					/*if( parent.getChildren( AbstractWindowsAccount.class ).isEmpty() && AbstractWindowsAccount.class.isAssignableFrom( clz ) )*/
						b.setEnabled( false );
			}
		}
		return hz;
	}

	@Override
	protected Table getNodeTable()
	{
		if( this.nodeTable == null )
		{
			this.nodeTable = new AccountMenuTable( TreeHandler.getRootNode(), nodeList, AbstractAccount.class );
			AbstractMenu menu = new NodeMenu( (AccountMenuTable) this.nodeTable )
			{
				@Override
				protected void handleMenuAction( MenuEvent event )
				{
					if( mode == FormMode.TAB )
						super.handleMenuAction( event );
					else if( mode == FormMode.WIZARD )
					{
						switch( event.getMenuAction() )
						{
							case UPDATE:
								AbstractCContainer container = new SingleNodeCContainer( menuHelper.getSelectedNodes().get( 0 ), Action.UPDATE, FormMode.WIZARD )
								{
									@SuppressWarnings("deprecation")
									@Override
									public void buttonClick( com.vaadin.ui.Button.ClickEvent event )
									{
										Button source = event.getButton();
										if( source == save )
										{
											if( !getForm().isValid() )
											{
												Notification.show( LocalizationHandler.get( LabelUtil.LABEL_VALIDATE_ERROR ),
														Type.WARNING_MESSAGE );
												return;
											}
											getForm().commit();

											( (AccountMenuTable) menuHelper ).removeItem( menuHelper.getSelectedNodes().get( 0 ) );
											( (AccountMenuTable) AccountTableCContainer.this.nodeTable ).addNewItem( node );
											// showIfVisible();
											closeWindow();
										} else if( source == edit )
											setReadOnly( false );
										else if( source == cancel )
											closeWindow();
									};
								};
								PopUpModel popup = new PopUpModel();
								Panel panel = LayoutUtil.addScrollablePanel( container, true );
								popup.add( panel );
								popup.setCaption( LocalizationHandler.get( menuHelper.getSelectedNodes().get( 0 ).getClass().getAnnotation( DomainClass.class ).label() ) );
								UI.getCurrent().addWindow( popup );
								break;

							case DEACTIVATE:
								Node node = NodeUtil.getNode( menuHelper.getSelectedNodes().get( 0 ).getParentId(), TreeHandler.getTargetNode() );
								if( CNodeUtil.isRelatedToWindowsGroupOrResource( node ) && action != Action.INSERT
										/*&& AbstractWindowsAccount.class.isAssignableFrom( menuHelper.getSelectedNodes().get( 0 ).getClass() )*/ )
									Notification.show( LocalizationHandler.get( LabelUtil.LABEL_CAN_NOT_DELETE_WINDOWS_ACCOUNT_OF_WINDOWS_MEMBER_ERROR ) );
								else
								{
									for( Node n : new ArrayList<Node>( menuHelper.getSelectedNodes() ) )
									{
										AccountTableCContainer.this.nodeList.remove( n );
										( (AccountMenuTable) menuHelper ).removeItem( n );
									}
									AccountTableCContainer.this.getHeader().removeAllComponents();
									AccountTableCContainer.this.addHeaderTitle( AccountTableCContainer.this.title, CSSUtil.BLACK_FEATURED_TITLE );
									AccountTableCContainer.this.initDisplay();
								}
								// requestRepaint();
								break;
						default:
							break;
						}
					}
				};

				@Override
				public void showMenu( int posX, int posY )
				{
					for( ContextMenuItem item : itemsMap.keySet() )
						this.removeItem( item );
					itemsMap.clear();

					PermissionDescriptor permissions = getPermissionDescriptor();
					Node firstNode = menuHelper.getSelectedNodes().iterator().next();
					boolean tmp = false;
					if( mode == FormMode.WIZARD && action == Action.INSERT )
						tmp = true;
					if( permissions.hasUpdatePermission() || tmp )
					{
						ContextMenuItem updateItem = addMenuItem( MenuAction.UPDATE, firstNode.getClass(), null );
						if( ( menuHelper.getSelectedNodes().size() > 1 ) || firstNode.isDeleted() )
							updateItem.setEnabled( false );
					}
					if( permissions.hasDestroyPermission() || tmp )
						addMenuItem( MenuAction.DEACTIVATE, firstNode.getClass(), null );

					setVisible( true );
					super.showMenu(posX, posY);
				}
			};
			( (AccountMenuTable) this.nodeTable ).setMenu( menu );
		}
		return this.nodeTable;
	}

	private void initAccountList()
	{
		this.nodeList.clear();
		if( !( mode == FormMode.WIZARD && action == Action.INSERT ) )
			nodeList.addAll( targetNode.getChildren( AbstractAccount.class ) );
	}

	@Override
	public void notify( NodeEvent event )
	{
		if( !( event.getNode() instanceof AbstractAccount ) )
			return;

		this.targetNode = ( (ConcreteNodeEvent) event ).getTargetNode();
		getHeader().removeAllComponents();
		getBodyContainer().removeAllComponents();
		addHeaderTitle( this.title, CSSUtil.BLACK_FEATURED_TITLE );
		initAccountList();
		initForm();
		( (AccountMenuTable) getNodeTable() ).setNodes( this.nodeList );
		( (AccountMenuTable) getNodeTable() ).notify( event );
		initDisplay();
	}

	@SuppressWarnings("unused")
	private void removeAccount( Class<? extends AbstractAccount> clazz )
	{
		int index = -1;
		for( Node node : nodeList )
		{
			AbstractAccount account = (AbstractAccount) node;
			if( account.getClass().equals( clazz ) )
				index = nodeList.indexOf( account );
		}
		if( index != -1 )
			nodeList.remove( index );
	}

	@Override
	protected void showIfVisible()
	{
		if( !getNodeTable().isVisible() )
			super.showIfVisible();
		else
			addBodyComponent( ( (AccountMenuTable) getNodeTable() ).getContent(), Alignment.TOP_LEFT );
	};
}
