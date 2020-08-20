package io.sitoolkit.cv.app.pres.functionmodel;

import io.sitoolkit.cv.core.domain.classdef.ApiDocDef;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;

@Data
public class DetailResponse {

  /** key:diagram.id, value:diagram.data */
  private Map<String, String> diagrams = new LinkedHashMap<>();
  /** key:methodSignature */
  private Map<String, ApiDocDef> apiDocs = new LinkedHashMap<>();
}
