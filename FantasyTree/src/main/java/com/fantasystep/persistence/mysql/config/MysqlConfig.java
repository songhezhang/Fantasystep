package com.fantasystep.persistence.mysql.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fantasystep.utils.Option;
import com.fantasystep.utils.Option.InvalidOptionFormatException;
import com.fantasystep.utils.Option.StringOption;

public class MysqlConfig
{
	private static Logger logger = LoggerFactory.getLogger(MysqlConfig.class);
	private final static MysqlConfig	databaseManager	= new MysqlConfig();
	private static String					mode;
	private static String					mysqlAddress;
	private static String					mysqlDriver;
	private static String					mysqlPassword;
	private static String					mysqlUsername;
	
	static
	{
		try
		{
			Option.setConfigFileName( "/etc/fantasystep/storage_handler_mysql.conf" );

			StringOption MODE = new StringOption( "mode", "AUTOMATIC", true, "Mode. How to handle table changes. (SAFE, AUDIT or AUTOMATIC)" );
			StringOption MYSQL_ADDRESS = new StringOption( "mysql.address", "jdbc:mysql://192.168.99.100:3306/fantasystep", true, "MySQL server address" );
			StringOption MYSQL_DRIVER = new StringOption( "mysql.driver", "org.gjt.mm.mysql.Driver", true, "MySQL database driver" );
			StringOption MYSQL_PASSWORD = new StringOption( "mysql.password", "fantasystep", true, "MySQL password" );
			StringOption MYSQL_USERNAME = new StringOption( "mysql.username", "root", true, "MySQL username" );

			Option.load();

			setMode( MODE.value() );
			setMYSQLAddress( MYSQL_ADDRESS.value() );
			setMYSQLDriver( MYSQL_DRIVER.value() );
			setMYSQLPassword( MYSQL_PASSWORD.value() );
			setMYSQLUsername( MYSQL_USERNAME.value() );

			Class.forName( getMYSQLDriver() );

		} catch( InvalidOptionFormatException e )
		{
			e.printStackTrace();
		} catch( ClassNotFoundException e )
		{
			e.printStackTrace();
		}
	}
	
	private MysqlConfig() {
	}

	public static MysqlConfig getInstance()
	{
		return databaseManager;
	}

	private static String getMYSQLDriver()
	{
		return mysqlDriver;
	}

	private static void setMode( String mode )
	{
		MysqlConfig.mode = mode;
	}

	private static void setMYSQLAddress( String mysqlAddress )
	{
		MysqlConfig.mysqlAddress = mysqlAddress;
	}

	private static void setMYSQLDriver( String mysqlDriver )
	{
		MysqlConfig.mysqlDriver = mysqlDriver;
	}

	private static void setMYSQLPassword( String mysqlPassword )
	{
		MysqlConfig.mysqlPassword = mysqlPassword;
	}

	private static void setMYSQLUsername( String mysqlUsername )
	{
		MysqlConfig.mysqlUsername = mysqlUsername;
	}

	public Connection createConnection() throws SQLException
	{
		logger.info( String.format( "%-30s : %s", "MYSQL_ADDRESS", getMYSQLAddress() ) );
		logger.info( String.format( "%-30s : %s", "MYSQL_USERNAME", getMYSQLUsername() ) );
		logger.info( String.format( "%-30s : %s", "MYSQL_PASSWORD", getMYSQLPassword() ) );

		return (Connection) DriverManager.getConnection( getMYSQLAddress(), getMYSQLUsername(), getMYSQLPassword() );
	}

	public String getMode()
	{
		return mode;
	}

	private String getMYSQLAddress()
	{
		return mysqlAddress;
	}

	private String getMYSQLPassword()
	{
		return mysqlPassword;
	}

	private String getMYSQLUsername()
	{
		return mysqlUsername;
	}

	public String getSchemaName()
	{
		String address = getMYSQLAddress();
		return address.substring( address.lastIndexOf( "/" ) + 1 );
	}
	
	public static void main(String[] args) throws SQLException {
//		MysqlConfig.getInstance().createConnection();
//		for(Entry<Class<? extends Node>, Map<String, Class<?>>> entry : DomainFieldManager.getInstance().lookupDomainClassByStorage( Storage.MYSQL ).entrySet()){
//			logger.info( entry.getKey().getSimpleName());
//			logger.info( entry.getValue());
//		}
//		MysqlTreeHandler handler = new MysqlTreeHandler();
		
//		Map<String, Object> nodeList = new HashMap<String, Object>();
//		nodeList.put("id", UUID.fromString("e3e8ae71-46da-46ef-ad76-c9dc6d1b7853"));
//		nodeList.put("deleted", false);
//		nodeList.put("label", "FantasyStep");
//		nodeList.put("name", "root");
//		nodeList.put("type", Node.class);
//		try {
//			handler.insert(Group.class, UUID.fromString("e3e8ae71-46da-46ef-ad76-c9dc6d1b7853"), nodeList);
//		} catch (PersistenceException e) {
//			e.printStackTrace();
//		}
//		
//		
//		UUID id = UUID.randomUUID();
//		nodeList = new HashMap<String, Object>();
//		nodeList.put("id", id.toString());
//		nodeList.put("deleted", false);
//		nodeList.put("label", "Family Group");
//		nodeList.put("name", "group");
//		nodeList.put("type", Group.class);
//		nodeList.put("parentId", UUID.fromString("e3e8ae71-46da-46ef-ad76-c9dc6d1b7853"));
//		try {
//			handler.insert(Group.class, id, nodeList);
//		} catch (PersistenceException e) {
//			e.printStackTrace();
//		}
		
//		UUID id = UUID.randomUUID();
//		nodeList = new HashMap<String, Object>();
//		nodeList.put("id", id.toString());
//		nodeList.put("deleted", false);
//		nodeList.put("label", "Family Member");
//		nodeList.put("name", "user");
//		nodeList.put("type", User.class);
//		nodeList.put("parentId", UUID.fromString("e5501d2f-65bf-488a-badf-be5e0dd6c574"));
//		try {
//			handler.insert(Group.class, id, nodeList);
//		} catch (PersistenceException e) {
//			e.printStackTrace();
//		}
//		List<? extends Node> list = handler.getTree(UUID.fromString("e3e8ae71-46da-46ef-ad76-c9dc6d1b7853"));
		
	}
}
