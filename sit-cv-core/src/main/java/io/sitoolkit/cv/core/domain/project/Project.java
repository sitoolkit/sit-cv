package io.sitoolkit.cv.core.domain.project;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class Project {

  private static final String WORK_DIR = "sit-cv";
  private static final String SQL_LOG_FILE = "sit-cv-repository-vs-sql.json";
  private static final String CRUD_FILE = "crud.json";

  @Setter(AccessLevel.NONE)
  private Path dir;

  private Path buildDir;

  private String javaVersion;

  private Charset sourceEncoding = Charset.defaultCharset();

  private Set<Path> srcDirs = new HashSet<>();

  private Set<Path> classpaths = new HashSet<>();

  private Set<Project> subProjects = new HashSet<>();

  private PreProcessor preProcessor = PreProcessor.DO_NOTHING;

  public Project(Path dir) {
    super();
    this.dir = dir.toAbsolutePath().normalize();
  }

  public void executeAllPreProcess() {
    preProcessor.execute();
    subProjects.forEach(Project::executeAllPreProcess);
  }

  public Set<Project> getAllProjects() {
    return getAllProjectsStream().collect(Collectors.toSet());
  }

  public Set<Path> getAllClasspaths() {
    return getAllProjectsStream()
        .flatMap(proj -> proj.classpaths.stream())
        .collect(Collectors.toSet());
  }

  public Set<Path> getAllSrcDirs() {
    return getAllProjectsStream()
        .flatMap(proj -> proj.srcDirs.stream())
        .collect(Collectors.toSet());
  }

  public Set<Path> getAllPreProcessedDirs() {
    return getAllProjectsStream()
        .flatMap(proj -> proj.getPreProcessedDirs().stream())
        .collect(Collectors.toSet());
  }

  public Optional<Path> findParseTarget(Path inputFile) {
    return findProjectFromSrc(inputFile)
        .map(proj -> proj.getPreProcessor().getPreProcessedPath(inputFile));
  }

  public Path getSqlLogPath() {
    return getWorkDir().resolve(SQL_LOG_FILE);
  }

  public Path getCrudPath() {
    return getWorkDir().resolve(CRUD_FILE);
  }

  public Path getWorkDir() {
    return dir.resolve(buildDir).resolve(WORK_DIR);
  }

  public boolean existsWorkDir() {
    return getBuildDir() != null
        && getBuildDir().toFile().exists()
        && getWorkDir().toFile().exists();
  }

  Set<Path> getPreProcessedDirs() {
    return getSrcDirs().stream()
        .map(srcDir -> preProcessor.getPreProcessedPath(srcDir))
        .filter(srcDir -> srcDir.toFile().exists())
        .collect(Collectors.toSet());
  }

  Stream<Project> getAllProjectsStream() {
    return Stream.concat(
        Stream.of(this), subProjects.stream().flatMap(Project::getAllProjectsStream));
  }

  Optional<Project> findProjectFromSrc(Path inputFile) {
    if (srcDirs.stream().anyMatch(dir -> inputFile.startsWith(dir))) {
      return Optional.of(this);

    } else {
      return subProjects.stream()
          .map(subProject -> subProject.findProjectFromSrc(inputFile))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .findFirst();
    }
  }

  @Override
  public String toString() {
    return "dir: "
        + dir
        + ", javaVersion:"
        + javaVersion
        + ", sourceEncoding: "
        + sourceEncoding
        + ", subProjects: "
        + subProjects.stream().map(Project::toString).collect(Collectors.joining(",", "[", "]"));
  }
}
