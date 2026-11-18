/*
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2026, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v2.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */

package ch.qos.logback.access.tomcat_11_0;

import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ListAppender extends AppenderBase<IAccessEvent>  {

    public List<IAccessEvent> list = new CopyOnWriteArrayList<>();

    @Override
    protected void append(IAccessEvent eventObject) {
        // the request and response are recycled once Tomcat is done with them
        eventObject.prepareForDeferredProcessing();
        list.add(eventObject);
    }
}
