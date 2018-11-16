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

    private static final Path delombokTargetDir = Paths.get(SystemUtils.JAVA_IO_TMPDIR, "sitoolkit/sit-cv/delomboked");
    private static DelombokProcessor current;

    final Project project;
    final Delomboker delomboker = new Delomboker();

    public static Optional<PreProcessor> getDelombokProcessor(Project project) {
        if (!isDelombokProject(project)) {
            return Optional.empty();
        }
        if (current == null) {
            init();
            current = new DelombokProcessor(project);
            return Optional.of(current);

        } else if (current.project == project) {
            return Optional.of(current);

        } else {
            throw new IllegalStateException("multiple projects is not supported");
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
        log.info("Delombok temp directory is : {}", delombokTargetDir);
        try {
            FileUtils.deleteDirectory(delombokTargetDir.toFile());
            log.info("Delombok temp directory cleaned.");
        } catch (IOException e) {
            log.info("Delombok temp directory cleaning failed.", e);
        }
    }

    private DelombokProcessor(Project project) {
        this.project = project;
    }

    @Override
    public Path getTargetSrcPath(Path srcDir) {
        Path relativized = project.getDir().relativize(srcDir);
        Path delomboked = delombokTargetDir.resolve(relativized.toString());
        return delomboked;
    }

    @Override
    public void execute(Path srcDir, Path targetDir) {
        DelombokParameter param = DelombokParameter.builder()
                .src(srcDir)
                .target(targetDir)
                .encoding("UTF-8")
                .classpath(project.getClasspaths())
                .sourcepath(project.getSrcDirs())
                .build();

        delomboker.execute(param);
    }
}
