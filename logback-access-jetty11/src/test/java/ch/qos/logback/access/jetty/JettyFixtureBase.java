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
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.AbstractHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class JettyFixtureBase {


    public static String HELLO_WORLD_MSG = "Hello world";


    final protected RequestLogImpl requestLogImpl;
    protected Handler handler = new HelloWorldHandler(HELLO_WORLD_MSG);
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

        ContextHandler context = new ContextHandler("/");
        context.setContextPath("/");
        context.setHandler(makeHelloWorldHandler(HELLO_WORLD_MSG));

        ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();
        contextHandlerCollection.setHandlers(new Handler[] { context });

        server.addEventListener(new LCListener());

        server.setHandler(contextHandlerCollection);
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
        server = null;
    }

    public void join() throws Exception {
        server.join();
    }

    protected void configureRequestLogImpl() {
        requestLogImpl.start();
    }

    protected Handler makeHelloWorldHandler(String msg) {
        return new HelloWorldHandler(msg);
    }

    class HelloWorldHandler extends AbstractHandler {

        String msg;

        HelloWorldHandler(String msg) {
            this.msg = msg;
        }

        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain");
            Writer writer = response.getWriter();
            writer.write(msg);
            writer.flush();
            baseRequest.setHandled(true);
        }
    }
}
