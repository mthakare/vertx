package com.vertx.sample.crud.http;


import com.vertx.sample.crud.MainPatchFactoryVerticle;
import com.vertx.sample.crud.db.DatabaseService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * @author mthakare
 *
 */
public class HTTPServiceVerticle extends AbstractVerticle 
{
	public static Logger LOGGER = LoggerFactory.getLogger(HTTPServiceVerticle.class);
	public static final int DEFAULT_HTTP_SERVER_PORT = 1729; //ramanujan number	
	public static Integer getCount = 0;
	private DatabaseService dbService;
	
	@Override
	public void start(Future<Void> startFuture) 
	{
		
		// Create HTTP Server
		HttpServer server = vertx.createHttpServer();
		
		//Create Router
		Router router = Router.router(vertx);
		router.get("/v1/vertxapp/version").handler(this::getVersion);
		router.route("/v1/vertxapp/records*").handler(BodyHandler.create());
		router.post("/v1/vertxapp/records").handler(this::createPatchingProject);
		router.get("/v1/vertxapp/records").handler(this::getAllPatchingProjects);
		
		int port = DEFAULT_HTTP_SERVER_PORT;
		
		server.requestHandler(router::accept).listen(port, ar -> {
			if (ar.succeeded()) {
				LOGGER.info("Listening at port " + port);
				startFuture.complete();
			} else {
				LOGGER.error("Failed to start http service verticle, cause: " + ar.cause());
				startFuture.fail(ar.cause());
			}
		});
		
		// Get service instance
		dbService = DatabaseService.createProxy(vertx, MainPatchFactoryVerticle.CONFIG_QUEUE);
	}
	
	private void getVersion(RoutingContext context) {
		context.response().setStatusCode(200).end(new JsonObject()
				.put("version", "1.0.0")
				.put("name", "vertx app service")
				.put("copyright", "Apahce 2.0 License")
				.encode());
	}
	
	private void createPatchingProject(RoutingContext context) {
		
		
		
		JsonObject patchingProjectDefinition = context.getBodyAsJson();
		
		LOGGER.info("--->", patchingProjectDefinition);
		
		dbService.createPatchingProject(patchingProjectDefinition,  reply -> {
			if (reply.succeeded()) {
				context.response().setStatusCode(201);
				context.response().putHeader("Content-Type", "applicaiton/json");
				LOGGER.info("-->->", reply.result().encode());
				context.response().end(reply.result().encode());
			} else {
				LOGGER.error(reply.cause());
				context.response().setStatusCode(500);
				context.response().putHeader("Content-Type", "application/json");
				context.response()
						.end(new JsonObject().put("success", false).put("error", reply.cause().getMessage()).encode());
			}
		});
	}
	
	private void getAllPatchingProjects(RoutingContext context) {
		//getCount++;
		dbService.getAllPatchingProjects(reply -> {
			if(reply.succeeded()) {
				//LOGGER.info("Request number : " + getCount);
				context.response().setStatusCode(200);
				context.response().putHeader("Content-Type", "applicaiton/json");
				context.response().end(reply.result().encode());
			} else {
				LOGGER.error(reply.cause());
				context.response().setStatusCode(500);
				context.response().putHeader("Content-Type", "application/json");
				context.response()
						.end(new JsonObject().put("success", false).put("error", reply.cause().getMessage()).encode());
			}
		});
	}
}
