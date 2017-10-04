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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;

/**
 * 
 *
 */
public class CommonsModule extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonsModule.class);

    private ServiceDiscovery discovery;
    private Record publishedRecord;

    @Override
    public void start() throws Exception {
        super.start();

        discovery = ServiceDiscovery.create(vertx);

        JsonObject metadata = new JsonObject()
                .put("provided-services", new JsonArray()
                        .add(CommonsBinder.class.getName()));

        Record serviceRecord = CommonsProviderType.createRecord(CommonsModule.class.getName(), metadata);

        discovery.publish(serviceRecord, ar -> {
            if (ar.succeeded()) {
                publishedRecord = ar.result();
                LOGGER.warn("Successfull publication of record {}", serviceRecord.getName());
            } else {
                LOGGER.warn("Something wrong with the publication of record {}", serviceRecord.getName(), ar.cause());
            }
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (publishedRecord != null) {
            discovery.unpublish(publishedRecord.getRegistration(),
                    ar -> {
                        if (!ar.succeeded()) {
                            LOGGER.warn("Something wrong with the publication of record {}", publishedRecord.getName(), ar.cause());
                        }
                    });
        }
        if (discovery != null) {
            discovery.close();
        }
    }
}
