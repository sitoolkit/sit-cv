package io.sitoolkit.cv.core.domain.uml;

import io.sitoolkit.cv.core.domain.classdef.ApiDocDef;
import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.classdef.RelationDef;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
    return classes.stream().map(ClassDef::getSourceId).distinct().collect(Collectors.toSet());
  }

  @Override
  public Set<String> getAllSourceIds() {
    return classes.stream().map(ClassDef::getSourceId).collect(Collectors.toSet());
  }

  @Override
  public Map<String, ApiDocDef> getAllApiDocs() {
    return new HashMap<String, ApiDocDef>();
  }
}
