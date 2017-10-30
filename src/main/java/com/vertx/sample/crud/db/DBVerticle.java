package com.vertx.sample.crud.db;



import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import com.vertx.sample.crud.MainPatchFactoryVerticle;
import com.vertx.sample.crud.ServiceConfiguration;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * 
 */

/**
 * @author mthakare
 *
 */
public class DBVerticle extends AbstractVerticle 
{
	
	public static String DB_DEFAULT_HOST = "localhost:5432";
	
	public static Integer DB_DEFAULT_PORT = 5432;
	
	public static Integer DB_DEFAULT_MAX_CONNECTION_POOL_SIZE = 30;
	
	public static final String CONFIG_SQL_QUERIES_RESOURCE_FILE = "sqlmap.properties";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DBVerticle.class);
	
	private SQLConnection connection;
	
	@Override
	public void start(Future<Void> startFuture) throws IOException	
	{
		
		HashMap<SQLQuery, String> sqlQueries = loadSqlQueries();
		
		// Define Database configuration
		JsonObject postgreSQLClientConfig = new JsonObject()
				.put("host", config().getString(ServiceConfiguration.DB_HOST_KEY, DB_DEFAULT_HOST))
				.put("port", config().getInteger(ServiceConfiguration.DB_PORT_KEY, DB_DEFAULT_PORT))
				.put("maxPoolSize", config().getInteger(ServiceConfiguration.DB_MAX_CONNECTION_POOL_SIZE_KEY, DB_DEFAULT_MAX_CONNECTION_POOL_SIZE))
				.put("username", config().getString(ServiceConfiguration.DB_USER_KEY))
				.put("password", config().getString(ServiceConfiguration.DB_PASSWORD_KEY))
				.put("database", config().getString(ServiceConfiguration.DB_DATABASE));
		
		SQLClient postgreSQLClient = PostgreSQLClient.createShared(vertx, postgreSQLClientConfig);
		
		// Test connection
		postgreSQLClient.getConnection(ar -> {
			if (ar.succeeded()) {
				LOGGER.info("connection successfull. Details " +  ar.result());
				connection = ar.result();
				//connection.close();
				startFuture.complete();
			} else {
				LOGGER.error("Failed to make connection");
				startFuture.fail(ar.cause());
			}
		});
		
		// Register service
		DatabaseService.create(postgreSQLClient, sqlQueries, ready -> {
			if (ready.succeeded()) {
				ProxyHelper.registerService(DatabaseService.class, vertx, ready.result(), MainPatchFactoryVerticle.CONFIG_QUEUE);
				startFuture.complete();
			} else {
				startFuture.fail(ready.cause());
			} 
		});
		
		
	}
	
	@Override
	public void stop(Future<Void> stopFuture) {
		if (connection != null) {
			connection.close();
		}
		
		stopFuture.complete();
	}
	
	
	private HashMap<SQLQuery, String> loadSqlQueries() throws IOException {
		String queriesFile = config().getString(CONFIG_SQL_QUERIES_RESOURCE_FILE);

		InputStream queriesInputStream;

		if (queriesFile != null) {
			queriesInputStream = new FileInputStream(queriesFile);
		} else {
			queriesInputStream = getClass().getResourceAsStream("/sqlmap.properties");
		}

		Properties queriesProps = new Properties();
		queriesProps.load(queriesInputStream);
		queriesInputStream.close();
		HashMap<SQLQuery, String> sqlQueries = new HashMap<>();
		sqlQueries.put(SQLQuery.CREATE_PROJECT, queriesProps.getProperty("create-project"));
		sqlQueries.put(SQLQuery.GET_ALL_PROJECT_RECORDS, queriesProps.getProperty("get-all-project"));
		
		return sqlQueries;
	}
	
}
