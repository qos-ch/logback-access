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
import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.VersionUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static ch.qos.logback.access.common.AccessConstants.LOGBACK_ACCESS_COMMON_MODULE_NAME;

/**
 * Utility class for retrieving version information about the "logback-access-common" module
 * based on the self-declared properties file.
 *
 *
 * @since 2.0.9
 */
public class AccessCommonVersionUtil extends VersionUtil {


    static String LOGBACK_ACCESS_COMMON_MODULE_VERSION_PROPERTIES_FILE = LOGBACK_ACCESS_COMMON_MODULE_NAME + "-version.properties";
    static String LOGBACK_ACCESS_COMMON_MODULE_VERSION_PROPERTY_KEY = LOGBACK_ACCESS_COMMON_MODULE_NAME + "-version";


    public AccessCommonVersionUtil(Context context) {
        super(context);
    }

    static public String getAccessCommonVersionBySelfDeclaredProperties() {
        Properties props = new Properties();
        try (InputStream is = AccessConstants.class.getResourceAsStream(LOGBACK_ACCESS_COMMON_MODULE_VERSION_PROPERTIES_FILE)) {
            if (is != null) {
                props.load(is);
                return props.getProperty(LOGBACK_ACCESS_COMMON_MODULE_VERSION_PROPERTY_KEY);
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Retrieves the expected version of a specific dependency from a properties file
     * located in the classpath of the specified depender class. The dependency version
     * is determined based on the key provided.
     *
     * @param dependerClass the class whose classloader is used to locate the properties file
     * @param propertiesFileName the name of the properties file containing dependency information
     * @param dependencyNameAsKey the key in the properties file representing the desired dependency
     * @return the version of the dependency as specified in the properties file, or null if the file
     *         or key is not found, or an error occurs during processing
     *
     * @since 2.0.10
     */
    @Override
    public String getExpectedVersionOfDependencyByProperties(Class<?> dependerClass, String propertiesFileName, String dependencyNameAsKey) {
        Properties props = new Properties();
        // propertiesFileName : logback-access-common-dependencies.properties

        // Class.getResourceAsStream invokes Class.isOpenToCaller(name, Reflection.getCallerClass())
        // Thus, the caller class must be in the same module
        try (InputStream is = dependerClass.getResourceAsStream(propertiesFileName)) {
            if (is != null) {
                props.load(is);
                return props.getProperty(dependencyNameAsKey);
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }
}
