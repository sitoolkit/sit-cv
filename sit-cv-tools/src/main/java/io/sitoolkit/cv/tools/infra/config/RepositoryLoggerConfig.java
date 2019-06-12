package io.sitoolkit.cv.tools.infra.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepositoryLoggerConfig {

    @JsonIgnore
    private String repositoryMethodMarker;
    
    private FilterConditionGroup repositoryFilter;
    
}
