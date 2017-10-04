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
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.account.internal.AccountFactoryImpl;
import org.eclipse.kapua.service.account.internal.AccountServiceImpl;

import com.google.inject.AbstractModule;

public class AccountServiceBinder extends AbstractModule {

    private CommonsBinder commonsBinder;

    public AccountServiceBinder(CommonsBinder binder) {
        this.commonsBinder = binder;
    }

    @Override
    protected void configure() {

        install(commonsBinder);

        bind(AccountService.class).to(AccountServiceImpl.class);    
        bind(AccountFactory.class).to(AccountFactoryImpl.class);
    } 
}
