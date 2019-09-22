package io.sitoolkit.cv.core.infra.watcher;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.nio.file.SensitivityWatchEventModifier;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileWatcher {

  private WatchingFiles watchingFiles = new WatchingFiles();
  private ModifiedFiles modifiedFiles = new ModifiedFiles();
  private final List<FileWatchEventListener> fileEventListeners = new ArrayList<>();
  private WatchService watchService;
  private ExecutorService executorService;
  private volatile boolean watching = true;

  public void add(Path path) {

    if (!path.toFile().exists()) {
      log.warn("Path not found {}", path);
      return;
    }

    log.info("Start watching {}", path);

    if (path.toFile().isFile()) {
      watchFile(path);
    } else {
      try {
        watchDir(path);

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

  public void start() {
    if (executorService == null) {
      executorService = Executors.newCachedThreadPool();
    }

    executorService.execute(() -> {
      while (watching) {
        pollWatchEvent();
      }
    });

    executorService.execute(() -> {
      while (watching) {
        handleChangeEvent();
      }
    });

  }

  public void stop() {
    if (executorService != null) {
      executorService.shutdown();
    }
  }

  public boolean isWatching(Path path) {
    return watchingFiles.contains(path);
  }

  private void watchFile(Path file) {
    if (watchingFiles.add(file)) {
      watchDir(file.getParent());
    }
  }

  private void watchDir(Path dir) {
    if (watchingFiles.add(dir)) {
      registerWatchService(dir);
    }
  }

  private void registerWatchService(Path dir) {
    try {

      if (watchService == null) {
        watchService = FileSystems.getDefault().newWatchService();
      }

      log.debug("Register WatchService to {}", dir);

      dir.register(watchService,
          new WatchEvent.Kind[] { StandardWatchEventKinds.ENTRY_CREATE,
              StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE },
          SensitivityWatchEventModifier.HIGH);

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  void pollWatchEvent() {
    WatchKey watchKey = null;
    try {
      watchKey = watchService.take();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (ClosedWatchServiceException e) {
      // NOP
    }

    if (watchKey == null) {
      return;
    }

    for (WatchEvent<?> event : watchKey.pollEvents()) {
      log.debug("Poll event:{} of wachable:{}", event.kind(), watchKey.watchable());

      Path watchingPath = (Path) watchKey.watchable();
      Path effectedPath = watchingPath.resolve((Path) event.context());

      if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
        add(effectedPath);
        modifiedFiles.ddd(effectedPath);
      } else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(event.kind())) {
        modifiedFiles.ddd(effectedPath);
      } else if (StandardWatchEventKinds.ENTRY_DELETE.equals(event.kind())) {
        watchingFiles.remove(effectedPath);
      }

    }
    watchKey.reset();

  }

  private void handleChangeEvent() {
    modifiedFiles.getChangedFilesWithinLast(300).ifPresent(files -> {

      fileEventListeners.forEach(listener -> {
        try {
          log.info("Fire modify event to {} with {}", listener.getClass(), files);
          listener.onModify(files);
        } catch (Exception e) {
          log.error("Exception in the process of file change event", e);
        }
      });
    });

  }

  public void addListener(FileWatchEventListener listener) {
    fileEventListeners.add(listener);
  }
}
