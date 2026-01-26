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
package ch.qos.logback.access.common.pattern;

import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.core.util.OptionHelper;

public class RequestHeaderConverter extends AccessConverter {

    String key;

    @Override
    public void start() {
        key = getFirstOption();
        if (OptionHelper.isNullOrEmpty(key)) {
            addWarn("Missing key for the requested header. Defaulting to all keys.");
            key = null;
        }
        super.start();
    }

    @Override
    public String convert(IAccessEvent accessEvent) {
        if (!isStarted()) {
            return "INACTIVE_HEADER_CONV";
        }

        if (key != null) {
            return accessEvent.getRequestHeader(key);
        } else {
            return accessEvent.getRequestHeaderMap().toString();
        }
    }

}
