package io.sitoolkit.cv.app.infra.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Data;

@Data
public class ApplicationConfig {

  private String project;
  private String allowedOrigins;

  public Path getProjectDir() {
    return Paths.get(project);
  }
}
