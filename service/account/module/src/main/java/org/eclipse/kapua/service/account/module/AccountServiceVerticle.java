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
import org.eclipse.kapua.service.KapuaServiceModule;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.account.internal.AccountServiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;

/**
 * 
 *
 */
public class AccountServiceVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceVerticle.class);

    private ServiceDiscovery discovery;
    private List<Future<Record>> publishedRecordFutures;

    public AccountServiceVerticle() {
        publishedRecordFutures = new ArrayList<Future<Record>>();
    }

    @Override
    public void start() throws Exception {
        super.start();

        discovery = ServiceDiscovery.create(vertx);

        String address = this.config().getString("address-local");
        String moduleName = KapuaServiceModule.class.getName();
        Record moduleRecord = AccountServiceLocalType.createRecord(moduleName, address, new JsonObject());
        Future<Record> moduleRecordPublishing = ServiceDiscoveryUtils.publish(discovery, moduleRecord);
        publishedRecordFutures.add(moduleRecordPublishing);

        String serviceName = AccountService.class.getName();
        Record serviceRecord = AccountServiceLocalType.createRecord(serviceName, address, new JsonObject());
        publishedRecordFutures.add(ServiceDiscoveryUtils.publish(discovery, serviceRecord));

        String factoryName = AccountFactory.class.getName();
        Record factoryRecord = AccountServiceLocalType.createRecord(factoryName, address, new JsonObject());
        publishedRecordFutures.add(ServiceDiscoveryUtils.publish(discovery, factoryRecord));

        // Start modules
        moduleRecordPublishing.setHandler(publishResult -> {
            discovery.getRecord(publishedRecord -> {
                // Search for a matching module record
                return publishedRecord.getName().equals(moduleName) && publishedRecord.getType().equals(AccountServiceLocalType.TYPE);
            }, recordSearchResult -> {
                // Start the module
                ServiceDiscoveryUtils.startModule(vertx, discovery, recordSearchResult, AccountServiceModule.class);
            });
        });
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
