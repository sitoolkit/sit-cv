package io.sitoolkit.cv.core.infra.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitResourceUtils;
import lombok.Data;

@Data
public class SitCvConfig {

    private static final String CONFIG_FILE_NAME = "sit-cv-config.json";
    private String jarList = "jar-list.txt";
    private String javaFilePattern = ".*\\.(java|class)$";
    private FilterConditionGroup entryPointFilter;
    private FilterConditionGroup sequenceDiagramFilter;

    public static SitCvConfig load(Path baseDir) {
        Path jsonFile = baseDir.resolve(CONFIG_FILE_NAME);

        try {
            String json = jsonFile.toFile().exists() ? new String(Files.readAllBytes(jsonFile))
                    : SitResourceUtils.res2str(SitCvConfig.class, CONFIG_FILE_NAME);
            return JsonUtils.str2obj(json, SitCvConfig.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
