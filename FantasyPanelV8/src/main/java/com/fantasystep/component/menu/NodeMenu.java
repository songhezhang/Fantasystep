package com.fantasystep.component.menu;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fantasystep.annotation.DomainClass;
import com.fantasystep.component.common.PopUpModel;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.LayoutUtil;
import com.fantasystep.container.AbstractCContainer.FormMode;
import com.fantasystep.container.single.SingleNodeCContainer;
import com.fantasystep.domain.DynamicDomain;
import com.fantasystep.domain.Entity;
import com.fantasystep.domain.Node;
import com.fantasystep.form.CFormFactory;
import com.fantasystep.form.NodeWizardCForm;
import com.fantasystep.helper.MemberHolder;
import com.fantasystep.helper.NodeEvent.Action;
import com.fantasystep.panel.CApplication;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.util.CNodeUtil;
import com.fantasystep.utils.JCompiler;
import com.fantasystep.utils.NodeClassUtil;
import com.fantasystep.utils.NodeUtil;
import com.fantasystep.utils.PermissionUtil.PermissionDescriptor;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class NodeMenu extends AbstractMenu
{
	public NodeMenu(AbstractComponent parentComponent, MenuHelper component )
	{
		super(parentComponent, component);
	}

	@Override
	protected void handleMenuAction( AbstractMenu.MenuEvent event )
	{
		// To avoid Concurrent modification exception.
		List<Node> list = new ArrayList<Node>( menuHelper.getSelectedNodes() );
		try
		{
			switch( event.getMenuAction() )
			{
				case INSERT:
					Node newNode = NodeUtil.getNewNode( event.getNodeType(), list.get( 0 ).getId() );

					if( !CFormFactory.isWizard( newNode.getClass() ) )
					{
						final SingleNodeCContainer container = new SingleNodeCContainer( newNode, Action.INSERT, FormMode.WIZARD );
						( (CApplication) CApplication.getCurrent() ).getEventHandler().addListener( container );

						final PopUpModel popup = new PopUpModel()
						{
							@Override
							public void close()
							{
								( (CApplication) CApplication.getCurrent() ).getEventHandler().removeListener( container );
								super.close();
							}
						};

						Panel panel = LayoutUtil.addScrollablePanel( container, true );
						popup.add( panel );
						popup.setCaption( LabelUtil.getDomainLabel( newNode.getClass() ) );
						UI.getCurrent().addWindow( popup );
					} else
					{
						Map<String,String> labels = new HashMap<String,String>();
						NodeWizardCForm form = new NodeWizardCForm( CFormFactory.getWizardPanelsFromTargetNode( TreeHandler.getRootNodeByApplication(), newNode, Action.INSERT,
								labels ), Action.INSERT )
						{
							@Override
							public void wizardCancel()
							{
								( (CApplication) CApplication.getCurrent() ).getEventHandler().removeListener( this );
								super.wizardCancel();
							}

							@Override
							public void wizardSave()
							{
								( (CApplication) CApplication.getCurrent() ).getEventHandler().removeListener( this );
								super.wizardSave();
							}

						};
						form.setFormLabel( labels );
						( (CApplication) CApplication.getCurrent() ).getEventHandler().addListener( form );
						UI.getCurrent().addWindow( form.getCurrentStep() );
					}
					break;

				case IMPORT:
					break;
				case UPDATE:
//					Node currentNode = NodeUtil.getNode( list.get( 0 ), TreeHandler.getTargetNode() );
					Node currentNode = list.get(0);
					if( !CFormFactory.isWizard( list.get( 0 ).getClass() ) )
					{
						final SingleNodeCContainer container = new SingleNodeCContainer( currentNode, Action.UPDATE, FormMode.WIZARD );
						( (CApplication) CApplication.getCurrent() ).getEventHandler().addListener( container );
						PopUpModel popup = new PopUpModel()
						{
							@Override
							public void close()
							{
								( (CApplication) CApplication.getCurrent() ).getEventHandler().removeListener( container );
								super.close();
							}
						};

						Panel panel = LayoutUtil.addScrollablePanel( container, true );
						if( currentNode instanceof MemberHolder )
							panel.getContent().setWidth( "1380px" );// special case for MemberHolders
						popup.add(panel);
						popup.setCaption( LocalizationHandler.get( currentNode.getClass().getAnnotation( DomainClass.class ).label() ) );
						UI.getCurrent().addWindow( popup );
					} else
					{
						Map<String,String> labels = new HashMap<String,String>();
						NodeWizardCForm form = new NodeWizardCForm( CFormFactory.getWizardPanelsFromTargetNode( TreeHandler.getRootNodeByApplication(), currentNode, Action.UPDATE,
								labels ), Action.UPDATE )
						{
							@Override
							public void wizardCancel()
							{
								( (CApplication) CApplication.getCurrent() ).getEventHandler().removeListener( this );
								super.wizardCancel();
							}

							@Override
							public void wizardSave()
							{
								( (CApplication) CApplication.getCurrent() ).getEventHandler().removeListener( this );
								super.wizardSave();
							}

						};
						form.setFormLabel( labels );
						( (CApplication) CApplication.getCurrent() ).getEventHandler().addListener( form );
						UI.getCurrent().addWindow( form.getCurrentStep() );
					}
					break;

				case DELETE:
					for( Node n : list ) {
						if(DynamicDomain.class.isAssignableFrom(n.getClass()))
							TreeHandler.get().destroyTree( NodeClassUtil.getSerializationNode(n) );
						else TreeHandler.get().deleteTree( n );
					}
					break;

				case UNDELETE:
					for( Node n : list )
						TreeHandler.get().unDeleteTree( n );
					break;

				case DEACTIVATE:
					Node node = NodeUtil.getNode( list.get( 0 ).getParentId(), TreeHandler.getTargetNode() );
					if( CNodeUtil.isRelatedToWindowsGroupOrResource( node ) /* && AbstractWindowsAccount.class.isAssignableFrom( menuHelper.getSelectedNodes().get( 0 ).getClass() )*/ )
						Notification.show( LocalizationHandler.get( LabelUtil.LABEL_CAN_NOT_DELETE_WINDOWS_ACCOUNT_OF_WINDOWS_MEMBER_ERROR ) );
					else TreeHandler.get().destroyTree( list.get( 0 ) );
					break;
				case DESTROY:
					for( Node n : list )
						TreeHandler.get().destroyTree( n );
					break;
				case HIDE:
				case SHOW:
					menuHelper.triggerHideMenuItem();
					break;

				default:
					break;
			}
		} catch( Exception e )
		{
			TreeHandler.handleTreeException( e );
		}
	}

	@Override
	protected PermissionDescriptor getPermissionDescriptor()
	{
		return CNodeUtil.getPermissionDescriptor( menuHelper.getSelectedNodes() );
	}

	@Override
	protected void onItemRendering( MenuItem item, MenuAction menuAction )
	{
		switch( menuAction )
		{
			case UNDELETE:
				if( !menuHelper.getSelectedNodes().isEmpty() )
				{
					boolean enableItem = true;
					for( Node n : menuHelper.getSelectedNodes() )
					{
						Node parent = NodeUtil.getNode( n.getParentId(), TreeHandler.getRootNode() );
						if( parent.isDeleted() )
							enableItem = false;
					}

					item.setEnabled( enableItem );
				}
				break;
			default:
				break;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List<Class<? extends Node>> getValidDynamicChidren(Class<? extends Node> class1) {
		List<Class<? extends Node>> list = super.getValidDynamicChidren(class1);
		
		Map<String, String> codes = new HashMap<String, String>();
		Node root = TreeHandler.getRootNode();
		for(Node node : NodeUtil.getChildren(root, root))
			if(node instanceof Entity) {
				Entity entity = (Entity) node;
				codes.put(entity.getFullName(), entity.getSourceCode());
			}
		for(Entry<String, String> entry : codes.entrySet()) {
			Class<?> clazz = JCompiler.getInstance().registerClass(entry.getKey(), codes);
			DomainClass dc = clazz.getAnnotation(DomainClass.class);
			if(dc == null)
				continue;
			if((dc.validParents() != null && Arrays.asList(dc.validParents()).contains(class1)) || class1.equals(Node.class))
				list.add((Class<? extends Node>)clazz);
		}
		return list;
	}
}
