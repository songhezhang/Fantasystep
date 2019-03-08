package com.fantasystep.container.multiple;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fantasystep.annotation.DomainClass;
import com.fantasystep.component.common.HierarchicalNodes;
import com.fantasystep.component.common.PopUpModel;
import com.fantasystep.component.menu.AbstractMenu;
import com.fantasystep.component.menu.NodeMenu;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.table.NodeMenuTreeTable;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.LayoutUtil;
import com.fantasystep.container.single.SingleNodeCContainer;
import com.fantasystep.domain.Node;
import com.fantasystep.form.CFormFactory;
import com.fantasystep.form.NodeWizardCForm;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.helper.NodeEvent.Action;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.utils.NodeUtil;
import com.vaadin.data.Item;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

@SuppressWarnings("deprecation")
public class NodeTableTreeCContainer extends AbstractTableCContainer
{
	private static final long serialVersionUID = 4506054546645575314L;
	
	private Node	targetNode;

	public NodeTableTreeCContainer( List<Node> nodeList, Class<? extends Node> nodeClass, NodeEvent.Action action, FormMode mode, Node targetNode )
	{
		super( nodeList, action, nodeClass, mode );
		this.targetNode = targetNode;
		initForm();
		initDisplay();
	}

	@Override
	public void buttonClick( ClickEvent event )
	{
		if( buttonsMap.containsKey( event.getButton() ) )
		{
			Node newNode = NodeUtil.getNewNode( buttonsMap.get( event.getButton() ), targetNode.getId() );
			PopUpModel popup = new PopUpModel();
			if( CFormFactory.isWizard( newNode.getClass() ) && mode == FormMode.TAB )
			{
				Map<String,String> labels = new HashMap<String,String>();
				NodeWizardCForm form = new NodeWizardCForm( CFormFactory.getWizardPanelsFromTargetNode( TreeHandler.getRootNode(), newNode, Action.INSERT, labels ), Action.INSERT );
				form.setFormLabel( labels );
				popup = form.getCurrentStep();
			} else
			{
				if( mode == FormMode.TAB )
					this.container = new SingleNodeCContainer( newNode, Action.INSERT, mode );
				else if( mode == FormMode.WIZARD )
					this.container = new SingleNodeCContainer( newNode, Action.INSERT, mode )
					{
						private static final long serialVersionUID = 4889511271285671045L;

						@Override
						public void buttonClick( ClickEvent event )
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
								setReadOnly( false );
								getForm().commit();

								NodeTableTreeCContainer.this.nodeList.add( node );
								Item item = ( (NodeMenuTreeTable) getNodeTable() ).addItem( node );
								HierarchicalNodes container = ( (HierarchicalNodes) ( (NodeMenuTreeTable) getNodeTable() ).getContainerDataSource() );
								container.bindCustomedProperty( item, node );
								container.setParent( node, null );
								container.setChildrenAllowed( node, false );
								NodeTableTreeCContainer.this.getBodyContainer().removeAllComponents();
								NodeTableTreeCContainer.this.getNodeTable().setVisible( true );
								NodeTableTreeCContainer.this.addBodyComponent( ( (NodeMenuTreeTable) getNodeTable() ).getContent(), Alignment.TOP_LEFT );
								NodeTableTreeCContainer.this.markAsDirty();
								closeWindow();
							} else if( source == edit )
								setReadOnly( false );
							else if( source == cancel )
								closeWindow();
						};
					};
				popup = new PopUpModel();
				popup.setCaption( LabelUtil.getDomainLabel( nodeClass ) );
				popup.add( LayoutUtil.addScrollablePanel( container, true ) );
			}

