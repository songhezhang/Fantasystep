package com.fantasystep.persistence;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CyclicBarrier;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fantasystep.annotation.AnnotationsParser;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.annotation.Storage;
import com.fantasystep.domain.DynamicDomain;
import com.fantasystep.domain.Entity;
import com.fantasystep.domain.MongoDynamicDomain;
import com.fantasystep.domain.Node;
import com.fantasystep.domain.Permission;
import com.fantasystep.domain.Resource;
import com.fantasystep.domain.SubNode;
import com.fantasystep.domain.User;
import com.fantasystep.exception.ValidationFailedException;
import com.fantasystep.helper.MemberHolder;
import com.fantasystep.helper.ParentListener;
import com.fantasystep.persistence.exception.IdNotFoundException;
import com.fantasystep.persistence.exception.InvalidCredentialsException;
import com.fantasystep.persistence.exception.PersistenceException;
import com.fantasystep.persistence.exception.RequiredFieldMissingException;
import com.fantasystep.persistence.exception.UniqueViolateException;
import com.fantasystep.persistence.ldap.LDAPStorageHandler;
import com.fantasystep.persistence.manager.DomainFieldManager;
import com.fantasystep.persistence.mongo.MongoStorageHandler;
import com.fantasystep.persistence.mysql.MysqlStorageHandler;
import com.fantasystep.persistence.mysql.MysqlTreeHandler;
import com.fantasystep.utils.EncryptionUtil;
import com.fantasystep.utils.JCompiler;
import com.fantasystep.utils.JSON2NodeUtil;
import com.fantasystep.utils.NodeClassUtil;
import com.fantasystep.utils.NodeUtil;
import com.fantasystep.utils.Option;
import com.fantasystep.utils.Option.BooleanOption;
import com.fantasystep.utils.Option.InvalidOptionFormatException;
import com.fantasystep.utils.Option.StringOption;

@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Singleton
@Startup
public class PersistenceProxy
{
	private static Logger logger = LoggerFactory.getLogger(PersistenceProxy.class);
	
	private class StorageThread extends Thread
	{
		private CyclicBarrier							barrier;
		private Map<Class<? extends Node>,List<UUID>>	items;
		private Map<UUID,Map<String,Object>>			resultPerThread;
		private Storage									s;

		StorageThread( CyclicBarrier barrier, Storage s, Map<Class<? extends Node>,List<UUID>> items, Map<UUID,Map<String,Object>> resultPerThread )
		{
			this.barrier = barrier;
			this.s = s;
			this.items = items;
			this.resultPerThread = resultPerThread;
		}

		@Override
		public void run()
		{
			Date start = new Date();
			logger.info( String.format( "Starting reading data from %s", s ) );

			for( Entry<Class<? extends Node>,List<UUID>> item : items.entrySet() )
			{
				Class<? extends Node> clazz = item.getKey();

				Set<Storage> storages = getStoragesByNodeClass( clazz );
				if( !storages.contains( s ) )
					continue;
				try
				{

					StorageHandler sh = getStorageHandlerList().get( s );
					Map<UUID,Map<String,Object>> data = sh.read( item.getKey(), item.getValue(), null );
					if( data != null )
						resultPerThread.putAll( data );

				} catch( PersistenceException e )
				{
					logger.info( e.getMessage() );
					e.printStackTrace();
				}
			}
			logger.info( String.format( "%s finished in %s ms", s, new Date().getTime() - start.getTime() ) );
			try
			{
				barrier.await();
			} catch( InterruptedException ex )
			{
				ex.printStackTrace();
			} catch( BrokenBarrierException ex )
			{
				ex.printStackTrace();
			}
		}
	}

	private static boolean	cacheMode		= false;
	private static boolean	openHouseMode	= false;
	private static UUID		rootId;

	static
	{
		try
		{
			Option.setConfigFileName( "/etc/fantasystep/treemanager.conf" );

			BooleanOption CACHEMODE = new BooleanOption( "cache.mode", false, true, "Should TreeManager run in cache mode?" );
			BooleanOption OPENHOUSEMODE = new BooleanOption( "openhouse.mode", false, true, "Should TreeManager run in open house mode?" );
			StringOption TREEMANAGER_ROOT_ID = new StringOption( "treemanager.root_id", "e3e8ae71-46da-46ef-ad76-c9dc6d1b7853", true, "ID of the root" );

			Option.load();

			setCacheMode( CACHEMODE.value() );
			setRootId( UUID.fromString( TREEMANAGER_ROOT_ID.value() ) );
			setOpenHouseMode( OPENHOUSEMODE.value() );
		} catch( InvalidOptionFormatException e )
		{
			e.printStackTrace();
		}

	}

	public static boolean isOpenHouseMode()
	{
		return openHouseMode;
	}

	private static void setCacheMode( boolean cacheMode )
	{
		PersistenceProxy.cacheMode = cacheMode;
	}

	private static void setOpenHouseMode( boolean openHouseMode )
	{
		PersistenceProxy.openHouseMode = openHouseMode;
	}

	private static void setRootId( UUID rootId )
	{
		PersistenceProxy.rootId = rootId;
	}

	private ConcurrentMap<UUID,Node>	cache;

	private boolean						hasInitialized	= false;

	private Map<Storage,StorageHandler>	storageHandlerList;

	private TreeHandler					treeHandler;

	@Lock(LockType.WRITE)
	public boolean activate( UUID id ) throws PersistenceException
	{
		if( id == null || !getCache().containsKey( id ) )
			return false;

		Node node = getCache().get( id );

		( (User) node ).setActivated( true );

		Map<String,Object> map = new HashMap<String,Object>();
		map.put( "activated", "TRUE" );

		getStorageHandlerList().get( Storage.MYSQL ).update( node.getClass(), node.getId(), map );

		return true;
	}

	@Lock(LockType.READ)
	public void authenticate( String identity, String password ) throws InvalidCredentialsException
	{
		User user = getUserByIdentity( identity );
		if( user == null )
		{
			logger.info( String.format( "Could not find any user identified by %s", identity ) );
			throw new InvalidCredentialsException();
		}

		if( !user.getActivated() )
		{
			logger.info( String.format( "User: %s is not activated", user.getLabel() ) );
			throw new InvalidCredentialsException();
		}

		if( user.isDeleted() )
		{
			logger.info( String.format( "User: %s is deleted", user.getLabel() ) );
			throw new InvalidCredentialsException();
		}

		if( !EncryptionUtil.testPass( password, user.getPassword() ) )
		{
			logger.info( "Invalid password" );
			throw new InvalidCredentialsException();
		}
	}

	private boolean compare( Object o1, Object o2 )
	{
		if( o1 instanceof Class && o2 instanceof String )
			return ( (Class<?>) o1 ).getName().equals( o2 );

		if( o2 instanceof String && !( o1 instanceof String ) )
			return o2.equals( o1.toString() );

		return o1.equals( o2 );
	}

