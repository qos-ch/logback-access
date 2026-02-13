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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 *
 *  <p>Use the following command to run this test
 *   </p>
 *
 *   <pre>  mvn install;  # avoid future compilation errors
 *   cd logback-access-common-blackbox;
 *   mvn test -P older-core -Dtest=ch.qos.logback.access.common.blackbox.util.WithOlderCoreAccessCommonVersionUtilTest
 *   </pre>
 */
public class WithOlderCoreAccessCommonVersionUtilTest {


    Context context = new ContextBase();

    @Test
    public void otherVersionTest() {
        String olderCoreVersion = System.getProperty("olderCore", "none");
        System.out.println("olderCoreVersion="+olderCoreVersion);
        assertTrue(olderCoreVersion.startsWith("1.5."));
        try {
            AccessCommonVersionUtil accessCommonVersionUtil = new AccessCommonVersionUtil(context);
            accessCommonVersionUtil.getExpectedVersionOfDependencyByProperties(AccessConstants.class, "", "");
            fail("Expected Error");
        } catch (NoClassDefFoundError e) {
            // logback-core version is 1.5.24 or older
            System.out.println("Got expected "+e.getClass().getName()+".");
            System.out.println(e.getMessage());
        } catch (NoSuchMethodError e) {
            // logback-core version is 1.5.25 or older
            System.out.println("Got expected "+e.getClass().getName()+".");
            System.out.println(e.getMessage());
        }
    }

}
