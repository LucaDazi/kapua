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
package org.eclipse.kapua.connector;

import java.util.Map;

import io.vertx.core.AbstractVerticle;

public abstract class AbstractConnectorVerticle<S,T> extends AbstractVerticle {

    private Converter<S,T> converter;
    private Processor<T> processor;

    AbstractConnectorVerticle(Converter<S,T> converter,
            Processor<T> processor) {
        this.converter = converter;
        this.processor = processor;
    }

    AbstractConnectorVerticle(Processor<T> processor) {
        this(null, processor);
    }

    public void start() throws Exception {
        processor.start();
    }

    @SuppressWarnings("unchecked")
    public void handleMessage(Map<String,Object> properties, S message) throws KapuaConnectorException {

        T convertedMessage = null;
        if (converter != null) {
            convertedMessage = converter.convert(properties, message);
        } else {
            convertedMessage = (T) message;
        }

        processor.process(convertedMessage);
    }

    public void stop() throws Exception {
        processor.stop();
    }
}
