package com.fantasystep.persistence.ldap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fantasystep.domain.Node;
import com.fantasystep.persistence.StorageHandler;
import com.fantasystep.persistence.exception.PersistenceException;
import com.fantasystep.persistence.ldap.FilterUtil.Operator;
import com.fantasystep.persistence.ldap.exception.NoSuchEntryException;
import com.fantasystep.utils.Option;
import com.fantasystep.utils.Option.InvalidOptionFormatException;
import com.fantasystep.utils.Option.StringOption;

public class LDAPStorageHandler implements StorageHandler
{

	private static Logger logger = LoggerFactory.getLogger(LDAPStorageHandler.class);
	protected static final String	BY_EMAIL_FILTER		= "(fantasystepEmail=%s)";
	protected static final String	BY_UUID_FILTER		= "(fantasystepId=%s)";
	protected static final String	OBJECT_CLASS_FILTER	= "(objectClass=%s)";

	@SuppressWarnings("unused")
	private static void printMap( Map<UUID,Map<String,Object>> result )
	{
		for( Entry<UUID,Map<String,Object>> node : result.entrySet() )
		{
			logger.info( node.getKey().toString() );
			for( Entry<String,Object> nodeAttributes : node.getValue().entrySet() )
				logger.info( String.format( "%35s : %s", nodeAttributes.getKey(), nodeAttributes.getValue() ) );
		}
	}

	private String					ldapAddress;
	private LDAPClient				ldapClient					= null;
	private String					ldapCredentialDN;
	private String					ldapCredentialPassword;
	private String					ldapRootDN;

