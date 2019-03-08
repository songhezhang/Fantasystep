package com.fantasystep.persistence.mysql;

import java.awt.geom.Point2D;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fantasystep.annotation.AnnotationsParser;
import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.annotation.SerializationType;
import com.fantasystep.annotation.Storage;
import com.fantasystep.annotation.ValueOptionEntry;
import com.fantasystep.domain.Node;
import com.fantasystep.persistence.manager.DomainFieldManager;
import com.fantasystep.persistence.mysql.config.MysqlConfig;

public class MysqlHandler
{

	private static Logger logger = LoggerFactory.getLogger(MysqlHandler.class);
	protected final static int					LONGTEXT_LENGTH	= 65535;
	
	private Comparator<Entry<String,Class<?>>>	comp			= new Comparator<Entry<String,Class<?>>>()
																{
																	@Override
																	public int compare( Entry<String,Class<?>> e1, Entry<String,Class<?>> e2 )
																	{

																		if( e1.getKey().equals( "id" ) )
																			return -1;
																		else if( e2.getKey().equals( "id" ) )
																			return 1;

																		if( e1.getValue() == e2.getValue() )
																			return e1.getKey().compareTo( e2.getKey() );

																		if( e1.getValue() == Date.class )
																			return 1;
																		else if( e2.getValue() == Date.class )
																			return -1;

																		if( e1.getKey().equals( e2.getKey() ) )
																			return 0;

																		return e1.getKey().compareTo( e2.getKey() );
																	}
																};

	private void generateColumnTypeAndDefaut( StringBuffer generateSql, FieldAttributeAccessor accessor, Class<?> clazz )
	{
		boolean hasQuote = false;
		if( accessor.getSerializationType() == null || accessor.getSerializationType() == SerializationType.USE_FIELD_TYPE )
		{
			if( clazz == String.class || clazz == boolean.class || clazz == Boolean.class || Enum.class.isAssignableFrom( clazz ) || clazz == Point2D.Double.class || clazz == Class.class
					|| clazz == UUID.class || clazz == Class.class || clazz == List.class || clazz == Set.class )
			{
				hasQuote = true;
				if( accessor.getSerializationMaximumLength() >= MysqlHandler.LONGTEXT_LENGTH )
					generateSql.append( "longtext " );
				else
					generateSql.append( "varchar(" ).append( accessor.getSerializationMaximumLength() ).append( ") " );

			} else if( clazz == Date.class )
				generateSql.append( "timestamp " );
			else if( clazz == int.class || clazz == Integer.class )
				generateSql.append( "int(" ).append( accessor.getSerializationMaximumLength() ).append( ") " );
			else if( clazz == double.class || clazz == Double.class )
				generateSql.append( "double " );
			else if( clazz == float.class || clazz == Float.class )
				generateSql.append( "float " );
			else if( clazz == byte.class || clazz == Byte.class || clazz == byte[].class || clazz == Byte[].class )
			{
				hasQuote = true;
				generateSql.append( "varbinary(" ).append( accessor.getSerializationMaximumLength() ).append( ") " );
			}
		} else
			switch( accessor.getSerializationType() )
			{
				case STRING:
				case BOOLEAN:
					if( accessor.getSerializationMaximumLength() >= MysqlStorageHandler.LONGTEXT_LENGTH )
						generateSql.append( "longtext " );
					else
						generateSql.append( "varchar(" ).append( accessor.getSerializationMaximumLength() ).append( ") " );
					hasQuote = true;
					break;
				case TIMESTAMP:
					generateSql.append( "timestamp " );
					break;
				case DATE:
					generateSql.append( "date " );
					break;
				case INTEGER:
					generateSql.append( "int(" ).append( accessor.getSerializationMaximumLength() ).append( ") " );
					break;
				case DECIMAL:
					generateSql.append( "double " );
					break;
				case BINARY:
					generateSql.append( "varbinary(" ).append( accessor.getSerializationMaximumLength() ).append( ") " );
					hasQuote = true;
					break;
				case USE_FIELD_TYPE:
					break;
			}

		if( accessor.getSharedKey() )
		{
			generateSql.append( "NOT NULL" );
			return;
		} else if( accessor.getRequired() )
			generateSql.append( "NOT NULL" );
		else if( !accessor.getRequired() )
			generateSql.append( "NULL" );

		String defaultValue = accessor.getDefaultValue();
		if( accessor.getDefaultValue() != null )
			generateSql.append( " DEFAULT " + ( hasQuote ? "'" + defaultValue + "'" : defaultValue ) );
//		else if( !accessor.getRequired() )
//			generateSql.append( " DEFAULT NULL " );
	}

