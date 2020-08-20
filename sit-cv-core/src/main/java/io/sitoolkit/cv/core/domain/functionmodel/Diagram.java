package io.sitoolkit.cv.core.domain.functionmodel;

import io.sitoolkit.cv.core.domain.classdef.ApiDocDef;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Data;

@Data
public class Diagram {
  private String id;
  private byte[] data;
  private DiagramType type;
  private Set<String> tags = new HashSet<>();
  /** key:methodSignature */
  private Map<String, ApiDocDef> apiDocs = new HashMap<>();
}
