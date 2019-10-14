package io.sitoolkit.cv.core.infra.config;

import java.nio.file.Path;
import java.util.Optional;

import io.sitoolkit.cv.core.infra.watcher.FileWatcher;

public class CvConfigService {

  FileWatcher watcher = new FileWatcher();

  CvConfigReader reader = new CvConfigReader();

  public CvConfig read(Path dir, boolean watch) {
    Optional<Path> configFilePathOpt = reader.findConfigPath(dir);

    if (configFilePathOpt.isEmpty()) {
      return reader.readDefaultConfig();
    }

    Path configFilePath = configFilePathOpt.orElseThrow();

    CvConfig config = reader.read(configFilePath);

    if (watch) {

      watcher.add(configFilePath);

      watcher.addListener(modifiedFiles -> {
        CvConfig modifiedConfig = reader.read(configFilePath);
        config.update(modifiedConfig);
      });

      watcher.start();
    }

    return config;
  }

}
