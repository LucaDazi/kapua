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

import org.eclipse.kapua.locator.ServiceProvider;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class CommonsProvider implements ServiceProvider {

    private static CommonsProvider instance;

    private CommonsBinder binder;
    private Injector injector;

    static {
        instance = new CommonsProvider();
    }

    private CommonsProvider() {
        this.binder = CommonsBinder.getInstance();
        this.injector = Guice.createInjector(binder);
    }

    public static CommonsProvider getInstance() {
        return instance;
    }

    public <T> T getInstance(Class<T> clazz) {
        return injector.getInstance(clazz);
    }

    @Override
    public <T> T getInstance(String className) {
        Class<T> clazz = binder.getExportedClass(className);
        return injector.getInstance(clazz);
    }

}
