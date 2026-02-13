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

package ch.qos.logback.access.tomcat;

import ch.qos.logback.core.CoreConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static ch.qos.logback.access.tomcat.AccessTomcatConstants.LOGBACK_ACCESS_TOMCAT_MODULE_NAME;

public class AccessTomcatVersionUtil {

    static String LOGBACK_ACCESS_TOMCAT_MODULE_VERSION_PROPERTIES_FILE = LOGBACK_ACCESS_TOMCAT_MODULE_NAME + "-version.properties";
    static String LOGBACK_ACCESS_TOMCAT_MODULE_VERSION_PROPERTY_KEY = LOGBACK_ACCESS_TOMCAT_MODULE_NAME + "-version";

    /**
     * Retrieves the version of the "logback-core" module using a properties file
     * associated with the module.
     *
     * <p>The method locates and reads a properties file named "logback-core-version.properties"
     * in the package of the {@code CoreConstants.class}. It then extracts the version
     * information using the key "logback-core-version".
     * </p>
     *
     * @return the version of the "logback-core" module as a string, or null if the version cannot be determined
     * @since 1.5.26
     */
    static public String getCoreVersionBySelfDeclaredProperties() {
        Properties props = new Properties();

        try (InputStream is = AccessTomcatConstants.class.getResourceAsStream(LOGBACK_ACCESS_TOMCAT_MODULE_VERSION_PROPERTIES_FILE)) {
            if (is != null) {
                props.load(is);
                return props.getProperty(LOGBACK_ACCESS_TOMCAT_MODULE_VERSION_PROPERTY_KEY);
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

}
