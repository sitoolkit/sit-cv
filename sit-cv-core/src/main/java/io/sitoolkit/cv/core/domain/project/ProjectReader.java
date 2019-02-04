package io.sitoolkit.cv.core.domain.project;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import io.sitoolkit.cv.core.domain.crud.SqlPerMethod;

public interface ProjectReader {

    Optional<Project> read(Path projectDir);

    List<SqlPerMethod> getSqlLog(Project project);

    boolean generateSqlLog(Project project);
}
