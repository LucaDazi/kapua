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


import org.apache.qpid.proton.message.Message;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.connector.AbstractConnectorVerticle;
import org.eclipse.kapua.connector.KapuaConnectorException;
import org.eclipse.kapua.connector.TelemetryProvider;
import org.eclipse.kapua.connector.Utility;
import org.eclipse.kapua.connector.amqp.settings.ConnectorSettings;
import org.eclipse.kapua.connector.amqp.settings.ConnectorSettingsKey;
import org.eclipse.kapua.locator.KapuaLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.proton.ProtonClient;
import io.vertx.proton.ProtonConnection;
import io.vertx.proton.ProtonDelivery;
import io.vertx.proton.ProtonMessageHandler;

public class AmqpConnectorVerticle extends AbstractConnectorVerticle {

    protected final static Logger logger = LoggerFactory.getLogger(AmqpConnectorVerticle.class);

    private final static String QUEUE_PATTERN = "Consumer.%s.VirtualTopic.>";// Consumer.*.VirtualTopic.>

    private ProtonClient client;

    // providers
    private MessageHandler messageHandler;
    private ConnectionHandler connectionHandler;
    private String brokerName;
    private int brokerPort;

    public AmqpConnectorVerticle() {
        logger.info("Initializing providers...");
        logger.info("             telemetry provider...");
        brokerName = ConnectorSettings.getInstance().getString(ConnectorSettingsKey.BROKER_NAME);
        brokerPort = ConnectorSettings.getInstance().getInt(ConnectorSettingsKey.BROKER_PORT);
        messageHandler = new MessageHandler();
        connectionHandler = new ConnectionHandler(brokerName, brokerPort);
        telemetryProvider = KapuaLocator.getInstance().getService(TelemetryProvider.class);
        logger.info("             telemetry provider... DONE");
        logger.info("Initializing providers... DONE");
    }

    @Override
    public void start() throws Exception {
        logger.info("Connectong to broker {}:{}...", brokerName, brokerPort);
        client = ProtonClient.create(vertx);
        client.connect(
                brokerName,
                brokerPort,
                ConnectorSettings.getInstance().getString(ConnectorSettingsKey.BROKER_USERNAME),
                ConnectorSettings.getInstance().getString(ConnectorSettingsKey.BROKER_PASSWORD),
                connectionHandler);
    }

    @Override
    public void stop() throws Exception {
    }

    private void registerConsumer(ProtonConnection connection) {
        String queue = String.format(QUEUE_PATTERN,
                ConnectorSettings.getInstance().getString(ConnectorSettingsKey.BROKER_CLIENT_ID));
        logger.info("Register consumer for queue {}...", queue);
        connection.open();
        // The client ID is set implicitly into the queue subscribed
        connection.createReceiver(queue).handler(messageHandler).open();
        logger.info("Register consumer for queue {}... DONE", queue);
    }

    private void forwardMessageToKafka(byte[] data) {
        try {
            telemetryProvider.sendSynchronous(Utility.convertFromByte(data));
        } catch (KapuaException e) {
            logger.error("Cannot convert recevived message to KapuaMessage! Error: {}", e.getMessage(), e);
        }
    }

    private class MessageHandler implements ProtonMessageHandler {

        @Override
        public void handle(ProtonDelivery delivery, Message message) {
            try {
                forwardMessageToKafka(Utility.extractBytePayload(message.getBody()));
            }
            catch (KapuaConnectorException e) {
                //DO nothing
                logger.warn("Error sending message to kafka: {}", e.getMessage(), e);
            }
            // By default, the receiver automatically accepts (and settles) the delivery
            // when the handler returns, if no other disposition has been applied.
            // To change this and always manage dispositions yourself, use the
            // setAutoAccept method on the receiver.
        }

    }

    private class ConnectionHandler implements Handler<AsyncResult<ProtonConnection>> {

        private String brokerName;
        private int brokerPort;

        public ConnectionHandler(String brokerName, int brokerPort) {
            this.brokerName = brokerName;
            this.brokerPort = brokerPort;
        }

        @Override
        public void handle(AsyncResult<ProtonConnection> event) {
            if (event.succeeded()) {
                // register the message consumer
                logger.info("Connecting to broker {}:{}... Creating receiver...", brokerName, brokerPort);
                registerConsumer(event.result());
                logger.info("Connecting to broker {}:{}... Creating receiver... DONE", brokerName, brokerPort);
            } else {
                logger.error("Cannot register kafka consumer! ", event.cause().getCause());
            }
        }

    }
}
