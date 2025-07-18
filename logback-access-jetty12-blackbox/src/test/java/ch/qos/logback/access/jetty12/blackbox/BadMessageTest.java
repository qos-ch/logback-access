package ch.qos.logback.access.jetty12.blackbox;

import java.nio.ByteBuffer;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BadMessageTest {
    private Server server;
    private LocalConnector connector;

    @BeforeEach
    public void startServer() throws Exception {
        server = new Server();
        connector = new LocalConnector(server);
        server.addConnector(connector);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        server.setHandler(context);
        server.start();
    }

    @AfterEach
    public void stopServer() throws Exception {
        server.stop();
    }

    @Disabled
    @Test
    public void testBadMethod() throws Exception {
        HttpTester.Request request = HttpTester.newRequest();
        request.setMethod("BAD"); // Triggers BadMessageException internally
        request.setURI("/");
        request.setVersion("HTTP/1.1");
        request.put(HttpHeader.HOST, "localhost");
        request.put(HttpHeader.CONNECTION, "close");
        ByteBuffer requestBuffer = request.generate();
        ByteBuffer responseBuffer = connector.getResponse(requestBuffer);
        HttpTester.Response response = HttpTester.parseResponse(responseBuffer);
        System.out.println(response.getStatus());
        assertEquals(400, response.getStatus());
    }
}
