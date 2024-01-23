package ch.qos.logback.access.common.spi;

import java.util.Map;

public interface WrappedHttpRequest {

    String getSessionID();

    Map<String, String> buildRequestHeaderMap();

    Map<String, String[]> buildRequestParameterMap();

}
