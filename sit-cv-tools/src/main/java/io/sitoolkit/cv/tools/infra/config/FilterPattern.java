package io.sitoolkit.cv.tools.infra.config;

import java.util.regex.Pattern;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

@Value
public class FilterPattern {

  private boolean empty;
  private Pattern pattern;
  private boolean resultWhenPatternEmpty;

  public FilterPattern(String patternString, boolean resultWhenPatternEmpty) {
    empty = StringUtils.isEmpty(patternString);
    this.resultWhenPatternEmpty = resultWhenPatternEmpty;
    if (empty) {
      pattern = null;
    } else {
      pattern = Pattern.compile(patternString);
    }
  }

  public boolean match(String value) {
    if (isEmpty()) {
      return resultWhenPatternEmpty;
    }

    return pattern.matcher(value).matches();
  }
}
