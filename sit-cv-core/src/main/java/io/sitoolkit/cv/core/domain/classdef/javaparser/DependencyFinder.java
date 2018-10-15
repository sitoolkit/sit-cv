package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.nio.file.Path;
import java.util.Collection;

public interface DependencyFinder {

    boolean canFindDependencies(Path projectDir);

    Collection<Path> findJarPaths(Path projectDir);

}
