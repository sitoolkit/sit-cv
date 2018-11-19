package io.sitoolkit.cv.core.domain.classdef.javaparser.preprocess;

import java.nio.file.Path;

public interface PreProcessor {

    Path getPreProcessedPath(Path originalSource);

    void execute();

    static final PreProcessor DO_NOTHING = new PreProcessor() {

        @Override
        public Path getPreProcessedPath(Path srcDir) {
            return srcDir;
        }

        @Override
        public void execute() {
        }
    };

}
