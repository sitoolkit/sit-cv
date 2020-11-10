package io.sitoolkit.cv.core.domain.project;

import io.sitoolkit.cv.core.infra.config.CvConfig;
import java.nio.file.Path;
import java.util.Optional;

public interface ProjectReader {

  Optional<Project> read(Path projectDir);

  boolean generateSqlLog(Project project, CvConfig cvConfig);
}
