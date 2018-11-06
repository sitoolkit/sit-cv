package io.sitoolkit.cv.core.domain.uml;

import java.util.Map;
import java.util.Set;

import io.sitoolkit.cv.core.domain.classdef.JavadocDef;

public interface DiagramModel {
    public String getId();
    public Set<String> getAllTags();
    public Map<String, JavadocDef> getAllJavadocs();
    public Set<String> getAllSourceIds();
}
