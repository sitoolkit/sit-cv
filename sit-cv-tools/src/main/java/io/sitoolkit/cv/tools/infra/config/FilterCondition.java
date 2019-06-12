package io.sitoolkit.cv.tools.infra.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class FilterCondition {

    private FilterPattern namePattern;
    private FilterPattern annotationPattern;

    @JsonCreator
    public FilterCondition(@JsonProperty("name") String name,
            @JsonProperty("annotation") String annotation) {
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
