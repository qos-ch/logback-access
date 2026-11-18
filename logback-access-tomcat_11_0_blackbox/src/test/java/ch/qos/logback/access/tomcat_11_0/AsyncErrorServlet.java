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

import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Answers with a 500 from another thread, after the container thread has left the servlet.
 */
class AsyncErrorServlet extends HttpServlet {

    static final String BODY = "{\"error\":\"not found\"}";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        AsyncContext asyncContext = req.startAsync();
        asyncContext.start(() -> {
            try {
                Thread.sleep(200);
                HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
                response.setStatus(500);
                response.getWriter().write(BODY);
            } catch (InterruptedException | IOException e) {
                throw new IllegalStateException(e);
            } finally {
                asyncContext.complete();
            }
        });
    }
}
