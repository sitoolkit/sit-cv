package io.sitoolkit.cv.core.infra.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitResourceUtils;
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

    public static SitCvConfig load(Path baseDir) {
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
}
