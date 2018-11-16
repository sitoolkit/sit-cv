package io.sitoolkit.cv.core.domain.project;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.sitoolkit.cv.core.domain.classdef.javaparser.preprocess.PreProcessor;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class Project {

    @Setter(AccessLevel.NONE)
    private Path dir;

    private Path buildDir;

    private Set<Path> srcDirs = new HashSet<>();

    private Set<Path> classpaths = new HashSet<>();

    private Set<Project> subProjects = new HashSet<>();

    private PreProcessor preProcessor = PreProcessor.DO_NOTHING;

    public Project(Path dir) {
        super();
        this.dir = dir.toAbsolutePath().normalize();
    }

    public void refresh() {
        preProcessor.execute();
        subProjects.forEach(Project::refresh);
    }

    public Set<Project> getProjectsIncludeSubs() {
        return getProjectsStreamIncludeSubs().collect(Collectors.toSet());
    }

    public Set<Path> getClasspathsIncludeSubs() {
        return getProjectsStreamIncludeSubs().flatMap(proj -> proj.classpaths.stream()).collect(Collectors.toSet());
    }

    public Set<Path> getSrcDirsIncludeSubs() {
        return getProjectsStreamIncludeSubs().flatMap(proj -> proj.srcDirs.stream()).collect(Collectors.toSet());
    }

    public Set<Path> getParseTargetDirsIncludeSubs() {
        return getProjectsStreamIncludeSubs().flatMap(proj -> proj.getParseTargetSrcDirs().stream())
                .collect(Collectors.toSet());
    }

    public Optional<Path> findParseTargetSrc(Path inputFile) {
        return findProjectFromSrc(inputFile)
                .map(proj -> proj.getPreProcessor().getTargetSrcPath(inputFile));
    }

    Set<Path> getParseTargetSrcDirs() {
        return getSrcDirs().stream()
                .map(srcDir -> preProcessor.getTargetSrcPath(srcDir))
                .collect(Collectors.toSet());
    }

    Stream<Project> getProjectsStreamIncludeSubs() {
        return Stream.concat(
                Stream.of(this),
                subProjects.stream()
                        .flatMap(Project::getProjectsStreamIncludeSubs));
    }

    Optional<Project> findProjectFromSrc(Path inputFile) {
        if (srcDirs.stream().anyMatch(dir -> inputFile.startsWith(dir))) {
            return Optional.of(this);

        } else {
            return subProjects.stream()
                    .map(subProject -> subProject.findProjectFromSrc(inputFile))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();
        }
    }
}
