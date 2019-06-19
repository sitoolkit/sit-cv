package io.sitoolkit.cv.core.domain.project.gradle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import io.sitoolkit.cv.core.app.config.ServiceFactory;
import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import io.sitoolkit.cv.core.domain.project.ProjectReader;
import io.sitoolkit.cv.core.infra.config.SitCvConfig;
import io.sitoolkit.cv.core.infra.config.SitCvConfigReader;
import io.sitoolkit.cv.core.infra.util.SitResourceUtils;
import io.sitoolkit.util.buildtoolhelper.gradle.GradleProject;
import io.sitoolkit.util.buildtoolhelper.process.ProcessCommand;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GradleProjectReader implements ProjectReader {

    private static final String PROJECT_INFO_SCRIPT_NAME = "project-info.gradle";

    public static void main(String[] args) {
        Path projectDir = Paths.get("../sample");
        SitCvConfigReader configReader = new SitCvConfigReader();
        SitCvConfig config = configReader.read(projectDir);
        ServiceFactory factory = ServiceFactory.create(projectDir);
        new GradleProjectReader().generateSqlLog(factory.getProjectManager().getCurrentProject(), config);
    }
    
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
    public boolean generateSqlLog(Project project, SitCvConfig sitCvConfig) {
        GradleProject gradleProject = GradleProject.load(project.getDir());

        if (!gradleProject.available()) {
            return false;
        }
        
        GradleProjectInfoListener listener = new GradleProjectInfoListener(project.getDir());

        ProcessCommand command = gradleProject.gradlew("--no-daemon", "--rerun-tasks", "test");
        command.getEnv().put("JAVA_TOOL_OPTIONS", "-javaagent:B:/tools/git/home/java/sit-cv/sit-cv-tools/target/sit-cv-tools-1.0.0-beta.4-SNAPSHOT-jar-with-dependencies.jar=configUrl=file:///B:/tools/git/home/java/sit-cv/sample/sit-cv-config.json;repositoryMethodMarker=[RepositoryMethod]");
        command.stdout(listener).execute();

        return true;
    }
}
