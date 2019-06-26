package io.sitoolkit.cv.core.infra.project.maven;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MavenSitCvToolsPathListener implements StdoutListener {

    @Getter
    private Path jarPath;

    private static final String PREVIOUS_MESSAGE = "[INFO] Dependencies classpath:";

    private boolean messageDetected = false;

    @Override
    public void nextLine(String line) {
        log.info(line);

        if (PREVIOUS_MESSAGE.equals(line.trim())) {
            messageDetected = true;
            return;
        }

        if (messageDetected) {
            jarPath = Paths.get(line);
            messageDetected = false;
        }
    }

}
