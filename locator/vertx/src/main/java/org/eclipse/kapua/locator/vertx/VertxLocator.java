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
package org.eclipse.kapua.locator.vertx;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.KapuaService;

import io.vertx.core.Vertx;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;

/**
 * 
 */
public class VertxLocator extends KapuaLocator {

    private Vertx vertx;

    public VertxLocator(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public <S extends KapuaService> S getService(Class<S> serviceClass) {
        return getInstance(vertx, serviceClass);
    }

    @Override
    public <F extends KapuaObjectFactory> F getFactory(Class<F> factoryClass) {
        return getInstance(vertx, factoryClass);
    }

    @Override
    public List<KapuaService> getServices() {
        // TODO Auto-generated method stub
        return null;
    }

    private static <T> T getInstance(Vertx vertx, Class<T> clazz) {

        ServiceDiscovery discovery = null;
        ServiceReference reference = null;

        try {
            discovery = ServiceDiscovery.create(vertx);
            reference = null;

            reference = getReference(discovery, record -> {
                return record.getName().equals(clazz.getName());
            });
            return reference.getAs(clazz);
        } catch (Exception e) {
            throw KapuaRuntimeException.internalError(e);
        } finally {
            if (reference != null) {
                discovery.release(reference);
            }
            if (discovery != null) {
                discovery.close();
            }
        }
    }

    private static ServiceReference getReference(ServiceDiscovery discovery, Function<Record, Boolean> funct)
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
                Record serviceRecord = futureRecord.get(1000, TimeUnit.MILLISECONDS);
                if (serviceRecord != null) {
                    reference = discovery.getReference(serviceRecord);
                } else {
                    Thread.sleep(1000);
                    attempts++;
                }
            }

            if (attempts > 10) {
                throw new TimeoutException("Timeout expired searching for record");
            }

            return reference;

        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            throw new Exception(e);
        }
    }
}
