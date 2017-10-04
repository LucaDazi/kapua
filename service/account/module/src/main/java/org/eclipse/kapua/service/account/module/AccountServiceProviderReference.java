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

import org.eclipse.kapua.commons.service.module.CommonsBinder;
import org.eclipse.kapua.commons.service.module.ServiceDiscoveryUtils;
import org.eclipse.kapua.locator.ServiceProvider;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.types.AbstractServiceReference;

public class AccountServiceProviderReference extends AbstractServiceReference<ServiceProvider> {

    private ServiceDiscovery discovery;
    private ServiceProvider provider;
    private AccountServiceBinder binder;

    public AccountServiceProviderReference(Vertx vertx, ServiceDiscovery discovery, Record record, JsonObject configuration) {
        super(vertx, discovery, record);
        this.discovery = discovery;
    }

    @Override
    protected ServiceProvider retrieve() {
        return getOrCreate();
    }

    @Override
    public void close() {
        super.close();
    }

    private ServiceProvider getOrCreate() {
        if (provider == null) {

            ServiceReference reference = null;

            try {
                reference = ServiceDiscoveryUtils.getReference(discovery, "", CommonsBinder.class.getName());
                ServiceProvider serviceProvider = reference.get();

                binder = new AccountServiceBinder(serviceProvider.getInstance(CommonsBinder.class));
                provider = new AccountServiceProvider(binder);

            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (reference != null) {
                    discovery.release(reference);
                }
            }
        }
        return provider;
    }
}
