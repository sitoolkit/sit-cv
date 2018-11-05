package io.sitoolkit.cv.core.domain.designdoc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.sitoolkit.cv.core.domain.classdef.javadoc.CvJavadoc;
import lombok.Data;

@Data
public class Diagram {
    private String id;
    private byte[] data;
    private DiagramType type;
    private Set<String> tags = new HashSet<>();
    private Map<String, String> comments = new HashMap<>();
    private Map<String, CvJavadoc> javadocs = new HashMap<>();
}
