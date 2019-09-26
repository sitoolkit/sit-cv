package io.sitoolkit.cv.core.infra.config;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitResourceUtils;
import io.sitoolkit.cv.core.infra.watcher.FileWatcher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SitCvConfigReader {

  private static final String CONFIG_FILE_NAME = "sit-cv-config.json";
  private static SitCvConfig config;
  private static SitCvConfig defaultConfig;
  private volatile Path baseDir;
  private List<Consumer<SitCvConfig>> configListeners = new ArrayList<>();
  private FileWatcher watcher = new FileWatcher();

  public SitCvConfig read(Path dir, boolean watch) {
    if (config == null) {
      config = readConfig(dir);
      baseDir = dir;
      if (watch) {
        startWatch();
      }
    } else {
      log.info("SitCvConfig is already loaded: {}", baseDir);
    }
    return config;
  }

  private SitCvConfig readConfig(Path baseDir) {
    Optional<Path> configFilePath = findConfigPath(baseDir);
    if (configFilePath.isPresent()) {
      SitCvConfig projConfig = readConfig(getConfigURL(configFilePath.get()));
      projConfig.setSourcePath(configFilePath.get());
      return SitCvConfig.merge(readDefaultConfig(), projConfig);
    } else {
      return readDefaultConfig();
    }
  }

  private SitCvConfig readDefaultConfig() {
    if (defaultConfig == null) {
      URL url = getDefaultConfigURL();
      defaultConfig = readConfig(url);
    }
    return defaultConfig;
  }

  private SitCvConfig readConfig(URL url) {
    log.info("Read config:{}", url.toString());

    SitCvConfig config = JsonUtils.url2obj(url, SitCvConfig.class);
    return config;
  }

  private URL getDefaultConfigURL() {
    return SitResourceUtils.getResourceUrl(SitCvConfig.class, CONFIG_FILE_NAME);
  }

  private URL getConfigURL(Path configFilePath) {
     try {
      return configFilePath.toAbsolutePath().normalize().toUri().toURL();
    } catch (MalformedURLException e) {
      throw new UncheckedIOException(e);
    }
  }
  
  private void startWatch() {
    findConfigPath(baseDir).ifPresent(configFilePath -> {
          
        watcher.add(configFilePath);
        watcher.addListener(modifiedFiles -> {
          List<Consumer<SitCvConfig>> listeners;
          reload();
          synchronized (this) {
            listeners = this.configListeners;
          }
          log.debug("config listeners: {}", listeners.toString());
          listeners.forEach(listener -> listener.accept(config));
        });
        watcher.start();
    });
  }

  public synchronized void addChangeListener(Consumer<SitCvConfig> listener) {
    configListeners.add(listener);
  }

  private synchronized void reload() {
    config.updateBy(readConfig(baseDir));
  }
  
  private Optional<Path> findConfigPath(Path baseDir) {
      Path path = baseDir.resolve(CONFIG_FILE_NAME);
      if (path.toFile().exists()) {
          return Optional.of(path);
      } else {
          return Optional.empty();
      }
  }

}
