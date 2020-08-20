package io.sitoolkit.cv.core.infra.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

@Value
public class FilterPattern {

  private boolean empty;
  private Pattern pattern;
  private boolean resultWhenPatternEmpty;

  private static final String EMPTY_STR = "";

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

  public String matchString(String value) {
    Matcher matcher = pattern.matcher(value);
    if (!matcher.matches() || matcher.groupCount() < 1) {
      return EMPTY_STR;
    }

    return matcher.group(1);
  }
}
