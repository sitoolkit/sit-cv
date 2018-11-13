package io.sitoolkit.cv.core.infra.lombok;

import java.nio.file.Path;
import java.util.Set;

import lombok.Builder;

@Builder
public class DelombokParameter {
    Path src;
    Path target;
    String encoding;
    Set<Path> classpath;
    Set<Path> sourcepath;
}
