package io.sitoolkit.cv.app.infra.config;

import java.nio.file.Path;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class ApplicationConfig {

  private String project;
  private String allowedOrigins;

  public Path getProjectDir() {
    return Path.of(project).toAbsolutePath().normalize();
  }

  public String[] getAllowedOriginsAsArr() {
    return StringUtils.split(allowedOrigins, ",");
  }
}