	@Lock(LockType.WRITE)
	public void deleteTree( Node rootNode, UUID transactionId, User executor )
	{
		if( isCacheMode() )
		{
			onPreDelete( rootNode, transactionId, executor );
			getCache().get( rootNode.getId() ).setDeleted( true );
			onPostDelete( rootNode, transactionId, executor );
			return;
		}

		onPreDelete( rootNode, transactionId, executor );

		List<Node> nodeList = getDecendants( rootNode );
		nodeList.add( rootNode );

		List<UUID> ids = new ArrayList<UUID>();
		for( Node node : nodeList )
		{
			node.setDeleted( true );
			ids.add( node.getId() );
		}
		getTreeHandler().delete( ids );
		getCache().get( rootNode.getId() ).setDeleted( true );
		onPostDelete( rootNode, transactionId, executor );
	}

	@Lock(LockType.WRITE)
	public void destroyTree( Node destroyNode, UUID transactionId, User executor ) throws PersistenceException
	{
		if( isCacheMode() )
		{
			onPreDestroy( destroyNode, transactionId, executor );
			for( Node child : destroyNode.getChildren() )
				destroyTree( child, transactionId, executor );

			for( MemberHolder holder : NodeUtil.getMemberships( destroyNode, getCache().get( getRootId() ) ) )
				holder.removeMember( destroyNode );

			getCache().get( destroyNode.getParentId() ).removeChild( getCache().get( destroyNode.getId() ) );
			getCache().remove( destroyNode.getId() );
			onPostDestroy( destroyNode, transactionId, executor );
			return;
		}
		if(destroyNode instanceof DynamicDomain || ( destroyNode.getClass().equals(Node.class) && destroyNode.getSerializationNode() != null))
			destroyNode = JSON2NodeUtil.json2Node(destroyNode.getSerializationNode(), NodeClassUtil.getDynamicEntityClassByFullName(new JSONObject(destroyNode.getSerializationNode()).get("type").toString()));

		onPreDestroy( destroyNode, transactionId, executor );

		for( Node child : destroyNode.getChildren() )
			destroyTree( child, transactionId, executor );

		if( destroyNode instanceof SubNode )
		{
			StorageHandler sh = getStorageHandlerList().get( DomainFieldManager.getInstance().getStorageForSubNode() );
			sh.destroy( destroyNode.getClass(), destroyNode.getId() );
		} if( destroyNode instanceof MongoDynamicDomain )
		{
			StorageHandler sh = getStorageHandlerList().get( Storage.MONGO );
			sh.destroy( destroyNode.getClass(), destroyNode.getId() );
		} else
		{
			List<Node> nodeList = getDecendants( destroyNode );
			nodeList.add( destroyNode );
			/**
			 * Recursive handle SubNode Collection along the node tree. First destroy all the SubNode nodes.
			 */
			for( Node node : nodeList )
			{
				List<UUID> list = new ArrayList<UUID>();
				StorageHandler sh = getStorageHandlerList().get( DomainFieldManager.getInstance().getStorageForSubNode() );
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("parentId", node.getId());
				list.addAll(sh.read(node.getClass(), new ArrayList<UUID>(), map).keySet());
				sh.destroy( destroyNode.getClass(), list );
			}
			/**
			 * Second destroy all the nodes properties in all storages along the node tree.
			 */
			Map<Storage,Map<Class<? extends Node>,Map<UUID,Map<String,Object>>>> solutions = splitDataGroupForInsert( nodeList );
			Map<Storage,Map<Class<? extends Node>,Map<UUID,Map<String,Object>>>> backup = new HashMap<Storage,Map<Class<? extends Node>,Map<UUID,Map<String,Object>>>>();
			PersistenceException e = null;

			done: for( Entry<Storage,Map<Class<? extends Node>,Map<UUID,Map<String,Object>>>> solution : solutions.entrySet() )
			{
				Storage s = solution.getKey();
				Map<Class<? extends Node>,Map<UUID,Map<String,Object>>> items = solution.getValue();
				backup.put( s, new HashMap<Class<? extends Node>,Map<UUID,Map<String,Object>>>() );

				for( Entry<Class<? extends Node>,Map<UUID,Map<String,Object>>> item : items.entrySet() )
				{
					if( !getStoragesByNodeClass( item.getKey() ).contains( s ) )
						continue;

					StorageHandler sh = getStorageHandlerList().get( s );

					try
					{
						sh.destroy( item.getKey(), new ArrayList<UUID>( item.getValue().keySet() ) );
					} catch( PersistenceException pe )
					{
						e = pe;
						break done;
					}
					backup.get( s ).put( item.getKey(), item.getValue() );
				}
			}
			if( e != null )
			{
				logger.info( "DESTROY CASE: Rollback start..." );
				for( Entry<Storage,Map<Class<? extends Node>,Map<UUID,Map<String,Object>>>> entry : backup.entrySet() )
					for( Entry<Class<? extends Node>,Map<UUID,Map<String,Object>>> entry2 : entry.getValue().entrySet() )
						try
						{
							getStorageHandlerList().get( entry.getKey() ).insert( entry2.getKey(), entry2.getValue() );
							logger.info( String.format( "             Rollback insert %s %s %s", entry.getKey(), entry2.getKey(), entry2.getValue() ) );
						} catch( PersistenceException pe )
						{
							logger.info( String.format( "DESTROY CASE: Rollback failed. For %s, %s, %s", entry.getKey(), entry2.getKey(), entry2.getValue() ) );
						}
				logger.info( "DESTROY CASE: Rollback finished." );
				throw e;
			}

			/**
			 * Remove relationship.
			 */
			for( MemberHolder holder : NodeUtil.getMemberships( destroyNode, getCache().get( getRootId() ) ) )
			{
				holder.removeMember( destroyNode );
				try
				{
					modifyTreeNode( (Node) holder, transactionId, executor );
				} catch( RequiredFieldMissingException e1 )
				{
					e1.printStackTrace();
				} catch( ValidationFailedException e1 )
				{
					e1.printStackTrace();
				} catch (UniqueViolateException e1) {
					e1.printStackTrace();
				}
			}

			getCache().get( destroyNode.getParentId() ).removeChild( getCache().get( destroyNode.getId() ) );
			getCache().remove( destroyNode.getId() );
		}
		onPostDestroy( destroyNode, transactionId, executor );
	}

	private void divideSubNodeList( List<? extends Node> nodeList, List<Node> subNodeList, List<Node> mongoNodeList, List<Node> commonNodeList )
	{
		for( Node node : nodeList )
			if( node instanceof SubNode )
				subNodeList.add( node );
			else if( node instanceof MongoDynamicDomain )
				mongoNodeList.add( node );
			else
				commonNodeList.add( node );
	}

