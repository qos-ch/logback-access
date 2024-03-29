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
package ch.qos.logback.access.common.blackbox.net;


import ch.qos.logback.access.common.blackbox.dummy.DummyRequest;
import ch.qos.logback.access.common.blackbox.dummy.DummyResponse;
import ch.qos.logback.access.common.blackbox.dummy.DummyServerAdapter;
import ch.qos.logback.access.common.net.URLEvaluator;
import ch.qos.logback.access.common.spi.AccessContext;
import ch.qos.logback.access.common.spi.AccessEvent;
import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class URLEvaluatorTest {

    final String expectedURL1 = "testUrl1";
    final String expectedURL2 = "testUrl2";
    AccessContext accessContext = new AccessContext();
    URLEvaluator evaluator;
    DummyRequest request;
    DummyResponse response;
    DummyServerAdapter serverAdapter;

    @BeforeEach
    public void setUp() throws Exception {
        evaluator = new ch.qos.logback.access.common.net.URLEvaluator();
        evaluator.setContext(accessContext);
        evaluator.addURL(expectedURL1);
        evaluator.start();
        request = new DummyRequest();
        response = new DummyResponse();
        serverAdapter = new DummyServerAdapter(request, response);
    }

    @AfterEach
    public void tearDown() throws Exception {
        evaluator.stop();
        evaluator = null;
        request = null;
        response = null;
        serverAdapter = null;
        accessContext = null;
    }

    @Test
    public void testExpectFalse() throws EvaluationException {
        request.setRequestUri("test");
        IAccessEvent ae = new AccessEvent(accessContext, request, response, serverAdapter);
        assertFalse(evaluator.evaluate(ae));
    }

    @Test
    public void testExpectTrue() throws EvaluationException {
        request.setRequestUri(expectedURL1);
        IAccessEvent ae = new AccessEvent(accessContext, request, response, serverAdapter);
        assertTrue(evaluator.evaluate(ae));
    }

    @Test
    public void testExpectTrueMultiple() throws EvaluationException {
        evaluator.addURL(expectedURL2);
        request.setRequestUri(expectedURL2);
        IAccessEvent ae = new AccessEvent(accessContext, request, response, serverAdapter);
        assertTrue(evaluator.evaluate(ae));
    }
}
