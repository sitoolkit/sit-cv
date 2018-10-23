package io.sitoolkit.cv.core.domain.report.designdoc;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

@Data
public class DesignDocReportDetailDef {

    private Map<String, String> diagrams = new LinkedHashMap<>();
    private Map<String, String> comments = new HashMap<>();

}
