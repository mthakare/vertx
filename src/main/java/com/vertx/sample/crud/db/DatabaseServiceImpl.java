/**
 * 
 */
package com.vertx.sample.crud.db;

import java.util.HashMap;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

/**
 * @author mthakare
 *
 */
public class DatabaseServiceImpl implements DatabaseService {

	private HashMap<SQLQuery, String> sqlQueries;
	private SQLClient dbClient;
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseServiceImpl.class);
	
	
	public DatabaseServiceImpl(SQLClient connection, HashMap<SQLQuery, String> sqlQueries,
			Handler<AsyncResult<DatabaseService>> readyHandler) {

		this.sqlQueries = sqlQueries;
		this.dbClient = connection;
		
		readyHandler.handle(Future.succeededFuture(this));

	}

	@Override
	public DatabaseService createPatchingProject(JsonObject patchingProject,
			Handler<AsyncResult<JsonObject>> resultHandler) {
		
		this.dbClient.getConnection(car -> {
			if (car.succeeded()) {
				SQLConnection connection = car.result();
		
				connection.updateWithParams(
						sqlQueries.get(SQLQuery.CREATE_PROJECT), 
						
						new JsonArray()
									   .add(patchingProject.getString("name"))
									   .add(patchingProject.getString("owner"))
									   .add(patchingProject.getJsonObject("policy").toString())
									   .add(patchingProject.getString("service_id")), 
						
		                ready -> {
		                	connection.close();
		                	if (ready.failed()) {
		                		LOGGER.error(ready.cause());
		                		resultHandler.handle(Future.failedFuture(ready.cause()));
		                		
		                	} else {
		                		UpdateResult result = ready.result();
		                		
		                		JsonObject res = new JsonObject()
		                			.put("updatedRows", result.getUpdated());
		                			
		                		resultHandler.handle(Future.succeededFuture(res));
		                		
		                	}
						});
			} else {
				LOGGER.error("Database query error", car.cause());
				resultHandler.handle(Future.failedFuture(car.cause()));
			}
		});

		
		return this;
	}

	@Override
	public DatabaseService getAllPatchingProjects(Handler<AsyncResult<JsonArray>> resultHandler) {

		this.dbClient.getConnection(car -> {
			if (car.succeeded()) {
				SQLConnection connection = car.result();
				connection.queryWithParams(sqlQueries.get(SQLQuery.GET_ALL_PROJECT_RECORDS), new JsonArray(), ready -> {
					connection.close();
					if (ready.succeeded()) {
						JsonArray result = new JsonArray();
						
						for (JsonArray row : ready.result().getResults()) {
							
							result.add(new JsonObject().put("id", row.getInteger(0))
							.put("name", row.getString(1))
							.put("owner", row.getString(2))
							.put("policy", new JsonObject(row.getString(3)))
							.put("service_id", row.getString(4)));
						}
						
						resultHandler.handle(Future.succeededFuture(result));
						
					} else {
                		LOGGER.error(ready.cause());
                		resultHandler.handle(Future.failedFuture(ready.cause()));
					}
				});
			} else {
				LOGGER.error("Database query error", car.cause());
				resultHandler.handle(Future.failedFuture(car.cause()));
			}
		});
		return this;
	}

}
