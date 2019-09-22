package io.sitoolkit.cv.core.infra.watcher;

import java.nio.file.Path;
import java.util.Set;

public interface FileWatchEventListener {

  void onModify(Set<Path> files);

}
