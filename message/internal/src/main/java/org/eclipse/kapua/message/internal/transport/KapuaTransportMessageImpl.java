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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.message.internal.transport;

import org.eclipse.kapua.message.internal.KapuaMessageImpl;
import org.eclipse.kapua.message.transport.KapuaTransportChannel;
import org.eclipse.kapua.message.transport.KapuaTransportMessage;
import org.eclipse.kapua.message.transport.KapuaTransportPayload;
import org.eclipse.kapua.message.transport.KapuaTransportQos;

/**
 * Kapua transportmessage object reference implementation.
 */
public class KapuaTransportMessageImpl extends KapuaMessageImpl<KapuaTransportChannel, KapuaTransportPayload> implements KapuaTransportMessage {

    private static final long serialVersionUID = 8636346978379971355L;

    private String transportTopic;
    private KapuaTransportQos transportQos;

    @Override
    public String getTransportTopic() {
        return transportTopic;
    }

    @Override
    public void setTransportTopic(String transportTopic) {
        this.transportTopic = transportTopic;
    }

    @Override
    public KapuaTransportQos getTransportQoS() {
        return transportQos;
    }

    @Override
    public void setTransportQoS(KapuaTransportQos transportQos) {
        this.transportQos = transportQos;
    }
}
