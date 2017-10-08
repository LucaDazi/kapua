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
package org.eclipse.kapua.service.account.module;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.commons.service.module.ServiceDiscoveryUtils;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;

/**
 * 
 *
 */
public class AccountServiceModule extends AbstractVerticle {

    private ServiceDiscovery discovery;
    private List<Future<Record>> publishedRecordFutures;

    public AccountServiceModule() {
        publishedRecordFutures = new ArrayList<Future<Record>>();
    }

    @Override
    public void start() throws Exception {
        super.start();

        discovery = ServiceDiscovery.create(vertx);

        String serviceName = AccountService.class.getName();
        Record serviceRecord = AccountServiceLocalType.createRecord(serviceName, "local", new JsonObject());
        publishedRecordFutures.add(ServiceDiscoveryUtils.publish(discovery, serviceRecord));

        String factoryName = AccountFactory.class.getName();
        Record factoryRecord = AccountServiceLocalType.createRecord(factoryName, "local", new JsonObject());
        publishedRecordFutures.add(ServiceDiscoveryUtils.publish(discovery, factoryRecord));
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        for(Future<Record> recordFuture:publishedRecordFutures) {
            if (recordFuture.succeeded()) {
                ServiceDiscoveryUtils.unpublish(discovery, recordFuture.result());
            }
        }
        publishedRecordFutures.clear();
        if (discovery != null) {
            discovery.close();
        }
    }
}
