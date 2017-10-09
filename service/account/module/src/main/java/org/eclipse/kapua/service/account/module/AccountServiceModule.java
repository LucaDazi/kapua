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

import org.eclipse.kapua.service.KapuaServiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountServiceModule implements KapuaServiceModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceModule.class);

    @Override
    public void start() {
        LOGGER.info(">>>>> STARTING {} .... ", this.getClass().getSimpleName());       
        LOGGER.info(">>>>> STARTING {} .... DONE", this.getClass().getSimpleName());       
    }

    @Override
    public void stop() {
        LOGGER.info(">>>>> STOPPING {} .... ", this.getClass().getSimpleName());       
        LOGGER.info(">>>>> STOPPING {} .... DONE", this.getClass().getSimpleName());       
    }
}
