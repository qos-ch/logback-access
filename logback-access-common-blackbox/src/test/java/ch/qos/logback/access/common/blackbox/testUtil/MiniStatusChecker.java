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

package ch.qos.logback.access.common.blackbox.testUtil;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.StatusUtil;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MiniStatusChecker extends StatusUtil {

    public static final int UNKNOWN_LEVEL = -1;

    public MiniStatusChecker(StatusManager sm) {
        super(sm);
    }

    public MiniStatusChecker(Context context) {
        super(context);
    }

    public void assertIsWarningOrErrorFree() {
        Assertions.assertTrue(isWarningOrErrorFree(0));
    }
}
