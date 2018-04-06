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
package org.eclipse.kapua.message.transport;

import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.xml.MessageXmlRegistry;

/**
 * Kapua data message object definition.
 *
 * @since 1.0
 *
 */
@XmlType(factoryClass = MessageXmlRegistry.class, factoryMethod = "newKapuaDataMessage")
public interface KapuaTransportMessage extends KapuaMessage<KapuaTransportChannel, KapuaTransportPayload> {

    /**
     * Gets the topic used in the in the transport of the message
     * @return
     */
    public String getTransportTopic();

    /**
     * Sets the topic used in the in the transport of the message
     */
    public void setTransportTopic(String transportTopic);

    /**
     * Gets the qos used in the in the transport of the message
     * @return
     */    
    public KapuaTransportQos getTransportQoS();

    /**
     * Sets the qos used in the in the transport of the message
     */
    public void setTransportQoS(KapuaTransportQos qos);
}
