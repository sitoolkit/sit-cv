package io.sitoolkit.cv.tools.infra.config;

import lombok.Data;

@Data
public class RepositoryLoggerConfig {

  private String repositoryMethodMarker;

  private String projectType;

  private FilterConditionGroup repositoryFilter;

  private FilterConditionGroup entrypointFilter;
}
