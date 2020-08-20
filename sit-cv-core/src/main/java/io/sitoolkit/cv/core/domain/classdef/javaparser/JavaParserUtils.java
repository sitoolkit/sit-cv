package io.sitoolkit.cv.core.domain.classdef.javaparser;

import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import java.util.List;

public class JavaParserUtils {

  public static boolean hasAnyAnnotation(
      NodeWithAnnotations<?> nodeWithAnnotations, List<String> annotaionNames) {
    return annotaionNames.stream()
        .anyMatch(
            annotationName -> nodeWithAnnotations.getAnnotationByName(annotationName).isPresent());
  }
}
