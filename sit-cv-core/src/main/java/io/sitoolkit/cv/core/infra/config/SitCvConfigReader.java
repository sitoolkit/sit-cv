package io.sitoolkit.cv.core.infra.config;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitResourceUtils;
import io.sitoolkit.cv.core.infra.watcher.FileWatcher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SitCvConfigReader {

  private static final String CONFIG_FILE_NAME = "sit-cv-config.json";
  private static SitCvConfig config;
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
    URL url = getConfigURL(baseDir);
    log.info("Read config:{}", url.toString());

    SitCvConfig config = JsonUtils.url2obj(url, SitCvConfig.class);
    config.setSourceUrl(url);
    return config;
  }

  private URL getConfigURL(Path baseDir) {
    Path configFilePath = baseDir.resolve(CONFIG_FILE_NAME);

    if (!configFilePath.toFile().exists()) {
      return SitResourceUtils.getResourceUrl(SitCvConfig.class, CONFIG_FILE_NAME);
    }

    try {
      return configFilePath.toAbsolutePath().normalize().toUri().toURL();
    } catch (MalformedURLException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void startWatch() {
    Path configFilePath = baseDir.resolve(CONFIG_FILE_NAME);

    if (!configFilePath.toFile().exists()) {
      return;
    }

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
  }

  public synchronized void addChangeListener(Consumer<SitCvConfig> listener) {
    configListeners.add(listener);
  }

  private synchronized void reload() {
    JsonUtils.url2obj(config.getSourceUrl(), config);
  }
}
