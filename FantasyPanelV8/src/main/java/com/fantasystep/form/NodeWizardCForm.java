package com.fantasystep.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.container.AbstractCContainer;
import com.fantasystep.container.multiple.AbstractMultiNodeCContainer;
import com.fantasystep.container.multiple.relation.AbstractMembersHolderCContainer;
import com.fantasystep.container.single.AbstractNodeCContainer;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent.Action;
import com.fantasystep.panel.CApplication;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.utils.NodeUtil;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class NodeWizardCForm extends AbstractWizardCForm
{
	private static final long serialVersionUID = 5559902066314083201L;

	public NodeWizardCForm( Map<String,AbstractCContainer> containersMap, Action action )
	{
		super( containersMap, action );
	}

	@Override
	public void wizardBack()
	{
		--currentPage;
		getCurrentStep();
	}

	@Override
	public void wizardCancel()
	{
		TreeHandler.updateTreeFromStorage();
		( (CApplication) CApplication.getCurrent() ).getEventHandler().removeListener( this );
	}

	@Override
	public void wizardNext()
	{
		AbstractCContainer container = getCContainerByIndex( currentPage );
		if( container.validContainerForm() )
		{
			++currentPage;
			getCurrentStep();
		} else Notification.show( LocalizationHandler.get( LabelUtil.LABEL_VALIDATE_ERROR ) );
	}

	@SuppressWarnings("deprecation")
	@Override
	public void wizardSave()
	{
		Throwable t = null;
		try
		{
			// This currentNode must be the first step (target node) can not be null
			Node currentNode = null;
			for( AbstractCContainer container : getContainersMap().values() )
			{

				if( AbstractNodeCContainer.class.isAssignableFrom( container.getClass() ) )
				{
					AbstractNodeCContainer con = (AbstractNodeCContainer) container;

					if( currentNode == null )
						currentNode = con.getNode();

					if( con.getVolatileFile( con.getNode() ) != null )
						con.handleVolatile();
					if( !con.getForm().isValid() )
					{
						Notification.show( LocalizationHandler.get( LabelUtil.LABEL_VALIDATE_ERROR ), Type.WARNING_MESSAGE );
						return;
					}
					con.getForm().commit();
					if( con.getAction() == Action.INSERT )
						TreeHandler.get().insertTreeNode( con.getNode() );
					else if( con.getAction() == Action.UPDATE )
						TreeHandler.get().modifyTreeNode( con.getNode() );

				} else if( AbstractMembersHolderCContainer.class.isAssignableFrom( container.getClass() ) )
				{
					for( Node member : ( (AbstractMembersHolderCContainer) container ).getUpdateList() )
						TreeHandler.get().modifyTreeNode( member );
				} else if( AbstractMultiNodeCContainer.class.isAssignableFrom( container.getClass() ) )
				{
					AbstractMultiNodeCContainer con = (AbstractMultiNodeCContainer) container;
					// Node targetNode = TreeHandler.getTargetNodeByApplication( getApplication() );

					List<Node> list = new ArrayList<Node>();
					for( Node node : currentNode.getChildren( con.getNodeClass() ) )
						list.addAll( NodeUtil.getChildren( node, node, true ) );

					List<Node> destroyList = new ArrayList<Node>();
					if( con.getAction() != Action.INSERT )
					{
						for( Node node : list )
							if( !con.getNodeList().contains( node ) )
							{
								boolean hasParent = false;
								for( Node dNode : destroyList )
								{
									if( dNode.getId().equals( node.getParentId() ) )
									{
										hasParent = true;
										break;
									}
								}
								if( !hasParent )
									destroyList.add( node );
							}
					}

					for( Node node : con.getNodeList() )
					{
						if( con.getAction() == Action.INSERT )
							TreeHandler.get().insertTreeNode( node );
						else if( con.getAction() == Action.UPDATE )
						{
							if( list.contains( node ) )
								TreeHandler.get().modifyTreeNode( node );
							else TreeHandler.get().insertTreeNode( node );
						}
					}
					for( Node node : destroyList )
						TreeHandler.get().destroyTree( node );
				}
			}
		} catch( Exception e )
		{
			t = e;
			TreeHandler.handleTreeException( e );
		} finally
		{
			if( t != null )
				TreeHandler.updateTreeFromStorage();
		}
		( (CApplication) CApplication.getCurrent() ).getEventHandler().removeListener( this );
	}
}
