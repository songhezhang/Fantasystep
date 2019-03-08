package com.fantasystep.persistence.ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fantasystep.persistence.ldap.exception.MissingParameterException;
import com.fantasystep.persistence.ldap.exception.NoSuchEntryException;

class AttributeTypeParser
{
	private static Map<String,Class<?>>	convertMap	= new HashMap<String,Class<?>>();
	static
	{
		convertMap.put( "1.3.6.1.4.1.1466.115.121.1.27", Integer.class );
		convertMap.put( "1.3.6.1.4.1.1466.115.121.1.7", Boolean.class );
		convertMap.put( "1.3.6.1.4.1.1466.115.121.1.5", Byte.class );
	}

	public static Object convertFromString( Attribute attr, Object obj ) throws NamingException
	{
		if( null == obj )
			return null;
		if( attr == null )
			return obj;
		String ldapClass = attr.get().toString();
		if( null == ldapClass )
			throw new NamingException();

		Class<?> javaClass = convertMap.get( ldapClass );

		if( javaClass == Integer.class )
			return Integer.parseInt( obj.toString() );
		else if( javaClass == Boolean.class )
			return Boolean.parseBoolean( obj.toString() );
		else if( javaClass == Byte.class )
			return Byte.parseByte( obj.toString() );
		else
			return obj;
	}

	public static Object convertToString( Object obj )
	{
		if( null == obj )
			return null;
		Class<? extends Object> javaClass = obj.getClass();

		if( javaClass == Integer.class || javaClass == Boolean.class || javaClass == Byte.class )
			return obj.toString();
		else
			return obj;
	}
}

public class LDAPClient
{

	private static Logger logger = LoggerFactory.getLogger(LDAPClient.class);
	public enum AlterationType
	{
		ADD( InitialDirContext.ADD_ATTRIBUTE ), DELETE( InitialDirContext.REMOVE_ATTRIBUTE ), REPLACE( InitialDirContext.REPLACE_ATTRIBUTE );
		private int	modification;

		AlterationType( int modification )
		{
			this.modification = modification;
		}

		public int toInt()
		{
			return modification;
		}
	}

	private abstract class ConnectionTester<E>
	{
		protected void onFailure() throws NamingException
		{
			connect();
		}

		public E run() throws NamingException
		{
			return run( 2 );
		}

		public E run( int numberOfRetries ) throws NamingException
		{
			while( numberOfRetries > 0 )
				try
				{
					return tryThis();
				} catch( NamingException e )
				{
					logger.info( "Caught NamingException. Trying to reconnect..." );
					e.printStackTrace();
					onFailure();
					numberOfRetries--;
				}
			return null;
		}

		protected abstract E tryThis() throws NamingException;
	}

	public static SearchControls	SCOPE_OBJECT;
	public static SearchControls	SCOPE_SUBTREE;

	static
	{
		SCOPE_SUBTREE = new SearchControls();
		SCOPE_SUBTREE.setSearchScope( SearchControls.SUBTREE_SCOPE );
		SCOPE_OBJECT = new SearchControls();
		SCOPE_OBJECT.setSearchScope( SearchControls.OBJECT_SCOPE );
	}
	private InitialDirContext		ctx;
	private String					providerAddress;
	private String					securityCredentials;

	private String					securityPrincipal;

	public LDAPClient()
	{
		super();
	}

	/**
	 * 
	 * @param dn
	 * @param data
	 * @param alteration
	 * @throws NamingException
	 */
	public void alterEntryAttributesByDN( final String dn, Map<String,Object> data, AlterationType alteration ) throws NamingException
	{
		logger.info( String.format( "Updating ldap entry %s with %d attributes", dn.toString(), data.size() ) );
		final ModificationItem[] modifications = new ModificationItem[data.size()];
		int i = 0;
		for( Entry<String,Object> dataEntry : data.entrySet() )
		{
			Attribute attr = new BasicAttribute( dataEntry.getKey() );
			if( dataEntry.getValue() instanceof List<?> )
			{
				logger.info( String.format( "Attribute %s is a list with %d values", dataEntry.getKey(), ( (List<?>) dataEntry.getValue() ).size() ) );
				for( Object o : (List<?>) dataEntry.getValue() )
					attr.add( o.toString() );
			} else
			{
				logger.info( String.format( "Attribute %s is a scalar with value %s", dataEntry.getKey(), dataEntry.getValue().toString() ) );
				attr.add( dataEntry.getValue().toString() );
			}
			ModificationItem modification = new ModificationItem( alteration.toInt(), attr );

			modifications[i] = modification;
			i++;
		}

		new ConnectionTester<Void>()
		{
			@Override
			protected Void tryThis() throws NamingException
			{
				getCtx().modifyAttributes( dn.toString(), modifications );
				return null;
			}
		}.run();

	}

