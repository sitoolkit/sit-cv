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
import io.sitoolkit.util.buidtoolhelper.maven.MavenProject;
import io.sitoolkit.util.buidtoolhelper.process.StdoutListener;
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
        if (isMavenProject(projectDir)) {
            log.info("project: {} is a maven project - finding depending jars... ", projectDir);
            DependencyListener listener = new DependencyListener();
            MavenProject.load(projectDir)
                    .mvnw("dependency:build-classpath")
                    .stdout(listener)
                    .execute();

            Collection<Path> gotPaths = listener.listenedPath;
            log.info("jarPaths got from maven dependency - Paths: {}", gotPaths);
            return gotPaths;

        } else {
            log.info("project: {} is not a maven project", projectDir);
            return Collections.emptySet();
        }
    }

    boolean isMavenProject(Path projectDir) {
        return Files.exists(projectDir.resolve("pom.xml"));
    }

}

@Slf4j
class DependencyListener implements StdoutListener {

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
