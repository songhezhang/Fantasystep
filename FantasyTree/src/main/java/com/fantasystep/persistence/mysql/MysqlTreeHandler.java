package com.fantasystep.persistence.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fantasystep.annotation.Storage;
import com.fantasystep.domain.Node;
import com.fantasystep.exception.ValidationFailedException;
import com.fantasystep.persistence.TreeHandler;
import com.fantasystep.persistence.exception.PersistenceException;
import com.fantasystep.persistence.mysql.config.MysqlConfig;
import com.fantasystep.utils.DateUtil;
import com.fantasystep.utils.NodeClassUtil;
import com.fantasystep.utils.NodeUtil;
import com.fantasystep.utils.StringUtil;

public class MysqlTreeHandler extends MysqlHandler implements TreeHandler
{

	private static Logger logger = LoggerFactory.getLogger(MysqlTreeHandler.class);
	private SimpleDateFormat sdf = DateUtil.STARDARD_TIMESTAMP_FORMAT;
	@SuppressWarnings("unchecked")
	private Node buildNode( Map<String,Object> nodeData )
	{
		Node node = null;
		try
		{
			Class<? extends Node> nodeType = null;
			String classFullName = nodeData.get( "type" ).toString().substring(6);
			try {
				nodeType = (Class<? extends Node>) getClass().getClassLoader().loadClass( classFullName );
			} catch (ClassNotFoundException e) {
				logger.info("Dynamic Class " + classFullName);
				nodeType = NodeClassUtil.getDynamicEntityClassByFullName(classFullName);
				if(nodeType == null) {
					logger.info("None class exists.");
					nodeType = Node.class;
				}
			}
			node = nodeType.newInstance();
			node.setId( UUID.fromString( nodeData.get( "id" ).toString() ) );
			node.setDeleted( Boolean.valueOf( nodeData.get( "deleted" ).toString() ) );
			node.setLabel(nodeData.get("label").toString());
			node.setCreatedDate(sdf.parse(nodeData.get("createdDate").toString()));
			node.setLastModifiedDate(sdf.parse(nodeData.get("lastModifiedDate").toString()));
			if( nodeData.get( "parentId" ).toString().equals( "null" ) )
				node.setParentId( UUID.randomUUID() );
			else
				node.setParentId( UUID.fromString( nodeData.get( "parentId" ).toString() ) );
			return node;

		} catch( InstantiationException e )
		{
			e.printStackTrace();
		} catch( IllegalAccessException e )
		{
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setup()
	{
		super.setup(Storage.TREE);
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
			if( !delete( id ) )
				return false;
		return true;
	}

	@Override
	public boolean delete( UUID id )
	{
		return setDeleted( id, true );
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
		List<Map<String,Object>> rawData = new ArrayList<Map<String,Object>>();

		Connection c = null;
		try
		{
			c = MysqlConfig.getInstance().createConnection();
			Statement s = c.createStatement();

			List<String> mysqlFields = new ArrayList<String>();
			mysqlFields.add( "deleted" );
			mysqlFields.add( "id" );
			mysqlFields.add( "parentId" );
			mysqlFields.add( "type" );
//			mysqlFields.add( "name" );
			mysqlFields.add( "label" );
			mysqlFields.add( "createdDate" );
			mysqlFields.add( "lastModifiedDate" );

//			Map<String,String> storageToStorageName = new HashMap<String,String>();
//
//			for( String field : mysqlFields )
//			{
//				String storageName = AnnotationsParser.getAttributes( Node.class, field ).getStorageName();
//				if( storageName != null )
//					storageToStorageName.put( field, storageName );
//			}

			String sqlQuery = "SELECT * from node";
			ResultSet rs = s.executeQuery( sqlQuery );
			while( rs.next() )
			{
				HashMap<String,Object> node = new HashMap<String,Object>();
				for( String field : mysqlFields )
				{
					String storageName = field;
//					if( storageToStorageName.containsKey( field ) )
//						storageName = storageToStorageName.get( field );

					node.put( field, rs.getObject( storageName ) );
				}
				rawData.add( node );
			}

		} catch( SQLException e )
		{
			e.printStackTrace();
		} finally
		{
			if( c != null )
			{
				try
				{
					c.close();
				} catch( SQLException e )
				{
					e.printStackTrace();
				}
			}
		}

		nodeList = buildNodeList( rawData );
		buildTree( NodeUtil.getNode( nodeId, nodeList ), nodeList );

		logger.info( String.format( "Tree loaded in %s ms", new Date().getTime() - start.getTime() ) );
		return nodeList;
	}

	private boolean setDeleted( UUID id, boolean deleted )
	{
		Map<String,Object> node = new HashMap<String,Object>();
		node.put( "deleted", deleted );
		try
		{
			update( Node.class, id, node );
			return true;
		} catch( PersistenceException e )
		{
			e.printStackTrace();
		}
		return false;
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
		return setDeleted( id, false );
	}

	@Override
	public boolean destroy( Class<? extends Node> nodeClass, List<UUID> ids ) throws PersistenceException
	{

		Connection c = null;
		try
		{
			c = MysqlConfig.getInstance().createConnection();

			Statement s = c.createStatement();
			StringBuffer sqlDelete = new StringBuffer( " DELETE FROM `node` WHERE id IN ('" );
			boolean isFirst = true;
			for( UUID uuid : ids )
			{
				if( isFirst )
					isFirst = false;
				else
					sqlDelete.append( "', '" );
				sqlDelete.append( uuid );
			}
			sqlDelete.append( "')" );

			if( s.execute( sqlDelete.toString() ) )
				return true;
			else
				return false;

		} catch( SQLException e )
		{
			e.printStackTrace();
			throw new PersistenceException( e.getCause() );
		} finally
		{
			if( c != null )
			{
				try
				{
					c.close();
				} catch( SQLException e )
				{
					e.printStackTrace();
					throw new PersistenceException( e.getCause() );
				}
			}
		}
	}

	@Override
	public boolean destroy( Class<? extends Node> nodeClass, UUID id ) throws PersistenceException
	{
		Connection c = null;
		try
		{
			c = MysqlConfig.getInstance().createConnection();

			Statement s = c.createStatement();
			StringBuffer sqlDelete = new StringBuffer( " DELETE FROM `node` WHERE id = '" );
			sqlDelete.append( id ).append( "'" );

			if( s.execute( sqlDelete.toString() ) )
				return true;
			else
				return false;

		} catch( SQLException e )
		{
			e.printStackTrace();
			throw new PersistenceException( e.getCause() );
		} finally
		{
			if( c != null )
			{
				try
				{
					c.close();
				} catch( SQLException e )
				{
					e.printStackTrace();
					throw new PersistenceException( e.getCause() );
				}
			}
		}
	}

	@Override
	public boolean insert( Class<? extends Node> nodeClass, Map<UUID,Map<String,Object>> nodeList ) throws PersistenceException
	{
		Connection c = null;
		try
		{
			c = MysqlConfig.getInstance().createConnection();

			Statement s = c.createStatement();
			String date = sdf.format(new Date());
			for( Entry<UUID,Map<String,Object>> record : nodeList.entrySet() )
			{
				Map<String,Object> node = record.getValue();
				String sqlInsert = String.format( " INSERT INTO `node` (`id`, `parentId`, `type`,`deleted`, `label`, `createddate`, `lastmodifieddate`) VALUES ('%s', '%s', '%s', %s, '%s', '%s', '%s') ", node.get( "id" ), node.get( "parentId" ),
						node.get( "type" ), node.get( "deleted" ), node.get("label"), date, date );
				s.addBatch( sqlInsert );
			}
			s.executeBatch();
			return true;

		} catch( SQLException e )
		{
			e.printStackTrace();
			throw new PersistenceException( e.getCause() );
		} finally
		{
			if( c != null )
			{
				try
				{
					c.close();
				} catch( SQLException e )
				{
					e.printStackTrace();
					throw new PersistenceException( e.getCause() );
				}
			}
		}
	}

	@Override
	public boolean insert( Class<? extends Node> nodeClass, UUID id, Map<String,Object> node ) throws PersistenceException
	{
		Connection c = null;
		try
		{
			c = MysqlConfig.getInstance().createConnection();

			Statement s = c.createStatement();
			String date = sdf.format(new Date());
			String sqlInsert = String.format( " INSERT INTO `node` (`id`, `parentId`, `type`, `deleted`, `label`, `createddate`, `lastmodifieddate`) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s') ", node.get( "id" ), node.get( "parentId" ), node.get( "type" ),
					node.get( "deleted" ), node.get("label"), date, date );

			if( s.execute( sqlInsert ) )
				return true;
			else
				return false;

		} catch( SQLException e )
		{
			e.printStackTrace();
			throw new PersistenceException( e.getCause() );
		} finally
		{
			if( c != null )
			{
				try
				{
					c.close();
				} catch( SQLException e )
				{
					e.printStackTrace();
					throw new PersistenceException( e.getCause() );
				}
			}
		}
	}

	@Override
	public Map<UUID,Map<String,Object>> read( Class<? extends Node> nodeClass, List<UUID> ids, Map<String,Object> nodeData ) throws PersistenceException
	{
		return null;
	}

	@Override
	public Map<String,Object> read( Class<? extends Node> nodeClass, UUID id, Map<String,Object> nodeData ) throws PersistenceException
	{
		return null;
	}

	@Override
	public boolean update( Class<? extends Node> nodeClass, UUID id, Map<String,Object> node ) throws PersistenceException
	{
		Connection c = null;
		try
		{
			c = MysqlConfig.getInstance().createConnection();

			Statement s = c.createStatement();
			StringBuilder sqlUpdate = new StringBuilder();
			sqlUpdate.append( "UPDATE `node` SET " );

			List<String> conditions = new ArrayList<String>();
			for( Entry<String,Object> e : node.entrySet() )
			{
				if( e.getKey().equalsIgnoreCase("createddate") )
					continue;
				else if( e.getKey().equalsIgnoreCase("lastmodifieddate") )
					conditions.add( String.format( "`%s`='%s'", e.getKey(), sdf.format(new Date() ) ) );
				else if( e.getValue() == null)
					conditions.add( String.format( "`%s`=''", e.getKey() ) );
				else if( isStringField( e.getValue().getClass(), e.getValue() ) )
					conditions.add( String.format( "`%s`='%s'", e.getKey(), e.getValue() ) );
				else
					conditions.add( String.format( "`%s`=%s", e.getKey(), e.getValue() ) );
			}
			sqlUpdate.append( StringUtil.join( conditions, "," ) );
			sqlUpdate.append( String.format( " WHERE id = '%s'", id ) );

			String query = sqlUpdate.toString();
			logger.info("Execute sql : " + query);
			if( s.execute( query ) )
				return true;
			else
				return false;
		} catch( SQLException e )
		{
			e.printStackTrace();
			throw new PersistenceException( e.getCause() );
		} finally
		{
			if( c != null )
			{
				try
				{
					c.close();
				} catch( SQLException e )
				{
					e.printStackTrace();
					throw new PersistenceException( e.getCause() );
				}
			}
		}
	}

	@Override
	public void terminate() {
		
	}
	public static void main(String[] args) {
		String a = null;
		logger.info("aaaa" + a);
		SimpleDateFormat sdf = DateUtil.STARDARD_TIMESTAMP_FORMAT;
		logger.info(sdf.format(new Date() ) );
	}
}
