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
package ch.qos.logback.access.common.blackbox.net;

import java.io.IOException;
import java.io.ObjectOutputStream;

import ch.qos.logback.access.common.blackbox.dummy.DummyAccessEventBuilder;
import ch.qos.logback.access.common.spi.IAccessEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

@Disabled
public class SerializationPerfTest {

    ObjectOutputStream oos;

    int loopNumber = 10000;
    int resetFrequency = 100;
    int pauseFrequency = 10;
    long pauseLengthInMillis = 20;

    @BeforeEach
    public void setUp() throws Exception {
        oos = new ObjectOutputStream(new NOPOutputStream());
    }

    @AfterEach
    public void tearDown() throws Exception {
        oos.close();
        oos = null;
    }

    @Test
    public void test1() throws Exception {
        // first run for just in time compiler
        int resetCounter = 0;
        int pauseCounter = 0;
        for (int i = 0; i < loopNumber; i++) {
            try {
                IAccessEvent ae = DummyAccessEventBuilder.buildNewDefaultAccessEvent();
                // average time for the next method: 5000 nanos
                ae.prepareForDeferredProcessing();
                oos.writeObject(ae);
                oos.flush();
                if (++resetCounter >= resetFrequency) {
                    oos.reset();
                    resetCounter = 0;
                }
                if (++pauseCounter >= pauseFrequency) {
                    Thread.sleep(pauseLengthInMillis);
                    pauseCounter = 0;
                }

            } catch (IOException ex) {
                fail(ex.getMessage());
            }
        }

        // second run
        Long t1;
        Long t2;
        Long total = 0L;
        resetCounter = 0;
        pauseCounter = 0;
        // System.out.println("Beginning measured run");
        for (int i = 0; i < loopNumber; i++) {
            try {
                IAccessEvent ae = DummyAccessEventBuilder.buildNewDefaultAccessEvent();
                t1 = System.nanoTime();
                // average length of the next method: 4000 nanos
                ae.prepareForDeferredProcessing();
                oos.writeObject(ae);
                oos.flush();
                t2 = System.nanoTime();
                total += (t2 - t1);
                if (++resetCounter >= resetFrequency) {
                    oos.reset();
                    resetCounter = 0;
                }
                if (++pauseCounter >= pauseFrequency) {
                    Thread.sleep(pauseLengthInMillis);
                    pauseCounter = 0;
                }
            } catch (IOException ex) {
                fail(ex.getMessage());
            }
        }

        total /= (1000);// nanos -> micros
        System.out.println(
                "Loop done : average time = " + total / loopNumber + " microsecs after " + loopNumber + " writes.");
        // average time: 26-30 microsec = 0.030 millis
    }

}
