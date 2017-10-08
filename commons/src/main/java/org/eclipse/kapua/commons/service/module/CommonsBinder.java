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

import java.util.HashMap;
import java.util.Map;

import com.google.inject.AbstractModule;

public class CommonsBinder extends AbstractModule {

    private Map<String, Class<?>> exportedObjects;

    private static CommonsBinder instance;

    static {
        instance = new CommonsBinder();
    }

    private CommonsBinder() {
        this.exportedObjects = new HashMap<String, Class<?>>();
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getExportedClass(String className) {
        // Cast safe by construction of exportedObjects
        return (Class<T>) exportedObjects.get(className);
    }

    public static CommonsBinder getInstance() {
        return instance;
    }

    @Override
    protected void configure() {
    } 
}
