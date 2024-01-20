module ch.qos.logback.access.jakarta.common {
  requires ch.qos.logback.core;

  // jakarta.servlet 5.0 is not modular
  requires static jakarta.servlet;

  requires static java.management;

  exports ch.qos.logback.access.jakarta.common;
  exports ch.qos.logback.access.jakarta.common.boolex;
  exports ch.qos.logback.access.jakarta.common.filter;

  exports ch.qos.logback.access.jakarta.common.html;
  exports ch.qos.logback.access.jakarta.common.joran;
  exports ch.qos.logback.access.jakarta.common.joran.action;

  exports ch.qos.logback.access.jakarta.common.model;
  exports ch.qos.logback.access.jakarta.common.model.processor;

  exports ch.qos.logback.access.jakarta.common.net;
  exports ch.qos.logback.access.jakarta.common.net.server;

  exports ch.qos.logback.access.jakarta.common.pattern;
  exports ch.qos.logback.access.jakarta.common.servlet;
  exports ch.qos.logback.access.jakarta.common.sift;

  exports ch.qos.logback.access.jakarta.common.spi;


}

