package io.sitoolkit.cv.core.infra.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SqlEnclosureFilter {

    private FilterPattern start;
    private FilterPattern end;

    @JsonCreator
    public SqlEnclosureFilter(@JsonProperty("start") String start,
            @JsonProperty("end") String end) {
        this.start = new FilterPattern(start);
        this.end = new FilterPattern(end);
    }

}
