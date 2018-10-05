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
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
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
 * ファイルの入力ソースに対する監視クラス実装です。
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
     * ファイルを監視対象に含めます。
     *
     * @param inputSource
     *            監視対象ファイル
     */
    @Override
    public void watchInputSource(String inputSource) {
        File file = new File(inputSource);
        if (!file.exists()) {
            log.warn("File not found {}", file.getAbsolutePath());
            return;
        }
        if (watchingFileMap.containsKey(file.getAbsolutePath())) {
            return;
        }
        log.info("Start to watch {}", file.getAbsolutePath());
        watchingFileMap.put(file.getAbsolutePath(),
                new InputSource(inputSource, file.lastModified()));

        File dir = file.isFile() ? file.getParentFile() : file;

        if (file.isFile()) {
            if (watchingDirSet.contains(dir.getAbsolutePath())) {
                return;
            }
            log.info("Start to watch {}", dir.getAbsolutePath());
            watchingDirSet.add(dir.getAbsolutePath());
        }

        Path dirPath = dir.toPath();

        try {
            if (watcher == null) {
                // TODO ファイル監視方式の統一
                watcher = FileSystems.getDefault().newWatchService();
            }
            WatchKey watchKey = dirPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            pathMap.put(watchKey, dirPath);
        } catch (IOException e) {
            throw new IllegalStateException(e);
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
    protected void end(ContinuousGeneratable cg) {
        try {
            watcher.close();
        } catch (IOException e) {
            log.warn("Exception when watcher close", e);
        }

        cg.regenerate(watchingFileMap.values().stream().map(InputSource::getName)
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
