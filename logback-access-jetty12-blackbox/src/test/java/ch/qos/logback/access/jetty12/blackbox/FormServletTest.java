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

package ch.qos.logback.access.jetty12.blackbox;

import ch.qos.logback.access.jetty.RequestLogImpl;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class FormServletTest {
    static Server server;
    private URI baseUri;

    RequestLogImpl requestLogImpl = new RequestLogImpl();

    @BeforeEach
    void startServer() throws Exception {
        server = new Server(0);
        configAppenders(requestLogImpl);
        server.setRequestLog(requestLogImpl);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");

        server.setHandler(context);
        context.addServlet(new ServletHolder(new FormServlet()), "/form");
        server.start();
        baseUri = server.getURI();
    }

    void configAppenders( RequestLogImpl requestLogImpl) {
        System.out.println(System.getProperty("user.dir"));
        requestLogImpl.setFileName("src/input/logback-access-issue-25.xml");
//        ConsoleAppender<IAccessEvent> consoleAppender = new ConsoleAppender<IAccessEvent>();
//        consoleAppender.setContext(requestLogImpl);
//        consoleAppender.setName("console");
//        PatternLayoutEncoder layout = new PatternLayoutEncoder();
//        layout.setContext(requestLogImpl);
//        layout.setPattern("%date %header{host} %server %clientHost");
//        consoleAppender.setEncoder(layout);
//        layout.start();
//        consoleAppender.start();
//
//        requestLogImpl.addAppender(consoleAppender);
    }


    @AfterEach
    void stopServer() throws Exception {
        server.stop();
    }

    @Test
    void testBadForm() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        byte[] badBytes = new byte[] { (byte) 0xFF };
        HttpRequest req = HttpRequest.newBuilder().uri(baseUri.resolve("/form")).header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofByteArray(badBytes)).build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, resp.statusCode());
    }

    @Test
    void testLargeForm() throws Exception {
        String largeValue = String.join("", Collections.nCopies(200001, "a"));
        String body = new StringBuilder("key=").append(largeValue).toString();
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                        .uri(baseUri.resolve("/form"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofByteArray(bytes))
                        .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, resp.statusCode());
    }

}
