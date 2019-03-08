package com.fantasystep.container.multiple;


import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import com.fantasystep.annotation.DomainClass;
import com.fantasystep.component.common.PopUpModel;
import com.fantasystep.component.menu.AbstractMenu;
import com.fantasystep.component.menu.NodeMenu;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.table.NodeMenuTable;
import com.fantasystep.component.table.NodeTable;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.LayoutUtil;
import com.fantasystep.container.AbstractCContainer;
import com.fantasystep.container.single.SingleNodeCContainer;
import com.fantasystep.domain.Node;
import com.fantasystep.form.CFormFactory;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.helper.NodeEvent.Action;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.utils.NodeUtil;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;


@SuppressWarnings("deprecation")
public class NodeTableCContainer extends AbstractTableCContainer
{
	private static final long serialVersionUID = -6894093903203199112L;
	
	private UUID		keyNodeId;
	private FormMode	mode;

	public NodeTableCContainer( List<Node> nodeList, Class<? extends Node> nodeClass, NodeEvent.Action action, FormMode mode, UUID uuid )
	{
		super( nodeList, action, nodeClass, mode );
		this.mode = mode;
		this.keyNodeId = uuid;
		initForm();
		initDisplay();
		initTemplateButton();
	}

	@Override
	protected AbstractCContainer getContainer( Class<? extends Node> clazz )
	{
		if( mode == FormMode.TAB )
			return new SingleNodeCContainer( NodeUtil.getNewNode( clazz, TreeHandler.getTargetNodeByApplication().getId() ), Action.INSERT, FormMode.TAB );
		else if( mode == FormMode.WIZARD )
			return new SingleNodeCContainer( NodeUtil.getNewNode( clazz, NodeTableCContainer.this.keyNodeId ), Action.INSERT, FormMode.WIZARD )
			{
				private static final long serialVersionUID = 1132206608834033845L;

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
						setReadOnly( false );
						getForm().commit();

						NodeTableCContainer.this.nodeList.add( node );
						NodeTableCContainer.this.getBodyContainer().removeAllComponents();
						( (NodeMenuTable) getNodeTable() ).setVisible( true );
						( (NodeMenuTable) getNodeTable() ).addNewItem( node );
						getPreviousPrimaryNodesByNodeList( NodeTableCContainer.this.nodeList );
						NodeTableCContainer.this.addBodyComponent( ( (NodeMenuTable) getNodeTable() ).getContent(), Alignment.TOP_LEFT );
						NodeTableCContainer.this.markAsDirty();
						closeWindow();
					} else if( source == edit )
						setReadOnly( false );
					else if( source == cancel )
						closeWindow();
				};
			};
		else return null;
	}

	@Override
	protected Table getNodeTable()
	{
		if( this.nodeTable == null )
		{
			this.nodeTable = new NodeMenuTable( TreeHandler.getRootNode(), nodeList, this.nodeClass );
			AbstractMenu menu = new NodeMenu( (NodeMenuTable) this.nodeTable )
			{
				private static final long serialVersionUID = 2544989284299630767L;

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
								SingleNodeCContainer container = new SingleNodeCContainer( menuHelper.getSelectedNodes().get( 0 ), Action.UPDATE, FormMode.WIZARD )
								{
									private static final long serialVersionUID = -953804891002538117L;

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
											( (NodeMenuTable) menuHelper ).removeItem( menuHelper.getSelectedNodes().get( 0 ) );
											( (NodeTable) NodeTableCContainer.this.nodeTable ).addNewItem( node );
											getPreviousPrimaryNodesByNodeList( NodeTableCContainer.this.nodeList );
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

							case DELETE:
								for( Node node : new ArrayList<Node>( menuHelper.getSelectedNodes() ) )
								{
									NodeTableCContainer.this.nodeList.remove( node );
									( (NodeMenuTable) menuHelper ).removeItem( node );
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
					if( mode == FormMode.WIZARD && action == Action.INSERT )
						super.showMenuForWizardInsert( posX, posY );
					else if( mode == FormMode.WIZARD && action == Action.UPDATE )
						super.showMenuForWizardUpdate( posX, posY );
					else super.showMenu( posX, posY, false );
				}
			};
			( (NodeMenuTable) this.nodeTable ).setMenu( menu );
		}
		return this.nodeTable;
	}

	private void initTemplateButton()
	{
		if( mode == FormMode.WIZARD && CFormFactory.hasNodeInputTemplate( nodeClass ) )
		{
			HorizontalLayout layout = new HorizontalLayout();
			layout.setMargin( true );
			layout.setSpacing( true );

			for( final Entry<String,List<Node>> entry : CFormFactory.getNodeInputTemplateMap( nodeClass ).entrySet() )
			{
				Button button = new Button( LocalizationHandler.get( entry.getKey() ) );
				button.addClickListener( new ClickListener()
				{
					private static final long serialVersionUID = 5788064227980822329L;

					@Override
					public void buttonClick( ClickEvent event )
					{
						getNodeTable().removeAllItems();

						for( Node node : entry.getValue() )
							getNodeTable().addItem( node );
						if( !getNodeTable().isVisible() )
						{
							getBodyContainer().removeComponent( noneDataLabel );
							getNodeTable().setVisible( true );
							addBodyComponent( getNodeTable(), Alignment.TOP_LEFT );
						}
					}
				} );
				layout.addComponent( button );
			}
			this.addBodyComponent( layout, Alignment.TOP_RIGHT );
		}
	};

	@Override
	protected void showIfVisible()
	{
		if( !getNodeTable().isVisible() )
			super.showIfVisible();
		else addBodyComponent( ( (NodeMenuTable) getNodeTable() ).getContent(), Alignment.TOP_LEFT );
	}

	@Override
	public boolean validContainerForm()
	{
		// if( getNodeList().isEmpty() )
		// return false;
		// else
		return true;
	}
}
