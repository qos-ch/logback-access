/*
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2026, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v2.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */

package ch.qos.logback.access.common.blackbox.joran;

import ch.qos.logback.access.common.blackbox.CommonBlackboxConstants;
import ch.qos.logback.access.common.blackbox.testUtil.MiniStatusChecker;
import ch.qos.logback.access.common.joran.JoranConfigurator;
import ch.qos.logback.access.common.spi.AccessContext;
import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.testUtil.RandomUtil;
import ch.qos.logback.core.util.StatusPrinter2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ch.qos.logback.core.model.processor.FileCollisionAnalyser.COLLISION_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;

public class FileCollisionAnalyserTest {

    AccessContext accessContext = new AccessContext();
    MiniStatusChecker miniStatusChecker = new MiniStatusChecker(accessContext);
    int diff = RandomUtil.getPositiveInt();
    String outputTargetVal = CommonBlackboxConstants.OUTPUT_DIR_PREFIX + "collision/output-" + diff + ".log";
    String fileNamePatternVal = CommonBlackboxConstants.OUTPUT_DIR_PREFIX + "collision/output-%d{yyyy-MM-dd}-" + diff + ".log";

    StatusPrinter2 statusPrinter2 = new StatusPrinter2();

    @BeforeEach
    public void setUp() {
    }

    void configure(String file) throws JoranException {
        accessContext.putProperty("outputTargetKey", outputTargetVal);
        accessContext.putProperty("fileNamePatternKey", fileNamePatternVal);
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(accessContext);
        jc.doConfigure(file);
    }


    @Test
    void testFileCollision() throws JoranException {
        String configFile = CommonBlackboxConstants.JORAN_INPUT_PREFIX + "collision/repeatFile.xml";
        runCollisionTest(configFile, 1, 0, "file", outputTargetVal);
    }

    @Test
    void testRollingFileAppenderCollisionByFile() throws JoranException {
        String configFile = CommonBlackboxConstants.JORAN_INPUT_PREFIX + "collision/repeatRollingFileAppenderByFile.xml";
        runCollisionTest(configFile, 0, 1, "file", outputTargetVal);
    }

    @Test
    void testRollingFileAppenderCollisionByFilePattern() throws JoranException {
        String configFile = CommonBlackboxConstants.JORAN_INPUT_PREFIX + "collision/repeatRollingFileAppenderByFilePattern.xml";
        runCollisionTest(configFile, 0, 1, "fileNamePattern", fileNamePatternVal);
    }

    @Test
    public void testMixedFileaAppenderRollingFileAppenderCollisionByFile() throws JoranException {
        String configFile = CommonBlackboxConstants.JORAN_INPUT_PREFIX + "collision/repeatMixedFileAndRolling.xml";
        runCollisionTest(configFile, 0, 1, "file", outputTargetVal);
    }

    public void runCollisionTest(String configFile, int fileAppenderCount, int rollingAppenderCount, String tagName, String value) throws JoranException {
        configure(configFile);
        //statusPrinter2.print(accessContext);

        Appender<IAccessEvent> fileAppender1 = accessContext.getAppender("FILE1");

        assertNotNull(fileAppender1);

        Appender<IAccessEvent> fileAppender2 = accessContext.getAppender("FILE2");
        assertNull(fileAppender2);

        String expectationPattern = COLLISION_MESSAGE.replace("[", "\\[").replace("]", "\\]");

        String sanitizeValue = value.replace("{", "\\{").replace("}", "\\}");
        String expected = String.format(expectationPattern, "FILE2", tagName, sanitizeValue, "FILE1");
        //System.out.println(expected);
        assertTrue(miniStatusChecker.containsMatch(Status.ERROR, expected));
        assertEquals(fileAppenderCount, miniStatusChecker.matchCount("About to instantiate appender of type \\[" + FileAppender.class.getName() + "\\]"));
        assertEquals(rollingAppenderCount, miniStatusChecker.matchCount("About to instantiate appender of type \\[" + RollingFileAppender.class.getName() + "\\]"));


    }

}
