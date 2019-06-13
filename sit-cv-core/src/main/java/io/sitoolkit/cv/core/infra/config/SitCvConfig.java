package io.sitoolkit.cv.core.infra.config;

import java.net.URL;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class SitCvConfig {

    @JsonIgnore
    private URL sourceUrl;
    
    private String jarList = "jar-list.txt";
    private String javaFilePattern = ".*\\.(java|class)$";
    private FilterConditionGroup entryPointFilter;
    private FilterConditionGroup sequenceDiagramFilter;
    private FilterConditionGroup repositoryFilter;
    private EnclosureFilterCondition sqlEnclosureFilter;

}
