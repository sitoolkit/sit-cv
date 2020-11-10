package io.sitoolkit.cv.core.domain.project.maven;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.domain.project.ProjectReader;
import io.sitoolkit.cv.core.domain.project.analyze.SqlLogProcessor;
import io.sitoolkit.cv.core.infra.config.CvConfig;
import io.sitoolkit.cv.core.infra.project.SitCvToolsManager;
import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;
import java.nio.file.Path;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MavenProjectReader implements ProjectReader {

  @NonNull private SqlLogProcessor sqlLogProcessor;

  @Override
  public Optional<Project> read(Path projectDir) {

    MavenProject mvnPrj = MavenProject.load(projectDir);

    if (!mvnPrj.available()) {
      return Optional.empty();
    }

    MavenProjectInfoListener listener = new MavenProjectInfoListener(projectDir);

    mvnPrj.mvnw("compile", "-X").stdout(listener).execute();

    return Optional.of(listener.getProject());
  }

  @Override
  public boolean generateSqlLog(Project project, CvConfig sitCvConfig) {
    MavenProject mvnPrj = MavenProject.load(project.getDir());

    if (!mvnPrj.available()) {
      return false;
    }

    Path agentJar = SitCvToolsManager.install(project.getWorkDir(), project.getJavaVersion());

    sqlLogProcessor.process(
        "maven",
        sitCvConfig,
        agentJar,
        project,
        (String agentParam) -> {
          return mvnPrj.mvnw("test", "-DargLine=" + agentParam);
        });
    return true;
  }
}
