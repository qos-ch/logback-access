module ch.qos.logback.access.common.blackbox {

    requires ch.qos.logback.core;
    requires ch.qos.logback.access.common;

    requires org.junit.jupiter.api;

    requires org.junit.jupiter.engine;
    requires org.junit.jupiter.params;
    requires org.assertj.core;

    requires janino;
    requires jakarta.servlet;

    opens ch.qos.logback.access.common.blackbox.boolex to org.junit.platform.commons;
    opens ch.qos.logback.access.common.blackbox.joran to org.junit.platform.commons;
    opens ch.qos.logback.access.common.blackbox.net to org.junit.platform.commons;
    opens ch.qos.logback.access.common.blackbox.pattern to org.junit.platform.commons;
    opens ch.qos.logback.access.common.blackbox.servlet to org.junit.platform.commons;

    opens ch.qos.logback.access.common.blackbox.spi to org.junit.platform.commons;



}