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

package ch.qos.logback.access.common.util;

import ch.qos.logback.access.common.AccessConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for retrieving version information about the "logback-access-common" module
 * based on the self-declared properties file.
 *
 *
 * @since 2.0.9
 */
public class AccessCommonVersionUtil {


    static public String getAccessCommonVersionBySelfDeclaredProperties() {
        return getArtifactVersionBySelfDeclaredProperties(AccessConstants.class, "logback-access-common");
    }

    static public String getArtifactVersionBySelfDeclaredProperties(Class<?> aClass, String moduleName) {
        Properties props = new Properties();
        // example propertiesFileName: logback-core-version.properties
        //
        String propertiesFileName = moduleName + "-version.properties";
        String propertyKey = moduleName+"-version";
        try (InputStream is = aClass.getResourceAsStream(propertiesFileName)) {
            if (is != null) {
                props.load(is);
                return props.getProperty(propertyKey);
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }


}
