package io.sitoolkit.cv.core.domain.report.designdoc;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.sitoolkit.cv.core.domain.classdef.ApiDocDef;
import lombok.Data;

@Data
public class DesignDocReportDetailDef {

    private Map<String, String> diagrams = new LinkedHashMap<>();
    private Map<String, ApiDocDef> apiDocs = new HashMap<>();

}