			UI.getCurrent().addWindow( popup );
			UI.getCurrent().setImmediate( true );
		}
	}

	@Override
	protected Table getNodeTable()
	{
		if( this.nodeTable == null )
		{
			if( mode == FormMode.WIZARD && action == Action.INSERT )
				this.nodeTable = new NodeMenuTreeTable( nodeClass, new ArrayList<Node>() );
			else this.nodeTable = new NodeMenuTreeTable( nodeClass, targetNode.getChildren( nodeClass ) );
			AbstractMenu menu = new NodeMenu( (NodeMenuTreeTable) this.nodeTable )
			{
				private static final long serialVersionUID = 1846626990631366448L;

				@Override
				protected void handleMenuAction( MenuEvent event )
				{
					if( mode == FormMode.TAB )
						super.handleMenuAction( event );
					else if( mode == FormMode.WIZARD )
					{
						final HierarchicalNodes nodesContainer = ( (HierarchicalNodes) ( (NodeMenuTreeTable) menuHelper ).getContainerDataSource() );
						final NodeMenuTreeTable nodeMenuTreeTable = ( (NodeMenuTreeTable) menuHelper );
						switch( event.getMenuAction() )
						{
							case INSERT:
								Node newNode = NodeUtil.getNewNode( event.getNodeType(), menuHelper.getSelectedNodes().get( 0 ).getId() );
								SingleNodeCContainer insertContainer = new SingleNodeCContainer( newNode, Action.INSERT, FormMode.WIZARD )
								{
									private static final long serialVersionUID = 8856208666088323353L;

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

											Node parent = menuHelper.getSelectedNodes().get( 0 );
											Item childItem = nodeMenuTreeTable.addItem( node );
											nodesContainer.bindCustomedProperty( childItem, node );
											nodesContainer.setChildrenAllowed( parent, true );
											nodesContainer.setParent( node, parent );
											nodesContainer.setChildrenAllowed( node, false );
											NodeTableTreeCContainer.this.nodeList.add( node );
											closeWindow();;
										} else if( source == edit )
											setReadOnly( false );
										else if( source == cancel )
											closeWindow();
									};
								};
								PopUpModel insertPopup = new PopUpModel();
								Panel insertPanel = LayoutUtil.addScrollablePanel( insertContainer, true );
								insertPopup.add( insertPanel );
								insertPopup.setCaption( LocalizationHandler.get( menuHelper.getSelectedNodes().get( 0 ).getClass().getAnnotation( DomainClass.class ).label() ) );
								UI.getCurrent().addWindow( insertPopup );
								break;

							case UPDATE:
								SingleNodeCContainer updateContainer = new SingleNodeCContainer( menuHelper.getSelectedNodes().get( 0 ), Action.UPDATE, FormMode.WIZARD )
								{
									private static final long serialVersionUID = -9216179720041603677L;

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

											nodesContainer.bindCustomedProperty( getNodeTable().getItem( node ), node );
											closeWindow();
										} else if( source == edit )
											setReadOnly( false );
										else if( source == cancel )
											closeWindow();
									};
								};
								PopUpModel updatePopup = new PopUpModel();
								Panel updatePanel = LayoutUtil.addScrollablePanel( updateContainer, true );
								updatePopup.add( updatePanel );
								updatePopup.setCaption( LocalizationHandler.get( menuHelper.getSelectedNodes().get( 0 ).getClass().getAnnotation( DomainClass.class ).label() ) );
								UI.getCurrent().addWindow( updatePopup );
								break;

							case DELETE:
								List<Node> list = new ArrayList<Node>();
								list.addAll( menuHelper.getSelectedNodes() );
								for( Node node : list )
								{
									Object parent = nodeMenuTreeTable.getParent( node );
									NodeTableTreeCContainer.this.nodeList.remove( node );
									NodeTableTreeCContainer.this.nodeList.removeAll( node.getChildren() );
									if( nodeMenuTreeTable.getChildren( node ) != null )
										for( Object obj : new ArrayList<Object>( nodeMenuTreeTable.getChildren( node ) ) )
											nodeMenuTreeTable.removeItem( obj );
									nodeMenuTreeTable.removeItem( node );
									if( parent != null && ( nodeMenuTreeTable.getChildren( parent ) == null || nodeMenuTreeTable.getChildren( parent ).size() == 0 ) )
										nodeMenuTreeTable.setChildrenAllowed( parent, false );
								}

								markAsDirty();
								break;
						default:
							break;
						}
					}
				};

				@Override
				public void showMenu( int posX, int posY )
				{
					// Two level remove permission restriction since the parent node could not exist
					if( mode == FormMode.WIZARD )
						super.showMenuForWizardInsert( posX, posY );
					else super.showMenu( posX, posY, false );
				}
			};
			( (NodeMenuTreeTable) this.nodeTable ).setMenu( menu );
		}
		return this.nodeTable;
	}

	@Override
	protected void showIfVisible()
	{
		if( !getNodeTable().isVisible() )
			super.showIfVisible();
		else addBodyComponent( ( (NodeMenuTreeTable) getNodeTable() ).getContent(), Alignment.TOP_LEFT );
	}
}
