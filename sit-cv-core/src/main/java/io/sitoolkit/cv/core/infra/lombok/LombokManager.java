package io.sitoolkit.cv.core.infra.lombok;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;

import io.sitoolkit.cv.core.infra.SitRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LombokManager {
    private static final Path LOMBOK_REPOSITORY = SitRepository.getRepositoryPath().resolve("lombok");
    private static final LombokManager manager = new LombokManager();
    private String downloadUrl;
    private Path installFile;


    public static Path getLombokPath() {
        return manager.getJarPath();
    }

    private Path getJarPath() {
        return installFile;
    }

    private LombokManager() {
        init();
    }

    void init() {
        ResourceBundle rb = ResourceBundle.getBundle("lombok");
        downloadUrl = rb.getString("lombok.downloadUrl");
        installFile = LOMBOK_REPOSITORY.resolve(rb.getString("lombok.installFile"));
        checkBinary();
    }

    void checkBinary() {
        if (Files.exists(installFile)) {
            log.info("Executable Lombok found in SitRepository : {}", installFile);

        } else {
            log.info("Lombok not found in SitRepository");
            installLombok();
        }
    }

    void installLombok() {
        if (!Files.exists(LOMBOK_REPOSITORY)) {
            try {
                Files.createDirectories(LOMBOK_REPOSITORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (Files.exists(installFile)) {
            log.info("JarFile exists :{}", installFile.toString());
        } else {
            // TODO proxy対応
            try {
                URL url = new URL(downloadUrl);
                log.info("downloading jarFile from '{}' ... ", url);
                FileUtils.copyURLToFile(url, installFile.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
