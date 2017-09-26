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

import org.eclipse.kapua.service.KapuaService;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.AbstractServiceReference;

public class AccountServiceReference extends AbstractServiceReference<KapuaService> {

    private KapuaService kapuaService;

    public AccountServiceReference(Vertx vertx, ServiceDiscovery discovery, Record record, JsonObject configuration, KapuaService kapuaService) {
        super(vertx, discovery, record);
        this.kapuaService = kapuaService;
    }

    @Override
    protected KapuaService retrieve() {
        return kapuaService;
    }

    @Override
    public void close() {
        // add your code here, if ever your service object needs cleanup
        super.close();
    }
}
