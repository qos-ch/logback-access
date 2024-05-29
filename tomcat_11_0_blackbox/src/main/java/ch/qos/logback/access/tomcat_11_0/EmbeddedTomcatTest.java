package ch.qos.logback.access.tomcat_11_0;

import ch.qos.logback.access.common.servlet.TeeFilter;
import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.access.tomcat.LogbackValve;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
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
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName(TeeFilter.class.getSimpleName());
        filterDef.setFilterClass(TeeFilter.class.getName());
        context.addFilterDef(filterDef);

        FilterMap myFilterMap = new FilterMap();
        myFilterMap.setFilterName(TeeFilter.class.getSimpleName());
        myFilterMap.addURLPattern("/*");
        context.addFilterMap(myFilterMap);

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
        HttpURLConnection connection = hgu.connect();
        String response = hgu.readResponse(connection);
        System.out.println("==========================");
        System.out.println(response);
        assertEquals(text, response);
        ListAppender nla = (ListAppender) logbackValve.getAppender("LIST");
        assertEquals(1, nla.list.size());

        IAccessEvent event = nla.list.get(0);
        assertEquals(uri, event.getRequestURI());
    }

}
