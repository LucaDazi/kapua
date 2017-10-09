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

import com.google.inject.Injector;

public abstract class AbstractServiceProvider {

    public <T> T getInstance(Class<T> clazz) {
        return getInjector().getInstance(clazz);
    }

    public <T> T getInstance(String className) {
        Class<T> clazz = (Class<T>) getClassFromName(className);
        return getInjector().getInstance(clazz);
    }

    protected abstract Injector getInjector();

    protected abstract Class<?> getClassFromName(String className);
}