	public LDAPStorageHandler()
	{
		logger.info( String.format( "%s constructor.", getClass().getSimpleName() ) );
		try
		{
			Option.setConfigFileName( "/etc/fantasystep/storage_handler_ldap.conf" );
			StringOption	LDAP_ADDRESS				= new StringOption( "ldap.address", "ldap://192.168.99.100:389", true, "LDAP server address" );
			StringOption	LDAP_CREDENTIAL_DN			= new StringOption( "ldap.bindDN", "cn=admin,dc=fantasystep,dc=com", true, "LDAP credentials DN" );
			StringOption	LDAP_CREDENTIAL_PASSWORD	= new StringOption( "ldap.bindPassword", "admin", true, "LDAP credentials password" );
			StringOption	LDAP_ROOT_DN				= new StringOption( "ldap.rootDN", "ou=people,dc=fantasystep,dc=com", true, "LDAP root DN" );

			Option.load();
			
			setLDAPAddress( LDAP_ADDRESS.value() );
			setLDAPCredentialDN( LDAP_CREDENTIAL_DN.value() );
			setLDAPCredentialPassword( LDAP_CREDENTIAL_PASSWORD.value() );
			setLDAPRootDN( LDAP_ROOT_DN.value() );
		} catch (InvalidOptionFormatException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean destroy( Class<? extends Node> nodeClass, List<UUID> ids ) throws PersistenceException
	{
		for( UUID id : ids )
			if( !destroy( nodeClass, id ) )
				return false;
		return true;
	}

	@Override
	public boolean destroy( Class<? extends Node> nodeClass, UUID id ) throws PersistenceException
	{
		try
		{
			getLDAPClient().destroyEntryByDN( getDNbyUUID( id ) );
			return true;
		} catch( NamingException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	// private static final String ALL_GID_NUMBERS_FILTER = "(gidNumber=*)";
	// private static final String ALL_UID_NUMBERS_FILTER = "(uidNumber=*)";
	// private static final String BY_PARENT_ID_FILTER = "(&(fantasystepParentId=%s)(!(deleted=true)))";

	protected String getDNbyEmail( String email )
	{
		String dn = null;
		try
		{
			String filter = String.format( BY_EMAIL_FILTER, email );

			dn = getLDAPClient().getDNbyFilter( filter, getLDAPRootDN() );
		} catch( NamingException e )
		{
			e.printStackTrace();
		} catch( NoSuchEntryException e )
		{
			e.printStackTrace();
		}

		return dn;
	}

	protected String getDNbyUUID( UUID uuid )
	{
		String dn = null;
		try
		{
			String filter = String.format( BY_UUID_FILTER, uuid.toString() );

			dn = getLDAPClient().getDNbyFilter( filter, getLDAPRootDN() );
		} catch( NamingException e )
		{
			e.printStackTrace();
		} catch( NoSuchEntryException e )
		{
			e.printStackTrace();
		}

		return dn;
	}

	protected String getLDAPAddress()
	{
		return ldapAddress;
	}

	protected LDAPClient getLDAPClient()
	{
		return ldapClient;
	}

	protected String getLDAPCredentialDN()
	{
		return ldapCredentialDN;
	}

	protected String getLDAPCredentialPassword()
	{
		return ldapCredentialPassword;
	}

	protected String getLDAPRootDN()
	{
		return ldapRootDN;
	}

	@Override
	public boolean insert( Class<? extends Node> nodeClass, Map<UUID,Map<String,Object>> nodeList ) throws PersistenceException
	{
		for( Entry<UUID,Map<String,Object>> e : nodeList.entrySet() )
			if( !insert( nodeClass, e.getKey(), e.getValue() ) )
				return false;
		return true;
	}

	@Override
	public boolean insert( Class<? extends Node> nodeClass, UUID id, Map<String,Object> node ) throws PersistenceException
	{
		try
		{
			// Get parent DN

			logger.info( "======ParentId======" + UUID.fromString( node.get( "fantasystepParentId" ).toString() ) );
			Name dn = Name.fromString( getDNbyUUID( UUID.fromString( node.get( "fantasystepParentId" ).toString() ) ) );

			// Create new DN using parent DN
//			dn.prepend( "fantasystepId", id.toString() );
			dn.prepend( "cn", node.get("cn").toString() );
			// Make sure we have fantasystepId in our node data
//			node.put( "fantasystepId", id.toString() );
//			node.put( "dn", dn.toString());

			// Bind entry
			getLDAPClient().createEntryByDN( dn, node );
			return true;
		} catch( NamingException e )
		{
			e.printStackTrace();
		}
		return false;
	}

//	public static void main( String[] args ) throws IOException, PersistenceException, ClassNotFoundException
//	{
//		LDAPStorageHandler sh = new LDAPStorageHandler();
//		sh.setLDAPAddress( "ldap://tm.kontorsplatsen.se:10389" );
//		sh.setup();
//
//		List<UUID> ids = new ArrayList<UUID>();
//
//		String data = new String( FileUtil.getContent( new File( "/tmp/id" ) ) );
//		for( String id : data.split( "\n" ) )
//			ids.add( UUID.fromString( id ) );
//
//		Map<UUID,Map<String,Object>> result = sh.read( null, ids, null );
//
//		List<String> skipKeys = new ArrayList<String>();
//		skipKeys.add( "fantasysteptype" );
//		skipKeys.add( "objectclass" );
//		skipKeys.add( "fantasystepid" );
//		skipKeys.add( "fantasystepparentid" );
//		skipKeys.add( "deleted" );
//		skipKeys.add( "dn" );
//
//		for( Entry<UUID,Map<String,Object>> e : result.entrySet() )
//		{
//			UUID id = e.getKey();
//
//			StringBuilder sb = new StringBuilder();
//			Map<String,Object> nodeData = e.getValue();
//			if( nodeData.get( "fantasysteppassword" ) == null )
//				continue;
//
//			// sb.append( "INSERT INTO `node` (`id`, `parentid`, `type`, `deleted`) VALUES (" );
//			// sb.append( String.format( "\"%s\",", id ) );
//			// sb.append( String.format( "\"%s\",", nodeData.get( "fantasystepparentid" ) ) );
//			// sb.append( String.format( "\"%s\",", nodeData.get( "fantasysteptype" ) ) );
//			// sb.append( String.format( "%s", nodeData.get( "deleted" ) ) );
//			// sb.append( ");" );
//
//			sb.append( "UPDATE `user` SET " );
//			sb.append( String.format( "`email`=\"%s\",", nodeData.get( "fantasystepemail" ) ) );
//			sb.append( String.format( "`password`=\"%s\"", nodeData.get( "fantasysteppassword" ) ) );
//			sb.append( String.format( " WHERE `id`=\"%s\";", id ) );
//
//			logger.info( sb.toString() );
//		}
//
//	}

	@Override
	public Map<UUID,Map<String,Object>> read( Class<? extends Node> nodeClass, List<UUID> ids, Map<String,Object> nodeData ) throws PersistenceException
	{
		String filter = FilterUtil.buildFilter( Operator.OR, "fantasystepId", new ArrayList<Object>( ids ) );

		Map<UUID,Map<String,Object>> result = new HashMap<UUID,Map<String,Object>>();

		try
		{
			List<Map<String,Object>> dataList = getLDAPClient().getEntriesbyFilter( filter, getLDAPRootDN() );
			for( Map<String,Object> data : dataList )
			{
				String id = data.get( "fantasystepId" ).toString();
				result.put( UUID.fromString( id ), data );
			}
		} catch( NamingException e )
		{
			e.printStackTrace();
		}

		// for( UUID id : nodeDataMap.keySet() )
		// result.put( id, read( null, id, null ) );
		return result;
	}

	@Override
	public Map<String,Object> read( Class<? extends Node> nodeClass, UUID id, Map<String,Object> node ) throws PersistenceException
	{
		Map<String,Object> result = null;
		try
		{
			result = getLDAPClient().getEntryByDN( getDNbyUUID( id ) );
		} catch( NamingException e )
		{
			e.printStackTrace();
		}
		byte[] o = (byte[])result.get("userPassword");
//		Class<?> clazz = o.getClass();
		logger.info(o.getClass().toString());
		logger.info(new String(o));
		return result;
	}

	private void setLDAPAddress( String ldapAddress )
	{
		this.ldapAddress = ldapAddress;
	}

	public void setLDAPClient( LDAPClient ldapClient )
	{
		this.ldapClient = ldapClient;
	}

	private void setLDAPCredentialDN( String ldapCredentialDN )
	{
		this.ldapCredentialDN = ldapCredentialDN;
	}

	private void setLDAPCredentialPassword( String ldapCredentialPassword )
	{
		this.ldapCredentialPassword = ldapCredentialPassword;
	}

	private void setLDAPRootDN( String ldapRootDN )
	{
		this.ldapRootDN = ldapRootDN;
	}

	@Override
	public void setup()
	{
		setLDAPClient( new LDAPClient() );
		try
		{

			getLDAPClient().setProviderAddress( getLDAPAddress() );
			getLDAPClient().setSecurityPrincipal( getLDAPCredentialDN() );
			getLDAPClient().setSecurityCredentials( getLDAPCredentialPassword() );
			getLDAPClient().connect();

		} catch( NamingException e )
		{
			e.printStackTrace();
		}

	}

	@Override
	public void terminate()
	{
		getLDAPClient().disconnect();
	}

	@Override
	public boolean update( Class<? extends Node> nodeClass, UUID id, Map<String,Object> node ) throws PersistenceException
	{
		try
		{
			getLDAPClient().updateEntryByDN( getDNbyUUID( id ), node );
		} catch( NamingException e )
		{
			e.printStackTrace();
		}
		return true;
	}
	public static void main(String[] args) throws PersistenceException {
		LDAPStorageHandler handler = new LDAPStorageHandler();
		handler.setup();
//		DomainFieldManager manager = new DomainFieldManager();
//		MysqlStorageHandler handler2 = new MysqlStorageHandler();
//		Map<String, Object> result = handler.read(null, UUID.fromString("72bedae8-85e0-11e4-928e-0242ac11000b"), null);
//		Map<UUID, Map<String, Object>> result2 = handler.read(null, Arrays.asList( new UUID[]{UUID.fromString("72bedae8-85e0-11e4-928e-0242ac11000b")}), null);
//		Map<String, Object> result3 = handler2.read(User.class, UUID.fromString("72bedae8-85e0-11e4-928e-0242ac11000b"), null);
//		logger.info(result);
//		logger.info(result2);
//		logger.info(result3);
//		logger.info(Boolean.parseBoolean("true"));
//		User user = new User();
//		Node u = manager.convertFromMapToDomain(user, result);
//		logger.info(result);
		
		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("dn", "cn=Ruixi,ou=people,dc=fantasystep,dc=com");
		map.put("cn", "Ruixi");
		map.put("sn", "rzhang");
		map.put("fantasystepEmail", "ruixi@gmail.com");
		UUID id = UUID.randomUUID();
		map.put("fantasystepId", id.toString());
		map.put("fantasystepParentId", UUID.fromString("e5501d2f-65bf-488a-badf-be5e0dd6c574").toString());
		List<String> list = Arrays.asList(new String[] {"top", "person", "fantasystepNode"});
		map.put("objectClass", list);
		handler.insert(null, id, map);
		logger.info("Finished");
//				objectClass: top
//				objectClass: person
//				objectClass: fantasystepNode
	}
}
