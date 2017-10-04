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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;

public class ServiceDiscoveryUtils {

    private ServiceDiscoveryUtils() {}

    public static ServiceReference getReference(ServiceDiscovery discovery, String moduleName, String serviceName) 
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

                discovery.getRecord(record -> {
                    return record.getMetadata().containsKey("provided-services")
                            && record.getMetadata().getJsonArray("provided-services").contains(serviceName);
                }, res -> {
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
                throw new TimeoutException("Timeout expired for: " + serviceName);
            }

            return reference;

        } catch (TimeoutException|InterruptedException|ExecutionException e) {
            throw new Exception(e);
        }
    }
}
