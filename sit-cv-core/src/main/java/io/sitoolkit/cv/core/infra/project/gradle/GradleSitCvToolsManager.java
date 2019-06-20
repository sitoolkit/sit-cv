package io.sitoolkit.cv.core.infra.project.gradle;

import java.nio.file.Path;

import io.sitoolkit.cv.core.infra.project.maven.MavenSitCvToolsManager;
import io.sitoolkit.cv.core.infra.util.SitFileUtils;
import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;
import io.sitoolkit.util.buildtoolhelper.maven.MavenUtils;
import lombok.Getter;

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

        SitFileUtils.createDirectories(projectDir);
        MavenUtils.findAndInstall(projectDir);

        MavenProject project = MavenProject.load(projectDir);
        MavenSitCvToolsManager.initialize(project);
    }

}
