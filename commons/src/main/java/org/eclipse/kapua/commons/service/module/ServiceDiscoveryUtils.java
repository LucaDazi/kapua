/*******************************************************************************
 * Copyright (c) 2011, 2017 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.commons.service.module;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.eclipse.kapua.service.KapuaServiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;

public class ServiceDiscoveryUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscoveryUtils.class);

    private ServiceDiscoveryUtils() {}

    public static ServiceReference getReference(ServiceDiscovery discovery, Function<Record,Boolean> funct) 
            throws Exception {

        try {
            ServiceReference reference = null;

            // Counts the number of queries to ServiceDiscovery
            int attempts = 0;

            // Loop until the reference is found or the maximum attempts
            // have been reached
            while (reference == null && attempts <= 10) {

                // Query the ServiceDiscovery.
                // Returns a future that has to be checked later on.
                CompletableFuture<Record> futureRecord = new CompletableFuture<Record>();

                discovery.getRecord(funct, res -> {
                    if (res.succeeded()) {
                        futureRecord.complete(res.result());
                    } else {
                        futureRecord.completeExceptionally(res.cause());
                    }
                });

                // Wait until completion or timeout expiration
                Record serviceRecord = null;
                try {
                    serviceRecord = futureRecord.get(1000, TimeUnit.MILLISECONDS);
                    if (serviceRecord != null) {
                        reference = discovery.getReference(serviceRecord);
                    } else {
                        Thread.sleep(1000);
                        attempts++;
                    }
                } catch (TimeoutException e) {
                    attempts++;
                }
            }

            if (attempts > 10) {
                throw new TimeoutException("Timeout expired searching for record");
            }

            return reference;

        } catch (TimeoutException|InterruptedException|ExecutionException e) {
            throw new Exception(e);
        }
    }

    public static Future<Record> publish(ServiceDiscovery discovery, Record record) {

        Objects.requireNonNull(discovery);
        Objects.requireNonNull(record);

        Future<Record> publishedRecord = Future.future();

        discovery.publish(record, ar -> {
            if (ar.succeeded()) {
                publishedRecord.complete(ar.result());
                LOGGER.debug("Record successfully published {}", ar.result().getName());
            } else {
                publishedRecord.fail(ar.cause());
                LOGGER.warn("Failed to publish record", ar.cause());
            }
        });

        return publishedRecord;
    }

    public static Future<Record> unpublish(ServiceDiscovery discovery, Record record) {

        Objects.requireNonNull(discovery);
        Objects.requireNonNull(record);

        Future<Record> future = Future.future();
        discovery.unpublish(record.getRegistration(),
                ar -> {
                    if (ar.succeeded()) {
                        future.complete();
                        LOGGER.debug("Record successfully unpublished {}", record.getName());
                    } else {
                        future.fail(ar.cause());
                        LOGGER.warn("Failed to unbuplish record", ar.cause());
                    }
                });     
        return future;
    }

    public static <T extends KapuaServiceModule> void startModule(Vertx vertx, ServiceDiscovery discovery, AsyncResult<Record> publishedRecordSearch, Class<T> moduleClazz) {
        // If a matching record has been found proceed starting the module otherwise sentout a message
        if (publishedRecordSearch.succeeded()) {
            // The code to be executed is not reactive, use executeBlocking
            vertx.executeBlocking(blockingExecution -> {
                ServiceReference reference = discovery.getReference(publishedRecordSearch.result());
                T module = reference.getAs(moduleClazz);
                if (module == null) {
                    blockingExecution.fail(new NullPointerException("Null module: AccountServiceModule"));
                }
                // Start the module and complete the execution
                module.start();
                blockingExecution.complete();
            }, execResult -> {
                if (execResult.succeeded()) {
                    LOGGER.info("Module started: {}", moduleClazz.getName());
                } else {
                    LOGGER.warn("Failed to start module: " + moduleClazz.getName(), execResult.cause());
                }
            });
        } else {
            LOGGER.warn("Failed to start module: " + moduleClazz.getName(), publishedRecordSearch.cause());
        }       
    }
}
