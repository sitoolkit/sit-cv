package io.sitoolkit.cv.core.domain.project.lombok;

import io.sitoolkit.cv.core.domain.project.PreProcessor;
import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.infra.exception.ProcessExecutionException;
import io.sitoolkit.util.buildtoolhelper.process.ProcessCommand;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DelombokProcessor implements PreProcessor {

  private Project project;
  private Path lombokJarPath;

  public static Optional<PreProcessor> of(Project project) {

    if (isLombokUsed(project)) {
      return Optional.of(new DelombokProcessor(project));

    } else {
      return Optional.empty();
    }
  }

  static boolean isLombokUsed(Project project) {
    Optional<Path> delombokClasspath =
        project.getClasspaths().stream()
            .filter(classPath -> classPath.getFileName().toString().startsWith("lombok-"))
            .findFirst();

    if (project.getBuildDir() == null) {
      log.debug("build directory not found in {}", project.getDir());
      return false;
    }

    if (delombokClasspath.isPresent()) {
      log.debug("Lombok dependency found in {} : {}", project.getDir(), delombokClasspath.get());
    } else {
      log.debug("Lombok dependency not found in {}", project.getDir());
    }
    return delombokClasspath.isPresent();
  }

  DelombokProcessor(Project project) {
    this.project = project;
    project.getClasspaths().stream()
        .filter(classPath -> classPath.getFileName().toString().startsWith("lombok-"))
        .findFirst()
        .ifPresent(lombokJar -> this.lombokJarPath = lombokJar);
  }

  @Override
  public Path getPreProcessedPath(Path original) {

    Optional<Path> enclosingSrcDir =
        project.getSrcDirs().stream().filter(dir -> original.startsWith(original)).findFirst();

    if (enclosingSrcDir.isPresent()) {
      Path relativized = enclosingSrcDir.get().relativize(original);
      return getDelombokTargetDir().resolve(relativized.toString()).normalize();

    } else {
      throw new IllegalArgumentException(original.toAbsolutePath() + " is not_in source directory");
    }
  }

  @Override
  public void execute() {
    project.getSrcDirs().forEach(this::executeDelombok);
  }

  void executeDelombok(Path srcDir) {
    String encoding = project.getSourceEncoding().name();
    String srcPath = srcDir.toFile().getAbsolutePath();
    String targetPath = getDelombokTargetDir().toFile().getAbsolutePath();
    String classPath =
        project.getClasspaths().stream()
            .map(Path::toAbsolutePath)
            .map(Path::toString)
            .collect(Collectors.joining(File.pathSeparator));

    int exitCode =
        new ProcessCommand()
            .command("java")
            .args(
                "-jar",
                lombokJarPath.toFile().getAbsolutePath(),
                "delombok",
                "-e",
                encoding,
                "-c",
                classPath,
                srcPath,
                "-d",
                targetPath)
            .stdout(log::info)
            .stderr(log::warn)
            .execute();

    if (exitCode != 0) {
      throw new ProcessExecutionException(exitCode);
    }
  }

  Path getDelombokTargetDir() {
    return project.getBuildDir().resolve("generated-sources/sit-cv/delombok");
  }
}
