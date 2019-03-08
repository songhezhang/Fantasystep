package com.fantasystep.persistence.ldap;

import java.util.List;

public class FilterUtil
{
	public enum Operator
	{
		AND( "&" ), OR( "|" );
		private String	value;
		Operator( String value )
		{
			this.value = value;
		}

		public String asString()
		{
			return value;
		}
	}

	public static String combineFilter( Operator operator, String... filters )
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "(" );
		sb.append( operator.asString() );
		for( String filter : filters )
			sb.append( filter );
		sb.append( ")" );
		return sb.toString();
	}

	public static String buildFilter( Operator operator, String keyName, List<Object> keyValues )
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "(" );
		sb.append( operator.asString() );
		for( Object keyValue : keyValues )
		{
			sb.append( String.format( "(%s=%s)", keyName, keyValue.toString() ) );
		}
		sb.append( ")" );
		return sb.toString();
	}

	public static String getFilter( String preparedFilter, Object... arguments )
	{
		for( Object arg : arguments )
			preparedFilter = preparedFilter.replace( "%s", arg.toString() );

		return preparedFilter;
	}
}
