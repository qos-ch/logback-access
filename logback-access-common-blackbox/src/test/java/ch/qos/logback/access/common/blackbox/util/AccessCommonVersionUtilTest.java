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

package ch.qos.logback.access.common.blackbox.util;

import ch.qos.logback.access.common.AccessConstants;
import ch.qos.logback.access.common.util.AccessCommonVersionUtil;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.util.CoreVersionUtil;
import ch.qos.logback.core.util.VersionUtil;
import org.junit.jupiter.api.Test;


import static ch.qos.logback.access.common.AccessConstants.LOGBACK_CORE_NAME;
import static ch.qos.logback.access.common.AccessConstants.LOGBACK_ACCESS_COMMON_NAME;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccessCommonVersionUtilTest {


    Context context = new ContextBase();

    @Test
    void testAccessCommonVersionBySelfDeclaredProperties() {
        String version = AccessCommonVersionUtil.getAccessCommonVersionBySelfDeclaredProperties();
        assertNotNull(version);
        assertTrue(version.startsWith("2.0"));
    }

    @Test
    void testExpectedAndFoundVersionOfCore() {
        String coreVersion = CoreVersionUtil.getCoreVersionBySelfDeclaredProperties();
        String accessCommonVersion = AccessCommonVersionUtil.getAccessCommonVersionBySelfDeclaredProperties();
        VersionUtil.compareExpectedAndFoundVersion(context, coreVersion, AccessConstants.class, accessCommonVersion, LOGBACK_ACCESS_COMMON_NAME, LOGBACK_CORE_NAME);
    }


}
