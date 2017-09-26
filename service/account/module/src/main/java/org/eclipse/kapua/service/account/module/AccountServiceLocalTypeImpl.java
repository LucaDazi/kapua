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
import org.eclipse.kapua.service.KapuaService;

import com.google.inject.Injector;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;

public class AccountServiceLocalTypeImpl implements AccountServiceLocalType {

    private static Injector injector;    

    @Override
    public ServiceReference get(Vertx vertx, ServiceDiscovery discovery, Record record, JsonObject configuration) {
        JsonObject location = record.getLocation();
        String serviceClassName = location.getString(LOCATION_SERVICE_CLASSNAME);
        KapuaService kapuaService;
        try {
            kapuaService = (KapuaService) injector.getInstance(Class.forName(serviceClassName));
            return new AccountServiceReference(vertx, discovery, record, configuration, kapuaService);
        } 
        catch (ClassNotFoundException e) {
            throw KapuaRuntimeException.internalError(e);
        }        
    }

    @Override
    public String name() {
        return TYPE;
    }

    static void setInjector(Injector aInjector) {
        injector = aInjector;
    }
}
