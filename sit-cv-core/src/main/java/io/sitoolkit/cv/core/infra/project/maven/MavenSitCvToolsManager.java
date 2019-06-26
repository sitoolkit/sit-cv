package io.sitoolkit.cv.core.infra.project.maven;

import java.nio.file.Path;

import io.sitoolkit.cv.core.infra.util.SitFileUtils;
import io.sitoolkit.cv.core.infra.util.SitResourceUtils;
import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;
import io.sitoolkit.util.buildtoolhelper.maven.MavenUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MavenSitCvToolsManager {

    @Getter
    private static MavenSitCvToolsManager instance;

    private static final String ARTIFACT_ID = "sit-cv-tools";

    @Getter
    private Path jarPath;

    public static void initialize(Path workDir) {
        if (instance != null) {
            return;
        }

        instance = new MavenSitCvToolsManager();
        instance.install(workDir);
    }

    private void install(Path workDir) {
        Path projectDir = workDir.resolve("sit-cv-tools-installer");

        createInstallerProject(projectDir);
        resolveJarPath(projectDir);
    }

    private void createInstallerProject(Path projectDir) {
        SitFileUtils.createDirectories(projectDir);
        SitResourceUtils.res2file(MavenSitCvToolsManager.class, "pom.xml",
                projectDir.resolve("pom.xml"));
        MavenUtils.findAndInstall(projectDir);
    }

    private void resolveJarPath(Path projectDir) {
        MavenProject project = MavenProject.load(projectDir);
        MavenSitCvToolsPathListener listener = new MavenSitCvToolsPathListener();

        log.info("Finding {}...", ARTIFACT_ID);
        project.mvnw("dependency:build-classpath", "-DincludeArtifactIds=" + ARTIFACT_ID)
                .stdout(listener).execute();

        jarPath = listener.getJarPath();

        if (jarPath == null) {
            throw new RuntimeException("Install failed");
        }

        log.info("sit-cv-tools found: {}", jarPath);
    }

}
