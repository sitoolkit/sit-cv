package io.sitoolkit.cv.app.pres.designdoc;

import java.util.LinkedHashMap;
import java.util.Map;

import io.sitoolkit.cv.core.domain.classdef.javadoc.CvJavadoc;
import lombok.Data;

@Data
public class DetailResponse {

    private Map<String, String> diagrams = new LinkedHashMap<>();
    private Map<String, String> comments = new LinkedHashMap<>();
    private Map<String, CvJavadoc> javadocs = new LinkedHashMap<>();

}
