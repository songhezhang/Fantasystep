package com.fantasystep.persistence.mongo.config;

import java.sql.SQLException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fantasystep.utils.Option;
import com.fantasystep.utils.Option.IntegerOption;
import com.fantasystep.utils.Option.InvalidOptionFormatException;
import com.fantasystep.utils.Option.StringOption;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoConfig
{
	private static Logger logger = LoggerFactory.getLogger(MongoConfig.class);
	private final static MongoConfig		databaseManager	= new MongoConfig();
	private static String					mongoHost;
	private static int						mongoPort;
	private static String					mongoAuthDB;
	private static String					mongoDatabase;
	private static String					mongoPassword;
	private static String					mongoUsername;
	
	static
	{
		try
		{
			Option.setConfigFileName( "/etc/fantasystep/storage_handler_mongo.conf" );

			StringOption MONGO_HOST = new StringOption( "mongo.host", "192.168.99.100", true, "MONGO server host" );
			IntegerOption MONGO_PORT = new IntegerOption( "mongo.port", 27017, true, "MONGO database port" );
			StringOption MONGO_AUTH_DB = new StringOption( "mongo.auth.database", "admin", true, "MONGO anthentication database" );
			StringOption MONGO_DATABASE = new StringOption( "mongo.database", "fantasystep", true, "MONGO database" );
			StringOption MONGO_PASSWORD = new StringOption( "mongo.password", "fantasystep", true, "MONGO password" );
			StringOption MONGO_USERNAME = new StringOption( "mongo.username", "root", true, "MONGO username" );

			Option.load();

			mongoHost = MONGO_HOST.value();
			mongoPort = MONGO_PORT.value();
			mongoAuthDB = MONGO_AUTH_DB.value();
			mongoDatabase = MONGO_DATABASE.value();
			mongoPassword = MONGO_PASSWORD.value();
			mongoUsername = MONGO_USERNAME.value();

		} catch( InvalidOptionFormatException e )
		{
			e.printStackTrace();
		}
	}
	
	private MongoConfig() {}

	public static MongoConfig getInstance()
	{
		return databaseManager;
	}
	
	private MongoClient client = null;

	@SuppressWarnings("deprecation")
	public MongoClient getClient()
	{
		if(client == null) {
			logger.info( String.format( "%-30s : %s", "MONGO_HOST", mongoHost) );
			logger.info( String.format( "%-30s : %s", "MONGO_PORT", mongoPort) );
			logger.info( String.format( "%-30s : %s", "MONGO_AUTH_DB", mongoAuthDB ) );
			logger.info( String.format( "%-30s : %s", "MONGO_DATABASE", mongoDatabase ) );
			logger.info( String.format( "%-30s : %s", "MONGO_USERNAME", mongoUsername ) );
			logger.info( String.format( "%-30s : %s", "MONGO_PASSWORD", mongoPassword ) );
			
				MongoCredential credential = MongoCredential.createScramSha1Credential(mongoUsername, mongoAuthDB, mongoPassword.toCharArray());
				client = new MongoClient(new ServerAddress(mongoHost, mongoPort), Arrays.asList(credential));
		}
		return client;
	}
	
	@SuppressWarnings("deprecation")
	public DB getDB() {
		return getClient().getDB(mongoDatabase);
	}
	
	public static void main(String[] args) throws SQLException {
		DB db = MongoConfig.getInstance().getDB();
		logger.info(db.getCollectionNames().toString());
	}
}
