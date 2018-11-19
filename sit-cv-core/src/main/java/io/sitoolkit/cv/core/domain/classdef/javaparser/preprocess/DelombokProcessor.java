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
    final Path delombokTargetDir;

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
        this.delombokTargetDir = project.getBuildDir().resolve("generated-sources/sit-cv/delombok");
    }

    @Override
    public Path getTargetSrcPath(Path srcDir) {
        Optional<Path> sDir = project.getSrcDirs().stream().filter(dir -> srcDir.startsWith(srcDir)).findFirst();
        if (sDir.isPresent()) {
            Path relativized = sDir.get().relativize(srcDir);
            Path delomboked = delombokTargetDir.resolve(relativized.toString());
            return delomboked;
        } else {
            throw new IllegalArgumentException(srcDir.toAbsolutePath() + " is not_in source directory");
        }
    }

    @Override
    public void execute() {
        project.getSrcDirs().forEach(this::execute);
    }

    public void execute(Path srcDir) {
        DelombokParameter param = DelombokParameter.builder()
                .src(srcDir)
                .target(delombokTargetDir)
                .encoding("UTF-8")
                .classpath(project.getClasspaths())
                .sourcepath(project.getSrcDirs())
                .build();

        delomboker.execute(param);
    }
}
