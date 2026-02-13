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

package ch.qos.logback.access.jetty;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static ch.qos.logback.access.jetty.Jetty12Constants.LOGBACK_ACCESS_JETTY12_MODULE_NAME;

public class Jetty12VersionUtil {

    static String LOGBACK_ACCESS_JETTY12_MODULE_VERSION_PROPERTIES_FILE = LOGBACK_ACCESS_JETTY12_MODULE_NAME + "-version.properties";
    static String LOGBACK_ACCESS_JETTY12_MODULE_VERSION_PROPERTY_KEY = LOGBACK_ACCESS_JETTY12_MODULE_NAME + "-version";

    static public String getAccessJetty12VersionBySelfDeclaredProperties() {
        Properties props = new Properties();

        try (InputStream is = Jetty12Constants.class.getResourceAsStream(LOGBACK_ACCESS_JETTY12_MODULE_VERSION_PROPERTIES_FILE)) {
            if (is != null) {
                props.load(is);
                return props.getProperty(LOGBACK_ACCESS_JETTY12_MODULE_VERSION_PROPERTY_KEY);
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

}
