package com.vertx.sample.crud.db;

import java.util.HashMap;

import com.vertx.sample.crud.db.DatabaseServiceVertxEBProxy;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLClient;

@ProxyGen
public interface DatabaseService {

	@Fluent
	DatabaseService createPatchingProject(JsonObject patchingProject,
			Handler<AsyncResult<JsonObject>> resultHandler);

	@Fluent
	DatabaseService getAllPatchingProjects(Handler<AsyncResult<JsonArray>> resultHandler);

	static DatabaseService create(SQLClient dbClient, HashMap<SQLQuery, String> sqlQueries,
			Handler<AsyncResult<DatabaseService>> readyHandler) {
		return new DatabaseServiceImpl(dbClient, sqlQueries, readyHandler);
	}

	 static DatabaseService createProxy(Vertx vertx, String address) {
	
		 return new DatabaseServiceVertxEBProxy(vertx, address);
	 }

}
