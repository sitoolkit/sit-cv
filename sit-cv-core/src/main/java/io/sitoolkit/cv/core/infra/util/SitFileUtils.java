package io.sitoolkit.cv.core.infra.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SitFileUtils {

    private SitFileUtils() {
    }

    public static void provideTemporaryFile(Path targetDir, Consumer<Path> tempFileConsumer) {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(targetDir, ".sit-cv-temp-", null);
            log.info("temporary file was created: {}", tempFile);
            tempFileConsumer.accept(tempFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            try {
                Files.deleteIfExists(tempFile);
                log.info("temporary file was deleted: {}", tempFile);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

}
