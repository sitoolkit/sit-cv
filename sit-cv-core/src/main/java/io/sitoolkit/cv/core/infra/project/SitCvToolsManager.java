package io.sitoolkit.cv.core.infra.project;

import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.infra.util.PackageUtils;
import io.sitoolkit.cv.core.infra.util.SitResourceUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SitCvToolsManager {

  private static final String ARTIFACT_ID = "sit-cv-tools";

  public static Path install(Path workDir, String javaVersion) {
    String jarName = resolveJarName(javaVersion);
    Path jarPath = workDir.resolve(jarName);

    log.info("Installing {}", ARTIFACT_ID);
    SitResourceUtils.res2file(SitCvToolsManager.class, "/lib/" + jarName, jarPath);

    return jarPath;
  }

  static String resolveJarName(String javaVersion) {
    return String.format(
        "%s-%s-%s.jar",
        resolveArtifactId(javaVersion), PackageUtils.getVersion(), "jar-with-dependencies");
  }

  static String resolveArtifactId(String javaVersion) {
    if (StringUtils.startsWith(javaVersion, "1.8")) {
      return ARTIFACT_ID + "-1_8";
    }
    return ARTIFACT_ID;
  }
}
