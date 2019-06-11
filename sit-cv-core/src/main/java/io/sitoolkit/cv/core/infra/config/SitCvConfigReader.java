package io.sitoolkit.cv.core.infra.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
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
        URL url = getURL(baseDir.resolve(CONFIG_FILE_NAME));
        log.info("Read config:{}", url.toString());
        String json = SitResourceUtils.res2str(url);
        SitCvConfig config = JsonUtils.str2obj(json, SitCvConfig.class);
        config.setSourceUrl(url);
        return config;
    }

    private URL getURL(Path configFilePath) {
        if (!configFilePath.toFile().exists()) {
            return SitResourceUtils.getResourceUrl(SitCvConfig.class, CONFIG_FILE_NAME);
        }

        try {
            return configFilePath.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private String readFile(Path baseDir) {
        Path configFilePath = baseDir.resolve(CONFIG_FILE_NAME);

        String json = null;
        if (configFilePath.toFile().exists()) {
            try {
                log.info("Read config:{}", configFilePath.toAbsolutePath().normalize());
                json = new String(Files.readAllBytes(configFilePath));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            json = SitResourceUtils.res2str(SitCvConfig.class, CONFIG_FILE_NAME);
        }

        return json;
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
        String json = readFile(this.baseDir);
        JsonUtils.str2obj(json, config);
    }
}
