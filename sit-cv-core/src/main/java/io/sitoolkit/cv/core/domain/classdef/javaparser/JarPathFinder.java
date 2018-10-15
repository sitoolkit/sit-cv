package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import io.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryParam;
import io.sitoolkit.cv.core.infra.bt.gradle.GradleDependencyFinder;
import io.sitoolkit.cv.core.infra.bt.maven.MavenDependencyFinder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JarPathFinder {

    public ClassDefRepositoryParam solveJarParams(ClassDefRepositoryParam param) {
        Set<Path> jarPaths = new HashSet<>();
        jarPaths.addAll(getPathsFromJarList(param.getJarList()));
        jarPaths.addAll(getPathsProjectDepending(param.getProjectDir()));
        jarPaths.addAll(param.getJarPaths());

        return ClassDefRepositoryParam.builder()
                .srcDirs(param.getSrcDirs())
                .binDirs(param.getBinDirs())
                .jarPaths(new ArrayList<>(jarPaths))
                .build();
    }

    public Collection<Path> getPathsFromJarList(Path jarListFile) {
        Collection<Path> gotPaths = new HashSet<>();
        if (Files.exists(jarListFile)) {
            try {
                String jarListStr = new String(Files.readAllBytes(jarListFile));
                Stream.of(jarListStr.split(File.pathSeparator + "|" + System.lineSeparator()))
                        .map(Paths::get).forEach(gotPaths::add);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            log.info("jarPaths got - from jarListFile: {}  gotPaths: {}", jarListFile, gotPaths);
            return gotPaths;

        } else {
            log.info("jarListFile: {} not found", jarListFile);
            return Collections.emptySet();
        }
    }

    public Collection<Path> getPathsProjectDepending(Path projectDir) {

        return Stream.of(new GradleDependencyFinder(), new MavenDependencyFinder())
                .filter(finder -> finder.canFindDependencies(projectDir))
                .findFirst()
                .map(finder -> finder.findJarPaths(projectDir))
                .orElse(Collections.emptySet());
    }

}
