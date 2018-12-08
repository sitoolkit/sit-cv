/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sitoolkit.cv.core.infra.watcher;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is to monitor change of input source.
 *
 * @author yuichi.kuwahara
 */
@Slf4j
public abstract class InputSourceWatcher {

    private boolean isContinue = false;

    Set<String> waitingSources = new HashSet<>();
    Instant lastSourceChangedTime;

    final long RELOAD_WAIT_TIME_MILLIS = 300;

    /**
     * Add an input source to be monitored. Actual processing is delegated to
     * subclass. Also, at the first execution after the process starts, it
     * creates a file being monitored. This method does nothing if it is not in
     * continuous monitoring mode.
     *
     * @param inputSource
     *            input source
     * @see #watchInputSource(java.lang.String)
     */
    public void watch(String inputSource) {
        if (!isContinue()) {
            return;
        }

        watchInputSource(inputSource);
    }

    /**
     * Start monitoring the input source. Actual processing is delegated to
     * subclass. This method does nothing if it is not in continuous monitoring
     * mode.
     * 
     * @param inputSourceEventListener
     * @see #watching()
     */
    public void start(final InputSourceEventListener inputSourceEventListener) {
        if (!isContinue()) {
            return;
        }

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(() -> {
            while (isContinue()) {
                Set<String> inputSources = watching();
                putInputSources(inputSources);
                log.info("Detected input source change {}", inputSources);
            }
        });

        executor.execute(() -> {
            while (isContinue()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(RELOAD_WAIT_TIME_MILLIS);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
                getReadyToRegenerateSources().ifPresent(sources -> {
                    try {
                        inputSourceEventListener.onChange(sources);
                    } catch (Exception e) {
                        log.error("Exception in the process of file change event", e);
                    }
                });
            }
        });
    }

    public boolean isContinue() {
        return isContinue;
    }

    public void setContinue(boolean isContinue) {
        this.isContinue = isContinue;
    }

    private synchronized void putInputSources(Collection<String> inputSources) {
        waitingSources.addAll(inputSources);
        lastSourceChangedTime = Instant.now();
    }

    private synchronized Optional<Set<String>> getReadyToRegenerateSources() {
        if (!waitingSources.isEmpty() && Instant.now()
                .isAfter(lastSourceChangedTime.plusMillis(RELOAD_WAIT_TIME_MILLIS))) {
            Set<String> result = new HashSet<>(waitingSources);
            waitingSources.clear();
            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Implement the actual process of adding the input source to the monitoring
     * target.
     *
     * @param inputSource
     *            input source
     */
    protected abstract void watchInputSource(String inputSource);

    /**
     * Start monitoring the input source. Implementation duties of subclass are
     * as follows.
     * <ul>
     * <li>Monitor input source changes
     * <li>Execute repeat interface onChange method on input source that
     * detected change
     * </ul>
     *
     */
    protected abstract Set<String> watching();

    protected abstract void end(InputSourceEventListener cg);
}
