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

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.KapuaService;

import io.vertx.core.Future;
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

        final S service;
        Future<Record> futureRecord = Future.future();
        ServiceDiscovery discovery = ServiceDiscovery.create(vertx);
        discovery.getRecord(r -> {
            System.err.println("Filtered record name: " + r.getName());
            return serviceClass.getName().equals(r.getName());
        }, futureRecord);

        System.err.println("getService - waiting for complete...");
        while (!futureRecord.isComplete()) {
        }

        if (futureRecord.succeeded()) {
            Record serviceRecord = futureRecord.result();
            System.err.println("getService - serviceRecord: " + serviceRecord);
            if (serviceRecord != null) {
                ServiceReference reference = discovery.getReference(serviceRecord);
                service = reference.get();
            } else {
                service = null;
            }
        } else {
            service = null;
        }
        return service;
    }

    @Override
    public <F extends KapuaObjectFactory> F getFactory(Class<F> factoryClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<KapuaService> getServices() {
        // TODO Auto-generated method stub
        return null;
    }
}
