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
package org.eclipse.kapua.connector.amqp;

import org.eclipse.kapua.connector.Processor;
import org.eclipse.kapua.connector.converter.KuraPayloadConverter;
import org.eclipse.kapua.message.transport.KapuaTransportMessage;

import io.vertx.core.Vertx;

public class AmqpConnector {

    private AmqpConnector() {
    }

    public static void main(String argv[]) {

        KuraPayloadConverter converter = new KuraPayloadConverter();
        Processor<KapuaTransportMessage> processor = null; // TODO
        AmqpConnectorVerticle amqpConnVrtcl = new AmqpConnectorVerticle(converter, processor);

        Vertx vertx = Vertx.vertx(); // TODO options
        vertx.deployVerticle(amqpConnVrtcl);
    }
}
