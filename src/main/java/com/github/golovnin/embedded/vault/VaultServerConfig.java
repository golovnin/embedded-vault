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

import de.flapdoodle.embed.process.builder.AbstractBuilder;
import de.flapdoodle.embed.process.builder.TypedProperty;
import de.flapdoodle.embed.process.config.IExecutableProcessConfig;
import de.flapdoodle.embed.process.config.ISupportConfig;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.runtime.Network;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Andrej Golovnin
 */
public final class VaultServerConfig implements IExecutableProcessConfig {

    private final IVersion version;
    private final long startupTimeout;
    private final String listenerHost;
    private final int listenerPort;
    private final String rootTokenID;
    private final VaultLogLevel logLevel;
    private final String clusterName;
    private final String defaultLeaseTTL;
    private final String maxLeaseTTL;

    VaultServerConfig(IVersion version, long startupTimeout,
        String listenerHost, int listenerPort, String rootTokenID,
        VaultLogLevel logLevel, String clusterName, String defaultLeaseTTL,
        String maxLeaseTTL)
    {
        this.version = version;
        this.startupTimeout = startupTimeout;
        this.listenerHost = listenerHost;
        this.listenerPort = listenerPort;
        this.rootTokenID = rootTokenID;
        this.logLevel = logLevel;
        this.clusterName = clusterName;
        this.defaultLeaseTTL = defaultLeaseTTL;
        this.maxLeaseTTL = maxLeaseTTL;
    }

    public static final class Builder extends AbstractBuilder<VaultServerConfig> {

        private static final String DEFAULT_ADDRESS = "127.0.0.1";

        private static final TypedProperty<IVersion> VERSION =
            TypedProperty.with("version", IVersion.class);

        private static final TypedProperty<Long> STARTUP_TIMEOUT =
            TypedProperty.with("startup-timeout", Long.class);

        private static final TypedProperty<String> LISTENER_HOST =
            TypedProperty.with("listener-host", String.class);

        private static final TypedProperty<Integer> LISTENER_PORT =
            TypedProperty.with("listener-port", Integer.class);

        private static final TypedProperty<String> ROOT_TOKEN_ID =
            TypedProperty.with("root-token-id", String.class);

        private static final TypedProperty<VaultLogLevel> LOG_LEVEL =
            TypedProperty.with("log-level", VaultLogLevel.class);

        private static final TypedProperty<String> CLUSTER_NAME =
            TypedProperty.with("cluster-name", String.class);

        private static final TypedProperty<String> DEFAULT_LEASE_TTL =
            TypedProperty.with("default-lease-ttl", String.class);

        private static final TypedProperty<String> MAX_LEASE_TTL =
            TypedProperty.with("max-lease-ttl", String.class);

        public Builder() {
            property(VERSION).setDefault(VaultVersion.V0_10_1);
            property(STARTUP_TIMEOUT).setDefault(60000L);
            property(LISTENER_HOST).setDefault(DEFAULT_ADDRESS);
            property(LISTENER_PORT).setDefault(8200);
            property(ROOT_TOKEN_ID).setDefault(UUID.randomUUID().toString());
            property(LOG_LEVEL).setDefault(VaultLogLevel.INFO);
            property(CLUSTER_NAME).setDefault("dev");
            property(DEFAULT_LEASE_TTL).setDefault("768h");
            property(MAX_LEASE_TTL).setDefault("768h");
        }

        public Builder version(IVersion version) {
            property(VERSION).set(version);
            return this;
        }

        public Builder startupTimeout(long startupTimeout, TimeUnit unit) {
            property(STARTUP_TIMEOUT).set(unit.toMillis(startupTimeout));
            return this;
        }

        public Builder listenerHost(String address) {
            property(LISTENER_HOST).set(address);
            return this;
        }

        public Builder listenerPort(int port) {
            property(LISTENER_PORT).set(port);
            return this;
        }

        public Builder rootTokenID(String id) {
            property(ROOT_TOKEN_ID).set(id);
            return this;
        }

        public Builder randomPort() {
            return randomPort(DEFAULT_ADDRESS);
        }

        public Builder randomPort(String address) {
            try {
                int port = Network.getFreeServerPort(
                    InetAddress.getByName(address));
                listenerHost(address);
                listenerPort(port);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            return this;
        }

        public Builder logLevel(VaultLogLevel level) {
            property(LOG_LEVEL).set(level);
            return this;
        }

        public Builder clusterName(String name) {
            property(CLUSTER_NAME).set(name);
            return this;
        }

        public Builder defaultLeaseTTL(String ttl) {
            property(DEFAULT_LEASE_TTL).set(ttl);
            return this;
        }

        public Builder maxLeaseTTL(String ttl) {
            property(MAX_LEASE_TTL).set(ttl);
            return this;
        }

        @Override
        public VaultServerConfig build() {
            return new VaultServerConfig(
                property(VERSION).get(),
                property(STARTUP_TIMEOUT).get(),
                property(LISTENER_HOST).get(),
                property(LISTENER_PORT).get(),
                property(ROOT_TOKEN_ID).get(),
                property(LOG_LEVEL).get(),
                property(CLUSTER_NAME).get(),
                property(DEFAULT_LEASE_TTL).get(),
                property(MAX_LEASE_TTL).get());
        }

    }

    public long getStartupTimeout() {
        return startupTimeout;
    }

    public String getListenerHost() {
        return listenerHost;
    }

    public int getListenerPort() {
        return listenerPort;
    }

    public String getRootTokenID() {
        return rootTokenID;
    }

    public VaultLogLevel getLogLevel() {
        return logLevel;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getDefaultLeaseTTL() {
        return defaultLeaseTTL;
    }

    public String getMaxLeaseTTL() {
        return maxLeaseTTL;
    }

    @Override
    public IVersion version() {
        return version;
    }

    @Override
    public ISupportConfig supportConfig() {
        return VaultSupportConfig.INSTANCE;
    }

    String toJson() {
        return new StringBuilder()
            .append("{\n")
            .append("\t\"cluster_name\": \"").append(clusterName).append("\",\n")
            .append("\t\"default_lease_ttl\": \"").append(defaultLeaseTTL).append("\",\n")
            .append("\t\"max_lease_ttl\": \"").append(maxLeaseTTL).append("\"\n")
            .append("}\n")
            .toString();
    }

}
