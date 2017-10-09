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

import org.eclipse.kapua.service.KapuaServiceModule;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.AbstractServiceReference;

public class KapuaServiceModuleLocalReference extends AbstractServiceReference<KapuaServiceModule> {

    private KapuaServiceModule serviceModule;

    public KapuaServiceModuleLocalReference(Vertx vertx, ServiceDiscovery discovery, Record record, JsonObject configuration, KapuaServiceModule serviceModule) {
        super(vertx, discovery, record);
        this.serviceModule = serviceModule;
    }

    @Override
    protected KapuaServiceModule retrieve() {
        return serviceModule;
    }

    @Override
    public void close() {
        super.close();
    }
}
