package ch.qos.logback.access.common.blackbox.util;

import ch.qos.logback.access.common.util.AccessCommonVersionUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccessCommonVersionUtilTest {


    @Test
    void smoke() {
        String version = AccessCommonVersionUtil.getAccessCommonVersionBySelfDeclaredProperties();
        assertNotNull(version);
        assertTrue(version.startsWith("2.0"));
    }
}
