package ch.qos.logback.access.jetty12.blackbox;

import ch.qos.logback.access.jetty12.blackbox.PostOnlyServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostOnlyServletTest {
    private static Server server;
    private static URI baseUri;

    @BeforeAll
    static void startServer() throws Exception {
        server = new Server(0);
        ServletContextHandler context = new ServletContextHandler( );
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new PostOnlyServlet()), "/post");
        server.start();
        baseUri = server.getURI();
    }

    @AfterAll
    static void stopServer() throws Exception {
        server.stop();
    }

    @Test
    void testPostSuccess() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder().uri(baseUri.resolve("/post")).POST(HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());
    }

    @Test
    void testGetFails() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder().uri(baseUri.resolve("/post")).build(); // GET
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, resp.statusCode());
    }
}
