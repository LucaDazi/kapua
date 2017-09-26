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

import org.eclipse.kapua.commons.service.module.KapuaServiceModule;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.account.internal.AccountServiceImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import io.vertx.core.AbstractVerticle;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;

/**
 * 
 *
 */
public class AccountServiceModule extends AbstractVerticle implements KapuaServiceModule {

    @Override
    public void start() throws Exception {
        super.start();

        Injector injector = Guice.createInjector( new AbstractModule() {
            @Override
            protected void configure() {
                bind(AccountService.class).to(AccountServiceImpl.class);                
            } 
        });
        AccountServiceLocalTypeImpl.setInjector(injector);

        ServiceDiscovery discovery = ServiceDiscovery.create(vertx);
        Record serviceRecord = AccountServiceLocalType.createRecord(AccountService.class.getName());
        discovery.publish(serviceRecord, ar -> {
            if (ar.succeeded()) {
                System.out.println("Service alfa successfully published!");

            } else {
                System.err.println("Service alfa has not been published: something went wrong...");

            }
        });
    }

    @Override
    public void stop() throws Exception {
        // TODO Auto-generated method stub
        System.err.println("AccountServiceModule.stop");
    }
}
