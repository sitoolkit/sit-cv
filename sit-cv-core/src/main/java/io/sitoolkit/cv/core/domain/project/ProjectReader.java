package io.sitoolkit.cv.core.domain.project;

import java.nio.file.Path;
import java.util.Optional;

import io.sitoolkit.cv.core.infra.config.CvConfig;

public interface ProjectReader {

    Optional<Project> read(Path projectDir);

    boolean generateSqlLog(Project project, CvConfig sitCvConfig);
}
