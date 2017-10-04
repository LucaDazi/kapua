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

public interface AccountServiceProviderType extends ServiceType {

    public static String TYPE = "account-service-provider";

    public static Record createRecord(final String serviceName, final JsonObject metadata) {
        Record record = new Record()
                .setName(serviceName)
                .setType(TYPE)
                .setMetadata(metadata);

        return record;
    }
}
