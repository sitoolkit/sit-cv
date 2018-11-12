package io.sitoolkit.cv.core.domain.classdef.javaparser.preprocess;

import java.nio.file.Path;

public interface PreProcessor {

    Path getTargetSrcPath(Path srcDir);

    void execute(Path srcDir, Path targetDir);

    static final PreProcessor DO_NOTHING = new PreProcessor() {

        @Override
        public Path getTargetSrcPath(Path srcDir) {
            return srcDir;
        }

        @Override
        public void execute(Path srcDir, Path targetDir) {
        }
    };

}
