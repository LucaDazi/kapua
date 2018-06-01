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
package org.eclipse.kapua.connector.activemq;

import io.vertx.core.Future;
import org.apache.qpid.proton.message.Message;

import org.eclipse.kapua.connector.AbstractConnectorVerticle;
import org.eclipse.kapua.connector.KapuaConnectorException;
import org.eclipse.kapua.connector.MessageContext;
import org.eclipse.kapua.connector.activemq.settings.ConnectorActiveMQSettings;
import org.eclipse.kapua.connector.activemq.settings.ConnectorActiveMQSettingsKey;
import org.eclipse.kapua.converter.Converter;
import org.eclipse.kapua.converter.KapuaConverterException;
import org.eclipse.kapua.message.transport.TransportMessage;
import org.eclipse.kapua.processor.KapuaProcessorException;
import org.eclipse.kapua.processor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AsyncResult;
import io.vertx.proton.ProtonClient;
import io.vertx.proton.ProtonConnection;
import io.vertx.proton.ProtonDelivery;

public class AmqpActiveMQConnectorVerticle extends AbstractConnectorVerticle<Message, byte[], TransportMessage> {

    protected final static Logger logger = LoggerFactory.getLogger(AmqpActiveMQConnectorVerticle.class);

    private final static String QUEUE_PATTERN = "queue://Consumer.%s.VirtualTopic.>";// Consumer.*.VirtualTopic.>

    private ProtonClient client;
    private ProtonConnection connection;

    // providers
    private String brokerHost;
    private int brokerPort;

    public AmqpActiveMQConnectorVerticle(Converter<Message, byte[]> transportConverter, Converter<byte[], TransportMessage> applicationConverter, Processor<TransportMessage> processor) {
        super(transportConverter, applicationConverter, processor);

        brokerHost = ConnectorActiveMQSettings.getInstance().getString(ConnectorActiveMQSettingsKey.BROKER_HOST);
        brokerPort = ConnectorActiveMQSettings.getInstance().getInt(ConnectorActiveMQSettingsKey.BROKER_PORT);
    }

    @Override
    public void startInternal(Future<Void> startFuture) throws KapuaConnectorException {
        // make sure connection is already closed
        closeConnection();

        logger.info("Connecting to broker {}:{}...", brokerHost, brokerPort);
        client = ProtonClient.create(vertx);
        client.connect(
                brokerHost,
                brokerPort,
                ConnectorActiveMQSettings.getInstance().getString(ConnectorActiveMQSettingsKey.BROKER_USERNAME),
                ConnectorActiveMQSettings.getInstance().getString(ConnectorActiveMQSettingsKey.BROKER_PASSWORD),
                this::handleProtonConnection);

        logger.info("Connecting to broker {}:{}... Done.", brokerHost, brokerPort);

    }

    @Override
    public void stopInternal(Future<Void> stopFuture) throws KapuaConnectorException {
        logger.info("Closing broker connection...");
        closeConnection();
    }

    private void closeConnection() {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    private void registerConsumer(ProtonConnection connection) {

        String queue = String.format(QUEUE_PATTERN,
                ConnectorActiveMQSettings.getInstance().getString(ConnectorActiveMQSettingsKey.BROKER_CLIENT_ID));
        logger.info("Register consumer for queue {}...", queue);
        connection.open();
        this.connection = connection;

        // The client ID is set implicitly into the queue subscribed
        connection.createReceiver(queue).handler(this::handleInternalMessage).open();
        logger.info("Register consumer for queue {}... DONE", queue);
    }

    /**
     * Callback for Connection Handler implementing interface Handler<AsyncResult<ProtonConnection>>
     *
     * @param event
     */
    public void handleProtonConnection(AsyncResult<ProtonConnection> event) {
        if (event.succeeded()) {
            // register the message consumer
            logger.info("Connecting to broker {}:{}... Creating receiver...", brokerHost, brokerPort);
            registerConsumer(event.result());
            logger.info("Connecting to broker {}:{}... Creating receiver... DONE", brokerHost, brokerPort);
        } else {
            logger.error("Cannot register kafka consumer! ", event.cause().getCause());
        }
    }

    /**
     * Callback for Proton Message receiver implementing interface ProtonMessageHandler
     *
     * @param delivery
     * @param message
     */
    public void handleInternalMessage(ProtonDelivery delidddvery, Message message) {
        try {
            super.handleMessage(new MessageContext<Message>(message));
        } catch (KapuaConnectorException | KapuaConverterException | KapuaProcessorException e) {
            logger.error("Exception while processing message: {}", e.getMessage(), e);
        }
    }

}
