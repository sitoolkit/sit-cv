package io.sitoolkit.cv.core.infra.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitResourceUtils;
import io.sitoolkit.cv.core.infra.watcher.FileInputSourceWatcher;
import io.sitoolkit.cv.core.infra.watcher.InputSourceWatcher;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class SitCvConfig {

    private static final String CONFIG_FILE_NAME = "sit-cv-config.json";
    private String jarList = "jar-list.txt";
    private String javaFilePattern = ".*\\.(java|class)$";
    private FilterConditionGroup entryPointFilter;
    private FilterConditionGroup sequenceDiagramFilter;

    private volatile Path baseDir;
    private List<Consumer<SitCvConfig>> configListeners = new ArrayList<>();

    private static InputSourceWatcher watcher = new FileInputSourceWatcher();

    public static SitCvConfig load(Path baseDir) {
        SitCvConfig config = loadResource(baseDir);
        config.baseDir = baseDir;
        config.startWatch();
        return config;
    }

    private static SitCvConfig loadResource(Path baseDir) {
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
        return JsonUtils.str2obj(json, SitCvConfig.class);
    }

    private void startWatch() {
        Path configFilePath = baseDir.resolve(CONFIG_FILE_NAME);
        watcher.setContinue(true);
        watcher.watch(configFilePath.toAbsolutePath().toString());
        watcher.start(inputSources -> {
            List<Consumer<SitCvConfig>> cs;
            reload();
            synchronized(this) {
                cs = this.configListeners;
            }
            log.info(cs.toString());
            cs.forEach(listener -> listener.accept(this));
        });
    }

    public synchronized void addChangeListener(Consumer<SitCvConfig> listener) {
        configListeners.add(listener);
    }

    private synchronized void reload() {
        SitCvConfig newConfig = loadResource(this.baseDir);
        this.jarList = newConfig.jarList;
        this.javaFilePattern = newConfig.javaFilePattern;
        this.entryPointFilter = newConfig.entryPointFilter;
        this.sequenceDiagramFilter = newConfig.sequenceDiagramFilter;
    }
}
