package io.sitoolkit.cv.core.infra.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class EnclosureFilterCondition {

    private FilterPattern startPattern;
    private FilterPattern endPattern;

    @JsonCreator
    public EnclosureFilterCondition(@JsonProperty("start") String start,
            @JsonProperty("end") String end) {
        this.startPattern = new FilterPattern(start);
        this.endPattern = new FilterPattern(end);
    }

    public boolean matchStart(String value) {
        return startPattern.match(value);
    }

    public boolean matchEnd(String value) {
        return endPattern.match(value);
    }

}
