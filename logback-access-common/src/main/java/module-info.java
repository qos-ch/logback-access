module ch.qos.logback.access.common {
  requires ch.qos.logback.core;

  // jakarta.servlet 5.0 is not modular but 6.0 is
  requires static jakarta.servlet;

  requires static java.management;

  requires static janino;
  requires static commons.compiler;

  exports ch.qos.logback.access.common;
  exports ch.qos.logback.access.common.filter;

  exports ch.qos.logback.access.common.html;
  exports ch.qos.logback.access.common.joran;
  exports ch.qos.logback.access.common.joran.action;

  exports ch.qos.logback.access.common.model;
  exports ch.qos.logback.access.common.model.processor;

  exports ch.qos.logback.access.common.net;
  exports ch.qos.logback.access.common.net.server;

  exports ch.qos.logback.access.common.pattern;
  exports ch.qos.logback.access.common.servlet;
  exports ch.qos.logback.access.common.sift;

  exports ch.qos.logback.access.common.spi;

}

