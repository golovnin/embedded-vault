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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.io.LogWatchStreamProcessor;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.StreamToLineProcessor;
import de.flapdoodle.embed.process.io.file.Files;
import de.flapdoodle.embed.process.runtime.AbstractProcess;
import de.flapdoodle.embed.process.runtime.ProcessControl;

/**
 * @author Andrej Golovnin
 */
public final class VaultServerProcess
    extends AbstractProcess<VaultServerConfig, VaultServerExecutable, VaultServerProcess>
{

    private static final String SUCCESS_MESSAGE =
        "==> Vault server started!";

    private static final Set<String> KNOWN_FAILURE_MESSAGES =
        Collections.singleton("Error ");


    private File configFile;


    VaultServerProcess(Distribution distribution, VaultServerConfig config,
        IRuntimeConfig runtimeConfig, VaultServerExecutable executable)
        throws IOException
    {
        super(distribution, config, runtimeConfig, executable);
    }

    @Override
    protected List<String> getCommandLine(Distribution distribution,
        VaultServerConfig config, IExtractedFileSet files) throws IOException
    {
        configFile = File.createTempFile("embedded-vault-config", ".json");
        try (
            OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(configFile), "UTF-8")
        ) {
            writer.write(config.toJson());
        }
        String listenerHost = config.getListenerHost();
        String listenerPort = String.valueOf(config.getListenerPort());
        String rootTokenID = config.getRootTokenID();
        String logLevel = config.getLogLevel().toConsulValue();
        return Arrays.asList(
            Files.fileOf(files.baseDir(), files.executable()).getAbsolutePath(),
            "server",
            "-dev",
            "-dev-root-token-id=" + rootTokenID,
            "-dev-listen-address=" + listenerHost + ':' + listenerPort,
            "-config=" + configFile.getAbsolutePath(),
            "-log-level=" + logLevel);
    }

    @Override
    protected void onAfterProcessStart(ProcessControl process,
        IRuntimeConfig runtimeConfig) throws IOException
    {
        ProcessOutput outputConfig = runtimeConfig.getProcessOutput();
        LogWatchStreamProcessor logWatch = new LogWatchStreamProcessor(
            SUCCESS_MESSAGE, KNOWN_FAILURE_MESSAGES,
            StreamToLineProcessor.wrap(outputConfig.getOutput()));
        Processors.connect(process.getReader(), logWatch);
        Processors.connect(process.getError(),
            StreamToLineProcessor.wrap(outputConfig.getError()));
        logWatch.waitForResult(getConfig().getStartupTimeout());
        if (logWatch.isInitWithSuccess()) {
            setProcessId(getProcessId());
        } else {
            String failureFound = logWatch.getFailureFound();
            if (failureFound == null) {
                failureFound = "\n----------------------\n"
                             + "The failure message was not found.\n"
                             + "The process output may contain the cause:\n"
                             + logWatch.getOutput();
            }
            try {
                if (process.waitFor() != 0) {
                    throw new IOException(
                        "Could not start process: " + failureFound);
                }
            } catch (InterruptedException e) {
                throw new IOException(
                    "Could not start process: " + failureFound, e);
            }
        }
    }

    @Override
    protected void stopInternal() {
        sendKillToProcess();
    }

    @Override
    protected void cleanupInternal() {
        Files.forceDelete(configFile);
    }

}
