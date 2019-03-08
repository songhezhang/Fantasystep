package com.fantasystep.container.multiple.account;

import com.fantasystep.annotation.AnnotationsParser;
import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.common.PopUpModel;
import com.fantasystep.component.field.custom.LabelCField;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.component.utils.IconUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.container.single.AbstractNodeCContainer;
import com.fantasystep.container.single.SingleNodeCContainer;
import com.fantasystep.domain.AbstractAccount;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.helper.NodeEvent.Action;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.utils.NodeUtil;
import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class LinkAccountCContainer extends AbstractAccountCContainer
{

	public LinkAccountCContainer( NodeEvent.Action action, FormMode mode, Class<? extends Node> currentNodeClass )
	{
		super( action, mode, currentNodeClass );
	}

	@Override
	protected String getLabel( Class<? extends AbstractAccount> account )
	{
		String accountLabel;
		if( getAccount( account ) != null )
			accountLabel = String.format( "%s %s", LocalizationHandler.get( LabelUtil.LABEL_EDIT ), LocalizationHandler.get( validAccountsMap.get( account ) ) );
		else
			accountLabel = String.format( "%s %s", LocalizationHandler.get( NodeActionType.ACTIVATE.getLabel() ), LocalizationHandler.get( validAccountsMap.get( account ) ) );
		return accountLabel;
	}

	@Override
	protected Component getNodeComponent( final Class<? extends AbstractAccount> account )
	{
		HorizontalLayout layout = new HorizontalLayout();
		final Button link = new Button();
		link.setStyleName( BaseTheme.BUTTON_LINK );

		link.addClickListener( new ClickListener()
		{
			@Override
			public void buttonClick( ClickEvent event )
			{
				PopUpModel popup = new PopUpModel( "", 40, 80);
				popup.add( accountMap.get( account ) );
				popup.setCaption( getLabel( account ) );
				UI.getCurrent().addWindow( popup );
			}
		} );

		if( getAccount( account ) != null )
		{

			final Button deactivate = new Button();
			deactivate.setCaption( String.format( "%s %s", LocalizationHandler.get( NodeActionType.DE_ACTIVATE.getLabel() ), LocalizationHandler.get( validAccountsMap.get( account ) ) ) );
			IconUtil.wrapIcon( deactivate, IconUtil.ICON_DEACTIVATE, IconUtil.SMALL_ICON_SIZE, deactivate.getCaption() );

			deactivate.addClickListener( new ClickListener()
			{
				@Override
				public void buttonClick( ClickEvent event )
				{
					if( mode == FormMode.TAB )
					{
						Throwable t = null;
						try
						{
							TreeHandler.get().destroyTree( accountMap.get( account ).getNode() );
						} catch( Exception e )
						{
							t = e;
							TreeHandler.handleTreeException( e );
						} finally
						{
							if( t != null )
								TreeHandler.updateTreeFromStorage();
						}
					} else
					{
						removeAccount( account );
						repaintForWizard();
					}
				}
			} );

			// set edit setting
			IconUtil.wrapIcon( link, IconUtil.ICON_EDIT, IconUtil.SMALL_ICON_SIZE, LocalizationHandler.get( LabelUtil.LABEL_EDIT ), CSSUtil.GRAY_STRONG_ITALIC_TEXT );

			layout.setMargin( new MarginInfo(false, true, false, true) );
			layout.addComponent( deactivate );
		} else
		{
			link.setCaption( getLabel( account ) ); // else create new account title
			IconUtil.wrapIcon( link, IconUtil.ICON_ACTIVATE, IconUtil.SMALL_ICON_SIZE, link.getCaption() );
		}

		layout.addComponent( link );
		layout.setComponentAlignment( link, Alignment.TOP_LEFT );

		return layout;
	}

	@SuppressWarnings({"unchecked", "rawtypes", "deprecation"})
	@Override
	protected AbstractNodeCContainer getNodeContainer( final Class<? extends AbstractAccount> account )
	{
		Node node = null;
		if( getAccount( account ) != null )
			node = getAccount( account );
		else
		{
			Node targetNode = TreeHandler.getTargetNodeByApplication();
			node = NodeUtil.getNewNode( account, targetNode.getId() );
			// ( (ParentListener) node ).update( targetNode );
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
						LinkAccountCContainer.this.nodeList.add( getNode() );
						this.detach();
						repaintForWizard();

					} else if( source == edit )
					{
						setReadOnly( false );
					} else if( source == cancel )
					{
						this.detach();
					}
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
	}

	private void repaintForWizard()
	{
		this.removeAllComponents();
		for( Class<? extends AbstractAccount> account : validAccountsMap.keySet() )
			this.addComponent( this.getNodeComponent( account ) );
	}
}
