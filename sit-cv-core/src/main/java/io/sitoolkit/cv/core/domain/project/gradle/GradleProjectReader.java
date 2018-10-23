package io.sitoolkit.cv.core.domain.project.gradle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.domain.project.ProjectReader;
import io.sitoolkit.cv.core.infra.util.SitResourceUtils;
import io.sitoolkit.util.buidtoolhelper.gradle.GradleProject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GradleProjectReader implements ProjectReader {

    private static final String PROJECT_INFO_SCRIPT_NAME = "project-info.gradle";

    @Override
    public Optional<Project> read(Path projectDir) {

        GradleProject gradleProject = GradleProject.load(projectDir);

        if (gradleProject == null) {
            return Optional.empty();
        }

        Project project = new Project(projectDir);
        GradleProjectInfoListener listener = new GradleProjectInfoListener();

        log.info("project: {} is a gradle project - finding depending jars... ", projectDir);

        Path initScript = projectDir.resolve(PROJECT_INFO_SCRIPT_NAME);
        SitResourceUtils.res2file(this, PROJECT_INFO_SCRIPT_NAME, initScript);

        try {

            gradleProject.gradlew("--init-script", initScript.toString(), "projectInfo")
                    .stdout(listener).execute();

        } finally {
            try {
                Files.deleteIfExists(initScript);
            } catch (IOException e) {
                log.warn(e.getMessage());
            }
        }

        project.setSrcDirs(listener.getJavaSrcDirs());
        project.setClasspaths(listener.getClasspaths());
        // log.info("jarPaths got from gradle dependency - Paths: {}",
        // gotPaths);
        return Optional.of(project);
    }

}
