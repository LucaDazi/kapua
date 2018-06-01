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
package org.eclipse.kapua.converter.hono;

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

public class HonoAmqpConverter implements Converter<Message, byte[]> {

    protected final static Logger logger = LoggerFactory.getLogger(HonoAmqpConverter.class);

    private final static String CONTROL_PREFIX = "c/";
    private final static String TELEMETRY_PREFIX = "t/";

    @Override
    public MessageContext<byte[]> convert(MessageContext<Message> message) throws KapuaConverterException {
        Message tm = message.getMessage();
        // build the message properties
        // extract original MQTT topic
        String mqttTopic = (String)tm.getApplicationProperties().getValue().get("orig_address");
        mqttTopic = mqttTopic.replace(".", "/");
        if (mqttTopic.startsWith(TELEMETRY_PREFIX)) {
            message.putProperty(Converter.MESSAGE_TYPE, TransportMessageType.TELEMETRY);
            mqttTopic = mqttTopic.substring(TELEMETRY_PREFIX.length());
        }
        else if (mqttTopic.startsWith(CONTROL_PREFIX)) {
            message.putProperty(Converter.MESSAGE_TYPE, TransportMessageType.CONTROL);
            mqttTopic = mqttTopic.substring(CONTROL_PREFIX.length());
        }
        //TODO handle alerts, ... messages types
        message.putProperty(Converter.MESSAGE_DESTINATION, mqttTopic);

        // extract the original QoS
        //TODO
        message.putProperty(Converter.MESSAGE_QOS, TransportQos.AT_MOST_ONCE);

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
