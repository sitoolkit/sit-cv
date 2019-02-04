package io.sitoolkit.cv.core.domain.project.gradle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.domain.project.ProjectReader;
import io.sitoolkit.cv.core.infra.util.SitResourceUtils;
import io.sitoolkit.util.buildtoolhelper.gradle.GradleProject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GradleProjectReader implements ProjectReader {

    private static final String PROJECT_INFO_SCRIPT_NAME = "project-info.gradle";

    @Override
    public Optional<Project> read(Path projectDir) {

        GradleProject gradleProject = GradleProject.load(projectDir);

        if (!gradleProject.available()) {
            return Optional.empty();
        }

        GradleProjectInfoListener listener = new GradleProjectInfoListener(projectDir);

        log.info("project: {} is a gradle project - finding depending jars... ", projectDir);

        Path initScript = projectDir.resolve(PROJECT_INFO_SCRIPT_NAME);
        SitResourceUtils.res2file(this, PROJECT_INFO_SCRIPT_NAME, initScript);

        try {

            gradleProject
                    .gradlew("--no-daemon", "--init-script", initScript.toString(), "projectInfo")
                    .stdout(listener).execute();

        } finally {
            try {
                Files.deleteIfExists(initScript);
            } catch (IOException e) {
                log.warn(e.getMessage());
            }
        }

        return Optional.of(listener.getProject());
    }

    @Override
    public boolean generateSqlLog(Project project) {
        // TODO Generate SQLlog with gradle
        return false;
    }
}
