package com.fantasystep.component.utils;

import java.util.ArrayList;
import java.util.List;

import com.fantasystep.annotation.AnnotationsParser;
import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.domain.Node;

public class PropertyUtil
{

	public static List<String> getHeaderLabels( Class<? extends Node> clazz, List<String> fields )
	{
		List<String> headers = new ArrayList<String>();
		for( String field : fields )
		{
			String label = AnnotationsParser.getAttributes( clazz, field ).getLabel();
			headers.add( label );
		}

		return headers;
	}

	public static List<String> getSpecialsList( Class<? extends Node> clazz )
	{
		List<String> slist = new ArrayList<String>();

		for( String col : AnnotationsParser.getUIControlFieldNames( clazz ) )
		{
			FieldAttributeAccessor att = AnnotationsParser.getAttributes( clazz, col );
			if( att.getSpecialDisplay() )
				slist.add( col );
		}

		return slist;
	}
}
