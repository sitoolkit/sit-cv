package io.sitoolkit.cv.core.domain.uml;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.classdef.RelationDef;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClassDiagram implements DiagramModel {
    private Set<ClassDef> classes;
    private Set<RelationDef> relations;
    private String id;

    @Override
    public Set<String> getAllTags() {
        return classes.stream()
                .map(ClassDef::getSourceId)
                .distinct()
                .collect(Collectors.toSet());
    }

    @Override
    public Map<String, String> getAllComments() {
        return new HashMap<String, String>();
    }

    @Override
    public Set<String> getAllSourceIds() {
        return classes.stream().map(ClassDef::getSourceId).collect(Collectors.toSet());
    }

}
