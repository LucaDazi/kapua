/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua;

public class KapuaLocatorExistsException extends KapuaException {

    private static final long serialVersionUID = 5110597972514998086L;

    private String name;

    /**
     * Constructor for the {@link KapuaLocatorExistsException} taking in the duplicated name.
     * 
     * @param t
     * @param name
     *            the name that conflicts with an existing entry
     */
    public KapuaLocatorExistsException(Throwable t, String name) {
        super(KapuaErrorCodes.LOCATOR_ALREADY_EXISTS, t);
        this.name = name;
    }

    /**
     * Constructor for the {@link KapuaLocatorExistsException} taking in the duplicated name.
     * 
     * @param name
     *            the name that conflicts with an existing entry
     */
    public KapuaLocatorExistsException(String name) {
        super(KapuaErrorCodes.LOCATOR_ALREADY_EXISTS);
        this.name = name;
    }

    public String getName( ) {
        return name;
    }
}