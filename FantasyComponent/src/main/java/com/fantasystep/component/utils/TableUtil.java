package com.fantasystep.component.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.fantasystep.annotation.AnnotationsParser;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.domain.Node;
import com.fantasystep.utils.NodeUtil;
import com.fantasystep.utils.StringUtil;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

public class TableUtil
{
	public static String getValueByClass( Object value, Node root )
	{
		if( value == null || root == null )
			return "";

		Class<? extends Object> clazz = value.getClass();

		if( value instanceof Collection )
		{
			List<String> labelParts = new ArrayList<String>();
			for( Object item : (Collection<?>) value )
				labelParts.add( getValueByClass( item, root ) );

			return StringUtil.join( labelParts, ", " );
		}

		if( clazz.equals( Class.class ) )
		{
			if( ( (Class<?>) value ).isAnnotationPresent( DomainClass.class ) )
				return LocalizationHandler.get( AnnotationsParser.getAttributes( (Class<?>) value ).getLabel() );
			return LocalizationHandler.get( ( (Class<?>) value ).getSimpleName() );
		}

		if( clazz.equals( UUID.class ) )
		{
			Node node = NodeUtil.getNode( (UUID) value, root );
			if( node != null )
				return node.getLabel();
			else
				return value.toString();
		}

		return value.toString();
	}

	@SuppressWarnings("serial")
	public static ColumnGenerator wrapValue( final Node root )
	{
		return new Table.ColumnGenerator()
		{
			@Override
			public Component generateCell( Table source, Object itemId, Object columnId )
			{
				if( columnId != null )
				{
					Object value = source.getItem( itemId ).getItemProperty( columnId ).getValue();

					if( value instanceof Collection )
						return IconUtil.getIconsCollection( value, IconUtil.SMALL_ICON_SIZE );
					else if( value instanceof UUID )
						return IconUtil.getIconLabel( (UUID) value, root, IconUtil.SMALL_ICON_SIZE );

					return new Label( getValueByClass( value, root ).toString() );

				}

				return new Label( "null" );
			}
		};
	}
}
