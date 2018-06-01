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

import io.vertx.core.Future;

import org.eclipse.kapua.converter.Converter;
import org.eclipse.kapua.converter.KapuaConverterException;
import org.eclipse.kapua.processor.KapuaProcessorException;
import org.eclipse.kapua.processor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;

/**
 * 
 * @param <T> Transport message type
 * @param <A> Application message type
 * @param <P> Processor message type
 */
public abstract class AbstractConnectorVerticle<T, A, P> extends AbstractVerticle {

    protected final static Logger logger = LoggerFactory.getLogger(AbstractConnectorVerticle.class);

    protected Converter<T, A> transportConverter;
    protected Converter<A, P> applicationConverter;
    protected Processor<P> processor;

    protected AbstractConnectorVerticle(Converter<T, A> transportConverter, Converter<A, P> applicationConverter, Processor<P> processor) {
        this.transportConverter = transportConverter;
        this.applicationConverter = applicationConverter;
        this.processor = processor;
    }

    protected AbstractConnectorVerticle(Converter<A, P> applicationConverter, Processor<P> processor) {
         this(null, applicationConverter, processor);
    }

    protected AbstractConnectorVerticle(Processor<P> processor) {
        this(null, processor);
    }

    protected abstract void startInternal(Future<Void> startFuture) throws KapuaConnectorException;

    protected abstract void stopInternal(Future<Void> stopFuture) throws KapuaConnectorException;

    public void start(Future<Void> startFuture) throws KapuaConnectorException {
        try {
            // Start subclass
            startInternal(startFuture);

            //Start processor
            logger.info("Invoking processor.start...");
            processor.start();

            startFuture.complete();
        } catch (Exception ex) {
            logger.warn("Verticle start failed", ex);
            startFuture.fail(ex);
        }
    }

    @SuppressWarnings("unchecked")
    protected void handleMessage(MessageContext<T> message) throws KapuaConnectorException, KapuaConverterException, KapuaProcessorException {
        MessageContext<A> applicationMessage = null;
        if (transportConverter != null) {
            applicationMessage = transportConverter.convert(message);
        }
        else {
            applicationMessage = (MessageContext<A>) message;
        }
        MessageContext<P> convertedMessage = null;
        if (applicationConverter != null) {
            convertedMessage = applicationConverter.convert(applicationMessage);
        } else {
            convertedMessage = (MessageContext<P>) applicationMessage;
        }

        processor.process(convertedMessage);
    }

    public void stop(Future<Void> stopFuture) throws KapuaConnectorException {
        try {
            // Stop subclass
            stopInternal(stopFuture);

            // Stop processor
            logger.info("Invoking processor.stop...");
            processor.stop();

            stopFuture.complete();
        } catch (Exception ex) {
            logger.warn("Verticle stop failed", ex);
            stopFuture.fail(ex);
        }
    }
}