	private List<String> getDomainNameListWithLowerCase()
	{
		List<String> result = new ArrayList<String>();
		for( Class<? extends Node> clazz : DomainFieldManager.getInstance().lookupDomainClassByStorage( Storage.MYSQL ).keySet() )
			result.add( clazz.getSimpleName().toLowerCase() );
		return result;
	}

	protected boolean isStringField( Class<?> clazz, Object obj )
	{
		if( obj instanceof String )
			return true;
		/** Defensive code, actually obj should be cast to String in DomainFieldManager */
		if( clazz == String.class || clazz == UUID.class || clazz == Date.class || Enum.class.isAssignableFrom( clazz ) || clazz == Boolean.class || clazz == boolean.class
				|| clazz == Point2D.Double.class || clazz instanceof Class)
			return true;
		else
			return false;
	}

	private List<Entry<String,Class<?>>> orderColumns( Set<Entry<String,Class<?>>> entrySet )
	{
		List<Entry<String,Class<?>>> list = new ArrayList<Entry<String,Class<?>>>();
		list.addAll( entrySet );

		Collections.sort( list, comp );
		return list;
	}
	
	protected void setup(Storage storage)
	{
		String mode = MysqlConfig.getInstance().getMode();
		logger.info( "Running in " + mode + " mode." );
		if( mode.trim().equalsIgnoreCase( "SAFE" ) )
			setupWithMode( false, storage );
		else if( mode.trim().equalsIgnoreCase( "AUTOMATIC" ) )
			setupWithMode( true, storage );
		else if( mode.trim().equalsIgnoreCase( "AUDIT" ) )
			setupWithMode( false, storage );
	}

