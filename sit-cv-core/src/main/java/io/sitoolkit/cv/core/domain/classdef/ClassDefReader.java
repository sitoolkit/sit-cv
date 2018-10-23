package io.sitoolkit.cv.core.domain.classdef;

import java.nio.file.Path;
import java.util.Optional;

public interface ClassDefReader {

    ClassDefReader init();

    ClassDefReader readDir();

    Optional<ClassDef> readJava(Path javaFile);
}
