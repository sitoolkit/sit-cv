package io.sitoolkit.cv.tools.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepositoryLoggerConfig {
    private RepositoryFilterConditionGroup repositoryFilter;
    private String repositoryMethodMarker;
}
