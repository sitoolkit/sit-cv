package io.sitoolkit.cv.core.domain.project;

import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

public interface ProjectReader {

    Optional<Project> read(Path projectDir);

    boolean generateSqlLog(URL configUrl, Project project);
}
