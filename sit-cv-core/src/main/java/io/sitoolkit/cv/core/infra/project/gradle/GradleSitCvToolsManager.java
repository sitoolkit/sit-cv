package io.sitoolkit.cv.core.infra.project.gradle;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import io.sitoolkit.cv.core.infra.project.maven.MavenSitCvToolsManager;
import io.sitoolkit.cv.core.infra.util.SitFileUtils;
import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;
import io.sitoolkit.util.buildtoolhelper.maven.MavenUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GradleSitCvToolsManager {

    @Getter
    private static GradleSitCvToolsManager instance;

    private GradleSitCvToolsManager() {
    }

    public static void initialize(Path workDir) {
        if (instance != null) {
            return;
        }

        instance = new GradleSitCvToolsManager();
        instance.install(workDir);
    }

    public Path getJarPath() {
        return MavenSitCvToolsManager.getInstance().getJarPath();
    }

    private void install(Path workDir) {
        Path projectDir = workDir.resolve("empty-project");

        createEmptyMavenProject(projectDir);

        MavenProject project = MavenProject.load(projectDir);
        MavenSitCvToolsManager.initialize(project);
    }

    private void createEmptyMavenProject(Path projectDir) {
        SitFileUtils.createDirectories(projectDir);

        try {
            FileUtils.touch(projectDir.resolve("pom.xml").toFile());
        } catch (IOException e) {
            log.warn(e.getMessage());
        }

        MavenUtils.findAndInstall(projectDir);
    }

}
