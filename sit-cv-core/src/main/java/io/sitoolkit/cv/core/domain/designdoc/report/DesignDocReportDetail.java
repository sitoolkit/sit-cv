package io.sitoolkit.cv.core.domain.designdoc.report;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class DesignDocReportDetail {

    private Map<String, String> diagrams = new HashMap<>();
    private Map<String, Map<String, String>> comments = new HashMap<>();

}
