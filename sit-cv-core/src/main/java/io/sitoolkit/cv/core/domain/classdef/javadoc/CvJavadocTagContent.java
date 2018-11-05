package io.sitoolkit.cv.core.domain.classdef.javadoc;

import com.github.javaparser.javadoc.JavadocBlockTag;

public interface CvJavadocTagContent {
    public <T extends CvJavadocTagContent> T parse(JavadocBlockTag blockTag);
}
