package io.sitoolkit.cv.core.domain.project;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public void executeAllPreProcess() {
        preProcessor.execute();
        subProjects.forEach(Project::executeAllPreProcess);
    }

    public Set<Project> getAllProjects() {
        return getAllProjectsStream().collect(Collectors.toSet());
    }

    public Set<Path> getAllClasspaths() {
        return getAllProjectsStream().flatMap(proj -> proj.classpaths.stream())
                .collect(Collectors.toSet());
    }

    public Set<Path> getAllSrcDirs() {
        return getAllProjectsStream().flatMap(proj -> proj.srcDirs.stream())
                .collect(Collectors.toSet());
    }

    public Set<Path> getAllPreProcessedDirs() {
        return getAllProjectsStream().flatMap(proj -> proj.getPreProcessedDirs().stream())
                .collect(Collectors.toSet());
    }

    public Optional<Path> findParseTarget(Path inputFile) {
        return findProjectFromSrc(inputFile)
                .map(proj -> proj.getPreProcessor().getPreProcessedPath(inputFile));
    }

    Set<Path> getPreProcessedDirs() {
        return getSrcDirs().stream().map(srcDir -> preProcessor.getPreProcessedPath(srcDir))
                .filter(srcDir -> srcDir.toFile().exists()).collect(Collectors.toSet());
    }

    Stream<Project> getAllProjectsStream() {
        return Stream.concat(Stream.of(this),
                subProjects.stream().flatMap(Project::getAllProjectsStream));
    }

    Optional<Project> findProjectFromSrc(Path inputFile) {
        if (srcDirs.stream().anyMatch(dir -> inputFile.startsWith(dir))) {
            return Optional.of(this);

        } else {
            return subProjects.stream().map(subProject -> subProject.findProjectFromSrc(inputFile))
                    .filter(Optional::isPresent).map(Optional::get).findFirst();
        }
    }
}
