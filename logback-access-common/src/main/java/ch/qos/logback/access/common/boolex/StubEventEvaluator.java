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

package ch.qos.logback.access.common.boolex;

import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

public class StubEventEvaluator  extends EventEvaluatorBase<IAccessEvent>  {

    static public final String MSG_0 = "This is a stub for JaninoEventEvaluator which was removed in logback-access version 2.0.5";
    static public final String MSG_1 = "You can migrate existing configurations to Java-only equivalents by extending the EventEvaluatorBase class.";


    String expression;

    @Override
    public void start() {
        stop();
        addWarn(MSG_0);
        addWarn(MSG_1);
    }

    @Override
    public boolean evaluate(IAccessEvent iAccessEvent) throws NullPointerException, EvaluationException {
        return false;
    }


    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

}
