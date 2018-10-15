package io.sitoolkit.cv.core.infra.bt.gradle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;

import io.sitoolkit.cv.core.domain.classdef.javaparser.DependencyFinder;
import io.sitoolkit.util.buidtoolhelper.gradle.GradleProject;
import io.sitoolkit.util.buidtoolhelper.process.StdoutListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GradleDependencyFinder implements DependencyFinder {

    @Override
    public Collection<Path> findJarPaths(Path projectDir) {
        if (!isGradleProject(projectDir)) {
            return Collections.emptySet();
        }
        log.info("project: {} is a gradle project - finding depending jars... ", projectDir);

        GradleDependencyListener listener = new GradleDependencyListener();
        provideTemporaryFile(projectDir, tempScriptPath -> {
            writeListJarsScript(tempScriptPath);
            GradleProject.load(projectDir)
                    .gradlew("-b", tempScriptPath.toString(), "listJarPaths")
                    .stdout(listener)
                    .execute();
        });

        Collection<Path> gotPaths = listener.listenedPath;
        log.info("jarPaths got from gradle dependency - Paths: {}", gotPaths);
        return gotPaths;
    }

    void writeListJarsScript(Path creating) {
        try {
            Files.copy(getBuildScriptPath(creating.getParent()), creating, StandardCopyOption.REPLACE_EXISTING);
            Files.write(creating,
                    IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream("dependency-lister.gradle")),
                    StandardOpenOption.APPEND);
            log.info("list jars script written: {}", creating);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    void provideTemporaryFile(Path targetDir, Consumer<Path> tempFileConsumer) {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(targetDir, ".sit-cv-temp-", null);
            log.info("temporary file was created: {}", tempFile);
            tempFileConsumer.accept(tempFile);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            try {
                Files.deleteIfExists(tempFile);
                log.info("temporary file was deleted: {}", tempFile);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    Path getBuildScriptPath(Path projectDir) {
        return projectDir.resolve("build.gradle");
    }

    public boolean isGradleProject(Path projectDir) {
        return Files.exists(getBuildScriptPath(projectDir));
    }

    @Override
    public boolean canFindDependencies(Path projectDir) {
        return isGradleProject(projectDir);
    }
}

@Slf4j
class GradleDependencyListener implements StdoutListener {

    Collection<Path> listenedPath = new HashSet<>();
    boolean started = false;
    boolean ended = false;

    @Override
    public void nextLine(String line) {
        log.debug("listening to gradlew stdout: {}", line);

        checkEnd(line);
        if (isClassPathLine()) {
            listenedPath.add(Paths.get(line));
        }
        checkStart(line);
    }

    boolean isClassPathLine() {
        return started && !ended;
    }

    void checkEnd(String line) {
        if (!ended && line.contains("SIT-CV: Finished listing Jar paths")) {
            ended = true;
            log.debug("dependency listening end", line);
        }
    }

    void checkStart(String line) {
        if (!started && line.contains("SIT-CV: Started listing Jar paths")) {
            started = true;
            log.debug("dependency listening start", line);
        }
    }
}
