package com.fantasystep.component.table;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fantasystep.annotation.AnnotationsParser;
import com.fantasystep.component.panel.ConcreteNodeEvent;
import com.fantasystep.component.panel.Listener;
import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.component.utils.IconUtil;
import com.fantasystep.component.utils.NodeSorter;
import com.fantasystep.component.utils.PropertyUtil;
import com.fantasystep.component.utils.TableUtil;
import com.fantasystep.component.utils.UINodeUtil;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.helper.Status;
import com.fantasystep.utils.NodeClassUtil;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class NodeTable extends Table implements Listener
{
	class CustomCellStyle implements CellStyleGenerator
	{
		@Override
		public String getStyle(Table source, Object itemId, Object propertyId) {
			if( size() == 0 || propertyId == null || itemId == null )
				return null;
			try
			{
				java.lang.reflect.Field field = NodeClassUtil.getField( ( (Node) ( itemId ) ).getClass(), propertyId.toString() );
				if( field == null )
					return "";
				String style = field.getType().getSimpleName().toLowerCase();
				Object val = getItem( itemId ).getItemProperty( propertyId ).getValue();

				if( val == null )
					return "";

				if( val instanceof Status )
					return String.format( "dummy %s %s-%s", style, style, val.toString().toLowerCase() );
				else if( val instanceof Boolean )
					return String.format( "dummy %s status-%s", style, val.toString().toLowerCase() );

				if( ( (Node) ( itemId ) ).isDeleted() )
					return CSSUtil.DELETED_ITALIC_TABLE_ITEM;

				/** For primary key */
				if( UINodeUtil.isPrimarySiblingValue( (Node) itemId ) )
					return CSSUtil.GREEN_BOLD_ITALIC_TABLE_ITEM;

				return "";
			} catch( SecurityException e )
			{
				return "null";
			} catch( IllegalArgumentException e )
			{
				return "null";
			}
		}
	}

	protected class TitleGenerator implements ColumnGenerator
	{
		@Override
		public Component generateCell( final Table source, final Object itemId, Object columnId )
		{
			if( itemId != null )
				return IconUtil.getIconLabel( (Node) itemId, IconUtil.SMALL_ICON_SIZE, true );
			else
				return new Label( "" );
		}
	};

	private BeanItemContainer<Node>	dataSource	= null;
	private List<? extends Node>	nodes;
	protected Class<? extends Node>	nodeType;
	private List<String>			uiFields;

	public NodeTable( Node root, List<Node> nodes, Class<? extends Node> nodeType )
	{
		setNodes( nodes );
		setNodeType( nodeType );
		setImmediate( true );
		setSelectable( true );
		setMultiSelect( false );
		setColumnCollapsingAllowed( true );
		setColumnReorderingAllowed( false );
		setCellStyleGenerator( new CustomCellStyle() );
		setSortEnabled( false );

		this.uiFields = AnnotationsParser.getUIControlFieldNames( getNodeType() );
		setContainerDataSource( getDataSource() );

		List<String> slist = PropertyUtil.getSpecialsList( getNodeType() );
		if( !slist.isEmpty() )
		{
			// Node root = TreeHandler.getRootNodeByApplication( getApplication() );
			for( String field : slist )
			{
				addGeneratedColumn( field, TableUtil.wrapValue( root ) );
				setColumnExpandRatio( field, 1 );
			}
		}
		setSizeFull();

		setVisibleColumns( this.uiFields.toArray() );

		List<String> headers = PropertyUtil.getHeaderLabels( getNodeType(), this.uiFields );
		setColumnHeaders( headers.toArray( new String[headers.size()] ) );

		// setWidth( 100, Sizeable.UNITS_PERCENTAGE );

	}

	public Item addNewItem( Node node )
	{
		Item item = addItem( node );
		markAsDirty();
		return item;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BeanItemContainer getDataSource()
	{
		if( dataSource == null )
		{
			dataSource = new BeanItemContainer( getNodeType() );
			setDataSource( getNodes() );
		}
		return dataSource;
	}

	public List<? extends Node> getNodes()
	{
		return nodes;
	}

	public Class<? extends Node> getNodeType()
	{
		return nodeType;
	}
	
	public List<UUID> getNodeIds() {
		List<UUID> list = new ArrayList<UUID>();
		if(nodes != null)
			for(Node node : nodes)
				list.add(node.getId());
		return list;
	}

	@Override
	public void notify( NodeEvent event )
	{
		if( ( (ConcreteNodeEvent) event ).getTargetNode() != null )
		{
			nodes = ( (ConcreteNodeEvent) event ).getTargetNode().getChildren( getNodeType() );
			getDataSource().removeAllItems();
			setDataSource( nodes );

			if( this.isVisible() )
				markAsDirty();
		}
	}

	public void setDataSource( List<? extends Node> list )
	{
		dataSource.removeAllItems();
		if( list != null )
			for( Node node : list )
				if( node != null )
					dataSource.addBean( node );

		dataSource.setItemSorter( new NodeSorter() );
		dataSource.sort( uiFields.toArray(), new boolean[] { true } );

//		if( list.size() == 0 )
//			this.setVisible( false );
//		else
			this.setVisible( true );
	}

	public void setNodes( List<? extends Node> nodes )
	{
		this.nodes = nodes;
	}

	private void setNodeType( Class<? extends Node> nodeType )
	{
		this.nodeType = nodeType;
	}

}
