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

import org.eclipse.kapua.locator.ServiceProvider;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class AccountServiceProvider implements ServiceProvider {

    AccountServiceBinder binder;
    Injector injector;

    public AccountServiceProvider(AccountServiceBinder binder) {
        this.binder = binder;
        this.injector = Guice.createInjector(binder);
    }

    @Override
    public <T> T getInstance(Class<T> clazz) {

        if (AccountServiceBinder.class.equals(clazz)) {
            return clazz.cast(this.binder);
        }
        return injector.getInstance(clazz);
    }

}
