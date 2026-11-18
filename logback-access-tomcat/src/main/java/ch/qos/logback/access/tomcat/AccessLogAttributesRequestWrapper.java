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

package ch.qos.logback.access.tomcat;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.catalina.AccessLog;

/**
 * Exposes the values stored by valves such as Tomcat's {@code RemoteIpValve} under the {@link AccessLog} request
 * attributes, e.g. {@link AccessLog#REMOTE_ADDR_ATTRIBUTE}, falling back to the wrapped request when an attribute is
 * absent.
 *
 * <p>Since access events are created once the request is finished, {@code RemoteIpValve} has already restored the
 * original connection values by then. These attributes carry the values as seen during request processing.</p>
 *
 * @since 2.0.16
 */
class AccessLogAttributesRequestWrapper extends HttpServletRequestWrapper {

    AccessLogAttributesRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * Returns true if at least one of the {@link AccessLog} request attributes is set on the given request.
     */
    static boolean hasAccessLogAttributes(HttpServletRequest request) {
        return request.getAttribute(AccessLog.REMOTE_ADDR_ATTRIBUTE) != null
                || request.getAttribute(AccessLog.REMOTE_HOST_ATTRIBUTE) != null
                || request.getAttribute(AccessLog.PROTOCOL_ATTRIBUTE) != null
                || request.getAttribute(AccessLog.SERVER_NAME_ATTRIBUTE) != null
                || request.getAttribute(AccessLog.SERVER_PORT_ATTRIBUTE) != null;
    }

    @Override
    public String getRemoteAddr() {
        return stringAttribute(AccessLog.REMOTE_ADDR_ATTRIBUTE, super.getRemoteAddr());
    }

    @Override
    public String getRemoteHost() {
        return stringAttribute(AccessLog.REMOTE_HOST_ATTRIBUTE, super.getRemoteHost());
    }

    @Override
    public String getProtocol() {
        return stringAttribute(AccessLog.PROTOCOL_ATTRIBUTE, super.getProtocol());
    }

    @Override
    public String getServerName() {
        return stringAttribute(AccessLog.SERVER_NAME_ATTRIBUTE, super.getServerName());
    }

    @Override
    public int getServerPort() {
        Object value = getAttribute(AccessLog.SERVER_PORT_ATTRIBUTE);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return super.getServerPort();
    }

    private String stringAttribute(String name, String defaultValue) {
        Object value = getAttribute(name);
        return value == null ? defaultValue : value.toString();
    }
}
