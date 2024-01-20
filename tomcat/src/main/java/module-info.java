module ch.qos.logback.access.jakarta.tomcat {
  requires ch.qos.logback.core;
  requires ch.qos.logback.access.jakarta.common;

  requires org.apache.tomcat.coyote;
  requires org.apache.tomcat.catalina;

  exports ch.qos.logback.access.jakarta.tomcat;
  
}

