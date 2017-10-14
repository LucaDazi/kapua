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
package org.eclipse.kapua.service.account.module.vertx;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.commons.service.module.CommonsBinder;
import org.eclipse.kapua.service.KapuaServiceModule;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.account.internal.AccountFactoryImpl;
import org.eclipse.kapua.service.account.internal.AccountServiceImpl;
import org.eclipse.kapua.service.account.internal.AccountServiceModule;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class AccountServiceBinder extends AbstractModule {

    private CommonsBinder commonsBinder;
    private Map<String, Class<?>> exportedObjects;

    public AccountServiceBinder(CommonsBinder binder) {
        this.commonsBinder = binder;
        this.exportedObjects = new HashMap<String, Class<?>>();
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getExportedClass(String className) {
        // Safe conversion per construction of exportedObjects
        return (Class<T>) exportedObjects.get(className);
    }

    @Override
    protected void configure() {

        install(commonsBinder);

        bind(KapuaServiceModule.class).to(AccountServiceModule.class);
        exportedObjects.put(KapuaServiceModule.class.getName(), AccountServiceModule.class);

        bind(AccountService.class).to(AccountServiceImpl.class).in(Singleton.class);
        exportedObjects.put(AccountService.class.getName(), AccountService.class);

        bind(AccountFactory.class).to(AccountFactoryImpl.class).in(Singleton.class);;
        exportedObjects.put(AccountFactory.class.getName(), AccountFactory.class);
    } 
}
