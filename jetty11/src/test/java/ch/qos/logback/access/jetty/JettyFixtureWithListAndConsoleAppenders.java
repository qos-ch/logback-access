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

import ch.qos.logback.access.common.PatternLayoutEncoder;
import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.access.common.testUtil.NotifyingListAppender;
import ch.qos.logback.core.ConsoleAppender;

public class JettyFixtureWithListAndConsoleAppenders extends JettyFixtureBase {

    public JettyFixtureWithListAndConsoleAppenders(RequestLogImpl impl, int port) {
        super(impl, port);
        url = "http://localhost:" + port + "/";
    }

    public void start() throws Exception {
        super.start();
        Thread.yield();
    }

    public void stop() throws Exception {
        super.stop();
    }

    public void addEventListener() {
        server.addEventListener(new LCListener());
    }

    @Override
    protected void configureRequestLogImpl() {
        NotifyingListAppender appender = new NotifyingListAppender();
        appender.setContext(requestLogImpl);
        appender.setName("list");
        appender.start();

        ConsoleAppender<IAccessEvent> console = new ConsoleAppender<IAccessEvent>();
        console.setContext(requestLogImpl);
        console.setName("console");
        PatternLayoutEncoder layout = new PatternLayoutEncoder();
        layout.setContext(requestLogImpl);
        layout.setPattern("%date %server %clientHost");
        console.setEncoder(layout);
        layout.start();
        console.start();

        requestLogImpl.addAppender(appender);
        requestLogImpl.addAppender(console);
    }
}
