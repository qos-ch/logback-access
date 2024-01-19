package ch.qos.logback.access.jakarta.net;

import java.io.IOException;
import java.io.InputStream;

import ch.qos.logback.access.jakarta.spi.AccessEvent;
import ch.qos.logback.core.net.HardenedObjectInputStream;

public class HardenedAccessEventInputStream extends HardenedObjectInputStream {

    public HardenedAccessEventInputStream(InputStream in) throws IOException {
        super(in, new String[] { AccessEvent.class.getName(), String[].class.getName() });
    }

}
