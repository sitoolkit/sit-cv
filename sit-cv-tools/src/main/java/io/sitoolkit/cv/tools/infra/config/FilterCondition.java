package io.sitoolkit.cv.tools.infra.config;

import lombok.Value;

@Value
public class FilterCondition {

  private FilterPattern namePattern;
  private FilterPattern annotationPattern;

  public FilterCondition(String name, String annotation) {
    this.namePattern = new FilterPattern(name, true);
    this.annotationPattern = new FilterPattern(annotation, true);
  }

  public boolean matchName(String name) {
    return namePattern.match(name);
  }

  public boolean matchAnnotation(String annotation) {
    return annotationPattern.match(annotation);
  }
}
