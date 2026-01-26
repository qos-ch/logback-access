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

import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;

import java.util.Map;
import java.util.TreeMap;

/**
 * <p>A utility class that builds a map from HttpFields (headers).</p>
 *
 * <p>In case a homonymous header exists, it is merged into the existing value by concatenation. </p>
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Robert Elliot
 * @since 2.0.7
 */
class HeaderUtil {
    static Map<String, String> buildHeaderMap(HttpFields headers) {
        Map<String, String> requestHeaderMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (HttpField httpField : headers) {
            String existing = requestHeaderMap.get(httpField.getName());
            String value = combine(existing, httpField.getValue());
            requestHeaderMap.put(httpField.getName(), value);
        }
        return requestHeaderMap;
    }

    private static String combine(String existing, String field) {
        if (existing == null) {
            return field;
        } else {
            return existing + "," + field;
        }
    }
}
