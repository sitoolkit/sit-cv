package io.sitoolkit.cv.core.domain.project.gradle;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class GradleProjectInfoListener implements StdoutListener {

  @Getter private final Project project;
  private Project recordingProject;

  public GradleProjectInfoListener(Path projectDir) {
    this.project = new Project(projectDir);
  }

  @Override
  public void nextLine(String line) {

    log.debug(line);

    String javaBaseDirStr = StringUtils.substringAfter(line, "sitCvProjectDir:");
    if (StringUtils.isNotEmpty(javaBaseDirStr)) {
      recordBaseDirStr(javaBaseDirStr);
    }

    String javaBuildDirStr = StringUtils.substringAfter(line, "sitCvBuildDir:");
    if (StringUtils.isNotEmpty(javaBuildDirStr)) {
      recordBuildDirStr(javaBuildDirStr);
    }

    String javaSrcDirStr = StringUtils.substringAfter(line, "sitCvJavaSrcDir:");
    if (StringUtils.isNotEmpty(javaSrcDirStr)) {
      recordSrcDirStr(javaSrcDirStr);
    }

    String classpathStr = StringUtils.substringAfter(line, "sitCvClasspath:");
    if (StringUtils.isNotEmpty(classpathStr)) {
      recordClasspathStr(classpathStr);
    }

    String javaVersion = StringUtils.substringAfter(line, "javaVersion:");
    if (StringUtils.isNotEmpty(javaVersion)) {
      project.setJavaVersion(javaVersion);
    }

    String encoding = StringUtils.substringAfter(line, "javaEncoding:");
    if (StringUtils.isNotEmpty(encoding)) {
      project.setSourceEncoding(Charset.forName(encoding));
    }
  }

  void recordBaseDirStr(String javaBaseDirStr) {
    Path javaBaseDir = Paths.get(javaBaseDirStr);
    if (project.getDir().equals(javaBaseDir)) {
      recordingProject = project;

    } else {
      recordingProject = new Project(javaBaseDir);
      project.getSubProjects().add(recordingProject);
    }
  }

  void recordBuildDirStr(String javaBuildDirStr) {
    if (recordingProject != null) {
      recordingProject.setBuildDir(Paths.get(javaBuildDirStr));
    }
  }

  void recordSrcDirStr(String javaSrcDirStr) {
    if (recordingProject != null) {
      recordingProject.getSrcDirs().add(Paths.get(javaSrcDirStr));
    }
  }

  void recordClasspathStr(String classpathStr) {
    if (recordingProject != null) {
      recordingProject.getClasspaths().add(Paths.get(classpathStr));
    }
  }
}