	private void encryptValues( Node node )
	{
		try
		{
			for( String fieldName : NodeClassUtil.getAllNodeFieldNames( node.getClass() ) )
				if( AnnotationsParser.getAttributes( node.getClass(), fieldName ).getEncrypted() )
				{

					Field field = NodeClassUtil.getField( node.getClass(), fieldName );
					field.setAccessible( true );
					String encrypted = (String) field.get( node );
					if( encrypted.startsWith( EncryptionUtil.MD5 ) )
						return;
					field.set( node, EncryptionUtil.aesEncrypt( encrypted ) );
				}
		} catch( IllegalArgumentException e )
		{
			e.printStackTrace();
		} catch( SecurityException e )
		{
			e.printStackTrace();
		} catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}
	}

	@Lock(LockType.READ)
	public List<Resource> getAssignedResources( Node node ) throws PersistenceException
	{
		return getAssignedResourcesByClass( node, Resource.class );
	}

	@Lock(LockType.READ)
	public List<Resource> getAssignedResourcesByClass( Node node, Class<? extends Resource> clazz ) throws PersistenceException
	{
		if( node.getId() == null )
			throw new PersistenceException( "Provided node is missing id" );
		if( node.getParentId() == null )
			throw new PersistenceException( "Provided node is missing parentId" );

		Set<Resource> resources = new HashSet<Resource>();

		for( Resource res : getResourcesByClass( clazz ) )
			if( res.getMembers() != null && res.getMembers().contains( node.getId() ) )
				resources.add( res );

		Node parent = getCache().get( node.getParentId() );
		if( parent != null )
			resources.addAll( getAssignedResourcesByClass( parent, clazz ) );

		return new ArrayList<Resource>( resources );
	}

	private ConcurrentMap<UUID,Node> getCache()
	{
		return cache;
	}

	private List<Node> getDecendants( Node parent )
	{
		List<Node> decendants = new ArrayList<Node>();
		for( Node c : getCache().get( parent.getId() ).getChildren() )
		{
			decendants.add( c );
			decendants.addAll( getDecendants( c ) );
		}
		return decendants;
	}

	@Lock(LockType.READ)
	public Node getFullNodeByID( UUID uuid ) throws PersistenceException
	{
		logger.info("getFullNodeId %s", uuid.toString());
		Node nodeInCache = getCache().get( uuid );
		if( uuid == null || nodeInCache == null )
			throw new IdNotFoundException( String.format( "Could not find node with id: %s", uuid ) );

		Map<String,Object> rawData = new HashMap<String,Object>();
		for( Storage s : getStoragesByNodeClass( nodeInCache.getClass() ) )
		{

			StorageHandler sh = getStorageHandlerList().get( s );
			try
			{
				Map<String,Object> data = sh.read( nodeInCache.getClass(), uuid, null );
				if( data != null )
					rawData.putAll( data );
			} catch( PersistenceException e )
			{
				throw new PersistenceException( String.format( "%s : %s", sh.getClass().getSimpleName(), e.getMessage() ) );
			}
		}

		Node node = null;
		try
		{
			node = nodeInCache.getClass().newInstance();
			DomainFieldManager.getInstance().convertFromMapToDomain( node, rawData );
		} catch( InstantiationException e )
		{
			e.printStackTrace();
		} catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}
		
		List<Node> children = new ArrayList<Node>(nodeInCache.getChildren());
		for(Class<? extends Node> c : NodeClassUtil.getValidDynamicEntityChildren(node.getClass())) {
			Map<UUID, Map<String, Object>> data = null;
			if(MongoDynamicDomain.class.isAssignableFrom(c))
				data = getStorageHandlerList().get(Storage.MONGO).read( c, new ArrayList<UUID>(), null);
			else if(SubNode.class.isAssignableFrom(c))
				data = getStorageHandlerList().get(Storage.MYSQL).read( c, new ArrayList<UUID>(), null);
			for(Map<String, Object> map : data.values()) {
				Node n = null;
				try
				{
					n = c.newInstance();
					DomainFieldManager.getInstance().convertFromMapToDomain( n, map );
				} catch( InstantiationException e )
				{
					e.printStackTrace();
				} catch( IllegalAccessException e )
				{
					e.printStackTrace();
				}
				if(n != null)
					children.add(NodeClassUtil.getSerializationNode(n));;
			}
		}

		node.setChildren( children );
