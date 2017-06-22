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

import de.flapdoodle.embed.process.config.store.FileSet;
import de.flapdoodle.embed.process.config.store.FileType;
import de.flapdoodle.embed.process.config.store.IPackageResolver;
import de.flapdoodle.embed.process.distribution.ArchiveType;
import de.flapdoodle.embed.process.distribution.Distribution;

/**
 * @author Andrej Golovnin
 */
final class VaultPackageResolver implements IPackageResolver {

    @Override
    public FileSet getFileSet(Distribution distribution) {
        String executableName = "vault";
        switch (distribution.getPlatform()) {
            case FreeBSD:
            case Linux:
            case OS_X:
            case Solaris:
                // Nothing to do for Unix systems.
                break;
            case Windows:
                executableName += ".exe";
                break;
            default:
                throw new IllegalArgumentException(
                    "Unknown platform " + distribution.getPlatform());
        }
        return FileSet.builder()
            .addEntry(FileType.Executable, executableName)
            .build();
    }

    @Override
    public ArchiveType getArchiveType(Distribution distribution) {
        return ArchiveType.ZIP;
    }

    @Override
    public String getPath(Distribution distribution) {
        String version = distribution.getVersion().asInDownloadPath();
        String arch;
        // Special handling for ARM architecture.
        if (System.getProperty("os.arch").startsWith("arm")) {
            arch = "arm";
        } else {
            switch (distribution.getBitsize()) {
                case B32:
                    arch = "386";
                    break;
                case B64:
                    arch = "amd64";
                    break;
                default:
                    throw new IllegalArgumentException(
                        "Unknown bit size " + distribution.getBitsize());
            }
        }
        String platform;
        switch (distribution.getPlatform()) {
            case OS_X:
                platform = "darwin";
                break;
            case FreeBSD:
                platform = "freebsd";
                break;
            case Linux:
                platform = "linux";
                break;
            case Solaris:
                platform = "solaris";
                break;
            case Windows:
                platform = "windows";
                break;
            default:
                throw new IllegalArgumentException(
                    "Unknown platform " + distribution.getPlatform());
        }
        return version + "/vault_" + version + '_' + platform + '_' + arch + ".zip";
    }

}
