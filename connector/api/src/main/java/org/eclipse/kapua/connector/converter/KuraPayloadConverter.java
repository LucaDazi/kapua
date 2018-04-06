/*******************************************************************************
 * Copyright (c) 2018 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.connector.converter;

import java.util.Map;

import org.eclipse.kapua.connector.Converter;
import org.eclipse.kapua.message.transport.KapuaTransportMessage;

public class KuraPayloadConverter implements Converter<byte[],KapuaTransportMessage> {

    @Override
    public KapuaTransportMessage convert(Map<String, Object> properties, byte[] message) {
        // TODO Auto-generated method stub
        return null;
    }
}
