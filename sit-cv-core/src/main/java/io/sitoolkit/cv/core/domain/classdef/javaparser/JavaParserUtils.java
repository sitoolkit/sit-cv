package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.List;

import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;

public class JavaParserUtils {

  public static boolean hasAnyAnnotation(
      NodeWithAnnotations<?> nodeWithAnnotations, List<String> annotaionNames) {
    return annotaionNames
        .stream()
        .anyMatch(
            annotationName -> nodeWithAnnotations.getAnnotationByName(annotationName).isPresent());
  }
}
