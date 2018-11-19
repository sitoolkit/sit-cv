package io.sitoolkit.cv.core.domain.classdef.javaparser.preprocess;

import java.nio.file.Path;
import java.util.Optional;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.infra.lombok.DelombokParameter;
import io.sitoolkit.cv.core.infra.lombok.Delomboker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DelombokProcessor implements PreProcessor {

    final Delomboker delomboker = new Delomboker();
    final Project project;

    public static Optional<PreProcessor> of(Project project) {

        if (isDelombokProject(project)) {
            return Optional.of(new DelombokProcessor(project));

        } else {
            return Optional.empty();
        }
    }

    static boolean isDelombokProject(Project project) {
        Optional<Path> delombokClasspath = project.getClasspaths().stream()
                .filter(classPath -> classPath.getFileName().toString().startsWith("lombok-"))
                .findFirst();

        if (project.getBuildDir() == null) {
            log.debug("build directory not found in {}", project.getDir());
            return false;
        }

        if (delombokClasspath.isPresent()) {
            log.debug("Lombok dependency found in {} : {}", project.getDir(), delombokClasspath.get());
        } else {
            log.debug("Lombok dependency not found in {}", project.getDir());
        }
        return delombokClasspath.isPresent();
    }

    private DelombokProcessor(Project project) {
        this.project = project;
    }

    @Override
    public Path getPreProcessedPath(Path original) {

        Optional<Path> enclosingSrcDir = project.getSrcDirs().stream()
                .filter(dir -> original.startsWith(original))
                .findFirst();

        if (enclosingSrcDir.isPresent()) {
            Path relativized = enclosingSrcDir.get().relativize(original);
            return getDelombokTargetDir().resolve(relativized.toString());

        } else {
            throw new IllegalArgumentException(original.toAbsolutePath() + " is not_in source directory");
        }
    }

    @Override
    public void execute() {
        project.getSrcDirs().forEach(this::executeDelombok);
    }

    void executeDelombok(Path srcDir) {
        DelombokParameter param = DelombokParameter.builder()
                .src(srcDir)
                .target(getDelombokTargetDir())
                .encoding("UTF-8")
                .classpath(project.getClasspaths())
                .sourcepath(project.getSrcDirs())
                .build();

        delomboker.execute(param);
    }

    Path getDelombokTargetDir() {
        return project.getBuildDir().resolve("generated-sources/sit-cv/delombok");
    }

}
