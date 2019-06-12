package io.sitoolkit.cv.core.infra.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitResourceUtils;
import io.sitoolkit.cv.core.infra.watcher.FileInputSourceWatcher;
import io.sitoolkit.cv.core.infra.watcher.InputSourceWatcher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SitCvConfigReader {

    private static final String CONFIG_FILE_NAME = "sit-cv-config.json";
    private static SitCvConfig config;

    private volatile Path baseDir;
    private List<Consumer<SitCvConfig>> configListeners = new ArrayList<>();
    private InputSourceWatcher watcher = new FileInputSourceWatcher();

    public SitCvConfig read(Path dir) {
        if (config == null) {
            config = readConfig(dir);
            baseDir = dir;
            startWatch();
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
            throw new RuntimeException(e);
        }
    }

    private void startWatch() {
        Path configFilePath = baseDir.resolve(CONFIG_FILE_NAME);

        if (!configFilePath.toFile().exists()) {
            return;
        }

        watcher.setContinue(true);
        watcher.watch(configFilePath.toAbsolutePath().toString());
        watcher.start(inputSources -> {
            List<Consumer<SitCvConfig>> listeners;
            reload();
            synchronized (this) {
                listeners = this.configListeners;
            }
            log.debug("config listeners: {}", listeners.toString());
            listeners.forEach(listener -> listener.accept(config));
        });
    }

    public synchronized void addChangeListener(Consumer<SitCvConfig> listener) {
        configListeners.add(listener);
    }

    private synchronized void reload() {
        JsonUtils.url2obj(config.getSourceUrl(), config);
    }
}
