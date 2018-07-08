/*
 * Copyright (c) 2017, Andrej Golovnin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of fontviewer nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.golovnin.embedded.vault;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

/**
 * @author Andrej Golovnin
 */
@RunWith(Parameterized.class)
public class VaultServerStarterTest {

    @Parameterized.Parameters
    public static List<VaultLogLevel> logLevels() {
        return Arrays.asList(VaultLogLevel.values());
    }

    @Parameterized.Parameter
    public VaultLogLevel logLevel;

    @Test
    public void testDefault() throws IOException {
        VaultServerConfig config = new VaultServerConfig.Builder()
            .logLevel(logLevel)
            .build();
        VaultServerStarter starter = VaultServerStarter.getDefaultInstance();
        VaultServerExecutable executable = starter.prepare(config);
        try {
            VaultServerProcess process = executable.start();
            assertTrue(process.isProcessRunning());
            process.stop();
        } finally {
            executable.stop();
        }
    }

    @Test
    public void testUnsealKey() throws IOException {
        VaultServerConfig config = new VaultServerConfig.Builder()
            .logLevel(logLevel)
            .build();
        VaultServerStarter starter = VaultServerStarter.getDefaultInstance();
        VaultServerExecutable executable = starter.prepare(config);
        try {
            VaultServerProcess process = executable.start();
            assertTrue(process.isProcessRunning());
            assertNotNull(process.getUnsealKey());
            process.stop();
        } finally {
            executable.stop();
        }
    }

    @Test
    public void testOutputConsumer() throws IOException {
        AtomicBoolean b = new AtomicBoolean();
        VaultServerConfig config = new VaultServerConfig.Builder()
            .logLevel(logLevel)
            .outConsumer(s -> b.set(true))
            .build();
        VaultServerStarter starter = VaultServerStarter.getDefaultInstance();
        VaultServerExecutable executable = starter.prepare(config);
        try {
            VaultServerProcess process = executable.start();
            assertTrue(process.isProcessRunning());
            process.stop();
        } finally {
            executable.stop();
        }
        assertTrue(b.get());
    }

    @Test
    public void testErrorConsumer() throws IOException {
        AtomicBoolean b = new AtomicBoolean();
        VaultServerConfig config = new VaultServerConfig.Builder()
            .logLevel(logLevel)
            .errConsumer(s -> b.set(true))
            .build();
        VaultServerStarter starter = VaultServerStarter.getDefaultInstance();
        VaultServerExecutable executable = starter.prepare(config);
        try {
            VaultServerProcess process = executable.start();
            assertTrue(process.isProcessRunning());
            process.stop();
        } finally {
            executable.stop();
        }
        if (logLevel.compareTo(VaultLogLevel.WARN) < 0) {
            assertTrue(b.get());
        }
    }

    @Test
    public void testDefaultWithRandomPort() throws IOException {
        VaultServerConfig config = new VaultServerConfig.Builder()
            .randomPort()
            .logLevel(logLevel)
            .build();
        assertNotEquals(8200, config.getListenerPort());
        VaultServerStarter starter = VaultServerStarter.getDefaultInstance();
        VaultServerExecutable executable = starter.prepare(config);
        try {
            VaultServerProcess process = executable.start();
            assertTrue(process.isProcessRunning());
            process.stop();
        } finally {
            executable.stop();
        }
    }

    @Test
    public void testCustomVersion() throws IOException {
        VaultServerConfig config = new VaultServerConfig.Builder()
            .version("0.7.2")
            .logLevel(logLevel)
            .build();
        VaultServerStarter starter = VaultServerStarter.getDefaultInstance();
        VaultServerExecutable executable = starter.prepare(config);
        try {
            VaultServerProcess process = executable.start();
            assertTrue(process.isProcessRunning());
            process.stop();
        } finally {
            executable.stop();
        }
    }

}
