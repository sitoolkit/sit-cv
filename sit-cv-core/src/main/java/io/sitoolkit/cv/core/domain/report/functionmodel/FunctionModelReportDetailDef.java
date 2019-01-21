package io.sitoolkit.cv.core.domain.report.functionmodel;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.sitoolkit.cv.core.domain.classdef.ApiDocDef;
import lombok.Data;

@Data
public class FunctionModelReportDetailDef {

    /**
     * key:diagram.id, value:diagram.data
     */
    private Map<String, String> diagrams = new LinkedHashMap<>();
    /**
     * key:methodSignature
     */
    private Map<String, ApiDocDef> apiDocs = new HashMap<>();

}
