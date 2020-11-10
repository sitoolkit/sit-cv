package io.sitoolkit.cv.core.app.functionmodel;

import java.util.stream.Stream;
import lombok.Data;

@Data
public class AnalysisResult {

  private Stream<String> effectedSourceIds;

  private boolean entryPointModified;
}
