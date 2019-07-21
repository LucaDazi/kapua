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
package org.eclipse.kapua.processor.ksql;

import org.eclipse.kapua.connector.MessageContext;
import org.eclipse.kapua.message.KsqlKafkaMessage;
import org.eclipse.kapua.processor.KapuaProcessorException;
import org.eclipse.kapua.processor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;

public class KsqlProcessor implements Processor<KsqlKafkaMessage> {

    private static final Logger logger = LoggerFactory.getLogger(KsqlProcessor.class);

    @Override
    public void start(Future<Void> startFuture) {
    	//Instantiate Vertex Kafka Producer
        startFuture.complete();
    }

    @Override
    public void process(MessageContext<KsqlKafkaMessage> message) throws KapuaProcessorException {
        
    }

    @Override
    public void stop(Future<Void> stopFuture) {
        // nothing to do
        stopFuture.complete();
    }

}
