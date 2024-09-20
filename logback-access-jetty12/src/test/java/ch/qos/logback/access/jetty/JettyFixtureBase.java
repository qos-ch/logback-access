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
package ch.qos.logback.access.jetty;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.AbstractHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.util.Callback;

public class JettyFixtureBase {
    final protected RequestLogImpl requestLogImpl;
    protected Handler handler = new BasicHandler();
    private final int port;
    Server server;
    protected String url;

    public JettyFixtureBase(RequestLogImpl impl, int port) {
        requestLogImpl = impl;
        this.port = port;
        url = "http://localhost:" + port + "/";
    }

    public String getName() {
        return "Jetty Test Setup";
    }

    public String getUrl() {
        return url;
    }

    public void start() throws Exception {
        server = new Server(port);

        server.setRequestLog(requestLogImpl);
        configureRequestLogImpl();

        server.setHandler(getRequestHandler());
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
        server = null;
    }

    protected void configureRequestLogImpl() {
        requestLogImpl.start();
    }

    protected Handler getRequestHandler() {
        return handler;
    }

    class BasicHandler extends AbstractHandler {

        @Override
        public boolean handle(Request request, Response response, Callback callback) throws Exception {
            HttpFields.Mutable responseHeaders = response.getHeaders();
            responseHeaders.put(HttpHeader.CONTENT_TYPE, "text/plain; charset=UTF-8");

            byte[] bytes = "hello world".getBytes(StandardCharsets.UTF_8);

            ByteBuffer content = ByteBuffer.wrap(bytes);
            response.write(true, content, callback);
            return true;
        }
    }
}
