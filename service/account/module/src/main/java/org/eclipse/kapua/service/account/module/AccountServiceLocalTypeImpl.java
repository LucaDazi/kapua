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

import org.eclipse.kapua.commons.service.module.AbstractModuleLocalType;
import org.eclipse.kapua.commons.service.module.AbstractServiceProvider;
import org.eclipse.kapua.commons.service.module.CommonsBinder;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class AccountServiceLocalTypeImpl extends AbstractModuleLocalType implements AccountServiceLocalType {

    private AbstractServiceProvider provider;

    public AccountServiceLocalTypeImpl() {
        final AccountServiceBinder binder = new AccountServiceBinder(CommonsBinder.getInstance());
        provider = new AbstractServiceProvider() {

            private Injector injector = Guice.createInjector(binder);

            @Override
            protected Injector getInjector() {
                return injector;
            }

            @Override
            protected Class<?> getClassFromName(String className) {
                return binder.getExportedClass(className);
            }
        };
    }

    @Override
    public String name() {
        return TYPE;
    }

    protected AbstractServiceProvider getProvider() {
        return provider;
    }
}
