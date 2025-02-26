package ch.qos.logback.access.jetty;

import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;

import java.util.Map;
import java.util.TreeMap;

class HeaderUtil {
    static Map<String, String> buildHeaderMap(HttpFields headers) {
        Map<String, String> requestHeaderMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (HttpField f : headers) {
            String existing = requestHeaderMap.get(f.getName());
            String value = combine(existing, f.getValue());
            requestHeaderMap.put(f.getName(), value);
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
