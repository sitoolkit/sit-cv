package io.sitoolkit.cv.core.domain.classdef.javaparser.preprocess;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import io.sitoolkit.cv.core.domain.project.Project;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PreProcessingProject {

    final Project original;
    final PreProcessor processor;

    public void execPreProcess() {
        original.getSrcDirs().stream().forEach(srcDir -> {
            processor.execute(srcDir, processor.getTargetSrcPath(srcDir));
        });
    }

    public Set<Path> getParseTargetSrcDirs() {
        return original.getSrcDirs().stream()
                .map(processor::getTargetSrcPath)
                .collect(Collectors.toSet());
    }

    public Path getParseTargetSrc(Path inputFile) {
        return processor.getTargetSrcPath(inputFile);
    }

    public Set<Path> getSrcDirs() {
        return original.getSrcDirs();
    }

    public Set<Path> getClasspaths() {
        return original.getClasspaths();
    }

}
