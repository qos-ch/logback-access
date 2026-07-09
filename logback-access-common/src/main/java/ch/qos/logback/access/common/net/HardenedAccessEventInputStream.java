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

package ch.qos.logback.access.common.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamClass;

import ch.qos.logback.access.common.spi.AccessEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.net.HardenedObjectInputStream;
import jakarta.servlet.http.Cookie;

public class HardenedAccessEventInputStream extends HardenedObjectInputStream {

    static String[] ACCESS_WHITE_LIST = new String[] { AccessEvent.class.getName(),
            String[].class.getName(),
            Cookie.class.getName(),
            "java.util.CollSer",
            "java.util.TreeMap",
            "java.lang.String$CaseInsensitiveComparator"};

    public HardenedAccessEventInputStream(Context context, InputStream in) throws IOException {
        super(context, in, ACCESS_WHITE_LIST);
    }


}
