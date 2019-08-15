package io.sitoolkit.cv.core.domain.project.gradle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import io.sitoolkit.cv.core.app.config.ServiceFactory;
import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.domain.project.ProjectReader;
import io.sitoolkit.cv.core.domain.project.analyze.SqlLogProcessor;
import io.sitoolkit.cv.core.infra.config.SitCvConfig;
import io.sitoolkit.cv.core.infra.config.SitCvConfigReader;
import io.sitoolkit.cv.core.infra.project.SitCvToolsManager;
import io.sitoolkit.cv.core.infra.util.SitResourceUtils;
import io.sitoolkit.util.buildtoolhelper.gradle.GradleProject;
import io.sitoolkit.util.buildtoolhelper.process.ProcessCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class GradleProjectReader implements ProjectReader {

  private static final String PROJECT_INFO_SCRIPT_NAME = "project-info.gradle";

  @NonNull
  private SqlLogProcessor sqlLogProcessor;

  public static void main(String[] args) {
    Path projectDir = Paths.get(args[0]);
    SitCvConfigReader configReader = new SitCvConfigReader();
    SitCvConfig config = configReader.read(projectDir, false);
    ServiceFactory factory = ServiceFactory.create(projectDir, false);
    Project project = factory.getProjectManager().getCurrentProject();
    new GradleProjectReader(new SqlLogProcessor()).generateSqlLog(project, config);
  }

  @Override
  public Optional<Project> read(Path projectDir) {

    GradleProject gradleProject = GradleProject.load(projectDir);

    if (!gradleProject.available()) {
      return Optional.empty();
    }

    GradleProjectInfoListener listener = new GradleProjectInfoListener(projectDir);

    log.info("project: {} is a gradle project - finding depending jars... ", projectDir);

    Path initScript = projectDir.resolve(PROJECT_INFO_SCRIPT_NAME);
    SitResourceUtils.res2file(this, PROJECT_INFO_SCRIPT_NAME, initScript);

    try {

      gradleProject.gradlew("--no-daemon", "--init-script", initScript.toString(), "projectInfo")
          .stdout(listener).execute();

    } finally {
      try {
        Files.deleteIfExists(initScript);
      } catch (IOException e) {
        log.warn(e.getMessage());
      }
    }

    return Optional.of(listener.getProject());
  }

  @Override
  public boolean generateSqlLog(Project project, SitCvConfig sitCvConfig) {
    GradleProject gradleProject = GradleProject.load(project.getDir());

    if (!gradleProject.available()) {
      return false;
    }

    SitCvToolsManager.initialize(project.getWorkDir());
    Path agentJar = SitCvToolsManager.getInstance().getJarPath();

    sqlLogProcessor.process("gradle", sitCvConfig, agentJar, project, (String agentParam) -> {
      ProcessCommand command = gradleProject.gradlew("--no-daemon", "--rerun-tasks", "test");
      command.getEnv().put("JAVA_TOOL_OPTIONS", agentParam);
      return command;
    });

    return true;
  }
}
