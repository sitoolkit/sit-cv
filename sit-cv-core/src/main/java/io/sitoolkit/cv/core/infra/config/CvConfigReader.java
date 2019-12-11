package io.sitoolkit.cv.core.infra.config;

import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitResourceUtils;

public class CvConfigReader {

  static final String CONFIG_FILE_NAME = "sit-cv-config.json";

  public CvConfig read(Path configFilePath) {
    CvConfig newConfig = JsonUtils.file2obj(configFilePath, CvConfig.class).orElseThrow();

    if (!newConfig.isOverride()) {
      newConfig = readDefaultConfig();
      JsonUtils.merge(newConfig, configFilePath);
    }
    return newConfig;
  }

  public Optional<Path> findConfigPath(Path baseDir) {
    Path path = baseDir.resolve(CONFIG_FILE_NAME);
    if (path.toFile().exists()) {
      return Optional.of(path);
    } else {
      return Optional.empty();
    }
  }

  public CvConfig readDefaultConfig() {
    return JsonUtils.url2obj(getDefaultConfigURL(), CvConfig.class);
  }

  private URL getDefaultConfigURL() {
    return SitResourceUtils.getResourceUrl(CvConfig.class, CONFIG_FILE_NAME);
  }
}
