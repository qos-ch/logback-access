package ch.qos.logback.access.tomcat_11_0;

import ch.qos.logback.access.common.HttpGetUtil;
import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.access.tomcat.LogbackValve;
import ch.qos.logback.core.testUtil.RandomUtil;
import jakarta.servlet.http.Cookie;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EmbeddedTomcatTest {

    Tomcat tomcat;
    int port;
    LogbackValve logbackValve;

    String text = "<html><body><p>Welcome</p></html></body>";

    @BeforeEach
    public void embed() throws LifecycleException {
        this.tomcat = new Tomcat();
        this.port = RandomUtil.getRandomServerPort();;
        this.logbackValve = new LogbackValve();

        tomcat.setBaseDir("/tmp");
        //tomcat.setPort(port);
        Connector connector = tomcat.getConnector();
        connector.setPort(port);
        tomcat.setConnector(connector);//getService().addConnector(connector);
        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();
        Context context = tomcat.addContext(contextPath, docBase);
        String servletName = "SampleServlet";
        String urlPattern = "/*";

        setupValve("logback-stdout.xml");
        //logbackValve.setContainer(context);
        context.getPipeline().addValve(logbackValve);

        tomcat.addServlet(contextPath, servletName, new SampleServlet(text));
        context.addServletMappingDecoded(urlPattern, servletName);
        tomcat.start();

    }

    private void setupValve(String fileName) {
        logbackValve.setFilename(fileName);
        logbackValve.setName("test");
    }

    @AfterEach
    public void tearDown() throws LifecycleException, InterruptedException {
        tomcat.stop();
    }

    @Test
    public void smoke() throws MalformedURLException, InterruptedException {
       String uri = "/moo";
        HttpGetUtil hgu = new HttpGetUtil("http://127.0.0.1:" + port + uri);
        HttpURLConnection connection = hgu.init().connect();
        String response = hgu.readResponse();
        System.out.println("==========================");
        System.out.println(response);
        assertEquals(text, response);
        ListAppender nla = (ListAppender) logbackValve.getAppender("LIST");
        assertEquals(1, nla.list.size());

        IAccessEvent event = nla.list.get(0);
        assertEquals(uri, event.getRequestURI());
    }

    @Test
    public void cookies() throws MalformedURLException, InterruptedException {
        String uri = "/cookies";
        ch.qos.logback.access.common.HttpGetUtil hgu = new ch.qos.logback.access.common.HttpGetUtil("http://127.0.0.1:" + port + uri);
        hgu.init().addCookie("k0=v0").addCookie("k1=v1").connect();

        String response = hgu.readResponse();
        hgu.disconnect();
        System.out.println("==========================");
        System.out.println(response);
        assertEquals(text, response);
        ListAppender nla = (ListAppender) logbackValve.getAppender("LIST");
        assertEquals(1, nla.list.size());

        IAccessEvent event = nla.list.get(0);
        assertEquals(uri, event.getRequestURI());

        List<Cookie> cookies =  event.getCookies();
        assertNotNull(cookies);

        assertEquals(2, cookies.size());
        for(int i = 0; i < 2 ; i++) {
            assertEquals("k" + i, cookies.get(i).getName());
            assertEquals("v" + i, cookies.get(i).getValue());
        }
    }

}
