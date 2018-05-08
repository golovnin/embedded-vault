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

import de.flapdoodle.embed.process.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.config.store.DownloadConfigBuilder;
import de.flapdoodle.embed.process.extract.ITempNaming;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.directories.PropertyOrPlatformTempDir;
import de.flapdoodle.embed.process.io.directories.UserHome;
import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import de.flapdoodle.embed.process.store.Downloader;
import de.flapdoodle.embed.process.store.ExtractedArtifactStoreBuilder;

/**
 * @author Andrej Golovnin
 */
final public class VaultBuilders {

    private VaultBuilders() {
        // NOP
    }

    static DownloadConfigBuilder downloadConfigBuilder() {
        return new DownloadConfigBuilder()
            .fileNaming(new UUIDTempNaming())
            .downloadPath("https://releases.hashicorp.com/vault/")
            .progressListener(new StandardConsoleProgressListener())
            .artifactStorePath(new UserHome(".embedded-vault"))
            .downloadPrefix("embedded-vault-download")
            .packageResolver(new VaultPackageResolver())
            .userAgent("Mozilla/5.0 (compatible; Embedded Vault; +https://github.com/golovnin/embedded-vault)");
    }

    static RuntimeConfigBuilder runtimeConfigBuilder() {
        return runtimeConfigBuilder(ProcessOutput.getDefaultInstance("default"), new ICommandLinePostProcessor.Noop());
    }

    public static RuntimeConfigBuilder runtimeConfigBuilder(ProcessOutput processOutput, ICommandLinePostProcessor commandLinePostProcessor) {
        return new RuntimeConfigBuilder()
                .processOutput(processOutput)
                .commandLinePostProcessor(commandLinePostProcessor)
                .artifactStore(storeBuilder().build());
    }

    static ExtractedArtifactStoreBuilder storeBuilder() {
        return new ExtractedArtifactStoreBuilder()
            .extractDir(new UserHome(".embedded-vault/extracted"))
            .extractExecutableNaming(new OriginNaming())
            .tempDir(new PropertyOrPlatformTempDir())
            .executableNaming(new UUIDTempNaming())
            .download(downloadConfigBuilder().build())
            .downloader(new Downloader());
    }

    private static final class OriginNaming implements ITempNaming {

        @Override
        public String nameFor(String prefix, String postfix) {
            return postfix;
        }

    }

}