	private void setupWithMode( boolean isAuto, Storage storage )
	{
		Connection c = null;
		try
		{
			c = MysqlConfig.getInstance().createConnection();

			Statement s = c.createStatement();

			StringBuffer generateSql = new StringBuffer();
			/** Lookup schema. */
			String schema = MysqlConfig.getInstance().getSchemaName();
			/** Lookup engine. */
			String engine = null;
			String engineNameSQL = " SELECT `ENGINE` FROM information_schema.ENGINES WHERE `SUPPORT`='DEFAULT' ";
			ResultSet rs = s.executeQuery( engineNameSQL );

			if( rs.next() )
				engine = rs.getObject( "ENGINE" ).toString();
			else
				engine = "InnoDB";
			
			/** Drop unnecessary tables. */
			List<String> tables = new ArrayList<String>();
			String sqlCheckBySchema = " SELECT `TABLE_NAME` FROM information_schema.TABLES WHERE `TABLE_SCHEMA` = '" + schema + "'";
			rs = s.executeQuery( sqlCheckBySchema );
			if( rs.next() )
			{
				do
				{
					tables.add( rs.getObject( "TABLE_NAME" ).toString() );
				} while( rs.next() );

				List<String> domainList = getDomainNameListWithLowerCase();
				domainList.add( "node" );
				for( String t : tables )
				{
					if( !domainList.contains( t ) )
						generateSql.append( String.format( "DROP TABLE `%s`;\n", t ) );
				}
			}

			/** Lookup the columns in each table. And alter and create tables dynamically. */
			String sqlCheckByTable = " SELECT `COLUMN_NAME` FROM information_schema.COLUMNS WHERE `TABLE_SCHEMA` = '" + schema + "' AND `TABLE_NAME`='%s' ";
			
			for( Entry<Class<? extends Node>,Map<String,Class<?>>> domainClassEntry : DomainFieldManager.getInstance().lookupDomainClassByStorage( storage ).entrySet() )
			{
				String table = domainClassEntry.getKey().getSimpleName().toLowerCase();

				Map<String,Class<?>> columns = new TreeMap<String,Class<?>>( String.CASE_INSENSITIVE_ORDER );
				columns.putAll( domainClassEntry.getValue() );

				Map<String,String> nameResolve = new HashMap<String,String>();

				for( String key : domainClassEntry.getValue().keySet() )
					nameResolve.put( key.toLowerCase(), key );
				logger.info("Execute sql : " + String.format( sqlCheckByTable, table ));
				rs = s.executeQuery( String.format( sqlCheckByTable, table ) );

				List<String> records = new ArrayList<String>();
				if( rs.next() )
				{
					/**
					 * This block is responsible for altering table dynamically.
					 * 
					 * */
					do
					{
						records.add( rs.getObject( "COLUMN_NAME" ).toString() );
					} while( rs.next() );

					/** Delete column functionality. */
					for( String column : records )
					{
						if( columns.get( column ) == null )
							generateSql.append( String.format( "ALTER TABLE `%s` DROP %s; \n", table, column ) );
					}

					/** Add column functionality. */
					Class<?> wrapClass = null;

					for( Entry<String,Class<?>> domainPropertyEntry : columns.entrySet() )
					{
						String column = domainPropertyEntry.getKey().toLowerCase();
						boolean isWrapClassProperty = false;
						if( column.indexOf( DomainFieldManager.DELIMITER ) != -1 )
							isWrapClassProperty = true;
						String columnName = isWrapClassProperty ? column.substring( column.lastIndexOf( DomainFieldManager.DELIMITER ) + 1 ) : column;

						Class<?> clazz = domainPropertyEntry.getValue();
						FieldAttributeAccessor accessor = null;
						if( nameResolve.get( columnName ) != null )
							accessor = AnnotationsParser.getAttributes( domainClassEntry.getKey(), nameResolve.get( columnName ) );
						if( accessor != null && accessor.getListType() != null )
							wrapClass = domainPropertyEntry.getValue();

						if( isWrapClassProperty )
							accessor = AnnotationsParser.getAttributes( wrapClass, columnName );

						if( !records.contains( column ) /*&& ( accessor.getListType() == null || ( accessor.getListType() != null && ValueOptionEntry.class.isAssignableFrom( accessor.getListType() ) ) ) */)
						{
							generateSql.append( String.format( "ALTER TABLE `%s` ADD %s ", table, column ) );
							generateColumnTypeAndDefaut( generateSql, accessor, clazz );
							generateSql.append( ";\n" );
						}
					}
				} else
				{
					/**
					 * This block is responsible for creating table dynamically.
					 * 
					 * */
					generateSql.append( String.format( "CREATE TABLE IF NOT EXISTS `%s` (\n", table ) );

					Class<?> wrapClass = null;
					String primaryKey = null;

					for( Entry<String,Class<?>> domainPropertyEntry : orderColumns( columns.entrySet() ) )
					{
						String column = domainPropertyEntry.getKey().toLowerCase();
						boolean isWrapClassProperty = false;
						if( column.indexOf( DomainFieldManager.DELIMITER ) != -1 )
							isWrapClassProperty = true;
						String columnName = isWrapClassProperty ? column.substring( column.lastIndexOf( DomainFieldManager.DELIMITER ) + 1 ) : column;

						Class<?> clazz = domainPropertyEntry.getValue();

						FieldAttributeAccessor accessor = null;
						if( nameResolve.get( columnName ) != null )
						{
							if( isWrapClassProperty )
								accessor = AnnotationsParser.getAttributes( wrapClass, nameResolve.get( columnName ) );
							else
								accessor = AnnotationsParser.getAttributes( domainClassEntry.getKey(), nameResolve.get( columnName ) );
						}
						if( accessor != null && accessor.getSharedKey())
							primaryKey = column;

						if( accessor != null && accessor.getListType() != null )
							wrapClass = domainPropertyEntry.getValue();

						if( isWrapClassProperty )
							accessor = AnnotationsParser.getAttributes( wrapClass, columnName );

						if( accessor != null && (accessor.getListType() == null || ( accessor.getListType() != null && ValueOptionEntry.class.isAssignableFrom( accessor.getListType() ) ) ) )
						{
							generateSql.append( String.format( "  `%s` ", column ) );
							this.generateColumnTypeAndDefaut( generateSql, accessor, clazz );
							generateSql.append( ",\n" );
						}
					}

					if( primaryKey != null )
						generateSql.append( String.format( "  PRIMARY KEY (`%s`)\n", primaryKey ) );
					generateSql.append( ") ENGINE=" ).append( engine ).append( " DEFAULT CHARSET=latin1;\n" );
				}

			}
			
			handleReservedSql(tables, generateSql);

			if( isAuto )
			{
				logger.info( "We are going to execute sql below ......" );
				logger.info( "========================================" );
				logger.info( generateSql.toString() );
				logger.info( "========================================" );
				for( String sql : generateSql.toString().split( ";" ) )
					if( !sql.trim().isEmpty() )
						s.execute( sql );
				logger.info( "Sql execution successful !!\n\n" );
			} else
			{
				logger.info( "========================================" );
				logger.info( "GenerateSQL : \n" + generateSql.toString() );
				logger.info( "========================================" );
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
	}
	
	protected void handleReservedSql(List<String> tables, StringBuffer generateSql) {
		
	}
	
	public static void main(String[] args) {
//		logger.info(DomainFieldManager.getInstance().lookupDomainClassByStorage( Storage.MYSQL ));
	}
}
