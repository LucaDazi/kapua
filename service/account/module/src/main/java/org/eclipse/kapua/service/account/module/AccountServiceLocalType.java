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

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.spi.ServiceType;

public interface AccountServiceLocalType extends ServiceType {

    public static String TYPE = "account-service-local";

    public static Record createRecord(final String serviceName, final String location, final JsonObject metadata) {
        Record record = new Record()
                .setName(serviceName)
                .setType(TYPE)
                .setLocation(new JsonObject().put("endpoint", location))
                .setMetadata(metadata);

        return record;
    }
}
