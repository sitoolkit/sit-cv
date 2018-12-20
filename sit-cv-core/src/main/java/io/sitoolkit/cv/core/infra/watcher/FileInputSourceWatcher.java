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

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * A monitoring class implementation for the input source of the file.
 *
 * @author yuichi.kuwahara
 */
@Slf4j
public class FileInputSourceWatcher extends InputSourceWatcher {

    private WatchService watcher;
    private final Set<String> watchingDirSet = new HashSet<>();
    private final Map<String, InputSource> watchingFileMap = new HashMap<>();
    private final Map<WatchKey, Path> pathMap = new HashMap<>();

    /**
     * Include the file for monitoring.
     *
     * @param inputSource
     *            input source
     */
    @Override
    public void watchInputSource(String inputSource) {
        Path path = Paths.get(inputSource).normalize().toAbsolutePath();

        if (!path.toFile().exists()) {
            log.warn("File not found {}", inputSource);
            return;
        }

        log.info("Start to watch {}", path);

        if (path.toFile().isFile()) {
            watchFile(path);
        } else {
            try {
                Files.walk(path).forEach(childPath -> {
                    if (childPath.toFile().isFile()) {
                        watchFile(childPath);
                    } else {
                        watchDir(childPath);
                    }
                });
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

    }

    private void watchFile(Path file) {
        String fileStr = file.toString();
        if (watchingFileMap.containsKey(fileStr)) {
            return;
        }
        log.debug("Start to watch {}", file);
        watchingFileMap.put(fileStr, new InputSource(fileStr, file.toFile().lastModified()));

        try {
            if (watcher == null) {
                watcher = FileSystems.getDefault().newWatchService();
            }
            Path parentDir = toParentDir(file);
            WatchKey watchKey = parentDir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
            pathMap.put(watchKey, parentDir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void watchDir(Path dir) {

        String dirStr = dir.toString();
        if (watchingDirSet.contains(dirStr)) {
            return;
        }
        log.debug("Start to watch {}", dir);
        watchingDirSet.add(dir.toString());

        try {
            if (watcher == null) {
                watcher = FileSystems.getDefault().newWatchService();
            }
            WatchKey watchKey = dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
            pathMap.put(watchKey, dir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Path toParentDir(Path path) {
        if (path.toFile().isFile()) {
            return path.getParent();
        } else {
            return path;
        }
    }

    @Override
    public Set<String> watching() {
        WatchKey watchKey;
        try {
            watchKey = watcher.take();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } catch (ClosedWatchServiceException e) {
            if (isContinue()) {
                throw new IllegalStateException(e);
            } else {
                return Collections.emptySet();
            }
        }

        Set<String> inputSources = new HashSet<>();
        for (WatchEvent<?> event : watchKey.pollEvents()) {
            Path dir = pathMap.get(watchKey);
            File changedFile = dir.resolve((Path) event.context()).toFile();

            InputSource inputSource = watchingFileMap.get(changedFile.getAbsolutePath());
            if (inputSource == null) {
                inputSource = new InputSource(changedFile.getAbsolutePath(), 0);
                watchingFileMap.put(changedFile.getAbsolutePath(), inputSource);
            }
            if (inputSource.lastModified != changedFile.lastModified()) {
                inputSources.add(inputSource.name);
                inputSource.lastModified = changedFile.lastModified();
            }
        }
        watchKey.reset();

        return inputSources;
    }

    @Override
    protected void end(InputSourceEventListener cg) {
        try {
            watcher.close();
        } catch (IOException e) {
            log.warn("Exception when watcher close", e);
        }

        cg.onChange(watchingFileMap.values().stream().map(InputSource::getName)
                .collect(Collectors.toSet()));
    }

    @Data
    class InputSource {
        String name;
        long lastModified;

        InputSource(String name, long lastModified) {
            this.name = name;
            this.lastModified = lastModified;
        }
    }
}
