package com.motadata;

import com.motadata.api.ApiEngine;
import com.motadata.constants.Constants;
import com.motadata.database.ConfigDB;
import com.motadata.engine.Scheduler;
import com.motadata.engine.Worker;
import com.motadata.utils.Config;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap
{
    public static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args)
    {
        logger.info("Application started");

        var vertx = Vertx.vertx();


        try
        {
            ConfigDB.createDatabase(Constants.DISCOVERY);

            ConfigDB.createDatabase(Constants.CREDENTIAL);

            ConfigDB.createDatabase(Constants.PROVISION);

            vertx.deployVerticle(Worker.class.getName(), new DeploymentOptions().setThreadingModel(ThreadingModel.WORKER))

                    .compose(id -> vertx.deployVerticle(Scheduler.class.getName()))

                    .compose(id -> vertx.deployVerticle(ApiEngine.class.getName()))

                    .onSuccess(handler-> logger.info("Application verticals deployed"))

                    .onFailure(handler -> logger.error("unable to deploy vertical"));

        } catch(Exception exception)
        {
            logger.error("deploy exception {}", exception.getMessage());

            logger.error("Application stopped");
        }

    }
}
