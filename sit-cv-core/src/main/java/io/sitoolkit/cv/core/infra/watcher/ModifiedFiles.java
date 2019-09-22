package io.sitoolkit.cv.core.infra.watcher;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ModifiedFiles {

  private Set<Path> files = new HashSet<>();
  private long lastModified;

  synchronized void ddd(Path file) {
    if (!file.toFile().isFile()) {
      return;
    }
    lastModified = file.toFile().lastModified();
    Path absPath = file.normalize().toAbsolutePath();
    log.debug("Add :{}", absPath);
    files.add(absPath);
  }

  synchronized Optional<Set<Path>> getChangedFilesWithinLast(long mills) {
    if (files.isEmpty()) {
      return Optional.empty();
    }

    if (System.currentTimeMillis() < lastModified + mills) {
      return Optional.empty();
    }

    Set<Path> result = new HashSet<>(files);
    files.clear();

    return Optional.of(result);
  }

}
