/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.access.tomcat;

import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.testUtil.StatusChecker;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.ContainerBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LogbackValveTest {

    LogbackValve valve = new LogbackValve();
    StatusChecker checker = new StatusChecker(valve);

    @AfterEach
    public void tearDown() {
        System.clearProperty(LogbackValve.CATALINA_BASE_KEY);
        System.clearProperty(LogbackValve.CATALINA_HOME_KEY);
    }

    @Test
    public void nonExistingConfigFileShouldResultInWarning() throws LifecycleException {
        final String resourceName = "logback-test2-config.xml";
        setupValve(resourceName);
        valve.start();
        checker.assertContainsMatch(Status.WARN, "Failed to find valid");
    }

    @Test
    public void fileUnderCatalinaBaseShouldBeFound() throws LifecycleException {
        System.setProperty(LogbackValve.CATALINA_BASE_KEY, TomcatTestContants.JORAN_INPUT_PREFIX + "tomcat/");
        final String fileName = "logback-access.xml";
        setupValve(fileName);
        valve.start();

        checker.assertContainsMatch("Found configuration file");
        checker.assertContainsMatch("Done configuring");
        checker.assertIsErrorFree();
    }
    @Test
    public void fileUnderCatalinaBaseWithInclusionsShouldBeFound() throws LifecycleException {
        System.setProperty(LogbackValve.CATALINA_BASE_KEY, TomcatTestContants.JORAN_INPUT_PREFIX + "tomcat/");
        final String fileName = "logback-access-including.xml";
        setupValve(fileName);
        valve.start();

        assertEquals("value_of_var_tomcat_inclusion", System.getProperty("var_tomcat_inclusion"));

        checker.assertContainsMatch("Found configuration file");
        checker.assertContainsMatch("Done configuring");
        checker.assertIsErrorFree();
    }

    @Test
    public void nonExistingConfigFileUnderCatalinaBaseShouldResultInWarning() throws LifecycleException {
        final String path = TomcatTestContants.JORAN_INPUT_PREFIX + "tomcat/";
        final String fileName = "logback-test2-config.xml";
        final String fullPath = path + fileName;
        System.setProperty(LogbackValve.CATALINA_BASE_KEY, path);
        setupValve(fileName);
        valve.start();
        checker.assertContainsMatch("Could NOT find configuration file");
        checker.assertIsErrorFree();
    }

    @Test
    public void fileUnderCatalinaHomeShouldBeFound() throws LifecycleException {
        System.setProperty(LogbackValve.CATALINA_HOME_KEY, TomcatTestContants.JORAN_INPUT_PREFIX + "tomcat/");
        final String fileName = "logback-access.xml";
        setupValve(fileName);
        valve.start();
        checker.assertContainsMatch("Found configuration file");
        checker.assertContainsMatch("Done configuring");
        checker.assertIsErrorFree();
    }

    @Test
    public void resourceShouldBeFound() throws LifecycleException {
        final String fileName = "logback-asResource.xml";
        setupValve(fileName);
        valve.start();
        checker.assertContainsMatch("Found ." + fileName + ". as a resource.");
        checker.assertContainsMatch("Done configuring");
        checker.assertIsErrorFree();
    }

    @Test
    public void executorServiceShouldBeNotNull() throws LifecycleException {
        final String fileName = "logback-asResource.xml";
        setupValve(fileName);
        valve.start();
        assertNotNull(valve.getScheduledExecutorService());

    }

    private void setupValve(final String resourceName) {
        valve.setFilename(resourceName);
        valve.setName("test");
        valve.setContainer(new ContainerBase() {
            @Override
            protected String getObjectNameKeyProperties() {
                return "getObjectNameKeyProperties-test";
            }
        });
    }
}