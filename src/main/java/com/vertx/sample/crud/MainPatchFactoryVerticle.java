/**
 * 
 */
package com.vertx.sample.crud;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.VertxOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * @author mthakare
 *
 */
public class MainPatchFactoryVerticle extends AbstractVerticle {

	public static Logger LOGGER = LoggerFactory.getLogger(MainPatchFactoryVerticle.class);
	public static final String CONFIG_QUEUE = "dbqueue";


	@Override
	public void start(Future<Void> startFuture) {
		Future<String> dbVerticleDeployment = Future.future();

		DeploymentOptions options = new DeploymentOptions().setConfig(config());
		
		
		vertx.deployVerticle("com.vertx.sample.crud.db.DBVerticle",
				options.setInstances(config().getInteger(ServiceConfiguration.DB_VERTICLE_INSTANCE_COUNT, 1)),
				dbVerticleDeployment.completer());

		dbVerticleDeployment.compose(id -> {

			Future<String> patchingServiceVerticleDeployment = Future.future();
			vertx.deployVerticle("com.vertx.sample.crud.http.HTTPServiceVerticle",
					options
							.setInstances(config().getInteger(ServiceConfiguration.HTTP_VERTICLE_INSTANCE_COUNT, 2)),
					patchingServiceVerticleDeployment.completer());

			return patchingServiceVerticleDeployment;
		}).setHandler(ar -> {
			if (ar.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(ar.cause());
			}
		});
	}
}
