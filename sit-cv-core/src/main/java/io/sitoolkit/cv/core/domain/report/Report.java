package io.sitoolkit.cv.core.domain.report;

import java.nio.file.Path;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Report {
    private Path path;
    @Builder.Default
    private String content = "";
}
