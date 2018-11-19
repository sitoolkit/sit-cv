package io.sitoolkit.cv.core.domain.classdef.javaparser.preprocess;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.infra.lombok.DelombokParameter;
import io.sitoolkit.cv.core.infra.lombok.Delomboker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DelombokProcessor implements PreProcessor {

    private static final Path tempDelombokDir = Paths.get(SystemUtils.JAVA_IO_TMPDIR, "sitoolkit/sit-cv/delomboked");
    
    final Project project;
    final Delomboker delomboker = new Delomboker();
    final Path delombokTargetDir;
    static {
        init();
    }

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

        if (delombokClasspath.isPresent()) {
            log.debug("Lombok dependency found in {} : {}", project.getDir(), delombokClasspath.get());

        } else {
            log.debug("Lombok dependency not found in {}", project.getDir());
        }

        return delombokClasspath.isPresent();
    }

    public static void init() {
        log.info("Delombok temp directory is : {}", tempDelombokDir);
        try {
            FileUtils.deleteDirectory(tempDelombokDir.toFile());
            log.info("Delombok temp directory cleaned.");
        } catch (IOException e) {
            log.info("Delombok temp directory cleaning failed.", e);
        }
    }

    private DelombokProcessor(Project project) {
        this.project = project;
        if (project.getBuildDir() != null) {
            this.delombokTargetDir = project.getBuildDir().resolve("generated-sources/sit-cv/delombok");
        } else {
            this.delombokTargetDir = tempDelombokDir;
        }
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