	/**
	 * Build a Map representing a single ldap entry
	 * 
	 * @param sr
	 * @return
	 * @throws NamingException
	 */
	@SuppressWarnings("unchecked")
	private Map<String,Object> buildEntry( SearchResult sr ) throws NamingException
	{

		// logger.info( String.format( "Building entry for sr: %s", sr.getName() ) );
		// Map<String,Object> entry = new HashMap<String,Object>();
		Map<String,Object> entry = new TreeMap<String,Object>( String.CASE_INSENSITIVE_ORDER );
		NamingEnumeration<Attribute> attrs = (NamingEnumeration<Attribute>) sr.getAttributes().getAll();

		while( attrs.hasMore() )
		{
			Attribute attr = attrs.next();
			String name = attr.getID();
			Attributes attributes = attr.getAttributeDefinition().getAttributes( "" );
			Attribute syntax = attributes.get( "syntax" );
//			Attribute single = attributes.get( "single-value" );

//			if( syntax == null )
//				throw new NamingException();

			NamingEnumeration<?> values = attr.getAll();
			Set<Object> valueList = new HashSet<Object>();
			while( values.hasMore() )
			{
				Object obj = values.next();
				valueList.add( AttributeTypeParser.convertFromString( syntax, obj ) );
			}

			if( valueList.size() > 1 )
				entry.put( name, valueList );
			else //if( valueList.size() == 1 && single.get().toString().toLowerCase().equals( "true" ) )
				entry.put( name, valueList.toArray()[0] );

		}
		entry.put( "dn", sr.getNameInNamespace() );
		return entry;
	}

	public void connect() throws NamingException
	{
		if( getProviderAddress() == null || getSecurityCredentials() == null || getSecurityPrincipal() == null )
			throw new MissingParameterException( String.format( "ProviderAddress: %s, Password %s, LoginDN %s, ", getProviderAddress(), ( getSecurityCredentials() == null ? "missing" : "******" ),
					getSecurityPrincipal() ) );

		Hashtable<String,String> env = new Hashtable<String,String>();

		env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
		env.put( Context.PROVIDER_URL, getProviderAddress() );
		env.put( "com.sun.jndi.ldap.read.timeout", "30000" );
		env.put( "com.sun.jndi.ldap.connect.timeout", "5000" );
		env.put( Context.SECURITY_PRINCIPAL, getSecurityPrincipal() );
		env.put( Context.SECURITY_CREDENTIALS, getSecurityCredentials() );
		env.put( Context.SECURITY_AUTHENTICATION, "simple" );

		ctx = new InitialDirContext( env );
	}

	public void createEntryByDN( final Name dn, Map<String,Object> data ) throws NamingException
	{
		final Attributes attrs = new BasicAttributes( true ); // case-ignore
		for( Entry<String,Object> dataEntry : data.entrySet() )
		{
			Attribute attr = new BasicAttribute( dataEntry.getKey() );
			if( dataEntry.getValue() instanceof List<?> )
			{
				for( Object o : (List<?>) dataEntry.getValue() )
					attr.add( AttributeTypeParser.convertToString( o ) );
			} else if( dataEntry.getValue() instanceof Set<?> )
			{
				for( Object o : (Set<?>) dataEntry.getValue() )
					attr.add( AttributeTypeParser.convertToString( o ) );
			} else
			{
				attr.add( AttributeTypeParser.convertToString( dataEntry.getValue() ) );
			}

			attrs.put( attr );
		}
		// Perform bind
		new ConnectionTester<Void>()
		{
			@Override
			protected Void tryThis() throws NamingException
			{
				getCtx().createSubcontext( dn.toString(), attrs );
				return null;
			}
		}.run();

	}

	public void destroyEntryByDN( Name dn ) throws NamingException
	{
		destroyEntryByDN( dn.toString() );
	}

	public void destroyEntryByDN( final String dn ) throws NamingException
	{
		new ConnectionTester<Void>()
		{
			@Override
			protected Void tryThis() throws NamingException
			{
				getCtx().unbind( dn.toString() );
				return null;
			}
		}.run();

	}

	public void disconnect()
	{
		try
		{
			getCtx().close();
		} catch( NamingException e )
		{
			e.printStackTrace();
		}
	}

	private InitialDirContext getCtx()
	{
		return ctx;
	}

	public String getDNbyFilter( final String filter, final String baseDN ) throws NamingException, NoSuchEntryException
	{

		NamingEnumeration<SearchResult> answer = new ConnectionTester<NamingEnumeration<SearchResult>>()
		{
			@Override
			protected NamingEnumeration<SearchResult> tryThis() throws NamingException
			{
				return getCtx().search( baseDN, filter, LDAPClient.SCOPE_SUBTREE );
			}
		}.run();

		if( answer.hasMore() )
		{
			SearchResult sr = answer.next();
			return sr.getNameInNamespace();
		}

		logger.info( String.format( "Nothing found for '%s' under '%s'", filter, baseDN ) );
		throw new NoSuchEntryException( filter );
	}

