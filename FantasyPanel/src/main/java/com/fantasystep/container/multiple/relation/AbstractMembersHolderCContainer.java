package com.fantasystep.container.multiple.relation;


import java.util.ArrayList;
import java.util.List;

import com.fantasystep.component.MemberHelper;
import com.fantasystep.component.NodeEnum;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.table.AbstractMembersTable;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.MembersUiUtil;
import com.fantasystep.component.utils.UINodeUtil;
import com.fantasystep.container.multiple.AbstractMultiNodeCContainer;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.MemberHolder;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.util.CNodeUtil;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractMembersHolderCContainer extends AbstractMultiNodeCContainer
{
	private static final long serialVersionUID = -3675494464391311411L;
	
	protected static final int		CONTAINER_HEIGHT	= 450;
	private List<Node>				clonedNodes;
	protected Button				mvLeftBtn			= new Button( String.format( "<< %s ", LocalizationHandler.get( LabelUtil.LABEL_REMOVE_ITEMS ) ) );
	protected Button				mvRightBtn			= new Button( String.format( "%s >>", LocalizationHandler.get( LabelUtil.LABEL_ADD_ITEMS ) ) );
	protected MemberHelper			sourceContainer;
	protected AbstractMembersTable	targetContainer;

	private Node					targetNode			= null;

	public AbstractMembersHolderCContainer( List<Node> nodeList, Node targetNode, Class<? extends Node> membersType, FormMode mode )
	{
		super( nodeList, NodeEvent.Action.UPDATE, membersType, mode, String.format( "%s %s", LocalizationHandler.get( LabelUtil.LABEL_UPDATE ), LabelUtil.getDomainLabel( membersType ).toLowerCase() ) );

		this.targetNode = targetNode;
		this.clonedNodes = new ArrayList<Node>();
		this.clonedNodes.addAll( nodeList );
	}

	@Override
	public void buttonClick( ClickEvent event )
	{
		if( event.getButton() == mvRightBtn )
		{
			if( isValidMove() )
			{
				if( CNodeUtil.getPermissionDescriptor( getSourceContainer().getSelectedNodes() ).hasSelectPermission() )
				{
					getTargetContainer().addNodes( getSourceContainer().getSelectedNodes() );
					getSourceContainer().addState( getSourceContainer().getSelectedNodes(), NodeEnum.NodeState.NOT_AVAILABLE );
				} else
				{
					Notification.show( LocalizationHandler.get( LabelUtil.LABEL_PERMISSION_ERROR ) + "!", LocalizationHandler.get( LabelUtil.LABEL_INVALID_MOVE ),
							Type.WARNING_MESSAGE );
				}
			} else Notification.show( LocalizationHandler.get( LabelUtil.LABEL_ALERT ) + "!", LocalizationHandler.get( LabelUtil.LABEL_INVALID_MOVE ),
					Type.ERROR_MESSAGE );
		} else if( event.getButton() == mvLeftBtn )
		{
			if( !isInheritedSelected() )
			{
				// Note: Remove selected nodes from target table
				getTargetContainer().removeSelectedNodes();
				// remove 'unavailable' nodes from statesMap : Note: statesMap holds unavailable/invalid states
				getSourceContainer().removeState( getTargetContainer().getSelectedNodes() );

			} else Notification.show( LocalizationHandler.get( LabelUtil.LABEL_ALERT ) + "!",
					LocalizationHandler.get( LabelUtil.LABEL_NOT_ALLOWED_TO_MOVE_INHERITED_NODES ), Type.ERROR_MESSAGE );

		} else if( event.getButton() == save )
		{
			updateMembers();
			closeWindow();
		} else if( event.getButton() == cancel )
			closeWindow();

	}

	private Component getMembersLayout()
	{
		VerticalLayout wrapper = new VerticalLayout();
		// adding spacer
		wrapper.addComponent( new Label( "<br/>", ContentMode.HTML ) );

		HorizontalLayout hz = new HorizontalLayout();
		wrapper.addComponent( hz );

		mvLeftBtn.setWidth( "145px" );
		mvRightBtn.setWidth( "145px" );
		mvLeftBtn.addClickListener( (Button.ClickListener) this );
		mvRightBtn.addClickListener( (Button.ClickListener) this );

		VerticalLayout vl = new VerticalLayout();
		vl.addComponent( mvRightBtn );
		vl.addComponent( new Label( "&nbsp;", ContentMode.HTML ) ); // space
		vl.addComponent( mvLeftBtn );

		vl.setComponentAlignment( mvRightBtn, Alignment.MIDDLE_CENTER );
		vl.setComponentAlignment( mvLeftBtn, Alignment.MIDDLE_CENTER );
		vl.setWidth( "205px" );

		getTargetContainer().setWidth( "100%" );
		getTargetContainer().setHeight( "450px" );

		hz.setWidth( "100%" );

		Component source = getSourceWrapper();
		hz.addComponent( source );
		hz.setComponentAlignment( source, Alignment.TOP_LEFT );

		hz.addComponent( vl );
		hz.setComponentAlignment( vl, Alignment.MIDDLE_CENTER );

		AbstractMembersTable target = getTargetContainer();
		target.setWidth( "100%" );
		hz.addComponent( target );
		hz.setComponentAlignment( target, Alignment.TOP_LEFT );
		hz.setExpandRatio( target, 1.0f );

		wrapper.setSpacing( true );
		wrapper.addComponent( MembersUiUtil.getHorizontalLagend() );

		if( withSaveAndCancelButtons )
		{
			HorizontalLayout hl = new HorizontalLayout();
			hl.addComponent( save );
			hl.addComponent( cancel );
			hl.setSpacing( true );

			// adding spacer
			wrapper.addComponent( new Label( "<br/>", ContentMode.HTML ) );
			// adding buttons layout
			wrapper.addComponent( hl );
			wrapper.setComponentAlignment( hl, Alignment.TOP_LEFT );
		}

		getTargetContainer().setHeight( CONTAINER_HEIGHT + "px" );

		return wrapper;
	}

	protected abstract MemberHelper getSourceContainer();

	protected abstract Component getSourceWrapper();

	protected abstract AbstractMembersTable getTargetContainer();

	public List<Node> getUpdateList()
	{
		List<Node> newList = new ArrayList<Node>();
		Node root = TreeHandler.getRootNodeByApplication();

		// update membership on new existing nodes
		for( Object n : getTargetContainer().getItemIds() )
			if( !( (MemberHolder) n ).getMembers().contains( this.targetNode.getId() ) )
			{
				if( UINodeUtil.hasInheritedNodes( (MemberHolder) n, this.targetNode, root ) )
					clonedNodes.remove( n ); // remove inherited nodes
				else
				{
					( (MemberHolder) n ).addMember( this.targetNode );
					newList.add( (Node) n );
				}
			}

		// remove old loaded nodes, if these nodes still exists in targetContainer
		for( Node cn : this.clonedNodes )
			if( !newList.contains( cn ) && !getTargetContainer().getItemIds().contains( cn ) )
			{
				( (MemberHolder) cn ).removeMember( this.targetNode );
				newList.add( cn );
			}

		return newList;
	}

	@Override
	protected void initDisplay()
	{
		if( isSourceEmpty() )
			addComponent( LabelUtil.getNoneDataLabel() );
		else
		{
			// Panel panel = LayoutUtil.addScrollablePanel( getMembersLayout(), true );
			// panel.getContent().setWidth( LayoutUtil.CONST_MEMBERS_SIZE, Sizeable.UNITS_PIXELS ); // special case
			addComponent( getMembersLayout() );
		}
	}

	@Override
	protected void initForm()
	{
	}

	private boolean isInheritedSelected()
	{
		for( Node n : getTargetContainer().getSelectedNodes() )
			if( n instanceof MemberHolder && UINodeUtil.hasInheritedNodes( (MemberHolder) n, this.targetNode, TreeHandler.getRootNodeByApplication() ) )
				return true;
		return false;
	}

	protected abstract boolean isSourceEmpty();

	protected abstract boolean isValidMove();

	@Override
	public void notify( NodeEvent event )
	{
		( (com.fantasystep.component.panel.Listener) sourceContainer ).notify( event );
		targetContainer.notify( event );
	}

	private void updateMembers()
	{
		if( mode == FormMode.TAB )
		{
			Throwable t = null;
			try
			{
				for( Node member : getUpdateList() )
					TreeHandler.get().modifyTreeNode( member );

			} catch( Exception e )
			{
				t = e;
				TreeHandler.handleTreeException( e );
			} finally
			{
				if( t != null )
					TreeHandler.updateTreeFromStorage();
			}
		}
	}

	protected abstract void validateNodes();
}
