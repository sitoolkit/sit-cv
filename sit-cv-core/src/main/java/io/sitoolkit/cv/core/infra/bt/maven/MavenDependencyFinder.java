package io.sitoolkit.cv.core.infra.bt.maven;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Stream;

import io.sitoolkit.cv.core.domain.classdef.javaparser.DependencyFinder;
import io.sitoolkit.util.buidtoolhelper.maven.MavenProject;
import io.sitoolkit.util.buidtoolhelper.process.StdoutListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MavenDependencyFinder implements DependencyFinder {

    @Override
    public Collection<Path> findJarPaths(Path projectDir) {
        if (!isMavenProject(projectDir)) {
            return Collections.emptySet();
        }
        log.info("project: {} is a maven project - finding depending jars... ", projectDir);
        MavenDependencyListener listener = new MavenDependencyListener();
        MavenProject.load(projectDir)
                .mvnw("dependency:build-classpath")
                .stdout(listener)
                .execute();

        Collection<Path> gotPaths = listener.listenedPath;
        log.info("jarPaths got from maven dependency - Paths: {}", gotPaths);
        return gotPaths;
    }

    public boolean isMavenProject(Path projectDir) {
        return Files.exists(projectDir.resolve("pom.xml"));
    }

    @Override
    public boolean canFindDependencies(Path projectDir) {
        return isMavenProject(projectDir);
    }

}

@Slf4j
class MavenDependencyListener implements StdoutListener {

    Collection<Path> listenedPath = new HashSet<>();
    String previousLine = "";

    @Override
    public void nextLine(String line) {
        log.debug("listening to mvn cmd stdout: {}", line);
        if (isClassPathLine()) {
            Stream.of(line.split(File.pathSeparator)).map(Paths::get).forEach(listenedPath::add);
            log.debug("dependency listened", line);
        }
        previousLine = line;
    }

    boolean isClassPathLine() {
        return previousLine.contains("Dependencies classpath:");
    }
}
