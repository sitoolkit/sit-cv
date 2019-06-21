package io.sitoolkit.cv.core.infra.project.maven;

import java.nio.file.Path;

import io.sitoolkit.cv.core.infra.SitRepository;
import io.sitoolkit.cv.core.infra.util.PackageUtils;
import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MavenSitCvToolsManager {

    @Getter
    private static MavenSitCvToolsManager instance;

    private static final String ARTIFACT_ID = "sit-cv-tools";
    private static final String CLASSIFIER = "jar-with-dependencies";

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
        if (!instance.getJarPath().toFile().exists()) {
            log.info("{} jar not found in SitRepository", ARTIFACT_ID);

            instance.install(project);
        } else {
            log.info("{} jar found in SitRepository : {}", ARTIFACT_ID, instance.getJarPath());
        }
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
        project.mvnw("dependency:get", artifactArg).execute();

        log.info("Copying {}...", ARTIFACT_ID);
        project.mvnw("dependency:copy", artifactArg, "-DoutputDirectory=" + instance.getDirectory())
                .execute();

        if (!getJarPath().toFile().exists()) {
            throw new RuntimeException("Install failed");
        }

        log.info("Finished Installing {}", ARTIFACT_ID);
    }

}
