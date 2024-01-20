package ch.qos.logback.access.jakarta.tomcat_10_1;

import ch.qos.logback.access.jakarta.tomcat.LogbackValve;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmbeddedTomcatTest {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    Tomcat tomcat = new Tomcat();
    Server server = tomcat.getServer();
    int port = 8888;
    LogbackValve logbackValve = new LogbackValve();

    String text = "<html><body><p>Welcome</p></html></body>";

    @BeforeEach
    public void embed() throws LifecycleException {
        tomcat.setBaseDir("/tmp");
        //tomcat.setPort(port);
        Connector connector = tomcat.getConnector();
        connector.setPort(port);
        tomcat.setConnector(connector);//getService().addConnector(connector);
        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();
        Context context = tomcat.addContext(contextPath, docBase);
        String servletName = "SampleServlet";
        String urlPattern = "/";

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
        //        logger.atInfo().setMessage("tearDown").log();
        Thread.sleep(1000);
        ///tomcat.stop();
        server.setShutdown("shutdown");
    }

    @Test
    public void smoke() throws MalformedURLException, InterruptedException {
        Thread.sleep(1000);
        HttpGetUtil hgu = new HttpGetUtil("http://127.0.0.1:" + port + "/");
        HttpURLConnection connection = hgu.connect();
        String response = hgu.readResponse(connection);
        System.out.println("==========================");
        System.out.println(response);
        assertEquals(text, response);
    }

}
