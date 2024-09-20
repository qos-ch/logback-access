/*
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2024, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *    or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */

package ch.qos.logback.access.common.testUtil;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static ch.qos.logback.access.common.AccessConstants.COOKIE_REQUEST_PROP_KEY;

public class HttpGetUtil {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    URL url;

    HttpURLConnection conn;

    public HttpGetUtil(String urlStr) throws MalformedURLException {
        this.url = new URL(urlStr);
    }

    String requestMethod = "GET";


    public HttpGetUtil init() {
        if(conn != null) {
            throw new IllegalStateException("Already initialized");
        }
        try {
            this.conn = (HttpURLConnection) url.openConnection();
            this.conn.setRequestMethod(requestMethod);
            return this;
        } catch (IOException e) {
            logger.atError().addKeyValue("url", url.toString()).setCause(e).log("Failed to connect");
            return this;
        }
    }

    public HttpGetUtil addRequestProperty(String key, String val) {
        this.conn.addRequestProperty(key, val);
        return this;
    }


    public HttpGetUtil addCookie(String val) {
        return addRequestProperty(COOKIE_REQUEST_PROP_KEY, val);
    }

    public HttpURLConnection connect() {
        try {
            conn.connect();
            return conn;
        } catch (IOException e) {
            logger.atError().addKeyValue("url", url.toString()).setCause(e).log("Failed to connect");
            return null;
        }
    }

    public String readResponse() {
        if(conn == null)
            return null;

        try {
            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                return innerReadResponse();
            } else {
                logger.atError().addKeyValue("status", responseCode).log("Failed response");
                return null;
            }
        } catch (IOException e) {
            logger.atError().addKeyValue("url", url.toString()).setCause(e).log("failed to read status");
            return  null;
        }
    }



    private String innerReadResponse() {
        try (InputStream is = conn.getInputStream()) {
            BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String inputLine;
            StringBuffer buffer = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                buffer.append(inputLine);
            }
            return buffer.toString();
        } catch (IOException e) {
            logger.atError().addKeyValue("url", url.toString()).setCause(e).log("failed");
            return null;
        }
    }

    public void disconnect() {
        if(conn != null)
            conn.disconnect();
    }
}