	public List<Map<String,Object>> getEntriesByAttributes( final String baseDN, Map<String,Object> matchingAttributes, final List<String> attributesToReturn ) throws NamingException
	{
		final BasicAttributes mAttributes = new BasicAttributes();

		for( Entry<String,Object> entry : matchingAttributes.entrySet() )
		{
			logger.info( entry.getKey() + "================" + entry.getValue() );
		}

		for( Entry<String,Object> e : matchingAttributes.entrySet() )
			mAttributes.put( e.getKey(), e.getValue() );

		NamingEnumeration<SearchResult> answer = new ConnectionTester<NamingEnumeration<SearchResult>>()
		{

			@Override
			protected NamingEnumeration<SearchResult> tryThis() throws NamingException
			{
				return getCtx().search( baseDN, mAttributes, attributesToReturn.toArray( new String[attributesToReturn.size()] ) );
			}

		}.run();

		List<Map<String,Object>> entries = new ArrayList<Map<String,Object>>();
		while( answer.hasMore() )
			entries.add( buildEntry( answer.next() ) );

		return entries;
	}

	public List<Map<String,Object>> getEntriesbyFilter( final String filter, final String baseDN ) throws NamingException
	{
		List<Map<String,Object>> entries = new ArrayList<Map<String,Object>>();

		NamingEnumeration<SearchResult> answer = new ConnectionTester<NamingEnumeration<SearchResult>>()
		{
			@Override
			protected NamingEnumeration<SearchResult> tryThis() throws NamingException
			{
				return getCtx().search( baseDN, filter, LDAPClient.SCOPE_SUBTREE );
			}

		}.run();

		while( answer.hasMore() )
			entries.add( buildEntry( answer.next() ) );

		return entries;
	}

	public Map<String,Object> getEntryByDN( final String dn ) throws NamingException
	{
		NamingEnumeration<SearchResult> answer = new ConnectionTester<NamingEnumeration<SearchResult>>()
		{
			@Override
			protected NamingEnumeration<SearchResult> tryThis() throws NamingException
			{
				return getCtx().search( dn, "(objectClass=*)", LDAPClient.SCOPE_OBJECT );
			}

		}.run();

		if( answer.hasMore() )
		{
			return buildEntry( answer.next() );
		}
		throw new NamingException( dn );
	}

	public String getProviderAddress()
	{
		return providerAddress;
	}

	public String getSecurityCredentials()
	{
		return securityCredentials;
	}

	public String getSecurityPrincipal()
	{
		return securityPrincipal;
	}

	public void renameEntry( final Name oldDn, final Name newDN ) throws NamingException
	{
		new ConnectionTester<Void>()
		{

			@Override
			protected Void tryThis() throws NamingException
			{
				getCtx().rename( oldDn.toString(), newDN.toString() );
				return null;
			}
		}.run();
	}

	public void setProviderAddress( String providerAddress )
	{
		this.providerAddress = providerAddress;
	}

	public void setSecurityCredentials( String securityCredentials )
	{
		this.securityCredentials = securityCredentials;
	}

	public void setSecurityPrincipal( String securityPrincipal )
	{
		this.securityPrincipal = securityPrincipal;
	}

	public void updateEntryByDN( Name dn, Map<String,Object> data ) throws NamingException
	{
		updateEntryByDN( dn.toString(), data );
	}

	public void updateEntryByDN( final String dn, Map<String,Object> data ) throws NamingException
	{
		// logger.info( String.format( "Updating ldap entry %s with %d attributes", dn.toString(), data.size() )
		// );
		final ModificationItem[] modifications = new ModificationItem[data.size()];
		int i = 0;
		for( Entry<String,Object> dataEntry : data.entrySet() )
		{
			Attribute attr = new BasicAttribute( dataEntry.getKey() );
			if( dataEntry.getValue() instanceof List<?> )
			{
				// logger.info( String.format( "Attribute %s is a list with %d values", dataEntry.getKey(), (
				// (List<?>) dataEntry.getValue() ).size() ) );
				for( Object o : (List<?>) dataEntry.getValue() )
					attr.add( o.toString() );
			} else if( dataEntry.getValue() instanceof Set<?> )
			{
				for( Object o : (Set<?>) dataEntry.getValue() )
					attr.add( AttributeTypeParser.convertToString( o ) );
			} else
			{
				// logger.info( String.format( "Attribute %s is a scalar with value %s", dataEntry.getKey(),
				// dataEntry.getValue().toString() ) );
				attr.add( dataEntry.getValue().toString() );
			}
			ModificationItem modification = new ModificationItem( InitialDirContext.REPLACE_ATTRIBUTE, attr );

			modifications[i] = modification;
			i++;
		}
		new ConnectionTester<Void>()
		{
			@Override
			protected Void tryThis() throws NamingException
			{
				getCtx().modifyAttributes( dn.toString(), modifications );
				return null;
			}
		}.run();

	}
}