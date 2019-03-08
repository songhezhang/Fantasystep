package com.fantasystep.component.field.custom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.field.AbstractMultiNodeCField;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.table.MembersTable;
import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.LayoutUtil;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.component.panel.Listener;
import com.vaadin.data.util.converter.Converter.ConversionException;

public class NodeMembersReadOnlyCField extends AbstractMultiNodeCField
{

	public class NodeMembersReadOnlyField extends AbstractCustomField implements Listener
	{

		private static final long	serialVersionUID	= 1L;
		private List<Node>			members;
		private Map<Node,String>	pathMap;
		private MembersTable		table;

		public NodeMembersReadOnlyField( String caption, List<Node> members, Map<Node,String> pathMap )
		{
			super( caption, Node.class );
			this.members = members;
			this.pathMap = pathMap;
			bindRequest();
		}

		protected void bindRequest()
		{

			getField().setValue( getTable().getNodesIds() );
			getTable().setStyleName( CSSUtil.CUSTOMIZED_TABLE_STYLE );
			getTable().setSortAscending( true );
			getTable().setSortContainerPropertyId( LocalizationHandler.get( LabelUtil.LABEL_TITLES ) );

			if( this.members.size() > 10 )
				setCompositionRoot( LayoutUtil.addScrollablePanel( getTable(), true, 400 ) );
			else
			{
				setCompositionRoot( getTable() );
				getTable().setHeight( String.format( "%spx", this.members.size() * 60 + 60 ) ); // 60 is header + footer
																								// size
			}
		}

		public MembersTable getTable()
		{
			if( table == null )
				table = new MembersTable( rootNode, targetNode, this.members, Node.class, pathMap );
			return table;
		}

		@Override
		public Object getValue()
		{
			return getTable().getNodesIds();
		}

		@Override
		public void notify( NodeEvent event )
		{
			table.notify( event );
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void setValue( Object newValue ) throws ReadOnlyException, ConversionException
		{
			if( newValue instanceof Collection )
			{
				this.members = new ArrayList( (Collection) newValue );
				bindRequest();
			}
		}
	}

	private Map<Node,String>	pathMap;
	private Node				rootNode;

	private Node				targetNode;

	public NodeMembersReadOnlyCField( Node rootNode, Node targetNode, FieldAttributeAccessor fieldAttributes, List<Node> nodes, Map<Node,String> pathMap )
	{
		super( fieldAttributes, nodes );
		this.rootNode = rootNode;
		this.targetNode = targetNode;
		this.pathMap = pathMap;
	}

	@Override
	public void initField()
	{
		field = new NodeMembersReadOnlyField( this.fieldAttributes.getLabel(), getNodes(), pathMap );
	}
}
