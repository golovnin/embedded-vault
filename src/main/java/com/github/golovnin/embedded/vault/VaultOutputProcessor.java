/*
 * Copyright (c) 2018, Andrej Golovnin
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

import java.util.function.Consumer;

import de.flapdoodle.embed.process.io.IStreamProcessor;

import static java.util.Objects.requireNonNull;

/**
 * @author <a href="mailto:andrej.golovnin@gmail.com">Andrej Golovnin</a>
 */
final class VaultOutputProcessor implements IStreamProcessor {

    private final IStreamProcessor delegate;
    private final Consumer<String> outputProcessor;

    VaultOutputProcessor(IStreamProcessor delegate, Consumer<String> outputProcessor) {
        this.delegate = requireNonNull(delegate, "delegate may not be null");
        this.outputProcessor = requireNonNull(outputProcessor, "outputProcessor may not be null");
    }

    @Override
    public void process(String block) {
        outputProcessor.accept(block);
        delegate.process(block);
    }

    @Override
    public void onProcessed() {
        delegate.onProcessed();
    }

}
