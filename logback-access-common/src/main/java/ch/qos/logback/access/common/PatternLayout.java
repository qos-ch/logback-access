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
package ch.qos.logback.access.common;

import ch.qos.logback.access.common.pattern.ContentLengthConverter;
import ch.qos.logback.access.common.pattern.DateConverter;
import ch.qos.logback.access.common.pattern.ElapsedSecondsConverter;
import ch.qos.logback.access.common.pattern.ElapsedTimeConverter;
import ch.qos.logback.access.common.pattern.EnsureLineSeparation;
import ch.qos.logback.access.common.pattern.FullRequestConverter;
import ch.qos.logback.access.common.pattern.FullResponseConverter;
import ch.qos.logback.access.common.pattern.LineSeparatorConverter;
import ch.qos.logback.access.common.pattern.LocalIPAddressConverter;
import ch.qos.logback.access.common.pattern.LocalPortConverter;
import ch.qos.logback.access.common.pattern.NAConverter;
import ch.qos.logback.access.common.pattern.QueryStringConverter;
import ch.qos.logback.access.common.pattern.RemoteHostConverter;
import ch.qos.logback.access.common.pattern.RemoteIPAddressConverter;
import ch.qos.logback.access.common.pattern.RemoteUserConverter;
import ch.qos.logback.access.common.pattern.RequestAttributeConverter;
import ch.qos.logback.access.common.pattern.RequestContentConverter;
import ch.qos.logback.access.common.pattern.RequestCookieConverter;
import ch.qos.logback.access.common.pattern.RequestHeaderConverter;
import ch.qos.logback.access.common.pattern.RequestMethodConverter;
import ch.qos.logback.access.common.pattern.RequestParameterConverter;
import ch.qos.logback.access.common.pattern.RequestProtocolConverter;
import ch.qos.logback.access.common.pattern.RequestURIConverter;
import ch.qos.logback.access.common.pattern.RequestURLConverter;
import ch.qos.logback.access.common.pattern.ResponseContentConverter;
import ch.qos.logback.access.common.pattern.ResponseHeaderConverter;
import ch.qos.logback.access.common.pattern.ServerNameConverter;
import ch.qos.logback.access.common.pattern.SessionIDConverter;
import ch.qos.logback.access.common.pattern.StatusCodeConverter;
import ch.qos.logback.access.common.pattern.ThreadNameConverter;
import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.core.pattern.DynamicConverter;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import ch.qos.logback.core.pattern.color.*;
import ch.qos.logback.core.pattern.parser.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * <p>
 * This class is a module-specific implementation of
 * {@link PatternLayout} to allow http-specific patterns
 * to be used. The <code>ch.qos.logback.access.PatternLayout</code> provides a
 * way to format the logging output that is just as easy and flexible as the
 * usual <code>PatternLayout</code>.
 * </p>
 * <p/>
 * For more information about this layout, please refer to the online manual at
 * http://logback.qos.ch/manual/layouts.html#AccessPatternLayout
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author S&eacute;bastien Pennec
 */
public class PatternLayout extends PatternLayoutBase<IAccessEvent> {

    public static final Map<String, Supplier<DynamicConverter>> ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP = new HashMap<>();
    public static final String HEADER_PREFIX = "#logback.access pattern: ";

    public static final String CLF_PATTERN = "%h %l %u [%t] \"%r\" %s %b";
    public static final String CLF_PATTERN_NAME = "common";
    public static final String CLF_PATTERN_NAME_2 = "clf";
    public static final String COMBINED_PATTERN = "%h %l %u [%t] \"%r\" %s %b \"%i{Referer}\" \"%i{User-Agent}\"";
    public static final String COMBINED_PATTERN_NAME = "combined";

    static {
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.putAll(Parser.DEFAULT_COMPOSITE_CONVERTER_MAP);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("a", RemoteIPAddressConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("remoteIP", RemoteIPAddressConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("A", LocalIPAddressConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("localIP", LocalIPAddressConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("b", ContentLengthConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("B", ContentLengthConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("bytesSent", ContentLengthConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("h", RemoteHostConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("clientHost", RemoteHostConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("H", RequestProtocolConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("protocol", RequestProtocolConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("i", RequestHeaderConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("header", RequestHeaderConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("I", ThreadNameConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("threadName", ThreadNameConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("l", NAConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("m", RequestMethodConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("requestMethod", RequestMethodConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("q", QueryStringConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("queryString", QueryStringConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("r", RequestURLConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("requestURL", RequestURLConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("s", StatusCodeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("statusCode", StatusCodeConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("S", SessionIDConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("sessionID", SessionIDConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("t", DateConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("date", DateConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("u", RemoteUserConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("user", RemoteUserConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("U", RequestURIConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("requestURI", RequestURIConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("v", ServerNameConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("server", ServerNameConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("localPort", LocalPortConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("requestAttribute", RequestAttributeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("reqAttribute", RequestAttributeConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("reqCookie", RequestCookieConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("requestCookie", RequestCookieConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("responseHeader", ResponseHeaderConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("requestParameter", RequestParameterConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("reqParameter", RequestParameterConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("requestContent", RequestContentConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("responseContent", ResponseContentConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("fullRequest", FullRequestConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("fullResponse", FullResponseConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("elapsedTime", ElapsedTimeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("D", ElapsedTimeConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("elapsedSeconds", ElapsedSecondsConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("T", ElapsedSecondsConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("n", LineSeparatorConverter::new);

        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("black", BlackCompositeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("red", RedCompositeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("green", GreenCompositeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("yellow", YellowCompositeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("blue", BlueCompositeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("magenta", MagentaCompositeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("cyan", CyanCompositeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("white", WhiteCompositeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("gray", GrayCompositeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("boldRed", BoldRedCompositeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("boldGreen", BoldGreenCompositeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("boldYellow", BoldYellowCompositeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("boldBlue", BoldBlueCompositeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("boldMagenta", BoldMagentaCompositeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("boldCyan", BoldCyanCompositeConverter::new);
        ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP.put("boldWhite", BoldWhiteCompositeConverter::new);
    }

    public PatternLayout() {
        // set a default value for pattern
        setPattern(CLF_PATTERN);
        // by default postCompileProcessor is an EnsureLineSeparation instance
        this.postCompileProcessor = new EnsureLineSeparation();
    }

    /**
     * Returns the default converter map for this instance.
     */
    @Override
    public Map<String, Supplier<DynamicConverter>> getDefaultConverterSupplierMap() {
        return ACCESS_DEFAULT_CONVERTER_SUPPLIER_MAP;
    }

    @Override
    public Map<String, String> getDefaultConverterMap() {
        return Map.of();
    }

    @Override
    public String doLayout(IAccessEvent event) {
        if (!isStarted()) {
            return null;
        }
        return writeLoopOnConverters(event);
    }

    @Override
    public void start() {
        if (getPattern().equalsIgnoreCase(CLF_PATTERN_NAME) || getPattern().equalsIgnoreCase(CLF_PATTERN_NAME_2)) {
            setPattern(CLF_PATTERN);
        } else if (getPattern().equalsIgnoreCase(COMBINED_PATTERN_NAME)) {
            setPattern(COMBINED_PATTERN);
        }
        super.start();
    }

    @Override
    protected String getPresentationHeaderPrefix() {
        return HEADER_PREFIX;
    }
}
