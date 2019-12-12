package io.sitoolkit.cv.core.infra.config;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class EnclosureFilterCondition {

  private FilterPattern startPattern;
  private FilterPattern endPattern;
  private FilterPattern ignorePattern;
  private boolean sqlStartsWithStartLine;
  private String startStr;

  @JsonCreator
  public EnclosureFilterCondition(
      @JsonProperty("start") String start,
      @JsonProperty("end") String end,
      @JsonProperty("ignore") String ignore,
      @JsonProperty("sqlStartsWithStartLine") boolean sqlStartsWithStartLine) {
    this.startPattern = new FilterPattern(start, false);
    this.endPattern = new FilterPattern(end, false);
    this.ignorePattern = new FilterPattern(ignore, false);
    this.sqlStartsWithStartLine = sqlStartsWithStartLine;
    this.startStr = start;
  }

  public boolean matchStart(String value) {
    return startPattern.match(value);
  }

  public boolean matchEnd(String value) {
    return endPattern.match(value);
  }

  public boolean matchIgnore(String value) {
    return ignorePattern.match(value);
  }

  public String substringAfterStart(String line) {
    return StringUtils.substringAfter(line, RegExUtils.removeAll(startStr, "\\.\\*"));
  }
}
