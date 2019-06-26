package io.sitoolkit.cv.core.infra.project.maven;

import java.nio.file.Path;

import io.sitoolkit.cv.core.infra.SitRepository;
import io.sitoolkit.cv.core.infra.project.DefaultStdoutListener;
import io.sitoolkit.cv.core.infra.util.PackageUtils;
import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;
import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MavenSitCvToolsManager {

    @Getter
    private static MavenSitCvToolsManager instance;

    private static final String ARTIFACT_ID = "sit-cv-tools";
    private static final String CLASSIFIER = "jar-with-dependencies";

    private static final StdoutListener STDOUT_LISTENER = new DefaultStdoutListener();

    private String version;
    private String jarName;

    private MavenSitCvToolsManager() {
        version = PackageUtils.getVersion();
        jarName = String.format("%s-%s-%s.jar", ARTIFACT_ID, version, CLASSIFIER);
    }

    public static void initialize(MavenProject project) {
        if (instance != null) {
            return;
        }

        instance = new MavenSitCvToolsManager();
        instance.install(project);
    }

    public Path getJarPath() {
        return getDirectory().resolve(jarName);
    }

    private Path getDirectory() {
        return SitRepository.getRepositoryPath().resolve("sit-cv/" + ARTIFACT_ID);
    }

    private void install(MavenProject project) {
        String artifactArg = String.format("-Dartifact=io.sitoolkit.cv:%s:%s:jar:%s", ARTIFACT_ID,
                instance.version, CLASSIFIER);

        log.info("Installing {}...", ARTIFACT_ID);
        project.mvnw("dependency:get", artifactArg).stdout(STDOUT_LISTENER).execute();

        log.info("Copying {}...", ARTIFACT_ID);
        project.mvnw("dependency:copy", artifactArg, "-DoutputDirectory=" + instance.getDirectory(),
                "-Dmdep.overWriteReleases=true", "-Dmdep.overWriteSnapshots=true",
                "-DoutputAbsoluteArtifactFilename=true").stdout(STDOUT_LISTENER).execute();

        if (!getJarPath().toFile().exists()) {
            throw new RuntimeException("Install failed");
        }

        log.info("Finished Installing {}", ARTIFACT_ID);
    }

}
