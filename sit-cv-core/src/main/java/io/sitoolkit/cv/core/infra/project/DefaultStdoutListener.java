package io.sitoolkit.cv.core.infra.project;

import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultStdoutListener implements StdoutListener {

    @Override
    public void nextLine(String line) {
        log.info(line);
    }

}
