package io.sitoolkit.cv.core.infra.config;

import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterCondition {

  private String name;
  private String annotation;
  private boolean withDetail = true;

  // TODO use FilterPattern class
  @Setter(AccessLevel.NONE)
  @Getter(AccessLevel.NONE)
  private Pattern namePattern;

  @Setter(AccessLevel.NONE)
  @Getter(AccessLevel.NONE)
  private Pattern annotationPattern;

  public boolean matchName(String name) {
    if (StringUtils.isEmpty(this.name)) {
      return true;
    }

    if (namePattern == null) {
      namePattern = Pattern.compile(this.name);
    }

    return namePattern.matcher(name).matches();
  }

  public boolean matchAnnotation(String annotation) {
    if (StringUtils.isEmpty(this.annotation)) {
      return true;
    }

    if (annotationPattern == null) {
      annotationPattern = Pattern.compile(this.annotation);
    }

    return annotationPattern.matcher(annotation).matches();
  }
}
