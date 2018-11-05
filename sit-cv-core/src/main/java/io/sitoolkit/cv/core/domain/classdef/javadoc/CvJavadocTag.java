package io.sitoolkit.cv.core.domain.classdef.javadoc;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public abstract class CvJavadocTag {
    private String name;
    private String label;
    private List<CvJavadocTagContent> contents = new ArrayList<>();
}
