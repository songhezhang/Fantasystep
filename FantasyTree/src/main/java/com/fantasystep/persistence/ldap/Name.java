package com.fantasystep.persistence.ldap;

import java.util.Iterator;
import java.util.LinkedList;

public class Name implements Iterable<Name.NamePart>
{
	private LinkedList<NamePart> nameParts;
	
	public Name()
	{
		nameParts = new LinkedList<NamePart>();
	}

	public void append( String key, String value )
	{
		nameParts.add( new NamePart( key, value ) );
	}
	
	public void prepend( String key, String value )
	{
		nameParts.add( 0, new NamePart( key, value ) );
	}

	public void prefix( Name prefix )
	{
		int i = 0;
		for( NamePart np : prefix )
			nameParts.add(  i++, np );
	}
	
	public void suffix( Name suffix )
	{
		for( NamePart np : suffix )
			nameParts.add( np );
	}
	
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		for( NamePart p : nameParts )
		{
			if( s.length() > 0 )
				s.append( "," );
			s.append( p.toString() );
		}
		return s.toString();
	}
	
	public String getLeafName()
	{
		return nameParts.get( 0 ).nameValue;
	}
	
	public String getSuffix()
	{
		StringBuilder s = new StringBuilder();
		for( int i = 1 ; i < nameParts.size() ; i++ )
		{
			NamePart p = nameParts.get( i );
			if( s.length() > 0 )
				s.append( "," );
			s.append( p.toString() );
		}
		return s.toString();
	}

	public static Name fromString( String dn )
	{
		String[] dnPs = dn.split( "," );
		Name name = new Name();
		for( String dnP : dnPs )
		{
			String[] kv = dnP.split( "=" );
			name.append( kv[0], kv[1] );
		}
		return name;
	}
	
	class NamePart
	{
		public String nameKey;
		public String nameValue;
		public NamePart( String key, String value )
		{
			nameKey = key;
			nameValue = value;
		}
		
		@Override
		public String toString()
		{
			return String.format( "%s=%s", nameKey, nameValue );
		}
	}

	@Override
	public Iterator<NamePart> iterator()
	{
		return nameParts.iterator();
	}
	
	@Override
	public boolean equals( Object object )
	{
		if( ! ( object instanceof Name ) )
			return false;
		return toString().equals( object.toString() );
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
}
