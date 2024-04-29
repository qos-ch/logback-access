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

import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.access.common.spi.Util;
import ch.qos.logback.access.common.testUtil.NotifyingListAppender;
import ch.qos.logback.core.testUtil.RandomUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JettyBasicTest {

    static RequestLogImpl REQUEST_LOG_IMPL;
    static JettyFixtureWithListAndConsoleAppenders JETTY_FIXTURE;

    private static final int TIMEOUT = 5;
    static int RANDOM_SERVER_PORT = RandomUtil.getRandomServerPort();
    static String JETTY_FIXTURE_URL_STR;

    @BeforeAll
    static public void startServer() throws Exception {
        REQUEST_LOG_IMPL = new RequestLogImpl();
        JETTY_FIXTURE = new JettyFixtureWithListAndConsoleAppenders(REQUEST_LOG_IMPL, RANDOM_SERVER_PORT);
        JETTY_FIXTURE.start();
        JETTY_FIXTURE_URL_STR = JETTY_FIXTURE.getUrl();
    }

    @AfterAll
    static public void stopServer() throws Exception {
        if (JETTY_FIXTURE != null) {
            JETTY_FIXTURE.stop();
        }
    }

    @AfterEach
    public void tearDown() {
        //REQUEST_LOG_IMPL.detachAndStopAllAppenders();
    }

    @Test
    public void getRequest() throws Exception {
        URL url = new URL("http://localhost:" + RANDOM_SERVER_PORT + "/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);

        String result = Util.readToString(connection.getInputStream());

        assertEquals("hello world", result);

        NotifyingListAppender listAppender = (NotifyingListAppender) REQUEST_LOG_IMPL.getAppender("list");
        listAppender.list.clear();
    }

    @Test
    public void eventGoesToAppenders() throws Exception {
        URL url = new URL(JETTY_FIXTURE_URL_STR + "path/foo%20bar;param?query#fragment");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //connection.addRequestProperty("Cookie", "k0=v0; k1=v1");
        connection.addRequestProperty("Cookie", "k0=v0");
        connection.addRequestProperty("Cookie", "k1=v1");
        InputStream inputStream = connection.getInputStream();
        String result = Util.readToString(inputStream);
        close(inputStream);
        connection.disconnect();

        assertEquals("hello world", result);

        NotifyingListAppender listAppender = (NotifyingListAppender) REQUEST_LOG_IMPL.getAppender("list");
        IAccessEvent event = listAppender.list.poll(TIMEOUT, TimeUnit.SECONDS);
        assertNotNull(event, "No events received");

        assertEquals("127.0.0.1", event.getRemoteHost());
        assertEquals("localhost", event.getServerName());
        assertEquals("/path/foo%20bar;param", event.getRequestURI());

        List<Cookie> cookies =  event.getCookies();
        assertNotNull(cookies);

        assertEquals(2, cookies.size());
        for(int i = 0; i < 2 ; i++) {
            assertEquals("k" + i, cookies.get(i).getName());
            assertEquals("v" + i, cookies.get(i).getValue());
        }

        listAppender.list.clear();
    }

    private void close(InputStream inputStream) {
        if(inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void postContentConverter() throws Exception {
        URL url = new URL(JETTY_FIXTURE_URL_STR);
        String msg = "test message";

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // this line is necessary to make the stream aware of when the message is
        // over.
        connection.setFixedLengthStreamingMode(msg.getBytes().length);
        ((HttpURLConnection) connection).setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestProperty("Content-Type", "text/plain");

        PrintWriter output = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
        output.print(msg);
        output.flush();
        output.close();

        // StatusPrinter.print(requestLogImpl.getStatusManager());

        NotifyingListAppender listAppender = (NotifyingListAppender) REQUEST_LOG_IMPL.getAppender("list");

        IAccessEvent event = listAppender.list.poll(TIMEOUT, TimeUnit.SECONDS);
        assertNotNull(event, "No events received");

        // we should test the contents of the requests
        // assertEquals(msg, event.getRequestContent());
    }
}
