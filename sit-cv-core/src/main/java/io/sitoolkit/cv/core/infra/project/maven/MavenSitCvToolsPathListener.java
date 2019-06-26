package io.sitoolkit.cv.core.infra.project.maven;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MavenSitCvToolsPathListener implements StdoutListener {

    @Getter
    private Path jarPath;

    private static final Pattern PATH_PATTERN = Pattern.compile(".*sit-cv-tools.*\\.jar$");

    @Override
    public void nextLine(String line) {
        log.info(line);

        if (PATH_PATTERN.matcher(line).matches()) {
            jarPath = Paths.get(line);
        }
    }

}
