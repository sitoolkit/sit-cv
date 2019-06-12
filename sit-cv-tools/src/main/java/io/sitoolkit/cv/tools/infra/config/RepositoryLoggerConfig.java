package io.sitoolkit.cv.tools.infra.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepositoryLoggerConfig {
    private FilterConditionGroup repositoryFilter;
    private String repositoryMethodMarker;
}
