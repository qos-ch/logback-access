/**
 * Logback: the reliable, generic, fast and flexible logging framework. Copyright (C) 1999-2015, QOS.ch. All rights
 * reserved.
 *
 * This program and the accompanying materials are dual-licensed under either the terms of the Eclipse Public License
 * v1.0 as published by the Eclipse Foundation
 *
 * or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation.
 */
package ch.qos.logback.access.common.blackbox.dummy;

import ch.qos.logback.access.common.spi.AccessContext;
import ch.qos.logback.access.common.spi.AccessEvent;
import ch.qos.logback.access.common.spi.IAccessEvent;

public class DummyAccessEventBuilder {

    DummyRequest request;
    DummyResponse response;
    DummyServerAdapter adapter;
    AccessContext accessContext;

    public DummyAccessEventBuilder() {

    }

    public DummyAccessEventBuilder setRequest(DummyRequest request) {
        this.request = request;
        return this;
    }

    public DummyAccessEventBuilder setResponse(DummyResponse response) {
        this.response = response;
        return this;
    }

    public DummyAccessEventBuilder setAdapter(DummyServerAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public DummyAccessEventBuilder setAccessContext(AccessContext accessContext) {
        this.accessContext = accessContext;
        return this;
    }

    public IAccessEvent build() {
        if (this.request == null)
            this.request = new DummyRequest();
        if (this.response == null)
            response = new DummyResponse();
        if (this.adapter == null)
            adapter = new DummyServerAdapter(request, response);
        if (this.accessContext == null)
            accessContext = new AccessContext();

        return new AccessEvent(accessContext, request, response, adapter);
    }

    static public IAccessEvent buildNewAccessEvent() {
        DummyAccessEventBuilder daeb = new DummyAccessEventBuilder();
        return daeb.build();
    }

}
