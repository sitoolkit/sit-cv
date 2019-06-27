package io.sitoolkit.cv.core.infra.project;

import java.nio.file.Path;

import io.sitoolkit.cv.core.infra.util.PackageUtils;
import io.sitoolkit.cv.core.infra.util.SitResourceUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SitCvToolsManager {

    @Getter
    private static SitCvToolsManager instance;

    private static final String ARTIFACT_ID = "sit-cv-tools";
    private static final String JAR_NAME = String.format("%s-%s-%s.jar", ARTIFACT_ID,
            PackageUtils.getVersion(), "jar-with-dependencies");

    @Getter
    private Path jarPath;

    public static void initialize(Path workDir) {
        if (instance != null) {
            return;
        }

        instance = new SitCvToolsManager();
        instance.install(workDir);
    }

    private void install(Path workDir) {
        jarPath = workDir.resolve(JAR_NAME);

        log.info("Installing {}", ARTIFACT_ID);
        SitResourceUtils.res2file(SitCvToolsManager.class, "/lib/" + JAR_NAME, jarPath);
    }

}
