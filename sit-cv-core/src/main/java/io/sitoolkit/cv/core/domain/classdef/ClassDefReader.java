package io.sitoolkit.cv.core.domain.classdef;

import java.nio.file.Path;
import java.util.Optional;

public interface ClassDefReader {

    void init(Path projectDir, Path srcDir);

    void init(ClassDefRepositoryParam param);

    void rebuild();

    void readDir(Path srcDir);

    Optional<ClassDef> readJava(Path javaFile);
}
