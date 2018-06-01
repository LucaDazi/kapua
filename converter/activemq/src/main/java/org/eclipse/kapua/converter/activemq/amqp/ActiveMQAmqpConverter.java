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
package org.eclipse.kapua.converter.activemq.amqp;

import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.apache.qpid.proton.amqp.messaging.Section;
import org.apache.qpid.proton.message.Message;
import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.connector.MessageContext;
import org.eclipse.kapua.converter.Converter;
import org.eclipse.kapua.converter.KapuaConverterException;
import org.eclipse.kapua.message.transport.TransportMessageType;
import org.eclipse.kapua.message.transport.TransportQos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveMQAmqpConverter implements Converter<Message, byte[]> {

    protected final static Logger logger = LoggerFactory.getLogger(ActiveMQAmqpConverter.class);

    private final static String ACTIVEMQ_QOS = "ActiveMQ.MQTT.QoS";
    private final static String VIRTUAL_TOPIC_PREFIX = "topic://VirtualTopic.";
    private final static int VIRTUAL_TOPIC_PREFIX_LENGTH = VIRTUAL_TOPIC_PREFIX.length();

    @Override
    public MessageContext<byte[]> convert(MessageContext<Message> message) throws KapuaConverterException {
        Message tm = message.getMessage();
        // extract original MQTT topic
        String mqttTopic = tm.getProperties().getTo(); // topic://VirtualTopic.kapua-sys.02:42:AC:11:00:02.heater.data
        mqttTopic = mqttTopic.substring(VIRTUAL_TOPIC_PREFIX_LENGTH);
        mqttTopic = mqttTopic.replace(".", "/");
        // process prefix and extract message type
        // FIXME: pluggable message types and dialects
        if ("$EDC".equals(mqttTopic)) {
            message.putProperty(Converter.MESSAGE_TYPE, TransportMessageType.CONTROL);
            mqttTopic = mqttTopic.substring("$EDC".length());
        } else {
            message.putProperty(Converter.MESSAGE_TYPE, TransportMessageType.TELEMETRY);
        }
        message.putProperty(Converter.MESSAGE_DESTINATION, mqttTopic);

        // extract the original QoS
        Object activeMqQos = tm.getApplicationProperties().getValue().get(ACTIVEMQ_QOS);
        if (activeMqQos != null && activeMqQos instanceof Integer) {
            int activeMqQosInt = (int) activeMqQos;
            switch (activeMqQosInt) {
            case 0:
                message.putProperty(Converter.MESSAGE_QOS, TransportQos.AT_MOST_ONCE);
                break;
            case 1:
                message.putProperty(Converter.MESSAGE_QOS, TransportQos.AT_LEAST_ONCE);
                break;
            case 2:
                message.putProperty(Converter.MESSAGE_QOS, TransportQos.EXACTLY_ONCE);
                break;
            }
        }

        // process the incoming message
        byte[] messageBody = extractBytePayload(tm.getBody());
        return new MessageContext<byte[]>(messageBody, message.getProperties());

        // By default, the receiver automatically accepts (and settles) the delivery
        // when the handler returns, if no other disposition has been applied.
        // To change this and always manage dispositions yourself, use the
        // setAutoAccept method on the receiver.
    }

    private byte[] extractBytePayload(Section body) throws KapuaConverterException {
        logger.info("Received message with body: {}", body);
        if (body instanceof Data) {
            Binary data = ((Data) body).getValue();
            logger.info("Received DATA message");
            return data.getArray();
        } else if (body instanceof AmqpValue) {
            String content = (String) ((AmqpValue) body).getValue();
            logger.info("Received message with content: {}", content);
            return content.getBytes();
        } else {
            logger.warn("Recevide message with unknown message type! ({})", body != null ? body.getClass() : "NULL");
            // TODO use custom exception
            throw new KapuaConverterException(KapuaErrorCodes.INTERNAL_ERROR);
        }
    }

}
