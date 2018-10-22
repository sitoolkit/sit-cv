package io.sitoolkit.cv.core.domain.report;

import java.nio.file.Path;

import lombok.Value;

@Value
public class Report {
    private Path path;
    private String content;
}
