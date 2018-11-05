package io.sitoolkit.cv.core.domain.uml;

import java.util.Map;
import java.util.Set;

import io.sitoolkit.cv.core.domain.classdef.javadoc.CvJavadoc;

public interface DiagramModel {
    public String getId();
    public Set<String> getAllTags();
    public Map<String, String> getAllComments();
    public Map<String, CvJavadoc> getAllJavadocs();
    public Set<String> getAllSourceIds();
}
