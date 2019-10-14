package io.sitoolkit.cv.core.domain.project;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;

import io.sitoolkit.cv.core.domain.crud.SqlPerMethod;
import io.sitoolkit.cv.core.domain.project.lombok.DelombokProcessor;
import io.sitoolkit.cv.core.infra.config.CvConfig;
import io.sitoolkit.cv.core.infra.util.JsonUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProjectManager {

  @NonNull
  private List<ProjectReader> readers;

  @NonNull
  @Getter
  private CvConfig cvConfig;

  @Getter
  private Project currentProject;

  public void load(Path projectDir) {

    Optional<Project> project = readers.stream().map(reader -> reader.read(projectDir))
        .filter(Optional::isPresent).map(Optional::get).findFirst();

    if (project.isPresent()) {
      currentProject = project.get();
      currentProject.getAllProjects().forEach(proj -> {
        DelombokProcessor.of(proj).ifPresent(proj::setPreProcessor);
      });

    } else {
      throw new IllegalArgumentException("Project is not supported " + projectDir);
    }

  }

  public Optional<List<SqlPerMethod>> getSqlLog() {
    return JsonUtils.file2obj(currentProject.getSqlLogPath(),
        new TypeReference<List<SqlPerMethod>>() {
        });
  }

  public void generateSqlLog() {
    readers.stream().filter(reader -> reader.generateSqlLog(currentProject, cvConfig)).findFirst();
  }

}
