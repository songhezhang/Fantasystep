package com.fantasystep.component.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fantasystep.annotation.DomainClass;
import com.fantasystep.component.MemberHelper;
import com.fantasystep.component.panel.Listener;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.utils.NodeUtil;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class MembersTable extends AbstractMembersTable implements MemberHelper, Listener
{
	private static final long serialVersionUID = 837994806492018581L;

	private class PathGenerator implements ColumnGenerator
	{
		private static final long serialVersionUID = 6245242086235263018L;
		@Override
		public Component generateCell( Table source, Object itemId, Object columnId )
		{
			if( itemId != null && getPathsMap().get( itemId ) != null )
				return new Label( getPathsMap().get( itemId ) );

			return new Label( LocalizationHandler.get( LabelUtil.LABEL_NOT_FOUND ) );
		}
	}

	private class TypeGenerator implements ColumnGenerator
	{
		private static final long serialVersionUID = 5303008901507214328L;
		@Override
		public Component generateCell( Table source, Object itemId, Object columnId )
		{
			if( itemId != null )
				return new Label( LocalizationHandler.get( ( (Node) itemId ).getClass().getAnnotation( DomainClass.class ).label() ) );
			else
				return new Label( "" );
		}
	};

	private Map<Node,String>	pathsMap			= null;

	public MembersTable( Node root, Node target, List<Node> list, Class<? extends Node> nodeType )
	{
		this( root, target, list, nodeType, null );
	}

	public MembersTable( Node root, Node target, List<Node> list, Class<? extends Node> nodeType, boolean isSource )
	{
		this( root, target, list, nodeType, null, isSource );
	}

	public MembersTable( Node root, Node target, List<Node> list, Class<? extends Node> nodeType, Map<Node,String> pathMap )
	{
		this( root, target, list, nodeType, pathMap, true );
	}

	public MembersTable( Node root, Node target, List<Node> list, Class<? extends Node> nodeType, Map<Node,String> pathMap, boolean isSource )
	{
		super( root, target, list, nodeType, isSource );
		setMultiSelect( true );

		if( pathMap == null )
			setPaths( list );
		else
			this.pathsMap = pathMap;

		addCustomColumns();
	}

	private void addCustomColumns()
	{
		// remove generated column on notifier case
		removeGeneratedColumn( LocalizationHandler.get( LabelUtil.LABEL_PATH ) );
		removeGeneratedColumn( LocalizationHandler.get( LabelUtil.LABEL_TYPE ) );

		addGeneratedColumn( LocalizationHandler.get( LabelUtil.LABEL_TYPE ), new TypeGenerator() );
//		setColumnWidth( LocalizationHandler.get( LabelUtil.LABEL_TYPE ), 150 );

		addGeneratedColumn( LocalizationHandler.get( LabelUtil.LABEL_PATH ), new PathGenerator() );

		List<String> visCols = new ArrayList<String>();
		visCols.add( LocalizationHandler.get( LabelUtil.LABEL_TITLES ) );
		visCols.add( LocalizationHandler.get( LabelUtil.LABEL_TYPE ) );
		if( this.isSource )
			visCols.add( LocalizationHandler.get( LabelUtil.LABEL_SOURCE ) );
		visCols.add( LocalizationHandler.get( LabelUtil.LABEL_PATH ) );

		setVisibleColumns( visCols.toArray() );

	}

	@Override
	public void addNodes( List<Node> selectedNodes )
	{
		setPaths( selectedNodes );
		super.addNodes( selectedNodes );
	}

	private Map<Node,String> getPathsMap()
	{
		if( pathsMap == null )
			pathsMap = new HashMap<Node,String>();
		return pathsMap;
	}

	@Override
	public void notify( NodeEvent event )
	{
		getDataSource().removeAllItems();
		setDataSource( getNodes() );

		getPathsMap().clear();
		setPaths( new ArrayList<Node>( getNodes() ) );

		addCustomColumns();

		super.notify( event );
	}

	private void setPaths( List<Node> nodes )
	{
		if( this.rootNode != null )
			for( Node n : nodes )
				if( n != null && getPathsMap().get( n ) == null )
				{
					String path = NodeUtil.getMemberPath( n, this.rootNode );
					getPathsMap().put( n, ( path == null ) ? "" : path );
				}
	}
}
