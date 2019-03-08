package com.fantasystep.component.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fantasystep.component.NodeEnum;
import com.fantasystep.component.common.HierarchicalNodes;
import com.fantasystep.component.panel.ConcreteNodeEvent;
import com.fantasystep.component.panel.Listener;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.helper.PropertyHelper;
import com.fantasystep.utils.NodeClassUtil;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;

@SuppressWarnings("serial")
public class NodeTreeTable extends TreeTable implements Listener
{
	class CustomCellStyle implements CellStyleGenerator
	{
		@Override
		public String getStyle(Table source, Object itemId, Object propertyId) {
			if( itemId != null && ( (Node) itemId ).isDeleted() )
				return CSSUtil.DELETED_ITALIC_TABLE_ITEM;
			else
				return "";
		}
	}

	private Class<? extends Node>	nodeClass;
	private HierarchicalNodes		nodesContainer;

	private Node[]					roots;

	public NodeTreeTable( Class<? extends Node> nodeClass, List<Node> nodeList )
	{
		this.nodeClass = nodeClass;
		this.roots = nodeList.toArray( new Node[nodeList.size()] );
		bindTreeTable();
		if( roots.length == 0 )
			this.setVisible( false );
		else
			this.setVisible( true );
	}

	private void bindTreeTable()
	{

		setContainerDataSource( getNodesContainer() );
		setItemCaptionPropertyId( LabelUtil.LABEL_NAME );
		setColumnWidth( LabelUtil.LABEL_NAME, 500 );

		for( Object head : getNodesContainer().getContainerPropertyIds() )
			setColumnHeader( head, LocalizationHandler.get( head.toString() ) );

		setMultiSelect( true );
		setImmediate( true );
		setSelectable( true );
		setWidth( "100%" );

		setCellStyleGenerator( new CustomCellStyle() );
		setItemIconPropertyId( LabelUtil.LABEL_ICON );

		List<String> vcols = new ArrayList<String>();
		vcols.add( LabelUtil.LABEL_NAME );

		if( getPropertyMappings() != null )
			vcols.addAll( getPropertyMappings().keySet() );

		setVisibleColumns( vcols.toArray() );
	}

	public HierarchicalNodes getNodesContainer()
	{
		if( this.nodesContainer == null )
			this.nodesContainer = new HierarchicalNodes( NodeEnum.NodePropertyType.PROPERTY, false, roots )
			{
				@Override
				public Set<String> getCustomProperties()
				{
					return ( getPropertyMappings() == null ) ? super.getCustomProperties() : getPropertyMappings().keySet();
				}
			};
		return nodesContainer;
	}

	@SuppressWarnings("unchecked")
	public Map<String,List<String>> getPropertyMappings()
	{
		try
		{
			for( Class<?> clazz : NodeClassUtil.getSubClassesInJVM( nodeClass ) )
			{
				Class<? extends Node> clz = (Class<? extends Node>) clazz;
				if( PropertyHelper.class.isAssignableFrom( clz ) )
					return ( (PropertyHelper) clz.newInstance() ).getPropertyMappings();
			}
		} catch( InstantiationException e )
		{
			e.printStackTrace();
		} catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void notify( NodeEvent event )
	{
		List<Node> childs = ( (ConcreteNodeEvent) event ).getTargetNode().getChildren( nodeClass );
		getNodesContainer().removeAllItems();
		getNodesContainer().getDeletedNodes().clear();
		this.roots = childs.toArray( new Node[childs.size()] );
		if( roots.length == 0 )
			this.setVisible( false );
		else
			this.setVisible( true );
		for( Node n : this.roots )
			getNodesContainer().buildContainer( n, n );
		getNodesContainer().sort();
	}
}