//		node.setDeleted( nodeInCache.isDeleted() );
//		node.setId( uuid );
//		node.setParentId( nodeInCache.getParentId() );

		return node;
	}

	private List<? extends Node> getFullTreeWithNodes( List<? extends Node> nodeList )
	{
		
		/** Initialize the raw data */
		Map<Storage,Map<UUID,Map<String,Object>>> rawData = new HashMap<Storage,Map<UUID,Map<String,Object>>>();

		for( Storage s : getStorageHandlerList().keySet() )
			rawData.put( s, new HashMap<UUID,Map<String,Object>>() );

		/** Split the data categorized by storage and node class */
		Map<Storage,Map<Class<? extends Node>,List<UUID>>> solutions = splitDataGroupByIDs( nodeList );

		/** Handle each solution */
		// Start Barrier

		CyclicBarrier barrier = new CyclicBarrier( getStorageHandlerList().size(), new Runnable()
		{
			@Override
			public void run()
			{
				hasInitialized = true;
			}
		} );

		if( solutions.isEmpty() )
			hasInitialized = true;
		else
			for( Entry<Storage,Map<Class<? extends Node>,List<UUID>>> solution : solutions.entrySet() )
			{
				Storage s = solution.getKey();
				logger.info( String.format( "Starting thread for %s", s ) );
				Map<Class<? extends Node>,List<UUID>> items = solution.getValue();
				new StorageThread( barrier, s, items, rawData.get( s ) ).start();
			}

		logger.info( "Waiting for all threads ..." );

		while( !hasInitialized )
		{
			try
			{
				Thread.sleep( 1000 );
			} catch( InterruptedException e )
			{
				e.printStackTrace();
			}
		}
		
		/** Assemble together */
		return joinData( rawData, indexList( nodeList ) );
	}
	
	@SuppressWarnings("unchecked")
	private void loadDynamicEntities() {
		logger.info( "Load Dynamic Classes ..." );
		try {
			Map<String, String> allSourceCodes = new HashMap<String, String>();
			List<String> noneAbstractDynamicClassNames = new ArrayList<String>();
			Map<String, Class<? extends Node>> dynamicClasses = new HashMap<String, Class<? extends Node>>();
			for (Map<String, Object> map : getStorageHandlerList().get(
					AnnotationsParser.getAttributes(Entity.class, "sourceCode")
							.getStorage()).read(Entity.class,
					new ArrayList<UUID>(), null).values()) {
				String fullName = map.get("fullName").toString();
				allSourceCodes.put(fullName, map.get("sourceCode").toString());
				if(!map.get("isAbstract").toString().equals("true"))
					noneAbstractDynamicClassNames.add(fullName);
			}
			for(String fullName : noneAbstractDynamicClassNames)
				dynamicClasses.put(fullName, (Class<? extends Node>)JCompiler.getInstance().registerClass(fullName, allSourceCodes));
			
			NodeClassUtil.setupDynamicEntityClass(dynamicClasses);
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
		getStorageHandlerList().get(Storage.MYSQL).setup();
	}
	
	private boolean testEntity(String newClassName, String newClassCode) {
		Map<String, String> allSourceCodes = new HashMap<String, String>();
		try {
			for (Map<String, Object> map : getStorageHandlerList().get(
					AnnotationsParser.getAttributes(Entity.class, "sourceCode")
							.getStorage()).read(Entity.class,
					new ArrayList<UUID>(), null).values()) {
				String fullName = map.get("fullName").toString();
				allSourceCodes.put(fullName, map.get("sourceCode").toString());
			}
			allSourceCodes.put(newClassName, newClassCode);
			return JCompiler.getInstance().registerClass(newClassName, allSourceCodes) != null;
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Lock(LockType.READ)
	public Node getNodeByID( UUID uuid ) throws PersistenceException
	{
		if( uuid == null )
			throw new IdNotFoundException( String.format( "Could not find node with id: %s", uuid ) );
		Node node = getCache().get( uuid );

		if( node == null )
			throw new IdNotFoundException( String.format( "Could not find node with id: %s", uuid ) );
		return node;
	}

	@Lock(LockType.READ)
	public Node getNodeByNode( Node rootNode ) throws PersistenceException
	{
		return getNodeByID( rootNode.getId() );
	}

	public ConcurrentMap<UUID,Node> getNodeCache()
	{
		return getCache();
	}

	@Lock(LockType.READ)
	public List<? extends Node> getNodesByAttribute( String attributeName, Object value )
	{
		List<Node> nodes = new ArrayList<Node>();
		for( Node n : getCache().values() )
		{
			Object attribute = NodeUtil.getAttribute( n, attributeName );

			if( attribute != null && compare( attribute, value ) )
				nodes.add( n );
		}
		return nodes;
	}

	@SuppressWarnings("unchecked")
	@Lock(LockType.READ)
	public <E> List<E> getNodesByClass( Class<E> clazz )
	{
		List<E> nodes = new ArrayList<E>();
		for( Node n : getCache().values() )
			if( clazz.isAssignableFrom( n.getClass() ) )
				nodes.add( (E) n );
		return nodes;
	}

	private List<PersistenceInterceptor> getPersistenceInterceptors( Node node )
	{
		List<PersistenceInterceptor> interceptors = new ArrayList<PersistenceInterceptor>();
		for( Field f : node.getClass().getDeclaredFields() )
		{
			if( PersistenceInterceptor.class.isAssignableFrom( f.getType() ) )
			{
				f.setAccessible( true );
				try
				{
					interceptors.add( (PersistenceInterceptor) f.get( node ) );
				} catch( IllegalArgumentException e )
				{
					e.printStackTrace();
				} catch( IllegalAccessException e )
				{
					e.printStackTrace();
				}
			}
		}

		return interceptors;
	}

	@Lock(LockType.READ)
	public List<Resource> getResources()
	{
		return getResourcesByClass( Resource.class );
	}

	@Lock(LockType.READ)
	public List<Resource> getResourcesByClass( Class<? extends Resource> clazz )
	{
		List<Resource> resources = new ArrayList<Resource>();
		for( Node n : getCache().values() )
			if( n instanceof Resource && clazz.isAssignableFrom( n.getClass() ) )
				resources.add( (Resource) n );
		return resources;
	}

	public UUID getRootId()
	{
		return rootId;
	}

	private Map<Storage,StorageHandler> getStorageHandlerList()
	{
		return storageHandlerList;
	}
	
	public StorageHandler getDynamicStorageHandler() {
		return storageHandlerList.get(Storage.MONGO);
	}

	private Set<Storage> getStoragesByNodeClass( Class<? extends Node> clazz )
	{
		Set<Storage> storages = new HashSet<Storage>();
		for( Field field : NodeClassUtil.getAllNodeFields( clazz ) )
		{
			FieldAttributeAccessor attributes = AnnotationsParser.getAttributes( clazz, field.getName() );
			if( attributes != null )
				storages.addAll( Arrays.asList( attributes.getStorage() ) );
		}
		storages.remove(null);
		return storages;
	}

	private TreeHandler getTreeHandler()
	{
		return treeHandler;
	}

	@Lock(LockType.READ)
	public User getUser( UUID userId )
	{
		User user = (User) getCache().get( userId );
		return user;
	}

	@Lock(LockType.READ)
	public User getUserByIdentity( String identity )
	{
		User user = null;
		identity = identity.toLowerCase();
		for( Node node : getCache().values() )
			if( node instanceof User )
			{
				User cachedUser = (User) node;
				if( identity.contains( "@" ) && cachedUser.getEmail() != null )
				{
					if( identity.equals( cachedUser.getEmail().toLowerCase() ) )
					{
						user = cachedUser;
						break;
					}
				} else
				{
					if( cachedUser.getUsername() != null && identity.equals( cachedUser.getUsername().toLowerCase() ) )
					{
						user = cachedUser;
						break;
					}

				}
			}

		return user;
	}

	@Lock(LockType.READ)
	public List<Resource> getUserResources( UUID userId ) throws PersistenceException
	{
		User user = getUser( userId );
		Set<Resource> userResources = new HashSet<Resource>();
		List<Node> childResources = user.getChildren( Resource.class );
		for( Node n : childResources )
			userResources.add( (Resource) n );
		userResources.addAll( getAssignedResources( user ) );

		return new ArrayList<Resource>( userResources );
	}

	@Lock(LockType.READ)
	public List<Resource> getUserResourcesByClass( UUID userId, Class<? extends Resource> clazz ) throws PersistenceException
	{
		User user = getUser( userId );
		Set<Resource> userResources = new HashSet<Resource>();
		List<Node> childResources = user.getChildren( clazz );
		for( Node n : childResources )
			userResources.add( (Resource) n );
		userResources.addAll( getAssignedResourcesByClass( user, clazz ) );

		return new ArrayList<Resource>( userResources );
	}

	@Lock(LockType.READ)
	public Node getUserTree( UUID userId ) throws PersistenceException
	{
		return getNodeByID( getUser( userId ).getAdminNode() );
	}

	private Map<UUID,Node> indexList( List<? extends Node> nodeList )
	{
		Map<UUID,Node> result = new HashMap<UUID,Node>();
		for( Node node : nodeList )
			result.put( node.getId(), node );
		return result;
	}

	@Lock(LockType.WRITE)
	@PostConstruct
	public void init()
	{
		long start = System.currentTimeMillis();
		logger.info( "INITIALIZING: " + hashCode() );
//		Environment.addProperty( VolatileFile.STORAGE_LOCATION_PROPERTY, "/var/lib/fantasystep/treemanager" );

		setStorageHandlerList( new HashMap<Storage,StorageHandler>() );
		getStorageHandlerList().put( Storage.MYSQL, new MysqlStorageHandler() );
		getStorageHandlerList().put( Storage.MONGO, new MongoStorageHandler() );
		getStorageHandlerList().put( Storage.LDAP, new LDAPStorageHandler() );
		getStorageHandlerList().put( Storage.TREE, new MysqlTreeHandler() );

		setTreeHandler( new MysqlTreeHandler() );
		setCache( new ConcurrentHashMap<UUID,Node>() );

		getTreeHandler().setup();
		logger.info( "TreeHandler initialized" );

		for( Entry<Storage, StorageHandler> handler : getStorageHandlerList().entrySet() )
		{
			if (handler.getKey() != Storage.TREE) {
				logger.info( String.format( "Setting up StorageHandler: %s", handler.getValue().getClass().getCanonicalName() ) );
				handler.getValue().setup();
			}
		}

		logger.info( "StorageHandlers initialized" );

		loadDynamicEntities();
		
		List<? extends Node> temp = getTreeHandler().getTree( getRootId() );
		for( Node node : getFullTreeWithNodes( temp ) )
		{
			onInitialize( node );
			getCache().put( node.getId(), node );
		}

		logger.info( "Spent: " + ( System.currentTimeMillis() - start ) / 1000f );
	}

	@Lock(LockType.WRITE)
	public void insertTree( List<? extends Node> nodeList, UUID transactionId, User executor ) throws UniqueViolateException, RequiredFieldMissingException, PersistenceException,
			ValidationFailedException
	{
		if( isCacheMode() )
		{
			for( Node node : nodeList )
				onPreInsert( node, transactionId, executor );
			for( Node node : nodeList )
			{
				getCache().get( node.getParentId() ).addChild( node );
				getCache().put( node.getId(), node );
				onPostInsert( node, transactionId, executor );
			}
			return;
		}

		for(Node node : nodeList) {
			if(node instanceof DynamicDomain || ( node.getClass().equals(Node.class) && node.getSerializationNode() != null))
				node = JSON2NodeUtil.json2Node(node.getSerializationNode(), NodeClassUtil.getDynamicEntityClassByFullName(new JSONObject(node.getSerializationNode()).get("type").toString()));
		}
		List<Node> subNodeList = new ArrayList<Node>();
		List<Node> mongoNodeList = new ArrayList<Node>();
		List<Node> commonNodeList = new ArrayList<Node>();
		divideSubNodeList( nodeList, subNodeList, mongoNodeList, commonNodeList );

		/**
		 * Here we insert batch of SubNode, they are only stored in MySQL.
		 */
		if( subNodeList.size() > 0 )
		{
			Storage storage = DomainFieldManager.getInstance().getStorageForSubNode();
			StorageHandler sh = getStorageHandlerList().get( storage );
			Map<Class<? extends Node>,Map<UUID,Map<String,Object>>> items = splitDataGroupForInsert( subNodeList ).get( storage );
			for( Entry<Class<? extends Node>,Map<UUID,Map<String,Object>>> item : items.entrySet() )
				sh.insert( item.getKey(), item.getValue() );
		}
		
		if( mongoNodeList.size() > 0 )
		{
			StorageHandler sh = getStorageHandlerList().get( Storage.MONGO );
			Map<Class<? extends Node>,Map<UUID,Map<String,Object>>> items = splitDataGroupForInsert( mongoNodeList ).get( Storage.MONGO );
			for( Entry<Class<? extends Node>,Map<UUID,Map<String,Object>>> item : items.entrySet() )
				sh.insert( item.getKey(), item.getValue() );
		}

		/**
		 * Here we insert batch of common node, they are stored in all the storages.
		 */
		if( commonNodeList.size() > 0 )
		{
			for( Node node : commonNodeList )
				onPreInsert( node, transactionId, executor );
			Map<Storage,Map<Class<? extends Node>,Map<UUID,Map<String,Object>>>> solutions = splitDataGroupForInsert( commonNodeList );
			Map<Storage,Map<Class<? extends Node>,Map<UUID,Map<String,Object>>>> backup = new HashMap<Storage,Map<Class<? extends Node>,Map<UUID,Map<String,Object>>>>();
			PersistenceException e = null;

			done: for( Entry<Storage,Map<Class<? extends Node>,Map<UUID,Map<String,Object>>>> solution : solutions.entrySet() )
			{
				Storage s = solution.getKey();
				Map<Class<? extends Node>,Map<UUID,Map<String,Object>>> items = solution.getValue();
				backup.put( s, new HashMap<Class<? extends Node>,Map<UUID,Map<String,Object>>>() );

				for( Entry<Class<? extends Node>,Map<UUID,Map<String,Object>>> item : items.entrySet() )
				{
					StorageHandler sh = getStorageHandlerList().get( s );
					try
					{
						sh.insert( item.getKey(), item.getValue() );
					} catch( PersistenceException pe )
					{
						e = pe;
						break done;
					}
					backup.get( s ).put( item.getKey(), item.getValue() );
				}
			}
			if( e != null )
			{
				logger.info( "MUTIPLE INSERT CASE: Rollback start..." );
				for( Entry<Storage,Map<Class<? extends Node>,Map<UUID,Map<String,Object>>>> entry : backup.entrySet() )
					for( Entry<Class<? extends Node>,Map<UUID,Map<String,Object>>> entry2 : entry.getValue().entrySet() )
						try
						{
							getStorageHandlerList().get( entry.getKey() ).destroy( entry2.getKey(), new ArrayList<UUID>( entry2.getValue().keySet() ) );
							logger.info( String.format( "             Rollback destroy %s %s %s", entry.getKey(), entry2.getKey(), entry2.getValue() ) );
						} catch( PersistenceException pe )
						{
							logger.info( String.format( "MUTIPLE INSERT CASE: Rollback failed. For %s, %s, %s", entry.getKey(), entry2.getKey(), entry2.getValue() ) );
						}
				logger.info( "INSERT CASE: Rollback finieshed." );
				throw e;
			}

			boolean hasEntityNode = false;
			for( Node node : commonNodeList )
			{
				if(node instanceof Entity)
					hasEntityNode = true;
				onPostInsert( node, transactionId, executor );
				if(node instanceof DynamicDomain) {
					node = NodeClassUtil.getSerializationNode(node);
					node.setChildren(getCache().get( node.getId() ).getChildren());
				}
				getCache().get( node.getParentId() ).addChild( node );
				getCache().put( node.getId(), node );
			}
			if(hasEntityNode)
				loadDynamicEntities();
		}
	}

	@Lock(LockType.WRITE)
	public void insertTreeNode( Node node, UUID transactionId, User executor ) throws UniqueViolateException, RequiredFieldMissingException, PersistenceException, ValidationFailedException
	{
		if( isCacheMode() )
		{
			onPreInsert( node, transactionId, executor );
			getCache().get( node.getParentId() ).addChild( node );
			getCache().put( node.getId(), node );
			onPostInsert( node, transactionId, executor );
			return;
		}

		if(node instanceof DynamicDomain || ( node.getClass().equals(Node.class) && node.getSerializationNode() != null))
			node = JSON2NodeUtil.json2Node(node.getSerializationNode(), NodeClassUtil.getDynamicEntityClassByFullName(new JSONObject(node.getSerializationNode()).get("type").toString()));

		onPreInsert( node, transactionId, executor );
		Map<String,Object> nodeMap = DomainFieldManager.getInstance().convertFromDomainToMap( node );
		/**
		 * SubNode is only stored in MySQL. And it is often used in our log system.
		 */
		if( node instanceof SubNode )
		{
			Storage storage = DomainFieldManager.getInstance().getStorageForSubNode();
			StorageHandler sh = getStorageHandlerList().get( storage );
			Map<String,Object> filtered = DomainFieldManager.getInstance().filterMapByStorage( nodeMap, storage, node.getClass() );
			sh.insert( node.getClass(), node.getId(), filtered );
		} else if( node instanceof MongoDynamicDomain )
		{
			StorageHandler sh = getStorageHandlerList().get( Storage.MONGO );
			Map<String,Object> filtered = DomainFieldManager.getInstance().filterMapByStorage( nodeMap, Storage.MONGO, node.getClass() );
			sh.insert( node.getClass(), node.getId(), filtered );
		} else
		{
			if(node instanceof Entity && !testEntity(((Entity) node).getFullName(), ((Entity) node).getSourceCode()))
				throw new ValidationFailedException();
			
			List<Storage> backup = new ArrayList<Storage>();
			PersistenceException e = null;

			for( Storage s : getStoragesByNodeClass( node.getClass() ) )
			{
				StorageHandler sh = getStorageHandlerList().get( s );
				Class<? extends Node> clazz = node.getClass();
				Map<String,Object> filtered = DomainFieldManager.getInstance().filterMapByStorage( nodeMap, s, clazz );
				if( !filtered.isEmpty() )
				{
					logger.info( String.format( "Calling insert for %s on %s", clazz.getSimpleName(), s ) );
					try
					{
						sh.insert( clazz, node.getId(), filtered );
					} catch( PersistenceException pe )
					{
						e = pe;
						break;
					}
					backup.add( s );
				}
			}
			if( e != null )
			{
				logger.info( "INSERT CASE: Rollback start..." );
				for( Storage ss : backup )
					try
					{
						getStorageHandlerList().get( ss ).destroy( node.getClass(), node.getId() );
						logger.info( String.format( "             Rollback destroy %s %s %s", ss, node.getClass(), node.getId() ) );
					} catch( PersistenceException pe )
					{
						logger.info( String.format( "INSERT CASE: Rollback failed. For %s, %s, %s", ss, node.getClass(), node.getId() ) );
					}
				logger.info( "INSERT CASE: Rollback finished." );
				throw e;
			}
			if(node instanceof DynamicDomain) {
				node = NodeClassUtil.getSerializationNode(node);
				node.setChildren(getCache().get( node.getId() ).getChildren());
			}
			node.setChildren( new ArrayList<Node>() );
			getCache().get( node.getParentId() ).addChild( node );
			getCache().put( node.getId(), node );
		}
		onPostInsert( node, transactionId, executor );
		if(node instanceof Entity)
			loadDynamicEntities();
	}

	private boolean isCacheMode()
	{
		return cacheMode;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Node> joinData( Map<Storage,Map<UUID,Map<String,Object>>> rawData, Map<UUID,Node> nodeList )
	{
		if( rawData.isEmpty() )
			return null;

		rawData.put( Storage.TREE, new HashMap<UUID,Map<String,Object>>() );
		for( Entry<UUID,Node> nodeEntry : nodeList.entrySet() )
		{
			Node rawNode = nodeEntry.getValue();
			Map<String,Object> rawNodeData = new HashMap<String,Object>();
			rawNodeData.put( "deleted", rawNode.isDeleted() );
			rawNodeData.put( "id", rawNode.getId() );
			rawNodeData.put( "parentId", rawNode.getParentId() );
			rawNodeData.put( "type", rawNode.getType().getCanonicalName() );
			rawData.get( Storage.TREE ).put( rawNode.getId(), rawNodeData );
		}

		List<Node> result = new ArrayList<Node>();
		Map<UUID,Map<String,Object>> dataInMap = new HashMap<UUID,Map<String,Object>>();
		dataInMap.putAll( rawData.get( Storage.TREE ) );

		for( Entry<Storage,Map<UUID,Map<String,Object>>> solution : rawData.entrySet() )
		{
			if( solution.getKey() == Storage.TREE )
				continue;
			for( Entry<UUID,Map<String,Object>> item : dataInMap.entrySet() )
			{
				Map<String,Object> added = solution.getValue().get( item.getKey() );
				if( added != null )
				{
					if( added.containsKey( "objectClass" ) )
					{
						if(!item.getValue().containsKey("objectClass"))
							item.getValue().put("objectClass", added.get( "objectClass" ));
						else ( (Set) ( item.getValue().get( "objectClass" ) ) ).addAll((Set) added.get( "objectClass" ) );
						added.remove( "objectClass" );
					}
					item.getValue().putAll( added );
				}
			}
		}

		for( Entry<UUID,Map<String,Object>> nodeMap : dataInMap.entrySet() ) {
			result.add( DomainFieldManager.getInstance().convertFromMapToDomain( nodeList.get( nodeMap.getKey() ), nodeMap.getValue() ) );
		}
		return result;
	}

	@Lock(LockType.WRITE)
	public void modifyTreeNode( Node node, UUID transactionId, User executor ) throws UniqueViolateException, RequiredFieldMissingException, PersistenceException, ValidationFailedException
	{
		if( isCacheMode() )
		{
			onPreUpdate( node, transactionId, executor );
			getCache().get( getCache().get( node.getId() ).getParentId() ).removeChild( getCache().get( node.getId() ) );
			getCache().get( node.getParentId() ).addChild( node );
			getCache().put( node.getId(), node );
			onPostUpdate( node, transactionId, executor );
			return;
		}

		if(DynamicDomain.class.isAssignableFrom(node.getClass()) || ( node.getClass().equals(Node.class) && node.getSerializationNode() != null))
			node = JSON2NodeUtil.json2Node(node.getSerializationNode(), NodeClassUtil.getDynamicEntityClassByFullName(new JSONObject(node.getSerializationNode()).get("type").toString()));
	
		onPreUpdate( node, transactionId, executor );
		Map<String,Object> nodeMap = DomainFieldManager.getInstance().convertFromDomainToMap( node );

		if( node instanceof SubNode )
		{
			Storage storage = DomainFieldManager.getInstance().getStorageForSubNode();
			StorageHandler sh = getStorageHandlerList().get( storage );
			Map<String,Object> filtered = DomainFieldManager.getInstance().filterMapByStorage( nodeMap, storage, node.getClass() );
			sh.update( node.getClass(), node.getId(), filtered );
		} if( node instanceof MongoDynamicDomain )
		{
			StorageHandler sh = getStorageHandlerList().get( Storage.MONGO );
			Map<String,Object> filtered = DomainFieldManager.getInstance().filterMapByStorage( nodeMap, Storage.MONGO, node.getClass() );
			sh.update( node.getClass(), node.getId(), filtered );
		} else
		{
			if(node instanceof Entity && !testEntity(((Entity) node).getFullName(), ((Entity) node).getSourceCode()))
				throw new ValidationFailedException();
			List<Storage> backup = new ArrayList<Storage>();
			PersistenceException e = null;

			for( Storage s : getStoragesByNodeClass( node.getClass() ) )
			{
				Map<String,Object> map = DomainFieldManager.getInstance().filterMapByStorage( nodeMap, s, node.getClass() );
				logger.info( String.format( "Sending update request to %s", s.toString() ) );
				try
				{
					getStorageHandlerList().get( s ).update( node.getClass(), node.getId(), map );
				} catch( PersistenceException pe )
				{
					e = pe;
					break;
				}
				backup.add( s );
				logger.info( String.format( "Got update response from %s", s.toString() ) );
			}

			if( e != null )
			{
				logger.info( "UPDATE CASE: Rollback start..." );
				Node originNode = getCache().get( node.getId() );
				Map<String,Object> originNodeMap = DomainFieldManager.getInstance().convertFromDomainToMap( originNode );
				for( Storage ss : backup )
				{
					Map<String,Object> map = DomainFieldManager.getInstance().filterMapByStorage( originNodeMap, ss, originNode.getClass() );
					try
					{
						getStorageHandlerList().get( ss ).update( originNode.getClass(), originNode.getId(), map );
						logger.info( String.format( "             Rollback update %s %s %s", ss, originNode.getClass(), originNode.getId() ) );
					} catch( PersistenceException pe )
					{
						logger.info( String.format( "UPDATE CASE: Rollback failed. For %s, %s, %s", ss, originNode.getClass(), originNode.getId() ) );
					}
				}
				logger.info( "UPDATE CASE: Rollback finished." );
				throw e;
			}

			if(node instanceof DynamicDomain) {
				node = NodeClassUtil.getSerializationNode(node);
				return;
			}
			getCache().get( getCache().get( node.getId() ).getParentId() ).removeChild( getCache().get( node.getId() ) );
			getCache().get( node.getParentId() ).addChild( node );
			getCache().put( node.getId(), node );
		}
		onPostUpdate( node, transactionId, executor );
		if(node instanceof Entity)
			loadDynamicEntities();
	}

	private void onInitialize( Node node )
	{
		if( node == null )
			return;
		if(!NodeClassUtil.getSubClassesInJVM(Node.class).contains(node.getClass()) && !node.getId().equals(rootId))
			node.setSerializationNode(JSON2NodeUtil.node2Json(node));
		for( PersistenceInterceptor interceptor : getPersistenceInterceptors( node ) )
			interceptor.initialize();
	}

	private void onPostDelete( Node node, UUID transactionId, User executor )
	{
		if( node == null )
			return;
		for( PersistenceInterceptor interceptor : getPersistenceInterceptors( node ) )
			interceptor.postDelete();

//		TreeManagerLogger.log( Action.DELETE, node, transactionId, executor );
	}

	private void onPostDestroy( Node node, UUID transactionId, User executor )
	{
		if( node == null )
			return;
		for( PersistenceInterceptor interceptor : getPersistenceInterceptors( node ) )
			interceptor.postDestroy();

//		TreeManagerLogger.log( Action.DESTROY, node, transactionId, executor );
	}

	private void onPostInsert( Node node, UUID transactionId, User executor )
	{
		if( node == null )
			return;
		for( PersistenceInterceptor interceptor : getPersistenceInterceptors( node ) )
			interceptor.postInsert();

//		TreeManagerLogger.log( Action.INSERT, node, transactionId, executor );
	}

	private void onPostUnDelete( Node node, UUID transactionId, User executor )
	{
		if( node == null )
			return;
		for( PersistenceInterceptor interceptor : getPersistenceInterceptors( node ) )
			interceptor.postUnDelete();

//		TreeManagerLogger.log( Action.UNDELETE, node, transactionId, executor );

	}

	private void onPostUpdate( Node node, UUID transactionId, User executor ) throws UniqueViolateException, RequiredFieldMissingException, ValidationFailedException, PersistenceException
	{
		if( node == null )
			return;

		// Insert newly added children, if any
		for( Node newChild : new ArrayList<Node>( node.getChildren() ) )
		{
			if( !getCache().containsKey( newChild.getId() ) )
			{
				insertTreeNode( newChild, transactionId, executor );
			} else if( newChild instanceof ParentListener )
				modifyTreeNode( newChild, transactionId, executor );
		}

		// Remove newly removed children, if any
		if(getCache().get( node.getId() ) != null)
			for( Node oldChild : getCache().get( node.getId() ).getChildren() )
			{
				if( !node.getChildren().contains( oldChild ) )
				{
					deleteTree( oldChild, transactionId, executor );
				}
			}

		for( PersistenceInterceptor interceptor : getPersistenceInterceptors( node ) )
			interceptor.postUpdate();

//		TreeManagerLogger.log( Action.UPDATE, node, transactionId, executor );

	}

	private void onPreDelete( Node node, UUID transactionId, User executor )
	{
		if( node == null )
			return;
		for( PersistenceInterceptor interceptor : getPersistenceInterceptors( node ) )
			interceptor.preDelete();
		updateAliases( node );
	}

	private void onPreDestroy( Node node, UUID transactionId, User executor )
	{
		if( node == null )
			return;
		for( PersistenceInterceptor interceptor : getPersistenceInterceptors( node ) )
			interceptor.preDestroy();
		updateAliases( node );
	}

	private void onPreInsert( Node node, UUID transactionId, User executor ) throws UniqueViolateException, RequiredFieldMissingException, ValidationFailedException
	{
		if( node == null )
			return;

		if( node.getId() == null )
			node.setId( UUID.randomUUID() );

		if( getCache().containsKey( node.getId() ) )
		{
			logger.info( String.format( "Node with ID: %s already in cache", node.getId() ) );
			throw new UniqueViolateException();
		}

		updateAliases( node );

		validateUniqueness( node );
		encryptValues( node );
		for( PersistenceInterceptor interceptor : getPersistenceInterceptors( node ) )
			interceptor.preInsert();
		
	}

	private void onPreUnDelete( Node node, UUID transactionId, User executor )
	{
		if( node == null )
			return;
		for( PersistenceInterceptor interceptor : getPersistenceInterceptors( node ) )
			interceptor.preUnDelete();
		updateAliases( node );

	}

	private void onPreUpdate( Node node, UUID transactionId, User executor ) throws RequiredFieldMissingException, PersistenceException, ValidationFailedException, UniqueViolateException
	{
		if( node == null )
			return;
		
		updateAliases( node );

		encryptValues( node );
		// Move Case
		if(node.getParentId() != null && !node.getParentId().equals( getCache().get( node.getId() ).getParentId() ) )
			validateDeadlockAndScope( node );
		validateUniqueness( node );

		for( PersistenceInterceptor interceptor : getPersistenceInterceptors( node ) )
			interceptor.preUpdate();
		
	}

	public void preDestroy()
	{
		getTreeHandler().terminate();
		logger.info( "TreeHandler terminated" );

		for( StorageHandler handler : getStorageHandlerList().values() )
		{
			logger.info( String.format( "Terminating StorageHandler: %s", handler.getClass().getCanonicalName() ) );
			handler.terminate();
		}
		logger.info( "StorageHandlers terminated" );
	}

	private void setCache( ConcurrentMap<UUID,Node> cache )
	{
		this.cache = cache;
	}

	private void setStorageHandlerList( Map<Storage,StorageHandler> storageHandlerList )
	{
		this.storageHandlerList = storageHandlerList;
	}

	private void setTreeHandler( TreeHandler treeHandler )
	{
		this.treeHandler = treeHandler;
	}

	private Map<Storage,Map<Class<? extends Node>,List<UUID>>> splitDataGroupByIDs( List<? extends Node> nodeList )
	{
		return splitDataGroupByIDs( nodeList, false );
	}

	private Map<Storage,Map<Class<? extends Node>,List<UUID>>> splitDataGroupByIDs( List<? extends Node> nodeList, boolean includeSubNodeID )
	{
		Map<Storage,Map<Class<? extends Node>,List<UUID>>> data = new HashMap<Storage,Map<Class<? extends Node>,List<UUID>>>();

		for( Storage s : getStorageHandlerList().keySet() )
		{
			Map<Class<? extends Node>,List<UUID>> map = new HashMap<Class<? extends Node>,List<UUID>>();
			for( Node node : nodeList )
			{
				Class<? extends Node> clazz = node.getClass();
				if( map.get( clazz ) == null )
					map.put( clazz, new ArrayList<UUID>() );
				map.get( clazz ).add( node.getId() );
			}
			if( map.isEmpty() )
				continue;

			data.put( s, map );
		}

		return data;
	}

	private Map<Storage,Map<Class<? extends Node>,Map<UUID,Map<String,Object>>>> splitDataGroupForInsert( List<? extends Node> nodeList )
	{
		Map<Storage,Map<Class<? extends Node>,Map<UUID,Map<String,Object>>>> data = new HashMap<Storage,Map<Class<? extends Node>,Map<UUID,Map<String,Object>>>>();

		for( Storage s : getStorageHandlerList().keySet() )
		{
			Map<Class<? extends Node>,Map<UUID,Map<String,Object>>> map = new HashMap<Class<? extends Node>,Map<UUID,Map<String,Object>>>();
			for( Node node : nodeList )
			{
				Class<? extends Node> clazz = node.getClass();
				if( map.get( clazz ) == null )
					map.put( clazz, new HashMap<UUID,Map<String,Object>>() );
				Map<String,Object> nodeMap = DomainFieldManager.getInstance().convertFromDomainToMap( node );
				map.get( clazz ).put( node.getId(), DomainFieldManager.getInstance().filterMapByStorage( nodeMap, s, clazz ) );
			}
			if( map.isEmpty() )
				continue;

			data.put( s, map );
		}

		return data;
	}

	@Lock(LockType.WRITE)
	public void unDeleteTree( Node rootNode, UUID transactionId, User executor )
	{
		if( isCacheMode() )
		{
			onPreUnDelete( rootNode, transactionId, executor );
			getCache().get( rootNode.getId() ).setDeleted( false );
			onPostUnDelete( rootNode, transactionId, executor );
			return;
		}

		onPreUnDelete( rootNode, transactionId, executor );

		List<Node> nodeList = getDecendants( rootNode );
		nodeList.add( rootNode );

		List<UUID> ids = new ArrayList<UUID>();
		for( Node node : nodeList )
			ids.add( node.getId() );
		getTreeHandler().unDelete( ids );

		getCache().get( rootNode.getId() ).setDeleted( false );
		onPostUnDelete( rootNode, transactionId, executor );
	}

	private void updateAliases( Node node )
	{
		try
		{
			for( String fieldName : NodeClassUtil.getAllNodeFieldNames( node.getClass() ) )
			{
				FieldAttributeAccessor accessor = AnnotationsParser.getAttributes( node.getClass(), fieldName );
				if( accessor == null)
					continue;
				String alias = accessor.getAlias();
				if( alias == null )
					continue;

				Object value;
				if( alias.startsWith( "parent." ) )
				{
					alias = alias.substring( 7 );
					value = NodeUtil.getAttribute( getNodeByID( node.getParentId() ), alias );
				} else
					value = NodeUtil.getAttribute( node, alias );

				Field fieldType = NodeClassUtil.getField( node.getClass(), fieldName );
				String setterName = String.format( "set%s%s", fieldName.substring( 0, 1 ).toUpperCase(), fieldName.substring( 1 ) );
				Method m = node.getClass().getMethod( setterName, fieldType.getType() );
				m.setAccessible( true );
				m.invoke( node, value );

				logger.info( String.format( "Setting alias to %s", value ) );
			}
		} catch( SecurityException e )
		{
			e.printStackTrace();
		} catch( NoSuchMethodException e )
		{
			e.printStackTrace();
		} catch( IllegalArgumentException e )
		{
			e.printStackTrace();
		} catch( IllegalAccessException e )
		{
			e.printStackTrace();
		} catch( InvocationTargetException e )
		{
			e.printStackTrace();
		} catch( PersistenceException e )
		{
			e.printStackTrace();
		}
	}

	private void validateDeadlockAndScope( Node node ) throws ValidationFailedException
	{
		Node currentNode = getCache().get( node.getId() );
		Node parentNode = getCache().get( node.getParentId() );
		if( currentNode == null || parentNode == null )
			throw new ValidationFailedException();
		if( NodeUtil.getChildren( currentNode, getCache().get( getRootId() ) ).contains( parentNode ) )
			throw new ValidationFailedException();

		if( currentNode.isDeleted() || parentNode.isDeleted() )
			throw new ValidationFailedException();
		if( parentNode.getClass().getAnnotation( DomainClass.class ).isPropertyNode() )
			throw new ValidationFailedException();
		if( !NodeUtil.getValidChildren( parentNode.getClass() ).contains( currentNode.getClass() ) )
			throw new ValidationFailedException();

		if( currentNode instanceof User )
		{
			User u = (User) currentNode;

//			if( u.hasAccount( WindowsAccount2008.class ) )
//				if( parentNode.getChildren( AbstractWindowsAccount.class ).isEmpty() )
//					throw new ValidationFailedException();

			Node administrationNode = getCache().get( u.getAdminNode() );
			if( administrationNode == null )
				throw new ValidationFailedException();
			if( NodeUtil.getParents( administrationNode, getCache().get( getRootId() ) ).contains( parentNode ) )
				throw new ValidationFailedException();
			for( Node n : u.getChildren( Permission.class ) )
			{
				Permission p = (Permission) n;
				Node tn = getCache().get( p.getTargetNodeId() );
				if( NodeUtil.getParents( tn, getCache().get( getRootId() ) ).contains( parentNode ) )
					throw new ValidationFailedException();
			}
		}
	}

	private void validateUniqueness( Node node ) throws UniqueViolateException
	{
		for( Field field : NodeClassUtil.getAllNodeFields( node.getClass() ) )
		{
			try
			{
				FieldAttributeAccessor accessor = AnnotationsParser.getAttributes( node.getClass(), field.getName() );

				if( accessor.getUnique() )
				{
					Node n = NodeUtil.getNodeByFieldValue( field, field.get( node ), this.cache.get( rootId ) );
					if( null == n )
						return;
					else if( node.getId().equals( n.getId() ) )
						return;
					else
						throw new UniqueViolateException();
				}
			} catch( IllegalArgumentException e )
			{
				e.printStackTrace();
			} catch( IllegalAccessException e )
			{
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) throws PersistenceException {
		PersistenceProxy proxy = new PersistenceProxy();
		proxy.init();
		try {
			proxy.authenticate("bhuang", "bhuang");
		} catch (InvalidCredentialsException e) {
			e.printStackTrace();
		}
		Node a = proxy.getNodeByID(UUID.fromString("72bedae8-85e0-11e4-928e-0242ac11000b"));
		Node node = proxy.getFullNodeByID(UUID.fromString("72bedae8-85e0-11e4-928e-0242ac11000b"));
		System.out.println(User.class.getName());
		System.out.println();
//		User user = new User();
//		user.setFirstName("test");
//		user.setLastName("test");
//		user.setEmail("a@gmail.com");
//		user.setPassword("test");
//		user.setUsername("test");
//		user.setBirthday(new Date());
//		user.setDeleted(false);
//		user.setGender(GenderEnum.MAN);
//		user.setLabel("User");
//		user.setAdminNode(UUID.fromString("e3e8ae71-46da-46ef-ad76-c9dc6d1b7853"));
//		user.setActivated(true);
//		user.setComment("Just test");
//		user.setParentId(UUID.fromString("e5501d2f-65bf-488a-badf-be5e0dd6c574"));
//		user.setId(UUID.randomUUID());
//		Application node = new Application();
//		node.setName("aaa");
//		node.setParentId(UUID.fromString("e3e8ae71-46da-46ef-ad76-c9dc6d1b7853"));
//		try {
//			proxy.modifyTreeNode(node, UUID.randomUUID(), null);
//		} catch (UniqueViolateException | RequiredFieldMissingException
//				| ValidationFailedException e) {
//			e.printStackTrace();
//		}
//		System.out.println();
	}
}
