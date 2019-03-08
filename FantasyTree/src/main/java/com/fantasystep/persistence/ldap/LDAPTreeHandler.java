package com.fantasystep.persistence.ldap;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fantasystep.domain.Node;
import com.fantasystep.exception.ValidationFailedException;
import com.fantasystep.persistence.TreeHandler;
import com.fantasystep.utils.NodeUtil;
import com.fantasystep.utils.StringUtil;
import com.fantasystep.utils.StringUtil.Stringifier;

public class LDAPTreeHandler extends LDAPStorageHandler implements TreeHandler
{

	private static Logger logger = LoggerFactory.getLogger(LDAPTreeHandler.class);

	public static void main( String[] args )
	{
		LDAPTreeHandler th = new LDAPTreeHandler();
		th.setup();

		UUID nodeId = UUID.fromString( "fd0069f6-1d9d-11e0-9c27-001d09dacfaa" );

		logger.info( "Loading tree..." );
		Date start = new Date();
		try
		{
			String baseDN = th.getDNbyUUID( nodeId );
			String filter = OBJECT_CLASS_FILTER;
			List<Map<String,Object>> rawData = th.getLDAPClient().getEntriesbyFilter( FilterUtil.getFilter( filter, "fantasystepNode" ), baseDN );
			List<String> ikeys = new ArrayList<String>();

			ikeys.add( "deleted" );
			ikeys.add( "fantasystepid" );
			ikeys.add( "fantasystepparentid" );
			ikeys.add( "fantasysteptype" );

			for( Map<String,Object> e : rawData )
			{

				StringBuilder sb = new StringBuilder();
				sb.append( "INSERT node (" );
				sb.append( StringUtil.join( ikeys, "," ) );
				sb.append( ") VALUES (" );

				List<Object> values = new ArrayList<Object>();
				for( String k : ikeys )
					values.add( e.get( k ) );

				sb.append( StringUtil.join( values, ",", new Stringifier<Object>()
				{
					@Override
					public String toString( Object subject )
					{
						if( subject instanceof Boolean )
							return subject.toString();
						else
							return String.format( "\"%s\"", subject.toString() );
					}
				}));
				sb.append( ");" );

				logger.info( sb.toString() );

			}

		} catch( NamingException e )
		{
			e.printStackTrace();
		}
		logger.info( String.format( "Tree loaded in %s ms", new Date().getTime() - start.getTime() ) );

	}

	@SuppressWarnings("unchecked")
	private Node buildNode( Map<String,Object> nodeData )
	{
		Node node;
		try
		{
			Class<? extends Node> nodeType = (Class<? extends Node>) getClass().getClassLoader().loadClass( nodeData.get( "fantasystepType" ).toString() );

			node = nodeType.newInstance();
			node.setId( UUID.fromString( nodeData.get( "fantasystepId" ).toString() ) );
			if( nodeData.get( "fantasystepParentId" ).toString().equals( "null" ) )
				node.setParentId( UUID.randomUUID() );
			else
				node.setParentId( UUID.fromString( nodeData.get( "fantasystepParentId" ).toString() ) );
			return node;

		} catch( ClassNotFoundException e )
		{
			e.printStackTrace();
		} catch( InstantiationException e )
		{
			e.printStackTrace();
		} catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}
		return null;
	}

	private List<Node> buildNodeList( List<Map<String,Object>> nodeDataList )
	{
		List<Node> nodeList = new ArrayList<Node>();
		Node node;
		for( Map<String,Object> nodeData : nodeDataList )
		{
			node = buildNode( nodeData );
			if( node != null )
				nodeList.add( node );
		}
		return nodeList;

	}

	private Node buildTree( Node rootNode, List<Node> nodeList )
	{

		for( Node child : nodeList )
		{
			if( child.getParentId().equals( rootNode.getId() ) )
			{
				try
				{
					rootNode.addChild( child );
				} catch( ValidationFailedException e )
				{
					e.printStackTrace();
				}
				buildTree( child, nodeList );
			}
		}

		return rootNode;

	}

	@Override
	public boolean delete( List<UUID> ids )
	{
		for( UUID id : ids )
		{
			if( !delete( id ) )
				return false;
		}
		return true;
	}

	@Override
	public boolean delete( UUID id )
	{
		Map<String,Object> attributes = new HashMap<String,Object>();
		attributes.put( "deleted", true );
		try
		{
			getLDAPClient().updateEntryByDN( getDNbyUUID( id ), attributes );
			return true;
		} catch( NamingException e )
		{
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public List<? extends Node> getTree( Node rootNode )
	{
		return getTree( rootNode.getId() );
	}

	@Override
	public List<? extends Node> getTree( UUID nodeId )
	{
		logger.info( "Loading tree..." );
		Date start = new Date();
		List<Node> nodeList = null;
		try
		{
			String baseDN = getDNbyUUID( nodeId );
			String filter = OBJECT_CLASS_FILTER;
			List<Map<String,Object>> rawData = getLDAPClient().getEntriesbyFilter( FilterUtil.getFilter( filter, "fantasystepNode" ), baseDN );
			nodeList = buildNodeList( rawData );
			buildTree( NodeUtil.getNode( nodeId, nodeList ), nodeList );

		} catch( NamingException e )
		{
			e.printStackTrace();
		}
		logger.info( String.format( "Tree loaded in %s ms", new Date().getTime() - start.getTime() ) );
		return nodeList;
	}

	@Override
	public boolean unDelete( List<UUID> ids )
	{
		for( UUID id : ids )
		{
			if( !unDelete( id ) )
				return false;
		}
		return true;
	}

	@Override
	public boolean unDelete( UUID id )
	{
		Map<String,Object> attributes = new HashMap<String,Object>();
		attributes.put( "deleted", false );
		try
		{
			getLDAPClient().updateEntryByDN( getDNbyUUID( id ), attributes );
			return true;
		} catch( NamingException e )
		{
			e.printStackTrace();
		}

		return false;
	}

}
