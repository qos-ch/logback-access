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
package ch.qos.logback.access.jetty;

import ch.qos.logback.access.common.spi.ServerAdapter;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;

import java.util.Map;

import static ch.qos.logback.access.jetty.HeaderUtil.buildHeaderMap;

/**
 * A jetty specific implementation of the {@link ServerAdapter} interface.
 *
 * @author S&eacute;bastien Pennec
 * @author Ceki Gulcu
 */
public class JettyServerAdapter implements ServerAdapter {

    Request request;
    Response response;

    public JettyServerAdapter(Request jettyRequest, Response jettyResponse) {
        this.request = jettyRequest;
        this.response = jettyResponse;
    }

    @Override
    public long getContentLength() {
        return Response.getContentBytesWritten(response);
    }

    @Override
    public int getStatusCode() {
        return response.getStatus();
    }

    @Override
    public long getRequestTimestamp() {
        return Request.getTimeStamp(request);
    }

    @Override
    public Map<String, String> buildResponseHeaderMap() {
        return buildHeaderMap(response.getHeaders());
    }

}
