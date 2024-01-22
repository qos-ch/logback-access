package ch.qos.logback.access.common.spi;

import java.util.Map;

public interface WrappedHttpRequest {

    String getSessionID();

    Map<String, String> getRequestHeaderMap();

    Map<String, String[]> buildRequestParameterMap();

}
