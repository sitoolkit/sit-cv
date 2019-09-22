package io.sitoolkit.cv.core.infra.watcher;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class WatchingFiles {

  private Set<Path> paths = new HashSet<>();

  boolean add(Path path) {
    return paths.add(abs(path));
  }

  boolean contains(Path path) {
    return paths.contains(abs(path));
  }

  Path abs(Path path) {
    return path.normalize().toAbsolutePath();
  }

  void remove(Path self) {
    paths.remove(abs(self));
  }
}
