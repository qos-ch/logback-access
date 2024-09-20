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

import ch.qos.logback.access.common.testUtil.HttpGetUtil;
import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.access.common.testUtil.NotifyingListAppender;
import ch.qos.logback.core.testUtil.RandomUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ch.qos.logback.access.jetty.JettyFixtureBase.HELLO_WORLD_MSG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Jetty11BasicTest {

    private static final int TIMEOUT_IN_SECONDS = 3;

    RequestLogImpl REQUEST_LOG_IMPL;
    JettyFixtureWithListAndConsoleAppenders JETTY_FIXTURE;
    String JETTY_FIXTURE_URL_STR;

    int RANDOM_SERVER_PORT = RandomUtil.getRandomServerPort();
    HttpGetUtil httpGetUtil;

    @BeforeEach
    @Timeout(value = TIMEOUT_IN_SECONDS, unit = TimeUnit.SECONDS)
    public void startServer() throws Exception {
        REQUEST_LOG_IMPL = new RequestLogImpl();
        JETTY_FIXTURE = new JettyFixtureWithListAndConsoleAppenders(REQUEST_LOG_IMPL, RANDOM_SERVER_PORT);
        JETTY_FIXTURE.start();

        JETTY_FIXTURE_URL_STR = JETTY_FIXTURE.getUrl();
        Thread.sleep(100);

    }

    @AfterEach
    public void stopServer() throws Exception {
        if(this.httpGetUtil != null) {
            this.httpGetUtil.disconnect();
        }

        if (JETTY_FIXTURE != null) {
            JETTY_FIXTURE.stop();
        }
    }

    @Test
    public void getRequest() throws Exception {
        this.httpGetUtil = new HttpGetUtil("http://localhost:" + RANDOM_SERVER_PORT + "/");
        this.httpGetUtil = this.httpGetUtil.init();
        this.httpGetUtil.connect();

        String result = this.httpGetUtil.readResponse();

        assertEquals(HELLO_WORLD_MSG, result);

        NotifyingListAppender listAppender = (NotifyingListAppender) REQUEST_LOG_IMPL.getAppender("list");
        listAppender.list.clear();
    }

    @Test
    public void eventGoesToAppenders() throws Exception {

        this.httpGetUtil = new HttpGetUtil(JETTY_FIXTURE_URL_STR + "path/foo%20bar;param?query#fragment");
        this.httpGetUtil = httpGetUtil.init().addCookie("k0=v0; k1=v1");
        this.httpGetUtil.connect();

        String result = this.httpGetUtil.readResponse();


        assertEquals(HELLO_WORLD_MSG, result);

        NotifyingListAppender listAppender = (NotifyingListAppender) REQUEST_LOG_IMPL.getAppender("list");
        IAccessEvent event = listAppender.list.poll(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        assertNotNull(event, "No events received");

        assertEquals("127.0.0.1", event.getRemoteHost());
        assertEquals("localhost", event.getServerName());
        assertEquals("/path/foo%20bar;param", event.getRequestURI());

        List<Cookie> cookies =  event.getCookies();
        assertNotNull(cookies);

        System.out.println("---------------------");
        cookies.stream().forEach(System.out::println);
        System.out.println("---------------------");
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

        IAccessEvent event = listAppender.list.poll(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        assertNotNull(event, "No events received");

        // we should test the contents of the requests
        // assertEquals(msg, event.getRequestContent());
    }
}
