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

import ch.qos.logback.access.tomcat.AccessTomcatVersionUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccessTomcatVersionTest {


    @Test
    public void smoke() {

        String version = AccessTomcatVersionUtil.getCoreVersionBySelfDeclaredProperties();
        assertNotNull(version);
        System.out.println("version="+version);
        assertTrue(version.startsWith("2.0"));
    }
}
