/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.access.common.pattern;

import java.net.InetAddress;
import java.net.UnknownHostException;

import ch.qos.logback.access.common.spi.IAccessEvent;

public class LocalIPAddressConverter extends AccessConverter {

    String localIPAddressStr;

    public LocalIPAddressConverter() {
        try {
            localIPAddressStr = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException uhe) {
            localIPAddressStr = "127.0.0.1";
        }
    }

    @Override
    public String convert(IAccessEvent accessEvent) {
        return localIPAddressStr;
    }

}
