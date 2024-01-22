module ch.qos.logback.access.jetty {
    requires ch.qos.logback.core;
    requires ch.qos.logback.access.common;

    requires org.eclipse.jetty.server;

    // servlet api
    requires jakarta.servlet;
    requires org.eclipse.jetty.http;

    exports ch.qos.logback.access.jetty;

}

