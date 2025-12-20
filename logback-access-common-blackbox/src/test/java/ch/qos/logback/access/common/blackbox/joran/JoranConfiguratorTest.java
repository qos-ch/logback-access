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
package ch.qos.logback.access.common.blackbox.joran;

import ch.qos.logback.access.common.blackbox.CommonBlackboxConstants;
import ch.qos.logback.access.common.blackbox.dummy.DummyAccessEventBuilder;
import ch.qos.logback.access.common.blackbox.dummy.DummyRequest;
import ch.qos.logback.access.common.blackbox.dummy.DummyResponse;
import ch.qos.logback.access.common.blackbox.testUtil.MiniStatusChecker;
import ch.qos.logback.access.common.boolex.StubEventEvaluator;
import ch.qos.logback.access.common.joran.JoranConfigurator;
import ch.qos.logback.access.common.spi.AccessContext;
import ch.qos.logback.access.common.spi.AccessEvent;
import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.core.testUtil.StringListAppender;
import ch.qos.logback.core.util.StatusPrinter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JoranConfiguratorTest {

    AccessContext context = new AccessContext();
    MiniStatusChecker miniStatusChecker = new MiniStatusChecker(context);

    @BeforeEach
    public void setUp() throws Exception {

    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    void configure(String file) throws JoranException {
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(context);
        jc.doConfigure(file);
    }

    @Test
    public void smoke() throws Exception {
        configure(CommonBlackboxConstants.TEST_DIR_PREFIX + "input/joran/smoke.xml");
        StatusPrinter.print(context);
        ListAppender<IAccessEvent> listAppender = (ListAppender<IAccessEvent>) context.getAppender("LIST");
        assertNotNull(listAppender);
        IAccessEvent event = DummyAccessEventBuilder.buildNewDefaultAccessEvent();
        listAppender.doAppend(event);

        assertEquals(1, listAppender.list.size());

        assertEquals(1, listAppender.list.size());
        IAccessEvent ae = listAppender.list.get(0);
        assertNotNull(ae);
    }

    @Test
    public void defaultLayout() throws Exception {
        configure(CommonBlackboxConstants.TEST_DIR_PREFIX + "input/joran/defaultLayout.xml");
        StringListAppender<IAccessEvent> listAppender = (StringListAppender<IAccessEvent>) context
                .getAppender("STR_LIST");
        IAccessEvent event = DummyAccessEventBuilder.buildNewDefaultAccessEvent();
        listAppender.doAppend(event);
        assertEquals(1, listAppender.strList.size());
        // the result contains a line separator at the end
        assertTrue(listAppender.strList.get(0).startsWith("testMethod"));
    }

    @Test
    public void noClassEventEvaluator() throws Exception {
        configure(CommonBlackboxConstants.TEST_DIR_PREFIX + "input/joran/evaluator/noClassEventEvaluator.xml");
        ListAppender<IAccessEvent> listAppender = (ListAppender<IAccessEvent>) context.getAppender("LIST");
        IAccessEvent event = DummyAccessEventBuilder.buildNewDefaultAccessEvent();
        listAppender.doAppend(event);
        assertEquals(1, listAppender.list.size());

        assertEquals(1, listAppender.list.size());
        IAccessEvent ae = listAppender.list.get(0);
        assertNotNull(ae);

        assertTrue(miniStatusChecker.containsMatch(StubEventEvaluator.MSG_0));
        assertTrue(miniStatusChecker.containsMatch(StubEventEvaluator.MSG_1));
    }

    @Test
    public void statusCodeEventEvaluator() throws Exception {
        configure(CommonBlackboxConstants.TEST_DIR_PREFIX + "input/joran/evaluator/statusCodeEventEvaluator.xml");
        ListAppender<IAccessEvent> listAppender = (ListAppender<IAccessEvent>) context.getAppender("LIST");
        AccessEvent event0 = createAccessEventWithRequestURIAndStatusCode( "msg1", 404);
        AccessEvent event1 = createAccessEventWithRequestURIAndStatusCode("msg2", 200);

        listAppender.doAppend(event0);
        listAppender.doAppend(event1);
        assertEquals(1, listAppender.list.size());
    }

    @Test
    public void requestURIEventEvaluatorTest() throws Exception {
        configure(CommonBlackboxConstants.TEST_DIR_PREFIX + "input/joran/evaluator/requestURIEvaluator.xml");
        ListAppender<IAccessEvent> listAppender = (ListAppender<IAccessEvent>) context.getAppender("LIST");
        AccessEvent event0 = createAccessEventWithRequestURIAndStatusCode( "index.html", 200);
        AccessEvent event1 = createAccessEventWithRequestURIAndStatusCode( "toto.css", 200);

        listAppender.doAppend(event0);
        listAppender.doAppend(event1);
        assertEquals(1, listAppender.list.size());

        IAccessEvent eventInList0 = listAppender.list.get(0);

        assertTrue(eventInList0.getRequestURI().contains("index.html"));
    }

    @Test
    public void combinedStatusCode_RequestURIEventEvaluatorsTest() throws Exception {
        configure(CommonBlackboxConstants.TEST_DIR_PREFIX + "input/joran/evaluator/combinedStatusCodeRequestURI.xml");
        ListAppender<IAccessEvent> listAppender = (ListAppender<IAccessEvent>) context.getAppender("LIST");
        AccessEvent event0 = createAccessEventWithRequestURIAndStatusCode( "index.html", 404);
        AccessEvent event1 = createAccessEventWithRequestURIAndStatusCode( "toto.css", 404);

        listAppender.doAppend(event0);
        listAppender.doAppend(event1);
        assertEquals(1, listAppender.list.size());

        IAccessEvent eventInList0 = listAppender.list.get(0);

        assertTrue(eventInList0.getRequestURI().contains("index.html"));
    }


    private static AccessEvent createAccessEventWithRequestURIAndStatusCode(String requestURI, int statusCode) {
        DummyAccessEventBuilder daeb = new DummyAccessEventBuilder();
        DummyRequest dummyRequest = new DummyRequest();
        dummyRequest.setRequestUri(requestURI);
        daeb.setRequest(dummyRequest);

        DummyResponse dummyResponse = new DummyResponse();
        dummyResponse.setStatus(statusCode);
        daeb.setResponse(dummyResponse);

        AccessEvent event0 = daeb.build();
        return event0;
    }
}
