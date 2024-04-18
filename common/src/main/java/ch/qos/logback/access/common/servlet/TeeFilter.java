/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.access.common.servlet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.access.common.AccessConstants;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TeeFilter implements Filter {

    boolean active;

    @Override
    public void destroy() {
        // NOP
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        if (active && request instanceof HttpServletRequest) {
            TeeHttpServletRequest teeRequest = new TeeHttpServletRequest((HttpServletRequest) request);
            TeeHttpServletResponse teeResponse = new TeeHttpServletResponse((HttpServletResponse) response);

            try {
                filterChain.doFilter(teeRequest, teeResponse);
            } finally {
                teeResponse.finish();
                // let the output contents be available for later use by
                // logback-access-logging
                teeRequest.setAttribute(AccessConstants.LB_OUTPUT_BUFFER, teeResponse.getOutputBuffer());
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String includeListAsStr = filterConfig.getInitParameter(AccessConstants.TEE_FILTER_INCLUDES_PARAM);
        String excludeListAsStr = filterConfig.getInitParameter(AccessConstants.TEE_FILTER_EXCLUDES_PARAM);
        String localhostName = getLocalhostName();

        active = computeActivation(localhostName, includeListAsStr, excludeListAsStr);
        if (active)
            logInfo("TeeFilter will be ACTIVE on this host [" + localhostName + "]");
        else
            logInfo("TeeFilter will be DISABLED on this host [" + localhostName + "]");

    }

    public static List<String> extractNameList(String nameListAsStr) {
        List<String> nameList = new ArrayList<String>();
        if (nameListAsStr == null) {
            return nameList;
        }

        nameListAsStr = nameListAsStr.trim();
        if (nameListAsStr.length() == 0) {
            return nameList;
        }

        String[] nameArray = nameListAsStr.split("[,;]");
        for (String n : nameArray) {
            n = n.trim();
            nameList.add(n);
        }
        return nameList;
    }

    String getLocalhostName() {
        String hostname = "127.0.0.1";

        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException uhe) {
            logWarn("Unknown host", uhe);
        }
        return hostname;
    }

    public static boolean computeActivation(String hostname, String includeListAsStr, String excludeListAsStr) {
        List<String> includeList = extractNameList(includeListAsStr);
        List<String> excludeList = extractNameList(excludeListAsStr);
        boolean inIncludesList = mathesIncludesList(hostname, includeList);
        boolean inExcludesList = mathesExcludesList(hostname, excludeList);
        return inIncludesList && (!inExcludesList);
    }

    static boolean mathesIncludesList(String hostname, List<String> includeList) {
        if (includeList.isEmpty())
            return true;
        return includeList.contains(hostname);
    }

    static boolean mathesExcludesList(String hostname, List<String> excludesList) {
        if (excludesList.isEmpty())
            return false;
        return excludesList.contains(hostname);
    }

    /**
     * Log a warning.
     *
     * Can be overwritten to use a logger.
     *
     * @param msg The message.
     * @param ex The exception.
     */
    protected void logWarn(String msg, Throwable ex) {
        System.err.println(msg + ": " + ex);
    }

    /**
     * Log an info message.
     *
     * Can be overwritten to use a logger.
     *
     * @param msg The message to log.
     */
    protected void logInfo(String msg) {
        System.out.println(msg);
    }
}
