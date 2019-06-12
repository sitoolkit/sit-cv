package io.sitoolkit.cv.core.infra.config;

import java.net.URL;

import lombok.Data;

@Data
public class SitCvConfig {

    private URL sourceUrl;
    
    private String jarList = "jar-list.txt";
    private String javaFilePattern = ".*\\.(java|class)$";
    private FilterConditionGroup entryPointFilter;
    private FilterConditionGroup sequenceDiagramFilter;
    private FilterConditionGroup repositoryFilter;
    private EnclosureFilterCondition sqlEnclosureFilter;

}
