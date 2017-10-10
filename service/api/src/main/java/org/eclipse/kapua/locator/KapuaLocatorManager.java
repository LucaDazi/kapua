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
package org.eclipse.kapua.locator;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.kapua.KapuaRuntimeException;

public abstract class KapuaLocatorManager {

    private static final String ALLOWE_TO_GET = KapuaLocator.class.getName();

    private static Map<String, KapuaLocator> instances = new ConcurrentHashMap<String, KapuaLocator>();

    private KapuaLocatorManager() {
    }

    public static void registerInstance(KapuaLocator locator) {
        Objects.requireNonNull(locator);
        instances.put(locator.getClass().getName(), locator);
    }

    public static KapuaLocator getInstance(String instanceName) {
        Objects.requireNonNull(instanceName);
        if (ALLOWE_TO_GET.equals(Thread.currentThread().getStackTrace()[2].getClassName())) {
            KapuaRuntimeException.internalError("Not allowed to execute method");
        }
        return instances.get(instanceName);
    }
}
