package io.sitoolkit.cv.core.infra.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class EnclosureFilterCondition {

  private FilterPattern startPattern;
  private FilterPattern endPattern;
  private FilterPattern matchPattern;

  @JsonCreator
  public EnclosureFilterCondition(
      @JsonProperty("start") String start,
      @JsonProperty("end") String end,
      @JsonProperty("match") String match) {
    this.startPattern = new FilterPattern(start, false);
    this.endPattern = new FilterPattern(end, false);
    this.matchPattern = new FilterPattern(match, false);
  }

  public boolean matchStart(String value) {
    return startPattern.match(value);
  }

  public boolean matchEnd(String value) {
    return endPattern.match(value);
  }

  public boolean matchRegex(String value) {
    return matchPattern.match(value);
  }

  public String getMatchString(String value) {
    return matchPattern.matchString(value);
  }
}
