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
package org.eclipse.kapua.connector.kafka;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.connector.AbstractConnector;
import org.eclipse.kapua.connector.MessageContext;
import org.eclipse.kapua.converter.Converter;
import org.eclipse.kapua.converter.KapuaConverterException;
import org.eclipse.kapua.message.transport.TransportMessage;
import org.eclipse.kapua.processor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.consumer.KafkaConsumer;

/**
 * AMQP ActiveMQ connector implementation
 *
 */
public class KafkaConnector extends AbstractConnector<byte[], TransportMessage> {

	private final Logger logger = LoggerFactory.getLogger(KafkaConnector.class);
	
	private static final String KAFKA_GROUP = "test_group";
	private Vertx vertx;
	private KafkaConsumer<String, String> consumer;
	private Converter<byte[], TransportMessage> converter;
	private Processor<TransportMessage> processor;
	
	protected KafkaConnector(Vertx vertx, Converter<byte[], TransportMessage> converter,
			Processor<TransportMessage> processor) {
		super(vertx, converter, processor);
		// TODO Auto-generated constructor stub
		this.vertx = vertx;
		this.converter = converter;
		this.processor = processor;
	}

	@Override
	protected void startInternal(Future<Void> startFuture) {
		Map<String, String> config = new HashMap<>();
		config.put("bootstrap.servers", "localhost:9092");
		config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		config.put("group.id", KAFKA_GROUP);
		config.put("auto.offset.reset", "earliest");
		config.put("enable.auto.commit", "false");

		// use consumer for interacting with Apache Kafka
		consumer = KafkaConsumer.create(this.vertx, config);
		consumer.handler(handler -> {
			//Handle message?
		});
	}

	@Override
	protected void stopInternal(Future<Void> stopFuture) {
		consumer.close(result -> {
			if(!result.succeeded()) {
				logger.warn("Cannot close Kafka Consumer!");
			}
		});
	}

	@Override
	protected MessageContext<byte[]> convert(MessageContext<?> message) throws KapuaConverterException {
		// TODO Auto-generated method stub
		return null;
	}

   

}
