package com.fantasystep.component.utils;

import com.fantasystep.domain.Node;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.util.ItemSorter;

@SuppressWarnings("serial")
public class NodeSorter implements ItemSorter
{
	@Override
	public int compare( Object itemId1, Object itemId2 )
	{
		Node n1 = (Node) itemId1;
		Node n2 = (Node) itemId2;

		if( n1 != null && n2 != null )
		{

//			if( n1 instanceof Organization && !( n2 instanceof Organization ) || ( n1 instanceof AbstractGroup && !( n2 instanceof AbstractGroup ) )
//					|| ( n1 instanceof Organization && n2 instanceof AbstractGroup ) )
//				return -1;
//
//			if( ( n1 instanceof User && !( n2 instanceof User ) ) || n1 instanceof AbstractGroup && n2 instanceof Organization )
//				return 1;
//
//			if( ( ( n1 instanceof Organization ) && ( n2 instanceof Organization ) ) || ( ( n1 instanceof AbstractGroup ) && ( n2 instanceof AbstractGroup ) )
//					|| ( ( n1 instanceof User ) && ( n2 instanceof User ) ) )
//				return ( n1.getLabel().trim() ).compareToIgnoreCase( n2.getLabel().trim() );

			if( n1.getLabel() != null && n2.getLabel() != null ) // if label does not retun null value
				return ( n1.getLabel().trim() ).compareToIgnoreCase( n2.getLabel().trim() );
			else
				return -1;
		}

		return -1;
	}

	@Override
	public void setSortProperties( Sortable container, Object[] propertyId, boolean[] ascending )
	{
	}
}
