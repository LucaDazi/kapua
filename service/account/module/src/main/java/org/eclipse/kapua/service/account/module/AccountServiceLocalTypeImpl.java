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

import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.commons.service.module.CommonsBinder;
import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.KapuaService;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;

public class AccountServiceLocalTypeImpl implements AccountServiceLocalType {

    private AccountServiceProvider provider;

    public AccountServiceLocalTypeImpl() {
        AccountServiceBinder binder = new AccountServiceBinder(CommonsBinder.getInstance());
        this.provider = new AccountServiceProvider(binder);
    }

    @Override
    public ServiceReference get(Vertx vertx, ServiceDiscovery discovery, Record record, JsonObject configuration) {        
        String className = record.getName();
        Object service = provider.getInstance(className);
        if (service instanceof KapuaService) {
            return new AccountServiceLocalReference(vertx, discovery, record, configuration, KapuaService.class.cast(service));
        } 
        if (service instanceof KapuaObjectFactory) {
            return new AccountObjectFactoryLocalReference(vertx, discovery, record, configuration, KapuaObjectFactory.class.cast(service));
        }

        throw KapuaRuntimeException.internalError("No reference matching the provided record with name: " + record.getName());
    }

    @Override
    public String name() {
        return TYPE;
    }
}
