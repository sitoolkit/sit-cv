package io.sitoolkit.cv.core.infra.config;

import lombok.Data;

@Data
public class SitCvConfig {

    private String jarList = "jar-list.txt";
    private String javaFilePattern = ".*\\.(java|class)$";
    private FilterConditionGroup entryPointFilter;
    private FilterConditionGroup sequenceDiagramFilter;
    private String crudPath = "./docs/datamodel/crud/crud.json";

}
