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
package org.eclipse.kapua.converter.kafka;

import org.eclipse.kapua.connector.MessageContext;
import org.eclipse.kapua.converter.Converter;
import org.eclipse.kapua.converter.KapuaConverterException;
import org.eclipse.kapua.message.transport.TransportMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaPayloadConverter implements Converter<byte[], TransportMessage> {

    protected final static Logger logger = LoggerFactory.getLogger(KafkaPayloadConverter.class);

    @Override
    public MessageContext<TransportMessage> convert(MessageContext<byte[]> message) throws KapuaConverterException {

    	//TODO
    	return new MessageContext<TransportMessage>(null, message.getProperties());
    }
}
