/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 * <p>
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 * <p>
 * or (per the licensee's choosing)
 * <p>
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.access.jetty;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ch.qos.logback.access.common.spi.ServerAdapter;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;

/**
 * A Jetty 9.4.x and 10.0.x specific implementation of the {@link ServerAdapter} interface.
 *
 * @author S&eacute;bastien Pennec
 * @author Ceki Gulcu
 * @author Joakim Erdfelt
 */
public class JettyModernServerAdapter extends JettyServerAdapter {


    public JettyModernServerAdapter(Request jettyRequest, Response jettyResponse) {
        super(jettyRequest, jettyResponse);
    }

    @Override
    public long getContentLength() {

        return Response.getContentBytesWritten(response);
        //return response.getHttpChannel().getBytesWritten();
    }

    @Override
    public int getStatusCode() {
        return response.getStatus();
        //return response.getCommittedMetaData().getStatus();
    }

    @Override
    public long getRequestTimestamp() {
        return Request.getTimeStamp(request);
    }

    @Override
    public Map<String, String> buildResponseHeaderMap() {
        Map<String, String> responseHeaderMap = new HashMap<>();
        HttpFields.Mutable httpFields = response.getHeaders();

        for(HttpField field: httpFields) {
            String key = field.getName();
            String value = field.getValue();
            responseHeaderMap.put(key, value);
        }

        return responseHeaderMap;
    }

}
