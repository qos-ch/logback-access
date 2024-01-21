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
package ch.qos.logback.access.common.dummy;

import ch.qos.logback.access.common.spi.AccessContext;
import ch.qos.logback.access.common.spi.AccessEvent;
import ch.qos.logback.access.common.spi.IAccessEvent;

public class DummyAccessEventBuilder {

    static public IAccessEvent buildNewAccessEvent() {
        ch.qos.logback.access.common.dummy.DummyRequest request = new ch.qos.logback.access.common.dummy.DummyRequest();
        ch.qos.logback.access.common.dummy.DummyResponse response = new ch.qos.logback.access.common.dummy.DummyResponse();
        ch.qos.logback.access.common.dummy.DummyServerAdapter adapter = new ch.qos.logback.access.common.dummy.DummyServerAdapter(request, response);
        AccessContext accessContext = new AccessContext();

        return new AccessEvent(accessContext, request, response, adapter);
    }

}
