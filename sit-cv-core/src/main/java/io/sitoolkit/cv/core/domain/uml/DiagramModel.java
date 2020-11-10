package io.sitoolkit.cv.core.domain.uml;

import io.sitoolkit.cv.core.domain.classdef.ApiDocDef;
import java.util.Map;
import java.util.Set;

public interface DiagramModel {
  public String getId();

  public Set<String> getAllTags();

  public Map<String, ApiDocDef> getAllApiDocs();

  public Set<String> getAllSourceIds();
}
